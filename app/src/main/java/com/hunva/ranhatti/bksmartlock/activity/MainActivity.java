package com.hunva.ranhatti.bksmartlock.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hunva.ranhatti.bksmartlock.R;
import com.hunva.ranhatti.bksmartlock.dataControl.OfflineDatabase;
import com.hunva.ranhatti.bksmartlock.fragment.FragmentAdmin;
import com.hunva.ranhatti.bksmartlock.fragment.FragmentMainDefault;
import com.hunva.ranhatti.bksmartlock.fragment.FragmentManagementLock;
import com.hunva.ranhatti.bksmartlock.fragment.FragmentUser;

public class MainActivity extends AppCompatActivity {

    // DEFINE GLOBAL VARIABLE
    ImageButton btnMainChangeFragment;
    TextView textNameActivity;

    // FRAGMENT
    String fragmentTag;
    FragmentMainDefault fragmentMainDefault;
    FragmentAdmin fragmentAdmin;
    FragmentManagementLock fragmentManagementLock;
    FragmentUser fragmentUser;

    // SHARE PREFERENCES
    SharedPreferences sharedPreferences;

    // SQLite DATA OFFLINE
    OfflineDatabase database;

    // FIRE-BASE DATABASE
    DatabaseReference fireBaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        register();
        addEvent();
    }

    private void register() {

        // PREFERENCES FOR THE VIEWS
        btnMainChangeFragment = findViewById(R.id.btnMainChangeFragment);
        textNameActivity = findViewById(R.id.textNameActivity);

        // CHECK LOG-IN FLAG IN THE SHARE PREFERENCES
        sharedPreferences = getSharedPreferences("dataLogin", MODE_PRIVATE);
        if (!sharedPreferences.getBoolean("certificationLogIn",false)){
            Intent moveToLogInActivity = new Intent(MainActivity.this, LogInActivity.class);
            startActivity(moveToLogInActivity);
            finish();
        }else {
            // OPEN SQLite DATABASE
            database = new OfflineDatabase(this, "OfflineData.sqlite", null, 1);
            // ADD FRAGMENT TO TO FRAME LAYOUT
            changeMainActivityFragment("default");
            // GET FIRES-BASE
            fireBaseDatabase = FirebaseDatabase.getInstance().getReference();
        }
    }

    private void addEvent() {
        btnMainChangeFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fragmentTag.equals("default")){
                    fragmentTag = "managementLock";
                    changeMainActivityFragment(fragmentTag);
                }else{
                    fragmentTag = "default";
                    changeMainActivityFragment(fragmentTag);
                }
            }
        });
    }

    public void changeMainActivityFragment(String fragmentTag){
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Fragment fragmentMainActivity;
        this.fragmentTag = fragmentTag;
        switch (fragmentTag) {
            case "user":
                fragmentMainActivity = new FragmentUser();
                fragmentUser = (FragmentUser) fragmentMainActivity;
                btnMainChangeFragment.setImageResource(R.drawable.icons_back);
                textNameActivity.setText(R.string.user_information);
                break;
            case "admin":
                fragmentMainActivity = new FragmentAdmin();
                fragmentAdmin = (FragmentAdmin) fragmentMainActivity;
                btnMainChangeFragment.setImageResource(R.drawable.icons_back);
                textNameActivity.setText(R.string.admin);
                break;
            case "managementLock":
                fragmentMainActivity = new FragmentManagementLock();
                fragmentManagementLock = (FragmentManagementLock) fragmentMainActivity;
                btnMainChangeFragment.setImageResource(R.drawable.icons_back);
                textNameActivity.setText(R.string.manage_lock);
                break;
            default:
                fragmentMainActivity = new FragmentMainDefault();
                fragmentMainDefault = (FragmentMainDefault) fragmentMainActivity;

                Cursor curData = database.GetData("SELECT * FROM lock_information WHERE id = " + sharedPreferences.getInt("currentLock", 0) + " LIMIT 1");
                curData.moveToFirst();
                textNameActivity.setText(curData.getString(1));
                curData.close();

                btnMainChangeFragment.setImageResource(R.drawable.icons_lock_manager);
                break;
        }
        fragmentTransaction.replace(R.id.frameMainContentFragment, fragmentMainActivity,"tagFragmentSetting");
        fragmentTransaction.commit();
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public OfflineDatabase getDatabase() {
        return database;
    }

    public DatabaseReference getFireBaseDatabase() {
        return fireBaseDatabase;
    }

    // CHECKING INTERNET CONNECTION
    public boolean isInternetOnline(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = null;
        if (connectivityManager != null) {
            netInfo = connectivityManager.getActiveNetworkInfo();
        }
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
