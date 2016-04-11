package com.example.chrissebesta.travology;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

public class CountryWebView extends AppCompatActivity {

    WebView webView;

    String htmlPre = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"utf-8\"></head><body style='margin:0; pading:0; background-color: black;'>";
    String htmlCode =
            " <embed style='width:100%; height:100%' src='http://www.platipus.nl/flvplayer/download/1.0/FLVPlayer.swf?fullscreen=true&video=http://www.platipus.nl/flvplayer/download/pl-600.flv&autoplay=true' " +
                    "  autoplay='true' " +
                    "  quality='high' bgcolor='#000000' " +
                    "  name='VideoPlayer' align='middle'" + // width='640' height='480'
                    "  allowScriptAccess='*' allowFullScreen='true'" +
                    "  type='application/x-shockwave-flash' " +
                    "  pluginspage='http://www.macromedia.com/go/getflashplayer' />" +
                    "";
    String htmlPost = "</body></html>";

    //
//    String htmlPre = "<!DOCTYPE html>\n" +
//            "<html>\n" +
//            "<head>\n" +
//            "  <title>jVectorMap demo</title>\n" +
//            "  <link rel=\"stylesheet\" href=\"jquery-jvectormap-2.0.1.css\" type=\"text/css\" media=\"screen\"/>\n" +
//            "  <script src=\"jquery.js\"></script>\n" +
//            "  <script src=\"jquery-jvectormap-2.0.1.min.js\"></script>\n" +
//            "  <script src=\"jquery-jvectormap-world-mill-en.js\"></script>\n" +
//            "</head>\n" +
//            "<body>";
//    String htmlCode = "<div id=\"world-map\" style=\"width: 600px; height: 400px\"></div>\n" +
//            "  <script>\n" +
//            "    $(function(){\n" +
//            "      $('#world-map').vectorMap();\n" +
//            "    });\n" +
//            "  </script>";
//    String htmlPost = "</body>\n" +
//            "</html>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_web_view);
        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setAppCacheEnabled(false);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setCacheMode(webView.getSettings().LOAD_NO_CACHE);

        webView.loadDataWithBaseURL("null", htmlPre + htmlCode + htmlPost, "text/html", "UTF-8", null);
        webView.loadUrl("http://www.google.com");
    }
}
