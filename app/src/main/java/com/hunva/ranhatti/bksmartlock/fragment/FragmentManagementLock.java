package com.hunva.ranhatti.bksmartlock.fragment;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.hunva.ranhatti.bksmartlock.R;
import com.hunva.ranhatti.bksmartlock.activity.MainActivity;
import com.hunva.ranhatti.bksmartlock.arrayView.LockPresentAdapter;
import com.hunva.ranhatti.bksmartlock.dataControl.LockPresent;
import com.hunva.ranhatti.bksmartlock.dataControl.OfflineDatabase;

import java.util.ArrayList;

/**
 * Created by ranha on 1/23/2018.
 *
 */

public class FragmentManagementLock extends Fragment {

    // DEFINE GLOBAL VARIABLE
    GridView gvLock;
    ArrayList<LockPresent> listLock;
    LockPresentAdapter adapter;

    MainActivity activity;

    OfflineDatabase database;

    SharedPreferences sharedPreferences;

    Integer[] lockId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_management_lock,container,false);

        register(view);
        addEvent();

        return view;
    }

    private void register(View view) {
        // GET ACTIVITY
        activity = (MainActivity) getActivity();

        // INITIALIZE DATABASE AND SHARE PREFERENCE
        database = activity.getDatabase();
        sharedPreferences = activity.getSharedPreferences();

        // INITIALIZE THE LIST OF GRID VIEW
        gvLock =  view.findViewById(R.id.listLockPresent);
        listLock = new ArrayList<>();

        Cursor curCount = database.GetData("SELECT COUNT (*) FROM lock_information");
        curCount.moveToFirst();
        lockId = new Integer[curCount.getInt(0)];
        curCount.close();

        Cursor curData = database.GetData("SELECT * FROM lock_information");
        String nameLock;
        Integer certificationAdmin;
        Integer i=0;
        while (curData.moveToNext()){
            nameLock = curData.getString(1);
            certificationAdmin = curData.getInt(3);
            if ((certificationAdmin == 1) || (certificationAdmin == 2)){
                listLock.add(new LockPresent(nameLock,R.drawable.icons_user_lock_active));
            }else{
                listLock.add(new LockPresent(nameLock,R.drawable.icons_admin_lock_active));
            }

            lockId[i] = curData.getInt(0);
            i=i+1;
        }
        curData.close();
        listLock.add(new LockPresent(getString(R.string.add_lock),R.drawable.icons_adding_lock));

        adapter = new LockPresentAdapter(activity, R.layout.element_lock_present,listLock);
        gvLock.setAdapter(adapter);
    }

    private void addEvent() {
        // ACTION WHEN SHORT CLICK ON ITEM IN GRID VIEW
        gvLock.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if ((i+1)==listLock.size()){
//                    listLock.add(listLock.size()-2, new LockPresent(getString(R.string.unknown_lock),R.drawable.icons_user_lock_active));
//                    adapter.notifyDataSetChanged();
                    Toast.makeText(activity, "ADD LOCK IS STILL NOT PROGRAM", Toast.LENGTH_SHORT).show();
                }
                else{
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("currentLock",lockId[i]);
                    editor.apply();

                    activity.changeMainActivityFragment("default");
                }
            }
        });
    }
}
