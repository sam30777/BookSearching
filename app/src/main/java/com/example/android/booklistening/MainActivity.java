package com.example.android.booklistening;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<data>> {
    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    ProgressBar pbar;
    private String requestedurl;
    private BookAdapter mAdapter;
    private int blaoder = 1;
    private ListView listView;
    private TextView textView;
    private ArrayList<data> lastbooklist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listView = (ListView) findViewById(R.id.activity_booklist);
        mAdapter = new BookAdapter(this, lastbooklist);
        setContentView(R.layout.activity_main);
        pbar = (ProgressBar) findViewById(R.id.progressabr);
        pbar.setVisibility(View.GONE);
        TextView textView = (TextView) findViewById(R.id.radomText);
        textView.setVisibility(View.GONE);
        Button button = (Button) findViewById(R.id.buttonn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager manage = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = manage.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    EditText textEntered = (EditText) findViewById(R.id.books);
                    String dataValue = textEntered.getText().toString().replace(" ", "");
                    requestedurl = "https://www.googleapis.com/books/v1/volumes?q=" + dataValue;
                    pbar.setVisibility(View.VISIBLE);
                    LoaderManager loaderManager = getLoaderManager();
                    loaderManager.initLoader(blaoder, null, MainActivity.this);
                    blaoder++;
                } else {
                    TextView textView = (TextView) findViewById(R.id.radomText);
                    textView.setText("No interent connection");
                    textView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void updareui(ArrayList<data> listdata) {
        lastbooklist = listdata;
        ListView listView = (ListView) findViewById(R.id.activity_booklist);
        listView.setEmptyView(findViewById(R.id.empty_list_view));
        mAdapter = new BookAdapter(this, lastbooklist);
        listView.setAdapter(mAdapter);
        pbar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mAdapter.clear();

    }

    @Override
    public void onLoadFinished(Loader<ArrayList<data>> loader, ArrayList<data> data) {

        {
            updareui(data);
        }

    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new BookAsynLoader(this, requestedurl);
    }

    private static class BookAsynLoader extends android.content.AsyncTaskLoader<ArrayList<data>> {
        private static String murl;

        public BookAsynLoader(Context context, String url) {
            super(context);
            murl = url;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        public ArrayList<data> loadInBackground() {
            if (murl == null) {
                return null;
            }
            URL url = createUrl(murl);
            String jsonresponse = "";
            try {
                jsonresponse = makeHttpRequest(url);
            } catch (IOException e) {
                Log.e(LOG_TAG, "error maing http request ", e);
            }
            ArrayList<data> newlist = extractDataBook(jsonresponse);
            return newlist;
        }

        private URL createUrl(String sendedurl) {
            URL url = null;
            try {
                url = new URL(sendedurl);
            } catch (MalformedURLException f) {
                Log.e(LOG_TAG, "error with creting url ", f);
            }
            return url;
        }

        private String makeHttpRequest(URL url) throws IOException {
            String jsonresponse = "";
            if (url == null) {
                return jsonresponse;
            }
            HttpURLConnection urlconnection = null;
            InputStream stream = null;
            try {
                urlconnection = (HttpURLConnection) url.openConnection();
                urlconnection.setRequestMethod("GET");
                urlconnection.setConnectTimeout(150000);
                urlconnection.setReadTimeout(100000);
                urlconnection.connect();
                if (urlconnection.getResponseCode() == 200) {
                    stream = urlconnection.getInputStream();
                    jsonresponse = readFromStream(stream);
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "error making connection ", e);
            }
            if (urlconnection != null) {
                urlconnection.disconnect();
            }
            if (stream != null) {
                stream.close();
            }
            return jsonresponse;
        }

        private String readFromStream(InputStream stream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (stream != null) {
                InputStreamReader streamReader = new InputStreamReader(stream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(streamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }

            }
            return output.toString();
        }

        private ArrayList<data> extractDataBook(String jsonresponse) {
            ArrayList<data> random = new ArrayList<data>();
            if (TextUtils.isEmpty(jsonresponse)) {
                return null;
            }
            try {
                JSONObject base = new JSONObject(jsonresponse);
                JSONArray itemarray = base.getJSONArray("items");
                for (int i = 0; i < itemarray.length(); i++) {
                    JSONObject baseobject = itemarray.getJSONObject(i);
                    JSONObject volume = baseobject.getJSONObject("volumeInfo");
                    String author="";
                    JSONArray bookauthors=volume.optJSONArray("authors");
                    if(bookauthors==null)
                    {
                        author="Unknown";
                    }
                   else if (volume.has("title") || volume.has("authors") || volume.has("description")) {
                        String title = volume.getString("title");
                         author = volume.getString("authors");
                        String description = volume.getString("description");
                        JSONObject img = volume.getJSONObject("imageLinks");
                        String image = img.getString("smallThumbnail");
                        data books = new data(title, author, description, image);
                        random.add(books);
                    }

                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "problem extracting the datat ", e);
            }
            return random;
        }
    }
}







