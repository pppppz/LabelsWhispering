package com.app.labelswhispering;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.app.labelswhispering.Adapter.ListScheduleAdapter;
import com.app.labelswhispering.Function.DividerItemDecoration;
import com.app.labelswhispering.Function.isNetworkConnected;
import com.app.labelswhispering.Listener.RecyclerItemClickListener;
import com.app.labelswhispering.Model.Schedule;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class Schedule_Fragment extends Fragment {

    private FragmentActivity fragmentActivity;
    private SwipeRefreshLayout swipeRefresh;
    private List<Schedule> scheduleList = new ArrayList<>();
    private RecyclerView.Adapter mAdapter;
    private String TAG = Schedule_Fragment.class.getSimpleName();
    RecyclerItemClickListener recyclerItemClickListener = new RecyclerItemClickListener(fragmentActivity, new RecyclerItemClickListener.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            try {
                String scheduleID = scheduleList.get(position).getObjectId();
                Log.e(TAG, "objectId : " + scheduleID);
                Intent intent = new Intent(fragmentActivity, Detail_Schedule.class);
                intent.putExtra("scheduleId", scheduleID);
                startActivity(intent);
            } catch (Exception e) {
                Snackbar.make(MainActivity.rootLayout, R.string.try_again, Snackbar.LENGTH_SHORT).show();
                Log.e(TAG, "onItemClick : " + e.getMessage());
            }


        }
    });
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
        fragmentActivity = getActivity();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.schedule_fragment, container, false);
        /**declare list view and set adapter to list view (UI)**/
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_schedule);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(fragmentActivity, R.drawable.divider));

        ll_alarm = (LinearLayout) view.findViewById(R.id.ll_alarm);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(fragmentActivity);
        mRecyclerView.setLayoutManager(mLayoutManager);

        /** create adapter for put on array from class schedule **/
        mAdapter = new ListScheduleAdapter(fragmentActivity, scheduleList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addOnItemTouchListener(recyclerItemClickListener);

        /**set list view can listen the event when click some row **/
        //  mRecyclerView.addOnScrollListener(new RecyclerViewOnScrollListener());
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container_location);
        swipeRefresh.setOnRefreshListener(pullToRefreshListener);
        return view;
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
                MainActivity.progressBar.setVisibility(View.GONE);
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
                MainActivity.progressBar.setVisibility(View.GONE);
            }
        });
    }

    //query internet connection , if  online query in parse , if offline query in parse local store.
    private void Query() {
        MainActivity.progressBar.setVisibility(View.VISIBLE);

        if (new isNetworkConnected(fragmentActivity).CheckNow()) {
            updateListOnLine();
        } else {
            updateListOffline();
            Snackbar.make(MainActivity.rootLayout, R.string.offline_mode, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    })
                    .show();
        }
    }


}
