package com.andriod.cust.cure.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.andriod.cust.cure.LoginRegisterActivity;
import com.andriod.cust.cure.R;
import com.andriod.cust.cure.bean.Customer;
import com.andriod.cust.cure.bean.RegisterResponse;
import com.andriod.cust.cure.bean.User;
import com.andriod.cust.cure.service.CustomerService;
import com.andriod.cust.cure.util.RetrofitClient;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment {

    private EditText edUserName , edUserEmail , edUserPhone , edUserPass , edUserRepass ;
    private Button registerButton ;
    private CustomerService service ;


    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        edUserName  = view.findViewById(R.id.et_user_name) ;
        edUserEmail = view.findViewById(R.id.et_user_email) ;
        edUserPhone = view.findViewById(R.id.et_user_phone) ;
        edUserPass  = view.findViewById(R.id.et_user_password) ;
        edUserRepass= view.findViewById(R.id.et_user_repassword) ;
        registerButton = view.findViewById(R.id.btn_register) ;

        service = RetrofitClient.getRetrofitInstance().create(CustomerService.class) ;
        final ProgressDialog mProgress = new ProgressDialog(getActivity());
        mProgress.setTitle(getString(R.string.processing));
        mProgress.setMessage(getString(R.string.please_wait));
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInputValid()) {
                    mProgress.show();
                    User user = new User(edUserName.getText().toString() , edUserEmail.getText().toString(),
                            edUserPass.getText().toString() , edUserPhone.getText().toString()) ;

                    Customer customer = new Customer();
                    customer.setUser(user);

                    Call<RegisterResponse> call = service.register(customer) ;
                    call.enqueue(new Callback<RegisterResponse>() {
                        @Override
                        public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                            if (response.isSuccessful()) {
                                mProgress.dismiss();
                                AlertDialog show = new MaterialAlertDialogBuilder(getActivity())
                                        .setTitle(getString(R.string.register_success_title))
                                        .setMessage(getString(R.string.register_success_messg))
                                        .setPositiveButton(getString(R.string.register_success_clickToLogin), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                Intent i = new Intent(getContext(), LoginRegisterActivity.class);
                                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(i);
                                                getActivity().finish();
                                            }}).show();
                            }

                            else {
                                mProgress.dismiss();
                                if (response.code() == 400) {
                                    try {
                                        JSONObject errorObj = new JSONObject(response.errorBody().string());
                                        if (errorObj.getString("code").equalsIgnoreCase("UserExistsException")) {
                                            showDialog(getString(R.string.register_failure_title), getString(R.string.register_user_exist_err_messg));
                                        }
                                        else {
                                            showDialog(getString(R.string.register_failure_title), getString(R.string.register_failur_err_messg));
                                        }
                                    }
                                    catch (JSONException | IOException e) {
                                        e.printStackTrace();
                                        showDialog(getString(R.string.register_failure_title), getString(R.string.register_failur_err_messg));
                                    }

                                }

                                else {
                                    showDialog(getString(R.string.register_failure_title), getString(R.string.register_failur_err_messg));
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<RegisterResponse> call, Throwable t) {
                            mProgress.dismiss();
                            showDialog(getString(R.string.register_failure_title) , getString(R.string.register_failur_err_messg));
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
                .setPositiveButton(getString(android.R.string.ok),(DialogInterface dialog, int which) -> {
                    dialog.dismiss();
                }).show();
    }

    private boolean isInputValid() {
        boolean isVaild  = true ;

        if (edUserName.getText() == null || edUserName.getText().toString().trim().isEmpty()) {
            edUserName.setError(getString(R.string.field_required_error_messg));
            isVaild= false ;
        }

        if (edUserPhone.getText() == null || edUserPhone.getText().toString().trim().isEmpty()) {
            edUserPhone.setError(getString(R.string.field_required_error_messg));
            isVaild= false ;
        }

        if (edUserEmail.getText() == null || edUserEmail.getText().toString().trim().isEmpty()) {
            edUserEmail.setError(getString(R.string.field_required_error_messg));
            isVaild= false ;
        }

        if (edUserPass.getText() == null || edUserPass.getText().toString().trim().isEmpty()) {
            edUserPass.setError(getString(R.string.field_required_error_messg));
            isVaild= false ;
        }

        if (edUserRepass.getText() == null || edUserRepass.getText().toString().trim().isEmpty()) {
            edUserRepass.setError(getString(R.string.field_required_error_messg));
            isVaild= false ;
        }

        if (isVaild) {
            if (!edUserPass.getText().toString().equals(edUserRepass.getText().toString())) {
                edUserRepass.setError(getString(R.string.password_not_match_error_messg));
                isVaild = false ;
            }

            if (!edUserEmail.getText().toString().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                edUserEmail.setError(getString(R.string.email_invalid_error_messg));
                isVaild = false ;
            }

            if (!edUserPhone.getText().toString().matches("^249[0-9]{9}$")) {
                edUserPhone.setError(getString(R.string.phone_invalid_error_messg));
                isVaild = false ;
            }

        }
        return isVaild ;

    }
}
