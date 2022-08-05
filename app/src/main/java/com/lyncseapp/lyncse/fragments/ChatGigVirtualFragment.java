package com.lyncseapp.lyncse.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lyncseapp.lyncse.Gigs;
import com.lyncseapp.lyncse.R;
import com.lyncseapp.lyncse.User;
import com.lyncseapp.lyncse.chat.ChatSmallCardAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatGigVirtualFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatGigVirtualFragment extends Fragment {

    private FirebaseUser user;
    private String userId;



    RecyclerView recyclerView;
    RecyclerView recyclerView2;
    RecyclerView recyclerView3;
    ChatSmallCardAdapter adapter;
    ChatSmallCardAdapter adapter2;
    ChatSmallCardAdapter adapter3;
    ImageView iv;
    ImageView iv1;
    ImageView iv2;
    ImageView iv3;
    TextView tv;

    private List<String> userGigsIdList;
    private List<String> listTitles;
    private List<String> listUri;
    private List<String> listPrices;
    private List<String> listIds;
    private List<String> listType;
    private List<String> listCreator;



    private List<String> userGigsIdList2;
    private List<String> listTitles2;
    private List<String> listUri2;
    private List<String> listPrices2;
    private List<String> listIds2;
    private List<String> listType2;
    private List<String> listCreator2;


    private List<String> userGigsIdList3;
    private List<String> listTitles3;
    private List<String> listUri3;
    private List<String> listPrices3;
    private List<String> listIds3;
    private List<String> listType3;
    private List<String> listCreator3;

    private Integer x;
    boolean loadMore;
    boolean loadMore2;
    boolean loadMore3;
    boolean initialLoad;
    boolean initialLoad2;
    boolean initialLoad3;

    private int sizeResults;
    private int sizeResults2;
    private int sizeResults3;
    private int sizeResultsTotal;
    private int sizeResultsTotal2;
    private int sizeResultsTotal3;

    FirebaseDatabase database;
    DatabaseReference ref;
    DatabaseReference refu;
    DatabaseReference ref2;
    DatabaseReference ref3;

    String lastGigId;
    String lastGigId2;
    String lastGigId3;
    private String receiverId;

    LinearLayout llTabVirtual;
    LinearLayout llTabOutside;
    LinearLayout llTabFind;

    int goneVisibilityElements;
    int visibilityCount;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChatGigVirtualFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatGigVirtualFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatGigVirtualFragment newInstance(String param1, String param2) {
        ChatGigVirtualFragment fragment = new ChatGigVirtualFragment();
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
        return inflater.inflate(R.layout.fragment_chat_gig_virtual, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        user = FirebaseAuth.getInstance().getCurrentUser();
        userId= user.getUid();
        receiverId =this.getArguments().getString("RECEIVER_ID");

        database= FirebaseDatabase.getInstance();

        listTitles=new ArrayList<String>();
        listUri=new ArrayList<String>();
        listPrices=new ArrayList<String>();
        listIds=new ArrayList<String>();
        listType=new ArrayList<String>();
        listCreator=new ArrayList<String>();
        userGigsIdList=new ArrayList();


        listTitles2=new ArrayList<String>();
        listUri2=new ArrayList<String>();
        listPrices2=new ArrayList<String>();
        listIds2=new ArrayList<String>();
        listType2=new ArrayList<String>();
        listCreator2=new ArrayList<String>();
        userGigsIdList2=new ArrayList();

        listTitles3=new ArrayList<String>();
        listUri3=new ArrayList<String>();
        listPrices3=new ArrayList<String>();
        listIds3=new ArrayList<String>();
        listType3=new ArrayList<String>();
        listCreator3=new ArrayList<String>();
        userGigsIdList3=new ArrayList();

        initialLoad=true;
        initialLoad2=true;
        initialLoad3=true;
        sizeResults=0;
        sizeResultsTotal=0;
        sizeResults2=0;
        sizeResultsTotal2=0;
        sizeResults3=0;
        sizeResultsTotal3=0;
        x=0;

        ref=database.getReference("Gigs/Virtual");
        ref2=database.getReference("Gigs/Outside");
        ref3=database.getReference("Gigs/FindRequests");

        goneVisibilityElements=0;
        visibilityCount=0;
        getUserNameUriProfile(receiverId,this);
        getElements(view);
        eventListenerGigUID(ref,1);//Get IDS of user gigs
        eventListenerGigUID(ref2,2);
        eventListenerGigUID(ref3,3);

    }



    private void getElements(View v){
        recyclerView = v.findViewById(R.id.recyclerChatVirtualViewTab);
        recyclerView2 = v.findViewById(R.id.recyclerChatOutsideViewTab);
        recyclerView3 = v.findViewById(R.id.recyclerChatFindViewTab);
        iv= v.findViewById(R.id.profilePicChatViewTab);
        iv1=v.findViewById(R.id.imageView3ChatVirtualViewTab);
        iv2=v.findViewById(R.id.imageView4ChatVirtualViewTab);
        iv3=v.findViewById(R.id.imageView4ChatFindViewTab);
        tv=v.findViewById(R.id.firstNameChatViewTab);
        llTabVirtual=v.findViewById(R.id.llVirtualChatViewTab);
        llTabOutside=v.findViewById(R.id.linearLayoutOutsideChatViewTab);
        llTabFind=v.findViewById(R.id.linearLayoutFindChatViewTab);
    }

    private void getUserNameUriProfile(String id,Fragment f){
        refu = database.getReference("Users");
        refu.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile !=null){

                    tv.setText(userProfile.firstName);
                    if (userProfile.uriProfile.equals("Default")) {
                        Glide.with(f)
                                .load(R.drawable.ic_baseline_android_200)
                                .into(iv);
                    } else {
                        Glide.with(f)
                                .load(userProfile.uriProfile)
                                .into(iv);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });


    }
    private void verifyVisibilityRecycler(int x){
        if(x==3){
            llTabVirtual.setVisibility(View.VISIBLE);
            llTabOutside.setVisibility(View.VISIBLE);
            llTabFind.setVisibility(View.VISIBLE);
        }
    }
    private void eventListenerGigUID(DatabaseReference r,int a){
        if(a==1) {
            //GETUID OF GIG--------------------------------------------------------------------------------
            r.orderByChild("userID").equalTo(receiverId).limitToFirst(6).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    System.out.println("1 "+receiverId+" "+snapshot);
                    //userGigsIdList=new ArrayList();// everytime a change happens the array is cleared.
                    visibilityCount=visibilityCount+1;
                    if (snapshot.getValue() == null) {
                        goneVisibilityElements=goneVisibilityElements+1;
                        verifyVisibilityRecycler(goneVisibilityElements);
                        return;
                    }
                    llTabVirtual.setVisibility(View.VISIBLE);
                    sizeResults = (int) snapshot.getChildrenCount();
                    sizeResultsTotal = sizeResultsTotal + sizeResults;
                    System.out.println(sizeResults);
                    for (DataSnapshot userGigsSnapshot : snapshot.getChildren()) {


                        //System.out.println(uGigID);
                        userGigsIdList.add(userGigsSnapshot.getKey());
                        listIds.add(userGigsSnapshot.getKey());
                        listType.add("Virtual");
                        lastGigId = userGigsSnapshot.getKey();
                        getTitlesUriPrice(userGigsSnapshot.getKey().toString(), ref,1);


                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });

            //-----------------------------------------------------------------------------------------------
        }else if(a==2){
            System.out.println("Inside OUTSIDE");
            //GETUID OF GIG--------------------------------------------------------------------------------
            r.orderByChild("userID").equalTo(receiverId).limitToFirst(6).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    System.out.println(snapshot);
                    //userGigsIdList=new ArrayList();// everytime a change happens the array is cleared.
                    visibilityCount=visibilityCount+1;
                    if (snapshot.getValue() == null) {
                        goneVisibilityElements=goneVisibilityElements+1;
                        verifyVisibilityRecycler(goneVisibilityElements);
                        return;
                    }
                    llTabOutside.setVisibility(View.VISIBLE);
                    sizeResults2 = (int) snapshot.getChildrenCount();
                    sizeResultsTotal2 = sizeResultsTotal2 + sizeResults2;
                    for (DataSnapshot userGigsSnapshot : snapshot.getChildren()) {


                        //System.out.println(uGigID);
                        userGigsIdList2.add(userGigsSnapshot.getKey());
                        listIds2.add(userGigsSnapshot.getKey());
                        listType2.add("Outside");
                        lastGigId2 = userGigsSnapshot.getKey();
                        System.out.println("Snapshokey outside "+userGigsSnapshot.getKey().toString());
                        getTitlesUriPrice(userGigsSnapshot.getKey().toString(), ref2,2);


                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });

            //-----------------------------------------------------------------------------------------------

        } else {
            r.orderByChild("userID").equalTo(receiverId).limitToFirst(6).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    System.out.println("1 "+receiverId+" "+snapshot);
                    //userGigsIdList=new ArrayList();// everytime a change happens the array is cleared.
                    visibilityCount=visibilityCount+1;
                    if (snapshot.getValue() == null) {
                        goneVisibilityElements=goneVisibilityElements+1;
                        verifyVisibilityRecycler(goneVisibilityElements);
                        return;
                    }
                    llTabFind.setVisibility(View.VISIBLE);
                    sizeResults3 = (int) snapshot.getChildrenCount();
                    sizeResultsTotal3 = sizeResultsTotal3 + sizeResults3;
                    System.out.println(sizeResults3);
                    for (DataSnapshot userGigsSnapshot : snapshot.getChildren()) {


                        //System.out.println(uGigID);
                        userGigsIdList3.add(userGigsSnapshot.getKey());
                        listIds3.add(userGigsSnapshot.getKey());
                        listType3.add("Find");
                        lastGigId3 = userGigsSnapshot.getKey();
                        getTitlesUriPrice(userGigsSnapshot.getKey().toString(), ref3,3);


                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });
        }
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




                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getMoreResults(String key,DatabaseReference r,int a){


        if(a==1) {
            r.orderByChild("userID").startAfter(receiverId, key).endAt(receiverId).limitToFirst(5).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    //userGigsIdList=new ArrayList();// everytime a change happens the array is cleared.
                    if (snapshot.getValue() == null) {
                        return;
                    }


                    sizeResults = (int) snapshot.getChildrenCount();
                    sizeResultsTotal = sizeResultsTotal + sizeResults;
                    System.out.println(sizeResults);
                    for (DataSnapshot userGigsSnapshot : snapshot.getChildren()) {
                        System.out.println(userGigsSnapshot.child("userID").getValue());

                        System.out.println("same");


                        //System.out.println(uGigID);
                        userGigsIdList.add(userGigsSnapshot.getKey());
                        listIds.add(userGigsSnapshot.getKey());
                        listType.add("Virtual");

                        lastGigId = userGigsSnapshot.getKey();
                        getTitlesUriPrice(userGigsSnapshot.getKey().toString(), ref, a);
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });
        }else if(a==2){
            r.orderByChild("userID").startAfter(receiverId, key).endAt(receiverId).limitToFirst(5).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    //userGigsIdList=new ArrayList();// everytime a change happens the array is cleared.
                    if (snapshot.getValue() == null) {
                        return;
                    }


                    sizeResults2 = (int) snapshot.getChildrenCount();
                    sizeResultsTotal2 = sizeResultsTotal2 + sizeResults2;
                    System.out.println(sizeResults2);
                    for (DataSnapshot userGigsSnapshot : snapshot.getChildren()) {
                        System.out.println(userGigsSnapshot.child("userID").getValue());

                        System.out.println("same");


                        //System.out.println(uGigID);
                        userGigsIdList2.add(userGigsSnapshot.getKey());
                        listIds2.add(userGigsSnapshot.getKey());
                        listType2.add("Outside");

                        lastGigId2 = userGigsSnapshot.getKey();
                        getTitlesUriPrice(userGigsSnapshot.getKey().toString(), ref2, a);
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });
        }else{
            r.orderByChild("userID").startAfter(receiverId, key).endAt(receiverId).limitToFirst(5).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    //userGigsIdList=new ArrayList();// everytime a change happens the array is cleared.
                    if (snapshot.getValue() == null) {
                        return;
                    }


                    sizeResults3 = (int) snapshot.getChildrenCount();
                    sizeResultsTotal3 = sizeResultsTotal3 + sizeResults3;
                    System.out.println(sizeResults3);
                    for (DataSnapshot userGigsSnapshot : snapshot.getChildren()) {
                        System.out.println(userGigsSnapshot.child("userID").getValue());

                        System.out.println("same");


                        //System.out.println(uGigID);
                        userGigsIdList3.add(userGigsSnapshot.getKey());
                        listIds3.add(userGigsSnapshot.getKey());
                        listType3.add("Find");

                        lastGigId3 = userGigsSnapshot.getKey();
                        getTitlesUriPrice(userGigsSnapshot.getKey().toString(), ref3, a);
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });
        }
    }


    private void getTitlesUriPrice(String id,DatabaseReference r,int a){

if(a==1) {
    r.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            Gigs userGigs1 = snapshot.getValue(Gigs.class);

            if (userGigs1 != null) {


                listTitles.add(userGigs1.title);
                //-------
                listUri.add(userGigs1.uriBanner);
                listPrices.add(snapshot.child("option1").child("price").getValue().toString());
                listCreator.add(receiverId);
                System.out.println(id + " " + userGigs1.title);
                System.out.println(snapshot.child("option1").child("price").getValue());


                //fl.setVisibility(View.VISIBLE);
            }
            System.out.println(listPrices.size() + " " + sizeResultsTotal);
            if (listPrices.size() == sizeResultsTotal) {
                update(a);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }

    });
}else if(a==2){

    r.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            Gigs userGigs1 = snapshot.getValue(Gigs.class);

            if (userGigs1 != null) {


                listTitles2.add(userGigs1.title);
                //-------
                listUri2.add(userGigs1.uriBanner);
                listPrices2.add(snapshot.child("option1").child("price").getValue().toString());
                listCreator2.add(receiverId);



                //fl.setVisibility(View.VISIBLE);
            }

            if (listPrices2.size() == sizeResultsTotal2) {
                update(a);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }

    });

} else {
    r.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            Gigs userGigs1 = snapshot.getValue(Gigs.class);

            if (userGigs1 != null) {


                listTitles3.add(userGigs1.title);
                //-------
                listUri3.add(userGigs1.uriBanner);
                listPrices3.add(snapshot.child("option1").child("price").getValue().toString());
                listCreator3.add(receiverId);



                //fl.setVisibility(View.VISIBLE);
            }

            if (listPrices3.size() == sizeResultsTotal3) {
                update(a);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }

    });
}

    }


    private void update(int x) {

        if (x == 1) {
            if (sizeResults != 0) {
                if (initialLoad == true) {
                    recyclerAdapterInvoke();
                    loadMore = true;
                    System.out.println("invoke");
                } else {
                    adapter.notifyDataSetChanged();
                    loadMore = true;
                    System.out.println("update");
                }
                iv1.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }else{
                iv1.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        } else if(x==2) {
            if (sizeResults2 != 0) {
                if (initialLoad2 == true) {
                    recyclerAdapterInvoke2();
                    loadMore2 = true;
                    System.out.println("invoke2");
                } else {
                    adapter2.notifyDataSetChanged();
                    loadMore2 = true;
                    System.out.println("update");
                }
                iv2.setVisibility(View.GONE);
                recyclerView2.setVisibility(View.VISIBLE);
            } else {
                iv2.setVisibility(View.VISIBLE);
                recyclerView2.setVisibility(View.GONE);
            }
        } else {
            if (sizeResults3 != 0) {
                if (initialLoad3 == true) {
                    recyclerAdapterInvoke3();
                    loadMore3 = true;
                    System.out.println("invoke3");
                } else {
                    adapter3.notifyDataSetChanged();
                    loadMore3 = true;
                    System.out.println("update");
                }
                iv3.setVisibility(View.GONE);
                recyclerView3.setVisibility(View.VISIBLE);
            } else {
                iv3.setVisibility(View.VISIBLE);
                recyclerView3.setVisibility(View.GONE);
            }
        }
    }
    public void recyclerAdapterInvoke() {
        System.out.println(listCreator);

        initialLoad=false;


        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));


        adapter = new ChatSmallCardAdapter(this.getContext(), listTitles, listPrices, listUri,listIds,listCreator,listType);
        recyclerView.setAdapter(adapter);


        //Scroll listener for end of list
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollHorizontally(1)) {
                    if (loadMore == true) {
                        System.out.println("at end");
                        loadMore=false;
                        getMoreResults(lastGigId,ref,1);

                    }

                }
            }
        });


    }


    public void recyclerAdapterInvoke2() {

        initialLoad2=false;


        recyclerView2.setLayoutManager(new LinearLayoutManager(this.getContext(),LinearLayoutManager.HORIZONTAL, false));


        adapter2 = new ChatSmallCardAdapter(this.getContext(), listTitles2, listPrices2, listUri2,listIds2,listCreator2,listType2);
        recyclerView2.setAdapter(adapter2);


        //Scroll listener for end of list
        recyclerView2.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    if (loadMore2 == true) {
                        System.out.println("at end");
                        loadMore2=false;
                        getMoreResults(lastGigId2,ref2,2);
                    }
                }
            }
        });


    }

    public void recyclerAdapterInvoke3() {

        initialLoad3=false;


        recyclerView3.setLayoutManager(new LinearLayoutManager(this.getContext(),LinearLayoutManager.HORIZONTAL, false));


        adapter3 = new ChatSmallCardAdapter(this.getContext(), listTitles3, listPrices3, listUri3,listIds3,listCreator3,listType3);
        recyclerView3.setAdapter(adapter3);


        //Scroll listener for end of list
        recyclerView3.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    if (loadMore3 == true) {
                        System.out.println("at end");
                        loadMore3=false;
                        getMoreResults(lastGigId3,ref3,3);
                    }
                }
            }
        });


    }

}