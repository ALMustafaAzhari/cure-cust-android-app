package com.andriod.cust.cure.util;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.andriod.cust.cure.bean.Customer;
import com.andriod.cust.cure.bean.LoginResponse;
import com.andriod.cust.cure.service.CustomerService;

import java.text.SimpleDateFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Utils {

    public static final SimpleDateFormat ISO_DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZZ" , new Locale("en"));

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE));
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }

    public static boolean isLogin(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("USER_DETAILS" , Context.MODE_PRIVATE);
        return prefs.getBoolean("IsLogged",false);
    }

    public static void logout(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("USER_DETAILS" , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit() ;
        editor.clear();
        editor.apply();

        //stopGetRequestReceiverSchedule(context);

        //DatabaseService databaseService = new DatabaseService(context);
        //databaseService.deleteAll();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

    }

    public static void refreshToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("USER_DETAILS" , Context.MODE_PRIVATE);
        CustomerService service  = RetrofitClient.getRetrofitInstance().create(CustomerService.class) ;
        Call<LoginResponse> call = service.refreshToken(prefs.getString("refresh_Token" , null));
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    SharedPreferences.Editor editor = prefs.edit() ;
                    editor.putString("token" , response.body().getToken());
                    editor.putString("refresh_Token" , response.body().getRefresh_Token());
                    editor.apply();
                }

                else if (response.code() == 401) {
                    logout(context);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("Utils" , "Refresh token failure.");
                t.printStackTrace();
            }
        });

    }
}
