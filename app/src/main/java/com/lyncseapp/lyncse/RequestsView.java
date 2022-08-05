package com.lyncseapp.lyncse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.lyncseapp.lyncse.fragments.FindRequestsViewFragment;
import com.lyncseapp.lyncse.fragments.IncomingRequestsFragment;
import com.lyncseapp.lyncse.fragments.SentRquestsFragment;
import com.lyncseapp.lyncse.search.ActivitySearch;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;

public class RequestsView extends AppCompatActivity {

    private ViewPager2 vp;
    private TabLayout tl;
    private BottomNavigationView bottomNavigationView;
    private ImageButton bb;
    ViewPager2.OnPageChangeCallback callback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests_view);

        bottomNav();
        getElements();
        setAdapters();
        setListeners();
    }
    @Override
    public void onRestart() {
        super.onRestart();
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here
        bottomNavigationView.setSelectedItemId(R.id.requestsBottomBar);

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("destroy");
        vp.unregisterOnPageChangeCallback(callback);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
        overridePendingTransition(0,0);
    }
    private void setListeners(){
        bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
                overridePendingTransition(0,0);
            }
        });
    }


private void getElements(){
        tl=findViewById(R.id.tabLayoutRequestsView);
        vp=findViewById(R.id.viewPagerRequestsView);
        bb=findViewById(R.id.imageButtonRequestView);



}
private void setAdapters(){
    vp.setAdapter(new FragmentStateAdapter(getSupportFragmentManager(),getLifecycle()) {
        @NonNull
        @Override
        public Fragment createFragment(int position) {

            switch(position){
                case 0:
                    SentRquestsFragment sr = new SentRquestsFragment();
                    return sr;
                case 1:
                    IncomingRequestsFragment ir = new IncomingRequestsFragment();
                    return ir;
                case 2:
                    FindRequestsViewFragment fr = new FindRequestsViewFragment();
                    return fr;
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
        bottomNavigationView = findViewById(R.id.bottomNavigationViewRequestsView);
        bottomNavigationView.setSelectedItemId(R.id.requestsBottomBar);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()){
                    case R.id.chatBottomBar:
                        startActivity(new Intent(RequestsView.this, Messenger.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.searchBottomBar:

                        startActivity(new Intent(RequestsView.this, ActivitySearch.class));
                        overridePendingTransition(0,0);

                        return true;
                    case R.id.homeBottomBar:
                        startActivity(new Intent(RequestsView.this,ProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.myGigsBottomBar:
                        startActivity(new Intent(RequestsView.this,myGigs.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.requestsBottomBar:

                        return true;

                }


                return false;
            }
        });


    }
}