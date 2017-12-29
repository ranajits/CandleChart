package ranjit.com.chartapplication;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ranjit.com.chartapplication.Compare.Currency;
import ranjit.com.chartapplication.Compare.HttpHandler;
import ranjit.com.chartapplication.CryptoCurrency.CryptoPresentrer;
import ranjit.com.chartapplication.CryptoCurrency.ICryptoDataActivity;
import ranjit.com.chartapplication.customViews.CustomMarkerImageView;
import ranjit.com.chartapplication.customViews.CustomMarkerView;
import ranjit.com.chartapplication.quandle.OHLC;

public class CryptoDataActivity extends AppCompatActivity implements ICryptoDataActivity {

    CombinedChart mChart;
    ContentLoadingProgressBar progressBar;
    CryptoPresentrer cryptoPresenter;
    String periodContext="";
    String collapse= "daily";
    String period= "2016-10-27";
    CustomMarkerView customMarkerView;
    CustomMarkerImageView customMarkerImgView;
    TextView txtPeriod;
    Calendar cal;
    SimpleDateFormat sdf;
    String currency="ETH";
    String currency1="BTC";
    String per="30";
    private ArrayList<String> currencyList, currencyList2;
    private Spinner mCurSpinner, mCurSpinner1;
    private String [] mCurrencies = {"ETH","LTC", "XMR","XRP"};
    private String [] mCurrencies2 = {"BTC","USD"};

    TextView txtcurrecyRates;
    String jsonURL = null;
    Currency sCurrency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_crypto_data);

        progressBar= findViewById(R.id.progressBar);
        mCurSpinner = (Spinner) findViewById(R.id.currency_spinner);
        mCurSpinner1 = (Spinner) findViewById(R.id.currency_spinner2);
        txtcurrecyRates= findViewById(R.id.txtcurrecyRates);
        mCurSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
        mCurSpinner1.setOnItemSelectedListener(new CustomOnItemSelectedListener2());


        currencyList = new ArrayList<>();

        for (int i = 0; i < mCurrencies.length; i++){

            String title = mCurrencies[i];
            currencyList.add(title);

        }

        currencyList2 = new ArrayList<>();

        for (int i = 0; i < mCurrencies2.length; i++){

            String title = mCurrencies2[i];
            currencyList2.add(title);

        }

        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(CryptoDataActivity.this, android.R.layout.simple_spinner_item, currencyList);
        myAdapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        mCurSpinner.setAdapter(myAdapter);

        ArrayAdapter<String> myAdapter2 = new ArrayAdapter<>(CryptoDataActivity.this, android.R.layout.simple_spinner_item, currencyList2);
        myAdapter2.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        mCurSpinner1.setAdapter(myAdapter2);


        mChart = (CombinedChart) findViewById(R.id.chart1);
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setMaxVisibleValueCount(10);
        mChart.setPinchZoom(false);

        mChart.setDrawGridBackground(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setLabelCount(7, true);
        leftAxis.setDrawGridLines(true);
        leftAxis.setDrawAxisLine(true);
        leftAxis.setAxisMinimum(-0.01f);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setLabelCount(5, false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMaximum(12000000);
        rightAxis.setDrawAxisLine(false);
        rightAxis.setAxisMinimum(0);
        mChart.setNoDataText("");



        cryptoPresenter= new CryptoPresentrer(this) ;

        if(getIntent().hasExtra("collapse")){
            collapse= getIntent().getStringExtra("collapse");
        }

        if(getIntent().hasExtra("period")){
            period=  getIntent().getStringExtra("period");
        }else {
            period= getPeriod(90);
        }


        txtPeriod = findViewById(R.id.txtPeriod);

        sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        cal= Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH));

        txtPeriod.setText(period+" to "+sdf.format(cal.getTime()));
        // presenter.showOHLC("k", "k", collapse, period);
        cryptoPresenter.showCryptoOHLC( "k", currency, "30","CCCAGG");
        customMarkerView = new CustomMarkerView(getApplicationContext(), R.layout.marker);
        customMarkerImgView = new CustomMarkerImageView(getApplicationContext(), R.layout.marker_img);

        mChart.setMarkerView(customMarkerView);
        mChart.setTouchEnabled(true);


        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                mChart.setRenderer(null);
                mChart.setDrawMarkers(true);
                mChart.highlightValue(h);

                CandleDataSet cData = (CandleDataSet) mChart.getCandleData()
                        .getDataSetForEntry(e);

                BarDataSet bData = (BarDataSet) mChart.getBarData()
                        .getDataSetForEntry(e);

                LineDataSet lData = (LineDataSet) mChart.getLineData()
                        .getDataSetForEntry(e);

                if(cData!=null){
                    customMarkerView.setTvContent( cData.getEntryForXValue(e.getX(), e.getY()));
                    mChart.setRenderer(null);
                    mChart.setMarker(customMarkerView);
                }else if(bData!=null){

                    // bData.geten
                    //BarEntry b= bData.getEntryForXValue(e.getX(), e.getY());
                    // b.getRanges().toString();
                    // customMarkerView.setbarDataContent(b.getRanges().toString());
                    customMarkerView.setTvContent(e );
                    mChart.setRenderer(null);
                    mChart.setMarker(customMarkerView);



                }else if(lData!=null){
                    mChart.setMarker(customMarkerImgView);

                }

            }
            @Override
            public void onNothingSelected() {

            }
        });


        //jsonURL = "https://min-api.cryptocompare.com/data/pricemulti?fsyms=XMR&tsyms=BTC,USD";
        jsonURL = "https://min-api.cryptocompare.com/data/pricemulti?fsyms="+currency+"&tsyms=BTC,USD";
        Log.d("URL: "," "+jsonURL);
        /*new GetCurrencies().execute();

        if (jsonURL!=null){

            final Handler ha=new Handler();
            ha.postDelayed(new Runnable() {

                @Override
                public void run() {
                    refresh();
                    ha.postDelayed(this, 5000);
                }
            }, 5000);

        }*/

        Description description= new Description();
        description.setText("");
        mChart.setDescription(description);
        mChart.getViewPortHandler().setMaximumScaleX(2f);
        mChart.getViewPortHandler().setMaximumScaleY(2f);
    }

    public void showPopup(View v){

        PopupMenu popup = new PopupMenu(CryptoDataActivity.this, v);
        popup.getMenuInflater().inflate(R.menu.data_period, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                periodContext= item.getTitle().toString();

                String  pr= "";

                /*if(item.getItemId()==R.id.day){
                    pr=     getPeriod(1);
                }else if(item.getItemId()==R.id.week){
                    pr=      getPeriod(7);
                }else*/ if(item.getItemId()==R.id.month){
                    per="30";
                    pr=    getPeriod(30);
                }else if(item.getItemId()==R.id.quarter){
                    per="90";
                    pr=    getPeriod(90);
                }else if(item.getItemId()==R.id.annual){
                    per="365";
                    pr=     getPeriod(365);
                }else {
                    pr=    getPeriod(30);
                    per="30";

                }


                txtPeriod.setText(pr+" to "+sdf.format(cal.getTime()));
                cryptoPresenter.showCryptoOHLC(currency1, currency, per, "CCCAGG");
                mChart.invalidate();
                return true;
            }
        });

        popup.show();


    }

    public void showCurrencyPopup(View v){

        PopupMenu popup = new PopupMenu(CryptoDataActivity.this, v);
        popup.getMenuInflater().inflate(R.menu.currency, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                periodContext= item.getTitle().toString();

                String  pr= "";

                if(item.getItemId()==R.id.xrp){
                    currency= "XRP";

                }else if(item.getItemId()==R.id.xmr){
                    currency= "XMR";
                }else if(item.getItemId()==R.id.ltc){
                    currency= "LTC";
                }else if(item.getItemId()==R.id.eth){
                    currency= "ETH";
                }else if(item.getItemId()==R.id.btc){
                    currency= "BTC";
                }else  {
                    currency= "BTC";
                }


                txtPeriod.setText(pr+" to "+sdf.format(cal.getTime()));
                cryptoPresenter.showCryptoOHLC( currency1, currency, per, "CCCAGG");
                mChart.invalidate();
                return true;
            }
        });

        popup.show();


    }



    private String  getPeriod(int days) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH)-days);
        Log.d(" DATE:::::::::: "," "+ sdf.format(cal.getTime())+"  "+days);
        return ""+ sdf.format(cal.getTime());

    }

    ArrayList<CandleEntry> yVals1;
    ArrayList<String> labels;
    ArrayList<Float> avgs;
    ArrayList<Float> avgsDataList;


    @Override
    public void showCryptoOHLC(List<OHLC> list) {
        if(list!=null) {

            YAxis rightAxis = mChart.getAxisRight();
            rightAxis.setLabelCount(5, false);
            rightAxis.setDrawGridLines(false);
            rightAxis.setAxisMinimum(0);
            rightAxis.setDrawAxisLine(false);
            if (currency1.equals("USD")) {
                if (currency.equals("XRP")) {
                    rightAxis.setAxisMaximum(120000000);
                } else {
                    rightAxis.setAxisMaximum(12000000);
                }
            } else {
                rightAxis.setAxisMaximum(800000);
            }


            mChart.invalidate();
            mChart.clear();
            if (mChart.getCandleData() != null)
                mChart.getCandleData().clearValues();
            yVals1 = new ArrayList<CandleEntry>();
            labels = new ArrayList<String>();
            avgs = new ArrayList<>();
            avgsDataList = new ArrayList<>();
            labels.clear();
            yVals1.clear();

            if (list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    yVals1.add(new CandleEntry(
                            new Long(i),
                            (float) list.get(i).getHigh(),
                            (float) list.get(i).getLow(),
                            (float) list.get(i).getOpen(),
                            (float) list.get(i).getClose()
                    ));
                    labels.add(list.get(i).getDate());
                }


                float low, open, close, high;
                for (int i = 0; i < list.size(); i++) {
                    open = (float) list.get(i).getOpen();
                    high = (float) list.get(i).getHigh();
                    low = (float) list.get(i).getLow();
                    close = (float) list.get(i).getClose();
                    avgs.add((open + high + low + close) / 4);
                }

                Log.d("CandleActivity", " size: " + labels.size() + "  " + list.size());

                CandleDataSet set1 = new CandleDataSet(yVals1, "OHLC");
                CandleData data = new CandleData(set1);
                set1.setAxisDependency(YAxis.AxisDependency.LEFT);
                set1.setShadowColor(Color.GREEN);
                set1.setShadowWidth(0.7f);
                set1.setHighlightEnabled(true);
                set1.setDrawHighlightIndicators(true);
                set1.setDecreasingColor(Color.RED);
                set1.setDecreasingPaintStyle(Paint.Style.FILL);
                set1.setIncreasingColor(Color.rgb(122, 242, 84));
                set1.setIncreasingPaintStyle(Paint.Style.STROKE);
                set1.setNeutralColor(Color.BLUE);

                set1.setValueTextColor(Color.RED);
                set1.setShowCandleBar(true);
                set1.setHighlightLineWidth(2f);
                set1.setDrawVerticalHighlightIndicator(true);
                XAxis xAxis = mChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setDrawGridLines(true);

                CombinedData combinedData = new CombinedData();
                combinedData.setData(generateLineData());
                combinedData.setData(data);
                combinedData.setData(setBarData(list));
                mChart.setData(combinedData);

                xAxis.setValueFormatter(new IAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        return getLable(labels.get((int) value));
                    }
                });

                mChart.animateXY(3000, 3000);
                mChart.getData().setHighlightEnabled(true);
                mChart.getCandleData().setHighlightEnabled(true);
                mChart.getBarData().setHighlightEnabled(true);
                mChart.getLineData().setHighlightEnabled(true);

                mChart.invalidate();
            }
        }else {
            mChart.clear();

            mChart.invalidate();

            Toast.makeText(CryptoDataActivity.this, "No Data Found", Toast.LENGTH_SHORT).show();
        }
    }

    private String getLable(String seconds){

        String date= "";

        try {
            long currentDateTime = (Long.parseLong(seconds))*1000;
            //creating Date from millisecond
            Date currentDate = new Date(currentDateTime);
            DateFormat df = new SimpleDateFormat("dd-MMM-yy");
            date=df.format(currentDate);
        }catch (Exception e){
            e.printStackTrace();
        }
        return date;
    }


    private BarData setBarData(List<OHLC> list){

        float start = 0f;

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = (int) start; i < list.size(); i++) {
            yVals1.add(new BarEntry(i, (float) list.get(i).getVolume()));
        }

        BarDataSet set1=null;

        set1 = new BarDataSet(yVals1, "Volume");
        set1.setAxisDependency(YAxis.AxisDependency.RIGHT);

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        data.setValueTextSize(10f);
        data.setBarWidth(0.9f);

        return data;
    }

    private LineData generateLineData() {
        LineData d = new LineData();
        ArrayList<Entry> entries = new ArrayList<Entry>();

        int tempCount=0;
        float tempCounts=0;

        for (int index = 0; index < avgs.size(); index++){

            tempCounts= tempCounts+avgs.get(index);
            if(tempCount==4){
                tempCount=0;
                avgsDataList.add((tempCounts/5));
                tempCounts=0;
            }else {
                tempCount= tempCount+1;
            }
            Log.d(" ","tempCount "+ tempCount);
        }

        for (int index = 0; index < avgsDataList.size(); index++){
            if(index==0){
                entries.add(new Entry((3f), avgsDataList.get(index)));
            }else {
                entries.add(new Entry((((index  * 5))+(3f)), avgsDataList.get(index)));
            }
        }

        LineDataSet set = new LineDataSet(entries, "SMA");
        set.setColor(Color.rgb(240, 238, 70));
        set.setLineWidth(2.5f);
        set.setFillColor(Color.rgb(240, 238, 70));
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);

        //set.setHighlightEnabled(true);
        set.setDrawVerticalHighlightIndicator(true);
        set.setDrawHorizontalHighlightIndicator(true);
        set.setCircleColor(Color.rgb(240, 238, 70));
        set.setCircleRadius(5f);
        set.setCircleHoleRadius(4f);

        //    set.setHighLightColor(R.color.colorPrimaryDark);
        set.setDrawHighlightIndicators(true);
        set.setValueTextColor(Color.rgb(240, 238, 70));
        set.enableDashedHighlightLine(5f, 5f, 10f);

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.addDataSet(set);

        return d;
    }



    @Override
    public void showProgresBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void stopProgresBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showExangeList(Map<String, ArrayList<String>> list) {

    }


    private class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            currency = currencyList.get(pos);
            cryptoPresenter.showCryptoOHLC( currency1, currency, per, "CCCAGG");
            mChart.invalidate();
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }

    }


    private class CustomOnItemSelectedListener2 implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            currency1 = currencyList2.get(pos);
            cryptoPresenter.showCryptoOHLC( currency1, currency, per, "CCCAGG");
            mChart.invalidate();
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }

    }


    private class GetCurrencies extends AsyncTask<Void, Void, Void> {

        String jsonStr = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler handler = new HttpHandler();

            jsonStr = handler.makeServiceCall(jsonURL);

            if (jsonStr != null) {
                try {
                    JSONObject ethObj = new JSONObject(jsonStr).getJSONObject(currency);
                    Double btcVal = ethObj.getDouble("BTC");
                    Double ethVal = ethObj.getDouble("USD");

                   sCurrency= new Currency(currency,btcVal , ethVal);



                } catch (final JSONException e) {
                    e.printStackTrace();
                    sCurrency=null;
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (jsonStr!=null){

                if(sCurrency!=null)
                txtcurrecyRates.setText("BTC: "+ sCurrency.getBtcValue()+"\nUSD: "+sCurrency.getEthValue());



            }else {
            }

        }
    }

    private void refresh(){
        new RefreshCurrencies().execute();
    }


    private class RefreshCurrencies extends AsyncTask<Void, Void, Void> {

        String jsonStr = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            jsonURL = "https://min-api.cryptocompare.com/data/pricemulti?fsyms="+currency+"&tsyms=BTC,USD";
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler handler = new HttpHandler();

            jsonStr = handler.makeServiceCall(jsonURL);
            Log.d("URL: "," "+jsonURL+"\n"+jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject ethObj = new JSONObject(jsonStr).getJSONObject(currency);

                    Double btcVal = ethObj.getDouble("BTC");
                    Double ethVal = ethObj.getDouble("USD");
                    sCurrency = new Currency(currency,btcVal , ethVal);

                } catch (final JSONException e) {

                }

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (jsonStr!=null){
                txtcurrecyRates.setText("BTC: "+ sCurrency.getBtcValue()+"\nUSD: "+sCurrency.getEthValue());
            }else {

            }
        }
    }

}



