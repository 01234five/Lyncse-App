package com.lyncseapp.lyncse;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lyncseapp.lyncse.adapters.RecyclerAdapterMessenger;

import com.lyncseapp.lyncse.search.ActivitySearch;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Messenger extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView textViewDestroy;

    private BottomNavigationView bottomNavigationView;
    private FirebaseUser user;
    private String userId;
    private FirebaseDatabase database;
    private DatabaseReference refUsers;
    private DatabaseReference refUserChats;
    private DatabaseReference refChats;

    private List<String> chatIds;
    private List<String> receiverIds;
    private List<String> messages;
    private List<Long> sentTime;
    private List<String> name;
    private List<String> uri;

    private ChildEventListener mListener;
    private boolean initial;

    private int x;

    private String lastChild;

    RecyclerAdapterMessenger adapter;

    private boolean resumeHandles;
    ImageButton bb;
    private boolean allowBackPress=false;
    FrameLayout flProgressBar;
    private boolean noChats;

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if(allowBackPress==true) {
            if(noChats==false) {
                adapter.setDestroying();
            }
            this.finishAffinity();
            //adapter.pauseHandler();
            overridePendingTransition(0, 0);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if(resumeHandles==true) {
            if(noChats==false) {
                adapter.onResume();
            }
            resumeHandles=false;
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        resumeHandles=true;
        if(noChats==false) {
            adapter.onPause();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("destroy");
        textViewDestroy.setText("Destroy");
        if(mListener!=null) {
            database.getReference("Messages/Users/"+userId).removeEventListener(mListener);
        }
        if(noChats==false) {
        refUserChats.removeEventListener(mListener);

            adapter.onDestroy();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        getElements();
        enableBottomBar(false);
        noChats=true;
        flProgressBar.setVisibility(View.VISIBLE);
        allowBackPress=false;
        resumeHandles=false;
        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();
        database=FirebaseDatabase.getInstance();
        refChats=database.getReference("Messages/Chats/");
        refUsers=database.getReference("Users");
        refUserChats=database.getReference("Messages/Users/"+userId);


        chatIds=new ArrayList<String>();
        receiverIds=new ArrayList<String>();
        messages=new ArrayList<String>();
        sentTime=new ArrayList<Long>();
        uri=new ArrayList<String>();
        name=new ArrayList<String>();
        initial=true;


        bottomNav();



        //test();
        getLastChild();
        setListeners();
    }
    @Override
    public void onRestart() {
        super.onRestart();
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here
        bottomNavigationView.setSelectedItemId(R.id.chatBottomBar);

    }

    private void setListeners(){
        bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(allowBackPress==true) {
                    adapter.setDestroying();
                    finishAffinity();
                    overridePendingTransition(0, 0);
                }
            }
        });
    }
    private void getLastChild(){
        refUserChats.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    System.out.println("GETLASTCHILD SNAP"+ snapshot);
                    if(!snapshot.exists()){
                        flProgressBar.setVisibility(View.GONE);
                        enableBottomBar(true);
                        allowBackPress=true;
                        return;
                    }
                    if(snapshot.getValue()== null){
                        flProgressBar.setVisibility(View.GONE);
                        enableBottomBar(true);
                        allowBackPress=true;
                        return;
                    }
                    noChats=false;
                for (DataSnapshot lastChildKey: snapshot.getChildren()) {
                    System.out.println("FOR LOOP GETLASTCHILD" +lastChildKey);
                    getChatIds(lastChildKey.getKey());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getChatIds(String lastChild){
        mListener= new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                getNameUri(snapshot.getKey().toString(),snapshot.child("chatId").getValue().toString());
            }


            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        refUserChats.addChildEventListener(mListener);
    }

    private void getNameUri(String rId,String cId){
        refUsers.child(rId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    User userProfile = snapshot.getValue(User.class);
                    uri.add(userProfile.uriProfile);
                    name.add(userProfile.firstName);
                    x=x+1;
                    if(initial==true){
                        initial=false;
                        chatIds.add(cId);
                        receiverIds.add(rId);
                        recyclerAdapterInvokeMessenger();
                        System.out.println("Invoke");
                    }else{
                        chatIds.add(cId);
                        receiverIds.add(rId);
                        System.out.println("update");
                        adapter.notifyDataSetChanged();
                    }

                }else{
                    System.out.println("nothing");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Failed");
            }
        });
    }







private void enableBottomBar(boolean enable){
        for ( int i = 0; i < bottomNavigationView.getMenu().size(); i++){
            bottomNavigationView.getMenu().getItem(i).setEnabled(enable);
        }
}

    private void getElements(){
        textViewDestroy = findViewById(R.id.textViewDestroyMessenger);
        bottomNavigationView = findViewById(R.id.bottomNavigationViewMessenger);
        recyclerView = findViewById(R.id.recyclerViewMessenger);
        bb = findViewById(R.id.imageButtonMessenger);
        flProgressBar=findViewById(R.id.flMainProgressBar_Messenger);
    }
    private void bottomNav(){

        bottomNavigationView.setSelectedItemId(R.id.chatBottomBar);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()){
                    case R.id.searchBottomBar:
                        Intent intent = new Intent(Messenger.this, ActivitySearch.class);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.homeBottomBar:
                        startActivity(new Intent(Messenger.this,ProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.myGigsBottomBar:
                        startActivity(new Intent(Messenger.this,myGigs.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.requestsBottomBar:
                        startActivity(new Intent(Messenger.this,RequestsView.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.chatBottomBar:
                        return true;

                }


                return false;
            }
        });
    }



    public void recyclerAdapterInvokeMessenger() {



        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        System.out.println(chatIds + " " + receiverIds);

        adapter = new RecyclerAdapterMessenger(this, receiverIds,chatIds,name,uri,userId);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this){
            @Override
            public void onLayoutCompleted (RecyclerView.State state){
                super.onLayoutCompleted(state);
                // update UI for new layout
                flProgressBar.setVisibility(View.GONE);
                enableBottomBar(true);
                allowBackPress=true;
            }
        });

        //Scroll listener for end of list
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                //int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();

                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)) { //1 for down
                    //if (loadMore == true) {
                    //chopList();
                    //}
                }



            }
        });

    }

}

