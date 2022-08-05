package com.lyncseapp.lyncse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.lyncseapp.lyncse.fragments.ChatGigMessagesFragment;
import com.lyncseapp.lyncse.fragments.ChatGigVirtualFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;

public class ChatGig extends AppCompatActivity {

    private ViewPager2 vp;
    private TabLayout tl;
    private BottomNavigationView bottomNavigationView;
    Bundle bundle;
    private String receiverId;
    private String theirName;
    private String theirUri;
    private ImageButton bb;
    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_gig);


        receiverId=getIntent().getStringExtra("RECEIVER_ID");
        theirName=getIntent().getStringExtra("THEIR_NAME");
        theirUri=getIntent().getStringExtra("THEIR_URI");

        bundle = new Bundle();
        bundle.putString("RECEIVER_ID", receiverId );
        bundle.putString("THEIR_URI", theirUri);
        bundle.putString("THEIR_NAME", theirName );


        bottomNav();
        getElements();
        setAdapters();
        setListeners();
        setBroadCastReceiver();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        //Unregister BroadcastReceiver
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(broadcastReceiver);
    }
    private void setBroadCastReceiver(){
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Get data from intent
                String body = intent.getStringExtra("body");
                String title = intent.getStringExtra("title");
                //System.out.println("broadcast got "+ body);
                //sendNotification(title,body);
            }
        };

        IntentFilter intentFilter = new IntentFilter("IntentFilterAction");
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(broadcastReceiver, intentFilter);
    }
    private void sendNotification(String title, String text) {
        if (title.equals("Messenger: New Message")) {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(ChatGig.this, "NOTIFICATION_CHANNEL")
                    .setSmallIcon(R.drawable.logolyncse)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager =
                    NotificationManagerCompat.from(ChatGig.this);

            notificationManager.notify(1, notificationBuilder.build());
        }else{
            //Intent intent = new Intent(this, ProfileActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
            //PendingIntent.FLAG_ONE_SHOT);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(ChatGig.this, "NOTIFICATION_CHANNEL")
                    .setSmallIcon(R.drawable.logolyncse)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setAutoCancel(true);
            //.setContentIntent(pendingIntent);

            NotificationManagerCompat notificationManager =
                    NotificationManagerCompat.from(ChatGig.this);

            notificationManager.notify(1, notificationBuilder.build());
        }
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
        tl=findViewById(R.id.tabLayoutChatRequestInfoChatGig);
        vp=findViewById(R.id.viewPager2ChatRequestInfoChatGig);
        bb=findViewById(R.id.imageButtonChatRequestInfoChatGig);

        vp.setUserInputEnabled(false);
    }
    private void setAdapters(){
        vp.setAdapter(new FragmentStateAdapter(getSupportFragmentManager(),getLifecycle()) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {

                switch(position){
                    case 0:

                        ChatGigMessagesFragment c = new ChatGigMessagesFragment();
                        c.setArguments(bundle);
                        return c;
                    case 1:
                        ChatGigVirtualFragment v = new ChatGigVirtualFragment();
                        v.setArguments(bundle);
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

    private void bottomNav(){
        bottomNavigationView = findViewById(R.id.bottomNavigationViewChatRequestInfoChatGig);
        bottomNavigationView.setSelectedItemId(R.id.chatBottomBar);

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