package ranjit.com.chartapplication.quandle;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class RestResourse {
    private static RestResourse restResourse;

    private RestResourse() {
    }

    public static RestResourse getRestResourse() {
        if (restResourse == null) {
            restResourse = new RestResourse();
        }
        return restResourse;
    }


    public List<OHLC> getOHLC(String company, String collapse, String period) {

        List<OHLC> list = new ArrayList<>();

            try {
                URL url = new URL("https://www.quandl.com/api/v3/datasets/WIKI/" + company + ".json?collapse="+collapse+"&api_key=QwAcaE1Yz2oGLQ1XthEY&trim_start="+period+"&trim_end=2017-10-27");
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
                JSONObject dataset = full.getJSONObject("dataset");
                JSONArray data = dataset.getJSONArray("data");

                for (int i = 0; i < data.length(); i++) {
                    JSONArray array = data.getJSONArray(i);
                    list.add(new OHLC(array.getString(0), array.getDouble(1), array.getDouble(2)
                            , array.getDouble(3),  array.getDouble(4)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        return list;
    }
}
