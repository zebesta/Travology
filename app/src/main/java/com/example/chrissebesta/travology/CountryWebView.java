package com.example.chrissebesta.travology;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import com.example.chrissebesta.travology.data.GeoContract;
import com.example.chrissebesta.travology.data.GeoDbHelper;

public class CountryWebView extends AppCompatActivity {

    WebView webView;
    StringBuilder build = new  StringBuilder();

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
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setAllowFileAccess(true);
//        webView.getSettings().setSupportZoom(false);
//        webView.getSettings().setAppCacheEnabled(false);
//        webView.getSettings().setAllowContentAccess(true);
//        webView.getSettings().setCacheMode(webView.getSettings().LOAD_NO_CACHE);
//        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }


        webView.setInitialScale(getScale());

        webView.setWebChromeClient(new WebChromeClient());

        final GeoDbHelper helper = new GeoDbHelper(getBaseContext());
        final SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT  * FROM " + GeoContract.GeoEntry.TABLE_NAME, null);
        cursor.moveToFirst();

        //Cycle through the SQL database and pull the country names for each entry and color them
        for (int i = 0; i < cursor.getCount(); i++) {
            String countryName = cursor.getString(cursor.getColumnIndex(GeoContract.GeoEntry.COLUMN_COUNTRY));
            build.append("['"+countryName+"', 700],");
            cursor.moveToNext();
        }
//        build.append("['Germany', 200],");
//        build.append("['United States', 700],");
//        build.append("['Brazil', 300],");
//        build.append("['Canada', 400],");
//        build.append("['France', 500]");

        drawMap();

        //webView.loadUrl("http://www.google.com");
    }

    void drawMap()
    {
        if(build.length() > 0)
        {
            String js = "<html><head>" +
                    "<script type='"+"text/javascript"+"' src='"+"https://www.google.com/jsapi"+"'></script>"+
                    "<script type='"+"text/javascript"+"'>" +
                    "google.load('"+"visualization"+"', '"+"1"+"', {packages:['"+"geochart"+"']});" +
                    "google.setOnLoadCallback(drawRegionsMap);" +
                    " function drawRegionsMap() {" +
                    "  var data = google.visualization.arrayToDataTable([" +
                    "['Country', 'Popularity']," + build +
                    "]);" +
                    "var options = {colors: ['#CB96CE', '#871F7B']};" +
                    "var chart = new google.visualization.GeoChart(document.getElementById('regions_div'));" +
                    "chart.draw(data, options);" +
                    "}" +
                    "</script>" +
                    "</head>" +
                    "<body>" +
                    "<div id='"+"regions_div"+"' style='"+"width:100%; height: 100%;"+"'></div>" +
                    "</body>" +
                    "</html>";

            Log.d("tag", js);

            webView.loadDataWithBaseURL("file:///android_asset/", js, "text/html","UTF-8",  null);
        }
        else
        {
            Toast.makeText(this, "No data found", Toast.LENGTH_LONG).show();
        }

    }

    private int getScale(){
        Display display=((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width=display.getWidth();
        Double val=new Double(width)/new Double(800);
        val=val*100d;

        return val.intValue();
    }

}
