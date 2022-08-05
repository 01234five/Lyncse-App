package com.lyncseapp.lyncse.search;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.lyncseapp.lyncse.RecyclerAdapterSmallCard;
import com.lyncseapp.lyncse.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
 * Use the {@link ViewTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewTabFragment extends Fragment {

    private List<String> listTitles;
    private List<String> listUri;
    private List<String> listPrices;
    private List<String> listIds;
    private List<String> listCreatorIds;
    private List<String> listType;

    private List<String> oListTitles;
    private List<String> oListUri;
    private List<String> oListPrices;
    private List<String> oListIds;
    private List<String> oListCreatorIds;
    private List<String> oListType;

    private List<String> fListTitles;
    private List<String> fListUri;
    private List<String> fListPrices;
    private List<String> fListIds;
    private List<String> fListCreatorIds;
    private List<String> fListType;


    private List<String> listTitlesLastVirtual;
    private List<String> listUriLastVirtual;
    private List<String> listPricesLastVirtual;
    private List<String> listIdsLastVirtual;
    private List<String> listCreatorIdsLastVirtual;
    private List<String> listTypeLastVirtual;



    private DatabaseReference refVirtual;
    private DatabaseReference refOutside;
    private DatabaseReference refFindRequests;
    private DatabaseReference refApi;
    private FirebaseDatabase database;


    private int virtualRandomSize;
    private int outsideRandomSize;
    private int findRandomSize;
    private int lastCreatedSize;

    private List<String> listTitlesLastCreated;
    private List<String> listUriLastCreated;
    private List<String> listPricesLastCreated;
    private List<String> listIdsLastCreated;
    private List<String> listCreatorIdsLastCreated;
    private List<String> listTypeLastCreated;


    private TextView textViewRandomTitle;

    private RecyclerView virtualRecyclerViewRandom;
    private RecyclerView outsideRecyclerViewRandom;
    private RecyclerView findRecyclerViewRandom;
    private RecyclerView recyclerViewLastCreated;
    private RecyclerAdapterSmallCard virtualAdapter;
    private RecyclerAdapterSmallCard outsideAdapter;
    private RecyclerAdapterSmallCard findAdapter;
    private RecyclerAdapterSmallCard adapterLastCreated;

    private FrameLayout fl;

    private FirebaseUser user;
    private String userId;
    private String userName;
    private int countLastCreated;

    private FrameLayout flLoad;

    ImageView ivb1;
    ImageView ivb2;
    ImageView ivb3;
    ImageView ivb4;
    int xVirtual =0;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ViewTabFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewTabFragment newInstance(String param1, String param2) {
        ViewTabFragment fragment = new ViewTabFragment();
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
        return inflater.inflate(R.layout.fragment_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = FirebaseDatabase.getInstance();
        refVirtual = database.getReference("Gigs/Virtual");
        refOutside = database.getReference("Gigs/Outside");
        refFindRequests = database.getReference("Gigs/FindRequests");

        xVirtual =0;
        listTitlesLastVirtual=new ArrayList<String>();
        listUriLastVirtual=new ArrayList<String>();
        listPricesLastVirtual=new ArrayList<String>();
        listIdsLastVirtual=new ArrayList<String>();
        listCreatorIdsLastVirtual= new ArrayList<String>();
        listTypeLastVirtual= new ArrayList<String>();

        listTitlesLastCreated=new ArrayList<String>();
        listUriLastCreated=new ArrayList<String>();
        listPricesLastCreated=new ArrayList<String>();
        listIdsLastCreated=new ArrayList<String>();
        listCreatorIdsLastCreated= new ArrayList<String>();
        listTypeLastCreated= new ArrayList<String>();


        listTitles=new ArrayList<String>();
        listUri=new ArrayList<String>();
        listPrices=new ArrayList<String>();
        listIds=new ArrayList<String>();
        listCreatorIds= new ArrayList<String>();
        listType= new ArrayList<String>();

        oListTitles=new ArrayList<String>();
        oListUri=new ArrayList<String>();
        oListPrices=new ArrayList<String>();
        oListIds=new ArrayList<String>();
        oListCreatorIds= new ArrayList<String>();
        oListType= new ArrayList<String>();

        fListTitles=new ArrayList<String>();
        fListUri=new ArrayList<String>();
        fListPrices=new ArrayList<String>();
        fListIds=new ArrayList<String>();
        fListCreatorIds= new ArrayList<String>();
        fListType= new ArrayList<String>();

        user = FirebaseAuth.getInstance().getCurrentUser();
        userId= user.getUid();
        countLastCreated=0;

        getViewElements(view);
        //fl.setVisibility(View.VISIBLE);
        getApiKey();



    }



    private void getUserProfile() {
        FirebaseDatabase.getInstance().getReference("Users/" + userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if (userProfile != null) {
                    userName = userProfile.firstName;
                    editViewElements();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void getViewElements(View v){
        virtualRecyclerViewRandom = v.findViewById(R.id.recyclerViewTabRandomGigs);
        outsideRecyclerViewRandom=v.findViewById(R.id.recyclerViewTabRandomGigsOutside);
        findRecyclerViewRandom=v.findViewById(R.id.recyclerViewTabRandomGigsFind);

        recyclerViewLastCreated=v.findViewById(R.id.recyclerViewTabLastCreated);
        textViewRandomTitle=v.findViewById(R.id.textViewViewTabRandomTitle);
        fl = v.findViewById(R.id.frameLayoutSearchViewTab);
        ivb1=v.findViewById(R.id.imageView6);
        ivb2=v.findViewById(R.id.imageView7);
        ivb3=v.findViewById(R.id.imageView8);
        ivb4=v.findViewById(R.id.imageView8Find);
        flLoad= v.findViewById(R.id.flLoad_ViewTab);
        getUserProfile();
    }

    private void editViewElements(){
        textViewRandomTitle.setText("Check these out, "+userName);
    }

    private void getApiKey(){
        refApi = database.getReference();
        refApi.child("ElasticSearch").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ApiKey elasticKey = snapshot.getValue(ApiKey.class);

                if(elasticKey !=null){
                    String apikey=elasticKey.apikey;

                    getRandomGigsVirtual(apikey);
                    getRandomGigsOutside(apikey);
                    getLastCreatedPosts(apikey);
                    getRandomGigsFind(apikey);
                    //addListeners();//Add listeners only after key is received
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

    private void getLastCreatedPosts(String apikey){
        //Uses elasticsearch


        String url ="https://lyncse.es.us-central1.gcp.cloud.es.io:9243/virtualgigs/_search?filter_path=-_shards,-took,-hits.hits._index,-hits.hits._score,-hits.total,-hits.max_score,-timed_out,-hits.hits._source.date,-hits.hits._source.gigId,-hits.hits._source.title,-hits.hits.sort&source_content_type=application/json&source={\"size\": 10,\n" +
                " \"query\": { \n" +
                "  \"match_all\": {} },\n" +
                "  \"sort\": [ { \"date\": { \"order\": \"desc\" } } ] }";

// Request a string response from the provided URL.

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //System.out.println("R "+response);
                        if ( response.length() == 0 ) {
                            System.out.println("NO DATA");
                            recyclerViewLastCreated.setVisibility(View.GONE);
                            ivb1.setVisibility(View.VISIBLE);
                        }
                        try {


                            lastCreatedSize = response.getJSONObject("hits").getJSONArray("hits").length();
                            JSONArray ja =response.getJSONObject("hits").getJSONArray("hits");
                            for (int i = 0; i < lastCreatedSize; i++) {

                                System.out.println(response.getJSONObject("hits").getJSONArray("hits").getJSONObject(i).getString("_id")+" "+ja.getJSONObject(i).getJSONObject("_source").getString("type"));
                                String s=ja.getJSONObject(i).getJSONObject("_source").getString("type");
                                String id=response.getJSONObject("hits").getJSONArray("hits").getJSONObject(i).getString("_id");
                                listIdsLastCreated.add(id);
                                listTypeLastCreated.add(s);

                                if(i==lastCreatedSize-1) {
                                    if (listTypeLastCreated.get(0).equals("Virtual")) {
                                        System.out.println("Virtual");
                                        getVirtualGetTitlesUriPriceLastCreated(listIdsLastCreated.get(countLastCreated));

                                    } else if (listTypeLastCreated.get(0).equals("Outside")){
                                        System.out.println("Outside");
                                        getOutsideGetTitlesUriPriceLastCreated(listIdsLastCreated.get(countLastCreated));
                                    }else {
                                        System.out.println("Find");
                                        getFindRequestGetTitlesUriPriceLastCreated(listIdsLastCreated.get(countLastCreated));
                                    }
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error volley");
                fl.setVisibility(View.VISIBLE);
                flLoad.setVisibility(View.GONE);
            }
        }) {

            //This is for Headers If You Needed
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", "ApiKey "+apikey);
                return params;
            }
        };
        if(isAdded()) {
            RequestQueue queue = Volley.newRequestQueue(this.getContext());
            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        }
    }
    private void getRandomGigsVirtual(String apikey){
        //Uses elasticsearch

        System.out.println("EK "+apikey);
        String url ="https://lyncse.es.us-central1.gcp.cloud.es.io:9243/virtualgigs/_search?filter_path=-_shards,-took,-hits.hits._index,-hits.hits._score,-hits.total,-hits.max_score,-timed_out,-hits.hits._source&source_content_type=application/json&source={\"size\": 10,\n" +
                " \"query\": {\n" +
                "     \n" +
                "    \"function_score\": {\n" +
                "      \"query\": { \"term\" : { \"type\": \"virtual\"}},\n" +
                "      \"random_score\": {}\n" +
                "    }\n" +
                "  }\n" +
                "}";

// Request a string response from the provided URL.

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //System.out.println("R "+response);
                        if ( response.length() == 0 ) {
                            System.out.println("NO DATA");
                            virtualRecyclerViewRandom.setVisibility(View.GONE);
                            ivb2.setVisibility(View.VISIBLE);
                        }
                        try {

                            virtualRandomSize = response.getJSONObject("hits").getJSONArray("hits").length();
                            for (int i = 0; i < virtualRandomSize; i++) {
                                String id =response.getJSONObject("hits").getJSONArray("hits").getJSONObject(i).getString("_id");
                                listIds.add(id);

                            }
                            xVirtual =0;
                            virtualGetTitlesUriPrice();
                            System.out.println(response);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error volley");
                fl.setVisibility(View.VISIBLE);
                flLoad.setVisibility(View.GONE);
            }
        }) {

            //This is for Headers If You Needed
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", "ApiKey "+apikey);
                return params;
            }
        };
        if(isAdded()) {
            RequestQueue queue = Volley.newRequestQueue(this.getContext());
            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        }


    }
    private void getRandomGigsOutside(String apikey){

        String url ="https://lyncse.es.us-central1.gcp.cloud.es.io:9243/virtualgigs/_search?filter_path=-_shards,-took,-hits.hits._index,-hits.hits._score,-hits.total,-hits.max_score,-timed_out,-hits.hits._source&source_content_type=application/json&source={\"size\": 10,\n" +
                " \"query\": {\n" +
                "     \n" +
                "    \"function_score\": {\n" +
                "      \"query\": { \"term\" : { \"type\": \"outside\"}},\n" +
                "      \"random_score\": {}\n" +
                "    }\n" +
                "  }\n" +
                "}";

// Request a string response from the provided URL.

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //System.out.println("R "+response);
                        if ( response.length() == 0 ) {
                            System.out.println("NO DATA");
                            outsideRecyclerViewRandom.setVisibility(View.GONE);
                            ivb3.setVisibility(View.VISIBLE);
                        }
                        try {

                            outsideRandomSize = response.getJSONObject("hits").getJSONArray("hits").length();
                            for (int i = 0; i < outsideRandomSize; i++) {
                                String id =response.getJSONObject("hits").getJSONArray("hits").getJSONObject(i).getString("_id");
                                oListIds.add(id);
                                if(i==outsideRandomSize-1){
                                    outsideGetTitlesUriPrice(oListIds.get(0));
                                }
                            }

                            System.out.println("outside "+response);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error volley");
                fl.setVisibility(View.VISIBLE);
                flLoad.setVisibility(View.GONE);
            }
        }) {

            //This is for Headers If You Needed
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", "ApiKey "+apikey);
                return params;
            }
        };
        if(isAdded()) {
            RequestQueue queue = Volley.newRequestQueue(this.getContext());
            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        }


    }

    private void getRandomGigsFind(String apikey){

        String url ="https://lyncse.es.us-central1.gcp.cloud.es.io:9243/virtualgigs/_search?filter_path=-_shards,-took,-hits.hits._index,-hits.hits._score,-hits.total,-hits.max_score,-timed_out,-hits.hits._source&source_content_type=application/json&source={\"size\": 10,\n" +
                " \"query\": {\n" +
                "     \n" +
                "    \"function_score\": {\n" +
                "      \"query\": { \"term\" : { \"type\": \"find\"}},\n" +
                "      \"random_score\": {}\n" +
                "    }\n" +
                "  }\n" +
                "}";

// Request a string response from the provided URL.

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //System.out.println("R "+response);
                        if ( response.length() == 0 ) {
                            System.out.println("NO DATA");
                            findRecyclerViewRandom.setVisibility(View.GONE);
                            ivb4.setVisibility(View.VISIBLE);
                        }
                        try {

                            findRandomSize = response.getJSONObject("hits").getJSONArray("hits").length();
                            for (int i = 0; i < findRandomSize; i++) {
                                String id =response.getJSONObject("hits").getJSONArray("hits").getJSONObject(i).getString("_id");
                                fListIds.add(id);
                                if(i==findRandomSize-1){
                                    findGetTitlesUriPrice(fListIds.get(0));
                                }
                            }

                            System.out.println("outside "+response);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error volley");
                fl.setVisibility(View.VISIBLE);
                flLoad.setVisibility(View.GONE);
            }
        }) {

            //This is for Headers If You Needed
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", "ApiKey "+apikey);
                return params;
            }
        };
        if(isAdded()) {
            RequestQueue queue = Volley.newRequestQueue(this.getContext());
            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        }


    }
    private void virtualGetTitlesUriPrice(){

            refVirtual.child(listIds.get(xVirtual)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Gigs userGigs1 = snapshot.getValue(Gigs.class);

                    if (userGigs1 != null) {

                        listCreatorIds.add(userGigs1.userID);
                        listTitles.add(userGigs1.title);
                        //-------
                        listUri.add(userGigs1.uriBanner);
                        listPrices.add(snapshot.child("option1").child("price").getValue().toString());
                        listType.add("Virtual");
                        if(listPrices.size()==virtualRandomSize){
                            System.out.println("At end");
                            recyclerAdapterInvoke(virtualRecyclerViewRandom,virtualAdapter,listTitles, listPrices, listUri,listIds,listCreatorIds,listType);
                            virtualRecyclerViewRandom.setVisibility(View.VISIBLE);
                            ivb2.setVisibility(View.GONE);
                        }else{
                            xVirtual=xVirtual+1;
                            virtualGetTitlesUriPrice();
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });
        }
    private void outsideGetTitlesUriPrice(String id){

        refOutside.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Gigs userGigs1 = snapshot.getValue(Gigs.class);

                if (userGigs1 != null) {

                    oListCreatorIds.add(userGigs1.userID);
                    oListTitles.add(userGigs1.title);
                    //-------
                    oListUri.add(userGigs1.uriBanner);
                    oListPrices.add(snapshot.child("option1").child("price").getValue().toString());
                    oListType.add("Outside");
                    if(oListPrices.size()==outsideRandomSize){
                        recyclerAdapterInvoke(outsideRecyclerViewRandom,outsideAdapter,oListTitles, oListPrices, oListUri,oListIds,oListCreatorIds,oListType);
                        outsideRecyclerViewRandom.setVisibility(View.VISIBLE);
                        ivb3.setVisibility(View.GONE);
                    }else{
                        outsideGetTitlesUriPrice(oListIds.get(oListPrices.size()));
                    }
                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }


    private void findGetTitlesUriPrice(String id){

        refFindRequests.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Gigs userGigs1 = snapshot.getValue(Gigs.class);

                if (userGigs1 != null) {

                    fListCreatorIds.add(userGigs1.userID);
                    fListTitles.add(userGigs1.title);
                    //-------
                    fListUri.add(userGigs1.uriBanner);
                    fListPrices.add(snapshot.child("option1").child("price").getValue().toString());
                    fListType.add("Find");
                    if(fListPrices.size()==findRandomSize){
                        recyclerAdapterInvoke(findRecyclerViewRandom,findAdapter,fListTitles, fListPrices, fListUri,fListIds,fListCreatorIds,fListType);
                        findRecyclerViewRandom.setVisibility(View.VISIBLE);
                        ivb4.setVisibility(View.GONE);
                    }else{
                        findGetTitlesUriPrice(fListIds.get(fListPrices.size()));
                    }
                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }


    private void getVirtualGetTitlesUriPriceLastCreated(String id){

        refVirtual.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Gigs userGigs1 = snapshot.getValue(Gigs.class);

                if (userGigs1 != null) {

                    listCreatorIdsLastCreated.add(userGigs1.userID);
                    listTitlesLastCreated.add(userGigs1.title);
                    //-------
                    listUriLastCreated.add(userGigs1.uriBanner);
                    listPricesLastCreated.add(snapshot.child("option1").child("price").getValue().toString());
                    listTypeLastCreated.add("Virtual");
                    System.out.println(id + " " + userGigs1.title);
                    System.out.println(snapshot.child("option1").child("price").getValue());
                    System.out.println(listPrices.size() +"lp "+ virtualRandomSize +" <-vrs");
                    if(listPricesLastCreated.size()==lastCreatedSize){
                        recyclerAdapterInvoke(recyclerViewLastCreated,adapterLastCreated,listTitlesLastCreated, listPricesLastCreated, listUriLastCreated,listIdsLastCreated,listCreatorIdsLastCreated,listTypeLastCreated);

                        ivb1.setVisibility(View.GONE);
                        recyclerViewLastCreated.setVisibility(View.VISIBLE);
                        fl.setVisibility(View.VISIBLE);
                        flLoad.setVisibility(View.GONE);
                    }else{
                        countLastCreated=countLastCreated+1;
                        if (listTypeLastCreated.get(countLastCreated).equals("Virtual")) {
                            getVirtualGetTitlesUriPriceLastCreated(listIdsLastCreated.get(countLastCreated));
                        }if (listTypeLastCreated.get(countLastCreated).equals("Find")) {
                            getFindRequestGetTitlesUriPriceLastCreated(listIdsLastCreated.get(countLastCreated));
                        }
                        else {
                            getOutsideGetTitlesUriPriceLastCreated(listIdsLastCreated.get(countLastCreated));
                        }
                    }
                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }
    private void getOutsideGetTitlesUriPriceLastCreated(String id){

        refOutside.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Gigs userGigs1 = snapshot.getValue(Gigs.class);

                if (userGigs1 != null) {

                    listCreatorIdsLastCreated.add(userGigs1.userID);
                    listTitlesLastCreated.add(userGigs1.title);
                    //-------
                    listUriLastCreated.add(userGigs1.uriBanner);
                    listPricesLastCreated.add(snapshot.child("option1").child("price").getValue().toString());
                    listTypeLastCreated.add("Outside");
                    if(listPricesLastCreated.size()==lastCreatedSize){
                        recyclerAdapterInvoke(recyclerViewLastCreated,adapterLastCreated,listTitlesLastCreated, listPricesLastCreated, listUriLastCreated,listIdsLastCreated,listCreatorIdsLastCreated,listTypeLastCreated);

                        ivb1.setVisibility(View.GONE);
                        recyclerViewLastCreated.setVisibility(View.VISIBLE);
                        fl.setVisibility(View.VISIBLE);
                        flLoad.setVisibility(View.GONE);
                    }else{
                        countLastCreated=countLastCreated+1;
                        if (listTypeLastCreated.get(countLastCreated).equals("Virtual")) {
                            getVirtualGetTitlesUriPriceLastCreated(listIdsLastCreated.get(countLastCreated));
                        }if (listTypeLastCreated.get(countLastCreated).equals("Find")) {
                            getFindRequestGetTitlesUriPriceLastCreated(listIdsLastCreated.get(countLastCreated));
                        } else {
                            getOutsideGetTitlesUriPriceLastCreated(listIdsLastCreated.get(countLastCreated));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }


    private void getFindRequestGetTitlesUriPriceLastCreated(String id){

        refFindRequests.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Gigs userGigs1 = snapshot.getValue(Gigs.class);

                if (userGigs1 != null) {

                    listCreatorIdsLastCreated.add(userGigs1.userID);
                    listTitlesLastCreated.add(userGigs1.title);
                    //-------
                    listUriLastCreated.add(userGigs1.uriBanner);
                    listPricesLastCreated.add(snapshot.child("option1").child("price").getValue().toString());
                    listTypeLastCreated.add("Find");
                    if(listPricesLastCreated.size()==lastCreatedSize){
                        recyclerAdapterInvoke(recyclerViewLastCreated,adapterLastCreated,listTitlesLastCreated, listPricesLastCreated, listUriLastCreated,listIdsLastCreated,listCreatorIdsLastCreated,listTypeLastCreated);

                        ivb1.setVisibility(View.GONE);
                        recyclerViewLastCreated.setVisibility(View.VISIBLE);
                        fl.setVisibility(View.VISIBLE);
                        flLoad.setVisibility(View.GONE);
                    }else{
                        countLastCreated=countLastCreated+1;
                        if (listTypeLastCreated.get(countLastCreated).equals("Virtual")) {
                            getVirtualGetTitlesUriPriceLastCreated(listIdsLastCreated.get(countLastCreated));
                        }if (listTypeLastCreated.get(countLastCreated).equals("Find")) {
                            getFindRequestGetTitlesUriPriceLastCreated(listIdsLastCreated.get(countLastCreated));
                        } else {
                            getOutsideGetTitlesUriPriceLastCreated(listIdsLastCreated.get(countLastCreated));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }

private void getVirtualLastGigs(){
    refVirtual.limitToLast(10).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {

            for (DataSnapshot lastChildKey: snapshot.getChildren()) {
                Gigs userGigs1 = lastChildKey.getValue(Gigs.class);
                if (userGigs1 != null) {
                    listIdsLastVirtual.add(lastChildKey.getKey());
                    listCreatorIdsLastVirtual.add(userGigs1.userID);
                    listTitlesLastVirtual.add(userGigs1.title);
                    //-------
                    listUriLastVirtual.add(userGigs1.uriBanner);
                    listPricesLastVirtual.add(lastChildKey.child("option1").child("price").getValue().toString());
                    listTypeLastVirtual.add("Virtual");


                }
            }
            //recyclerAdapterInvoke(virtualRecyclerViewLast,virtualAdapterLastCreated,listTitlesLastVirtual, listPricesLastVirtual, listUriLastVirtual,listIdsLastVirtual,listCreatorIdsLastVirtual,listTypeLastVirtual);
            System.out.println("AT END virtual Last");
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });
}
public void recyclerAdapterInvoke(RecyclerView recyclerView,RecyclerAdapterSmallCard adapter,List<String> listTitles,
                                      List<String> listPrices,List<String> listUri,List<String> listIds,List<String> listCreatorIds,List<String> listType ){


        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));


        adapter = new RecyclerAdapterSmallCard(this.getContext(), listTitles, listPrices, listUri,listIds,listCreatorIds,listType);
        recyclerView.setAdapter(adapter);

    }

}

