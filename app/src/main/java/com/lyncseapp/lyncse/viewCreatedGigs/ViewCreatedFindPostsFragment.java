package com.lyncseapp.lyncse.viewCreatedGigs;

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

import com.lyncseapp.lyncse.Gigs;
import com.lyncseapp.lyncse.R;
import com.lyncseapp.lyncse.RecyclerAdapterUserViewCreatedGigs;
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
 * Use the {@link ViewCreatedFindPostsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewCreatedFindPostsFragment extends Fragment {
    private FirebaseUser user;
    private String userId;

    private FrameLayout flLoadCenter;
    private FrameLayout flLoadBottom;
    private ImageView ivDefault;

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
    private boolean createdAdapter;

    FirebaseDatabase database;
    DatabaseReference ref;

    String lastGigId;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ViewCreatedFindPostsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ViewCreatedFindPostsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewCreatedFindPostsFragment newInstance(String param1, String param2) {
        ViewCreatedFindPostsFragment fragment = new ViewCreatedFindPostsFragment();
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
        return inflater.inflate(R.layout.fragment_view_created_find_posts, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userId= user.getUid();
        database= FirebaseDatabase.getInstance();
        createdAdapter=false;
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

        ref=database.getReference("Gigs/FindRequests");


        getElements(view);
        eventListenerGigUID();//Get IDS of user gigs
    }
    public void refresh(){
        listTitles.clear();
        listUri.clear();
        listPrices.clear();
        listIds.clear();
        listType.clear();
        userGigsIdList.clear();
        sizeResults=0;
        sizeResultsTotal=0;
        x=0;
        if(createdAdapter==true) {
            adapter.notifyDataSetChanged();
        }
        flLoadCenter.setVisibility(View.VISIBLE);
        eventListenerGigUID();
    }
    private void getElements(View v){
        recyclerView = v.findViewById(R.id.recyclerViewCreatedFind);
        flLoadCenter = v.findViewById(R.id.flLoad_ViewCreatedFind);
        flLoadBottom = v.findViewById(R.id.llLoadBottom_ViewCreatedFind);
        ivDefault = v.findViewById(R.id.imageViewDefault_ViewCreatedFind);
    }


    private void eventListenerGigUID(){
        updateRecycler=false;

        //GETUID OF GIG--------------------------------------------------------------------------------
        ref.orderByChild("userID").equalTo(userId).limitToFirst(6).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //userGigsIdList=new ArrayList();// everytime a change happens the array is cleared.
                if(snapshot.getValue()==null) {
                    flLoadCenter.setVisibility(View.GONE);
                    ivDefault.setVisibility(View.VISIBLE);
                    return;
                }



                sizeResults=(int)snapshot.getChildrenCount();
                sizeResultsTotal=sizeResultsTotal+sizeResults;
                System.out.println(sizeResults);
                for (DataSnapshot userGigsSnapshot: snapshot.getChildren()) {


                    //System.out.println(uGigID);
                    userGigsIdList.add(userGigsSnapshot.getKey());
                    listIds.add(userGigsSnapshot.getKey());
                    listType.add("Find");
                    lastGigId=userGigsSnapshot.getKey();
                    getTitlesUriPrice(userGigsSnapshot.getKey().toString(),ref);


                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

        //-----------------------------------------------------------------------------------------------
    }


    private void getMoreResults(String key){



        System.out.println("in test function");

        ref.orderByChild("userID").startAfter(userId, key).endAt(userId).limitToFirst(5).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //userGigsIdList=new ArrayList();// everytime a change happens the array is cleared.
                if (snapshot.getValue() == null) {
                    flLoadBottom.setVisibility(View.GONE);
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
                    listType.add("Find");
                    lastGigId = userGigsSnapshot.getKey();
                    getTitlesUriPrice(userGigsSnapshot.getKey().toString(), ref);
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

        if (sizeResults != 0) {
            if (initialLoad == true) {
                flLoadCenter.setVisibility(View.GONE);
                recyclerAdapterInvoke();
                loadMore = true;
                System.out.println("invoke");
            } else {
                flLoadCenter.setVisibility(View.GONE);
                flLoadBottom.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
                loadMore = true;
                System.out.println("update");
            }
        }

    }



    public void recyclerAdapterInvoke() {

        initialLoad=false;


        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));


        adapter = new RecyclerAdapterUserViewCreatedGigs(this.getContext(), listTitles, listPrices, listUri,listIds,listType);
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
                        getMoreResults(lastGigId);
                        flLoadBottom.setVisibility(View.VISIBLE);

                    }

                }
            }
        });
        createdAdapter=true;

    }
}