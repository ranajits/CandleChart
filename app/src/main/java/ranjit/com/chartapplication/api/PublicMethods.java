package ranjit.com.chartapplication.api;


import java.util.List;

import ranjit.com.chartapplication.domain.ChartData;
import ranjit.com.chartapplication.service.CallBack;
import ranjit.com.chartapplication.service.ServiceRequest;
import retrofit2.Call;

public class PublicMethods {

    public void returnChartData(CallBack<List<ChartData>> poloniexCallBack, String currencyPair, String start, String end, String period) {
        ServiceRequest<List<ChartData>> poloniexServiceRequest = new ServiceRequest(poloniexCallBack);
        Methods methods = poloniexServiceRequest.getConfiguration().create(Methods.class);
        Call<List<ChartData>> orderBookCall = methods.returnChartData(currencyPair, start, end, period);
        orderBookCall.enqueue(poloniexServiceRequest.getCallback());
    }


    public void returnChartData2(CallBack<List<ChartData>> poloniexCallBack, String function, String symbol, String apikey) {
        ServiceRequest<List<ChartData>> poloniexServiceRequest = new ServiceRequest(poloniexCallBack);
        Methods methods = poloniexServiceRequest.getConfiguration().create(Methods.class);
        Call<List<ChartData>> orderBookCall = methods.returnChartData2(function, symbol, apikey);
        orderBookCall.enqueue(poloniexServiceRequest.getCallback());
    }


}
