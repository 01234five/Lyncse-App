package com.lyncseapp.lyncse.viewCreatedGigs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.lyncseapp.lyncse.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;

public class CreatedViewActivity extends AppCompatActivity {
    private ViewPager2 vp;
    private TabLayout tl;
    private BottomNavigationView bottomNavigationView;
    private ImageButton bb;
    private boolean loadedVirtualFrag;
    private boolean loadedOutsideFrag;
    private boolean loadedFindFrag;
    private boolean currentlyOnVirtual;
    private boolean currentlyOnOutside;
    private boolean currentlyOnFind;
    ViewPager2.OnPageChangeCallback callback;
    ViewCreatedVirtualFragment sr;
    CreatedViewOutsideFragment ir;
    ViewCreatedFindPostsFragment vf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadedFindFrag=false;
        loadedOutsideFrag=false;
        loadedVirtualFrag=false;
        currentlyOnVirtual=true;
        currentlyOnFind=false;
        currentlyOnOutside=false;
        setContentView(R.layout.activity_created_view);

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
        if(loadedVirtualFrag==true) {
            if(currentlyOnVirtual==true) {
                sr.refresh();
            }
        }
        if(loadedOutsideFrag==true) {
            if(currentlyOnOutside==true) {
                ir.refresh();
            }
        }
        if(loadedFindFrag==true) {
            if(currentlyOnFind==true) {
                vf.refresh();
            }
        }
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
        overridePendingTransition(0,0);
    }
    private void setListeners(){
        bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private void getElements(){
        tl=findViewById(R.id.tabLayoutCreatedView);
        vp=findViewById(R.id.viewPagerCreatedView);
        bb=findViewById(R.id.imageButtonCreatedView);
    }
    private void setAdapters(){
        vp.setAdapter(new FragmentStateAdapter(getSupportFragmentManager(),getLifecycle()) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {

                switch(position){
                    case 0:
                        sr = new ViewCreatedVirtualFragment();
                        return sr;
                    case 1:
                        ir = new CreatedViewOutsideFragment();
                        return ir;
                    case 2:
                        vf = new ViewCreatedFindPostsFragment();
                        return vf;
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
                System.out.println(position);
                tl.getTabAt(position).select();
                if(position==0){
                    loadedVirtualFrag=true;
                    currentlyOnVirtual=true;
                    currentlyOnOutside=false;
                    currentlyOnFind=false;
                }
                if(position==1){
                    loadedOutsideFrag=true;
                    currentlyOnVirtual=false;
                    currentlyOnOutside=true;
                    currentlyOnFind=false;
                }
                if(position==2){
                    loadedFindFrag=true;
                    currentlyOnVirtual=false;
                    currentlyOnOutside=false;
                    currentlyOnFind=true;
                }
            }
        };
        vp.registerOnPageChangeCallback(callback);
        tl.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                vp.setCurrentItem(tab.getPosition());
                if(tab.getPosition()==0){
                    loadedVirtualFrag=true;
                    currentlyOnVirtual=true;
                    currentlyOnOutside=false;
                    currentlyOnFind=false;
                }
                if(tab.getPosition()==1){
                    loadedOutsideFrag=true;
                    currentlyOnVirtual=false;
                    currentlyOnOutside=true;
                    currentlyOnFind=false;
                }
                if(tab.getPosition()==2){
                    loadedFindFrag=true;
                    currentlyOnVirtual=false;
                    currentlyOnOutside=false;
                    currentlyOnFind=true;
                }
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
        bottomNavigationView = findViewById(R.id.bottomNavigationViewCreatedView);
        bottomNavigationView.setSelectedItemId(R.id.myGigsBottomBar);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()){
                    case R.id.searchBottomBar:

                        //startActivity(new Intent(RequestsView.this, SearchGigs.class));
                        //overridePendingTransition(0,0);

                        return true;
                    case R.id.homeBottomBar:
                        //startActivity(new Intent(RequestsView.this, ProfileActivity.class));
                        //overridePendingTransition(0,0);
                        return true;

                    case R.id.myGigsBottomBar:
                        //startActivity(new Intent(RequestsView.this, myGigs.class));
                        //overridePendingTransition(0,0);
                        return true;
                    case R.id.requestsBottomBar:

                        return true;

                }


                return false;
            }
        });


    }
}