package com.hunva.ranhatti.bksmartlock.arrayView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hunva.ranhatti.bksmartlock.R;
import com.hunva.ranhatti.bksmartlock.dataControl.LockHistoryPresent;

import java.util.List;

/**
 * Created by Admin on 12/25/2017.
 * asdasd
 */

public class LockHistoryAdapter extends BaseAdapter
{
    private Context context;
    private int layout;
    private List<LockHistoryPresent> lockHistoryPresentsList;

    public LockHistoryAdapter(Context context, int layout, List<LockHistoryPresent> lockHistoryPresentsList) {
        this.context = context;
        this.layout = layout;
        this.lockHistoryPresentsList = lockHistoryPresentsList;
    }

    @Override
    public int getCount() {
        return lockHistoryPresentsList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    private class ViewHolder{
        TextView time, action;
        ImageView image;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view==null){
            holder = new ViewHolder();
            LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            view=inflater.inflate(layout,null);
            holder.time = view.findViewById(R.id.textTime);
            holder.action = view.findViewById(R.id.textAction);
            holder.image = view.findViewById(R.id.imgaeSatusLock);
            view.setTag(holder);
        }
        else {
            holder = (ViewHolder) view.getTag();
        }

        LockHistoryPresent lockHistoryPresent = lockHistoryPresentsList.get(i);
        holder.image.setImageResource(lockHistoryPresent.getImage());
        holder.time.setText(lockHistoryPresent.getTime());
        holder.action.setText(lockHistoryPresent.getAction());

        return view;
    }
}
