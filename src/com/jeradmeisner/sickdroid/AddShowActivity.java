package com.jeradmeisner.sickdroid;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.jeradmeisner.sickdroid.data.TvdbSearchResult;
import com.jeradmeisner.sickdroid.fragments.AddShowFragment;
import com.jeradmeisner.sickdroid.utils.TVDBApi;

import java.util.ArrayList;
import java.util.List;

public class AddShowActivity extends SherlockFragmentActivity implements SearchView.OnQueryTextListener {

    private ArrayAdapter<TvdbSearchResult> adapter;
    private List<TvdbSearchResult> results;

    private ListView list;

    private String apiurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_show);

        list = (ListView)findViewById(R.id.search_result_list);

        Intent i = getIntent();
        apiurl = i.getStringExtra("apiurl");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        results = new ArrayList<TvdbSearchResult>();
        adapter = new ArrayAdapter<TvdbSearchResult>(this, android.R.layout.simple_list_item_1, results);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AddShowFragment frag = AddShowFragment.getInstance(adapter.getItem(position), apiurl);
                frag.show(getSupportFragmentManager(), "AddShow");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.add_show_menu, menu);
        MenuItem searchItem = (MenuItem)menu.findItem(R.id.search_shows);
        SearchView searchView = (SearchView)menu.findItem(R.id.search_shows).getActionView();
        searchView.setOnQueryTextListener(this);
        searchItem.expandActionView();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        results.clear();
        new SearchShowsTask().execute(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    private class SearchShowsTask extends AsyncTask<String, Void, List<TvdbSearchResult>> {
        @Override
        protected List<TvdbSearchResult> doInBackground(String... params) {
            return TVDBApi.getSearchResults(params[0]);
        }

        @Override
        protected void onPostExecute(List<TvdbSearchResult> tvdbSearchResults) {
            for (TvdbSearchResult r : tvdbSearchResults) {
                results.add(r);
            }
            adapter.notifyDataSetChanged();
        }
    }
}