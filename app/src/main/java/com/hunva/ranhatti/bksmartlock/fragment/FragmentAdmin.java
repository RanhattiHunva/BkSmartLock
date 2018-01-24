package com.hunva.ranhatti.bksmartlock.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TabHost;

import com.hunva.ranhatti.bksmartlock.R;
import com.hunva.ranhatti.bksmartlock.activity.MainActivity;
import com.hunva.ranhatti.bksmartlock.arrayView.GuestInforAdapter;
import com.hunva.ranhatti.bksmartlock.arrayView.LockHistoryAdapter;
import com.hunva.ranhatti.bksmartlock.dataControl.GuestInforPresent;
import com.hunva.ranhatti.bksmartlock.dataControl.LockHistoryPresent;

import java.util.ArrayList;

/**
 * Created by ranha on 1/23/2018.
 *
 */

public class FragmentAdmin extends Fragment {

    // DEFINE GLOBAL VARIABLE
    ImageButton btnBack;
    ListView lvLockHistory, lvGuest;
    ArrayList<LockHistoryPresent> listLockHistory;
    ArrayList<GuestInforPresent> listGuest;
    LockHistoryAdapter lockHistoryAdapter;
    GuestInforAdapter guestInforAdapter;

    MainActivity activity;

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

        // INITIALIZE THE DATA FOR HISTORY TAB HOST
        lvLockHistory = view.findViewById(R.id.lvHistoryLock);
        listLockHistory = new ArrayList<>();
        listLockHistory.add(new LockHistoryPresent("13:00","unLock by Trinh Van Cong",R.drawable.icons_lock_history_unlocked));
        listLockHistory.add(new LockHistoryPresent("13:01","Lock by Trinh Van Cong",R.drawable.icons_lock_history_locked));
        listLockHistory.add(new LockHistoryPresent("13:15","unLock by Long Thanh Vu",R.drawable.icons_lock_history_unlocked));
        listLockHistory.add(new LockHistoryPresent("13:16","Lock by Long Thanh Vu",R.drawable.icons_lock_history_locked));
        listLockHistory.add(new LockHistoryPresent("13:55","unLock by Long Thanh Vu",R.drawable.icons_lock_history_unlocked));
        listLockHistory.add(new LockHistoryPresent("13:56","Lock by Long Thanh Vu",R.drawable.icons_lock_history_locked));
        listLockHistory.add(new LockHistoryPresent("13:59","unLock by Long Thanh Vu",R.drawable.icons_lock_history_unlocked));
        listLockHistory.add(new LockHistoryPresent("14:00","Lock by Long Thanh Vu",R.drawable.icons_lock_history_locked));

        lockHistoryAdapter = new LockHistoryAdapter(activity, R.layout.element_lock_history_present,listLockHistory);
        lvLockHistory.setAdapter(lockHistoryAdapter);

        lvGuest = view.findViewById(R.id.lvGuest);
        listGuest = new ArrayList<>();
        listGuest.add(new GuestInforPresent("Tran Thai Anh Vu"));
        listGuest.add(new GuestInforPresent("Nguyen Van Long"));
        listGuest.add(new GuestInforPresent("Thai Thuy Van"));
        listGuest.add(new GuestInforPresent("Thai Thuy Dung"));
        listGuest.add(new GuestInforPresent("Thai N"));
        guestInforAdapter = new GuestInforAdapter(activity, R.layout.element_guest_present, listGuest);
        lvGuest.setAdapter(guestInforAdapter);
    }

    private void addEvent() {
    }
}
