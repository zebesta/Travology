package com.example.chrissebesta.travology;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.chrissebesta.travology.data.GeoContract;
import com.example.chrissebesta.travology.data.GeoDbHelper;
import com.google.android.gms.common.api.GoogleApiClient;

public class CountryWebView extends AppCompatActivity {

    WebView webView;
    //ImageView loadingImage;
    ProgressBar progressBar;
    StringBuilder build = new StringBuilder();
    int width;
    int height;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_web_view);

        //Floating action button to return to city View mode
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.countryFab);
        fab.setImageResource(R.drawable.google_maps);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);
            }
        });

        webView = (WebView) findViewById(R.id.webview);
        //loadingImage = (ImageView) findViewById(R.id.imageLoading1);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        //enable required webview settings
        webView.getSettings().setJavaScriptEnabled(true);
        //done show zoom controls, but allow pinch to zoom gesture
        //webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);


        //allow for Caching, if caches content is available use that, geo chart usage does not change
        //The only thing that changes are the countries to be highlighted, cached content can handle this.
        webView.getSettings().setAppCachePath(getBaseContext().getCacheDir().toString());
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.getSettings().setAppCacheEnabled(true);
        if (!isNetworkAvailable()) { // loading offline
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }


        webView.setPadding(0, 0, 0, 0);
        //getScale();
        webView.setInitialScale(getScale());
        final Activity activity = this;


//WebViewclient instead of Chrome
//        webView.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                view.loadUrl(url);
//                return false;
//            }
//
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                super.onPageStarted(view, url, favicon);
//                Log.d("WEBCLIENT", "onPageStarted");
//                progressBar.setVisibility(View.VISIBLE);
//
//                //progressDialog.show();
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);
//                Log.d("WEBCLIENT", "onPageFinished");
//            }
//
//            @Override
//            public void onLoadResource(WebView view, String url) {
//                super.onLoadResource(view, url);
//                Log.d("WEBCLIENT", "onLoadResource");
//                if (webView.getProgress() == 100) {
//                    //progressDialog.dismiss();
//                    Log.d("WEBCLIENT", "onLoadResource 100%");
//                    progressBar.setVisibility(View.INVISIBLE);
//                    webView.setVisibility(View.VISIBLE);
//
//                }
//            }
//
//
//        });

        //Chrome Client
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                if (progress < 100){
                    Log.d("BUILD", "Loading....."+progress);
                    //loadingImage.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                }
                if (progress == 100){
                    Log.d("BUILD", "Done loading web client");
                    //fab.setImageResource(R.drawable.common_ic_googleplayservices);
                    //loadingImage.setVisibility(View.INVISIBLE);
                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            if ((webView != null)) {
                                Log.d("BUILD", "Running the runner!");

                                progressBar.setVisibility(View.INVISIBLE);
                                webView.setVisibility(View.VISIBLE);
                                // Draw the page into a bitmap
                                //webView.draw();

                                // After the bitmap is fully drawn, save the image
                                //webView.saveImage();

                                // Finish the helper activity
                                //finish();
                            }
                        }
                    };
                    handler.postDelayed(runnable, 1000);

                }
            }

        });

            //get access to SQL database to pull country names
            final GeoDbHelper helper = new GeoDbHelper(getBaseContext());
            final SQLiteDatabase db = helper.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT  * FROM " + GeoContract.GeoEntry.TABLE_NAME, null);
            cursor.moveToFirst();

            Log.d("BUILD","Beginning to build the build string for country view");
            //Cycle through the SQL database and pull the country names for each entry and color them
            for(
            int i = 0;
            i<cursor.getCount();i++)

            {
                String countryName = cursor.getString(cursor.getColumnIndex(GeoContract.GeoEntry.COLUMN_COUNTRY));
                //find number of times that country was visited
                Cursor cursorCountryVisitCount = db.rawQuery("SELECT * FROM " + GeoContract.GeoEntry.TABLE_NAME + " WHERE " + GeoContract.GeoEntry.COLUMN_COUNTRY + " = '" + countryName + "'", null);

                build.append("['" + countryName + "', " + cursorCountryVisitCount.getCount() + "],");
                cursor.moveToNext();
                cursorCountryVisitCount.close();
            }

            Log.d("BUILD","Done building the build string for country view, starting to draw map");


            //draw map using google Geo Chart and javascript
            drawMap();

            Log.d("BUILD","Done drawing map");
            //Zoom out and display the entire image to the user.
            webView.getSettings().

            setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

            webView.getSettings().

            setLoadWithOverviewMode(true);

            webView.getSettings().

            setUseWideViewPort(true);
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
                    "['Country', 'Cities Visited']," + build +
                    "]);" +
                    "var options = {colors: ['#5ae24b', '#228d16'],legend: 'none'};" +
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
                    "<div id='" + "regions_div" + "' style='" + "width:" + width + "px; height:" + height + "px; '></div>" +
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
        display.getRotation();
        Double val = 0d;

        if (width < height) {
            val = new Double(width) / new Double(800);
            val = val * 100d;
        } else {
            val = new Double(height) / new Double(800);
            val = val * 100d;
        }
        return val.intValue();
    }

    //Check if network is available
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
