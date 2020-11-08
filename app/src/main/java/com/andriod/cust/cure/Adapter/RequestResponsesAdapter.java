package com.andriod.cust.cure.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.andriod.cust.cure.R;
import com.andriod.cust.cure.bean.PharmacyResponse;

import java.util.List;

public class RequestResponsesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Object> responses;
    private Context context;

    private static enum DISTRICTS {
        KAR, NKR, NOR, KAS, BNI, NDA, SDA, SKR, GEZ, WNI, RNI, RSE, ALQ, SEN, WDA, CDA, EDA, WKE
    }


    public RequestResponsesAdapter(List<Object> responses ,Context context) {
        this.responses = responses ;
        this.context = context;
    }

    public class ResponseViewHolder extends RecyclerView.ViewHolder {
        public TextView pharmName, pharmPhone , pharmAddr ;
        public ImageView expandImageView ;
        public LinearLayout detailsLinearLayout ;

        public ResponseViewHolder(@NonNull View itemView) {
            super(itemView);
            pharmName = itemView.findViewById(R.id.pharm_name);
            pharmPhone = itemView.findViewById(R.id.pharm_phone);
            pharmAddr = itemView.findViewById(R.id.pharm_address);
            expandImageView = itemView.findViewById(R.id.expand_imageView);
            detailsLinearLayout = itemView.findViewById(R.id.response_details_linearLayout);
        }
    }

    public class DistrictViewHolder extends RecyclerView.ViewHolder {
        public TextView districtName ;

        public DistrictViewHolder(@NonNull View itemView) {
            super(itemView);
            districtName = itemView.findViewById(R.id.district_name);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(responses.get(position) instanceof PharmacyResponse) {
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
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.responses_item_view, parent, false);
            return new ResponseViewHolder(itemView);
        }

        else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.district_item_view, parent, false);
            return new DistrictViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == 0 ) {
            ResponseViewHolder viewHolder = (ResponseViewHolder) holder ;
            PharmacyResponse response = (PharmacyResponse) responses.get(position) ;
            viewHolder.pharmName.setText(response.getPharmacy().getName());
            viewHolder.pharmPhone.setText(response.getPharmacy().getPhone());
            viewHolder.pharmAddr.setText(response.getPharmacy().getAddress());
            viewHolder.expandImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (response.getPharmacy().IsExpanded()) {
                        viewHolder.expandImageView.setImageResource(R.drawable.ic_up_arrow);
                        ((PharmacyResponse) responses.get(position)).getPharmacy().setExpand(false);
                        viewHolder.detailsLinearLayout.setVisibility(View.GONE);

                    }

                    else {
                        viewHolder.expandImageView.setImageResource(R.drawable.ic_down_arrow);
                        ((PharmacyResponse) responses.get(position)).getPharmacy().setExpand(true);
                        viewHolder.detailsLinearLayout.setVisibility(View.VISIBLE);
                    }
                    notifyDataSetChanged();
                }
            });
        }

        else {
            DistrictViewHolder viewHolder = (DistrictViewHolder) holder ;
            viewHolder.districtName.setText(context.getResources().getIdentifier("DISTRICT_"+String.valueOf(responses.get(position)), "string", context.getPackageName()));
        }
    }

    @Override
    public int getItemCount() {
        return responses.size();
    }
}
