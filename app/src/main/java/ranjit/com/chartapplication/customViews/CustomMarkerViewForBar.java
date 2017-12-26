package ranjit.com.chartapplication.customViews;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import ranjit.com.chartapplication.R;

/**
 * Created by ranjit on 11/12/17.
 */

public class CustomMarkerViewForBar extends MarkerView implements IMarker {

    private TextView tvContent;

    public CustomMarkerViewForBar(Context context, int layoutResource) {
        super(context, layoutResource);
        tvContent = (TextView) findViewById(R.id.tvContent);
    }
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        super.refreshContent(e, highlight);
    }



    public void setTvContent(CandleEntry e) {


        Log.d("cData e: ", ""+ e);
        tvContent.setText("Open: " + e.getOpen()+", Close: "+ e.getClose()+ ",\n High: "+e.getHigh()+", Low: "+ e.getLow()); // set the entry-value as the display text

    }
    public void setTvContent(Entry e) {
        Log.d("Entry e: ", ""+ e);
        tvContent.setText("Volume: " + e.getY());
    }

    public void setbarDataContent(String data) {
        Log.d("Entry e: ", ""+ data);
        tvContent.setText("Volume: " +data);
    }


    private MPPointF mOffset;

    @Override
    public MPPointF getOffset() {

        if(mOffset == null) {
            // center the marker horizontally and vertically
            mOffset = new MPPointF(-(getWidth() / 2), -getHeight());
        }

        return mOffset;
    }


}