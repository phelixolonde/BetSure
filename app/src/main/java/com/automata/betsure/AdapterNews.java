package com.automata.betsure;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.AdView;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeBannerAd;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdapterNews extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private static final String TAG ="FACEBOOK ADS" ;
    private Context context;
    private LayoutInflater inflater;
    List<Model> contactList;
    private List<Model> contactListFiltered;
    private int AD_TYPE=1;
    private int CONTENT_TYPE=2;

    public AdapterNews(Context context, List<Model> contactList) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.contactList = contactList;
        this.contactListFiltered=contactList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.tip_row, parent, false);

        return new MyHolder(view);
    }



    // Bind contactList
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyHolder myHolder = (MyHolder) holder;
        final Model current = contactListFiltered.get(position);

        myHolder.tvCountry.setText(current.getCountry() + " - " + current.getLeague());
        myHolder.tvHome.setText(current.getHome());
        myHolder.tvAway.setText(current.getAway());
        myHolder.tvTime.setText(current.getTime());
        myHolder.tvTip.setText("Tip : " + current.getTip());
        if (!current.getOdd().equalsIgnoreCase("null")) {
            myHolder.tvOdd.setText("Odds : " + new DecimalFormat("#.##").format(Double.parseDouble(current.getOdd())));
        }

        try {

            if (current.getResult()==null || current.getResult().equals("")|| current.getResult().equalsIgnoreCase("postp")){
                Glide.with(context).load(R.drawable.blank).into(myHolder.imgResult);
            }
            else if (getResult(current.getResult(), current.getTip()).equals("won")) {
                Glide.with(context).load(R.drawable.won).into(myHolder.imgResult);
            } else if (getResult(current.getResult(), current.getTip()).equals("lost")) {
                Glide.with(context).load(R.drawable.lost).into(myHolder.imgResult);
            }else {
                Glide.with(context).load(R.drawable.blank).into(myHolder.imgResult);

            }
        } catch (Exception e) {
            Log.e("RESULT ", e.getMessage(), e);
        }



       /* myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, News_Detailed.class);
                intent.putExtra("url", current.url);
                context.startActivity(intent);
            }
        });*/


    }

    private String getResult(String result, String tip) {

        String res = "";
        int a, b;
        String[] contactList = result.split("-");
        a = Integer.parseInt(contactList[0].trim());
        b = Integer.parseInt(contactList[1].trim());

        switch (tip) {
            case "1":
                if (a > b) {
                    res = "won";
                } else {
                    res = "lost";
                }
                break;
            case "2":
                if (b > a) {
                    res = "won";
                } else {
                    res = "lost";
                }
                break;
            case "X":
            case "x":
                if (a == b) {
                    res = "won";
                } else {
                    res = "lost";
                }
                break;
            case "1X":
            case "1x":
                if (a > b || a == b) {
                    res = "won";
                } else {
                    res = "lost";
                }
                break;
            case "12":
                if (a > b || a < b) {
                    res = "won";
                } else {
                    res = "lost";
                }
                break;
            case "X2":
            case "x2":
            case "2X":
            case "2x":
                if (a < b || a == b) {
                    res = "won";
                } else {
                    res = "lost";
                }
        }

        return res;
    }

    // return total item from List

    @Override
    public int getItemCount() {
        return contactListFiltered.size();
    }
    @Override
    public int getItemViewType(int position)
    {
        if (position % 5 == 0)

            return AD_TYPE;
        return CONTENT_TYPE;
    }
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    contactListFiltered = contactList;
                } else {
                    List<Model> filteredList = new ArrayList<>();
                    for (Model model : contactList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (model.country.toLowerCase().contains(charString.toLowerCase()) || model.league.toLowerCase().contains(charString.toLowerCase()) || model.home.toLowerCase().contains(charString.toLowerCase()) || model.away.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(model);
                        }
                    }

                    contactListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = contactListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                contactListFiltered = (ArrayList<Model>) filterResults.values;
                notifyDataSetChanged();
            }
        };

    }


    public class MyHolder extends RecyclerView.ViewHolder {

        TextView tvCountry, tvHome, tvAway, tvTip, tvOdd, tvTime;
        ImageView imgResult;

        public MyHolder(View itemView) {
            super(itemView);
            tvCountry = itemView.findViewById(R.id.tvCompetition);
            tvHome = itemView.findViewById(R.id.tvHome);
            tvAway = itemView.findViewById(R.id.tvAway);
            tvTip = itemView.findViewById(R.id.tvTip);
            tvOdd = itemView.findViewById(R.id.tvOdds);
            tvTime = itemView.findViewById(R.id.tvTime);
            imgResult = itemView.findViewById(R.id.imgResult);

        }

    }

}