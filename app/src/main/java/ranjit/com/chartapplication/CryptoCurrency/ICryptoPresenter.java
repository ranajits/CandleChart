package ranjit.com.chartapplication.CryptoCurrency;

/**
 * Created by ranjit on 13/12/17.
 */

public interface ICryptoPresenter {
    void showCryptoOHLC( String company, String collapse, String period, String exchange);
    void getTopExchange(String currency, int limit);

}
