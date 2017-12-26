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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import ranjit.com.chartapplication.Utils;
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


    public List<OHLC> getOHLC(String currency1, String currency, String period, String exchange) {

        List<OHLC> list = null;



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
            URL url= null;
            if(period.equals("7")){
                url= new URL("https://min-api.cryptocompare.com/data/histohour?"+compare+"&limit=42&aggregate=4&toTs="+(System.currentTimeMillis()/1000)+"&e="+exchange+"&extraParams=your_app_name");
            }else if(period.equals("1")){
                url= new URL("https://min-api.cryptocompare.com/data/histohour?"+compare+"&limit=24&aggregate=1&toTs="+(System.currentTimeMillis()/1000)+"&e="+exchange+"&extraParams=your_app_name");
            }else {
                url = new URL("https://min-api.cryptocompare.com/data/histoday?" + compare + "&limit=" + period + "&aggregate=1&toTs=" + (System.currentTimeMillis() / 1000)+"&e="+exchange + "&extraParams=your_app_name");
            }
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
            System.out.println(full);
            System.out.println(resultCode);
            if(resultCode.equals("Success")) {
              list  = new ArrayList<>();

                JSONArray data = (JSONArray)full.get("Data");

                for (int i = 0; i < data.length(); i++) {

                    double vol=  (data.getJSONObject(i).getDouble("volumefrom"))+(data.getJSONObject(i).getDouble("volumeto"))/2;
                    double vol2=  0;
                    if((data.getJSONObject(i).getDouble("volumefrom"))>(data.getJSONObject(i).getDouble("volumeto"))){
                        vol2= (data.getJSONObject(i).getDouble("volumeto"));
                    }else {

                        vol2= (data.getJSONObject(i).getDouble("volumefrom"));
                    }


                    list.add(new OHLC(data.getJSONObject(i).getString("time"),
                            data.getJSONObject(i).getDouble("open"),
                            data.getJSONObject(i).getDouble("high"),
                            data.getJSONObject(i).getDouble("low"),
                            data.getJSONObject(i).getDouble("close"),
                            vol2));

                    Log.d("", "i= "+i+" "+  data.getJSONObject(i).getDouble("high"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<String> getExchangeList(String currency, int limit) {
        List<String> list = new ArrayList<>();

        try {

            //https://min-api.cryptocompare.com/data/top/pairs?fsym=XRP

            String compare= "";



            URL url= null;

            url= new URL("https://min-api.cryptocompare.com/data/top/pairs?fsym="+currency);

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
            Log.d("","JSON: "+result);
            String resultCode= full.getString("Response");
            if(resultCode.equals("Success")) {

                JSONArray data = (JSONArray)full.get("Data");

                for (int i = 0; i < data.length(); i++) {
                    list.add(data.getJSONObject(i).getString("toSymbol"));

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println( getExchangeListNew("OKCoin", 5)); ;

        return list;

    }



    public List<String> getExchangeListNew(String currency, int limit) {
        List<String> list = new ArrayList<>();

        try {

            //https://min-api.cryptocompare.com/data/top/pairs?fsym=XRP


            String result= Utils.str;
/*
            JSONObject full = new JSONObject(currency);
            Log.d("","JSON: "+result);

            JSONArray data = (JSONArray)full.get(currency);

            for (int i = 0; i < data.length(); i++) {
                JSONArray array = data.getJSONArray(i);
                list.add(array.getString(i));
            }*/



            JSONObject issueObj = new JSONObject(result);
            //Iterator iterator = issueObj.keys();
            Iterator<String> it = issueObj.keys();
            while(it.hasNext()){
              /*  String key = (String)iterator.next();
                JSONObject issue = issueObj.getJSONObject(key);
                issue.*/

                String key = it.next();

                try {
                    if (issueObj.get(key) instanceof JSONArray) {
                        JSONArray arry = issueObj.getJSONArray(key);
                        int size = arry.length();
                        for (int i = 0; i < size; i++) {

                           // parseJson(arry.getJSONObject(i));
                        }
                    } else if (issueObj.get(key) instanceof JSONObject) {
                       // parseJson(issueObj.getJSONObject(key));
                    } else {
                        System.out.println("" + key + " : " + issueObj.optString(key));
                    }
                } catch (Throwable e) {
                    System.out.println("" + key + " : " + issueObj.optString(key));
                    e.printStackTrace();

                }

                //  get id from  issue
                //String _pubKey = issue.optString("id");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;

    }
}
