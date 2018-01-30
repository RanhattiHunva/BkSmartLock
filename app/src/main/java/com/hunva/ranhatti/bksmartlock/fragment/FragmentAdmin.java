package com.hunva.ranhatti.bksmartlock.fragment;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TabHost;
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
import com.hunva.ranhatti.bksmartlock.arrayView.GuestAdapter;
import com.hunva.ranhatti.bksmartlock.arrayView.LockHistoryAdapter;
import com.hunva.ranhatti.bksmartlock.dataControl.GuestPresent;
import com.hunva.ranhatti.bksmartlock.dataControl.LockHistoryPresent;
import com.hunva.ranhatti.bksmartlock.dataControl.OfflineDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ranha on 1/23/2018.
 *
 */

public class FragmentAdmin extends Fragment {

    // DEFINE GLOBAL VARIABLE
    ListView lvLockHistory, lvGuest;
    ArrayList<LockHistoryPresent> listLockHistory;
    ArrayList<GuestPresent> listGuest;
    LockHistoryAdapter lockHistoryAdapter;
    GuestAdapter guestAdapter;

    MainActivity activity;

    OfflineDatabase database;

    SharedPreferences sharedPreferences;

    final String urlGetLockHistory = "http://192.168.56.1:8012/bksmartlock/getLockHistory.php";
    final String urlGetListUsers = "http://192.168.56.1:8012/bksmartlock/getListUsers.php";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin, container, false);

        register(view);
        addEvent();

        return view;
    }

    private void register(View view) {
        // GET ACTIVITY
        activity = (MainActivity) getActivity();

        // GET DATABASE
        database = activity.getDatabase();

        // GET SHARE PREFERENCES
        sharedPreferences = activity.getSharedPreferences();

        // INITIALIZE THE TAB HOST
        TabHost tabHostAdmin = view.findViewById(R.id.tabHostAdmin);
        tabHostAdmin.setup();

        TabHost.TabSpec tabGuess = tabHostAdmin.newTabSpec("tabGuess");
        tabGuess.setIndicator(getString(R.string.guest));
        tabGuess.setContent(R.id.tabGuest);
        tabHostAdmin.addTab(tabGuess);

        TabHost.TabSpec tabHistory = tabHostAdmin.newTabSpec("tagHistory");
        tabHistory.setIndicator(getString(R.string.history));
        tabHistory.setContent(R.id.tabHistory);
        tabHostAdmin.addTab(tabHistory);

        // INITIALIZE THE LIST VIEWS
        lvLockHistory = view.findViewById(R.id.lvHistoryLock);
        listLockHistory = new ArrayList<>();
        lockHistoryAdapter = new LockHistoryAdapter(activity, R.layout.element_lock_history_present,listLockHistory);
        lvLockHistory.setAdapter(lockHistoryAdapter);

        lvGuest = view.findViewById(R.id.lvGuest);
        listGuest = new ArrayList<>();
        guestAdapter = new GuestAdapter(activity, R.layout.element_guest_present, listGuest);
        lvGuest.setAdapter(guestAdapter);

        // INITIALIZE THE DATA FOR HISTORY TAB HOST
        getTabHostData(sharedPreferences.getInt("currentLock", 0));
    }

    private void getTabHostData(final Integer lockId) {
        final RequestQueue requestQueue = Volley.newRequestQueue(activity);
        StringRequest getLockHistory = new StringRequest(Request.Method.POST, urlGetLockHistory,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(!response.equals("false")) {
                            // SPLIT RESPONSE TO USABLE ARRAY DATA
                            String[][] arrayData = splitResponse(response);
                            Integer numRow = arrayData.length;

                            // INSERT HISTORY DATA TO LIST VIEWS
                            String oldDate = arrayData[numRow - 1][0].split(" ")[0];
                            for (int rowIndex = numRow - 1; rowIndex >= 0; rowIndex--) {
                                String[] datetime = arrayData[rowIndex][0].split(" ");
                                String date = datetime[0];
                                String time = datetime[1];

                                if ((rowIndex == numRow - 1) || (!date.equals(oldDate))) {
                                    oldDate = date;
                                    listLockHistory.add(new LockHistoryPresent("--:--:--", date, R.drawable.new_date));
                                }
                                if (Integer.parseInt(arrayData[rowIndex][2]) == 0) {
                                    listLockHistory.add(new LockHistoryPresent(time, arrayData[rowIndex][1], R.drawable.history_unlocked));
                                } else {
                                    listLockHistory.add(new LockHistoryPresent(time, arrayData[rowIndex][1], R.drawable.history_unlocked_by_internet));
                                }
                            }
                            lockHistoryAdapter.notifyDataSetChanged();
                        }else{
                            Toast.makeText(activity,getString(R.string.notify_report_app_admin),Toast.LENGTH_SHORT).show();
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
                param.put("lockId",String.valueOf(lockId));
                return param;
            }
        };

        StringRequest getListUsers = new StringRequest(Request.Method.POST, urlGetListUsers,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!response.equals("false")){
                            String[][] arrayData = splitResponse(response);
                            for (String[] anArrayData : arrayData) {
                                switch (Integer.valueOf(anArrayData[1])) {
                                    default:
                                        listGuest.add(new GuestPresent(anArrayData[0], R.drawable.empty));
                                        break;
                                    case 3:
                                        listGuest.add(new GuestPresent(anArrayData[0], R.drawable.admin));
                                        break;
                                    case 4:
                                        listGuest.add(new GuestPresent(anArrayData[0], R.drawable.super_admin));
                                        break;
                                }
                            }
                            guestAdapter.notifyDataSetChanged();
                        }else{
                            Toast.makeText(activity,getString(R.string.notify_report_app_admin),Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(activity,getString(R.string.notify_report_app_admin),Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("lockId",String.valueOf(lockId));
                return param;
            }
        };
        requestQueue.add(getLockHistory);
        requestQueue.add(getListUsers);
    }

    private void addEvent() {
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
