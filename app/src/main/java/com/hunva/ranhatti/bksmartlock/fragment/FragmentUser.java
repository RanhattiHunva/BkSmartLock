package com.hunva.ranhatti.bksmartlock.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hunva.ranhatti.bksmartlock.activity.LogInActivity;
import com.hunva.ranhatti.bksmartlock.R;
import com.hunva.ranhatti.bksmartlock.activity.MainActivity;
import com.hunva.ranhatti.bksmartlock.dataControl.OfflineDatabase;

import static android.view.Window.FEATURE_NO_TITLE;

/**
 * Created by ranha on 1/23/2018.
 * Fragment user
 */

public class FragmentUser extends Fragment {

    // DEFINE GLOBAL VARIABLE
    Button btnLogOut, btnChangePasswords;
    TextView textUsername, textPosition, textDepartment, textEmail, textPhoneNumber,
            textCertificationAdmin, textRequireAdmin, textFullName, textChangeUserInformation,
            textChangeSetting;

    MainActivity activity;

    SharedPreferences sharedPreferences;

    OfflineDatabase database;

    FragmentSettingDefault fragmentSettingDefault;
    FragmentSettingPlus fragmentSettingPlus;

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
        textChangeSetting = view.findViewById(R.id.textChangeSetting);

        // GET ACTIVITY
        activity = (MainActivity) getActivity();
        sharedPreferences = activity.getSharedPreferences();
        database = activity.getDatabase();
        updatePresentData();

        // ADD FRAGMENT FOR THE SETTING CHANG FRAME LAYOUT
        flagSettingFragment = false;
        changeSettingFragment();
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
                        // CHECK HAVE SYNC DATA YET
                        if (!sharedPreferences.getBoolean("isDataChange",false)) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.remove("certificationLogIn");
                            editor.putBoolean("isDataChange", false);
                            editor.apply();

                            Intent moveToLogInActivity = new Intent(activity, LogInActivity.class);
                            startActivity(moveToLogInActivity);
                        }else{
                            Toast.makeText(activity,getString(R.string.notify_data_not_synced),Toast.LENGTH_LONG).show();
                        }
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

        // ACTION WHEN CLICK BUTTON CHANGE PASSWORDS
        btnChangePasswords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // OPEN DIALOG TO CHANGE PASSWORDS
                final Dialog dialogChangePasswords = new Dialog(activity);
                dialogChangePasswords.requestWindowFeature(FEATURE_NO_TITLE);
                dialogChangePasswords.setContentView(R.layout.dialog_change_passworks);
                dialogChangePasswords.setCanceledOnTouchOutside(false);
                dialogChangePasswords.show();

                final EditText edtOldPasswords = dialogChangePasswords.findViewById(R.id.edtOldPasswords);
                final EditText edtNewPasswords = dialogChangePasswords.findViewById(R.id.edtNewPasswords);
                final EditText edtConfirmPasswords = dialogChangePasswords.findViewById(R.id.edtConfirmPasswords);

                // ACTION WHEN CLICK ON "SAVE"
                Button btnSavePasswords = dialogChangePasswords.findViewById(R.id.btnDialogChangePasswordsSave);
                btnSavePasswords.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // CHECK INFORMATION
                        String passwordsCompare = sharedPreferences.getString("passwords","");
                        String newPasswords = edtNewPasswords.getText().toString();
                        if (passwordsCompare.equals(edtOldPasswords.getText().toString()) &&
                                newPasswords.equals(edtConfirmPasswords.getText().toString())) {

                            // SAVE PASSWORDS TO OFFLINE SQLite DATABASE
                            database.QueryData("UPDATE user_information SET " +
                                    "passwords = '"+ newPasswords + "' " +
                                    "WHERE username = '"+ userName +"' ");

                            // TURN ON FLAG DATA NOT SYNCED
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("isDataChange",true);
                            editor.apply();
                            fragmentSettingDefault.presentSyncStatus(sharedPreferences.getBoolean("isDataChange",false));
                            dialogChangePasswords.dismiss();
                        }
                        else {
                            Toast.makeText(activity,
                                    getString(R.string.notify_wrong_confirm_passwords),Toast.LENGTH_LONG).show();
                        }
                    }
                });

                // ABORT DIALOG WHEN CLICK ON "CANCEL"
                Button btnCancelPasswords = dialogChangePasswords.findViewById
                        (R.id.btnDialogChangePasswordsCancel);
                btnCancelPasswords.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogChangePasswords.dismiss();
                    }
                });
            }
        });

        // ACTION TO REQUIRE PERMISSION ADMIN
        textRequireAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(activity,getString(R.string.notify_require_admin),Toast.LENGTH_LONG).show();

                textRequireAdmin.setClickable(false);
                textRequireAdmin.setTextColor(getResources().getColor(R.color.colorText));
                textRequireAdmin.setText(getString(R.string.waiting_acceptation_admin));

                database.QueryData("UPDATE lock_information SET certificationAdmin = 2 WHERE id = "+sharedPreferences.getInt("currentLock",0));

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isDataChange",true);
                editor.apply();
                fragmentSettingDefault.presentSyncStatus(sharedPreferences.getBoolean("isDataChange",false));
            }
        });

        // CHANGE USER INFORMATION WHEN CLICK ON THE TEXT "CHANGE INFORMATION"
        textChangeUserInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // OPEN DIALOG AND LOAD OLD INFORMATION
                final Dialog dialogChangeInformation = new Dialog(activity);
                dialogChangeInformation.requestWindowFeature(FEATURE_NO_TITLE);
                dialogChangeInformation.setContentView(R.layout.dialog_change_personal_information);
                dialogChangeInformation.setCanceledOnTouchOutside(false);
                dialogChangeInformation.show();

                final EditText edtDialogPosition = dialogChangeInformation.findViewById(R.id.edtDialogPosition);
                edtDialogPosition.setText(position);

                final EditText edtDialogDepartment = dialogChangeInformation.findViewById(R.id.edtDialogDepartment);
                edtDialogDepartment.setText(department);

                final EditText edtDialogPhoneNumber = dialogChangeInformation.findViewById(R.id.edtDialogPhoneNumber);
                edtDialogPhoneNumber.setText(phoneNumber);

                final EditText edtDialogEmail = dialogChangeInformation.findViewById(R.id.edtDialogEmail);
                edtDialogEmail.setText(email);

                final EditText edtDialogFullName = dialogChangeInformation.findViewById(R.id.edtDialogFullNam);
                edtDialogFullName.setText(fullName);

                // ACTION WHEN CLICK ON "SAVE"
                Button btnSaveInformation = dialogChangeInformation.findViewById(R.id.btnSaveInformation);
                btnSaveInformation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        position = edtDialogPosition.getText().toString().trim();
                        department = edtDialogDepartment.getText().toString().trim();
                        email = edtDialogEmail.getText().toString().trim();
                        phoneNumber = edtDialogPhoneNumber.getText().toString().trim();
                        fullName = edtDialogFullName.getText().toString().trim();

                        // SAVE TO OFFLINE SQLite DATABASE
                        database.QueryData("UPDATE user_information SET " +
                                "fullName = '"+ fullName + "', " +
                                "position = '"+ position + "', " +
                                "department = '"+ department + "', " +
                                "phoneNumber = '"+ phoneNumber + "', " +
                                "email = '"+ email + "' " +
                                "WHERE username = '"+ userName +"' ");
                        updatePresentData();

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("isDataChange",true);
                        editor.apply();
                        fragmentSettingDefault.presentSyncStatus(sharedPreferences.getBoolean("isDataChange",false));

                        dialogChangeInformation.dismiss();
                    }
                });

                Button btnCancelInformation = dialogChangeInformation.findViewById(R.id.btnCancelInformation);
                btnCancelInformation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogChangeInformation.dismiss();
                    }
                });
            }
        });

        textChangeSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeSettingFragment();
            }
        });
    }

    // UPDATE PRESENT USER INFORMATION FROM OFFLINE SQLite DATABASE
    private void updatePresentData() {
        Cursor curData = database.GetData("SELECT * FROM user_information");
        curData.moveToFirst();

        userName = curData.getString(1);
        textUsername.setText(getString(R.string.user_name).concat(userName));

        fullName = curData.getString(2);
        textFullName.setText(getString(R.string.full_name).concat(fullName));

        position = curData.getString(4);
        textPosition.setText(getString(R.string.position).concat(position));

        department = curData.getString(5);
        textDepartment.setText(getString(R.string.department).concat(department));

        phoneNumber = curData.getString(6);
        textPhoneNumber.setText(getString(R.string.phone_number).concat(phoneNumber));

        email = curData.getString(7);
        textEmail.setText(getString(R.string.email).concat(email));

        curData.close();

        Cursor curDataCertificationAdmin = database.GetData("SELECT * FROM lock_information WHERE id = "+sharedPreferences.getInt("currentLock",0)+" LIMIT 1");
        curDataCertificationAdmin.moveToFirst();
        if (curDataCertificationAdmin.getInt(3) == 3) {
            textCertificationAdmin.setText(getString(R.string.certification).concat("admin"));
            textRequireAdmin.setText(getString(R.string.none));
            textRequireAdmin.setClickable(false);
        }
        else if (curDataCertificationAdmin.getInt(3) == 2) {
            textCertificationAdmin.setText(getString(R.string.certification).concat("user"));
            textRequireAdmin.setClickable(false);
            textRequireAdmin.setTextColor(getResources().getColor(R.color.colorText));
            textRequireAdmin.setText(getString(R.string.waiting_acceptation_admin));
        }
        else {
            textCertificationAdmin.setText(getString(R.string.certification).concat("user"));
        }
        curDataCertificationAdmin.close();
    }

    // CHANGE FRAGMENT IN THE FRAME LAYOUT SETTING
    private void changeSettingFragment(){
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Fragment fragmentSetting;

        if (!flagSettingFragment) {
            fragmentSetting = new FragmentSettingDefault();
            fragmentSettingDefault = (FragmentSettingDefault) fragmentSetting;
        }
        else {
            fragmentSetting = new FragmentSettingPlus();
            fragmentSettingPlus = (FragmentSettingPlus) fragmentSetting;
        }

        fragmentTransaction.replace(R.id.frameSetting, fragmentSetting,"tagFragmentSetting");
        fragmentTransaction.commit();

        flagSettingFragment =!flagSettingFragment;
    }
}
