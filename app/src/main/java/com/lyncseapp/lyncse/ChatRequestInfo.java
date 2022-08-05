package com.lyncseapp.lyncse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.lyncseapp.lyncse.fragments.RequestChatFragment;
import com.lyncseapp.lyncse.fragments.RequestInfoFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;

public class ChatRequestInfo extends AppCompatActivity {
    private ViewPager2 vp;
    private TabLayout tl;
    private BottomNavigationView bottomNavigationView;
    Bundle bundle;
    private String reqId;
    private String theirName;
    private String theirUri;
    private String title;
    private String price;
    private String status;
    private String creatorId;
    private String durationTime;
    private String duration;
    private ImageButton bb;
    ViewPager2.OnPageChangeCallback callback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_request_info);

        reqId = getIntent().getStringExtra("REQ_ID");
        duration = getIntent().getStringExtra("REQ_DURATION");
        durationTime = getIntent().getStringExtra("REQ_DURATIONTIME");
        theirName=getIntent().getStringExtra("THEIR_NAME");
        theirUri=getIntent().getStringExtra("THEIR_URI");
        title=getIntent().getStringExtra("TITLE");
        price=getIntent().getStringExtra("PRICE");
        status=getIntent().getStringExtra("STATUS");
        creatorId=getIntent().getStringExtra("CREATOR_ID");
        bundle = new Bundle();

        bundle.putString("REQ_ID", reqId );
        bundle.putString("REQ_DURATION", duration );
        bundle.putString("REQ_DURATIONTIME", durationTime );
        bundle.putString("THEIR_NAME",theirName);
        bundle.putString("THEIR_URI",theirUri);
        bundle.putString("TITLE",title);
        bundle.putString("PRICE",price);
        bundle.putString("STATUS",status);
        bundle.putString("CREATOR_ID",creatorId);

        bottomNav();
        getElements();
        setAdapters();
        setListeners();
    }


    private void setListeners(){
        bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //RequestChatFragment firstFragment = (RequestChatFragment) getSupportFragmentManager().getFragments().get(0);
                //firstFragment.removeListener();
                finish();


            }
        });
    }



    private void getElements(){
        tl=findViewById(R.id.tabLayoutChatRequestInfo);
        vp=findViewById(R.id.viewPager2ChatRequestInfo);
        bb=findViewById(R.id.imageButtonChatRequestInfo);


    }
    private void setAdapters(){
        vp.setAdapter(new FragmentStateAdapter(getSupportFragmentManager(),getLifecycle()) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {

                switch(position){
                    case 0:

                        RequestChatFragment rc = new RequestChatFragment();
                        rc.setArguments(bundle);
                        return rc;
                    case 1:
                        RequestInfoFragment ri = new RequestInfoFragment();
                        ri.setArguments(bundle);
                        return ri;
                    default:
                        return null;
                }

            }

            @Override
            public int getItemCount() {
                return tl.getTabCount();
            }
        });
        callback = new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tl.getTabAt(position).select();
            }
        };
        vp.registerOnPageChangeCallback(callback);
        tl.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vp.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void bottomNav(){
        bottomNavigationView = findViewById(R.id.bottomNavigationViewChatRequestInfo);
        bottomNavigationView.setSelectedItemId(R.id.requestsBottomBar);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()){


                }


                return false;
            }
        });


    }



}