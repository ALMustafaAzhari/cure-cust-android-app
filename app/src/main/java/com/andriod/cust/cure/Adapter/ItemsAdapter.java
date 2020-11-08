package com.andriod.cust.cure.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.andriod.cust.cure.ItemsActivity;
import com.andriod.cust.cure.R;
import com.andriod.cust.cure.SendRequestActivity;
import com.andriod.cust.cure.bean.Item;
import com.andriod.cust.cure.service.DatabaseService;

import java.util.ArrayList;
import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.MyViewHolder> implements Filterable {

    private List<Item> items;
    private Context context;
    private DatabaseService databaseService ;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView brandName, genericName , companyName ;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            brandName = itemView.findViewById(R.id.brand_name);
            genericName = itemView.findViewById(R.id.generic_name);
            companyName = itemView.findViewById(R.id.company_name);
        }
    }

    public ItemsAdapter(List<Item> items ,Context context) {
        this.items = items ;
        this.context = context;
        this.databaseService = new DatabaseService(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.brandName.setText(items.get(position).getBrandName());
        holder.genericName.setText(items.get(position).getGenericName());
        holder.companyName.setText(items.get(position).getCompany().getName()+
                ", "+
                context.getString(context.getResources().getIdentifier("COUNTRY_"+items.get(position).getCompany().getCountryCode(), "string", context.getPackageName())));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context , SendRequestActivity.class) ;
                intent.putExtra(ItemsActivity.EXTRA_ITEM_ID , items.get(position).getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                filterResults.values = databaseService.itemsSearch(constraint.toString());
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                items = (ArrayList<Item>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
