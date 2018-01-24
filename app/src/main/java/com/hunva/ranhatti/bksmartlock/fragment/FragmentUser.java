package com.hunva.ranhatti.bksmartlock.fragment;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hunva.ranhatti.bksmartlock.activity.LogInActivity;
import com.hunva.ranhatti.bksmartlock.R;
import com.hunva.ranhatti.bksmartlock.activity.MainActivity;
import com.hunva.ranhatti.bksmartlock.dataControl.OfflineDatabase;

/**
 * Created by ranha on 1/23/2018.
 * Fragment user
 */

public class FragmentUser extends Fragment {

    // DEFINE GLOBAL VARIABLE
    Button btnLogOut, btnChangePasswords;
    TextView textUsername, textPosition, textDepartment, textEmail, textPhoneNumber,
            textCertificationAdmin, textRequireAdmin, textFullName, textChangeUserInformation;

    MainActivity activity;

    SharedPreferences sharedPreferences;

    OfflineDatabase database;

//    FragmentSettingDefault fragmentSettingDefault;
//    FragmentSettingPlus fragmentSettingPlus;

    Boolean flagSettingFragment;
    String userName, position, department, phoneNumber, email, fullName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_user,container,false);

        register(view);
        addEvent();

        return view;
    }

    private void register(View view) {
        // PREFERENCES FOE THE VIEWS
        btnLogOut = view.findViewById(R.id.btnLogOut);
        btnChangePasswords = view.findViewById(R.id.btnChangePassword);
        textUsername = view.findViewById(R.id.textUserName);
        textPosition = view.findViewById(R.id.textPosition);
        textDepartment = view.findViewById(R.id.textDepartment);
        textPhoneNumber = view.findViewById(R.id.textPhoneNumber);
        textEmail = view.findViewById(R.id.textEmail);
        textCertificationAdmin = view.findViewById(R.id.textCertificationAdmin);
        textRequireAdmin = view.findViewById(R.id.textRequireAdmin);
        textFullName = view.findViewById(R.id.textDialogFullName);
        textChangeUserInformation = view.findViewById(R.id.textChangeInformation);

        // GET ACTIVITY
        activity = (MainActivity) getActivity();
    }

    private void addEvent() {

        //ACTION WHEN CLICK BUTTON LOG OUT
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // OPEN DIALOG TO CONFIRM LOG OUT
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                alertDialog.setTitle(R.string.notify);
                alertDialog.setMessage(R.string.notify_confirm_log_out);

                // ACTION WHEN CLICK ON BUTTON "YES"
                alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent moveToLogInActivity = new Intent(activity, LogInActivity.class);
                        startActivity(moveToLogInActivity);
                    }
                });

                // ABORT DIALOG WHEN CLICK ON "NO"
                alertDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alertDialog.show();
            }
        });
    }
}
