
/*

package com.example.lyncse.adapters;

        import android.content.Context;
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
        import com.example.lyncse.R;
        import com.example.lyncse.User;
        import com.google.firebase.database.ChildEventListener;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;

        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;

public class RecyclerAdapterMessenger extends RecyclerView.Adapter{



    List<String> rId;
    List<String> chatId;
    Context context;
    HashMap<String, ChildEventListener> mRecyclerViewFirebaseListeners = new HashMap<>();
    FirebaseDatabase database =FirebaseDatabase.getInstance();
    DatabaseReference r = database.getReference("Users");



    public RecyclerAdapterMessenger(Context context, List<String> rId, List<String> chatId) {
        this.context = context;
        this.chatId=chatId;
        this.rId=rId;


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



        ViewHolder1 vh = (ViewHolder1) holder;
        String key = chatId.get(position);
        vh.ivProfilePic.setImageDrawable(null);



        //System.out.println("key "+key);


        r.child(rId.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    User userProfile = snapshot.getValue(User.class);
                    vh.textViewName.setText(userProfile.fullName);
                    Glide.with(vh.itemView.getContext()).load(userProfile.uriProfile).into(vh.ivProfilePic);


                }else{
                    System.out.println("nothing");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Failed");
            }
        });

        ChildEventListener listener= FirebaseDatabase.getInstance().getReference("Messages/Chats/"+key).limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                vh.textViewMessage.setText(snapshot.child("message").getValue().toString());

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
            public void onClick(View view) {
                System.out.println("clicked");
                //Intent intent = new Intent(v.getContext(), GigSelectedSearch.class);
                //intent.putExtra("GIG_ID", textViewListIds.getText().toString());
                //intent.putExtra("GIG_CREATORID", textViewCreatorIds.getText().toString());

                //v.getContext().startActivity(intent);
            }
        });


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
        LinearLayout llm;



        public ViewHolder1(@NonNull View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.textViewProfileNameMessenger);
            ivProfilePic = itemView.findViewById(R.id.imageViewProfilePicMessenger);
            textViewMessage= itemView.findViewById(R.id.textViewLastMessage);
            textViewTimeAgo= itemView.findViewById(R.id.textViewTimeAgoMessenger);
            llm=itemView.findViewById(R.id.linearLayoutMessenger);







        }
    }



    public void onDestroy() {

        System.out.println("On destroy "+ getItemCount());

        // Iterate over map and remove listeners for the key
        for (Map.Entry<String, ChildEventListener> entry : mRecyclerViewFirebaseListeners.entrySet()) {
            String key = entry.getKey();
            ChildEventListener value = entry.getValue();
            FirebaseDatabase.getInstance().getReference("Messages/Chats/"+key).removeEventListener(value);

            System.out.println("Removed Event listener for key: " + key);
        }


    }


}*/



