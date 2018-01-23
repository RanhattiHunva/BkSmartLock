package com.hunva.ranhatti.bksmartlock.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hunva.ranhatti.bksmartlock.R;
import com.hunva.ranhatti.bksmartlock.fragment.FragmentMainDefault;

public class MainActivity extends AppCompatActivity {


    // Fragment
    FragmentMainDefault fragmentMainDefault;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        register();
    }

    private void register() {
        changeMainActivityFragment("default");
    }

    private void changeMainActivityFragment(String fragmentTag){

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Fragment fragmentMainActivity;

        switch (fragmentTag) {
            case "default":
                fragmentMainActivity = new FragmentMainDefault();
                fragmentMainDefault = (FragmentMainDefault) fragmentMainActivity;
                break;
            case "user":
                fragmentMainActivity = new FragmentMainDefault();
                fragmentMainDefault = (FragmentMainDefault) fragmentMainActivity;
                break;

            case "admin":
                fragmentMainActivity = new FragmentMainDefault();
                fragmentMainDefault = (FragmentMainDefault) fragmentMainActivity;
                break;
            case "managementLock":
                fragmentMainActivity = new FragmentMainDefault();
                fragmentMainDefault = (FragmentMainDefault) fragmentMainActivity;
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
