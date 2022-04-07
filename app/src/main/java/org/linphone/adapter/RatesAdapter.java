package org.linphone.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nk.ntalk.R;

import java.util.ArrayList;

public class RatesAdapter extends RecyclerView.Adapter<RatesAdapter.PlansAdapterHolder> {

    Context context;
    LayoutInflater inflater;
    ArrayList<String> stringArrayList = new ArrayList<>();
    ArrayList<String> desc = new ArrayList<>();
    String countryName, countryCurrency;
    Drawable flag;


    public RatesAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void UpdateCycle(ArrayList<String> stringArrayList, ArrayList<String> desc, String countryName, Drawable flag, String countryCurrency) {
        this.stringArrayList = stringArrayList;
        this.desc = desc;
        this.countryName = countryName;
        this.flag = flag;
        this.countryCurrency = countryCurrency;
        notifyDataSetChanged();
    }

    @Override
    public RatesAdapter.PlansAdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_rates, parent, false);
        return new RatesAdapter.PlansAdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(RatesAdapter.PlansAdapterHolder holder, int position) {
        String rt = stringArrayList.get(position);
        String d = desc.get(position);
        holder.td.setText(d);
        holder.rateValue.setText(countryCurrency + "" + rt + "/min");
        holder.country.setText(countryName);
        holder.flag.setImageDrawable(flag);
    }

    @Override
    public int getItemCount() {
        return stringArrayList.size();
    }

    class PlansAdapterHolder extends RecyclerView.ViewHolder {

        TextView td, rateValue, country;
        ImageView flag;

        PlansAdapterHolder(View itemView) {
            super(itemView);
            td = itemView.findViewById(R.id.titleDesc);
            rateValue = itemView.findViewById(R.id.rateV);
            country = itemView.findViewById(R.id.countryName);
            flag = itemView.findViewById(R.id.ivFlag);
        }
    }
}
