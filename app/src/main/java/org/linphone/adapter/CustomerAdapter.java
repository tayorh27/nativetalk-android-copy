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

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerAdapterHolder>{

    Context context;
    LayoutInflater inflater;
    ArrayList<Customer> stringArrayList = new ArrayList<>();
    MenuClick menuClick;

    public CustomerAdapter(Context context, MenuClick menuClick) {
        this.context = context;
        this.menuClick = menuClick;
        inflater = LayoutInflater.from(context);
    }

    public void UpdateCycle(ArrayList<Customer> stringArrayList) {
        this.stringArrayList = stringArrayList;
        notifyDataSetChanged();
    }

    @Override
    public CustomerAdapter.CustomerAdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custome_customer, parent, false);
        return new CustomerAdapter.CustomerAdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomerAdapter.CustomerAdapterHolder holder, int position) {
        Customer customer = stringArrayList.get(position);
        holder.name.setText(customer.getName());
        holder.email.setText(customer.getEmail());
        holder.number.setText(customer.getNumber());
        holder.address.setText(customer.getAddress());
    }

    @Override
    public int getItemCount() {
        return stringArrayList.size();
    }

    class CustomerAdapterHolder extends RecyclerView.ViewHolder {

        TextView name, email, number, address;
        Button buy;

        CustomerAdapterHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.cust_name);
            email = itemView.findViewById(R.id.cust_email);
            number = itemView.findViewById(R.id.cust_number);
            address = itemView.findViewById(R.id.cust_address);
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
