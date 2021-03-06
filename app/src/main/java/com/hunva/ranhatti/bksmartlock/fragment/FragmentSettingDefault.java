package com.hunva.ranhatti.bksmartlock.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hunva.ranhatti.bksmartlock.R;
import com.hunva.ranhatti.bksmartlock.activity.MainActivity;
import com.hunva.ranhatti.bksmartlock.dataControl.OfflineDatabase;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Ranhatti Hunva on 12/27/2017.
 * Fragment to do action when click text "Change Setting"
 */

public class FragmentSettingDefault extends Fragment{

    ImageButton btnWifi, btnBluetooth;
    public ImageButton btnSyncData;

    TextView textStatusWifi, textStatusBluetooth;
    public TextView textSyncStatus;

    WifiManager wifiManager;

    OfflineDatabase database;

    SharedPreferences sharedPreferences;

    MainActivity activity;

//    String urlUpdateUserInformation = "https://bksmartlock.000webhostapp.com/updateUserData.php";
//    String urlUpdateAccessPermission = "https://bksmartlock.000webhostapp.com/updateUserLockData.php";

    final String urlUpdateUserInformation = "http://192.168.31.71:8888/bksmartlock/updateUserInformation.php";
    final String urlUpdateAccessPermission = "http://192.168.31.71:8888/bksmartlock/updateAccessPermission.php";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_default,container,false);

        register(view);
        addEvent();

        return view;
    }

    private void register(View view) {
        btnWifi = view.findViewById(R.id.btnWifi);
        btnBluetooth =  view.findViewById(R.id.btnBluetooth);
        textStatusWifi =  view.findViewById(R.id.textWfifStatus);
        textStatusBluetooth = view.findViewById(R.id.textBluetooth);
        textSyncStatus = view.findViewById(R.id.textSyncStatus);
        btnSyncData = view.findViewById(R.id.btnSyncData);

        // GET ACTIVITY
        activity = (MainActivity) getActivity();

        // GET SQLite DATABASE FROM ACTIVITY
        database = new OfflineDatabase(getActivity().getApplicationContext(),"OfflineData.sqlite",null,1 );

        // CHECK SYNC DATA
        sharedPreferences = getActivity().getSharedPreferences("dataLogin", MODE_PRIVATE);
        presentSyncStatus(sharedPreferences.getBoolean("isDataChange",false));

        // NEED TO MODIFY LATER TO WAITING UNTIL INTERNET CONNECTION IS ESTABLISH
        wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            presentStatusWifi(wifiManager.isWifiEnabled());
        }
    }

    private void addEvent() {
        // ACTION WHEN CLICK ON THE BUTTON WIFI
        btnWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wifiManager.isWifiEnabled()) {
                    wifiManager.setWifiEnabled(false);
                    presentStatusWifi(false);
                }
                else{
                    wifiManager.setWifiEnabled(true);
                    presentStatusWifi(true);
                }
            }
        });

        // ACTION WHEN CLICK ON THE BUTTON SYNC DATA
        btnSyncData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // CHECK INTERNET CONNECTION
                if ( activity.isInternetOnline() ) {
                    // UPDATE DATABASE TO SEVER
                    updateUserInformation();
                }else{
                    Toast.makeText(activity, getString(R.string.notify_no_internet), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // CHANGE INTERNET MODE
    private void presentStatusWifi(boolean isWifiOn ) {
        if (isWifiOn) {
            btnWifi.setImageResource(R.drawable.wifi_on);
            if ( activity.isInternetOnline() ){
                textStatusWifi.setText(getString(R.string.connected));
            }else{
                textStatusWifi.setText(getString(R.string.disconnected));
            }
        }
        else{
            btnWifi.setImageResource(R.drawable.wifi_off);
            textStatusWifi.setText(getString(R.string.disconnected));
        }
    }

    // UPDATE USER INFORMATION TO SEVER
    protected void updateUserInformation(){
        final RequestQueue requestQueue = Volley.newRequestQueue(activity);

        final Cursor curUserData = database.GetData("SELECT * FROM user_information");
        curUserData.moveToFirst();

        final Cursor curLockData = database.GetData("SELECT * FROM lock_information");

        final StringRequest updateLockPermissionIndex = new StringRequest(Request.Method.POST, urlUpdateAccessPermission,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.trim().equals("success")){
                            Toast.makeText(activity,getString(R.string.notify_sync_successful),Toast.LENGTH_LONG).show();
                            curUserData.close();
                            curLockData.close();
                            // MODIFY PRESENT SYNC STATUS
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("isDataChange", false);
                            editor.apply();
                            presentSyncStatus(sharedPreferences.getBoolean("isDataChange",false));
                        }else{
                            Toast.makeText(activity,getString(R.string.notify_report_app_admin),Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(activity,getString(R.string.notify_report_app_admin),Toast.LENGTH_LONG).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("username",String.valueOf(curUserData.getString(1)));
                while (curLockData.moveToNext()) {
                    param.put("BKSL_"+String.valueOf(curLockData.getInt(0)),String.valueOf(curLockData.getInt(3)));
                }
                return param;
            }
        };

        StringRequest updateUserInformation = new StringRequest(Request.Method.POST, urlUpdateUserInformation,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.trim().equals("success")){
                            // UPDATE USER LOCK RELATIONSHIP INFORMATION
                            requestQueue.add(updateLockPermissionIndex);
                        }else{
                            Toast.makeText(activity,getString(R.string.notify_report_app_admin),Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(activity,getString(R.string.notify_report_app_admin),Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("id",String.valueOf(curUserData.getInt(0)));
                param.put("fullName", curUserData.getString(2));
                param.put("passwords", curUserData.getString(3));
                param.put("position", curUserData.getString(4));
                param.put("department", curUserData.getString(5));
                param.put("phoneNumber", curUserData.getString(6));
                param.put("email", curUserData.getString(7));
                param.put("AES_key", curUserData.getString(8));
                return param;
            }
        };
        requestQueue.add(updateUserInformation);
    }

    public void presentSyncStatus(boolean status){
        if (status){
            btnSyncData.setImageResource(R.drawable.data_not_synced);
            textSyncStatus.setText(R.string.data_not_synced);
        }else{
            btnSyncData.setImageResource(R.drawable.data_synced);
            textSyncStatus.setText(R.string.data_synced);
        }
    }
}
