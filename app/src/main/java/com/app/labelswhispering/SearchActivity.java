package com.app.labelswhispering;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.app.labelswhispering.Adapter.ListMedicineAdapter;
import com.app.labelswhispering.Function.DividerItemDecoration;
import com.app.labelswhispering.Model.Medicine;
import com.app.labelswhispering.Service.isNetworkConnected;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class SearchActivity extends AppCompatActivity {

    public static LinearLayoutManager layoutManager;
    private final String TAG = SearchActivity.class.getSimpleName();
    private List<Medicine> medicineList = new ArrayList<>();
    private SearchView searchView;
    private TextView tvShowNoResults;
    private CoordinatorLayout rootLayout_search;

    private RecyclerView.Adapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity_layout);
        handleIntent(getIntent());

        rootLayout_search = (CoordinatorLayout) findViewById(R.id.rootLayout_Search);
        Toolbar toolbar_search = (Toolbar) findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar_search);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tvShowNoResults = (TextView) findViewById(R.id.tvNotFound);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_search);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, R.drawable.divider));

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();

        /** create adapter for put on array from class schedule **/
        mAdapter = new ListMedicineAdapter(this, medicineList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);

        /**set list view can listen the event when click some row **/
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String medicineID = medicineList.get(position).getObjectId();
                Log.e(TAG, "objectId : " + medicineID);
                Intent intent = new Intent(SearchActivity.this, MedicineDetail_Activity.class);
                intent.putExtra("medicineID", medicineID);
                startActivity(intent);
                finish();
            }
        }));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String word = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(word);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void doMySearch(String word) {
        searchView.setQuery(word, false);

        if (new isNetworkConnected(this).CheckNow()) {
            //search
            ParseQuery<Medicine> query = ParseQuery.getQuery(Medicine.class);
            query.whereContains("name", word);
            query.addAscendingOrder("name");

            query.findInBackground(new FindCallback<Medicine>() {
                @Override
                public void done(List<Medicine> items, ParseException error) {
                    //check if list task have some data = clear
                    if (error == null) {
                        medicineList.clear();
                        if (items.size() != 0) {
                            tvShowNoResults.setVisibility(View.GONE);
                            for (int i = 0; i < items.size(); i++) {
                                medicineList.add(items.get(i));
                            }
                        } else {
                            tvShowNoResults.setVisibility(View.VISIBLE);
                        }
                        mAdapter.notifyDataSetChanged();
                        Log.d(TAG, "updated data");
                    } else {

                        Log.e(TAG, "ParseException : " + error);
                    }

                }
            });

        } else {
            Snackbar.make(rootLayout_search, "Please check your connection.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Check now!", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openWIFI();
                        }
                    })
                    .show();
        }
    }

    private void openWIFI() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.android.settings", "com.android.settings.wifi.WifiSettings");
        startActivity(intent);
    }


}
