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

import java.util.List;

public class RecyclerAdapterUserViewCreatedGigs extends RecyclerView.Adapter<RecyclerAdapterUserViewCreatedGigs.ViewHolder> {
    List<String> data;
    List<String> listUri;
    List<String> listPrices;
    List<String> listIds;
    List<String> listType;
    Context context;

    public RecyclerAdapterUserViewCreatedGigs(Context context, List<String> data, List<String> listPrices, List<String> listUri,List<String> listIds,List<String> listType) {
        this.listUri=listUri;
        this.listPrices= listPrices;
        this.listIds=listIds;
        this.listType=listType;
        this.data = data;
        this.context = context;

    }

    @NonNull
    @Override
    public RecyclerAdapterUserViewCreatedGigs.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recycler_user_view_created_gigs,parent,false );
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterUserViewCreatedGigs.ViewHolder holder, int position) {
        holder.imageView.setImageDrawable (null);


        if(listUri.get(position).equals("Default")){
            Glide.with(holder.itemView.getContext()).load(R.drawable.ic_baseline_android_200).into(holder.imageView);
        }else {
            Glide.with(holder.itemView.getContext()).load(listUri.get(position)).into(holder.imageView);
        }
        holder.textView.setText(data.get(position));
        holder.textView2.setText("$"+listPrices.get(position));
        holder.textViewListIds.setText(listIds.get(position));
        holder.t = listType.get(position);
        //holder.textView.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //        Toast.makeText(context,"Clicked", Toast.LENGTH_LONG).show();
        //    }
        //});
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
        ImageView imageView;
        String t;
        CardView cv;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.gigInfoRecyclerUserViewCreatedGigs);
            textView2 = itemView.findViewById(R.id.textViewPriceRecyclerUserViewCreatedGigs);
            textViewListIds=itemView.findViewById(R.id.textViewGigId);
            imageView = itemView.findViewById(R.id.imageView2GigImage);
            cv = (CardView)itemView.findViewById(R.id.cardViewRecyclerUserViewCreatedGigs);
            t="";

            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   System.out.println("Clicked");
                   System.out.println(textViewListIds.getText().toString());
                   Intent intent = new Intent(v.getContext(),userEditCreatedGig.class);
                   intent.putExtra("GIG_ID", textViewListIds.getText().toString());
                    intent.putExtra("GIG_TYPE", t);
                    v.getContext().startActivity(intent);
                }

            });
        }
    }
}
