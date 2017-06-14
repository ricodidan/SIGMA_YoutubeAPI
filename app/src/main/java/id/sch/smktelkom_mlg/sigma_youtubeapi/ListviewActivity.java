package id.sch.smktelkom_mlg.sigma_youtubeapi;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ListviewActivity extends AppCompatActivity {

    public static String DATA_ID = "id";
    public static String DATA_TITLE = "title";
    public static String DATA_DESC = "description";
    public static String DATA_PUBLISH = "publishedAt";
    public static String DATA_THUMBNAILS = "url";
    // URL to get contacts JSON
    public static String url = "https://www.googleapis.com/youtube/v3/videos?part=snippet,contentDetails&id=" + Config.PLAYER_VIDEO + "&key=" + Config.YOUTUBE_API_KEY;
    ImageView img;
    ArrayList<HashMap<String, String>> dataList;
    private String TAG = ListviewActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);

        dataList = new ArrayList<>();

        lv = (ListView) findViewById(R.id.list);
        img = (ImageView) findViewById(R.id.imageView);


        new GetData().execute();
    }

    /**
     * Async task class to get json by making HTTP call
     */
    public class GetData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(ListviewActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray data = jsonObj.getJSONArray("items");

                    // looping through All Contacts
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject c = data.getJSONObject(i);

                        String id = c.getString(DATA_ID);
                        /*String name = c.getString(DATA_TITLE);
                        String desc = c.getString(DATA_DESC);*/

                        // Phone node is JSON Object
                        JSONObject snippet = c.getJSONObject("snippet");
                        String name = snippet.getString("title");
                        String desc = snippet.getString("description");
                        String publish = snippet.getString("publishedAt");

                        JSONObject thumnails = snippet.getJSONObject("thumbnails");
                        JSONObject thumbnails1 = thumnails.getJSONObject("default");
                        String url = thumbnails1.getString("url");

                        // tmp hash map for single dataview
                        HashMap<String, String> dataview = new HashMap<>();

                        // adding each child node to HashMap key => value
                        dataview.put(DATA_ID, id);
                        dataview.put(DATA_TITLE, name);
                        dataview.put(DATA_DESC, desc);
                        dataview.put(DATA_PUBLISH, publish);
                        dataview.put(DATA_THUMBNAILS, url);

                        // adding dataview to dataview list
                        dataList.add(dataview);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    ListviewActivity.this, dataList,
                    R.layout.list_item, new String[]{DATA_THUMBNAILS, DATA_TITLE, DATA_PUBLISH,
                    DATA_DESC}, new int[]{R.id.youtube_tview, R.id.id,
                    R.id.title, R.id.desc});

            lv.setAdapter(adapter);
        }

    }
}
