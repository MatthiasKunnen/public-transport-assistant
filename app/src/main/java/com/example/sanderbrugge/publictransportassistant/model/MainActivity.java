package com.example.sanderbrugge.publictransportassistant.model;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sanderbrugge.publictransportassistant.R;
import com.example.sanderbrugge.publictransportassistant.adapter.HalteAdapter;
import com.example.sanderbrugge.publictransportassistant.persistentie.BusMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();



    public static final String MIME_TEXT_PLAIN = "text/plain";


    private NfcAdapter mNfcAdapter;
    private LinearLayoutManager llm;
    private RecyclerView rvHaltes;
    private BusMapper bm;
    private GetJSON data;
    private HalteAdapter adapter;
    private int clickedIndex;
    OnPostSelectedListener mCallback;
    private ArrayList<Stop> stops;



    public interface OnPostSelectedListener {
        void onArticleSelected(int position,ArrayList<Stop> Stopsen);
        ArrayList<Stop> getStopsen();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bm = new BusMapper();
       //data = bm.getBussen();
        stops = new ArrayList<>();
        rvHaltes = (RecyclerView) findViewById(R.id.rvHaltes);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;

        }

        if (!mNfcAdapter.isEnabled()) {
            Toast.makeText(this, "NFC is disabled",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,"NFC is enabled",Toast.LENGTH_SHORT).show();
        }

        handleIntent(getIntent());
        new OkHttpHandler().execute("https://sd4u.be/en-GB/ip-project/api/?action=get_stops&vehicleId=1");



        //rvHaltes.addItemDecoration(new DividerItemDecoration(this));

    }
    private void setStops(ArrayList<Stop> stops){
        this.stops = stops;
    }

    private void refreshUI(){
        llm  = new LinearLayoutManager(getBaseContext());
        rvHaltes.setLayoutManager(llm);
        adapter = new HalteAdapter(stops);
        rvHaltes.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();

        /**
         * It's important, that the activity is in the foreground (resumed). Otherwise
         * an IllegalStateException is thrown.
         */
        setupForegroundDispatch((Activity) this, mNfcAdapter);
    }

    @Override
    protected void onPause() {
        /**
         * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
         */
        stopForegroundDispatch(this, mNfcAdapter);

        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        /**
         * This method gets called, when a new Intent gets associated with the current activity instance.
         * Instead of creating a new activity, onNewIntent will be called. For more information have a look
         * at the documentation.
         *
         * In our case this method gets called, when the user attaches a Tag to the device.
         */
        Log.i(TAG,"in onNewIntent");
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            String type = intent.getType();
            Log.i(TAG,type);
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

            // In case we would still use the Tech Discovered Intent
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            String searchedTech = Ndef.class.getName();

            for (String tech : techList) {
                if (searchedTech.equals(tech)) {
                    Log.i(TAG,"tech disc " + tech.toString());
                    new NdefReaderTask().execute(tag);
                    break;
                }
            }
        }
    }
    private class MainOnclickListener implements View.OnClickListener{

        private final Context context;

        public MainOnclickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            clickedIndex = rvHaltes.getChildAdapterPosition(v);
            Log.i(TAG,"lengte array: " + stops.size());
            ArrayList<Stop> posten = data.getData().getStops();
            Log.i(TAG,"Clicked on : "+clickedIndex);
            mCallback.onArticleSelected(clickedIndex,stops);

        }
    }

    /**
     * @param activity The corresponding {@link Activity} requesting the foreground dispatch.
     * @param adapter  The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        Log.i(TAG,"in setupForegroundDispatch");
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        adapter.enableForegroundDispatch(activity, pendingIntent, null, null);
    }


    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }

    private class NdefReaderTask extends AsyncTask<Tag, Void, String> {

        @Override
        protected String doInBackground(Tag... params) {
            Tag tag = params[0];
            Log.i(TAG,"in asycn doInBack");
            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                // NDEF is not supported by this Tag.
                Log.i("ndef", "== null");
                return null;
            }

            NdefMessage ndefMessage = ndef.getCachedNdefMessage();

            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                    try {
                        return readText(ndefRecord);
                    } catch (UnsupportedEncodingException e) {
                        Log.e(TAG, "Unsupported Encoding", e);
                    }
                }
            }

            return null;
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException {

            byte[] payload = record.getPayload();

            // Get the Text Encoding
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

            // Get the Language Code
            int languageCodeLength = payload[0] & 0063;

            // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            // e.g. "en"

            // Get the Text
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);

        }

        @Override
        protected void onPostExecute (String result){
            Log.i(TAG,"in async on post");
            if (result != null) {

                Log.i("onPost",result);

            }
        }
    }

    private class OkHttpHandler extends AsyncTask<String, Void, GetJSON> {
        GetJSON res;
        OkHttpClient client = new OkHttpClient();
        private ArrayList<Stop> Stopsen = new ArrayList<>();
        @Override
        protected GetJSON doInBackground(String... params) {


            String url = (params[0]);
            try {
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                Response response = client.newCall(request).execute();
                Log.i("DoInBG",response.toString());

                String responseJson = response.body().string();
                Log.i(TAG,responseJson);


                res = new Gson().fromJson(responseJson,GetJSON.class);

                Log.i(TAG, res.getData().getStops().get(0).getStopName());


            } catch (Exception e) {
                e.printStackTrace();
            }
            return res;
        }

        @Override
        protected void onPostExecute(GetJSON res) {
            super.onPostExecute(res);

            Log.i(TAG,"in onPost");
            Log.i(TAG,res.getData().getStops().get(0).getStopName());

            try {
                if (res != null) {
                    Log.i(TAG,"--------------------------------");
                   Log.i(TAG,"succes!");
                    setStops(res.getData().getStops());
                    Log.i(TAG,stops.get(0).getStopName());
                    refreshUI();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG,"no succes :(");
            }
        }
    }
}
