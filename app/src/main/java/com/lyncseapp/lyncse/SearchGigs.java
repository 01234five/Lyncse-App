package com.lyncseapp.lyncse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lyncse.classes.ApiKey;


public class SearchGigs extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView recyclerViewSearch;
    private RecyclerAdapterSmallCard adapter;
    private RecyclerAdapterSearchGigsSearchResults adapterSearch;
    boolean loadMore;

    private List<String> listTitles;
    private List<String> listUri;
    private List<String> listPrices;
    private List<String> listIds;
    private List<String> listCreatorIds;
    private List<String> searchListIds;
    private List<String> searchListUri;
    private List<String> searchListTitles;
    private List<String> searchListPrices;
    private List<String> searchListIdsBuffer;
    private List<String> searchListCreatorIds;
    private List<String> listType;

    private String authUserId;


    private DatabaseReference ref;
    private DatabaseReference refPrice;
    private DatabaseReference refTitleUriSearch;
    private DatabaseReference refPriceSearch;
    private FirebaseDatabase database;

    private Integer x;
    private int xPrice;
    private int searchX;
    private int searchXPrice;

    private EditText editTextSearch;
    private String apikey;
    private boolean initialLoadSearch;
    private boolean initialLoad;

    private RelativeLayout relativeLayout;

    private String searchBarValue;
    private String searchScore;
    private String searchString;
    private boolean scrollMore;

    private int bottomNavViewHeight;
    private BottomNavigationView bottomNavigationView;
    private int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_gigs);

        database = FirebaseDatabase.getInstance();

        listTitles=new ArrayList<String>();
        listUri=new ArrayList<String>();
        listPrices=new ArrayList<String>();
        listIds=new ArrayList<String>();
        listCreatorIds= new ArrayList<String>();
        searchListTitles=new ArrayList<String>();
        searchListUri=new ArrayList<String>();
        searchListPrices=new ArrayList<String>();
        searchListIds=new ArrayList<String>();
        searchListIdsBuffer= new ArrayList<String>();
        searchListCreatorIds= new ArrayList<String>();
        listType= new ArrayList<String>();


        loadMore=false;
        initialLoadSearch=true;
        initialLoad=true;
        scrollMore=false;

        x=0;
        xPrice=0;
        searchX=0;
        searchXPrice=0;
        searchScore="";



        getViewElements();
        getHeightDp(this);
        getApiKey();
        bottomNav();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

    }


    @Override
    public void onRestart() {
        super.onRestart();
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here
        bottomNavigationView.setSelectedItemId(R.id.searchBottomBar);

    }
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    private void bottomNav(){

        bottomNavigationView.setSelectedItemId(R.id.searchBottomBar);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()){
                    case R.id.searchBottomBar:

                        return true;
                    case R.id.homeBottomBar:
                        startActivity(new Intent(SearchGigs.this,ProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.myGigsBottomBar:
                        startActivity(new Intent(SearchGigs.this,myGigs.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });


    }

    public float getHeightDp(Context context) {

        relativeLayout=(RelativeLayout) findViewById(R.id.relativeLayoutSearch);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        System.out.println(dpHeight);



        ViewTreeObserver viewTreeObserver = bottomNavigationView.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int viewHeight = bottomNavigationView.getHeight();
                    bottomNavViewHeight=viewHeight;
                    float desiredSize= dpHeight-48-12-12;
                    final float scale = getResources().getDisplayMetrics().density;
                    // Convert the dps to pixels, based on density scale
                    int mGestureThreshold = (int) ((int)desiredSize * scale + 0.5f)-bottomNavViewHeight;
                    relativeLayout.getLayoutParams().height = mGestureThreshold;
                    if (viewHeight != 0)
                        bottomNavigationView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    //viewWidth = mBottomNavigation.getWidth(); //to get view's width

                }
            });
        }




        return dpHeight;
    }
    private void getViewElements(){
        bottomNavigationView= findViewById(R.id.bottomNavigationViewSearchGigs);
        editTextSearch= (EditText) findViewById((R.id.editTextSearchActivitySearch));
        recyclerView = findViewById(R.id.recyclerRandomGigs);
        recyclerViewSearch = findViewById(R.id.recyclerSearchResults);

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
                    getSearchQuery();
                    handled = true;
                }
                return handled;
            }
        });
        //------------------------------------------

    }
    private void getSearchQuery(){
        searchListIdsBuffer.clear();
        searchListTitles.clear();
        searchListUri.clear();
        searchListPrices.clear();
        searchListIds.clear();

        searchScore="";
        searchString = searchBarValue;


            //Uses elasticsearch

        String url = "https://lyncse.es.us-central1.gcp.cloud.es.io:9243/virtualgigs/_search?filter_path=-_shards,-took,-hits.hits._index,-hits.total,-hits.max_score,-timed_out,-hits.hits._source,-hits.hits.sort&source_content_type=application/json&source={\n" +
                "  \"size\": 5,\n" +
                "    \"query\": {\n" +
                "        \"term\" : { \"title\" : \""+searchString+"\" }\n" +
                "    },\n" +
                "    \n" +
                " \"sort\": [\n" +
                "\t{\n" +
                "  \t\"_score\": \"desc\"\n" +
                "\t},\n" +
                "\t{\n" +
                "  \t\"gigId.keyword\": \"asc\"\n" +
                "\t}\n" +
                "  ]\n" +
                "}";

            // Request a string response from the provided URL.

            JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                int x = response.getJSONObject("hits").getJSONArray("hits").length();
                                for (int i = 0; i < x; i++) {
                                    searchListIdsBuffer.add(response.getJSONObject("hits").getJSONArray("hits").getJSONObject(i).getString("_id"));
                                    searchListIds.add(response.getJSONObject("hits").getJSONArray("hits").getJSONObject(i).getString("_id"));

                                }
                                searchScore = response.getJSONObject("hits").getJSONArray("hits").getJSONObject(x-1).getString("_score");
                                System.out.println("searchScore "+searchScore);
                                getSearchTitlesUri();
                                //getSearchPrices();
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
            RequestQueue queue = Volley.newRequestQueue(this);
// Add the request to the RequestQueue.
            queue.add(stringRequest);

    }
    private void getSearchQueryScroll(){
        searchListIdsBuffer.clear();



        //Uses elasticsearch
        String url = "https://lyncse.es.us-central1.gcp.cloud.es.io:9243/virtualgigs/_search?filter_path=-_shards,-took,-hits.hits._index,-hits.total,-hits.max_score,-timed_out,-hits.hits._source,-hits.hits.sort&source_content_type=application/json&source={\n" +
                "   \"size\": 5,\n" +
                "    \"query\": {\n" +
                "        \"term\" : { \"title\" : \""+searchString+"\" }\n" +
                "    },\n" +
                "    \n" +
                "   \"sort\": [\n" +
                "\t{\n" +
                "  \t\"_score\": \"desc\"\n" +
                "\t},\n" +
                "\t{\n" +
                "  \t\"gigId.keyword\": \"asc\"\n" +
                "\t}\n" +
                "  ],\n" +
                "     \"search_after\": \n" +
                "     ["+searchScore+",\"1\"]\n" +
                "}";


        // Request a string response from the provided URL.

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(response.length()==0){
                            scrollMore=false;
                            //System.out.println("no more gigs");
                        }
                        try {
                            int x = response.getJSONObject("hits").getJSONArray("hits").length();
                            System.out.println("response length "+x);
                            for (int i = 0; i < x; i++) {
                                searchListIdsBuffer.add(response.getJSONObject("hits").getJSONArray("hits").getJSONObject(i).getString("_id"));
                                searchListIds.add(response.getJSONObject("hits").getJSONArray("hits").getJSONObject(i).getString("_id"));

                                    System.out.println("i - x "+ i +" "+ x);

                                    if(i == (x-1)) {
                                        searchScore = response.getJSONObject("hits").getJSONArray("hits").getJSONObject(i).getString("_score");
                                        getSearchTitlesUri();
                                        //getSearchPrices();
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
        RequestQueue queue = Volley.newRequestQueue(this);
// Add the request to the RequestQueue.
        queue.add(stringRequest);

    }
    private void getApiKey(){
            ref = database.getReference();
            ref.child("ElasticSearch").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ApiKey elasticKey = snapshot.getValue(ApiKey.class);

                    if(elasticKey !=null){
                        apikey=elasticKey.apikey;
                        getRandomGigs(apikey);
                        addListeners();//Add listeners only after key is received
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });
        }
    private void getRandomGigs(String apikey){
    //Uses elasticsearch
        i=0;
        ref = database.getReference("Gigs/Virtual/");
        String url ="https://lyncse.es.us-central1.gcp.cloud.es.io:9243/virtualgigs/_search?filter_path=-_shards,-took,-hits.hits._index,-hits.hits._score,-hits.total,-hits.max_score,-timed_out,-hits.hits._source&source_content_type=application/json&source={\"size\": 10,\n" +
                "    \"query\": {\n" +
                "        \"function_score\": {\n" +
                "                \"query\" : { \"match_all\": {} },\n" +
                "               \"random_score\": {}\n" +
                "        }\n" +
                "    }}";

// Request a string response from the provided URL.

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    int x = response.getJSONObject("hits").getJSONArray("hits").length();
                                for (i = 0; i < x; i++) {
                                    listIds.add(response.getJSONObject("hits").getJSONArray("hits").getJSONObject(i).getString("_id"));


                                }
                                System.out.println("ids "+listIds);
                                System.out.println(response);
                                x=0;

                                    getTitlesUri();

                                //getPrices();
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
                params.put("Authorization", "ApiKey "+apikey);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(this);
// Add the request to the RequestQueue.
        queue.add(stringRequest);

    }


    private void getTitlesUri(){
        ref = database.getReference("Gigs/Virtual");
        //Get title of each gig on arrayList
        System.out.println("List id position 0 " +listIds.get(0));

        for (int i =0; i < listIds.size(); i++) {
            ref.child(listIds.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Gigs userGigs1 = snapshot.getValue(Gigs.class);

                    if (userGigs1 != null) {
                        System.out.println(userGigs1.title);
                        listCreatorIds.add(userGigs1.userID);
                        listTitles.add(userGigs1.title);
                        //-------
                        listUri.add(userGigs1.uriBanner);
                        x = x + 1;
                        if (x < listIds.size()) {
                            //getTitlesUri();
                        }
                    }

                    if (x == listIds.size()) {
                        System.out.println("Titles Spotlight " + listTitles);
                        System.out.println("Uri SpotLight " + listUri);
                        x = 0;
                        //Last value received
                        getPrices();

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });

            System.out.println("List id position " + x + " " + listIds.get(x));
        }
        }

    private void getPrices(){
        refPrice = database.getReference("Gigs/Virtual");
        //Get title of each gig on arrayList
        //System.out.println(stringGigIdsArray[i]);
        for (int i = 0; i < listIds.size(); i++) {
            refPrice.child(listIds.get(i)).child("option1").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    GigOptions optionsGigs = snapshot.getValue(GigOptions.class);

                    if (optionsGigs != null) {
                        String gigPrice = optionsGigs.price;
                        //Long created = userGigs.createdOn;
                        //System.out.println(gigTitle +" created on:" + created);
                        listPrices.add(gigPrice);
                        listType.add("Virtual");
                        xPrice = xPrice + 1;
                        if (xPrice < listIds.size()) {
                            //getTitles();
                        }
                    }

                    if (xPrice == listIds.size()) {
                        //System.out.println(listPrices);
                        xPrice = 0;
                        //Last value received
                        if(initialLoad==true) {
                            recyclerAdapterInvoke();

                        }else {
                            adapter.notifyDataSetChanged();
                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });
        }

    }
    private void getSearchTitlesUri(){
        refTitleUriSearch = database.getReference("Gigs/Virtual");
        //Get title of each gig on arrayList

        for (int i = 0; i < searchListIdsBuffer.size(); i++) {

            refTitleUriSearch.child(searchListIdsBuffer.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Gigs userGigs = snapshot.getValue(Gigs.class);

                    if (userGigs != null) {
                        searchListTitles.add(userGigs.title);
                        searchListCreatorIds.add(userGigs.userID);
                        //-------
                        searchListUri.add(userGigs.uriBanner);
                        searchX = searchX + 1;

                        if (searchX < searchListIdsBuffer.size()) {
                            //getTitles();
                        }
                    }

                    if (searchX == searchListIdsBuffer.size()) {
                        searchX = 0;
                        System.out.println("idsize+searchx completed"+searchListIdsBuffer.size()+" "+searchX);
                        //Last value received
                        System.out.println("list Titles: "+searchListTitles);
                        getSearchPrices();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });
        }
    }
    private void getSearchPrices(){
        refPriceSearch = database.getReference("Gigs/Virtual");
        //Get title of each gig on arrayList
        //System.out.println(stringGigIdsArray[i]);
        for (int i = 0; i < searchListIdsBuffer.size(); i++) {
            refPriceSearch.child(searchListIdsBuffer.get(i)).child("option1").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    GigOptions optionsGigs = snapshot.getValue(GigOptions.class);

                    if (optionsGigs != null) {
                        searchListPrices.add(optionsGigs.price);

                        searchXPrice = searchXPrice + 1;
                        if (searchXPrice < searchListIdsBuffer.size()) {
                            //getTitles();
                        }
                    }

                    if (searchXPrice == searchListIdsBuffer.size()) {
                        System.out.println(searchListPrices);
                        searchXPrice = 0;
                        //Last value received
                        System.out.println("list IDS:" + searchListIds);
                        if(initialLoadSearch==true) {
                            recyclerAdapterInvokeSearch();
                            scrollMore = true;
                        }else {
                            adapterSearch.notifyDataSetChanged();
                            scrollMore = true;
                        }

                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });
        }
    }
    private void recyclerAdapterInvokeSearch(){
        initialLoadSearch=false;

        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));


        adapterSearch = new RecyclerAdapterSearchGigsSearchResults(this, searchListTitles, searchListPrices, searchListUri,searchListIds,searchListCreatorIds,listType);
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
                    }

                }



            }
        });


        //Scroll listener for end of list

    }
    public void recyclerAdapterInvoke() {
        initialLoad=false;

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


        adapter = new RecyclerAdapterSmallCard(this, listTitles, listPrices, listUri,listIds,listCreatorIds,listType);
        recyclerView.setAdapter(adapter);


        //Scroll listener for end of list
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                //int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();

                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)) { //1 for down
                    if (loadMore == true) {
                        //chopList();
                    }
                }



            }
        });

    }
}