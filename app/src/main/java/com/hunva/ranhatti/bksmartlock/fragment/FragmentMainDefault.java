package com.hunva.ranhatti.bksmartlock.fragment;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.hunva.ranhatti.bksmartlock.R;
import com.hunva.ranhatti.bksmartlock.activity.MainActivity;
import com.hunva.ranhatti.bksmartlock.dataControl.OfflineDatabase;

/**
 * Created by Master on 1/23/2018.
 * Fragment main default
 */

public class FragmentMainDefault extends Fragment{

    // DEFINE GLOBAL VARIABLE
    Button btnUser, btnAdmin;
    ImageButton btnChangeStatusLock;
    TextView textStatusLock;

    MainActivity activity;

    SharedPreferences sharedPreferences;

    OfflineDatabase database;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_default,container,false);

        register(view);
        addEvent();

        return view;
    }

    private void register(View view) {
        // PREFERENCES FOR THE VIEWS
        btnUser =  view.findViewById(R.id.btnUser);
        btnAdmin = view.findViewById(R.id.btnAdmin);
        btnChangeStatusLock = view. findViewById(R.id.btnChangeStatusLock);
        textStatusLock = view.findViewById(R.id.textStateLock);

        // GET ACTIVITY
        activity = (MainActivity) getActivity();

        // GET DATABASE AND SharePreferences
        database = activity.getDatabase();
        sharedPreferences = activity.getSharedPreferences();
    }


    private void addEvent() {
        //ACTION WHEN CLICK ON THE IMAGE LOCK TO CHANGE LOCK'S STATUS
        btnChangeStatusLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String StatusLock = textStatusLock.getText().toString();

                if (StatusLock.equals(getString(R.string.locked))) {
                    textStatusLock.setText(getString(R.string.unlocked));
                    btnChangeStatusLock.setImageResource(R.drawable.icons_unlocked);
                }
                else {
                    textStatusLock.setText(getString(R.string.locked));
                    btnChangeStatusLock.setImageResource(R.drawable.icons_locked);
                }
            }
        });

        //ACTION WHEN CLICK ON THE BUTTON USER
        btnUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.changeMainActivityFragment("user");
            }
        });

        //ACTION WHEN CLICK ON THE BUTTON ADMIN
        btnAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor curData = database.GetData("SELECT * FROM lock_information WHERE id = "+sharedPreferences.getInt("currentLock",0)+" LIMIT 1");
                curData.moveToFirst();

                if (curData.getInt(3)==3) {
                    curData.close();
                    activity.changeMainActivityFragment("admin");
                }
                else {
                    Toast.makeText(activity,getString(R.string.deny_access_admin),Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
