package ranjit.com.chartapplication.api;


import java.util.List;

import ranjit.com.chartapplication.domain.ChartData;
import ranjit.com.chartapplication.domain.Integration;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Methods {

    @GET(Integration.URI_RETUNR_Data)
    Call<List<ChartData>> returnChartData2(@Query("function") String function, @Query("symbol") String symbol, @Query("apikey") String apikey);

    @GET(Integration.URI_RETURN_CHART)
    Call<List<ChartData>> returnChartData(@Query("currencyPair") String currencyPair, @Query("start") String start, @Query("end") String end, @Query("period") String period);



}
