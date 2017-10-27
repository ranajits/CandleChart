package ranjit.com.chartapplication.quandle;

import android.os.AsyncTask;

import java.util.List;

public class MainPresenter implements IMainPresenter{

    IMainActivity activity;
    RestResourse restResourse;

    public MainPresenter(IMainActivity activity) {
        this.activity = activity;
        restResourse = RestResourse.getRestResourse();
    }

    @Override
    public void showOHLC(final String server, final String company, final String collapse, final String period) {
        new AsyncTask<Void, Void, List<OHLC>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                activity.showProgresBar();
            }

            @Override
            protected List<OHLC> doInBackground(Void... params) {
                return restResourse.getOHLC(company, collapse, period);
            }

            @Override
            protected void onPostExecute(List<OHLC> ohlcs) {
                super.onPostExecute(ohlcs);
                activity.showOHLC(ohlcs);
                activity.stopProgresBar();
            }
        }.execute();
    }


}
