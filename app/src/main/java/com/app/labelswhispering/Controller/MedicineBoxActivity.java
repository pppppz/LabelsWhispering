
package com.app.labelswhispering.Controller;


import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.app.labelswhispering.Model.Adapter.MedicineAdapter;
import com.app.labelswhispering.Model.Medicine;
import com.app.labelswhispering.OtherClass.DividerItemDecoration;
import com.app.labelswhispering.OtherClass.Listener.RecyclerItemClickListener;
import com.app.labelswhispering.OtherClass.isNetworkConnected;
import com.app.labelswhispering.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MedicineBoxActivity extends AppCompatActivity {
    FloatingActionButton.OnClickListener fabOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MedicineBoxActivity.this, AddSchedule_Activity.class);
            startActivity(intent);
        }
    };
    private CoordinatorLayout rootLayout;
    private String TAG;
    private List<Medicine> medicineList = new ArrayList<>();
    RecyclerItemClickListener recyclerItemClickListener = new RecyclerItemClickListener(getBaseContext(), new RecyclerItemClickListener.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            try {
                String medicineID = medicineList.get(position).getObjectId();
                String medicineName = medicineList.get(position).getName();
                Intent intent = new Intent(MedicineBoxActivity.this, MedicineDetails_Activity.class);
                intent.putExtra("medicineID", medicineID);
                intent.putExtra("medicineName", medicineName);
                startActivity(intent);
            } catch (Exception e) {
                Snackbar.make(rootLayout, R.string.try_again, Snackbar.LENGTH_SHORT).show();
            }


        }
    });
    private ProgressBar progressBar;
    private RecyclerView.Adapter mAdapter;
    private LinearLayout ll_medicineBox;
    SwipeRefreshLayout.OnRefreshListener pullToRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            Query();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = MedicineBoxActivity.class.getSimpleName();
        setContentView(R.layout.medicine_box);
        loadUI();
    }

    private void loadUI() {

        rootLayout = (CoordinatorLayout) findViewById(R.id.rootLayout_medicineBox);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_medicineBox);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        //Progress bar
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_medicineBox);

        ll_medicineBox = (LinearLayout) findViewById(R.id.ll_medicine_box);

        /**floating button**/

        FloatingActionButton fabBtn = (FloatingActionButton) findViewById(R.id.fab_medicineBox);
        if (fabBtn != null) {
            fabBtn.setOnClickListener(fabOnClick);
            fabBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.fab_normal_color)));
            fabBtn.setRippleColor(getResources().getColor(R.color.fab_pressed_color));
        }


        /**declare list view and set adapter to list view (UI)**/
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_home);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, R.drawable.divider));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        /**set list view can listen the event when click some row **/
        SwipeRefreshLayout swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_container_home);
        swipeRefresh.setOnRefreshListener(pullToRefreshListener);

        /** create adapter for put on array from class task **/
        mAdapter = new MedicineAdapter(this, medicineList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addOnItemTouchListener(recyclerItemClickListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        Query();
    }

    private void updateListOnLine() {
        ParseUser user = ParseUser.getCurrentUser();
        ParseRelation<Medicine> relation = user.getRelation("savedMedicine");
        ParseQuery<Medicine> query = relation.getQuery();
        user.pinInBackground();
        // Run the query
        query.findInBackground(new FindCallback<Medicine>() {

            @Override
            public void done(List<Medicine> savedByList, ParseException e) {
                if (e == null) {
                    // If there are results, update the list of posts
                    // and notify the adapter
                    for (Medicine medicine : savedByList) {
                        medicineList.add(medicine);
                        Log.e(TAG, "medicine name : " + medicine.get("name"));
                    }
                    if (savedByList.size() == 0) {
                        ll_medicineBox.setVisibility(View.VISIBLE);
                    } else {
                        ll_medicineBox.setVisibility(View.INVISIBLE);
                    }
                    ParseObject.pinAllInBackground(savedByList);
                } else {
                    Log.e("Post retrieval", "Error: " + e.getMessage());
                    Log.e(TAG, "Internet no connection");
                }
                mAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    // When offline
    private void updateListOffline() {
        ParseUser user = ParseUser.getCurrentUser();
        ParseRelation<Medicine> relation = user.getRelation("savedMedicine");
        ParseQuery<Medicine> query = relation.getQuery();
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<Medicine>() {
            @Override
            public void done(List<Medicine> savedByList, ParseException e) {
                if (e == null) {
                    // If there are results, update the list of posts
                    // and notify the adapter
                    for (Medicine medicine : savedByList) {
                        medicineList.add(medicine);
                    }
                    if (savedByList.size() == 0) {
                        ll_medicineBox.setVisibility(View.VISIBLE);
                    } else {
                        ll_medicineBox.setVisibility(View.INVISIBLE);
                    }
                } else {
                    Log.e("Post retrieval", "Error: " + e.getMessage());
                }
                mAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    //query internet connection , if  online query in parse , if offline query in parse local store.
    private void Query() {
        medicineList.clear();
        progressBar.setVisibility(View.VISIBLE);
        if (new isNetworkConnected().Check(this)) {
            updateListOnLine();
        } else {
            updateListOffline();
            Snackbar.make(rootLayout, R.string.offline_mode, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    })
                    .show();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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
}