package ranjit.com.chartapplication.quandle;


import java.util.List;

public interface IMainActivity {

    void showOHLC(List<OHLC> list);
    void showProgresBar();
    void stopProgresBar();
}
