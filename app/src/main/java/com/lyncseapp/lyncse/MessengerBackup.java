/*




package com.example.lyncse;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lyncse.adapters.RecyclerAdapterMessenger;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);

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


        getElements();
        bottomNav();

        getLastChild();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("destroy");
        textViewDestroy.setText("Destroy");
        if(mListener!=null) {
            database.getReference("Messages/Users/"+userId).removeEventListener(mListener);
        }

        adapter.onDestroy();
        refUserChats.removeEventListener(mListener);

    }




    private void getLastChild(){
        refUserChats.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                for (DataSnapshot lastChildKey: snapshot.getChildren()) {
                    test(lastChildKey.getKey().toString());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getChatIds(){
        mListener= new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                System.out.println("childevent "+ snapshot);
                chatIds.add(snapshot.child("chatId").getValue().toString());
                //getReceiverNameUri(snapshot.child("chatId").getValue().toString(),snapshot.getKey().toString());
                receiverIds.add(snapshot.getKey().toString());



                if (initial == true) {
                    System.out.println("Invoke");
                    recyclerAdapterInvokeMessenger();
                    initial = false;
                } else {
                    System.out.println("update");
                    adapter.notifyDataSetChanged();
                }
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

    private void test(String lastChild){

        mListener= new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                System.out.println("childevent "+ snapshot);
                chatIds.add(snapshot.child("chatId").getValue().toString());
                //getReceiverNameUri(snapshot.child("chatId").getValue().toString(),snapshot.getKey().toString());
                receiverIds.add(snapshot.getKey().toString());
                if(snapshot.getKey().toString().equals(lastChild)){
                    System.out.println("at Last Child "+lastChild +" "+snapshot.getKey());
                    System.out.println("Invoke");
                    //getNameUriInitial();
                    recyclerAdapterInvokeMessenger();
                    initial = false;
                }
                if (initial == false) {
                    System.out.println(snapshot);
                    System.out.println("update");
                    adapter.notifyDataSetChanged();
                }
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







    private void getElements(){
        textViewDestroy = findViewById(R.id.textViewDestroyMessenger);
        bottomNavigationView = findViewById(R.id.bottomNavigationViewMessenger);
        recyclerView = findViewById(R.id.recyclerViewMessenger);
    }
    private void bottomNav(){

        bottomNavigationView.setSelectedItemId(R.id.chatBottomBar);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()){
                    case R.id.searchBottomBar:
                        Intent intent = new Intent(Messenger.this,SearchGigs.class);
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
                        adapter.onDestroy();
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

        adapter = new RecyclerAdapterMessenger(this, receiverIds,chatIds);
        recyclerView.setAdapter(adapter);


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


*/








/*//RECYCLERADAPTER


package com.example.lyncse.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lyncse.R;
import com.example.lyncse.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecyclerAdapterMessenger extends RecyclerView.Adapter{



    List<String> rId;
    List<String> chatId;
    Context context;
    HashMap<String, ChildEventListener> mRecyclerViewFirebaseListeners = new HashMap<>();
    FirebaseDatabase database =FirebaseDatabase.getInstance();
    DatabaseReference r = database.getReference("Users");



    public RecyclerAdapterMessenger(Context context, List<String> rId, List<String> chatId) {
        this.context = context;
        this.chatId=chatId;
        this.rId=rId;


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.from(parent.getContext()).inflate(R.layout.recycler_messenger, parent, false);
        RecyclerAdapterMessenger.ViewHolder1 viewHolder1 = new RecyclerAdapterMessenger.ViewHolder1(view);
        return viewHolder1;



    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {



        ViewHolder1 vh = (ViewHolder1) holder;
        String key = chatId.get(position);
        vh.ivProfilePic.setImageDrawable(null);



        //System.out.println("key "+key);


        r.child(rId.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    User userProfile = snapshot.getValue(User.class);
                    vh.textViewName.setText(userProfile.fullName);
                    Glide.with(vh.itemView.getContext()).load(userProfile.uriProfile).into(vh.ivProfilePic);


                }else{
                    System.out.println("nothing");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Failed");
            }
        });

        ChildEventListener listener= FirebaseDatabase.getInstance().getReference("Messages/Chats/"+key).limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                vh.textViewMessage.setText(snapshot.child("message").getValue().toString());

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
        });

        //System.out.println(key);
        //System.out.println(listener);
        //FirebaseDatabase.getInstance().getReference("Messages/Chats/"+key).removeEventListener(listener);

        // Add to our map and set tag on view holder
        mRecyclerViewFirebaseListeners.put(key, listener);
        vh.itemView.setTag(R.id.TAG_RCV_EVENT_KEY, key);

        vh.llm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("clicked");
                //Intent intent = new Intent(v.getContext(), GigSelectedSearch.class);
                //intent.putExtra("GIG_ID", textViewListIds.getText().toString());
                //intent.putExtra("GIG_CREATORID", textViewCreatorIds.getText().toString());

                //v.getContext().startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {

        return chatId.size();

    }
    public class ViewHolder1 extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewMessage;
        TextView textViewTimeAgo;
        ImageView ivProfilePic;
        LinearLayout llm;



        public ViewHolder1(@NonNull View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.textViewProfileNameMessenger);
            ivProfilePic = itemView.findViewById(R.id.imageViewProfilePicMessenger);
            textViewMessage= itemView.findViewById(R.id.textViewLastMessage);
            textViewTimeAgo= itemView.findViewById(R.id.textViewTimeAgoMessenger);
            llm=itemView.findViewById(R.id.linearLayoutMessenger);







        }
    }



    public void onDestroy() {

        System.out.println("On destroy "+ getItemCount());

        // Iterate over map and remove listeners for the key
        for (Map.Entry<String, ChildEventListener> entry : mRecyclerViewFirebaseListeners.entrySet()) {
            String key = entry.getKey();
            ChildEventListener value = entry.getValue();
            FirebaseDatabase.getInstance().getReference("Messages/Chats/"+key).removeEventListener(value);

            System.out.println("Removed Event listener for key: " + key);
        }


    }


}*/



