package ranjit.com.chartapplication.service;

public interface CallBack<T> {

    void success(T response);

    void fail(String error);

}
