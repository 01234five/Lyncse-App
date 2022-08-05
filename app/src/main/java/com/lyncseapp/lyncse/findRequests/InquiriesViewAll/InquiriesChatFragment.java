package com.lyncseapp.lyncse.findRequests.InquiriesViewAll;

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
 * Use the {@link InquiriesChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InquiriesChatFragment extends Fragment {
    RecyclerView recyclerView;
    EditText editViewSend;
    Button buttonSend;
    private DatabaseReference ref;
    private FirebaseDatabase database;
    private String reqId;
    private FirebaseUser user;
    private String userId;
    private String theirName;
    private String theirUri;
    private ChildEventListener mListener;
    private List<String> messages;
    private List<String> uri;
    private List<String> name;
    private List<String> theirMessages;
    private List<Long> sentTime;
    private List<Integer> typeView;
    List<String> messageId;
    private String myUri;
    private String myName;
    private RetrieveMessages rm;
    private int x;
    private boolean initial;
    RecyclerAdapterChatRequestMyMessage adapter;
    private boolean scrollDown;
    private boolean checkScrollingUp;
    private NestedScrollView ns;
    private boolean loadMore;
    private boolean editTextFocused;
    private String receiverId;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public InquiriesChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InquiriesChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InquiriesChatFragment newInstance(String param1, String param2) {
        InquiriesChatFragment fragment = new InquiriesChatFragment();
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
        return inflater.inflate(R.layout.fragment_inquiries_chat, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        x=0;
        editTextFocused=false;
        rm = new RetrieveMessages();
        messages=new ArrayList<String>();
        uri=new ArrayList<String>();
        name=new ArrayList<String>();
        sentTime=new ArrayList<Long>();
        typeView=new ArrayList<Integer>();
        messageId=new ArrayList<String>();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();
        reqId = this.getArguments().getString("REQ_ID");
        theirName=this.getArguments().getString("THEIR_NAME");
        theirUri=this.getArguments().getString("THEIR_URI");
        database = FirebaseDatabase.getInstance();
        initial=true;
        scrollDown=false;
        loadMore=true;
        getViewElements(view);
        getReceiverId();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("DESTROYING");
        if(mListener!=null) {
            ref.removeEventListener(mListener);
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

    public void removeListener(){
        System.out.println("REMOVING LISTENER FROM FIREBASE");

    }
    private void getReceiverId(){
        database.getReference("Inquiries/"+reqId+"/").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("sId").getValue().toString().equals(userId)){
                    receiverId =snapshot.child("euId").getValue().toString();
                }else {
                    receiverId = snapshot.child("sId").getValue().toString();
                }
                getUserName();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getViewElements(View v){
        recyclerView = v.findViewById(R.id.InquiriesChatRecyclerView);
        buttonSend = v.findViewById(R.id.InquiriesChatButtonSend);
        editViewSend = v.findViewById(R.id.InquiriesChatEditText);
        ns=v.findViewById(R.id.nestedScrollViewInquiriesChat);
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
                    if (loadMore == true) {
                        System.out.println("Load More messages");
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


        //ref = database.getReference("Requests/"+reqId+"/Chat/").push();
        //Map<String, Object> values = new HashMap<>();
        //values.put("Name", "x");
        //values.put("Job", "y");
        //values.put("Number", "f");
        //ref.setValue(values);


        Messages m = new Messages(editViewSend.getText().toString(),userId,receiverId, ServerValue.TIMESTAMP);


        //ref= ref.child(gigId);
        FirebaseDatabase.getInstance().getReference("Inquiries/"+reqId+"/Chat")
                .child(ref.push().getKey()).setValue(m).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    //Toast.makeText(getActivity().getApplicationContext(),"Request created", Toast.LENGTH_LONG).show();
                }
                else
                {
                    //Toast.makeText(getActivity().getApplicationContext(),"Failed. Try again.", Toast.LENGTH_LONG).show();
                }
            }
        });
        editViewSend.getText().clear();

    }

    private void receiveMessage(){

        ref = database.getReference("Inquiries/"+reqId+"/Chat");
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
                    //recyclerView.scrollToPosition(messages.size() - 1);
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
        ref.limitToLast(20).addChildEventListener(mListener);

    }

    private void getUserName(){

        ref = database.getReference("Users");

        ref.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile !=null){

                    myName=userProfile.firstName;
                    myUri=userProfile.uriProfile;
                    System.out.println(myName + " %# "+myUri);
                    //receiveMessage();
                    addListeners();
                    receiveMessage();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
        ref.orderByKey().endBefore(messageId.get(0)).limitToLast(5).addListenerForSingleValueEvent(new ValueEventListener() {
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
                if (!recyclerView.canScrollVertically(1)) { //1 for down
                    //if (loadMore == true) {
                    //chopList();
                    //}
                }



            }
        });

    }

}