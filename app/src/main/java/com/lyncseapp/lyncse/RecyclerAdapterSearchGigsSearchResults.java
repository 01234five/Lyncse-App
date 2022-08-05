package com.lyncseapp.lyncse;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lyncseapp.lyncse.findRequests.SelectedGigFindActivity;

import java.util.List;

public class RecyclerAdapterSearchGigsSearchResults extends RecyclerView.Adapter<RecyclerAdapterSearchGigsSearchResults.ViewHolder> {
    private List<String> data;
    private List<String> listUri;
    private List<String> listPrices;
    private List<String> listIds;
    private List<String> listCreatorIds;
    private List<String> listType;
    Context context;

    public RecyclerAdapterSearchGigsSearchResults(Context context, List<String> data, List<String> listPrices, List<String> listUri,List<String> listIds,List<String> listCreatorIds,List<String> listType) {
        this.listUri=listUri;
        this.listPrices= listPrices;
        this.listIds=listIds;
        this.data = data;
        this.listType=listType;
        this.context = context;
        this.listCreatorIds=listCreatorIds;

    }

    @NonNull
    @Override
    public RecyclerAdapterSearchGigsSearchResults.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recycler_user_view_created_gigs,parent,false );
        RecyclerAdapterSearchGigsSearchResults.ViewHolder viewHolder = new RecyclerAdapterSearchGigsSearchResults.ViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterSearchGigsSearchResults.ViewHolder holder, int position) {
        holder.imageView.setImageDrawable (null);

        if(listUri.get(position).equals("Default")){
            Glide.with(holder.itemView.getContext()).load(R.drawable.ic_baseline_android_200).into(holder.imageView);
        }else {
            Glide.with(holder.itemView.getContext()).load(listUri.get(position)).into(holder.imageView);
        }
        holder.textView.setText(data.get(position));
        holder.textView2.setText("$"+listPrices.get(position));
        holder.textViewListIds.setText(listIds.get(position));
        holder.textViewCreatorId.setText(listCreatorIds.get(position));
        holder.t = listType.get(position);

        //holder.textView.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //        Toast.makeText(context,"Clicked", Toast.LENGTH_LONG).show();
        //    }
        //});
        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.t.equals("Find")){
                    System.out.println("FindRequest");
                    Intent intent = new Intent(v.getContext(), SelectedGigFindActivity.class);
                    intent.putExtra("GIG_CREATORID", holder.textViewCreatorId.getText().toString());
                    intent.putExtra("GIG_ID", holder.textViewListIds.getText().toString());
                    intent.putExtra("GIG_TYPE", holder.t);
                    v.getContext().startActivity(intent);
                }else {
                    Intent intent = new Intent(v.getContext(),GigSelectedSearch.class);
                    intent.putExtra("GIG_CREATORID", holder.textViewCreatorId.getText().toString());
                    intent.putExtra("GIG_ID", holder.textViewListIds.getText().toString());
                    intent.putExtra("GIG_TYPE", holder.t);
                    v.getContext().startActivity(intent);
                }
            }

        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void addElement(List<String> data, List<String> listPrices, List<String> listUri){
        data.addAll(data);
        listPrices.addAll(listPrices);
        listUri.addAll(listUri);
        notifyDataSetChanged();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        TextView textView2;
        TextView textViewListIds;
        TextView textViewCreatorId;
        ImageView imageView;
        CardView cv;
        String t;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.gigInfoRecyclerUserViewCreatedGigs);
            textView2 = itemView.findViewById(R.id.textViewPriceRecyclerUserViewCreatedGigs);
            textViewListIds=itemView.findViewById(R.id.textViewGigId);
            textViewCreatorId=itemView.findViewById(R.id.textViewCreatorIdUserViewCreatedGigs);
            imageView = itemView.findViewById(R.id.imageView2GigImage);
            cv = (CardView)itemView.findViewById(R.id.cardViewRecyclerUserViewCreatedGigs);
            t="";

        }
    }
}
