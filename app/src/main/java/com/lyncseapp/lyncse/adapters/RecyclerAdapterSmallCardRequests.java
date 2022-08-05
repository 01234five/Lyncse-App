package com.lyncseapp.lyncse.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lyncseapp.lyncse.ChatRequestInfo;
import com.lyncseapp.lyncse.R;
import com.lyncseapp.lyncse.requests.RequestAccept;
import com.lyncseapp.lyncse.requests.RequestPay;

import java.util.List;

public class RecyclerAdapterSmallCardRequests extends RecyclerView.Adapter {
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
    List<String> gigType;
    List<String> dateCreated;
    List<Integer> typeId;
    List<String> duration;
    List<String> durationTime;
    Context context;


    public RecyclerAdapterSmallCardRequests(Context context,  List<String> userId, List<String> euId, List<String> gigId,List<String> gigType, List<String> status, List<Long> createdOn,List<String> title,List<String> price,List<String> uriGig,List<String> userName,List<String> requestIds,List<String> userUri,List<String> dateCreated,List<Integer> typeId,List<String> duration,List<String> durationTime) {
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
        this.gigType=gigType;
        this.context = context;
        this.dateCreated=dateCreated;
        this.typeId=typeId;
        this.duration=duration;
        this.durationTime=durationTime;
    }

    @Override
    public int getItemViewType(int position) {
        if(typeId.get(position)==1){
                return 1;
        }
        else if(typeId.get(position)==2){
            return 2;
        }
        else if(typeId.get(position)==3){
            return 3;
        }
        else if(typeId.get(position)==99){
            return 99;
        }else
        {
            return 0;
        }
    }



        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder (@NonNull ViewGroup
        parent,int viewType){


            if(viewType == 1) {
                LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
                View view = layoutInflater.from(parent.getContext()).inflate(R.layout.recycker_small_card_request_accept, parent, false);
                RecyclerAdapterSmallCardRequests.ViewHolder1 viewHolder1 = new RecyclerAdapterSmallCardRequests.ViewHolder1(view);
                return viewHolder1;

            }
            else if(viewType == 2) {
                LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
                View view = layoutInflater.from(parent.getContext()).inflate(R.layout.recycler_small_card_request_p, parent, false);
                RecyclerAdapterSmallCardRequests.ViewHolderSendPayment viewHolderSendPayment = new RecyclerAdapterSmallCardRequests.ViewHolderSendPayment(view);
                return viewHolderSendPayment;

            } else if(viewType == 3) {
                LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
                View view = layoutInflater.from(parent.getContext()).inflate(R.layout.recycler_small_card_requests, parent, false);
                RecyclerAdapterSmallCardRequests.ViewHolderAwaitingPayment viewHolderAwaitingPayment = new RecyclerAdapterSmallCardRequests.ViewHolderAwaitingPayment(view);
                return viewHolderAwaitingPayment;

            } else if(viewType == 99) {
                LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
                View view = layoutInflater.from(parent.getContext()).inflate(R.layout.recycler_small_card_requests, parent, false);
                RecyclerAdapterSmallCardRequests.ViewHolderCompleted viewHolderCompleted = new RecyclerAdapterSmallCardRequests.ViewHolderCompleted(view);
                return viewHolderCompleted;
            }
            else {
                LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
                View view = layoutInflater.from(parent.getContext()).inflate(R.layout.recycler_small_card_requests, parent, false);
                RecyclerAdapterSmallCardRequests.ViewHolder viewHolder = new RecyclerAdapterSmallCardRequests.ViewHolder(view);
                return viewHolder;
            }

        }
        @Override
        public void onBindViewHolder (@NonNull RecyclerView.ViewHolder holder,
        int position){

                if(typeId.get(position)==1){
                ViewHolder1 vh1 = (ViewHolder1) holder;
                vh1.imageView.setImageDrawable(null);

                if (uriGig.get(position).equals("Default")) {
                    Glide.with(vh1.itemView.getContext()).load(R.drawable.ic_baseline_android_200).into(vh1.imageView);
                    vh1.path="Default";
                } else {
                    Glide.with(vh1.itemView.getContext()).load(uriGig.get(position)).into(vh1.imageView);
                    vh1.path=uriGig.get(position);
                }
                    vh1.textViewDuration=duration.get(position);
                    vh1.textViewDurationTime=durationTime.get(position);
                    vh1.theirUri.setText(userUri.get(position));
                    vh1.textView.setText(title.get(position));
                    vh1.textView2.setText("$" + price.get(position));
                    vh1.textViewListIds.setText(requestIds.get(position));
                    vh1.textViewCreatorIds.setText(userId.get(position));
                    vh1.textViewCreatorName.setText(userName.get(position));
                    vh1.textViewStatus.setText(status.get(position));
                    vh1.textView3Requests.setText(dateCreated.get(position));
                    vh1.tvt.setText(gigType.get(position));
                    if (userUri.get(position).equals("Default")) {
                        Glide.with(vh1.itemView.getContext()).load(R.drawable.ic_baseline_android_200).into(vh1.imageViewprofilePicRequests);
                    }else {
                        Glide.with(vh1.itemView.getContext()).load(userUri.get(position)).into(vh1.imageViewprofilePicRequests);
                    }


            }
                else  if(typeId.get(position)==2){

                    ViewHolderSendPayment vhsp = (ViewHolderSendPayment) holder;
                    vhsp.imageView.setImageDrawable(null);

                    //System.out.println("XJKASDKGHASDKJA "+typeId.get(position)+" "+ status.get(position) + " "+ title.get(position)+ " "+ typeId);

                    if (uriGig.get(position).equals("Default")) {
                        Glide.with(vhsp.itemView.getContext()).load(R.drawable.ic_baseline_android_200).into(vhsp.imageView);
                        vhsp.path="Default";
                    } else {
                        Glide.with(vhsp.itemView.getContext()).load(uriGig.get(position)).into(vhsp.imageView);
                        vhsp.path=uriGig.get(position);
                    }
                    vhsp.textViewDuration=duration.get(position);
                    vhsp.textViewDurationTime=durationTime.get(position);
                    vhsp.theirUri.setText(userUri.get(position));
                    vhsp.textView.setText(title.get(position));
                    vhsp.textView2.setText("$" + price.get(position));
                    vhsp.textViewListIds.setText(requestIds.get(position));
                    vhsp.textViewCreatorIds.setText(userId.get(position));
                    vhsp.textViewCreatorName.setText(userName.get(position));
                    vhsp.textViewStatus.setText(status.get(position));
                    vhsp.textView3Requests.setText(dateCreated.get(position));
                    vhsp.tvt.setText(gigType.get(position));
                    if (userUri.get(position).equals("Default")) {
                        Glide.with(vhsp.itemView.getContext()).load(R.drawable.ic_baseline_android_200).into(vhsp.imageViewprofilePicRequests);
                    }else {
                        Glide.with(vhsp.itemView.getContext()).load(userUri.get(position)).into(vhsp.imageViewprofilePicRequests);
                    }


                }else if(typeId.get(position)==3)
                {
                    ViewHolderAwaitingPayment vhap = (ViewHolderAwaitingPayment) holder;
                    vhap.imageView.setImageDrawable(null);

                    if (uriGig.get(position).equals("Default")) {
                        Glide.with(vhap.itemView.getContext()).load(R.drawable.ic_baseline_android_200).into(vhap.imageView);
                    } else {
                        Glide.with(vhap.itemView.getContext()).load(uriGig.get(position)).into(vhap.imageView);
                    }

                    if (status.get(position).equals("Requesting")) {
                        System.out.println(status.get(position) + " " + position);
                    }

                    vhap.textViewDuration=duration.get(position);
                    vhap.textViewDurationTime=durationTime.get(position);
                    vhap.theirUri.setText(userUri.get(position));
                    vhap.textView.setText(title.get(position));
                    vhap.textView2.setText("$" + price.get(position));
                    vhap.textViewListIds.setText(requestIds.get(position));
                    vhap.textViewCreatorIds.setText(userId.get(position));
                    vhap.textViewCreatorName.setText(userName.get(position));
                    vhap.textViewStatus.setText("Awaiting Payment");
                    vhap.textView3Requests.setText(dateCreated.get(position));
                    vhap.tvt.setText(gigType.get(position));
                    if (userUri.get(position).equals("Default")) {
                        Glide.with(vhap.itemView.getContext()).load(R.drawable.ic_baseline_android_200).into(vhap.imageViewprofilePicRequests);
                    }else {
                        Glide.with(vhap.itemView.getContext()).load(userUri.get(position)).into(vhap.imageViewprofilePicRequests);
                    }

                }
                else if(typeId.get(position)==99){
                    RecyclerAdapterSmallCardRequests.ViewHolderCompleted vh = (RecyclerAdapterSmallCardRequests.ViewHolderCompleted) holder;
                    vh.imageView.setImageDrawable(null);

                    if (uriGig.get(position).equals("Default")) {
                        Glide.with(vh.itemView.getContext()).load(R.drawable.ic_baseline_android_200).into(vh.imageView);
                    } else {
                        Glide.with(vh.itemView.getContext()).load(uriGig.get(position)).into(vh.imageView);
                    }

                    if (status.get(position).equals("Requesting")) {
                        System.out.println(status.get(position) + " " + position);
                    }

                    vh.textViewDuration=duration.get(position);
                    vh.textViewDurationTime=durationTime.get(position);
                    vh.theirUri.setText(userUri.get(position));
                    vh.textView.setText(title.get(position));
                    vh.textView2.setText("$" + price.get(position));
                    vh.textViewListIds.setText(requestIds.get(position));
                    vh.textViewCreatorIds.setText(userId.get(position));
                    vh.textViewCreatorName.setText(userName.get(position));
                    vh.textViewStatus.setText(status.get(position));
                    vh.textView3Requests.setText(dateCreated.get(position));
                    vh.tvt.setText(gigType.get(position));

                    if (userUri.get(position).equals("Default")) {
                        Glide.with(vh.itemView.getContext()).load(R.drawable.ic_baseline_android_200).into(vh.imageViewprofilePicRequests);
                    }else {
                        Glide.with(vh.itemView.getContext()).load(userUri.get(position)).into(vh.imageViewprofilePicRequests);
                    }


                    //holder.textView.setOnClickListener(new View.OnClickListener() {
                    //    @Override
                    //    public void onClick(View view) {
                    //        Toast.makeText(context,"Clicked", Toast.LENGTH_LONG).show();
                    //    }
                    //});
                }
                else
            {
                ViewHolder vh = (ViewHolder) holder;
                    vh.imageView.setImageDrawable(null);

                    if (uriGig.get(position).equals("Default")) {
                        Glide.with(vh.itemView.getContext()).load(R.drawable.ic_baseline_android_200).into(vh.imageView);
                    } else {
                        Glide.with(vh.itemView.getContext()).load(uriGig.get(position)).into(vh.imageView);
                    }

                    if (status.get(position).equals("Requesting")) {
                        System.out.println(status.get(position) + " " + position);
                    }

                vh.textViewDuration=duration.get(position);
                vh.textViewDurationTime=durationTime.get(position);
                vh.theirUri.setText(userUri.get(position));
                vh.textView.setText(title.get(position));
                vh.textView2.setText("$" + price.get(position));
                vh.textViewListIds.setText(requestIds.get(position));
                vh.textViewCreatorIds.setText(userId.get(position));
                vh.textViewCreatorName.setText(userName.get(position));
                vh.textViewStatus.setText(status.get(position));
                vh.textView3Requests.setText(dateCreated.get(position));
                vh.tvt.setText(gigType.get(position));
                if (userUri.get(position).equals("Default")) {
                    Glide.with(vh.itemView.getContext()).load(R.drawable.ic_baseline_android_200).into(vh.imageViewprofilePicRequests);
                }else {
                    Glide.with(vh.itemView.getContext()).load(userUri.get(position)).into(vh.imageViewprofilePicRequests);
                }

                    //holder.textView.setOnClickListener(new View.OnClickListener() {
                    //    @Override
                    //    public void onClick(View view) {
                    //        Toast.makeText(context,"Clicked", Toast.LENGTH_LONG).show();
                    //    }
                    //});
                }
        }

        @Override
        public int getItemCount () {
            return title.size();
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
            TextView tvt;
            CardView cv;
            ConstraintLayout cl;
            String p;
            String textViewDuration;
            String textViewDurationTime;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewDuration="";
                textViewDurationTime="";
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
                cl = (ConstraintLayout) itemView.findViewById(R.id.constraintLayOutSmallCardRequests);
                tvt=itemView.findViewById(R.id.textViewTypeSmallCardRequest);

                cl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        {
                            Intent intent = new Intent(v.getContext(), ChatRequestInfo.class);
                            intent.putExtra("REQ_DURATION", textViewDuration);
                            intent.putExtra("REQ_DURATIONTIME", textViewDurationTime);
                            intent.putExtra("REQ_ID", textViewListIds.getText().toString());
                            intent.putExtra("THEIR_NAME", textViewCreatorName.getText().toString());
                            intent.putExtra("CREATOR_ID", textViewCreatorIds.getText().toString());
                            intent.putExtra("THEIR_URI", theirUri.getText().toString());
                            intent.putExtra("TITLE", textView.getText().toString());
                            intent.putExtra("PRICE", textView2.getText().toString());
                            intent.putExtra("STATUS", textViewStatus.getText().toString());
                            //intent.putExtra("GIG_CREATORID", textViewCreatorIds.getText().toString());

                            v.getContext().startActivity(intent);
                        }
                    }

                });

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


    public class ViewHolderCompleted extends RecyclerView.ViewHolder {
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
        TextView tvt;
        CardView cv;
        ConstraintLayout cl;
        String p;
        String textViewDuration;
        String textViewDurationTime;


        public ViewHolderCompleted(@NonNull View itemView) {
            super(itemView);
            textViewDuration="";
            textViewDurationTime="";
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
            cl = (ConstraintLayout) itemView.findViewById(R.id.constraintLayOutSmallCardRequests);
            tvt=itemView.findViewById(R.id.textViewTypeSmallCardRequest);

            cl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    {
                        Intent intent = new Intent(v.getContext(), ChatRequestInfo.class);
                        intent.putExtra("REQ_DURATION", textViewDuration);
                        intent.putExtra("REQ_DURATIONTIME", textViewDurationTime);
                        intent.putExtra("REQ_ID", textViewListIds.getText().toString());
                        intent.putExtra("THEIR_NAME", textViewCreatorName.getText().toString());
                        intent.putExtra("CREATOR_ID", textViewCreatorIds.getText().toString());
                        intent.putExtra("THEIR_URI", theirUri.getText().toString());
                        intent.putExtra("TITLE", textView.getText().toString());
                        intent.putExtra("PRICE", textView2.getText().toString());
                        intent.putExtra("STATUS", textViewStatus.getText().toString());
                        //intent.putExtra("GIG_CREATORID", textViewCreatorIds.getText().toString());

                        v.getContext().startActivity(intent);
                    }
                }

            });

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

    public class ViewHolderAwaitingPayment extends RecyclerView.ViewHolder {
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
        TextView tvt;
        CardView cv;
        ConstraintLayout cl;
        String p;
        String textViewDuration;
        String textViewDurationTime;

        public ViewHolderAwaitingPayment(@NonNull View itemView) {
            super(itemView);
            textViewDuration="";
            textViewDurationTime="";
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
            cl = (ConstraintLayout) itemView.findViewById(R.id.constraintLayOutSmallCardRequests);
            tvt=itemView.findViewById(R.id.textViewTypeSmallCardRequest);

            cl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    {
                        Intent intent = new Intent(v.getContext(), ChatRequestInfo.class);
                        intent.putExtra("REQ_DURATION", textViewDuration);
                        intent.putExtra("REQ_DURATIONTIME", textViewDurationTime);
                        intent.putExtra("REQ_ID", textViewListIds.getText().toString());
                        intent.putExtra("THEIR_NAME", textViewCreatorName.getText().toString());
                        intent.putExtra("CREATOR_ID", textViewCreatorIds.getText().toString());
                        intent.putExtra("THEIR_URI", theirUri.getText().toString());
                        intent.putExtra("TITLE", textView.getText().toString());
                        intent.putExtra("PRICE", textView2.getText().toString());
                        intent.putExtra("STATUS", textViewStatus.getText().toString());
                        //intent.putExtra("GIG_CREATORID", textViewCreatorIds.getText().toString());

                        v.getContext().startActivity(intent);
                    }
                }

            });

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





    public class ViewHolder1 extends RecyclerView.ViewHolder {
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
        TextView tvt;
        CardView cv;
        ConstraintLayout cl;
        Button b;
        String path;
        String textViewDuration;
        String textViewDurationTime;

        public ViewHolder1(@NonNull View itemView) {
            super(itemView);
            textViewDuration="";
            textViewDurationTime="";
            theirUri=itemView.findViewById(R.id.theirUriTextViewAccept);
            textView = itemView.findViewById(R.id.recyclerSmallCardTitleRequestsAccept);
            textView2 = itemView.findViewById(R.id.recyclerSmallCardPriceRequestsAccept);
            textViewListIds = itemView.findViewById(R.id.recyclerSmallCardGigIdRequestsAccept);
            textViewCreatorIds = itemView.findViewById(R.id.textViewSmallCardCreatorIdRequestsAccept);
            imageView = itemView.findViewById(R.id.recyclerSmallCardImageViewRequestsAccept);
            imageViewprofilePicRequests=itemView.findViewById(R.id.profilePicRequestsAccept);
            textViewStatus=itemView.findViewById(R.id.textView5Accept);
            textViewCreatorName=itemView.findViewById(R.id.textView4Accept);
            textView3Requests=itemView.findViewById(R.id.textView3RequestsAccept);
            cv = (CardView) itemView.findViewById(R.id.cardViewRecyclerSmallCardRequestsAccept);
            cl = (ConstraintLayout) itemView.findViewById(R.id.constraintLayOutSmallCardRequestsAccept);
            tvt=itemView.findViewById(R.id.textViewTypeSmallCardRequestAccept);
            b=itemView.findViewById(R.id.buttonSmallCardRequestAccept);


            cl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    {
                        Intent intent = new Intent(v.getContext(), ChatRequestInfo.class);
                        intent.putExtra("REQ_DURATION", textViewDuration);
                        intent.putExtra("REQ_DURATIONTIME", textViewDurationTime);
                        intent.putExtra("REQ_ID", textViewListIds.getText().toString());
                        intent.putExtra("THEIR_NAME", textViewCreatorName.getText().toString());
                        intent.putExtra("CREATOR_ID", textViewCreatorIds.getText().toString());
                        intent.putExtra("THEIR_URI", theirUri.getText().toString());
                        intent.putExtra("TITLE", textView.getText().toString());
                        intent.putExtra("PRICE", textView2.getText().toString());
                        intent.putExtra("STATUS", textViewStatus.getText().toString());
                        //intent.putExtra("GIG_CREATORID", textViewCreatorIds.getText().toString());

                        v.getContext().startActivity(intent);
                    }
                }

            });

            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        System.out.println("Accept Activity");
                    Intent intent = new Intent(v.getContext(), RequestAccept.class);
                    intent.putExtra("REQ_DURATION", textViewDuration);
                    intent.putExtra("REQ_DURATIONTIME", textViewDurationTime);
                    intent.putExtra("REQ_ID", textViewListIds.getText().toString());
                    intent.putExtra("REQ_TITLE",textView.getText());
                    intent.putExtra("REQ_PRICE",textView2.getText());
                    intent.putExtra("REQ_URI",path);
                    intent.putExtra("REQ_THEIRNAME",textViewCreatorName.getText().toString());

                    v.getContext().startActivity(intent);
                }
            });

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


    public class ViewHolderSendPayment extends RecyclerView.ViewHolder {
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
        TextView tvt;
        CardView cv;
        ConstraintLayout cl;
        Button b;
        String path;
        String textViewDuration;
        String textViewDurationTime;
        int x;
        public ViewHolderSendPayment(@NonNull View itemView) {
            super(itemView);
            textViewDuration="";
            textViewDurationTime="";
            theirUri=itemView.findViewById(R.id.theirUriTextViewP);
            textView = itemView.findViewById(R.id.recyclerSmallCardTitleRequestsP);
            textView2 = itemView.findViewById(R.id.recyclerSmallCardPriceRequestsP);
            textViewListIds = itemView.findViewById(R.id.recyclerSmallCardGigIdRequestsP);
            textViewCreatorIds = itemView.findViewById(R.id.textViewSmallCardCreatorIdRequestsP);
            imageView = itemView.findViewById(R.id.recyclerSmallCardImageViewRequestsP);
            imageViewprofilePicRequests=itemView.findViewById(R.id.profilePicRequestsP);
            textViewStatus=itemView.findViewById(R.id.textView5P);
            textViewCreatorName=itemView.findViewById(R.id.textView4P);
            textView3Requests=itemView.findViewById(R.id.textView3RequestsP);
            cv = (CardView) itemView.findViewById(R.id.cardViewRecyclerSmallCardRequestsP);
            cl = (ConstraintLayout) itemView.findViewById(R.id.constraintLayOutSmallCardRequestsP);
            tvt=itemView.findViewById(R.id.textViewTypeSmallCardRequestP);
            b=itemView.findViewById(R.id.buttonSmallCardRequestP);
            x=0;
            cl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    {
                        Intent intent = new Intent(v.getContext(), ChatRequestInfo.class);
                        intent.putExtra("REQ_DURATION", textViewDuration);
                        intent.putExtra("REQ_DURATIONTIME", textViewDurationTime);
                        intent.putExtra("REQ_ID", textViewListIds.getText().toString());
                        intent.putExtra("THEIR_NAME", textViewCreatorName.getText().toString());
                        intent.putExtra("CREATOR_ID", textViewCreatorIds.getText().toString());
                        intent.putExtra("THEIR_URI", theirUri.getText().toString());
                        intent.putExtra("TITLE", textView.getText().toString());
                        intent.putExtra("PRICE", textView2.getText().toString());
                        intent.putExtra("STATUS", textViewStatus.getText().toString());
                        //intent.putExtra("GIG_CREATORID", textViewCreatorIds.getText().toString());

                        v.getContext().startActivity(intent);
                    }
                }

            });

            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("Send Payment Activity");
                    Intent intent = new Intent(v.getContext(), RequestPay.class);
                    intent.putExtra("REQ_DURATION", textViewDuration);
                    intent.putExtra("REQ_DURATIONTIME", textViewDurationTime);
                    intent.putExtra("REQ_ID", textViewListIds.getText().toString());
                    intent.putExtra("REQ_TITLE",textView.getText());
                    intent.putExtra("REQ_PRICE",textView2.getText());
                    intent.putExtra("REQ_URI",path);
                    intent.putExtra("REQ_THEIRNAME",textViewCreatorName.getText().toString());

                    v.getContext().startActivity(intent);
                }
            });

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

