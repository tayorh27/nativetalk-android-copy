package org.linphone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
//import androidx.recyclerview.widget.RecyclerView;

import com.nk.ntalk.R;

import org.linphone.callbacks.MenuClick;
import org.linphone.models.Plans;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class PlansAdapter extends RecyclerView.Adapter<PlansAdapter.PlansAdapterHolder> {


    Context context;
    LayoutInflater inflater;
    ArrayList<Plans> stringArrayList = new ArrayList<>();
    MenuClick menuClick;

    public PlansAdapter(Context context, MenuClick menuClick) {
        this.context = context;
        this.menuClick = menuClick;
        inflater = LayoutInflater.from(context);
    }

    public void UpdateCycle(ArrayList<Plans> stringArrayList) {
        this.stringArrayList = stringArrayList;
        notifyDataSetChanged();
    }

    @Override
    public PlansAdapter.PlansAdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_plans, parent, false);
        return new PlansAdapter.PlansAdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(PlansAdapter.PlansAdapterHolder holder, int position) {
        Plans plans = stringArrayList.get(position);
        holder.title.setText(plans.getName());
        //holder.tvPeriod.setText("Period: " + plans.getPeriod());
        //holder.tvPeriodCost.setText("Period cost: " + plans.getPeriodCost());
    }

    @Override
    public int getItemCount() {
        return stringArrayList.size();
    }

    class PlansAdapterHolder extends RecyclerView.ViewHolder {

        TextView title, tvPeriod, tvPeriodCost;
        Button buy;

        PlansAdapterHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.name);
            //tvPeriod = itemView.findViewById(R.id.period);
            //tvPeriodCost = itemView.findViewById(R.id.periodCost);
            buy = itemView.findViewById(R.id.btnBuy);
            buy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (menuClick != null) {
                        menuClick.onClick(view, getPosition());
                    }
                }
            });
        }
    }
}
