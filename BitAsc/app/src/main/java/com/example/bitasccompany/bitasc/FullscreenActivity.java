package com.example.bitasccompany.bitasc;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final String TAG = FullscreenActivity.class.getSimpleName();
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */

    private String ClickIdTask;
    private ArrayList<String> IdArray;


    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            //do your request in here so that you don't interrupt the UI thread
            try {
                return downloadContent(params[0],params[1]);
            } catch (IOException e) {
                return "Unable to retrieve data. URL may be invalid.";
            }
        }
        private ArrayList<String> Name;
        private ArrayList<Integer> HaveChl;
        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "The cred is: " + result);
            try {
                JSONArray jsonObj = new JSONArray(result);
                JSONArray answer1 = jsonObj.getJSONArray(0);
                JSONArray answer = answer1.getJSONArray(2);
                Log.d(TAG, "The result is: " + jsonObj.toString());
                //Iterator<Object> keys = jsonObj.keys();
                Name = new ArrayList<>();
                HaveChl = new ArrayList<>();
                IdArray = new ArrayList<>();
                for(int i=0; i<answer.length(); i++) {
                    //Name.add(answer.getString());
                    JSONObject taskobj = answer.getJSONObject(i);
                    Name.add(taskobj.getString("taskName"));
                    HaveChl.add(taskobj.getInt("children"));
                    IdArray.add(taskobj.getString("id"));
                }
                ArrayAdapter<String> arrayAdapter =
                        new ArrayAdapter<String>(FullscreenActivity.this,
                                android.R.layout.simple_list_item_1,
                                Name);
                listView1.setAdapter(arrayAdapter);
                listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        Log.d(TAG, "itemClick: position = " + position + ", id = "
                                + id);
                        if (HaveChl.get(position)>0) {
                            ClickIdTask = IdArray.get(position);
                            new DownloadTask().execute("http://api.bit-ask.com/index.php/event/all/", "task/subtasks");
                            }
                        }
                });
                Log.d(TAG, "All id: " + Name);

            } catch (JSONException e) {
                e.printStackTrace();
                new DownloadTask().execute("http://api.bit-ask.com/index.php/event/all/", "task/subtasks");
            }


            //TextView myAwesomeTextView = (TextView)findViewById(R.id.textView2);
            //myAwesomeTextView.setText(result);
        }
    }
    private String downloadContent(String myurl, String task) throws IOException {
        InputStream is = null;
        int length = 60000;
        Log.d(TAG, "The task is: " + task);
        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(100000 /* milliseconds */);
            conn.setConnectTimeout(150000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            conn.setRequestProperty("Content-Type", "application/json;charset=windows-1251");
           // conn.setRequestProperty("Accept", "application/json;charset=UTF-8");
            conn.setRequestProperty("AUTHORIZATION",getIntent().getExtras().getString("AuthToken"));
            JSONArray cred   = new JSONArray();
            ArrayList <String> Arr = new ArrayList<>();
            UUID uuid = UUID.randomUUID();
            String randomUUIDString = uuid.toString();
            //Arr.add(randomUUIDString);
            //String taskname = new String('task/openedtasks');
           // Arr.add("false");
           // Arr.add(taskname);
            conn.connect();
            cred.put(uuid);
            cred.put(false);
            cred.put(task);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            if (task == "task/subtasks") {
                JSONObject param = new JSONObject();
                String ph = ClickIdTask;
                param.put("parentId",ph);
                cred.put(param);
                wr.write('['+cred.toString()+']');
            }else{
                wr.write('['+cred.toString()+']');
            }

            wr.flush();

            int response = conn.getResponseCode();
            Log.d(TAG, "The response is: " + response);
            Log.d(TAG, "The cred is: " + '['+cred.toString()+']');
            //Log.d(TAG, "The token is: " + getIntent().getExtras().getString("AuthToken"));
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = convertInputStreamToString(is, length);
            return contentAsString;
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return null;
    }
    private ListView listView1;
    public String convertInputStreamToString(InputStream stream, int length) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[length];
        reader.read(buffer);
        return new String(buffer);
    }
    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new DownloadTask().execute("http://api.bit-ask.com/index.php/event/all/","task"+'/'+"openedtasks");
        //String ReturnString = UpTask.doInBackground();

        setContentView(R.layout.activity_fullscreen);
        listView1 = (ListView) findViewById(R.id.listView);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);

        mDatabaseHelper = new DatabaseHelper(this, "mydatabase.db", null, 1);

        mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        // Задайте значения для каждого столбца
        values.put(DatabaseHelper.CAT_NAME_COLUMN, "Рыжик");
        values.put(DatabaseHelper.PHONE_COLUMN, "4954553443");
        values.put(DatabaseHelper.AGE_COLUMN, "5");
        // Вставляем данные в таблицу
        mSqLiteDatabase.insert("cats", null, values);



    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
