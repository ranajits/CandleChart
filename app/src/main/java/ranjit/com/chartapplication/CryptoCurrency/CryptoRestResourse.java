package ranjit.com.chartapplication.CryptoCurrency;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ranjit.com.chartapplication.quandle.OHLC;


public class CryptoRestResourse {
    private static CryptoRestResourse restResourse;

    private CryptoRestResourse() {
    }

    public static CryptoRestResourse getRestResourse() {
        if (restResourse == null) {
            restResourse = new CryptoRestResourse();
        }
        return restResourse;
    }


    public List<OHLC> getOHLC(String currency1, String currency, String period) {

        List<OHLC> list = new ArrayList<>();


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH));
        //Date myDate = cal.getTime();
        Log.d(" DATE:::::::::: "," "+ sdf.format(cal.getTime()));


     //   https://min-api.cryptocompare.com/data/histominute?fsym=ETH&tsym=USD&limit=5&aggregate=3&e=Kraken&extraParams=your_app_name
       // https://min-api.cryptocompare.com/data/histoday?fsym=ETH&tsym=BTC&limit=60&aggregate=1&toTs=1452680400&extraParams=your_app_name
            try {
                //for histominute
                // URL url = new URL("https://min-api.cryptocompare.com/data/histominute?fsym=ETH&tsym=USD&limit="+period+"&aggregate=3&e=Kraken&extraParams=your_app_name");
                //for histoday
                //fsym=ETH&tsym=BTC

                String compare= "";
               /* if(currency.equals("BTC")){
                   // compare="fsym="+currency+"&tsym=USD";
                    compare="fsym=USD&tsym="+currency;
                }else {
                    compare= "fsym="+currency+"&tsym=BTC";
                }*/

                compare= "fsym="+currency+"&tsym="+currency1;

                URL url = new URL("https://min-api.cryptocompare.com/data/histoday?"+compare+"&limit="+period+"&aggregate=1&toTs="+(System.currentTimeMillis()/1000)+"&extraParams=your_app_name");

                //URL url = new URL("https://www.quandl.com/api/v3/datasets/WIKI/" + company + ".json?collapse="+collapse+"&api_key=QwAcaE1Yz2oGLQ1XthEY&trim_start="+period+"&trim_end="+ sdf.format(cal.getTime()));//2017-10-27");
                Log.d(" URL"," "+url);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String result;
                String str;
                while ((str = reader.readLine()) != null) {
                    buffer.append(str);
                }
                result = buffer.toString();
                JSONObject full = new JSONObject(result);
               // JSONObject dataset = full.getJSONObject("Response");
                String resultCode= full.getString("Response");
                if(resultCode.equals("Success")) {

                    JSONArray data = (JSONArray)full.get("Data");

                    for (int i = 0; i < data.length(); i++) {

                        double vol=  (data.getJSONObject(i).getDouble("volumefrom"))+(data.getJSONObject(i).getDouble("volumeto"))/2;
                        double vol2=  0;
                        if((data.getJSONObject(i).getDouble("volumefrom"))>(data.getJSONObject(i).getDouble("volumeto"))){
                            vol2= (data.getJSONObject(i).getDouble("volumeto"));
                        }else {

                            vol2= (data.getJSONObject(i).getDouble("volumefrom"));
                        }
                        ;///2;

                        list.add(new OHLC(data.getJSONObject(i).getString("time"),
                                data.getJSONObject(i).getDouble("open"),
                                data.getJSONObject(i).getDouble("high"),
                                data.getJSONObject(i).getDouble("low"),
                                data.getJSONObject(i).getDouble("close"),
                                vol2));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        return list;
    }
}
