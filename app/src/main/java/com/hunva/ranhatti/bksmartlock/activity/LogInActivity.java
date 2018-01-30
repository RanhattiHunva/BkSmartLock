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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hunva.ranhatti.bksmartlock.R;
import com.hunva.ranhatti.bksmartlock.dataControl.OfflineDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LogInActivity extends AppCompatActivity {

    // DEFINE GLOBAL VARIABLE
    EditText edtAccount, edtPassword;
    Button btnLogIn, btnSignUp;
    CheckBox cbRememberPassword;

    SharedPreferences sharedPreferences;

    OfflineDatabase database;

    Integer[] lockPermissionIndex; // TO SAVE USER_LOCK RELATIONSHIP DATA

//    final String urlGetUserInformation = "https://bksmartlock.000webhostapp.com/getUserData.php";
//    final String urlGetAccessPermission = "https://bksmartlock.000webhostapp.com/getUserLockData.php";
//    final String urlGetLocksInformation = "https://bksmartlock.000webhostapp.com/getLockData.php";

    final String urlGetUserInformation = "http://192.168.56.1:8012/bksmartlock/searchUserInformation.php";
    final String urlGetAccessPermission = "http://192.168.56.1:8012/bksmartlock/getAccessPermission.php";
    final String urlGetLocksInformation = "http://192.168.56.1:8012/bksmartlock/getLockInformation.php";

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
                "fullName VARCHAR(50), " +
                "passwords VARCHAR(50), " +
                "position VARCHAR(50), " +
                "department VARCHAR(50), " +
                "phoneNumber VARCHAR(50), " +
                "email VARCHAR(50), " +
                "AES_key VARCHAR(32))");

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
                        checkUserInformation(username, passwords);
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
    private void checkUserInformation(final String username, final String passwords){
        // USING VOLLEY LIBRARY TO QUEUE DATA FROM SEVER
        final RequestQueue requestQueue = Volley.newRequestQueue(this);

        // GET LOCK INFORMATION
        final JsonArrayRequest getLockInformation = new JsonArrayRequest(Request.Method.GET, urlGetLocksInformation, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // LOAD ALL TABLE USER INFORMATION TO COMPARE. THIS PART NEED TO OPTIMIZE LATER
                        JSONObject lock; Boolean flagFirstData = true;
                        for (int i=0; i<response.length();i++){
                            if (lockPermissionIndex[i]>0) {
                                try {
                                    lock = response.getJSONObject(i);
                                    database.QueryData("INSERT INTO lock_information  VALUES" +
                                            "("+lock.getInt("id")+"," +
                                            "'"+lock.getString("name")+"'," +
                                            "'"+lock.getString("location")+"'," +
                                            " "+ lockPermissionIndex[i]+")");

                                    if (flagFirstData){
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putInt("currentLock",lock.getInt("id"));
                                        editor.apply();
                                        flagFirstData = false;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

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

        // GET LOCK-USER getUserLockInformation INFORMATION
        final StringRequest getUserLockInformation = new StringRequest(Request.Method.POST, urlGetAccessPermission,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!response.equals("false")){
                            String[][] arrayData = splitResponse(response);
                            Integer rowIndex = 0;
                            lockPermissionIndex = new Integer[arrayData[rowIndex].length];
                            for (int colIndex=0; colIndex<arrayData[rowIndex].length;colIndex++){
                                lockPermissionIndex[colIndex]=Integer.parseInt(arrayData[rowIndex][colIndex]);
                            }
                        }else{
                            Toast.makeText(LogInActivity.this,getString(R.string.notify_report_app_admin),Toast.LENGTH_SHORT).show();
                        }
                        // GET LOCK INFORMATION
                        requestQueue.add(getLockInformation);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LogInActivity.this,getString(R.string.notify_report_app_admin),Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("username",username);
                return param;
            }
        };

        // CHECKING AND ADD USER INFORMATION TO OFFLINE DATABASE FROM ONLINE DATABASE
        StringRequest checkUserInformation = new StringRequest(Request.Method.POST, urlGetUserInformation,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!response.equals("false")){
                            String[][] arrayData = splitResponse(response);
                            Integer rowIndex = 0;
                            // SAVE DATA TO SQLite OFFLINE DATABASE.
                            database.QueryData("INSERT INTO user_information  VALUES" +
                                    "("+Integer.parseInt(arrayData[rowIndex][0])+"," +
                                    "'"+arrayData[rowIndex][1]+"'," +
                                    "'"+arrayData[rowIndex][2]+"',"+
                                    "'"+arrayData[rowIndex][3]+"'," +
                                    "'"+arrayData[rowIndex][4]+"'," +
                                    "'"+arrayData[rowIndex][5]+"'," +
                                    "'"+arrayData[rowIndex][6]+"'," +
                                    "'"+arrayData[rowIndex][7]+"',"+
                                    "'"+arrayData[rowIndex][8]+"')");
                            // GET DATA LOCK OF THE USER
                            requestQueue.add(getUserLockInformation);
                        }else{
                            Toast.makeText(LogInActivity.this,getString(R.string.notify_report_app_admin),Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LogInActivity.this,getString(R.string.notify_report_app_admin),Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("username",username);
                param.put("passwords",passwords);
                return param;
            }
        };
        requestQueue.add(checkUserInformation);
    }

    // CHECK THE INTERNET CONNECTION
    private boolean isInternetOnline(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = null;
        if (connectivityManager != null) {
            netInfo = connectivityManager.getActiveNetworkInfo();
        }
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    // SPLIT RESPONSE TO USABLE ARRAY DATA
    private String[][] splitResponse(String response){
        String[] row = response.split("\\|");

        Integer numRow = row.length;
        Integer numColumn = row[0].split(",").length;
        String[][] arrayData = new String[numRow][numColumn];

        for (int i = 0; i < numRow; i++) {
            String[] data = row[i].split(",");
            System.arraycopy(data, 0, arrayData[i], 0, data.length);
        }
        return arrayData;
    }
}
