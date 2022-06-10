package com.example.theweathear.support;

import static com.example.theweathear.support.ItemViewAdapter.baseURL;
import static com.example.theweathear.support.ItemViewAdapter.df;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.theweathear.R;
import com.example.theweathear.model.City;
import com.example.theweathear.view.ViewADD;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ItemCityAdapter extends RecyclerView.Adapter<ItemCityAdapter.ItemCityHoder> {
    private final Context mContext;
    private static String comlpURL;
    private List<City> mlist;
    private final IsetOnClickListener mOnClickListener;

    public ItemCityAdapter(Context mContext, IsetOnClickListener mOnClickListener) {
        this.mContext = mContext;
        this.mOnClickListener = mOnClickListener;
    }

    public void setData(List<City> list) {
        this.mlist = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemCityHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_city, parent, false);
        return new ItemCityAdapter.ItemCityHoder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemCityHoder holder, int position) {
        City city = mlist.get(position);
        if (city == null) {
            return;
        }
        int mPosition = position;
        if (ItemViewAdapter.checknull(city)) {
            comlpURL = baseURL + "lat=" + city.getLat().replaceAll(" ", "")
                    + "&lon=" + city.getLon().replaceAll(" ", "")
                    + KeyName.apiKey + KeyName.metric + KeyName.lang;
        } else {
            comlpURL = baseURL + "q=" + city.getName().replaceAll(" ", "")
                    + KeyName.apiKey + KeyName.metric + KeyName.lang;

        }
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        StringRequest stringRequest1 = new StringRequest(Request.Method.GET, comlpURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObjectToday = new JSONObject(response);
                    //
                    JSONArray jsonArrayWeather = jsonObjectToday.getJSONArray(KeyName.weather);
                    JSONObject jsonObjectweather = jsonArrayWeather.getJSONObject(0);
                    //
                    String icon = jsonObjectweather.getString(KeyName.icon);
                    String status = jsonObjectweather.getString(KeyName.description);
                    //
                    JSONObject jsonObjectMain = jsonObjectToday.getJSONObject(KeyName.main);
                    Double temp = jsonObjectMain.getDouble(KeyName.temp);
                    //
                    holder.imgWeather.setImageResource(ItemViewAdapter.seticon(icon));
                    holder.status.setText(status);
                    holder.nameCity.setText(city.getName());
                    holder.temp.setText(df.format(temp) + KeyName.doc);
                    holder.layout.setBackgroundResource(ItemViewAdapter.setbackground(icon));

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("TAG", e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.toString());

            }
        });
        requestQueue.add(stringRequest1);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnClickListener.onClickItemListener(mPosition);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (mlist != null) {
            return mlist.size();
        }
        return 0;
    }


    public static class ItemCityHoder extends RecyclerView.ViewHolder {
        private final TextView nameCity, temp, status;
        private final ImageView imgWeather;
        private final RelativeLayout layout;


        public ItemCityHoder(@NonNull View itemView) {
            super(itemView);
            nameCity = itemView.findViewById(R.id.nameCity);
            temp = itemView.findViewById(R.id.temlpCity);
            status = itemView.findViewById(R.id.statusCity);
            imgWeather = itemView.findViewById(R.id.imgWeatherCity);
            layout = itemView.findViewById(R.id.backgrounditemCity);
        }
    }


}
