package ranjit.com.chartapplication.CryptoCurrency;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    public void showCryptoOHLC( final String currencyTo, final String currencyFrom, final String period, final  String exchange) {
        new AsyncTask<Void, Void, List<OHLC>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                activity.showProgresBar();
            }

            @Override
            protected List<OHLC> doInBackground(Void... params) {
                return restResourse.getOHLC(currencyTo, currencyFrom, period, exchange);
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
    public void getTopExchange(final String exchange) {
        new AsyncTask<Void, Void,Map<String, ArrayList<String>>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                activity.showProgresBar();
            }

            @Override
            protected Map<String, ArrayList<String>> doInBackground(Void... params) {
                return restResourse.getExchangeListNew(exchange);
            }

            @Override
            protected void onPostExecute(Map<String, ArrayList<String>> currencies) {
                super.onPostExecute(currencies);
                activity.showExangeList(currencies);
                activity.stopProgresBar();
            }
        }.execute();
    }


}