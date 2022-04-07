package org.linphone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.nk.ntalk.R;

import org.linphone.callbacks.MenuClick;
import org.linphone.models.Customer;
import org.linphone.models.CustomerLog;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class CustomerLogAdapter extends RecyclerView.Adapter<CustomerLogAdapter.CustomerAdapterHolder>{

    Context context;
    LayoutInflater inflater;
    ArrayList<CustomerLog> stringArrayList = new ArrayList<>();
    MenuClick menuClick;

    public CustomerLogAdapter(Context context, MenuClick menuClick) {
        this.context = context;
        this.menuClick = menuClick;
        inflater = LayoutInflater.from(context);
    }

    public void UpdateCycle(ArrayList<CustomerLog> stringArrayList) {
        this.stringArrayList = stringArrayList;
        notifyDataSetChanged();
    }

    @Override
    public CustomerLogAdapter.CustomerAdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_customer_logs, parent, false);
        return new CustomerLogAdapter.CustomerAdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomerLogAdapter.CustomerAdapterHolder holder, int position) {
        CustomerLog customer = stringArrayList.get(position);
        holder.date.setText(customer.getCreated_date());
        holder.log.setText(customer.getText());
    }

    @Override
    public int getItemCount() {
        return stringArrayList.size();
    }

    class CustomerAdapterHolder extends RecyclerView.ViewHolder {

        TextView date, log;

        CustomerAdapterHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.cust_date);
            log = itemView.findViewById(R.id.cust_log);
        }
    }
}
