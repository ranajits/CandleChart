package ranjit.com.chartapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import ranjit.com.chartapplication.Compare.Currency;
import ranjit.com.chartapplication.Compare.CustomArray;
import ranjit.com.chartapplication.Compare.HttpHandler;

public class CompareActivity extends AppCompatActivity {

    //private List<Currency> currencyList = new ArrayList<>();
    private RecyclerView mMainRecycler;
    private Button mRetryBtn;
    private TextView mHeaderText;

    private CurrencyAdapter mAdapter;
    private ProgressDialog mProgresss;

    String jsonURL = null;
    private String mSelectedItem = "USD";
    private String [] mCurrencies = {"ETH","LTC", "XMR","XRP"};
    private ArrayList<Currency> currencyList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


       // jsonURL = "https://min-api.cryptocompare.com/data/pricemulti?fsyms=BTC,USD&tsyms="+builder.toString();
        String currecies= "ETH,LTC,XMR,XRP";

        jsonURL = "https://min-api.cryptocompare.com/data/pricemulti?fsyms="+currecies+"&tsyms=BTC,USD";
        Log.d("URL: "," "+jsonURL);
        currencyList= new ArrayList<>();
        mAdapter = new CurrencyAdapter(currencyList);

        new GetCurrencies().execute();

        if (jsonURL!=null){

            final Handler ha=new Handler();
            ha.postDelayed(new Runnable() {

                @Override
                public void run() {
                    refresh();
                    ha.postDelayed(this, 5000);
                }
            }, 5000);

        }

    }




    public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.MyViewHolder> {


        private ArrayList<Currency> currencyLists;

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView etheriumText, btcCurrency, currText, cardHeader;
            public ImageView removeBtn;

            public MyViewHolder(View view) {
                super(view);
                view.setOnClickListener(this);
                etheriumText = view.findViewById(R.id.eth_text);
                btcCurrency = view.findViewById(R.id.btc_text);
                cardHeader = view.findViewById(R.id.card_header);
                currText = view.findViewById(R.id.card_header2);
                removeBtn = view.findViewById(R.id.remove_btn);
            }

            @Override
            public void onClick(View view) {
                final int selectedItemPosition = mMainRecycler.getChildPosition(view);
            }
        }


        public CurrencyAdapter(ArrayList<Currency> currencyLists) {
            this.currencyLists = currencyLists;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.currency_card, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            Currency cs = currencyLists.get(position);
            holder.etheriumText.setText("USD:\n "+cs.getEthValue());
            holder.btcCurrency.setText("BTC:\n "+cs.getBtcValue());
            holder.currText.setText(cs.getTitle());

            holder.removeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currencyList.remove(position);
                    mAdapter.notifyDataSetChanged();

                }
            });
        }

        @Override
        public int getItemCount() {
            return currencyLists.size();
        }

    }

    private class GetCurrencies extends AsyncTask<Void, Void, Void> {

        String jsonStr = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            currencyList= new ArrayList<>();
            mProgresss = new ProgressDialog(CompareActivity.this);
            mProgresss.setCanceledOnTouchOutside(false);
            mProgresss.setMessage("Loading data from Server...");
            mProgresss.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler handler = new HttpHandler();

            jsonStr = handler.makeServiceCall(jsonURL);

            if (jsonStr != null) {
                Log.d("jsonStr: "," "+jsonStr);
                try {
                    for (int i=0 ; i<mCurrencies.length; i++) {
                        JSONObject ethObj = new JSONObject(jsonStr).getJSONObject(mCurrencies[i]);
                        Double btcVal = ethObj.getDouble("BTC");
                        Double ethVal = ethObj.getDouble("USD");
                        Currency sCurrency = new Currency(mCurrencies[i], btcVal, ethVal);
                        currencyList.add(sCurrency);
                    }

                } catch (final JSONException e) {

                e.printStackTrace();
                }
                mProgresss.dismiss();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (jsonStr!=null){
                mProgresss.dismiss();
                setContentView(R.layout.activity_compare);

                mHeaderText = (TextView) findViewById(R.id.headerText);
                mMainRecycler = (RecyclerView) findViewById(R.id.main_recyclerview);
                mMainRecycler.setItemAnimator(new DefaultItemAnimator());
                mMainRecycler.setLayoutManager(new LinearLayoutManager(CompareActivity.this));

                mHeaderText.setVisibility(View.VISIBLE);
                mAdapter = new CurrencyAdapter(currencyList);
                mMainRecycler.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }else {
                mProgresss.dismiss();
                Toast.makeText(CompareActivity.this, "Failed to connect to the server. Check your internet connection.", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private class RefreshCurrencies extends AsyncTask<Void, Void, Void> {

        String jsonStr = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            currencyList= new ArrayList<>();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler handler = new HttpHandler();

            jsonStr = handler.makeServiceCall(jsonURL);
            Log.d("URL: "," "+jsonURL+"\n"+jsonStr);
            if (jsonStr != null) {

                try {
                    for (int i=0 ; i<mCurrencies.length; i++) {
                        JSONObject ethObj = new JSONObject(jsonStr).getJSONObject(mCurrencies[i]);
                        Double btcVal = ethObj.getDouble("BTC");
                        Double ethVal = ethObj.getDouble("USD");
                        Currency sCurrency = new Currency(mCurrencies[i], btcVal, ethVal);
                        currencyList.add(sCurrency);
                    }

                } catch (final JSONException e) {
                    e.printStackTrace();
                }



                mProgresss.dismiss();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (jsonStr!=null){

                mMainRecycler.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                mProgresss.dismiss();
                mAdapter = new CurrencyAdapter(currencyList);
                mMainRecycler.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();

            }else {

            }
        }
    }

    private void refresh(){
        new RefreshCurrencies().execute();
    }

    public static ArrayList getArrayVal( Context ctx){
        SharedPreferences shref = ctx.getSharedPreferences("currArrayValues", Context.MODE_PRIVATE);

        ArrayList<CustomArray>   rArray;
        Gson gson;
        gson = new GsonBuilder().create();
        String response = shref.getString("currArray", null);

        if (response!=null){

            Type type = new TypeToken<ArrayList<CustomArray>>(){}.getType();
            rArray = gson.fromJson(response, type);

        }else {

            rArray = new ArrayList<>();

        }
        return rArray;
    }

    public static void storeArrayVal(ArrayList<CustomArray> inArrayList, Context context){
        SharedPreferences shref = context.getSharedPreferences("currArrayValues", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = shref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(inArrayList);
        prefEditor.putString("currArray", json);
        prefEditor.commit();
    }
}
