package com.andriod.cust.cure;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.andriod.cust.cure.bean.AddRequestResponse;
import com.andriod.cust.cure.bean.Item;
import com.andriod.cust.cure.service.CustomerService;
import com.andriod.cust.cure.service.DatabaseService;
import com.andriod.cust.cure.util.RetrofitClient;
import com.andriod.cust.cure.util.Utils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendRequestActivity extends AppCompatActivity {

    private final static String TAG = "SendRequestActivity";
    DatabaseService databaseService ;
    TextView brandName , genericName , company ;
    Button sendRequestBtn ;

    private CustomerService service ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_request);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        databaseService = new DatabaseService(this);

        brandName = (TextView) findViewById(R.id.item_brand_name);
        genericName = (TextView) findViewById(R.id.item_generic_name);
        company = (TextView) findViewById(R.id.item_company);
        sendRequestBtn = (Button) findViewById(R.id.btn_send_request);



        Intent intent = getIntent();
        Long itemId = intent.getLongExtra(ItemsActivity.EXTRA_ITEM_ID , 0);

        Item item = databaseService.findItemById(itemId) ;
        brandName.setText(item.getBrandName());
        genericName.setText(item.getGenericName());
        String textCompany  = item.getCompany().getName() + "," + getString(getResources().getIdentifier("COUNTRY_"+item.getCompany().getCountryCode(), "string", getPackageName()));
        company.setText(textCompany);

        final ProgressDialog mProgress = new ProgressDialog(this);
        mProgress.setTitle(getString(R.string.processing));
        mProgress.setMessage(getString(R.string.please_wait));
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);

        sendRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress.show();
                SharedPreferences prefs = getSharedPreferences("USER_DETAILS" , MODE_PRIVATE);
                service = RetrofitClient.getRetrofitInstance().create(CustomerService.class) ;
                String authorization = "Bearer " + prefs.getString("token", "") ;
                Item requestItem = new Item() ;
                requestItem.setId(item.getId());
                Call<AddRequestResponse> call = service.addRequest(authorization,item) ;
                call.enqueue(new Callback<AddRequestResponse>() {
                    @Override
                    public void onResponse(Call<AddRequestResponse> call, Response<AddRequestResponse> response) {
                        mProgress.dismiss();
                        if (response.isSuccessful()) {
                            databaseService.addRequest(response.body().getRequest());
                            Intent intent  = new Intent(SendRequestActivity.this , RequestResponsesActivity.class) ;
                            intent.putExtra("ITEM_NAME" , item.getBrandName());
                            intent.putExtra("REQUEST_ID", response.body().getRequest().getId()) ;
                            intent.putExtra("REQUEST_TIME_AGO", DateUtils.getRelativeTimeSpanString(response.body().getRequest().getEntryDate().getTime())) ;
                            if (response.body().getStatus() == 'N') {
                                intent.putExtra("ITEM_REF", true) ;
                            }
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Log.w(TAG , "Get not responded requests not success , HTTP code:"+response.code());
                            if (response.code() == 401) {
                                Log.e(TAG ,"Refresh token");
                                Utils.refreshToken(getApplicationContext());
                            }

                            showDialog(getString(R.string.add_request_failure_title) , getString(R.string.add_request_token_err_messg));
                        }
                    }

                    @Override
                    public void onFailure(Call<AddRequestResponse> call, Throwable t) {
                        mProgress.dismiss();
                        showDialog(getString(R.string.add_request_failure_title) , getString(R.string.add_request_err_messg));
                        t.printStackTrace();
                    }
                });
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void showDialog(String title , String message){
        new MaterialAlertDialogBuilder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

}
