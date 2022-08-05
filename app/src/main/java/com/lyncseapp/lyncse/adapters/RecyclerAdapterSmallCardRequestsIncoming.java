package com.lyncseapp.lyncse.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lyncseapp.lyncse.R;

import java.util.List;

public class RecyclerAdapterSmallCardRequestsIncoming extends RecyclerView.Adapter<RecyclerAdapterSmallCardRequestsIncoming.ViewHolder> {
    List<String> userId;
    List<String> euId;
    List<String> gigId;
    List<String> status;
    List<Long> createdOn;
    List<String> title;
    List<String> price;
    List<String> uriGig;
    List<String> userName;
    List<String> requestIds;
    List<String> userUri;
    List<String> dateCreated;
    Context context;


    public RecyclerAdapterSmallCardRequestsIncoming(Context context,  List<String> userId, List<String> euId, List<String> gigId, List<String> status, List<Long> createdOn,List<String> title,List<String> price,List<String> uriGig,List<String> userName,List<String> requestIds,List<String> userUri,List<String> dateCreated) {
        this.userId = userId;
        this.euId=euId;
        this.gigId=gigId;
        this.status=status;
        this.createdOn=createdOn;
        this.title=title;
        this.price=price;
        this.uriGig=uriGig;
        this.userName=userName;
        this.requestIds=requestIds;
        this.userUri=userUri;
        this.context = context;
        this.dateCreated=dateCreated;
    }

    @NonNull
    @Override
    public RecyclerAdapterSmallCardRequestsIncoming.ViewHolder onCreateViewHolder (@NonNull ViewGroup
                                                                                   parent,int viewType){

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.from(parent.getContext()).inflate(R.layout.recycler_small_card_requests, parent, false);
        RecyclerAdapterSmallCardRequestsIncoming.ViewHolder viewHolder = new RecyclerAdapterSmallCardRequestsIncoming.ViewHolder(view);
        return viewHolder;

    }
    @Override
    public void onBindViewHolder (@NonNull RecyclerAdapterSmallCardRequestsIncoming.ViewHolder holder,
                                  int position){
        holder.imageView.setImageDrawable(null);

        if(uriGig.get(position).equals("Default")){
            Glide.with(holder.itemView.getContext()).load(R.drawable.ic_baseline_android_200).into(holder.imageView);
        }else {
            Glide.with(holder.itemView.getContext()).load(uriGig.get(position)).into(holder.imageView);
        }
        holder.theirUri.setText(userUri.get(position));
        holder.textView.setText(title.get(position));
        holder.textView2.setText("$" + price.get(position));
        holder.textViewListIds.setText(requestIds.get(position));
        holder.textViewCreatorIds.setText(userId.get(position));
        holder.textViewCreatorName.setText(userName.get(position));
        holder.textViewStatus.setText(status.get(position));
        holder.textView3Requests.setText(dateCreated.get(position));
        if(userUri.get(position).equals("Default")){
            Glide.with(holder.itemView.getContext()).load(R.drawable.ic_baseline_android_200).into(holder.imageViewprofilePicRequests);
        }else {
            Glide.with(holder.itemView.getContext()).load(userUri.get(position)).into(holder.imageViewprofilePicRequests);
        }
        //holder.textView.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //        Toast.makeText(context,"Clicked", Toast.LENGTH_LONG).show();
        //    }
        //});
    }

    @Override
    public int getItemCount () {
        return title.size();
    }

    public void addElement
            (List < String > data, List < String > listPrices, List < String > listUri){
        data.addAll(data);
        listPrices.addAll(listPrices);
        listUri.addAll(listUri);
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView theirUri;
        TextView textView;
        TextView textView2;
        TextView textViewListIds;
        TextView textViewCreatorIds;
        ImageView imageView;
        ImageView imageViewprofilePicRequests;
        TextView textViewStatus;
        TextView textViewCreatorName;
        TextView textView3Requests;
        CardView cv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            theirUri=itemView.findViewById(R.id.theirUriTextView);
            textView = itemView.findViewById(R.id.recyclerSmallCardTitleRequests);
            textView2 = itemView.findViewById(R.id.recyclerSmallCardPriceRequests);
            textViewListIds = itemView.findViewById(R.id.recyclerSmallCardGigIdRequests);
            textViewCreatorIds = itemView.findViewById(R.id.textViewSmallCardCreatorIdRequests);
            imageView = itemView.findViewById(R.id.recyclerSmallCardImageViewRequests);
            imageViewprofilePicRequests=itemView.findViewById(R.id.profilePicRequests);
            textViewStatus=itemView.findViewById(R.id.textView5);
            textViewCreatorName=itemView.findViewById(R.id.textView4);
            textView3Requests=itemView.findViewById(R.id.textView3Requests);
            cv = (CardView) itemView.findViewById(R.id.cardViewRecyclerSmallCardRequests);


            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    {
                        //Intent intent = new Intent(v.getContext(), GigSelectedSearch.class);
                        //intent.putExtra("GIG_ID", textViewListIds.getText().toString());
                        //intent.putExtra("GIG_CREATORID", textViewCreatorIds.getText().toString());

                        //v.getContext().startActivity(intent);
                    }
                }

            });
        }
    }
}