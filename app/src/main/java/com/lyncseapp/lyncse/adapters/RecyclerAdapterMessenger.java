package com.lyncseapp.lyncse.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lyncseapp.lyncse.ChatGig;
import com.lyncseapp.lyncse.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecyclerAdapterMessenger extends RecyclerView.Adapter{



    List<String> rId;
    List<String> chatId;
    List<String> name;
    List<String> uri;
    Context context;
    HashMap<String, ChildEventListener> mRecyclerViewFirebaseListeners = new HashMap<>();
    String userId;


    HashMap<String,Handler> h = new HashMap<>();
    HashMap<String,Runnable> rArray = new HashMap<>();

    HashMap<String,String> messages = new HashMap<>();

    List<String> s= new ArrayList<String>();

    boolean destroying=false;


    public RecyclerAdapterMessenger(Context context, List<String> rId, List<String> chatId, List<String> name, List<String> uri,String userId) {


        this.context = context;
        this.chatId=chatId;
        this.rId=rId;
        this.name=name;
        this.uri=uri;
        this.userId=userId;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.from(parent.getContext()).inflate(R.layout.recycler_messenger, parent, false);
        RecyclerAdapterMessenger.ViewHolder1 viewHolder1 = new RecyclerAdapterMessenger.ViewHolder1(view);
        return viewHolder1;




    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        String r=rId.get(position);
        String n=name.get(position);
        String u=uri.get(position);
        String key = chatId.get(position);
        if(s.contains(key)){
            System.out.println("found Key");
        }else {
            ViewHolder1 vh = (ViewHolder1) holder;


            s.add(key);
            vh.ivProfilePic.setImageDrawable(null);

            vh.textViewName.setText(name.get(position));
            if (uri.get(position).equals("Default")) {
                Glide.with(vh.vhc).load(R.drawable.ic_baseline_android_200).into(vh.ivProfilePic);
            }else {
                Glide.with(vh.vhc).load(uri.get(position)).into(vh.ivProfilePic);
            }


            //System.out.println("key "+key);
            ChildEventListener listener = FirebaseDatabase.getInstance().getReference("Messages/Chats/" + key).limitToLast(1).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    if (snapshot.child("id").getValue().toString().equals(userId)) {
                        vh.textViewMessage.setText("Me: " + snapshot.child("message").getValue().toString());
                    } else {
                        vh.textViewMessage.setText(snapshot.child("message").getValue().toString());
                    }
                    vh.createdOn = snapshot.child("createdOn").getValue().toString();
                    //remove key first// Kept in here and not outside so all of this code runs first before adding more to hashmaps
                    if(h.containsKey(key)) {
                        System.out.println("found key");

                        if (h.get(key) != null) {
                            h.get(key).removeCallbacks(rArray.get(key));
                            h.remove(key);
                            rArray.remove(key);
                        }
                    }
                    //-------------------------

                        timeAgo(vh, snapshot.child("createdOn").getValue().toString(), key);//read key

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
            });


            //System.out.println(key);
            //System.out.println(listener);
            //FirebaseDatabase.getInstance().getReference("Messages/Chats/"+key).removeEventListener(listener);

            // Add to our map and set tag on view holder
            mRecyclerViewFirebaseListeners.put(key, listener);
            vh.itemView.setTag(R.id.TAG_RCV_EVENT_KEY, key);


            vh.llm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("clicked");
                    Intent intent = new Intent(v.getContext(), ChatGig.class);
                    intent.putExtra("RECEIVER_ID", r);
                    intent.putExtra("THEIR_NAME", n);
                    intent.putExtra("THEIR_URI", u);

                    v.getContext().startActivity(intent);
                }
            });


        }

    }

    @Override
    public int getItemCount() {
        return chatId.size();

    }
    public class ViewHolder1 extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewMessage;
        TextView textViewTimeAgo;
        ImageView ivProfilePic;
        Context vhc;
        LinearLayout llm;
        String createdOn;





        public ViewHolder1(@NonNull View itemView) {
            super(itemView);
            vhc=itemView.getContext();
            textViewName = itemView.findViewById(R.id.textViewProfileNameMessenger);
            ivProfilePic = itemView.findViewById(R.id.imageViewProfilePicMessenger);
            textViewMessage= itemView.findViewById(R.id.textViewLastMessage);
            textViewTimeAgo= itemView.findViewById(R.id.textViewTimeAgoMessenger);
            llm=itemView.findViewById(R.id.linearLayoutMessenger);





        }
    }




public void onResume(){
        String keyHandler;

    // Iterate over map and remove listeners for the key
    for (Map.Entry<String, ChildEventListener> entry : mRecyclerViewFirebaseListeners.entrySet()) {
        String key = entry.getKey();
        ChildEventListener value = entry.getValue();
        FirebaseDatabase.getInstance().getReference("Messages/Chats/"+key).addChildEventListener(value);

        System.out.println("Added Event listener for key: " + key);
    }




}

public void onPause(){
if(destroying==false) {
    for (Map.Entry<String, Handler> entry : h.entrySet()) {
        String key = entry.getKey();
        //Handler value = entry.getValue();
        if (h.get(key) != null) {
            h.get(key).removeCallbacks(rArray.get(key));
        }

    }


    // Iterate over map and remove listeners for the key
    for (Map.Entry<String, ChildEventListener> entry : mRecyclerViewFirebaseListeners.entrySet()) {
        String key = entry.getKey();
        ChildEventListener value = entry.getValue();
        FirebaseDatabase.getInstance().getReference("Messages/Chats/" + key).removeEventListener(value);

        System.out.println("Removed Event listener for key: " + key);
    }
}
}
    public void onDestroy() {

        System.out.println("On destroy "+ getItemCount());

//Destroy Hanlders
        for (Map.Entry<String, Handler> entry : h.entrySet()) {
            String key = entry.getKey();
            //Handler value = entry.getValue();

                if (h.get(key) != null) {
                    h.get(key).removeCallbacks(rArray.get(key));
                }

        }


        // Iterate over map and remove listeners for the key
        for (Map.Entry<String, ChildEventListener> entry : mRecyclerViewFirebaseListeners.entrySet()) {
            String key = entry.getKey();
            ChildEventListener value = entry.getValue();
            FirebaseDatabase.getInstance().getReference("Messages/Chats/"+key).removeEventListener(value);

            System.out.println("Removed Event listener for key: " + key);
        }


    }
public void timeAgo(ViewHolder1 vh, String createdOn,String key){

    final Handler handler = new Handler();

    final Runnable r = new Runnable() {
        @Override
        public void run() {

            System.out.println(createdOn) ;
            long timeDifference = (Instant.now().toEpochMilli() - Long.parseLong(createdOn)) / 1000;

            long timeUnit;

            if(timeDifference < 60){
                timeUnit = DateUtils.SECOND_IN_MILLIS;
            } else if(timeDifference < 3600){
                timeUnit = DateUtils.MINUTE_IN_MILLIS;
            } else if (timeDifference < 86400){
                timeUnit = DateUtils.HOUR_IN_MILLIS;
            }else if (timeDifference < 31536000){
                timeUnit = DateUtils.DAY_IN_MILLIS;
            } else {
                timeUnit = DateUtils.YEAR_IN_MILLIS;
            }

            CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(Long.parseLong(createdOn), Instant.now().toEpochMilli(),
                    timeUnit, DateUtils.FORMAT_ABBREV_RELATIVE);



            System.out.println(timeAgo.toString());
            if(timeDifference < 60){vh.textViewTimeAgo.setText("Just now");}else {
                vh.textViewTimeAgo.setText(timeAgo.toString());
            }


            handler.postDelayed(this, 60000);
        }
    };
    h.put(key,handler);
    rArray.put(key,r);
    handler.post(r);

}

public void setDestroying(){
       destroying=true;
}


}