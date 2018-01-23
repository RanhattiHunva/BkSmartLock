package com.hunva.ranhatti.bksmartlock.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hunva.ranhatti.bksmartlock.R;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        register();
        addEvent();
    }

    private void register() {
        btnMainChangeFragment = findViewById(R.id.btnMainChangeFragment);
        textNameActivity = findViewById(R.id.textNameActivity);

        fragmentTag = "default";
        changeMainActivityFragment(fragmentTag);
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

    private void changeMainActivityFragment(String fragmentTag){

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Fragment fragmentMainActivity;

        switch (fragmentTag) {
            case "user":
                fragmentMainActivity = new FragmentUser();
                fragmentUser = (FragmentUser) fragmentMainActivity;
                break;
            case "admin":
                fragmentMainActivity = new FragmentAdmin();
                fragmentAdmin = (FragmentAdmin) fragmentMainActivity;
                break;
            case "managementLock":
                fragmentMainActivity = new FragmentManagementLock();
                fragmentManagementLock = (FragmentManagementLock) fragmentMainActivity;
                break;
            default:
                fragmentMainActivity = new FragmentMainDefault();
                fragmentMainDefault = (FragmentMainDefault) fragmentMainActivity;
                break;
        }
        fragmentTransaction.replace(R.id.frameMainContentFragment, fragmentMainActivity,"tagFragmentSetting");
        fragmentTransaction.commit();
    }


}
