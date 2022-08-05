package com.lyncseapp.lyncse.search;

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

import com.lyncseapp.lyncse.Messenger;
import com.lyncseapp.lyncse.ProfileActivity;
import com.lyncseapp.lyncse.R;
import com.lyncseapp.lyncse.RequestsView;
import com.lyncseapp.lyncse.myGigs;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;

public class ActivitySearch extends AppCompatActivity  {
    private ViewPager2 vp;
    private TabLayout tl;
    private ImageButton bb;
    private BottomNavigationView bottomNavigationView;
    Bundle bundle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

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
        bottomNavigationView.setSelectedItemId(R.id.searchBottomBar);

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
        tl=findViewById(R.id.tabLayoutSearch);
        vp=findViewById(R.id.viewPager2Search);
        bb=findViewById(R.id.imageButtonSearch);

        vp.setUserInputEnabled(false);

    }
    private void setAdapters(){
        vp.setAdapter(new FragmentStateAdapter(getSupportFragmentManager(),getLifecycle()) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {

                switch(position){
                    case 0:

                        ViewTabFragment c = new ViewTabFragment();
                        c.setArguments(bundle);
                        return c;
                    case 1:
                        SearchTabFragment v = new SearchTabFragment();
                        return v;
                    default:
                        return null;
                }

            }

            @Override
            public int getItemCount() {
                return tl.getTabCount();
            }
        });
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

    private void bottomNav() {
        bottomNavigationView = findViewById(R.id.bottomNavigationViewSearch);
        bottomNavigationView.setSelectedItemId(R.id.searchBottomBar);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.searchBottomBar:
                        return true;


                    case R.id.homeBottomBar:
                        startActivity(new Intent(ActivitySearch.this, ProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.myGigsBottomBar:
                        startActivity(new Intent(ActivitySearch.this, myGigs.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.requestsBottomBar:
                        startActivity(new Intent(ActivitySearch.this, RequestsView.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.chatBottomBar:
                        startActivity(new Intent(ActivitySearch.this, Messenger.class));
                        overridePendingTransition(0,0);
                        return true;

                }


                return false;
            }
        });
    }

}