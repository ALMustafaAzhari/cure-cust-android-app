package com.andriod.cust.cure;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.andriod.cust.cure.Adapter.RequestResponsesAdapter;
import com.andriod.cust.cure.bean.AddRequestResponse;
import com.andriod.cust.cure.bean.Pharmacy;
import com.andriod.cust.cure.bean.PharmacyResponse;
import com.andriod.cust.cure.service.CustomerService;
import com.andriod.cust.cure.util.RetrofitClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestResponsesActivity extends AppCompatActivity {

    private static final String TAG = "RequestResponseActivity" ;
    private CustomerService service ;
    private Timer getResponsestimer ;
    private Call<List<PharmacyResponse>> call ;
    private List<Object> responses ;
    RecyclerView responsesRecyclerView;
    private RequestResponsesAdapter mAdapter;
    private TextView emptyListTextView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_responses);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String itemName = intent.getStringExtra("ITEM_NAME");
        Long requestId = intent.getLongExtra("REQUEST_ID" , 0);
        String requestTimeAgo = intent.getStringExtra("REQUEST_TIME_AGO");
        getSupportActionBar().setSubtitle(itemName+", "+requestTimeAgo);

        responses = new ArrayList<>();
        responsesRecyclerView = findViewById(R.id.responses_recyclerView);
        emptyListTextView = findViewById(R.id.responses_empty_list);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        responsesRecyclerView.setLayoutManager(mLayoutManager);
        responsesRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new RequestResponsesAdapter(responses , this);
        responsesRecyclerView.setAdapter(mAdapter);


        SharedPreferences prefs = getSharedPreferences("USER_DETAILS" , MODE_PRIVATE);
        service = RetrofitClient.getRetrofitInstance().create(CustomerService.class) ;
        String authorization = "Bearer " + prefs.getString("token", "") ;

        if (intent.getBooleanExtra("ITEM_REF" , false)) {
            final Handler handler = new Handler();
            TimerTask timertask = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        public void run() {
                            Log.i(TAG , "Handler started");
                            call = service.getResponses(authorization , requestId) ;
                            call.enqueue(new Callback<List<PharmacyResponse>>() {
                                @Override
                                public void onResponse(Call<List<PharmacyResponse>> call, Response<List<PharmacyResponse>> response) {
                                    if (response.isSuccessful()) {
                                        refreshResponses(response.body());
                                    }
                                }
                                @Override
                                public void onFailure(Call<List<PharmacyResponse>> call, Throwable t) {
                                    Log.e(TAG , "Get request responses failure.");
                                    t.printStackTrace();
                                }
                            });
                        }
                    });
                }
            };
            getResponsestimer = new Timer();
            getResponsestimer.schedule(timertask, 0, 10000);

        }

        else {
            call = service.getResponses(authorization , requestId) ;
            call.enqueue(new Callback<List<PharmacyResponse>>() {
                @Override
                public void onResponse(Call<List<PharmacyResponse>> call, Response<List<PharmacyResponse>> response) {
                    if (response.isSuccessful()) {
                        refreshResponses(response.body());
                    }
                }
                @Override
                public void onFailure(Call<List<PharmacyResponse>> call, Throwable t) {
                    Log.e(TAG , "Get request responses failure.");
                    t.printStackTrace();
                }
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (getResponsestimer != null) {
            getResponsestimer.cancel();
            getResponsestimer.purge();
        }
    }

    private void refreshResponses(List<PharmacyResponse>  pharmacyResponses) {

        if (pharmacyResponses.isEmpty()) {
            responsesRecyclerView.setVisibility(View.GONE);
            emptyListTextView.setVisibility(View.VISIBLE);
        }
        else {
            responses.clear();
            responses.add(pharmacyResponses.get(0).getPharmacy().getDistrict());
            PharmacyResponse pharmacyResponse = null ;
            for (int i = 0 ; i < pharmacyResponses.size() ; i++) {
                pharmacyResponse = pharmacyResponses.get(i) ;
                responses.add(pharmacyResponse);

                if ((i+1) < pharmacyResponses.size()) {
                    if (!pharmacyResponse.getPharmacy().getDistrict().equals(pharmacyResponses.get(i+1).getPharmacy().getDistrict())) {
                        responses.add(pharmacyResponses.get(i+1).getPharmacy().getDistrict());
                    }
                }
            }
            responsesRecyclerView.setVisibility(View.VISIBLE);
            emptyListTextView.setVisibility(View.GONE);
            mAdapter.notifyDataSetChanged();
        }

    }
}
