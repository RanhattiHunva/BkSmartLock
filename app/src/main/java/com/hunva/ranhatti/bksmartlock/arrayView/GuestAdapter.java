package com.hunva.ranhatti.bksmartlock.arrayView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hunva.ranhatti.bksmartlock.R;
import com.hunva.ranhatti.bksmartlock.dataControl.GuestPresent;

import java.util.List;

/**
 * Created by ranha on 1/22/2018.
 *
 */

public class GuestAdapter extends BaseAdapter{
    private Context context;
    private int layout;
    private List<GuestPresent> guestPresentsList;

    public GuestAdapter(Context context, int layout, List<GuestPresent> guestPresents) {
        this.context = context;
        this.layout = layout;
        this.guestPresentsList = guestPresents;
    }

    @Override
    public int getCount() {
        return guestPresentsList.size();
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
        TextView userName;
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
            holder.userName = view.findViewById(R.id.textGuestList);
            holder.image = view.findViewById(R.id.imageArrowGuestList);
            view.setTag(holder);
        }
        else {
            holder = (ViewHolder) view.getTag();
        }

        GuestPresent guestPresent = guestPresentsList.get(i);
        holder.userName.setText(guestPresent.getName());
        holder.image.setImageResource(guestPresent.getArrow());

        return view;
    }
}
