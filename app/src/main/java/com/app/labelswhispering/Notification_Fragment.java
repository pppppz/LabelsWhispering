package com.app.labelswhispering;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.labelswhispering.Foursquare.Model.Venue;
import com.app.labelswhispering.Function.DividerItemDecoration;
import com.app.labelswhispering.Listener.RecyclerViewOnScrollListener;

import java.util.ArrayList;
import java.util.List;


public class Notification_Fragment extends Fragment {

    public static LinearLayoutManager layoutManager;
    public static RecyclerView mRecyclerView;
    private FragmentActivity fragmentActivity;
    private RecyclerView.Adapter mAdapter;
    private List<Venue> nearbyList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefresh;

    SwipeRefreshLayout.OnRefreshListener pullToRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
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
        View view = inflater.inflate(R.layout.notification_fragment, container, false);
        /**declare list view and set adapter to list view (UI)**/
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_location);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(fragmentActivity, R.drawable.divider));

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(fragmentActivity);
        mRecyclerView.setLayoutManager(mLayoutManager);
        layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();

        /** create adapter for put on array from class task **/
        // mAdapter = new ListNearbyAdapter(fragmentActivity, nearbyList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);


        /**set list view can listen the event when click some row **/
        //mRecyclerView.addOnItemTouchListener(recycleViewOnTouchListener);
        mRecyclerView.addOnScrollListener(new RecyclerViewOnScrollListener());
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container_location);
        swipeRefresh.setOnRefreshListener(pullToRefreshListener);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }






}
