package ranjit.com.chartapplication;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ranjit.com.chartapplication.quandle.IMainActivity;
import ranjit.com.chartapplication.quandle.IMainPresenter;
import ranjit.com.chartapplication.quandle.MainPresenter;
import ranjit.com.chartapplication.quandle.OHLC;

//import com.jimmoores.quandl.DataSetRequest;
//import com.jimmoores.quandl.tablesaw.TableSawQuandlSession;
//import tech.tablesaw.api.Table;

public class CandleActivity extends DemoBase implements OnSeekBarChangeListener , IMainActivity {

    // private CandleStickChart mChart;
    CombinedChart mChart;
    ContentLoadingProgressBar progressBar;
    IMainPresenter presenter;

    String collapsecontext="";
    String periodContext="";

    String collapse= "daily";
    // presenter.showOHLC("k", "k", collapse);
    String period= "2016-10-27";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_candle);

        progressBar= findViewById(R.id.progressBar);


        //  mChart = (CandleStickChart) findViewById(R.id.chart1);
        mChart = (CombinedChart) findViewById(R.id.chart1);
        mChart.setBackgroundColor(Color.WHITE);

        mChart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(10);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        mChart.setDrawGridBackground(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setLabelCount(7, false);
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawAxisLine(false);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);
        mChart.getLegend().setEnabled(false);
        mChart.setNoDataText("");


        presenter = new MainPresenter(this);

        //collapse=none|daily|weekly|monthly|quarterly|annual

        if(getIntent().hasExtra("collapse")){
            collapse= getIntent().getStringExtra("collapse");
        }

        if(getIntent().hasExtra("period")){
            period=  getIntent().getStringExtra("period");
        }


        TextView PERI= findViewById(R.id.txtPeriod);
        PERI.setText(period+" to 2017-10-27\n Collapse: "+collapse );

        presenter.showOHLC("k", "k", collapse, period);
    }

    public void showPopup(View v){

        PopupMenu popup = new PopupMenu(CandleActivity.this, v);
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


                finish();
                startActivity(new Intent(CandleActivity.this, CandleActivity.class).putExtra("collapse", collapse).putExtra("period", pr));


                return true;
            }
        });

        popup.show();//showing popup menu


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.collapse, menu);
        return true;
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
    public boolean onOptionsItemSelected(MenuItem item) {


        finish();
        startActivity(new Intent(CandleActivity.this, CandleActivity.class).putExtra("collapse", item.getTitle().toString()).putExtra("period", period));


        return true;
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


    @Override
    public void showOHLC(List<OHLC> list) {
        Log.d("OHLC: ", ""+ list);
        mChart.invalidate();
        mChart.clear();
        if( mChart.getCandleData()!=null)
            mChart.getCandleData().clearValues();
        yVals1 = new ArrayList<CandleEntry>();
        labels = new ArrayList<String>();
        avgs= new ArrayList<>();
        labels.clear();
        yVals1.clear();

        if(list.size()>0){
            for (int i = 0; i < list.size(); i++) {

                Log.d(" "," "+ (float) list.get(i).getHigh());
                yVals1.add(new CandleEntry(
                        new Long(i),
                        (float) list.get(i).getHigh(),
                        (float) list.get(i).getLow(),
                        (float)list.get(i).getOpen(),
                        (float)list.get(i).getClose()

                ));

                labels.add( list.get(i).getDate());
            }

           /* long low, open, close = 45, high;
            for (int i = 0; i < 30; i++) {
                open = close;
                close += ~~(Math.random() * 10) * Math.pow(-1, ~~(Math.random() * 2));
                high = Math.max(open, close) + ~~(Math.random() * 10);
                low = Math.min(open, close) - ~~(Math.random() * 10);
                data.addRow([new Date(2014, 0, i + 1), low, open, close, high]);
            }*/

            float low, open, close , high;
            for (int i = 0; i < list.size(); i++) {
                open= (float)list.get(i).getOpen();
                high=     (float) list.get(i).getHigh();
                low=       (float) list.get(i).getLow();
                close=        (float)list.get(i).getClose();

                avgs.add((open+high+low+close)/4);
            }


            Log.d("CandleActivity", " size: "+labels.size()+ "  "+ list.size());

            CandleDataSet set1 = new CandleDataSet(yVals1, "Data Set");
            CandleData data= new CandleData(set1);
            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
            set1.setShadowColor(Color.DKGRAY);
            set1.setShadowWidth(0.7f);
            set1.setDecreasingColor(Color.RED);
            set1.setDecreasingPaintStyle(Paint.Style.FILL);
            set1.setIncreasingColor(Color.rgb(122, 242, 84));
            set1.setIncreasingPaintStyle(Paint.Style.STROKE);
            set1.setNeutralColor(Color.BLUE);

            XAxis xAxis = mChart.getXAxis();

            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);



            CombinedData combinedData = new CombinedData();

            combinedData.setData(generateLineData());
            combinedData.setData(data);
            combinedData.setData(generateBarData());

            // mChart.setData(data);
            mChart.setData(combinedData);

            xAxis.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return labels.get((int) value);
                }
            });


            mChart.animateXY(3000, 3000);
            mChart.getData().setHighlightEnabled(false);
            mChart.invalidate();


        }
    }


    private  int itemcount = 12;
    private LineData generateLineData() {

        itemcount= avgs.size()/5;
        LineData d = new LineData();

        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (int index = 0; index < itemcount; index++)
            entries.add(new Entry(((index+1)*5), avgs.get(index)));//getRandom(15, 5)));// index + 0.5f

        LineDataSet set = new LineDataSet(entries, "Line DataSet");
        set.setColor(Color.rgb(240, 238, 70));
        set.setLineWidth(2.5f);
        //set.setCircleColor(Color.rgb(240, 238, 70));
        //set.setCircleRadius(5f);
        set.setFillColor(Color.rgb(240, 238, 70));
        set.setMode(LineDataSet.Mode.LINEAR);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.rgb(240, 238, 70));

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.addDataSet(set);

        return d;
    }






    private BarData generateBarData() {

        ArrayList<BarEntry> entries1 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> entries2 = new ArrayList<BarEntry>();

        for (int index = 0; index < itemcount; index++) {
            entries1.add(new BarEntry(0, getRandom(25, 25)));

            // stacked
            entries2.add(new BarEntry(0, new float[]{getRandom(13, 12), getRandom(13, 12)}));
        }

        BarDataSet set1 = new BarDataSet(entries1, "Bar 1");
        set1.setColor(Color.rgb(60, 220, 78));
        set1.setValueTextColor(Color.rgb(60, 220, 78));
        set1.setValueTextSize(10f);
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);

        BarDataSet set2 = new BarDataSet(entries2, "");
        set2.setStackLabels(new String[]{"Stack 1", "Stack 2"});
        set2.setColors(new int[]{Color.rgb(61, 165, 255), Color.rgb(23, 197, 255)});
        set2.setValueTextColor(Color.rgb(61, 165, 255));
        set2.setValueTextSize(10f);
        set2.setAxisDependency(YAxis.AxisDependency.LEFT);

        float groupSpace = 0.06f;
        float barSpace = 0.02f; // x2 dataset
        float barWidth = 0.45f; // x2 dataset
        // (0.45 + 0.02) * 2 + 0.06 = 1.00 -> interval per "group"

        BarData d = new BarData(set1, set2);
        d.setBarWidth(barWidth);

        // make this BarData object grouped
        d.groupBars(0, groupSpace, barSpace); // start at x = 0

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



}



