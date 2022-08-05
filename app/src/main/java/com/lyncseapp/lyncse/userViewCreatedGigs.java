package com.lyncseapp.lyncse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class userViewCreatedGigs extends AppCompatActivity {

    private FirebaseUser user;
    private String userId;



    RecyclerView recyclerView;
    RecyclerAdapterUserViewCreatedGigs adapter;
    String stringGigIdsArray[]={};
    String arrayUri[]={};
    private List<String> userGigsIdList;
    private List<String> userGigsIdListOutside;
    private List<String> tempList;
    private List<String> listTitles;
    private List<String> listUri;
    private List<String> listPrices;
    private List<String> listIds;
    private List<String> choppedList;
    private List<String> listType;

    private Integer x;
    boolean loadMore;
    int sizePosition=0;
    boolean initialLoad;

    private int sizeResults;
    private int sizeResultsTotal;
    private boolean updateRecycler;

    FirebaseDatabase database;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_view_created_gigs);
        user = FirebaseAuth.getInstance().getCurrentUser();
        userId= user.getUid();
        database= FirebaseDatabase.getInstance();

        listTitles=new ArrayList<String>();
        listUri=new ArrayList<String>();
        listPrices=new ArrayList<String>();
        listIds=new ArrayList<String>();
        listType=new ArrayList<String>();
        userGigsIdList=new ArrayList();
        userGigsIdListOutside=new ArrayList();
        initialLoad=true;
        updateRecycler=false;
        sizeResults=0;
        sizeResultsTotal=0;
        x=0;


        eventListenerGigUID();//Get IDS of user gigs









    }



    @Override
    public void onRestart() {
        super.onRestart();
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here
        finish();
        startActivity(new Intent(userViewCreatedGigs.this,userViewCreatedGigs.class));
    }


    private void eventListenerGigUID2(){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Gigs/Outside");
        //GETUID OF GIG--------------------------------------------------------------------------------
        ref.orderByChild("userID").equalTo(userId).limitToFirst(6).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //userGigsIdListOutside=new ArrayList();// everytime a change happens the array is cleared.
                if(snapshot.getValue()==null)
                {
                    if(sizeResults==0) {
                        return;
                    }
                    else {
                        if (initialLoad == true) {
                            recyclerAdapterInvoke();
                            updateRecycler=false;
                            loadMore=true;
                            System.out.println("invoke");
                        } else {
                            adapter.notifyDataSetChanged();
                            updateRecycler=false;
                            loadMore=true;
                            System.out.println("update");

                        }
                    }

                }

                int x=sizeResults;
                sizeResults=sizeResults+(int)snapshot.getChildrenCount();
                System.out.println("f "+ sizeResults);

                for (DataSnapshot userGigsSnapshot: snapshot.getChildren()) {

                    x=x+1;
                    System.out.println(sizeResults+" yo " + x);
                    if(x==sizeResults){
                        updateRecycler=true;
                    }
                    userGigsIdListOutside.add(userGigsSnapshot.getKey());
                    getTitlesUriPrice(userGigsSnapshot.getKey().toString(),ref);
                    listIds.add(userGigsSnapshot.getKey());
                    listType.add("Outside");



                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void eventListenerGigUID(){
            updateRecycler=false;
            DatabaseReference ref = database.getReference("Gigs/Virtual");
            //GETUID OF GIG--------------------------------------------------------------------------------
            ref.orderByChild("userID").equalTo(userId).limitToFirst(6).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    //userGigsIdList=new ArrayList();// everytime a change happens the array is cleared.
                    if(snapshot.getValue()==null){
                        sizeResults=0;
                        eventListenerGigUID2();
                    }
                    else
                    {



                    sizeResults=(int)snapshot.getChildrenCount();
                    System.out.println(sizeResults);
                    for (DataSnapshot userGigsSnapshot: snapshot.getChildren()) {


                        //System.out.println(uGigID);
                        userGigsIdList.add(userGigsSnapshot.getKey());
                        getTitlesUriPrice(userGigsSnapshot.getKey().toString(),ref);
                        listIds.add(userGigsSnapshot.getKey());
                        listType.add("Virtual");


                    }
                    eventListenerGigUID2();




                }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });

            //-----------------------------------------------------------------------------------------------
        }

    private void searchGetKeysAfterSpecificKeyStartAfter(){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Gigs/Virtual");

        //System.out.println("in test function");

        ref.orderByKey().startAfter("-MvwC22A2U1XU4ycuP-S").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot==null)return;
                for (DataSnapshot userGigsSnapshot: snapshot.getChildren()) {
                    String keys = userGigsSnapshot.getKey();

                    //System.out.println("test: "+keys);



                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMoreResults(String key,DatabaseReference ref){



        System.out.println("in test function");

        ref.orderByChild("userID").startAfter(userId,key).limitToFirst(5).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println("TEST");
                if(snapshot==null){
                    userGigsIdList=new ArrayList();
                    return;}
                sizeResultsTotal=sizeResultsTotal+(int)snapshot.getChildrenCount();
                for (DataSnapshot userGigsSnapshot: snapshot.getChildren()) {
                    String keys = userGigsSnapshot.getKey();

                    if(keys.equals(userGigsIdList.get(userGigsIdList.size() - 1))){
                        return;
                    }

                    System.out.println("test: "+keys);
                    userGigsIdList.add(userGigsSnapshot.getKey());
                    listIds.add(userGigsSnapshot.getKey());
                    listType.add("Virtual");
                    getTitlesUriPrice(keys,database.getReference("Gigs/Virtual"));



                }
                if(userGigsIdListOutside.isEmpty()==false){
                    DatabaseReference ref = database.getReference("Gigs/Outside");
                    getMoreResults2(userGigsIdListOutside.get(userGigsIdListOutside.size() - 1),ref);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void getMoreResults2(String key,DatabaseReference ref){



        System.out.println("in test function");

        ref.orderByChild("userID").startAfter(userId,key).limitToFirst(5).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println("TEST");
                if(snapshot==null) {
                    userGigsIdListOutside = new ArrayList();
                    update();
                    return;
                }

                    sizeResultsTotal=sizeResultsTotal+(int)snapshot.getChildrenCount();
                    for (DataSnapshot userGigsSnapshot: snapshot.getChildren()) {
                        String keys = userGigsSnapshot.getKey();
                        if(keys.equals(userGigsIdListOutside.get(userGigsIdList.size() - 1))){
                            return;
                        }
                        userGigsIdListOutside.add(userGigsSnapshot.getKey());
                        listIds.add(userGigsSnapshot.getKey());
                        listType.add("Outside");
                        System.out.println("test: "+keys);
                        getTitlesUriPrice(keys,database.getReference("Gigs/Outside"));
                    }
                }




            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




    private void getTitlesUriPrice(String id,DatabaseReference r){

        r.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Gigs userGigs1 = snapshot.getValue(Gigs.class);

                if (userGigs1 != null) {


                    listTitles.add(userGigs1.title);
                    //-------
                    listUri.add(userGigs1.uriBanner);
                    listPrices.add(snapshot.child("option1").child("price").getValue().toString());
                    System.out.println(id + " " + userGigs1.title);
                    System.out.println(snapshot.child("option1").child("price").getValue());



                        //fl.setVisibility(View.VISIBLE);
                    }
                System.out.println(listPrices.size()+" "+sizeResultsTotal);
                if(listPrices.size()==sizeResultsTotal){
                    update();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }
private void update(){
    if(sizeResultsTotal!=0) {
        if ( initialLoad == true) {
            recyclerAdapterInvoke();
            loadMore = true;
            System.out.println("invoke");
        } else {
            adapter.notifyDataSetChanged();
            loadMore = true;
            System.out.println("update");
        }
    }
}


    public void recyclerAdapterInvoke() {

            initialLoad=false;

            recyclerView = findViewById(R.id.recyclerUserViewCreatedGigs);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));


            adapter = new RecyclerAdapterUserViewCreatedGigs(this, listTitles, listPrices, listUri,listIds,listType);
            recyclerView.setAdapter(adapter);


            //Scroll listener for end of list
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    if (loadMore == true) {
                        System.out.println("at end");
                        loadMore=false;




                        if(userGigsIdList.isEmpty()==true) {
                            if (userGigsIdListOutside.isEmpty() == false) {
                                sizeResults=0;
                                sizeResultsTotal=listPrices.size();
                                DatabaseReference ref = database.getReference("Gigs/Outside");
                                getMoreResults(userGigsIdListOutside.get(userGigsIdListOutside.size() - 1),ref);

                            }
                        }else if(userGigsIdList.isEmpty()==false){
                            sizeResults=0;
                            sizeResultsTotal=listPrices.size();
                            DatabaseReference ref = database.getReference("Gigs/Virtual");
                            getMoreResults(userGigsIdList.get(userGigsIdList.size() - 1),ref);

                        }
                    }

                }
            }
        });


    }



}

