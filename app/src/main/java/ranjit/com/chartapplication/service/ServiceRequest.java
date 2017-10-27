package ranjit.com.chartapplication.service;


import ranjit.com.chartapplication.domain.Integration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceRequest<ResponseAlphavantage> {

    private Retrofit retrofit;
    private CallBack<ResponseAlphavantage> poloniexCallBack;
    private Callback<ResponseAlphavantage> callback;

    public ServiceRequest(CallBack<ResponseAlphavantage> poloniexCallBack) {
        this.poloniexCallBack = poloniexCallBack;
        callback =  new BaseCallBack();
        builderRetrofit();
    }

    private void builderRetrofit() {
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
        retrofitBuilder
                .baseUrl(Integration.HOST)
                .addConverterFactory(GsonConverterFactory.create());
        retrofit = retrofitBuilder.build();
    }

    private class BaseCallBack implements Callback<ResponseAlphavantage> {
        @Override
        public void onResponse(Call<ResponseAlphavantage> call, Response<ResponseAlphavantage> response) {
            if (response.isSuccessful()) {
                poloniexCallBack.success(response.body());
            } else {
                poloniexCallBack.fail(response.message());
            }
        }

        @Override
        public void onFailure(Call<ResponseAlphavantage> call, Throwable t) {
            poloniexCallBack.fail(t.getMessage());
        }
    }

    public Retrofit getConfiguration() {
        return retrofit;
    }

    public Callback<ResponseAlphavantage> getCallback() {
        return callback;
    }
}
