package com.andriod.cust.cure.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.andriod.cust.cure.ItemsActivity;
import com.andriod.cust.cure.R;
import com.andriod.cust.cure.RequestResponsesActivity;
import com.andriod.cust.cure.SendRequestActivity;
import com.andriod.cust.cure.bean.PharmacyResponse;
import com.andriod.cust.cure.bean.Request;

import java.util.List;

public class RequestsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Object> requests;
    private Context context;

    public RequestsAdapter(List<Object> requests ,Context context) {
        this.requests = requests ;
        this.context = context;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView brandName, genericName , companyName ;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            brandName = itemView.findViewById(R.id.brand_name);
            genericName = itemView.findViewById(R.id.generic_name);
            companyName = itemView.findViewById(R.id.company_name);
        }
    }

    public class DateHeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView dateHeader ;

        public DateHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            dateHeader = itemView.findViewById(R.id.date_header);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(requests.get(position) instanceof Request) {
            return 0;
        }
        else {
            return 1 ;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0 ) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.requests_item_view, parent, false);
            return new RequestsAdapter.ItemViewHolder(itemView);
        }

        else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_item_view, parent, false);
            return new RequestsAdapter.DateHeaderViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == 0 ) {
            ItemViewHolder viewHolder = (ItemViewHolder) holder;
            Request request = (Request) requests.get(position);
            viewHolder.brandName.setText(request.getItem().getBrandName());
            viewHolder.genericName.setText(request.getItem().getGenericName());
            viewHolder.companyName.setText(request.getItem().getCompany().getName()+
                    ", "+
                    context.getString(context.getResources().getIdentifier("COUNTRY_"+request.getItem().getCompany().getCountryCode(), "string", context.getPackageName())));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context , RequestResponsesActivity.class) ;
                    intent.putExtra("ITEM_NAME" , request.getItem().getBrandName());
                    intent.putExtra("REQUEST_ID", request.getId()) ;
                    intent.putExtra("REQUEST_TIME_AGO", DateUtils.getRelativeTimeSpanString(request.getEntryDate().getTime())) ;
                    context.startActivity(intent);
                }
            });
        }

        else {
            DateHeaderViewHolder viewHolder = (DateHeaderViewHolder) holder;
            String dateHeader = (String) requests.get(position);
            viewHolder.dateHeader.setText(dateHeader);
        }
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }
}
