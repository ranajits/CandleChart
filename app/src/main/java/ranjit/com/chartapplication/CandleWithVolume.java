package ranjit.com.chartapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ranjit.com.chartapplication.customViews.CustomMarkerImageView;
import ranjit.com.chartapplication.customViews.CustomMarkerView;
import ranjit.com.chartapplication.customViews.ImageLineChartRenderer;
import ranjit.com.chartapplication.quandle.IMainActivity;
import ranjit.com.chartapplication.quandle.IMainPresenter;
import ranjit.com.chartapplication.quandle.MainPresenter;
import ranjit.com.chartapplication.quandle.OHLC;

public class CandleWithVolume extends DemoBase implements SeekBar.OnSeekBarChangeListener, IMainActivity, OnChartValueSelectedListener {

    // private CandleStickChart mChart;
    CombinedChart mChart;
    ContentLoadingProgressBar progressBar;
    IMainPresenter presenter;

    String collapsecontext="";
    String periodContext="";

    String collapse= "daily";
    // presenter.showOHLC("k", "k", collapse);
    String period= "2016-10-27";
    CustomMarkerView customMarkerView;
    CustomMarkerImageView customMarkerImgView;
    TextView txtPeriod;
    Calendar cal;
    SimpleDateFormat sdf;
    Bitmap starBitmap;
    private ImageLineChartRenderer imageLineChartRenderer;
    boolean isFirst= false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_candle_with_volume);

        progressBar= findViewById(R.id.progressBar);

        mChart = (CombinedChart) findViewById(R.id.chart1);
        mChart.setBackgroundColor(Color.WHITE);

        //mChart.getDescription().setEnabled(false);

        mChart.setMaxVisibleValueCount(10);


        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        mChart.setDrawGridBackground(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        //xAxis.setDrawGridLines(false);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setLabelCount(7, false);

        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawAxisLine(false);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setLabelCount(5, false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMaximum(30000000);
        rightAxis.setDrawAxisLine(false);
        rightAxis.setAxisMinimum(0);
        // rightAxis.setEnabled(false);


        //mChart.getLegend().setEnabled(false);
        mChart.setNoDataText("");


        presenter = new MainPresenter(this);

        //collapse=none|daily|weekly|monthly|quarterly|annual

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

        txtPeriod.setText(period+" to "+sdf.format(cal.getTime()));//+/* 2017-10-27*/"\n Collapse: "+collapse );


        presenter.showOHLC("k", "k", collapse, period);
        customMarkerView = new CustomMarkerView(getApplicationContext(), R.layout.marker);
        customMarkerImgView = new CustomMarkerImageView(getApplicationContext(), R.layout.marker_img);

        //    customMarkerView.refreshContent(e, h);
        mChart.setMarkerView(customMarkerView);
        mChart.setTouchEnabled(true);


        starBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.star);
        //imageLineChartRenderer= new ImageLineChartRenderer(mChart, mChart.getAnimator(), mChart.getViewPortHandler(), starBitmap);

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
                    customMarkerView.setTvContent(e );
                    mChart.setRenderer(null);
                    mChart.setMarker(customMarkerView);



                }else if(lData!=null){
                    mChart.setMarker(customMarkerImgView);
                   /* if(!isFirst){
                    MarkerImage myMarkerImage = new MarkerImage(getApplicationContext(), R.drawable.star);

                       //  MPPointF mOffset;


                          //  mOffset = new MPPointF(-(e.getX() / 2), -e.getY());



                    //myMarkerImage.setOffset(mOffset*//*h.getX(), h.getY()*//*);
                        //myMarkerImage.setOffset(-(getWidth() / 2));
                    mChart.setMarker(myMarkerImage);


                    //mChart.setMarker(myMarkerImage);

                  // mChart.setRenderer(imageLineChartRenderer);
                    }else {
                        isFirst=false;
                    }*/
                    //mChart.setRenderer(new LineChartRenderer(mChart, mChart.getAnimator(), mChart.getViewPortHandler()));
                 //   mChart.invalidate();

                    //myMarkerImage.setOffset(e.getX(),e.getY());
                    // customMarkerView.setTvContent( lData.getEntryForXValue(e.getX(), e.getY()), h);
                    //  customMarkerView.setTvContent(e);
                }
                /*Log.d("cData: ", ""+ cData);
                Log.d("lData: ", ""+ lData);
                Log.d("bData: ", ""+ bData);*/


            }
            @Override
            public void onNothingSelected() {

            }
        });

        Description description= new Description();
        description.setText("");
        mChart.setDescription(description);
        mChart.getViewPortHandler().setMaximumScaleX(5f);
        mChart.getViewPortHandler().setMaximumScaleY(5f);

    }




    public void showPopup(View v){

        PopupMenu popup = new PopupMenu(CandleWithVolume.this, v);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.data_period, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                periodContext= item.getTitle().toString();

                String  pr= "";
                /*if(item.getItemId()==R.id.day){
                    pr=     getPeriod(1);
                }else if(item.getItemId()==R.id.week){
                    pr=      getPeriod(7);
                }else*/ if(item.getItemId()==R.id.month){
                    pr=    getPeriod(30);
                }else if(item.getItemId()==R.id.quarter){
                    pr=    getPeriod(90);
                }else if(item.getItemId()==R.id.annual){
                    pr=     getPeriod(365);
                }else {
                    pr=    getPeriod(30);
                }


                txtPeriod.setText(pr+" to "+sdf.format(cal.getTime()));
                presenter.showOHLC("k", "k", collapse, pr);
                mChart.invalidate();

                // finish();
                // startActivity(new Intent(CandleWithVolume.this, CandleWithVolume.class).putExtra("collapse", collapse).putExtra("period", pr));


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
        //Date myDate = cal.getTime();
        Log.d(" DATE:::::::::: "," "+ sdf.format(cal.getTime())+"  "+days);
        return ""+ sdf.format(cal.getTime());

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    ArrayList<CandleEntry> yVals1;
    ArrayList<String> labels;
    ArrayList<Float> avgs;
    ArrayList<Float> avgsDataList;


    @Override
    public void showOHLC(List<OHLC> list) {

        mChart.invalidate();
        mChart.clear();
        if( mChart.getCandleData()!=null)
            mChart.getCandleData().clearValues();
        yVals1 = new ArrayList<CandleEntry>();
        labels = new ArrayList<String>();
        avgs= new ArrayList<>();
        avgsDataList= new ArrayList<>();
        labels.clear();
        yVals1.clear();

        if(list.size()>0){

            Collections.reverse(list);

            for (int i = 0; i < list.size(); i++) {
                yVals1.add(new CandleEntry(
                        new Long(i),
                        (float) list.get(i).getHigh(),
                        (float) list.get(i).getLow(),
                        (float)list.get(i).getOpen(),
                        (float)list.get(i).getClose()
                ));
                labels.add( list.get(i).getDate());
            }


            float low, open, close , high;
            for (int i = 0; i < list.size(); i++) {
                open= (float)list.get(i).getOpen();
                high= (float) list.get(i).getHigh();
                low=  (float) list.get(i).getLow();
                close= (float)list.get(i).getClose();
                avgs.add((open+high+low+close)/4);
            }

            Log.d("CandleActivity", " size: "+labels.size()+ "  "+ list.size());

            CandleDataSet set1 = new CandleDataSet(yVals1, "OHLC");
            CandleData data= new CandleData(set1);
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
            //set1.setShadowColor();

            set1.setDrawVerticalHighlightIndicator(true);




            //set1.geten

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
                    return labels.get((int) value);
                }
            });

            mChart.animateXY(3000, 3000);
            mChart.getData().setHighlightEnabled(true);
            mChart.getCandleData().setHighlightEnabled(true);
            mChart.getBarData().setHighlightEnabled(true);
            mChart.getLineData().setHighlightEnabled(true);

            mChart.invalidate();
        }
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
        data.setValueTypeface(mTfLight);
        data.setBarWidth(0.9f);
        //data.set

        return data;
    }


    //private  int itemcount = 12;
    private LineData generateLineData() {
        //itemcount= avgs.size();
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

        LineDataSet set = new LineDataSet(entries, "Simple Moving Average(SMA)");
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




    private BarData setVolume(List<OHLC> list){
        ArrayList<BarEntry> entries1 = new ArrayList<BarEntry>();
        for (int index = 0; index < list.size(); index++) {
            //entries1.add(new BarEntry(0, getRandom(25, 25)));

            // stacked
            entries1.add(new BarEntry(0, (float)list.get(index).getVolume()));
        }


        BarDataSet set2 = new BarDataSet(entries1, "");
        set2.setStackLabels(new String[]{"Stack 1", "Stack 2"});
        set2.setColors(new int[]{Color.rgb(61, 165, 255), Color.rgb(23, 197, 255)});
        set2.setValueTextColor(Color.rgb(61, 165, 255));
        set2.setValueTextSize(10f);
        set2.setAxisDependency(YAxis.AxisDependency.RIGHT);

        //float groupSpace = 0.06f;
        float barSpace = 0.02f; // x2 dataset
        float barWidth = 0.45f; // x2 dataset
        // (0.45 + 0.02) * 2 + 0.06 = 1.00 -> interval per "group"

        BarData d = new BarData(set2);
        //   d.setBarWidth(barWidth);


        // make this BarData object grouped
        // d.groupBars(0, 0, barSpace); // start at x = 0

        return d;
    }

    private BarData generateBarData(List<OHLC> list) {

        ArrayList<BarEntry> entries1 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> entries2 = new ArrayList<BarEntry>();

        for (int index = 0; index < list.size(); index++) {
            entries1.add(new BarEntry(0, (float) list.get(index).getVolume()));

            // stacked
            entries2.add(new BarEntry(0, (float) list.get(index).getVolume()));
        }

        BarDataSet set1 = new BarDataSet(entries1, "Bar 1");
        set1.setColor(Color.rgb(60, 220, 78));
        set1.setValueTextColor(Color.rgb(60, 220, 78));
        set1.setValueTextSize(10f);
        set1.setAxisDependency(YAxis.AxisDependency.RIGHT);

        BarDataSet set2 = new BarDataSet(entries2, "");
        set2.setStackLabels(new String[]{"Stack 1", "Stack 2"});
        set2.setColors(new int[]{Color.rgb(61, 165, 255), Color.rgb(23, 197, 255)});
        set2.setValueTextColor(Color.rgb(61, 165, 255));
        set2.setValueTextSize(5f);
        set2.setAxisDependency(YAxis.AxisDependency.RIGHT);

        float groupSpace = 0.06f;
        float barSpace = 0.02f; // x2 dataset
        float barWidth = 0.45f; // x2 dataset
        // (0.45 + 0.02) * 2 + 0.06 = 1.00 -> interval per "group"

        BarData d = new BarData(set1, set2);
        d.setBarWidth(barWidth);

        // make this BarData object grouped
        d.groupBars(0, groupSpace, barSpace); // start at x = 0


       /* ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        data.setValueTextSize(10f);
        //data.setValueTypeface(mTfLight);
        data.setBarWidth(0.9f);*/




        /*float start = 1f;

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = (int) start; i < start + count + 1; i++) {
            float mult = (range + 1);
            float val = (float) (Math.random() * mult);
                yVals1.add(new BarEntry(i, val));

        }


        BarDataSet set3;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "The year 2017");

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setValueTypeface(mTfLight);
            data.setBarWidth(0.9f);

            mChart.setData(data);
        }*/


        return d;
    }


   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.collapse, menu);
        return true;
    }*/
   /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {


        finish();
        startActivity(new Intent(CandleWithVolume.this, CandleWithVolume.class).putExtra("collapse", item.getTitle().toString()).putExtra("period", period));


        return true;
    }
*/


    @Override
    public void showProgresBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void stopProgresBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("I clicked on", String.valueOf(e.getX()) );
    }

    @Override
    public void onNothingSelected() {

    }
}



