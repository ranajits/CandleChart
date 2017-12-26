package ranjit.com.chartapplication.CryptoCurrency;

import android.os.AsyncTask;

import java.util.List;

import ranjit.com.chartapplication.quandle.OHLC;

/**
 * Created by ranjit on 13/12/17.
 */

public class CryptoPresentrer implements ICryptoPresenter {

    ICryptoDataActivity activity;
    CryptoRestResourse restResourse;

    public CryptoPresentrer(ICryptoDataActivity activity) {
        this.activity = activity;
        restResourse = CryptoRestResourse.getRestResourse();
    }

    @Override
    public void showCryptoOHLC( final String currency1, final String currency, final String period, final  String exchange) {
        new AsyncTask<Void, Void, List<OHLC>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                activity.showProgresBar();
            }

            @Override
            protected List<OHLC> doInBackground(Void... params) {
                return restResourse.getOHLC(currency1, currency, period, exchange);
            }

            @Override
            protected void onPostExecute(List<OHLC> ohlcs) {
                super.onPostExecute(ohlcs);
               // activity.showOHLC(ohlcs);
                activity.showCryptoOHLC(ohlcs);
                activity.stopProgresBar();
            }
        }.execute();
    }

    @Override
    public void getTopExchange(final String currency, final int limit) {
        new AsyncTask<Void, Void, List<String>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                activity.showProgresBar();
            }

            @Override
            protected List<String> doInBackground(Void... params) {
                return restResourse.getExchangeList(currency, limit);
            }

            @Override
            protected void onPostExecute(List<String> ohlcs) {
                super.onPostExecute(ohlcs);
                activity.showExangeList(ohlcs);
                activity.stopProgresBar();
            }
        }.execute();
    }


}