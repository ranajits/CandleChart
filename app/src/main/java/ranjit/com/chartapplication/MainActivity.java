package ranjit.com.chartapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebView webview;
        webview=(WebView)findViewById(R.id.webView);
        webview.getSettings().setJavaScriptEnabled(true);
       // webview.loadUrl("http://apps.programmerguru.com/examples/chennai.html");
       // webview.loadUrl("file:///android_asset/sample.html");

        String html_data ="<html>\n" +
                "<head>\n" +
                "\n" +
                "</head>\n" +
                "<body>\n" +
                " <script type=\"text/javascript\">\n" +
                "baseUrl = \"https://widgets.cryptocompare.com/\";\n" +
                "var scripts = document.getElementsByTagName(\"script\");\n" +
                "var embedder = scripts[ scripts.length - 1 ];\n" +
                "(function (){\n" +
                "var appName = encodeURIComponent(window.location.hostname);\n" +
                "if(appName==\"\"){appName=\"local\";}\n" +
                "var s = document.createElement(\"script\");\n" +
                "s.type = \"text/javascript\";\n" +
                "s.async = true;\n" +
                "var theUrl = baseUrl+'serve/v2/coin/chart?fsym=BTC&tsym=GOLD&period=1M';\n" +
                "s.src = theUrl + ( theUrl.indexOf(\"?\") >= 0 ? \"&\" : \"?\") + \"app=\" + appName;\n" +
                "embedder.parentNode.appendChild(s);\n" +
                "})();\n" +
                "</script>\n" +
                " \n" +
                "\n" +
                "</body>\n" +
                "</html>";
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadDataWithBaseURL("", html_data, "text/html", "UTF-8", "");


        Button next= (Button) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, TadingView.class));
            }
        });
    }
}
