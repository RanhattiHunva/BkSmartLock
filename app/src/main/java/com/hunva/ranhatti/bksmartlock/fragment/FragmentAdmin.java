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
import com.hunva.ranhatti.bksmartlock.arrayView.GuestInforAdapter;
import com.hunva.ranhatti.bksmartlock.arrayView.LockHistoryAdapter;
import com.hunva.ranhatti.bksmartlock.dataControl.GuestInforPresent;
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
    ArrayList<GuestInforPresent> listGuest;
    LockHistoryAdapter lockHistoryAdapter;
    GuestInforAdapter guestInforAdapter;

    MainActivity activity;

    OfflineDatabase database;

    SharedPreferences sharedPreferences;

    final String urlGetlockHistory = "http://192.168.56.1:8012/bksmartlock/getLockHistory.php";

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
        guestInforAdapter = new GuestInforAdapter(activity, R.layout.element_guest_present, listGuest);
        lvGuest.setAdapter(guestInforAdapter);

        // CHECKING INTERNET CONNECTION
        if (!activity.isInternetOnline()){
            Toast.makeText(activity,getString(R.string.notify_no_internet), Toast.LENGTH_SHORT).show();
        }else{
            // INITIALIZE THE DATA FOR HISTORY TAB HOST
            getLockHistory(urlGetlockHistory, sharedPreferences.getInt("currentLock", 0));
            lockHistoryAdapter.notifyDataSetChanged();

            listGuest.add(new GuestInforPresent("Trần Thái Anh Vũ"));
            listGuest.add(new GuestInforPresent("Nguyen Van Long"));
            listGuest.add(new GuestInforPresent("Thai Thuy Van"));
            listGuest.add(new GuestInforPresent("Thai Thuy Dung"));
            listGuest.add(new GuestInforPresent("Thai N"));
            guestInforAdapter.notifyDataSetChanged();
        }
    }

    private void getLockHistory(String url, final Integer lockId) {
        final RequestQueue requestQueue = Volley.newRequestQueue(activity);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(!response.equals("false")) {
                            // SPLIT RESPONSE TO USABLE ARRAY DATA
                            String[] row = response.split("\\|");

                            Integer numRow = row.length;
                            Integer numColumn = row[0].split(",").length;
                            String[][] arrayData = new String[numRow][numColumn];

                            for (int rowIndex = 0; rowIndex < numRow; rowIndex++) {
                                String[] data = row[rowIndex].split(",");
                                System.arraycopy(data, 0, arrayData[rowIndex], 0, data.length);
                            }

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
        requestQueue.add(stringRequest);
    }

    private void addEvent() {
    }

}
