package com.example.theweathear.support;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theweathear.R;
import com.example.theweathear.model.HourlyWeather;

import java.util.List;

public class ItemViewHourlyAdapter extends RecyclerView.Adapter<ItemViewHourlyAdapter.ItemViewhourlyHoder> {
    private final Context mContext;
    private List<HourlyWeather> mlist;

    public ItemViewHourlyAdapter(Context mContext)  {
        this.mContext = mContext;
    }
    public void setData(List<HourlyWeather> list){
        this.mlist = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemViewhourlyHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hourly, parent, false);
        return new ItemViewhourlyHoder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewhourlyHoder holder, int position) {
        HourlyWeather hourlyWeather = mlist.get(position);
        if (hourlyWeather == null){
            return;
        }
        holder.icon.setImageResource(ItemViewAdapter.seticon(hourlyWeather.getImgIcon()));
        holder.time.setText(ItemViewAdapter.covertUnixToHour(hourlyWeather.getTime()));
        holder.templ.setText(ItemViewAdapter.df.format(hourlyWeather.getTemlpcurrent()) + KeyName.doc);
    }

    @Override
    public int getItemCount() {
        if (mlist != null) {
            return mlist.size();
        }
        return 0;
    }

    public class ItemViewhourlyHoder extends RecyclerView.ViewHolder {
        private TextView time, templ;
        private ImageView icon;
        public ItemViewhourlyHoder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            templ = itemView.findViewById(R.id.temlpHourly);
            icon = itemView.findViewById(R.id.imgWeatherHourly);
        }
    }

}
