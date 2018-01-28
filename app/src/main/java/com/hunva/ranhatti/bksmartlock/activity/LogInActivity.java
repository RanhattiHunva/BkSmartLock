package com.hunva.ranhatti.bksmartlock.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.hunva.ranhatti.bksmartlock.R;
import com.hunva.ranhatti.bksmartlock.dataControl.OfflineDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LogInActivity extends AppCompatActivity {

    // DEFINE GLOBAL VARIABLE
    EditText edtAccount, edtPassword;
    Button btnLogIn, btnSignUp;
    CheckBox cbRememberPassword;

    Integer[] userLockData; // TO SAVE USER_LOCK RELATIONSHIP DATA

    SharedPreferences sharedPreferences;

    OfflineDatabase database;

//    final String urlGetUserInformation = "https://bksmartlock.000webhostapp.com/getUserData.php";
//    final String urlGetUserLockInformation = "https://bksmartlock.000webhostapp.com/getUserLockData.php";
//    final String urlGetLocksInformation = "https://bksmartlock.000webhostapp.com/getLockData.php";

    final String urlGetUserInformation = "http://192.168.56.1:8012/bksmartlock/getUserData.php";
    final String urlGetUserLockInformation = "http://192.168.56.1:8012/bksmartlock/getUserLockData.php";
    final String urlGetLocksInformation = "http://192.168.56.1:8012/bksmartlock/getLockData.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        register();
        addEvent();
    }

    private void register(){
        // PREFERENCES FOR THE VIEWS
        edtAccount = findViewById(R.id.edtLoginUsername);
        edtPassword = findViewById(R.id.edtLogInPassword);
        btnLogIn = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);
        cbRememberPassword = findViewById(R.id.cbRemember);

        // INITIALIZE SQLite DATABASE INCLUDE USER AND LOCK INFORMATION
        database = new OfflineDatabase(this,"OfflineData.sqlite",null,1 );

        // RECREATE USER INFORMATION TABLE
        database.QueryData("DROP TABLE IF EXISTS user_information");
        database.QueryData("CREATE TABLE IF NOT EXISTS user_information(" +
                "id INTEGER DEFAULT 0, " +
                "username VARCHAR(50), " +
                "passwords VARCHAR(50), " +
                "position VARCHAR(50), " +
                "department VARCHAR(50), " +
                "phoneNumber VARCHAR(50), " +
                "email VARCHAR(50), " +
                "fullName VARCHAR(50), " +
                "securityKey VARCHAR(32))");

        // RECREATE LOCKS TABLE
        database.QueryData("DROP TABLE IF EXISTS lock_information");
        database.QueryData("CREATE TABLE IF NOT EXISTS lock_information(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name VARCHAR(50), " +
                "location VARCHAR(50), " +
                "certificationAdmin INTEGER DEFAULT 0)");

        // FILL WITH OLD INFORMATION
        sharedPreferences = getSharedPreferences("dataLogin", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("checkRemember",true)) {
            edtAccount.setText(sharedPreferences.getString("account", ""));
            edtPassword.setText(sharedPreferences.getString("passwords", ""));
            cbRememberPassword.setChecked(sharedPreferences.getBoolean("checkRemember", true));
        }
    }

    private void addEvent() {
        //ACTION WHEN CLICK ON THE BUTTON LOG IN
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // CHECK INTERNET CONNECTION
                if ( isInternetOnline() ) {
                    // CHECK IS INFORMATION EMPTY
                    String username = edtAccount.getText().toString();
                    String passwords = edtPassword.getText().toString();
                    if ((username.isEmpty()) || (passwords.isEmpty())) {
                        Toast.makeText(LogInActivity.this, getString(R.string.notify_empty_information), Toast.LENGTH_SHORT).show();
                    } else {
                        // GET ALL USER INFORMATION AND MOVE TO MAIN ACTIVITY IF LOGIN SUCCESS FULL
                        getUserInformation(urlGetUserInformation, username, passwords);
                    }
                }else{
                    Toast.makeText(LogInActivity.this, getString(R.string.notify_no_internet), Toast.LENGTH_SHORT).show();
                }
            }
        });

        //ACTION WHEN CLICK ON THE BUTTON SIGN UP
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent moveToSignUpActivity = new Intent(LogInActivity.this, SignUpActivity.class);
                startActivity(moveToSignUpActivity);
            }
        });
    }

    // SEARCH USER INFORMATION ON THE SERVE
    private void getUserInformation(String url, final String username, final String passwords){
        // USING VOLLEY LIBRARY TO QUEUE DATA FROM SEVER
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Boolean flagLogInSuccessful = false;
                        // LOAD ALL TABLE USER INFORMATION TO COMPARE. THIS PART NEED TO OPTIMIZE LATER
                        JSONObject users;
                        for (int i=0; i<response.length();i++){
                            try {
                                users = response.getJSONObject(i);
                                if ((users.getString("username").equals(username))  && (users.getString("passwords").equals(passwords))){
                                    flagLogInSuccessful = true;

                                    // SAVE DATA TO SQLite OFFLINE DATABASE.
                                    database.QueryData("INSERT INTO user_information  VALUES" +
                                            "("+users.getInt("id")+"," +
                                            "'"+ username +"'," +
                                            "'"+passwords+"'," +
                                            "'"+users.getString("position")+"'," +
                                            "'"+users.getString("department")+"'," +
                                            "'"+users.getString("phoneNumber")+"'," +
                                            "'"+users.getString("email")+"',"+
                                            "'"+users.getString("fullName")+"',"+
                                            "'"+users.getString("securityKey")+"')");

                                    // GET DATA LOCK OF THE USER
                                    getUserLockInformation(urlGetUserLockInformation, username);

                                    // SAVE DATA TO SHARE PREFERENCES
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    if (cbRememberPassword.isChecked()) {
                                        editor.putString("account", username);
                                        editor.putString("passwords",passwords);
                                        editor.putBoolean("checkRemember",true);
                                        editor.putBoolean("certificationLogIn", true);
                                    }
                                    else{
                                        editor.putString("account", username);
                                        editor.putString("passwords",passwords);
                                        editor.putBoolean("checkRemember",false);
                                        editor.putBoolean("certificationLogIn", true);
                                    }
                                    editor.putBoolean("isDataChange",false);
                                    editor.apply();
                                    break;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        if (!flagLogInSuccessful){
                            // NOTIFY IF INFORMATION IS WRONG
                            Toast.makeText(LogInActivity.this, R.string.notify_wrong_information, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // GET NOTIFY FOR SYSTEM ERROR, NEED ADD MORE ACTION.
                        Toast.makeText(LogInActivity.this, "Error system, Please report this to admin!",Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonArrayRequest);
    }

    private void getUserLockInformation(String url, final String username) {
        RequestQueue requestQueue = Volley.newRequestQueue(LogInActivity.this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // LOAD ALL TABLE USER INFORMATION TO COMPARE. THIS PART NEED TO OPTIMIZE LATER
                        JSONObject usersLock;
                        for (int i=0; i<response.length();i++){
                            try {
                                usersLock = response.getJSONObject(i);
                                if (usersLock.getString("username").equals(username)){

                                    Integer numColumn = usersLock.length();
                                    userLockData = new Integer[numColumn-1];
                                    String nameHandle;

                                    for (int j = 0; j<=numColumn-1; j++){
                                        nameHandle ="lock".concat(String.valueOf(j+1));
                                        try {
                                            userLockData[j]= usersLock.getInt(nameHandle);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    break;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        // GET LOCK INFORMATION
                        getLocksInformation(urlGetLocksInformation);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // GET NOTIFY FOR SYSTEM ERROR, NEED ADD MORE ACTION.
                        Toast.makeText(LogInActivity.this, "Error system, Please report this to admin!",Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonArrayRequest);
    }

    private void getLocksInformation(String url) {
        RequestQueue requestQueue = Volley.newRequestQueue(LogInActivity.this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // LOAD ALL TABLE USER INFORMATION TO COMPARE. THIS PART NEED TO OPTIMIZE LATER
                        JSONObject lock; Boolean flagFirstData = true;
                        for (int i=0; i<response.length();i++){
                            if (userLockData[i]>0) {
                                try {
                                    lock = response.getJSONObject(i);
                                    database.QueryData("INSERT INTO lock_information  VALUES" +
                                            "("+lock.getInt("id")+"," +
                                            "'"+lock.getString("name")+"'," +
                                            "'"+lock.getString("location")+"'," +
                                            " "+userLockData[i]+")");

                                    if (flagFirstData){
                                        SharedPreferences.Editor editor2 = sharedPreferences.edit();
                                        editor2.putInt("currentLock",lock.getInt("id"));
                                        editor2.apply();
                                        flagFirstData = false;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        // TOAST NOTIFY THAT LOG IN SUCCESSFUL AND MOVE BACK TO MAIN ACTIVITY
                        Toast.makeText(LogInActivity.this, R.string.notify_access, Toast.LENGTH_SHORT).show();
                        Intent moveToMainActivity = new Intent(LogInActivity.this, MainActivity.class);
                        startActivity(moveToMainActivity);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // GET NOTIFY FOR SYSTEM ERROR, NEED ADD MORE ACTION.
                        Toast.makeText(LogInActivity.this, "Error system, Please report this to admin!",Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonArrayRequest);
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
