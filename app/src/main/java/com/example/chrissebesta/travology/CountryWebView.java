package com.example.chrissebesta.travology;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
    StringBuilder build = new StringBuilder();
    int width;
    int height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_web_view);

        //Floating action button to return to city View mode
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.countryFab);
        fab.setImageResource(R.drawable.google_maps);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);
            }
        });

        webView = (WebView) findViewById(R.id.webview);

        //enable required webview settings
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }


        webView.setPadding(0,0,0,0);
        webView.setInitialScale(getScale());
        webView.setWebChromeClient(new WebChromeClient());

        //get access to SQL database to pull country names
        final GeoDbHelper helper = new GeoDbHelper(getBaseContext());
        final SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT  * FROM " + GeoContract.GeoEntry.TABLE_NAME, null);
        cursor.moveToFirst();

        //Cycle through the SQL database and pull the country names for each entry and color them
        for (int i = 0; i < cursor.getCount(); i++) {
            String countryName = cursor.getString(cursor.getColumnIndex(GeoContract.GeoEntry.COLUMN_COUNTRY));
            build.append("['" + countryName + "', 1],");
            cursor.moveToNext();
        }


        //draw map using google Geo Chart and javascript
        drawMap();
        //Zoom out and display the entire image to the user.
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);

        //Close cursor that was loading SQL data.
        cursor.close();


    }

    void drawMap() {
        if (build.length() > 0) {
            String js = "<html><head>" +
                    "<script type='" + "text/javascript" + "' src='" + "https://www.google.com/jsapi" + "'></script>" +
                    "<script type='" + "text/javascript" + "'>" +
                    "google.load('" + "visualization" + "', '" + "1" + "', {packages:['" + "geochart" + "']});" +
                    "google.setOnLoadCallback(drawRegionsMap);" +
                    " function drawRegionsMap() {" +
                    "  var data = google.visualization.arrayToDataTable([" +
                    //Add countries that are dynamically loaded from the SQL database to the javascript command
                    "['Country', 'Popularity']," + build +
                    "]);" +
                    "var options = {colors: ['#CB96CE', '#871F7B'],legend: 'none'};" +
                    "var chart = new google.visualization.GeoChart(document.getElementById('regions_div'));" +
                     "chart.draw(data, options);" +
//                    "function resize () {" +
//                    "var chart = new google.visualization.LineChart(document.getElementById('chart_div'));" +
//                    "chart.draw(data, options);" +
//                    "}" +
//
//                    "window.onload = resize();" +
//                    "window.onresize = resize;" +
                    "}" +
                    "</script>" +
                    "</head>" +
                    "<body>" +

                    //Control size of image returned from Geo Charts here
                    //"<div id='" + "regions_div" + "' style='" + "width:"+width+"; height:"+height+";"+"'></div>" +
                    //"<div id='"+"regions_div"+"' style='"+"width:100%; height: 100%;"+"'></div>" +
                    "<div id='"+"regions_div"+"' style='"+"width:"+width + "px; height:100%;"+"'></div>" +
                    "</body>" +
                    "</html>";

            Log.d("tag", js);

            webView.loadDataWithBaseURL("file:///android_asset/", js, "text/html", "UTF-8", null);
        } else {
            Toast.makeText(this, "No data found", Toast.LENGTH_LONG).show();
        }

    }

    private int getScale() {
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
        Double val = new Double(width) / new Double(800);
        val = val * 100d;

        return val.intValue();
    }

}
