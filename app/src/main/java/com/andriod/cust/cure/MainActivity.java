package com.andriod.cust.cure;

import android.content.Intent;
import android.os.Bundle;

import com.andriod.cust.cure.Adapter.RequestResponsesAdapter;
import com.andriod.cust.cure.Adapter.RequestsAdapter;
import com.andriod.cust.cure.bean.PharmacyResponse;
import com.andriod.cust.cure.bean.Request;
import com.andriod.cust.cure.service.CustomerService;
import com.andriod.cust.cure.service.DatabaseService;
import com.andriod.cust.cure.util.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import retrofit2.Call;

public class MainActivity extends AppCompatActivity {

    private  DatabaseService databaseService;
    private CustomerService service ;
    private List<Object> requests ;
    RecyclerView requestsRecyclerView;
    private RequestsAdapter mAdapter;
    private TextView emptyListTextView ;
    private final static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd/MM/yyyy");
    private final static String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils.refreshToken(this);

        databaseService = new DatabaseService(this);

        requests = new ArrayList<>();
        requestsRecyclerView = findViewById(R.id.requests_recyclerView);
        emptyListTextView = findViewById(R.id.requests_empty_list);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        requestsRecyclerView.setLayoutManager(mLayoutManager);
        requestsRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new RequestsAdapter(requests , this);
        requestsRecyclerView.setAdapter(mAdapter);
        formatRequestsList();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,ItemsActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        formatRequestsList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        Utils.logout(this);
        Intent i = new Intent(this, LoginRegisterActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    private void formatRequestsList() {
        List<Request> reqList  = databaseService.findAllRequests();
        if (reqList.isEmpty()) {
            requestsRecyclerView.setVisibility(View.GONE);
            emptyListTextView.setVisibility(View.VISIBLE);
        }
        else {
            requests.clear();
            Long now = new Date().getTime();
            try {
                requests.add(DateUtils.getRelativeTimeSpanString(DATE_FORMATTER.parse(DATE_FORMATTER.format(reqList.get(0).getLastModifyDate())).getTime() ,now, DateUtils.DAY_IN_MILLIS));
            }
            catch (ParseException e) {
                Log.e(TAG, "Error parse date : "+reqList.get(0).getLastModifyDate());
                e.printStackTrace();
            }
            Date d1 , d2 ;
            for (int i = 0 ; i < reqList.size() ; i++) {

                requests.add(reqList.get(i));
                if ((i+1) < reqList.size()) {
                    try {
                        d1 = DATE_FORMATTER.parse(DATE_FORMATTER.format(reqList.get(i).getLastModifyDate())) ;
                        d2 = DATE_FORMATTER.parse(DATE_FORMATTER.format(reqList.get(i+1).getLastModifyDate())) ;
                        if (d1.compareTo(d2) != 0 ) {
                            requests.add( DateUtils.getRelativeTimeSpanString(d2.getTime() , now , DateUtils.DAY_IN_MILLIS));
                        }
                    }
                    catch (ParseException e) {
                        Log.e(TAG, "Error parse date : "+reqList.get(i).getLastModifyDate());
                        e.printStackTrace();
                    }

                }

            }
            requestsRecyclerView.setVisibility(View.VISIBLE);
            emptyListTextView.setVisibility(View.GONE);
            mAdapter.notifyDataSetChanged();
        }
    }
}
