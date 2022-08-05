package com.lyncseapp.lyncse.search;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.lyncseapp.lyncse.Gigs;
import com.lyncseapp.lyncse.R;
import com.lyncseapp.lyncse.RecyclerAdapterSearchGigsSearchResults;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lyncse.classes.ApiKey;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchTabFragment extends Fragment {

    private EditText editTextSearch;

    private String searchBarValue;
    private String searchScore;
    private String searchScore2;
    private String searchString;
    private boolean scrollMore;


    ImageView ivb;
    FrameLayout flLoad;
    FrameLayout llLoadBottom;
    private List<String> searchListIds;
    private List<String> searchListUri;
    private List<String> searchListTitles;
    private List<String> searchListPrices;

    private List<String> searchListCreatorIds;
    private List<String> searchListType;


    private String apikey;

    private FirebaseDatabase database;
    private DatabaseReference ref;
    private DatabaseReference refVirtual;
    private DatabaseReference refOutside;
    private DatabaseReference refFindRequests;

    private int virtualSearchSize;
    private int virtualSearchSizeTotal;
    private int countSearch;

    private RecyclerView recyclerViewSearch;
    private RecyclerAdapterSearchGigsSearchResults adapterSearch;

    private boolean initial;
    private boolean working;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchTabFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchTabFragment newInstance(String param1, String param2) {
        SearchTabFragment fragment = new SearchTabFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = FirebaseDatabase.getInstance();
        refVirtual = database.getReference("Gigs/Virtual");
        refOutside = database.getReference("Gigs/Outside");
        refFindRequests = database.getReference("Gigs/FindRequests");

        searchListTitles=new ArrayList<String>();
        searchListUri=new ArrayList<String>();
        searchListPrices=new ArrayList<String>();
        searchListIds=new ArrayList<String>();

        searchListCreatorIds= new ArrayList<String>();
        searchListType= new ArrayList<String>();

        initial=true;
        working=false;
        countSearch=0;

        getApiKey();
        getViewElements(view);

    }

    private void getViewElements(View v){

        editTextSearch= (EditText) v.findViewById((R.id.editTextSearch_SearchTab));
        recyclerViewSearch = v.findViewById(R.id.recyclerSearch_SearchTab);
        ivb = v.findViewById(R.id.imageView3);
        flLoad = v.findViewById(R.id.flLoad_SearchTab);
        llLoadBottom = v.findViewById(R.id.llLoadBottom_SearchTab);
    }

    private void addListeners(){

        //Removes icon when typing
        editTextSearch.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View view, boolean b) {
                // TODO Auto-generated method stub
                if(b)
                {
                    editTextSearch.setCompoundDrawables(null, null, null, null);

                }
                else if(!b)
                {
                    if(editTextSearch.getText().length()==0)
                        editTextSearch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_search_24, 0, 0, 0);
                }
            }
        });
        //----------------------------------------
        editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchBarValue=editTextSearch.getText().toString();
                    System.out.println(working+ " working");
                    if(working==false) {
                        flLoad.setVisibility(View.VISIBLE);
                        getSearchQuery();
                    }
                    handled = true;
                }
                return handled;
            }
        });
        //------------------------------------------

    }



    private void getSearchQuery(){
        working=true;
        countSearch=0;

        searchListTitles.clear();
        searchListUri.clear();
        searchListPrices.clear();
        searchListIds.clear();
        searchListType.clear();
        searchListCreatorIds.clear();
        virtualSearchSizeTotal=0;

        searchScore="";
        searchString = searchBarValue;


        //Uses elasticsearch
        String url = "https://lyncse.es.us-central1.gcp.cloud.es.io:9243/virtualgigs/_search?filter_path=-_shards,-took,-hits.hits._index,-hits.hits._score,-hits.total,-hits.max_score,-timed_out,-hits.hits._source.date,-hits.hits._source.gigId,-hits.hits._source.title&source_content_type=application/json&source={\"size\": 5,\n" +
                "  \"query\":{\n" +
                "    \"term\":{\"title\":\""+searchString+"\"}\n" +
                "  },\n" +
                "  \"sort\":[\n" +
                "    {\n" +
                "      \"date\": \"desc\"\n" +
                "    },{\n" +
                "      \"gigId.keyword\":\"asc\"\n" +
                "    }\n" +
                "    ]\n" +
                "}";

        // Request a string response from the provided URL.

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response);
                        if ( response.length() == 0 ) {
                            System.out.println("NO DATA");
                            recyclerViewSearch.setVisibility(View.GONE);
                            ivb.setVisibility(View.VISIBLE);
                            working=false;
                            hideLoadWidget();
                        }else{
                            System.out.println("DATA " +response);
                            ivb.setVisibility(View.GONE);
                            recyclerViewSearch.setVisibility(View.VISIBLE);
                        }
                        try {
                            virtualSearchSize =response.getJSONObject("hits").getJSONArray("hits").length();
                            virtualSearchSizeTotal=virtualSearchSizeTotal+virtualSearchSize;
                            JSONArray ja =response.getJSONObject("hits").getJSONArray("hits");
                            for (int i = 0; i < virtualSearchSize; i++) {
                                String id=ja.getJSONObject(i).getString("_id");
                                String s=ja.getJSONObject(i).getJSONObject("_source").getString("type");
                                searchListIds.add(id);
                                searchListType.add(s);
                                System.out.println(ja.getJSONObject(i).getJSONArray("sort").getString(0)+" "+ja.getJSONObject(i).getJSONArray("sort").getString(1));
                                if(i==virtualSearchSize-1) {
                                    if (searchListType.get(countSearch).equals("Virtual")) {
                                        getTitlesUriPriceVirtual(searchListIds.get(countSearch));
                                        searchScore = ja.getJSONObject(i).getJSONArray("sort").getString(0);
                                        searchScore2 =ja.getJSONObject(i).getJSONArray("sort").getString(1);
                                    } else if (searchListType.get(countSearch).equals("Outside")) {
                                        getTitlesUriPriceOutside(searchListIds.get(countSearch));
                                        searchScore = ja.getJSONObject(i).getJSONArray("sort").getString(0);
                                        searchScore2 =ja.getJSONObject(i).getJSONArray("sort").getString(1);
                                    }else {
                                        getTitlesUriPriceFind(searchListIds.get(countSearch));
                                        searchScore = ja.getJSONObject(i).getJSONArray("sort").getString(0);
                                        searchScore2 =ja.getJSONObject(i).getJSONArray("sort").getString(1);
                                    }
                                }
                            }
                            System.out.println("searchScore "+searchScore);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error volley");
            }
        }) {

            //This is for Headers If You Needed
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", "ApiKey " + apikey);
                return params;
            }
        };
        if(isAdded()) {
            RequestQueue queue = Volley.newRequestQueue(this.getContext());
// Add the request to the RequestQueue.
            queue.add(stringRequest);
        }

    }


    private void getSearchQueryScroll(){





        //Uses elasticsearch
        String url = "https://lyncse.es.us-central1.gcp.cloud.es.io:9243/virtualgigs/_search?filter_path=-_shards,-took,-hits.hits._index,-hits.hits._score,-hits.total,-hits.max_score,-timed_out,-hits.hits._source.date,-hits.hits._source.gigId,-hits.hits._source.title&source_content_type=application/json&source={\"size\": 5,\n" +
                "    \"query\":{\n" +
                "    \"term\":{\"title\":\""+searchString+"\"}\n" +
                "  },\n" +
                "  \"sort\":[\n" +
                "    {\n" +
                "      \"date\": \"desc\"\n" +
                "    },{\n" +
                "      \"gigId.keyword\":\"asc\"\n" +
                "    }\n" +
                "    ],\n" +
                "    \"search_after\": ["+searchScore+",\""+searchScore2+"\"]\n" +
                "}";


        // Request a string response from the provided URL.

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("response");
                        if(response.length()==0){
                            scrollMore=false;
                            System.out.println("no more gigs");
                            hideLoadWidget();
                        }

                        try {
                            virtualSearchSize =response.getJSONObject("hits").getJSONArray("hits").length();
                            virtualSearchSizeTotal=virtualSearchSizeTotal+virtualSearchSize;
                            JSONArray ja =response.getJSONObject("hits").getJSONArray("hits");
                            for (int i = 0; i < virtualSearchSize; i++) {
                                String id=ja.getJSONObject(i).getString("_id");
                                String s=ja.getJSONObject(i).getJSONObject("_source").getString("type");
                                System.out.println(searchListIds + " a");
                                searchListIds.add(id);
                                System.out.println(searchListIds + " b");
                                searchListType.add(s);
                                System.out.println(ja.getJSONObject(i).getJSONArray("sort").getString(0)+" "+ja.getJSONObject(i).getJSONArray("sort").getString(1));


                                if(i==virtualSearchSize-1) {
                                    if (searchListType.get(countSearch).equals("Virtual")) {
                                        getTitlesUriPriceVirtual(searchListIds.get(countSearch));
                                        searchScore = ja.getJSONObject(i).getJSONArray("sort").getString(0);
                                        searchScore2 =ja.getJSONObject(i).getJSONArray("sort").getString(1);
                                    } else if (searchListType.get(countSearch).equals("Outside")) {
                                        getTitlesUriPriceOutside(searchListIds.get(countSearch));
                                        searchScore = ja.getJSONObject(i).getJSONArray("sort").getString(0);
                                        searchScore2 =ja.getJSONObject(i).getJSONArray("sort").getString(1);
                                    } else {
                                        getTitlesUriPriceFind(searchListIds.get(countSearch));
                                        searchScore = ja.getJSONObject(i).getJSONArray("sort").getString(0);
                                        searchScore2 =ja.getJSONObject(i).getJSONArray("sort").getString(1);
                                    }
                                }
                            }

                        //llLoadBottom.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error volley");
            }
        }) {

            //This is for Headers If You Needed
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", "ApiKey " + apikey);
                return params;
            }
        };
        if(isAdded()) {
            RequestQueue queue = Volley.newRequestQueue(this.getContext());
// Add the request to the RequestQueue.
            queue.add(stringRequest);
        }

    }
    private void getApiKey(){
        ref = database.getReference();
        ref.child("ElasticSearch").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ApiKey elasticKey = snapshot.getValue(ApiKey.class);

                if(elasticKey !=null){
                    apikey=elasticKey.apikey;
                    addListeners();//Add listeners only after key is received
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }



    private void getTitlesUriPriceVirtual(String id){

//If(virtual filter selcted then ->))
        refVirtual.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Gigs userGigs1 = snapshot.getValue(Gigs.class);

                if (userGigs1 != null) {

                    searchListCreatorIds.add(userGigs1.userID);
                    searchListTitles.add(userGigs1.title);
                    //-------
                    searchListUri.add(userGigs1.uriBanner);
                    searchListPrices.add(snapshot.child("option1").child("price").getValue().toString());
                    System.out.println(id + " " + userGigs1.title);
                    //System.out.println(snapshot.child("option1").child("price").getValue());
                    System.out.println("lp size "+searchListPrices.size() +" vssT " +virtualSearchSizeTotal);

                    if(searchListPrices.size()==virtualSearchSizeTotal){
                        countSearch=countSearch+1;
                        try {
                        if(initial==true) {
                            hideLoadWidget();
                            System.out.println("invoke");
                            working=false;
                        }else{
                            hideLoadWidget();
                            System.out.println("notifYdATA CHANGED");
                            working=false;
                        }
                        scrollMore = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println(e);
                        }

                    }else{
                        countSearch=countSearch+1;
                        if (searchListType.get(countSearch).equals("Virtual")) {
                            getTitlesUriPriceVirtual(searchListIds.get(countSearch));
                        }if (searchListType.get(countSearch).equals("Find")) {
                            getTitlesUriPriceFind(searchListIds.get(countSearch));
                        } else {
                            getTitlesUriPriceOutside(searchListIds.get(countSearch));
                        }
                    }
                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }

    private boolean hideLoadWidget(){
        try {
            if (initial == true) {
                System.out.println("HIDE1");
                flLoad.setVisibility(View.GONE);
                recyclerAdapterInvokeSearch();
            } else {
                System.out.println("HIDE2");
                flLoad.setVisibility(View.GONE);
                llLoadBottom.setVisibility(View.GONE);
                adapterSearch.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
        return true;
    }
    private void getTitlesUriPriceOutside(String id){

//If(virtual filter selcted then ->))
        refOutside.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Gigs userGigs1 = snapshot.getValue(Gigs.class);

                if (userGigs1 != null) {

                    searchListCreatorIds.add(userGigs1.userID);
                    searchListTitles.add(userGigs1.title);
                    //-------
                    searchListUri.add(userGigs1.uriBanner);
                    searchListPrices.add(snapshot.child("option1").child("price").getValue().toString());
                    System.out.println(id + " " + userGigs1.title);
                    //System.out.println(snapshot.child("option1").child("price").getValue());
                    System.out.println("lp size "+searchListPrices.size() +" vssT " +virtualSearchSizeTotal);

                    if(searchListPrices.size()==virtualSearchSizeTotal){
                        countSearch=countSearch+1;
                        try {

                        if(initial==true) {
                            System.out.println("invoke");
                            working=false;
                            hideLoadWidget();
                        }else{
                            hideLoadWidget();
                            System.out.println("notifYdATA CHANGED");
                            working=false;
                        }
                        scrollMore = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println(e);
                        }

                    }else{
                        countSearch=countSearch+1;
                        if (searchListType.get(countSearch).equals("Virtual")) {
                            getTitlesUriPriceVirtual(searchListIds.get(countSearch));
                        }if (searchListType.get(countSearch).equals("Find")) {
                            getTitlesUriPriceFind(searchListIds.get(countSearch));
                        } else {
                            getTitlesUriPriceOutside(searchListIds.get(countSearch));
                        }
                    }
                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }


    private void getTitlesUriPriceFind(String id){

//If(virtual filter selcted then ->))
        refFindRequests.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Gigs userGigs1 = snapshot.getValue(Gigs.class);

                if (userGigs1 != null) {

                    searchListCreatorIds.add(userGigs1.userID);
                    searchListTitles.add(userGigs1.title);
                    //-------
                    searchListUri.add(userGigs1.uriBanner);
                    searchListPrices.add(snapshot.child("option1").child("price").getValue().toString());
                    System.out.println(id + " " + userGigs1.title);
                    //System.out.println(snapshot.child("option1").child("price").getValue());
                    System.out.println("lp size "+searchListPrices.size() +" vssT " +virtualSearchSizeTotal);

                    if(searchListPrices.size()==virtualSearchSizeTotal){
                        countSearch=countSearch+1;
                        try {

                            if(initial==true) {
                                System.out.println("invoke");
                                working=false;
                                hideLoadWidget();
                            }else{
                                hideLoadWidget();
                                System.out.println("notifYdATA CHANGED");
                                working=false;
                            }
                            scrollMore = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println(e);
                        }

                    }else{
                        countSearch=countSearch+1;
                        if (searchListType.get(countSearch).equals("Virtual")) {
                            getTitlesUriPriceVirtual(searchListIds.get(countSearch));
                        } if (searchListType.get(countSearch).equals("Find")) {
                            getTitlesUriPriceFind(searchListIds.get(countSearch));
                        }else {
                            getTitlesUriPriceOutside(searchListIds.get(countSearch));
                        }
                    }
                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }



    private void recyclerAdapterInvokeSearch(){

        System.out.println("CreatorIds  "+ searchListCreatorIds);


        initial=false;

        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));


        adapterSearch = new RecyclerAdapterSearchGigsSearchResults(this.getContext(), searchListTitles, searchListPrices, searchListUri,searchListIds,searchListCreatorIds,searchListType);
        recyclerViewSearch.setAdapter(adapterSearch);


        //Scroll listener for end of list
        recyclerViewSearch.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                //int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();

                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerViewSearch.canScrollVertically(1)) { //1 for down
                    if(scrollMore==true) {
                        System.out.println("scrolling");
                        getSearchQueryScroll();
                        llLoadBottom.setVisibility(View.VISIBLE);


                    }

                }



            }
        });


        //Scroll listener for end of list

    }

}