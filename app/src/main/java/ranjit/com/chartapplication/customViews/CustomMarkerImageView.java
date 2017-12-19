package ranjit.com.chartapplication.customViews;

import android.content.Context;
import android.widget.ImageView;

import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import ranjit.com.chartapplication.R;

/**
 * Created by ranjit on 11/12/17.
 */

public class CustomMarkerImageView extends MarkerView implements IMarker {

    private ImageView tvContent;

    public CustomMarkerImageView(Context context, int layoutResource) {
        super(context, layoutResource);
        // this markerview only displays a textview
        tvContent = (ImageView) findViewById(R.id.imgMarker);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
// content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
    /*    int dataSetIndex = highlight.getDataSetIndex();
        CandleEntry sd = null;
        CandleDataSet set = sd.get(dataSetIndex);
      CandleEntry s= set. getEntryForXValue(e.getX(), e.getY());*/

        //   tvContent.setText("" + e.getX()+", "+e.getY()+ e.getData()); // set the entry-value as the display text
        super.refreshContent(e, highlight);
        //highlight.setDraw(e.getX(), e.getY());
    }





    /*public void setTvContent(Entry e) {
        Log.d("Entry e: ", ""+ e);
        tvContent.setText("Volume: " + e.getX());
    }*/

    private MPPointF mOffset;

    @Override
    public MPPointF getOffset() {

        if(mOffset == null) {
            // center the marker horizontally and vertically
            mOffset = new MPPointF(-(getWidth() /3), -getHeight()/3);
        }

        return mOffset;
    }


}