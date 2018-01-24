package com.hunva.ranhatti.bksmartlock.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hunva.ranhatti.bksmartlock.R;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    //DEFINE GLOBAL VARIABLE
    ImageButton btnBack;
    Button btnSave, btnCancel;
    EditText edtFullName, edtUsername, edtPasswords, edtConfirmPasswords, edtPosition, edtDepartment,
            edtPhoneNumber, edtEmail;

    String username, fullName, passwords, confirmPasswords, position, department, phoneNumber, email;

    final String urlInsertUserInformation = "https://bkguardian.000webhostapp.com/insertUserData.php";
    final String urlInsertUserLockInformation = "https://bkguardian.000webhostapp.com/insertUserLockData.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        register();
        addEvent();
    }

    private void register() {

        // PREFERENCES FOR THE VIEWS
        btnBack = findViewById(R.id.btnBack);
        btnSave = findViewById(R.id.btnSignUpSave);
        btnCancel = findViewById(R.id.btnSignUpCancel);

        edtFullName = findViewById(R.id.edtSignUpFullName);
        edtUsername = findViewById(R.id.edtSignUpUsername);
        edtPasswords = findViewById(R.id.edtSignUpPasswords);
        edtConfirmPasswords = findViewById(R.id.edtSignUpConfirmPasswords);
        edtPosition = findViewById(R.id.edtSignUpPosition);
        edtDepartment = findViewById(R.id.edtSignUpDepartment);
        edtPhoneNumber = findViewById(R.id.edtSignUpPhoneNumber);
        edtEmail = findViewById(R.id.edtSignUpEmail);
    }

    private void addEvent() {

        // ACTION WHEN CLICK ON BUTTON BACK
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmCancel();
            }
        });

        // ACTION WHEN CLICK ON BUTTON CANCEL
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmCancel();
            }
        });

        // ACTION WHEN CLICK ON BUTTON OK
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInternetOnline()) {
                    username = edtUsername.getText().toString().trim();
                    fullName = edtFullName.getText().toString().trim();
                    passwords = edtPasswords.getText().toString().trim();
                    confirmPasswords = edtConfirmPasswords.getText().toString().trim();
                    position = edtPosition.getText().toString().trim();
                    department = edtDepartment.getText().toString().trim();
                    phoneNumber = edtPhoneNumber.getText().toString().trim();
                    email = edtEmail.getText().toString().trim();

                    if ( username.isEmpty() || fullName.isEmpty() || passwords.isEmpty() || confirmPasswords.isEmpty() ||
                            position.isEmpty() || department.isEmpty() || phoneNumber.isEmpty() || email.isEmpty())
                        Toast.makeText(SignUpActivity.this, getString(R.string.notify_empty_information), Toast.LENGTH_LONG).show();
                    else{
                        if (passwords.equals(confirmPasswords)){
                            insertUserInformation(urlInsertUserInformation);
                        }else{
                            Toast.makeText(SignUpActivity.this, getString(R.string.notify_wrong_confirm_passwords), Toast.LENGTH_LONG).show();
                        }
                    }
                }else{
                    Toast.makeText(SignUpActivity.this,R.string.notify_no_internet,Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void confirmCancel(){
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(SignUpActivity.this);
        alertDialog.setTitle(getString(R.string.notify));
        alertDialog.setMessage(getString(R.string.notify_confirm_cancel_sign_up));

        alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent moveToLogInActivity = new Intent(SignUpActivity.this, LogInActivity.class);
                startActivity(moveToLogInActivity);
            }
        });

        alertDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    // INSERT NEW USER INFORMATION TO SEVER
    private void insertUserInformation(String url){
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("success")) {
                            insertUserLockInformation(urlInsertUserLockInformation);
                        }else{
                            Toast.makeText(SignUpActivity.this, getString(R.string.notify_username_is_taken), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SignUpActivity.this,getString(R.string.notify_report_app_admin),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("username",username);
                param.put("fullName",fullName);
                param.put("passwords",passwords);
                param.put("position",position);
                param.put("department",department);
                param.put("phoneNumber",phoneNumber);
                param.put("email",email);
                param.put("securityKey","asd5613246ASD879Q654654sd4a6sd46");
                return param;
            }
        };
        requestQueue.add(stringRequest);
    }

    // INSERT NEW USER LOCK RELATIONSHIP DATA TO SEVER
    private void insertUserLockInformation(String url){
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("success")) {
                            Toast.makeText(SignUpActivity.this, getString(R.string.notify_sign_up_successful), Toast.LENGTH_LONG).show();
                            Intent moveToLogInActivity = new Intent(SignUpActivity.this, LogInActivity.class);
                            startActivity(moveToLogInActivity);
                        }else{
                            Toast.makeText(SignUpActivity.this, getString(R.string.notify_username_is_taken), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SignUpActivity.this,getString(R.string.notify_report_app_admin),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("username",username);
                param.put("lock1","1");
                param.put("lock2","1");
                param.put("lock3","1");
                return param;
            }
        };
        requestQueue.add(stringRequest);
    }

    // CHECK THE INTERNET CONNECTION
    public boolean isInternetOnline(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = null;
        if (connectivityManager != null) {
            netInfo = connectivityManager.getActiveNetworkInfo();
        }
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
