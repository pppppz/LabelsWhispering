package com.app.labelswhispering.viewcontroller;


import android.content.Intent;
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

import com.app.labelswhispering.Class.DividerItemDecoration;
import com.app.labelswhispering.Class.isNetworkConnected;
import com.app.labelswhispering.Class.listener.RecyclerItemClickListener;
import com.app.labelswhispering.R;
import com.app.labelswhispering.model.Schedule;
import com.app.labelswhispering.model.adapter.ScheduleAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class ScheduleActivity extends AppCompatActivity {

    protected FloatingActionButton floatingActionButtonSchedule;
    FloatingActionButton.OnClickListener scheduleButtonPresed = new FloatingActionButton.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ScheduleActivity.this, addSchedule.class);
            startActivity(intent);

        }
    };
    private SwipeRefreshLayout swipeRefresh;
    private List<Schedule> scheduleList = new ArrayList<>();
    RecyclerItemClickListener recyclerItemClickListener = new RecyclerItemClickListener(getBaseContext(), new RecyclerItemClickListener.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            try {
                String scheduleID = scheduleList.get(position).getObjectId();
                Intent intent = new Intent(ScheduleActivity.this, editSchedule.class);
                intent.putExtra("scheduleId", scheduleID);
                startActivity(intent);
            } catch (Exception e) {
                Snackbar.make(MainActivity.rootLayout, R.string.try_again, Snackbar.LENGTH_SHORT).show();
            }


        }
    });
    private ProgressBar progressBar;
    private CoordinatorLayout rootLayout;
    private RecyclerView.Adapter mAdapter;
    private LinearLayout ll_alarm;
    SwipeRefreshLayout.OnRefreshListener pullToRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            swipeRefresh.setRefreshing(true);
            Query();
            swipeRefresh.setRefreshing(false);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_fragment);
        loadUI();
    }

    private void loadUI() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_schedule);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
        rootLayout = (CoordinatorLayout) findViewById(R.id.rootLayout_schedule);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_schedule);

        /**declare list view and set adapter to list view (UI)**/
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_schedule);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getBaseContext(), R.drawable.divider));

        ll_alarm = (LinearLayout) findViewById(R.id.ll_alarm);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        /** create adapter for put on array from class schedule **/
        mAdapter = new ScheduleAdapter(this, scheduleList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addOnItemTouchListener(recyclerItemClickListener);

        /**set list view can listen the event when click some row **/
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_container_location);
        swipeRefresh.setOnRefreshListener(pullToRefreshListener);

        floatingActionButtonSchedule = (FloatingActionButton) findViewById(R.id.fab_schedule);
        floatingActionButtonSchedule.setOnClickListener(scheduleButtonPresed);
    }

    @Override
    public void onResume() {
        super.onResume();
        scheduleList.clear();
        Query();

    }

    private void updateListOnLine() {

        //pattern query
        ParseQuery<Schedule> query = ParseQuery.getQuery(Schedule.class);
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.addDescendingOrder("createdAt");

        query.findInBackground(new FindCallback<Schedule>() {
            @Override
            public void done(List<Schedule> items, ParseException error) {
                //check if list task have some data = clear
                if (error == null) {
                    scheduleList.clear();
                    for (Schedule schedule : items) {
                        scheduleList.add(schedule);
                    }
                    if (items.size() == 0) {
                        ll_alarm.setVisibility(View.VISIBLE);
                    } else {
                        ll_alarm.setVisibility(View.INVISIBLE);
                    }
                    mAdapter.notifyDataSetChanged();
                } else {
                    Log.e("ParseException : ", String.valueOf(error));
                }
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    // When offline
    private void updateListOffline() {
        ParseQuery<Schedule> query = ParseQuery.getQuery(Schedule.class);
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<Schedule>() {
            @Override
            public void done(List<Schedule> savedByList, ParseException e) {
                if (e == null) {
                    // If there are results, update the list of posts
                    // and notify the adapter
                    scheduleList.clear();
                    for (Schedule schedule : savedByList) {
                        scheduleList.add(schedule);
                    }
                    if (savedByList.size() == 0) {
                        ll_alarm.setVisibility(View.VISIBLE);
                    } else {
                        ll_alarm.setVisibility(View.INVISIBLE);
                    }
                    ParseObject.pinAllInBackground(savedByList);
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
        progressBar.setVisibility(View.VISIBLE);

        if (new isNetworkConnected().Check(this)) {
            updateListOnLine();
        } else {
            updateListOffline();
            Snackbar.make(rootLayout, R.string.offline_mode, Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
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
