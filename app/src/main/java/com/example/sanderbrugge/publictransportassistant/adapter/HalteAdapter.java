package com.example.sanderbrugge.publictransportassistant.adapter;

import android.content.Context;
import android.media.Image;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sanderbrugge.publictransportassistant.R;
import com.example.sanderbrugge.publictransportassistant.model.GetJSON;
import com.example.sanderbrugge.publictransportassistant.model.MainActivity;
import com.example.sanderbrugge.publictransportassistant.model.Stop;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by sanderbrugge on 23/02/17.
 */

public class HalteAdapter extends RecyclerView.Adapter<HalteAdapter.PostViewHolder> {

    private static final String TAG = "HalteAdapter";
    private ArrayList<Stop> bussen;
    private static final String url = "https://sd4u.be/en-GB/ip-project/api/?action=subscribe&subscriptionId=";

    public HalteAdapter(ArrayList<Stop> bussen) {
        this.bussen = bussen;
        Log.i(TAG, "adapter heeft: " + bussen.size() + " items");
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);
        // v.setOnClickListener(MainActivity.mainOnClickListener);
        return new PostViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {

        Log.i(TAG, "in onBindViewHolder");
        Log.i(TAG, String.format("%d", bussen.get(position).getVehicleId()));

        String nummer = String.format("%d", bussen.get(position).getVehicleId());
        TextView lijnnr = holder.lijnnr;
        TextView omschrijving = holder.omschrijving;
        ImageView ivPassed = holder.ivPassed;
        Context context = holder.ivPassed.getContext();
        if (bussen.get(position).getArrived_on() != null) {
            Picasso.with(context).load(R.drawable.red_ball).into(ivPassed);
            holder.isPassed = true;
        }
        Log.i(TAG,"test "+ String.valueOf(holder.isPassed));
        holder.subscriptionId = bussen.get(position).getSubscriptionId();

        lijnnr.setText(nummer);
        omschrijving.setText(bussen.get(position).getStopName());

    }

    @Override
    public int getItemCount() {
        return bussen.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        public int subscriptionId;
        public boolean isPassed;

        @BindView(R.id.tvNummer)
        public TextView lijnnr;

        @BindView(R.id.tvLijnBeschrijving)
        public TextView omschrijving;

        @BindView(R.id.ivPassed)
        ImageView ivPassed;

        @BindView(R.id.radioHalte)
        RadioButton rb;

        public PostViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            Log.i(TAG, "in post viewholder");
            Log.i(TAG,String.valueOf(isPassed));
            if (!isPassed && !rb.isChecked())
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PostViewHolder outerClass = new PostViewHolder(v);
                        outerClass.new subscriptionIdAsync().execute(String.valueOf(subscriptionId));
                        rb.setChecked(true);
                    }
                });
        }


        private class subscriptionIdAsync extends AsyncTask<String, Void, GetJSON> {

            private GetJSON res;
            private OkHttpClient client = new OkHttpClient();

            @Override
            protected GetJSON doInBackground(String... params) {
                String subscriptionId = params[0];

                Request request = new Request.Builder()
                        .url(url + subscriptionId + "&notificationRegistrationId=1")
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    Log.i(TAG, response.toString());
                    String responseJson = response.body().string();
                    Log.i(TAG, responseJson);


                    res = new Gson().fromJson(responseJson, GetJSON.class);

                    Log.i(TAG, "winner winner ");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return res;

            }


        }
    }

}
