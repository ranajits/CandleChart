package ranjit.com.chartapplication.quandle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ranjit.com.chartapplication.R;


public class OHLCAdapter extends BaseAdapter {

    private List<OHLC> listOHLC;
    private LayoutInflater inflater;

    OHLCAdapter(Context context, List<OHLC> listOHLC) {
        this.listOHLC = listOHLC;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listOHLC.size();
    }

    @Override
    public OHLC getItem(int position) {
        return listOHLC.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView date;
        TextView open;
        TextView high;
        TextView low;
        TextView close;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OHLCAdapter.ViewHolder holder = null;

        if (convertView == null) {

            holder = new OHLCAdapter.ViewHolder();
            convertView = inflater.inflate(R.layout.item, null);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.open = (TextView) convertView.findViewById(R.id.open);
            holder.high = (TextView) convertView.findViewById(R.id.high);
            holder.low = (TextView) convertView.findViewById(R.id.low);
            holder.close = (TextView) convertView.findViewById(R.id.close);
            convertView.setTag(holder);
        } else {
            holder = (OHLCAdapter.ViewHolder) convertView.getTag();
        }

        holder.date.setText(""+listOHLC.get(position).getDate());
        holder.open.setText("O-" + listOHLC.get(position).getOpen());
        holder.high.setText("H-" + listOHLC.get(position).getHigh());
        holder.low.setText("L-" + listOHLC.get(position).getLow());
        holder.close.setText("C-" + listOHLC.get(position).getClose());
        return convertView;
    }
}
