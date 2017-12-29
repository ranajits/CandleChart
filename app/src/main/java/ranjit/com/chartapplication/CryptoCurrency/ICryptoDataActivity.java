package ranjit.com.chartapplication.CryptoCurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ranjit.com.chartapplication.quandle.OHLC;

/**
 * Created by ranjit on 13/12/17.
 */

public interface ICryptoDataActivity {
    void showCryptoOHLC(List<OHLC> list);
    void showProgresBar();
    void stopProgresBar();
    void showExangeList(Map<String, ArrayList<String>> list);

}
