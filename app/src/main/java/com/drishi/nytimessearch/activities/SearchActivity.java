package com.drishi.nytimessearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.drishi.nytimessearch.R;
import com.drishi.nytimessearch.adapters.ArticleArrayAdapter;
import com.drishi.nytimessearch.fragments.FiltersFragment;
import com.drishi.nytimessearch.models.Article;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity implements FiltersFragment.FiltersFragmentListener{

    EditText etQuery;
    GridView gvResults;
    Button btnSearch;

    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;

    // filter values
    Date beginDateVal;
    String sortVal;
    ArrayList<String> filtersVal;

    private static final String API_KEY = "8ebaee6e9c2b4a85b486229445f73d5b";
    private static final String URL = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupViews();


    }

    public void setupViews() {
        etQuery = (EditText) findViewById(R.id.etQuery);
        gvResults = (GridView) findViewById(R.id.gvResults);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        articles = new ArrayList<>();
        adapter = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(adapter);

        // initialize values for fitler
        sortVal = "Oldest First";
        filtersVal = new ArrayList<>();

        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //create an intent to display the article
                Intent intent = new Intent(getApplicationContext(), ArticleActivity.class);
                //get the article to display
                Article article = articles.get(i);

                intent.putExtra("article", article);
                //pass in that activity into intent
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
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

    public void onArticleSearch(View view) {
        String query = etQuery.getText().toString();

        //Toast.makeText(this, "Searching for " + query, Toast.LENGTH_SHORT).show();
        AsyncHttpClient client = new AsyncHttpClient();

        adapter.clear();
        RequestParams params = getParams();
        params.put("page", 0);
        params.put("q", query);

        client.get(URL, params, requestHandler);

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
        RequestParams params = getParams();
        adapter.clear();
        client.get(URL, params, requestHandler);
    }

    public JsonHttpResponseHandler requestHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            JSONArray articleJsonResults = null;
            try {
                articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                adapter.addAll(Article.fromJsonArray(articleJsonResults));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    public RequestParams getParams() {
        RequestParams params = new RequestParams();
        params.put("api-key", API_KEY);
        if (beginDateVal != null) {
            params.put("begin_date", new SimpleDateFormat("yyyyMMdd").format(beginDateVal));
            params.put("end_date", new SimpleDateFormat("yyyyMMdd").format(new Date()));
        }

        if (sortVal.length() != 0) {
            params.put("sort", resolveSortOrder(sortVal));
        }

        String filtersString = "";
        for (String fitler: filtersVal) {
            filtersString += "\"" + fitler + "\" ";
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
}
