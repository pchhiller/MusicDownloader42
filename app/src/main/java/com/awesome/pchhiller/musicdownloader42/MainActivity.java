package com.awesome.pchhiller.musicdownloader42;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;

import android.app.SearchManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.awesome.pchhiller.musicdownloader42.Adapters.SongsAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.R.attr.name;
import static com.awesome.pchhiller.musicdownloader42.R.id.song_name;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    public RecyclerView mRecyclerView;
    public String currentText;
    private String songToDownload;
    Boolean isHandlingIntent = false;
    private static final Pattern TITLE_TAG =
            Pattern.compile("\\<title>(.*)\\</title>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String url1 = "https://chiller-deploy.herokuapp.com/getDownloadLink/?name=";

        String tempSongUrl = "http://m1.chiasenhac.vn/mp3/us-uk/us-dance-remix/you~axol-x-alex-skrindo~tsvq5cs0qehv4n.html";
        new GetUrlFromUrl(this, "test", true).execute(url1 + tempSongUrl);

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {

                // Toast.makeText(getApplicationContext(),"You have permission to write. Awesome!",Toast.LENGTH_SHORT).show();

            } else {
                requestPermission(); // Code for permission
            }
        }
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        Intent intent = getIntent();
        Uri data = intent.getData();
        try {
            String scheme = data.getScheme(); // "http"
            String host = data.getHost(); // "twitter.com"
            Log.v("host name", host);
            if (host.contains("spotify")) {
                Toast.makeText(getApplicationContext(), "Getting song data. Please wait :)", Toast.LENGTH_SHORT).show();
                Log.v("Spotify detected", host);
                List<String> params = data.getPathSegments();
                String first = params.get(0); // "status"
                String second = params.get(1); // "1234"
                Log.v("main", second);
                GetNameFromSpotify to_run1 = new GetNameFromSpotify();
                isHandlingIntent = true;

                to_run1.execute("https://open.spotify.com/track/" + second);

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void onClickCalled(String songUrl, String songName) {
        String url1 = "https://chiller-deploy.herokuapp.com/getDownloadLink/?name=";
        new GetUrlFromUrl(this, songName, false).execute(url1 + songUrl);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        // Retrieve the SearchView and plug it into SearchManager
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconified(false);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.v(TAG, query);
                mRecyclerView.setVisibility(View.VISIBLE);
                songToDownload=query;
                String url1 = "https://chiller-deploy.herokuapp.com/?remix=" + 1 + "&name=" + query;
                new GetDataTask(MainActivity.this).execute(url1);
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentText = newText;
                mRecyclerView.setVisibility(View.INVISIBLE);
                return false;
            }
        });
        return true;
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }


    public class GetDataTask extends AsyncTask<String, Void, String> {
        final ProgressDialog nDialog;
        public Context mContext;
        public int times_run;

        public GetDataTask(Context context) {
            nDialog = new ProgressDialog(context);
            mContext = context;
            times_run = 0;

        }

        @Override
        protected void onPreExecute() {
            nDialog.setMessage("Loading..");
            nDialog.setTitle("Get Data");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            times_run = times_run + 1;
            String url = params[0];
            OkHttpClient client = new OkHttpClient();

            Log.v(TAG, "GetDataTask" + url);
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response == null) {
                return null;
            }
            try {
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            nDialog.dismiss();
            if (result != null) {


                SongsDataModel[] data = new Gson().fromJson(result, SongsDataModel[].class);
                SongsDataModel firstItem= new SongsDataModel(songToDownload,"");
                List<SongsDataModel> listOfSongs = Arrays.asList(data);

                List<SongsDataModel> list = new LinkedList<SongsDataModel>(Arrays.asList(data));
                list = list.subList(0, listOfSongs.size() - 1);
                list.add(0,firstItem);



                SongsDataModel lastItem = listOfSongs.get(listOfSongs.size() - 1);
                String url = lastItem.getSongUrl();
                listOfSongs = listOfSongs.subList(0, listOfSongs.size() - 1);

//            listOfSongs.remove(listOfSongs.size()-1);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
                mRecyclerView.setLayoutManager(layoutManager);
                mRecyclerView.setAdapter(new SongsAdapter(list, mContext));
                mRecyclerView.setLayoutManager(layoutManager);
                doDownload(lastItem.getSongName(), lastItem.getSongUrl());


                Log.v("Download link", url);


            } else {

                if (times_run < 5) {
                    Toast.makeText(MainActivity.this, "Some Error occured. Retry again please", Toast.LENGTH_SHORT).show();

//                View test = (View) findViewById(R.id.submit);
//                myClickHandler(test);
                } else {
                    Toast.makeText(MainActivity.this, "Tried Enough times. Maybe, Check your net?", Toast.LENGTH_LONG).show();

                }
            }

        }

    }

    public void doDownload(String name, String url) {
//        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
//        request.setDescription("Downloading your song .");
//        request.setTitle(name);
//// in order for this if to run, you must use the android 3.2 to compile your app
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            request.allowScanningByMediaScanner();
//            request.setVisibleInDownloadsUi(true);
//            request.setMimeType("audio/mpeg");
//            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//        }
//        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, name + ".mp3");
//
//// get download service and enqueue file
//        final DownloadManager.Request request1 = request;
//        final DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        final String toDown=url;
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
        builder1.setMessage("Do you want to download " + name);
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "Yes Please",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
//                            name = (EditText) findViewById(song_name);
//                            name.setText("");
//                        manager.enqueue(request1);

//
//                        }
                        Intent i = new Intent();
                        i.setPackage("com.android.chrome");
                        i.setAction(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(toDown));
                        startActivity(i);
                        if (isHandlingIntent) {
                            if (android.os.Build.VERSION.SDK_INT >= 21) {
                                finishAndRemoveTask();
                            } else {
                                finish();
                            }
                        }

                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();


    }


    public class GetUrlFromUrl extends AsyncTask<String, Void, String> {
        final ProgressDialog nDialog;
        public Context mContext;
        String name;
        Boolean isFake;

        public GetUrlFromUrl(Context context, String name, Boolean fake) {
            nDialog = new ProgressDialog(context);
            mContext = context;
            this.name = name;
            isFake = fake;


        }

        @Override
        protected void onPreExecute() {
            if (!isFake) {
                nDialog.setMessage("Loading..");
                nDialog.setTitle("Get Data");
                nDialog.setIndeterminate(false);
                nDialog.setCancelable(true);
                nDialog.show();
            }
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            OkHttpClient client = new OkHttpClient();


            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response == null) {
                return null;
            }
            try {
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            nDialog.dismiss();
            if (result != null) {
                if (!isFake)
                    doDownload(name, result);


            }

        }
    }


    public class GetNameFromSpotify extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            OkHttpClient client = new OkHttpClient();


            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response == null) {
                return null;
            }
            try {
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            Log.v(TAG, result);
            Matcher matcher = TITLE_TAG.matcher(result);
            if (matcher.find()) {
                /* replace any occurrences of whitespace (which may
                 * include line feeds and other uglies) as well
                 * as HTML brackets with a space */
                String name1 = matcher.group(1).replaceAll("[\\s\\<>]+", " ").trim();
                String[] parts = name1.split(",");
                String to_down = parts[0].replaceAll("’", "");
                Log.v("Spotify name is ", to_down);
                to_down = to_down.replace("&#039;", "%27");
                String url1 = null;
                try {
                    url1 = "https://chiller-deploy.herokuapp.com/?remix=" + 1 + "&name=" + to_down;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new GetDataTask(MainActivity.this).execute(url1);


            }
        }
    }


    public void alternateDownloadClicked(String name) {


        new AlternateDownload().execute("https://chiller-deploy.herokuapp.com/test/?name=" + name);
    }

    public class AlternateDownload extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            OkHttpClient client = new OkHttpClient();


            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response == null) {
                return null;
            }
            try {
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            Log.v("result logged", result);

            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(result));
            startActivity(i);

        }
    }
}