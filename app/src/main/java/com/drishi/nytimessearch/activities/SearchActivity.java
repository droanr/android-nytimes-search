package com.drishi.nytimessearch.activities;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.drishi.nytimessearch.R;
import com.drishi.nytimessearch.adapters.ArticleAdapter;
import com.drishi.nytimessearch.fragments.FiltersFragment;
import com.drishi.nytimessearch.interfaces.EndlessRecyclerViewScrollListener;
import com.drishi.nytimessearch.models.Article;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class SearchActivity extends AppCompatActivity implements FiltersFragment.FiltersFragmentListener{

    RecyclerView rvResults;

    ArrayList<Article> articles;
    ArticleAdapter adapter;

    // filter values
    Date beginDateVal;
    String sortVal;
    ArrayList<String> filtersVal;

    // Menu item for progress bar
    MenuItem menuItemProgress;
    MenuItem menuItemFilters;

    SearchView searchView;

    private static final String API_KEY = "8ebaee6e9c2b4a85b486229445f73d5b";
    private static final String URL = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackground(new ColorDrawable(getResources().getColor(android.R.color.holo_red_dark)));
        setupViews();
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }

    public void setupViews() {
        rvResults = (RecyclerView) findViewById(R.id.rvResults);
        articles = new ArrayList<>();
        adapter = new ArticleAdapter(this, articles);
        rvResults.setAdapter(adapter);
        rvResults.setItemAnimator(new SlideInUpAnimator());
        rvResults.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        // initialize values for filter
        sortVal = "Oldest First";
        filtersVal = new ArrayList<>();
        rvResults.addOnScrollListener(new EndlessRecyclerViewScrollListener((StaggeredGridLayoutManager) rvResults.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                AsyncHttpClient client = new AsyncHttpClient();

                RequestParams params = getParams(page);

                if (checkConnection()) {
                    client.get(URL, params, requestHandler);
                }
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        menuItemProgress = menu.findItem(R.id.miActionProgress);
        menuItemFilters = menu.findItem(R.id.action_settings);

        // Extract the action-view from the menu item
        ProgressBar v =  (ProgressBar) MenuItemCompat.getActionView(menuItemProgress);
        // Return to finish
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                AsyncHttpClient client = new AsyncHttpClient();

                RequestParams params = getParams(0);
                params.put("q", query);
                boolean isConnected = checkConnection();

                if (isConnected) {
                    articles.clear();
                    client.get(URL, params, requestHandler);
                    searchView.clearFocus();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

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

    public void onSettingsClick(MenuItem item) {
        FragmentManager fm = getSupportFragmentManager();
        String date;
        if (beginDateVal != null) {
            date = new SimpleDateFormat("MMMM dd, yyyy").format(beginDateVal);
        } else {
            date = "";
        }
        FiltersFragment filtersFragment = FiltersFragment.newInstance("     Search Filters", date, sortVal, filtersVal);
        filtersFragment.show(fm, "fragment_filters");
    }

    @Override
    public void onSaveFilters(ArrayList<String> filters, String sort, Date date) {
        // Store filter values
        filtersVal = filters;
        sortVal = sort;
        beginDateVal = date;

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = getParams(0);
        String query = searchView.getQuery().toString().trim();
        if (query.length() != 0) {
            params.put("q", searchView.getQuery().toString());
        }
        boolean isConnected = checkConnection();

        if (isConnected) {
            articles.clear();
            client.get(URL, params, requestHandler);
            Toast.makeText(this, "Filters saved and applied to search", Toast.LENGTH_SHORT).show();
        }
    }

    public JsonHttpResponseHandler requestHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            JSONArray articleJsonResults = null;
            try {
                showProgressBar();
                articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                articles.addAll(Article.fromJsonArray(articleJsonResults));
                adapter.notifyDataSetChanged();
                hideProgressBar();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    public RequestParams getParams(int page) {
        RequestParams params = new RequestParams();
        params.put("api-key", API_KEY);
        if (beginDateVal != null) {
            params.put("begin_date", new SimpleDateFormat("yyyyMMdd").format(beginDateVal));
            params.put("end_date", new SimpleDateFormat("yyyyMMdd").format(new Date()));
        }

        params.put("page", page);

        if (sortVal.length() != 0) {
            params.put("sort", resolveSortOrder(sortVal));
        }

        String filtersString = "";
        for (String filter: filtersVal) {
            filtersString += "\"" + filter + "\" ";
        }

        if (filtersString.length() != 0) {
            params.put("fq", "news_desk:(" + filtersString.substring(0, filtersString.length() - 1) + ")");
        }

        return params;
    }

    public String resolveSortOrder(String sortOrder) {
        switch (sortOrder){
            case "Oldest First":
                return "oldest";
            case "NewestFirst":
                return "newest";
            default:
                return "oldest";
        }
    }

    public void showProgressBar() {
        // Show progress item
        menuItemProgress.setVisible(true);
    }

    public void hideProgressBar() {
        // Hide progress item
        menuItemProgress.setVisible(false);
    }

    public boolean checkConnection() {
        if (!isNetworkAvailable() || !isOnline()) {
            try {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

                alertDialog.setTitle("No Connection");
                alertDialog.setMessage("Internet not available. Check your internet connectivity and try again");
                alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                alertDialog.setNegativeButton("OK", null);
                alertDialog.show();
            }
            catch(Exception e)
            {
                Log.d("DEBUG", "Show Dialog: "+e.getMessage());
            }
            return false;
        }
        return true;
    }
}
