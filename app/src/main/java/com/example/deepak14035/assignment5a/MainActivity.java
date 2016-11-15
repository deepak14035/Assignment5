package com.example.deepak14035.assignment5a;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.example.deepak14035.assignment5a.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    public static final String WEBPAGE_ID="webpagelink", WEBPAGE_DATA="webpagedata";
    private TextView webpageData;
    private EditText webpageText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        webpageData = (TextView)findViewById(R.id.webpageData);
        webpageText = (EditText)findViewById(R.id.webpageText);
        if(savedInstanceState!=null){
            String url=savedInstanceState.getString(WEBPAGE_ID);
            webpageText.setText(url);
            webpageData.setText(savedInstanceState.getString(WEBPAGE_DATA));
        }
    }

    public void onGoClick(View v){
        String url = webpageText.getText().toString();
        if(url.equals("")){
            url="https://www.iiitd.ac.in/about";
            webpageText.setText(url);
        }

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nf = cm.getActiveNetworkInfo();
        if(nf!=null && nf.isConnected()){
            new DownloadWebpageTask().execute(url);
        }
        else{
            webpageData.setText("no connection");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(WEBPAGE_ID, webpageText.getText().toString());
        outState.putString(WEBPAGE_DATA, webpageData.getText().toString());
    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... urls) {

            try {
                return downloadUrl(urls[0]);
            } catch (IOException e)
            {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Document doc = Jsoup.parse(result);
            webpageData.setText(doc.body().toString());
        }
    }

    private String downloadUrl(String myurl) throws IOException{
        InputStream is = null;
        int length = 1000;
        try{
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setReadTimeout(10000);//ms
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("asd", "response-"+response);
            is = conn.getInputStream();
            String data = readIt(is, length);
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            StringBuilder total = new StringBuilder();

            String line;
            while ((line = r.readLine()) != null) {
                total.append(line).append('\n');
            }
            Log.d("lkj", total.toString());
            return data+total.toString();
        }finally {
            if(is!=null)
                is.close();
        }
    }
    public String readIt(InputStream stram, int length) throws IOException, UnsupportedEncodingException{
        Reader reader=null;
        reader = new InputStreamReader(stram, "UTF-8");
        char[] buffer = new char[length];
        reader.read(buffer);
        return new String(buffer);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
