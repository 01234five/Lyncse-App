package com.lyncseapp.lyncse.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.lyncseapp.lyncse.R;
import com.lyncseapp.lyncse.User;
import com.lyncseapp.lyncse.findRequests.InquiriesViewAll.InquiriesViewActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FindRequestsViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FindRequestsViewFragment extends Fragment {

    private TextView tv1;
    private Button b1;
    private FirebaseUser user;
    private String userId;
    private String userName;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FindRequestsViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FindRequestsViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FindRequestsViewFragment newInstance(String param1, String param2) {
        FindRequestsViewFragment fragment = new FindRequestsViewFragment();
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
        return inflater.inflate(R.layout.fragment_find_requests_view, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        getElements(view);
        super.onViewCreated(view, savedInstanceState);
        setListeners();
    }
    private void getUserProfile() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        userId= user.getUid();
        FirebaseDatabase.getInstance().getReference("Users/" + userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if (userProfile != null) {
                    userName = userProfile.firstName;
                    tv1.setText(userName +" to see inquiries, press below");
                    //editViewElements();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getElements(View v){
        b1 = v.findViewById(R.id.buttonFindRequestsView);
        tv1 = v.findViewById(R.id.textView20);
    }
    private void setListeners(){
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), InquiriesViewActivity.class));

                System.out.println("clicked");
            }
        });
    }
}