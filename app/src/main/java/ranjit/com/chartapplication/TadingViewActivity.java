package ranjit.com.chartapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

public class TadingViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebView webview;
        webview=(WebView)findViewById(R.id.webView);
        webview.getSettings().setJavaScriptEnabled(true);

        String html_data ="<html>\n" +
                "<head>\n" +
                "\n" +
                "</head>\n" +
                "<body>\n" +
                "<script type=\"text/javascript\" src=\"https://s3.tradingview.com/tv.js\"></script>"+
                "<script type=\"text/javascript\">"+
                "new TradingView.widget({"+
                "width"+": 420,"+
                "height"+": 510,"+
                "symbol"+": \"NASDAQ:AAPL\","+
                "interval"+": \"D\","+
                "timezone"+": \"Etc/UTC\","+
                "theme"+": \"Light\","+
                "style"+": \"1\","+
                "locale"+": \"en\","+
                "toolbar_bg"+": \"#f1f3f6\","+
                "enable_publishing"+": false,"+
                "allow_symbol_change"+": true,"+
                "hideideas"+": true"+
                "});"+
                "</script>"+

                "</body>\n" +
                "</html>";
        webview.getSettings().setJavaScriptEnabled(true);

        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
       webSettings.setDomStorageEnabled(true);
      //  webSettings.setLoadWithOverviewMode(true);
      ///  webSettings.setUseWideViewPort(true);
      //  webSettings.setBuiltInZoomControls(true);
       // webSettings.setDisplayZoomControls(false);
      //  webSettings.setSupportZoom(true);
        webSettings.setDefaultTextEncodingName("utf-8");

       // webview.loadDataWithBaseURL("", html_data, "text/html", "UTF-8", "");
        webview.loadUrl("file:///android_asset/sample2.html");

        Button next= (Button) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TadingViewActivity.this, CandleActivity.class));
            }
        });


        String st= "" +
                "<script type=\"text/javascript\" src=\"https://s3.tradingview.com/tv.js\"></script>"+
                "<script type=\"text/javascript\">"+
                "new TradingView.widget({"+
                "width"+": 350,"+
                "height"+": 620,"+
                "symbol"+": \"NASDAQ:AAPL\","+
                "interval"+": \"D\","+
                "timezone"+": \"Etc/UTC\","+
                "theme"+": \"Light\","+
                "style"+": \"1\","+
                "locale"+": \"en\","+
                "toolbar_bg"+": \"#f1f3f6\","+
                "enable_publishing"+": false,"+
                "allow_symbol_change"+": true,"+
                "hideideas"+": true"+
                "});"+
                "</script>"+

                "" +
                "";


    }

}
