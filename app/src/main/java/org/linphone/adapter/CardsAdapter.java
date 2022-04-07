package org.linphone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.nk.ntalk.R;

import org.linphone.callbacks.MenuClick;
import org.linphone.models.CustomerLog;
import org.linphone.models.NativeTalkCard;

import java.util.ArrayList;

public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.CardsAdapterHolder> {

    Context context;
    LayoutInflater inflater;
    ArrayList<NativeTalkCard> stringArrayList = new ArrayList<>();
    MenuClick menuClick;

    public CardsAdapter(Context context, MenuClick menuClick) {
        this.context = context;
        this.menuClick = menuClick;
        inflater = LayoutInflater.from(context);
    }

    public void UpdateCycle(ArrayList<NativeTalkCard> stringArrayList) {
        this.stringArrayList = stringArrayList;
        notifyDataSetChanged();
    }

    @Override
    public CardsAdapter.CardsAdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_cards_layout, parent, false);
        return new CardsAdapter.CardsAdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(CardsAdapter.CardsAdapterHolder holder, int position) {
        NativeTalkCard card = stringArrayList.get(position);
        if(card.getBrand().toLowerCase().contentEquals("mastercard")) {
            holder.layout.setBackgroundResource(R.drawable.mastercard_blue);
        }else{
            holder.layout.setBackgroundResource(R.drawable.card_green);
        }
        holder.brand.setText(card.getBrand());
        holder.expiry.setText(card.getExp_month() + "/" + card.getExp_year());
        holder.lastFour.setText(card.getLast_four());
    }

    @Override
    public int getItemCount() {
        return stringArrayList.size();
    }

    class CardsAdapterHolder extends RecyclerView.ViewHolder {

        LinearLayout layout;
        TextView brand, expiry, lastFour;

        CardsAdapterHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.card_layout);
            brand = itemView.findViewById(R.id.card_brand);
            expiry = itemView.findViewById(R.id.card_exp);
            lastFour = itemView.findViewById(R.id.card_last_four);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (menuClick != null) {
                        menuClick.onClick(view, getPosition());
                    }
                }
            });

            layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (menuClick != null) {
                        menuClick.onLongPress(view, getPosition());
                    }
                    return true;
                }
            });
        }
    }
}
