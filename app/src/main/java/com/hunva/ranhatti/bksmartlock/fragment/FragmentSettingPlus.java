package com.hunva.ranhatti.bksmartlock.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunva.ranhatti.bksmartlock.R;

/**
 * Created by ranha on 12/27/2017.
 *  Fragment to do action when click tect "Change Information"
 */

public class FragmentSettingPlus extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting_plus,container,false);
    }
}
