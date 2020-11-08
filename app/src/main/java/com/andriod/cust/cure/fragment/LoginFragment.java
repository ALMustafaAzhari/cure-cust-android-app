package com.andriod.cust.cure.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.andriod.cust.cure.MainActivity;
import com.andriod.cust.cure.R;
import com.andriod.cust.cure.bean.LoginRequest;
import com.andriod.cust.cure.bean.LoginResponse;
import com.andriod.cust.cure.service.CustomerService;
import com.andriod.cust.cure.util.RetrofitClient;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    private EditText usernameEditText , passwordEditText;
    private Button loginButton ;
    private CustomerService service ;
    private SharedPreferences.Editor editor;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        usernameEditText = view.findViewById(R.id.et_login_username);
        passwordEditText = view.findViewById(R.id.et_login_password) ;

        loginButton = view.findViewById(R.id.btn_login) ;

        service = RetrofitClient.getRetrofitInstance().create(CustomerService.class) ;

        final ProgressDialog mProgress = new ProgressDialog(getActivity());
        mProgress.setTitle(getString(R.string.processing));
        mProgress.setMessage(getString(R.string.please_wait));
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInputValid(usernameEditText , passwordEditText)) {
                    mProgress.show();
                    final SharedPreferences prefs = getContext().getSharedPreferences("USER_DETAILS" , Context.MODE_PRIVATE);
                    Call<LoginResponse> call = service.login(new LoginRequest(usernameEditText.getText().toString(), passwordEditText.getText().toString()));
                    call.enqueue(new Callback<LoginResponse>() {
                        @Override
                        public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                            mProgress.dismiss();
                            if (response.isSuccessful()) {
                                editor = prefs.edit() ;
                                editor.putBoolean("IsLogged" , true) ;
                                editor.putString("token" , response.body().getToken());
                                editor.putString("refresh_Token" , response.body().getRefresh_Token());
                                editor.putLong("customer_id" , response.body().getCustomerId());
                                editor.commit();

                                //Utils.scheduleGetRequestReceiver(getContext(),1000);

                                Intent i = new Intent(getContext(), MainActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                getActivity().finish();
                            }
                            else {
                                showDialog(getString(R.string.login_failure_title), getString(R.string.login_invalid_credintial_err_messg));
                            }

                        }

                        @Override
                        public void onFailure(Call<LoginResponse> call, Throwable t) {
                            mProgress.dismiss();
                            showDialog(getString(R.string.login_failure_title) , getString(R.string.login_failure_err_messg));
                            t.printStackTrace();
                        }
                    });
                }
            }
        });

    }

    private void showDialog(String title , String message){
        new MaterialAlertDialogBuilder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    private boolean isInputValid(EditText username , EditText password) {
        boolean isVaild = true ;
        if (username.getText() == null || username.getText().toString().isEmpty()) {
            username.setError(getString(R.string.field_required_error_messg));
            isVaild= false ;
        }

        if (password.getText() == null || password.getText().toString().isEmpty()) {
            password.setError(getString(R.string.field_required_error_messg));
            isVaild= false ;
        }

        return isVaild ;

    }
}
