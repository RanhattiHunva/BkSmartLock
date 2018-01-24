package com.hunva.ranhatti.bksmartlock.arrayView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hunva.ranhatti.bksmartlock.R;
import com.hunva.ranhatti.bksmartlock.dataControl.LockPresent;

import java.util.List;

/**
 * Created by Ranhatti Hunva on 11/27/2017.
 * Using to define class adapter for grid view in Manage Lock
 */

public class LockPresentAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private List<LockPresent> lockPresentList;

    public LockPresentAdapter(Context context, int layout, List<LockPresent> lockPresentList) {
        this.context = context;
        this.layout = layout;
        this.lockPresentList = lockPresentList;
    }

    @Override
    public int getCount() {
        return lockPresentList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    //Using view holder
    private class ViewHolder{
        TextView nameLock;
        ImageView imageLock;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view==null){
            holder = new ViewHolder();
            LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout,null);
            holder.imageLock =  view.findViewById(R.id.imageLock);
            holder.nameLock =  view.findViewById(R.id.textNameLock);
            view.setTag(holder);
        }
        else {
            holder =(ViewHolder) view.getTag();
        }

        LockPresent lockPresent = lockPresentList.get(i);
        holder.imageLock.setImageResource(lockPresent.getImage());
        holder.nameLock.setText(lockPresent.getName());

        return view;
    }
}
