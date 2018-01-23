package com.hunva.ranhatti.bksmartlock.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunva.ranhatti.bksmartlock.R;

/**
 * Created by Master on 1/23/2018.
 * Fragment main default
 */

public class FragmentMainDefault extends Fragment{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_default,container,false);
        return view;
    }
}
