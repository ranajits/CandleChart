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

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import ranjit.com.chartapplication.Compare.Currency;
import ranjit.com.chartapplication.Compare.HttpHandler;
import ranjit.com.chartapplication.CryptoCurrency.CryptoPresentrer;
import ranjit.com.chartapplication.CryptoCurrency.ICryptoDataActivity;
import ranjit.com.chartapplication.combined.CoupleChartGestureListener;
import ranjit.com.chartapplication.customViews.CustomMarkerImageView;
import ranjit.com.chartapplication.customViews.CustomMarkerView;
import ranjit.com.chartapplication.customViews.CustomMarkerViewForBar;
import ranjit.com.chartapplication.quandle.OHLC;

public class CandleBarSeperateActivity extends AppCompatActivity implements ICryptoDataActivity {

    CombinedChart mChart;
    BarChart barChart;
    ContentLoadingProgressBar progressBar;
    CryptoPresentrer cryptoPresenter;
    String periodContext="";
    String collapse= "daily";
    String period= "2016-10-27";
    CustomMarkerView customMarkerView;
    CustomMarkerViewForBar customMarkerViewForBar;
    CustomMarkerImageView customMarkerImgView;
    TextView txtPeriod;
    Calendar cal;
    SimpleDateFormat sdf;
    String currencyFrom="ETH";
    String currencyTo="BTC";
    String per="30";
    private ArrayList<String> currencyList, currencyListTo;
    private Spinner spinnerFrom, spinnerTo, exchange_spinner;
    private String [] currenciesFrom = {"ETH","LTC", "XMR","XRP"/*, "DASH", "ZEC"*/};
    private String [] currenciesTo = {"BTC","USD"};
    private String [] mExchanges = {"Default","Poloniex", "Gemini", "Binance", "Gatecoin", "Kucoin"};

    TextView txtcurrecyRates;
    String jsonURL = null;
    Currency sCurrency;
    boolean defalutExchanges= true;
    String exchange= "CCCAGG";
    Map<String, ArrayList<String>> arrayListMap=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.candle_bar_seperate);
        arrayListMap= new HashMap<>();
        progressBar= findViewById(R.id.progressBar);
        spinnerFrom = (Spinner) findViewById(R.id.currency_spinner);
        spinnerTo = (Spinner) findViewById(R.id.currency_spinner2);
        exchange_spinner= (Spinner) findViewById(R.id.exchange_spinner);
        txtcurrecyRates= findViewById(R.id.txtcurrecyRates);
        spinnerFrom.setOnItemSelectedListener(new SpinnerFromOnItemSelectedListener());
        spinnerTo.setOnItemSelectedListener(new SpinnetToOnItemSelectedListener());
        exchange_spinner.setOnItemSelectedListener(new CustomOnItemSelectedListenerforExchange());


        currencyList = new ArrayList<>();
        for (int i = 0; i < currenciesFrom.length; i++){
            String title = currenciesFrom[i];
            currencyList.add(title);
        }
        currencyListTo = new ArrayList<>();
        for (int i = 0; i < currenciesTo.length; i++){
            String title = currenciesTo[i];
            currencyListTo.add(title);
        }


        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(CandleBarSeperateActivity.this, android.R.layout.simple_spinner_item, currenciesFrom);
        myAdapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        spinnerFrom.setAdapter(myAdapter);

        ArrayAdapter<String> myAdapter2 = new ArrayAdapter<>(CandleBarSeperateActivity.this, android.R.layout.simple_spinner_item, currenciesTo);
        myAdapter2.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        spinnerTo.setAdapter(myAdapter2);


        ArrayAdapter<String> myAdapter3 = new ArrayAdapter<>(CandleBarSeperateActivity.this, android.R.layout.simple_spinner_item, mExchanges);
        myAdapter3.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        exchange_spinner.setAdapter(myAdapter3);


        initbarChart();
        initCombinedChart();
        setListener();



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
        cryptoPresenter.showCryptoOHLC( currencyTo, currencyFrom, per, exchange);
        customMarkerView = new CustomMarkerView(getApplicationContext(), R.layout.marker);
        customMarkerViewForBar = new CustomMarkerViewForBar(getApplicationContext(), R.layout.marker);
        customMarkerImgView = new CustomMarkerImageView(getApplicationContext(), R.layout.marker_img);



        //jsonURL = "https://min-api.cryptocompare.com/data/pricemulti?fsyms=XMR&tsyms=BTC,USD";
        jsonURL = "https://min-api.cryptocompare.com/data/pricemulti?fsyms="+currencyFrom+"&tsyms=BTC,USD";
        Log.d("URL: "," "+jsonURL);
        /*new GetCurrencies().execute();

        if (jsonURL!=null){

            final Handler ha=new Handler();
            ha.postDelayed(new Runnable() {

                @Override
                public void run() {
                    refresh();
                    ha.postDelayed(this, 3000);
                }
            }, 3000);

        }*/
    }



    private void initbarChart(){
        barChart = (BarChart) findViewById(R.id.barchart);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(false);
        XAxis barChartxAxis = barChart.getXAxis();
        barChartxAxis.setEnabled(true);
        barChart.getXAxis().setGranularity(1);
        YAxis barChartleftAxis = barChart.getAxisLeft();
        barChartleftAxis.setLabelCount(3, true);
        barChartleftAxis.setGranularityEnabled(true);
        barChartleftAxis.setGranularity(0.1f);
        barChartleftAxis.setXOffset(2);
        YAxis barChartrightAxis = barChart.getAxisRight();
        barChartrightAxis.setDrawGridLines(false);

        barChartrightAxis.setLabelCount(5, true);
        barChartrightAxis.setEnabled(false);

        barChart.animateXY(2000, 2000);
        barChart.setMarkerView(customMarkerViewForBar);

        barChart.setOnChartValueSelectedListener(onBarChartValueSelectedListener);
        barChart.setDoubleTapToZoomEnabled(false);

        Legend barLegend = barChart.getLegend();
        barLegend.setForm(Legend.LegendForm.CIRCLE);
        barLegend.setPosition(Legend.LegendPosition.BELOW_CHART_RIGHT);

        barChart.getViewPortHandler().setMaximumScaleX(2f);
        barChart.getViewPortHandler().setMaximumScaleY(1f);
    }


    private void initCombinedChart(){
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
        leftAxis.setXOffset(5);
        mChart.setNoDataText("");

        mChart.setMarkerView(customMarkerView);
        mChart.setTouchEnabled(true);
        Description description= new Description();
        description.setText("");
        mChart.setDescription(description);
        mChart.getViewPortHandler().setMaximumScaleX(2f);
        mChart.getViewPortHandler().setMaximumScaleY(1f);

        mChart.setOnChartValueSelectedListener(onmChartValueSelectedListener);
        mChart.setDoubleTapToZoomEnabled(false);
        Legend l = mChart.getLegend();
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setPosition(Legend.LegendPosition.ABOVE_CHART_RIGHT);
        mChart.getXAxis().setEnabled(false);

    }
    private void setListener(){
        mChart.setOnChartGestureListener(new CoupleChartGestureListener(mChart, new Chart[]{barChart, mChart}));
        barChart.setOnChartGestureListener(new CoupleChartGestureListener(barChart, new Chart[]{barChart,mChart }));
    }


    private  OnChartValueSelectedListener onmChartValueSelectedListener= new OnChartValueSelectedListener() {
        @Override
        public void onValueSelected(Entry e, Highlight h) {


            Highlight highlight = new Highlight(h.getX(),  h.getDataIndex(), h.getDataSetIndex());

            highlight.setDataIndex( h.getDataSetIndex());

            barChart.highlightValues(new Highlight[]{highlight});


            mChart.setRenderer(null);
            mChart.setDrawMarkers(true);
            mChart.highlightValue(h);
            barChart.highlightValue(h);


            CandleDataSet cData = (CandleDataSet) mChart.getCandleData()
                    .getDataSetForEntry(e);

            BarDataSet bData = (BarDataSet) barChart.getBarData()
                    .getDataSetByIndex(h.getDataSetIndex());

            LineDataSet lData = (LineDataSet) mChart.getLineData()
                    .getDataSetForEntry(e);

            if(cData!=null){
                customMarkerView.setTvContent( cData.getEntryForXValue(e.getX(), e.getY()));
                mChart.setRenderer(null);
                mChart.setMarker(customMarkerView);
            }
            if(bData!=null){

                int ind= (int) e.getX();
                BarEntry barEntry= bData.getEntryForIndex(ind);
                customMarkerViewForBar.setbarDataContent(""+barEntry.getY());
                barChart.setRenderer(null);
                barChart.setMarker(customMarkerViewForBar);

            }else if(lData!=null){
                mChart.setMarker(customMarkerImgView);

            }
        }

        @Override
        public void onNothingSelected() {

        }
    };


    private  OnChartValueSelectedListener onBarChartValueSelectedListener= new OnChartValueSelectedListener() {
        @Override
        public void onValueSelected(Entry e, Highlight h) {

            try {

                mChart.getCandleData().setHighlightEnabled(true);
                mChart. highlightValue(e.getX(), (int) e.getX());
                barChart.highlightValue(h);
                CandleEntry cData=   yVals1.get((int)h.getX());
                BarDataSet bData = (BarDataSet) barChart.getBarData().getDataSetForEntry(e);

                LineDataSet lData = (LineDataSet) mChart.getLineData()
                        .getDataSetByIndex(h.getDataIndex());

                if(cData!=null){
                    customMarkerView.setTvContent(cData);
                    mChart.setMarker(customMarkerView);
                }
                if(bData!=null){

                    customMarkerViewForBar.setTvContent(e);
                    barChart.setRenderer(null);
                    barChart.setMarker(customMarkerViewForBar);

                }else if(lData!=null){
                    mChart.setMarker(customMarkerImgView);

                }


            }catch (Exception ex){
                ex.printStackTrace();
            }
        }

        @Override
        public void onNothingSelected() {

        }
    };


    public void showPopup(View v){

        PopupMenu popup = new PopupMenu(CandleBarSeperateActivity.this, v);
        popup.getMenuInflater().inflate(R.menu.data_period_six_items, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                periodContext= item.getTitle().toString();

                String  pr= "";

                if(item.getItemId()==R.id.day){
                    pr=     getPeriod(1);
                    per="1";
                }else if(item.getItemId()==R.id.week){
                    pr=      getPeriod(7);
                    per="7";
                }else if(item.getItemId()==R.id.month){
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
                cryptoPresenter.showCryptoOHLC( currencyTo, currencyFrom, per, exchange );
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
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - days);
        Log.d(" DATE:::::::::: ", " " + sdf.format(cal.getTime()) + "  " + days);
        return "" + sdf.format(cal.getTime());

    }

    ArrayList<CandleEntry> yVals1;
    ArrayList<String> labels;
    ArrayList<Float> avgs;
    ArrayList<Float> avgsDataList;


    @Override
    public void showCryptoOHLC(List<OHLC> list) {

        if(list!=null){


            mChart.getAxisRight().setEnabled(false);

            mChart.invalidate();
            barChart.invalidate();
            mChart.clear();
            if( mChart.getCandleData()!=null)
                mChart.getCandleData().clearValues();
            yVals1 = new ArrayList<CandleEntry>();
            labels = new ArrayList<String>();
            avgs= new ArrayList<>();
            avgsDataList= new ArrayList<>();
            labels.clear();
            yVals1.clear();

            if(list.size()>0) {
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

                XAxis barChartxAxis = barChart.getXAxis();
                barChartxAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                barChartxAxis.setDrawGridLines(false);
                barChartxAxis.enableGridDashedLine(10f, 10f, 2f);

                CombinedData combinedData = new CombinedData();
                combinedData.setData(generateLineData());
                combinedData.setData(data);
                //  combinedData.setData(setBarData(list));

                mChart.setData(combinedData);
                barChart.setData(setBarData(list));

                barChartxAxis.setValueFormatter(new IAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        String val = "";
                        try {
                            val = getLable(labels.get((int) value));
                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                        return val;
                    }
                });
                xAxis.setValueFormatter(new IAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {

                        String val = "";
                        try {
                            val = getLable(labels.get((int) value));
                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                        return val;
                    }
                });

                mChart.animateXY(3000, 3000);
                barChart.animateXY(3000, 3000);
                mChart.getData().setHighlightEnabled(true);

                barChart.getBarData().setHighlightEnabled(true);
                mChart.getCandleData().setHighlightEnabled(true);
                //  mChart.getBarData().setHighlightEnabled(true);
                mChart.getLineData().setHighlightEnabled(true);

                mChart.invalidate();
                barChart.invalidate();
            }
        }else {
            mChart.clear();
            barChart.clear();
            mChart.invalidate();
            barChart.invalidate();
            //Toast.makeText(CandleBarSeperateActivity.this, "No Data Found", Toast.LENGTH_SHORT).show();
        }
    }

    private String getLable(String seconds){

        String date= "";

        try {
            long currentDateTime = (Long.parseLong(seconds))*1000;
            Date currentDate = new Date(currentDateTime);

            if(per.equals("1")){
                DateFormat df = new SimpleDateFormat("dd-MMM");
                DateFormat time = new SimpleDateFormat("hh:mm");

                date=df.format(currentDate)+"\n"+ time.format(currentDate);
            }else if(per.equals("7")){
                DateFormat df = new SimpleDateFormat("dd-MMM\nhh:mm");
                date=df.format(currentDate);
            }else {
                DateFormat df = new SimpleDateFormat("dd-MMM-yy");
                date=df.format(currentDate);
            }
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
        // set1.setAxisDependency(YAxis.AxisDependency.RIGHT);

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        data.setValueTextSize(10f);
        data.setDrawValues(false);
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
            // Log.d(" ","tempCount "+ tempCount);
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
        arrayListMap= list;
        /*currencyListTo = new ArrayList<>();
        currencyListTo.addAll(list);

        System.out.println(list);
        ArrayAdapter<String> myAdapter2 = new ArrayAdapter<>(CandleBarSeperateActivity.this, android.R.layout.simple_spinner_item, list);
        myAdapter2.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        spinnerTo.setAdapter(myAdapter2);*/
        ArrayList <String> tempStrings= new ArrayList<>();

        System.out.println("Key1--------------- ");
        Set set = (Set) list.entrySet();
        //Create iterator on Set
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry mapEntry = (Map.Entry) iterator.next();

            // Get Key
            String keyValue = (String) mapEntry.getKey();

            //Get Value
            ArrayList<String> value = (ArrayList<String>) mapEntry.getValue();
            tempStrings.add(keyValue);
            System.out.println("Key : " + keyValue + "= Value : " + value);
        }

        currencyList= new ArrayList<>();
        currencyList.addAll(tempStrings);
        ArrayAdapter<String> myAdapter2 = new ArrayAdapter<>(CandleBarSeperateActivity.this, android.R.layout.simple_spinner_item, currencyList);
        myAdapter2.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        spinnerFrom.setAdapter(myAdapter2);

        System.out.println("Key--------------- ");



    }

    private class SpinnerFromOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            currencyFrom = currencyList.get(pos);
            if(defalutExchanges){
                //cryptoPresenter.showCryptoOHLC( currencyTo, currencyFrom, per, exchange);
                //mChart.invalidate();
            }else {

                setDataForSpinnerTo(currencyFrom);

                //  cryptoPresenter.getTopExchange(currency,5);
            }

            cryptoPresenter.showCryptoOHLC( currencyTo, currencyFrom, per, exchange);
             mChart.invalidate();
            Log.d(" ","CustomOnItemSelectedListener "+ currencyFrom);
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }

    }

    private void setDataForSpinnerTo(String currencyFrom){
        ArrayList<String> tempTo= new ArrayList<>();
        Set set = (Set) arrayListMap.entrySet();
        //Create iterator on Set
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry mapEntry = (Map.Entry) iterator.next();
            // Get Key
            String keyValue = (String) mapEntry.getKey();
            //Get Value

            if(keyValue.equals(currencyFrom)){
                ArrayList<String> value = (ArrayList<String>) mapEntry.getValue();
                tempTo.addAll(value);
            }
            System.out.println("Key : " + keyValue + "= Value : " + tempTo);
        }

        currencyListTo= new ArrayList<>();
        currencyListTo.addAll(tempTo);
        ArrayAdapter<String> myAdapter2 = new ArrayAdapter<>(CandleBarSeperateActivity.this, android.R.layout.simple_spinner_item, currencyListTo);
        myAdapter2.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        spinnerTo.setAdapter(myAdapter2);
    }


    private class SpinnetToOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            currencyTo = currencyListTo.get(pos);
            cryptoPresenter.showCryptoOHLC( currencyTo, currencyFrom, per, exchange);
            mChart.invalidate();
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }

    }

    private class CustomOnItemSelectedListenerforExchange implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            if (pos == 0) {
                defalutExchanges= true;

                exchange= "CCCAGG";
                /*currencyList = new ArrayList<>();
                for (int i = 0; i < currenciesFrom.length; i++){
                    String title = currenciesFrom[i];
                    currencyList.add(title);
                }
                currencyListTo = new ArrayList<>();
                for (int i = 0; i < currenciesTo.length; i++){
                    String title = currenciesTo[i];
                    currencyListTo.add(title);
                }*/


                ArrayAdapter<String> myAdapter = new ArrayAdapter<>(CandleBarSeperateActivity.this, android.R.layout.simple_spinner_item, currenciesFrom);
                myAdapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);
                spinnerFrom.setAdapter(myAdapter);

                ArrayAdapter<String> myAdapter2 = new ArrayAdapter<>(CandleBarSeperateActivity.this, android.R.layout.simple_spinner_item, currenciesTo);
                myAdapter2.setDropDownViewResource(android.R.layout.simple_list_item_checked);
                spinnerTo.setAdapter(myAdapter2);

            }else {
                defalutExchanges=false;
                // cryptoPresenter.getTopExchange(currency,5);
                exchange= mExchanges[pos];
                /*cryptoPresenter.showCryptoOHLC( currencyTo, currency, per, exchange);
                mChart.invalidate();*/

                cryptoPresenter.getTopExchange(exchange);
                mChart.invalidate();



            }


            // cryptoPresenter.showCryptoOHLC( currencyTo, currencyFrom, per, exchange);
            //mChart.invalidate();
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
            Log.d("JSON: ", ""+jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject ethObj = new JSONObject(jsonStr).getJSONObject(currencyFrom);
                    Double btcVal = ethObj.getDouble("BTC");
                    Double ethVal = ethObj.getDouble("USD");

                    sCurrency= new Currency(currencyFrom,btcVal , ethVal);

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
            jsonURL = "https://min-api.cryptocompare.com/data/pricemulti?fsyms="+currencyFrom+"&tsyms=BTC,USD";
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler handler = new HttpHandler();

            jsonStr = handler.makeServiceCall(jsonURL);
            Log.d("URL: "," "+jsonURL+"\n"+jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject ethObj = new JSONObject(jsonStr).getJSONObject(currencyFrom);

                    Double btcVal = ethObj.getDouble("BTC");
                    Double ethVal = ethObj.getDouble("USD");
                    sCurrency = new Currency(currencyFrom,btcVal , ethVal);

                } catch (final JSONException e) {

                }

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {
                if (jsonStr!=null){
                    txtcurrecyRates.setText("BTC: "+ sCurrency.getBtcValue()+"\nUSD: "+sCurrency.getEthValue());
                }else {

                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }



}



