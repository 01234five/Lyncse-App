package com.lyncseapp.lyncse.adapters;

        import android.content.Context;
        import android.text.format.DateUtils;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.LinearLayout;
        import android.widget.TextView;

        import androidx.annotation.NonNull;
        import androidx.recyclerview.widget.RecyclerView;

        import com.bumptech.glide.Glide;
        import com.lyncseapp.lyncse.R;

        import java.time.Instant;
        import java.util.List;

public class RecyclerAdapterChatRequestMyMessage extends RecyclerView.Adapter{
    List<String> message;
    List<Long> time;
    List<String> profilePic;
    List<String> name;
    List<Integer> type;
    Context context;


    public RecyclerAdapterChatRequestMyMessage(Context context,  List<String> message, List<Long> time,List<String> profilePic,List<String> name,List<Integer> type) {
        this.message=message;
        this.time=time;
        this.name=name;
        this.context = context;
        this.profilePic=profilePic;
        this.type=type;

    }

    @Override
    public int getItemViewType(int position) {
        if(type.get(position)==0){
            return 0;
        }
        return 1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == 0) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.from(parent.getContext()).inflate(R.layout.recycler_chat_request_my_message, parent, false);
            RecyclerAdapterChatRequestMyMessage.ViewHolder1 viewHolder1 = new RecyclerAdapterChatRequestMyMessage.ViewHolder1(view);
            return viewHolder1;
        }else {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.from(parent.getContext()).inflate(R.layout.recycler_chat_request_their_message, parent, false);
            RecyclerAdapterChatRequestMyMessage.ViewHolder2 viewHolder2 = new RecyclerAdapterChatRequestMyMessage.ViewHolder2(view);
            return viewHolder2;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {



        if(type.get(position)==0) {
            final ViewHolder1 vh = (ViewHolder1) holder;
            vh.ivProfilePic.setImageDrawable(null);
            if (profilePic.get(position).equals("Default")) {
                Glide.with(vh.itemView.getContext()).load(R.drawable.ic_baseline_android_200).into(vh.ivProfilePic);
            }else {
                Glide.with(vh.itemView.getContext()).load(profilePic.get(position)).into(vh.ivProfilePic);
            }
            vh.textViewName.setText(name.get(position));
            vh.textViewMessage.setText(message.get(position));
            vh.textViewTime.setText(time.get(position).toString());
        }else {
            final ViewHolder2 vh2 = (ViewHolder2)holder;
            vh2.ivProfilePic2.setImageDrawable(null);
            if (profilePic.get(position).equals("Default")) {
                Glide.with(vh2.itemView.getContext()).load(R.drawable.ic_baseline_android_200).into(vh2.ivProfilePic2);
            }else {
                Glide.with(vh2.itemView.getContext()).load(profilePic.get(position)).into(vh2.ivProfilePic2);
            }
            vh2.textViewName2.setText(name.get(position));
            vh2.textViewMessage2.setText(message.get(position));
            vh2.textViewTime2.setText(time.get(position).toString());
        }

    }

    @Override
    public int getItemCount() {
        return message.size();

    }
    public class ViewHolder1 extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewMessage;
        TextView textViewTime;
        TextView textViewTimeAgo;
        ImageView ivProfilePic;
        LinearLayout ll;
        boolean x;

        public ViewHolder1(@NonNull View itemView) {
            super(itemView);
            textViewTime= itemView.findViewById(R.id.myMessageTimeTextView8);
            textViewName = itemView.findViewById(R.id.myMessageNameTextView7);
            ivProfilePic = itemView.findViewById(R.id.myMessageImageView4);
            textViewMessage= itemView.findViewById(R.id.myMessageTextView6);
            textViewTimeAgo= itemView.findViewById(R.id.myMessageTimeAgo);
            ll= (LinearLayout)itemView.findViewById(R.id.linearLayoutMyMessageTime);
            x=false;
            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(x==false) {
                        x=true;

                        long timeDifference = (Instant.now().toEpochMilli() - Long.parseLong(textViewTime.getText().toString())) / 1000;

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

                        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(Long.parseLong(textViewTime.getText().toString()), Instant.now().toEpochMilli(),
                                timeUnit, DateUtils.FORMAT_ABBREV_RELATIVE);



                        System.out.println(timeAgo.toString());
                        textViewTimeAgo.setText(timeAgo.toString());

                        textViewMessage.setVisibility(view.GONE);
                        textViewTimeAgo.setVisibility(view.VISIBLE);

                    }else{
                        System.out.println("Clicked again");
                        x=false;
                        textViewMessage.setVisibility(view.VISIBLE);
                        textViewTimeAgo.setVisibility(view.GONE);
                    }
                }
            });


        }
    }



    public class ViewHolder2 extends RecyclerView.ViewHolder {
        TextView textViewName2;
        TextView textViewMessage2;
        TextView textViewTime2;
        ImageView ivProfilePic2;
        TextView textViewTimeAgo2;
        LinearLayout ll2;
        boolean x2;


        public ViewHolder2(@NonNull View itemView) {
            super(itemView);
            textViewTime2= itemView.findViewById(R.id.theirMessageTimeTextView8);
            textViewName2 = itemView.findViewById(R.id.theirNameTextView7);
            ivProfilePic2 = itemView.findViewById(R.id.theirMessageImageView4);
            textViewMessage2= itemView.findViewById(R.id.theirMessageTextView6);
            textViewTimeAgo2= itemView.findViewById(R.id.theirMessageTimeAgo);
            ll2= (LinearLayout)itemView.findViewById(R.id.linearLayoutTheirMessageTime);
            x2=false;


            ll2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(x2==false) {
                        x2=true;

                        long timeDifference = (Instant.now().toEpochMilli() - Long.parseLong(textViewTime2.getText().toString())) / 1000;

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

                        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(Long.parseLong(textViewTime2.getText().toString()), Instant.now().toEpochMilli(),
                                timeUnit, DateUtils.FORMAT_ABBREV_RELATIVE);



                        System.out.println(timeAgo.toString());
                        textViewTimeAgo2.setText(timeAgo.toString());

                        textViewMessage2.setVisibility(view.GONE);
                        textViewTimeAgo2.setVisibility(view.VISIBLE);

                    }else{
                        System.out.println("Clicked again");
                        x2=false;
                        textViewMessage2.setVisibility(view.VISIBLE);
                        textViewTimeAgo2.setVisibility(view.GONE);
                    }
                }
            });

        }
    }

}

