package com.lyncseapp.lyncse.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lyncseapp.lyncse.Messages;
import com.lyncseapp.lyncse.R;
import com.lyncseapp.lyncse.RetrieveMessages;
import com.lyncseapp.lyncse.User;
import com.lyncseapp.lyncse.adapters.RecyclerAdapterChatRequestMyMessage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatGigMessagesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatGigMessagesFragment extends Fragment {
    RecyclerView recyclerView;
    EditText editViewSend;
    Button buttonSend;

    private NestedScrollView ns;
    private FirebaseUser user;
    private String userId;

    private boolean loadMore;


    private DatabaseReference ref;
    private DatabaseReference refChats;
    private DatabaseReference refReceive;
    private FirebaseDatabase database;
    private ChildEventListener mListener;
    private String receiverId;
    private RetrieveMessages rm;

    private int newChat;

    private String chatId;


    private String myUri;
    private String myName;
    private String theirName;
    private String theirUri;


    private List<String> messages;
    private List<String> uri;
    private List<String> name;
    private List<Long> sentTime;
    private List<Integer> typeView;
    private List<String> messageId;

    private int x;

    private boolean initial;

    RecyclerAdapterChatRequestMyMessage adapter;

    private boolean scrollDown;
    private boolean checkScrollingUp;
    private boolean editTextFocused;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChatGigMessagesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatGigMessagesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatGigMessagesFragment newInstance(String param1, String param2) {
        ChatGigMessagesFragment fragment = new ChatGigMessagesFragment();
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
        return inflater.inflate(R.layout.fragment_chat_gig_messages, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editTextFocused=false;
        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();
        receiverId =this.getArguments().getString("RECEIVER_ID");
        theirName =this.getArguments().getString("THEIR_NAME");
        theirUri =this.getArguments().getString("THEIR_URI");

        System.out.println("their uri "+theirUri);

        database=FirebaseDatabase.getInstance();
        refChats=database.getReference("Messages/Chats/");


        newChat=0;

        scrollDown=false;
        messages=new ArrayList<String>();
        uri=new ArrayList<String>();
        name=new ArrayList<String>();
        sentTime=new ArrayList<Long>();
        typeView=new ArrayList<Integer>();
        messageId=new ArrayList<String>();
        x=0;
        initial=true;
        loadMore=true;



        getUserNameUri();
        getViewElements(view);


        getChatId();




    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("DESTROYING");
        if(mListener!=null) {
            refReceive.removeEventListener(mListener);
        }


    }

    @Override
    public void onStop() {
        super.onStop();
        //ref.removeEventListener(mListener);
    }
    @Override
    public void onPause() {
        super.onPause();
        //ref.removeEventListener(mListener);
    }

    @Override
    public void onResume(){
        super.onResume();
        //Toast.makeText(getActivity().getApplicationContext(),"Resuming", Toast.LENGTH_LONG).show();
        //receiveMessage();
    }


    private void getUserNameUri(){

       database.getReference("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile !=null){

                    myName=userProfile.firstName;
                    myUri=userProfile.uriProfile;
                    System.out.println(myName + " %# "+myUri);
                    //receiveMessage();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void receiveMessage(){


        mListener= new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                System.out.println(snapshot.getValue());
                rm = snapshot.getValue(RetrieveMessages.class);

                messageId.add(snapshot.getKey().toString());
                if(rm.id.equals(userId)) {

                    messages.add(rm.message);
                    sentTime.add(rm.createdOn);
                    uri.add(myUri);
                    name.add(myName);
                    typeView.add(0);
                    System.out.println("My Messages: "+messages);
                    //formatTimeStamp(x);
                    x=x+1;

                }else{
                    messages.add(rm.message);
                    sentTime.add(rm.createdOn);
                    uri.add(theirUri);
                    name.add(theirName);
                    typeView.add(1);
                    System.out.println("My Messages: "+messages);
                    //formatTimeStamp(x);
                    x=x+1;

                }
                if (initial==true){
                    scrollDown=true;
                    recyclerAdapterInvokeMyMessage();
                    initial =false;
                }else{
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
        refReceive.limitToLast(20).addChildEventListener(mListener);

    }


    private void getViewElements(View v){
        recyclerView = v.findViewById(R.id.chatGigMessagesRecyclerView);
        buttonSend = v.findViewById(R.id.chatGigMessagesButtonSend);
        editViewSend = v.findViewById(R.id.chatGigMessagesEditText);
        ns=v.findViewById(R.id.nestedScrollViewChatGigMessages);
    }
    private void addListeners(){
        editViewSend.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendMessage();
                    handled = true;
                }
                return handled;
            }
        });
        editViewSend.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    System.out.println("edittext has focus");
                    editTextFocused=true;
                }else{
                    System.out.println("edittext lost focus");
                    editTextFocused=false;
                }

            }
        });
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        ns.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {


                if (scrollY == 0) {
                    System.out.println("Load More messages");
                    if (loadMore == true) {
                    loadMore=false;
                    getMoreMessages();
                    }
                }

                if(!v.canScrollVertically(1)){
                    System.out.println("ScrollDown True");
                    checkScrollingUp=true;
                    scrollDown=true;
                }
                if(checkScrollingUp==true){
                    if(v.canScrollVertically(1)){
                        System.out.println("Moving up");
                        checkScrollingUp=false;
                        scrollDown=false;
                    }
                }
                if(!v.canScrollVertically(-1)){
                    System.out.println("ScrollDown false");
                    scrollDown=false;
                }
            }
        });
    }


    private void sendMessage(){

        if(newChat==1){
            createChat();
        }else
        if(newChat==2) {
            Messages m = new Messages(editViewSend.getText().toString(), userId,receiverId, ServerValue.TIMESTAMP);


            //ref= ref.child(gigId);
            refChats
                    .child(chatId).child(refChats.child(chatId).push().getKey()).setValue(m).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        //Toast.makeText(getActivity().getApplicationContext(),"Request created", Toast.LENGTH_LONG).show();
                    } else {
                        //Toast.makeText(getActivity().getApplicationContext(),"Failed. Try again.", Toast.LENGTH_LONG).show();
                    }
                }
            });
            editViewSend.getText().clear();

        }



    }

    private void getChatId(){

        ref = database.getReference("Messages/Users/"+userId+"/"+receiverId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               if(snapshot.exists()){
                   newChat=2;
                   System.out.println("chat Exists " + snapshot.child("chatId").getValue() );
                   chatId=snapshot.child("chatId").getValue().toString();
                   refReceive = database.getReference("Messages/Chats/"+chatId);
                   receiveMessage();
               }else{
                   newChat=1;
                   System.out.println("new chat");
               }
                addListeners();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Failed");
                Toast.makeText(getActivity().getApplicationContext(),"Failed. Try again.", Toast.LENGTH_LONG).show();

            }
        });
    }

    private void createChat(){

        ref = database.getReference("Messages");
        chatId=ref.push().getKey();
        System.out.println("created Id "+chatId);

        ref = database.getReference("Messages/Users/"+userId+"/"+receiverId);

        ref.child("chatId").setValue(chatId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(getActivity().getApplicationContext(),"chat created", Toast.LENGTH_LONG).show();
                    ref = database.getReference("Messages/Users/"+receiverId+"/"+userId);

                    ref.child("chatId").setValue(chatId).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {

                                Toast.makeText(getActivity().getApplicationContext(),"chat created", Toast.LENGTH_LONG).show();
                                newChat=2;
                                sendMessageAfterCreated();
                            }
                            else
                            {
                                //DELETE CREATED RECORD BEFORE
                                ref = database.getReference("Messages/Users/");
                                ref.child(userId).removeValue();
                                ref = database.getReference("Messages/Users/");
                                ref.child(receiverId).removeValue();
                                Toast.makeText(getActivity().getApplicationContext(),"Failed. Try again.", Toast.LENGTH_LONG).show();

                            }
                        }
                    });
                }
                else
                {
                    Toast.makeText(getActivity().getApplicationContext(),"Failed. Try again.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void sendMessageAfterCreated(){

            Messages m = new Messages(editViewSend.getText().toString(), userId,receiverId, ServerValue.TIMESTAMP);


            //ref= ref.child(gigId);
            refChats
                    .child(chatId).child(refChats.child(chatId).push().getKey()).setValue(m).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        getChatId();
                        //Toast.makeText(getActivity().getApplicationContext(),"Request created", Toast.LENGTH_LONG).show();
                    } else {
                        //Toast.makeText(getActivity().getApplicationContext(),"Failed. Try again.", Toast.LENGTH_LONG).show();
                    }
                }
            });
            editViewSend.getText().clear();



    }

private void getMoreMessages(){

    List<String> messagesBuffer;
    List<String> uriBuffer;
    List<String> nameBuffer;
    List<Long> sentTimeBuffer;
    List<Integer> typeViewBuffer;
    List<String> messageIdBuffer;
    messagesBuffer=new ArrayList<String>();
    uriBuffer=new ArrayList<String>();
    nameBuffer=new ArrayList<String>();
    sentTimeBuffer=new ArrayList<Long>();
    typeViewBuffer=new ArrayList<Integer>();
    messageIdBuffer=new ArrayList<String>();

        System.out.println(messageId.get(0));
    refReceive.orderByKey().endBefore(messageId.get(0)).limitToLast(5).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            System.out.println(snapshot);
            if(snapshot.getValue() == null) {
                System.out.println("No more messages");
                return;
            }
            for (DataSnapshot userGigsSnapshot: snapshot.getChildren()) {
                System.out.println(userGigsSnapshot.getKey());
                messageIdBuffer.add(0,userGigsSnapshot.getKey());

                RetrieveMessages rm1 = userGigsSnapshot.getValue(RetrieveMessages.class);
                System.out.println(rm1.message);
                if(rm1.id.equals(userId)) {

                    messagesBuffer.add(rm1.message);
                    sentTimeBuffer.add(rm1.createdOn);
                    uriBuffer.add(myUri);
                    nameBuffer.add(myName);
                    typeViewBuffer.add(0);

                }
                else {
                    messagesBuffer.add(rm1.message);
                    sentTimeBuffer.add(rm1.createdOn);
                    uriBuffer.add(theirUri);
                    nameBuffer.add(theirName);
                    typeViewBuffer.add(1);
                }


            }
            Collections.reverse(messageIdBuffer);

            messageId.addAll(0,messageIdBuffer);
            messages.addAll(0,messagesBuffer);
            sentTime.addAll(0,sentTimeBuffer);
            uri.addAll(0,uriBuffer);
            name.addAll(0,nameBuffer);
            typeView.addAll(0,typeViewBuffer);
            System.out.println(messageId);
            //adapter.notifyDataSetChanged();
            adapter.notifyItemRangeInserted(0,messageIdBuffer.size());
            adapter.notifyItemChanged(messageIdBuffer.size());
            loadMore=true;
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });
}

    public void recyclerAdapterInvokeMyMessage() {

        System.out.println("messages :" +messages+ "uri :" +uri+ "name :" +name);

        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));


        adapter = new RecyclerAdapterChatRequestMyMessage(this.getContext(),  messages, sentTime, uri, name,typeView);
        recyclerView.setAdapter(adapter);


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()){
            @Override
            public void onLayoutCompleted (RecyclerView.State state){
                super.onLayoutCompleted(state);
                // update UI for new layout

                if(scrollDown==true) {
                    System.out.println("LayOut Completed Scrolling Down");
                    if(editTextFocused==false) {
                        ns.fullScroll(View.FOCUS_DOWN);
                    }
                    if(editTextFocused==true){
                        ns.fullScroll(View.FOCUS_DOWN);
                        editViewSend.requestFocus();
                    }
                }
            }
        });
        //Scroll listener for end of list
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                //int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();

                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(-1)) { //1 for down
                    //System.out.println("Load More messages");
                    //if (loadMore == true) {
                        //loadMore=false;
                        //getMoreMessages();
                    //}
                }



            }
        });

    }





}