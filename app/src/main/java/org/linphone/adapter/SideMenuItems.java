package org.linphone.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nk.ntalk.R;

import org.linphone.callbacks.MenuClick;

import java.util.ArrayList;

/**
 * Created by sanniAdewale on 13/01/2018.
 */

public class SideMenuItems extends RecyclerView.Adapter<SideMenuItems.SideMenuHolder> {

    Context context;
    LayoutInflater inflater;
    ArrayList<String> stringArrayList = new ArrayList<>();
    MenuClick menuClick;

    public SideMenuItems(Context context, MenuClick menuClick) {
        this.context = context;
        this.menuClick = menuClick;
        inflater = LayoutInflater.from(context);
    }

    public void UpdateCycle(ArrayList<String> stringArrayList) {
        this.stringArrayList = stringArrayList;
        notifyDataSetChanged();
    }

    @Override
    public SideMenuHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.single_row, parent, false);
        return new SideMenuHolder(view);
    }

    @Override
    public void onBindViewHolder(SideMenuHolder holder, int position) {
        String titles = stringArrayList.get(position);
        holder.title.setText(titles);
        holder.imageView.setImageDrawable(context.getResources().getDrawable(setDrawable(titles)));
    }

    @Override
    public int getItemCount() {
        return stringArrayList.size();
    }

    private int setDrawable(String item) {
        int drawable = 0;
        if (item.equals(context.getString(R.string.menu_settings))) {
            drawable = R.drawable.ic_settings_black_24dp;
        }
        if (item.equals(context.getString(R.string.menu_packages))) {
            drawable = R.drawable.ic_shopping_basket_black_24dp;
        }
        if (item.equals(context.getString(R.string.menu_assistant))) {
            drawable = R.drawable.ic_vpn_key_black_24dp;
        }
        if (item.equals(context.getString(R.string.buy_credit))) {
            drawable = R.drawable.ic_monetization_on_black_24dp;
        }
        if (item.equals(context.getString(R.string.menu_rates))) {
            drawable = R.drawable.ic_data_usage_black_24dp;
        }
        if (item.equals(context.getString(R.string.menu_account))) {
            drawable = R.drawable.ic_account_circle_black_24dp;
        }
        if (item.equals(context.getString(R.string.menu_balance))) {
            drawable = R.drawable.ic_account_balance_wallet_black_24dp;
        }
        if (item.equals(context.getString(R.string.menu_customers))) {
            drawable = R.drawable.ic_people_black_24dp;
        }
        if (item.equals(context.getString(R.string.menu_enterprise))) {
            drawable = R.drawable.ic_business_center_black_24dp;
        }
        return drawable;
    }

    class SideMenuHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView imageView;
        RelativeLayout relativeLayout;

        SideMenuHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.item_name);
            imageView = (ImageView) itemView.findViewById(R.id.item_icon);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.rootMenu);
            relativeLayout.setOnClickListener(new View.OnClickListener() {
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
