
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

import com.app.labelswhispering.Adapter.MedicineAdapter;
import com.app.labelswhispering.Function.DividerItemDecoration;
import com.app.labelswhispering.Function.isNetworkConnected;
import com.app.labelswhispering.Listener.RecyclerItemClickListener;
import com.app.labelswhispering.Model.Medicine;
import com.app.labelswhispering.detail_fragment.Main_Medicine_Details_Activity;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class Medicine_box_fragment extends Fragment {
    private String TAG;
    private FragmentActivity fragmentActivity;
    private SwipeRefreshLayout swipeRefresh;
    private List<Medicine> medicineList = new ArrayList<>();
    RecyclerItemClickListener recyclerItemClickListener = new RecyclerItemClickListener(fragmentActivity, new RecyclerItemClickListener.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            try {
                String medicineID = medicineList.get(position).getObjectId();
                String medicineName = medicineList.get(position).getName();
                Intent intent = new Intent(fragmentActivity, Main_Medicine_Details_Activity.class);
                intent.putExtra("medicineID", medicineID);
                intent.putExtra("medicineName", medicineName);
                startActivity(intent);
            } catch (Exception e) {
                Snackbar.make(MainActivity.rootLayout, R.string.try_again, Snackbar.LENGTH_SHORT).show();
            }


        }
    });
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
        TAG = Medicine_box_fragment.class.getSimpleName();
        fragmentActivity = getActivity();

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.medicine_box, container, false);

        ll_medicineBox = (LinearLayout) view.findViewById(R.id.ll_medicine_box);

        /**declare list view and set adapter to list view (UI)**/
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_home);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(fragmentActivity, R.drawable.divider));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(fragmentActivity);
        mRecyclerView.setLayoutManager(mLayoutManager);

        /**set list view can listen the event when click some row **/
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container_home);
        swipeRefresh.setOnRefreshListener(pullToRefreshListener);

        /** create adapter for put on array from class task **/
        mAdapter = new MedicineAdapter(fragmentActivity, medicineList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addOnItemTouchListener(recyclerItemClickListener);
        return view;
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
                MainActivity.progressBar.setVisibility(View.GONE);
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
                MainActivity.progressBar.setVisibility(View.GONE);
            }
        });
    }

    //query internet connection , if  online query in parse , if offline query in parse local store.
    private void Query() {
        medicineList.clear();
        MainActivity.progressBar.setVisibility(View.VISIBLE);
        if (new isNetworkConnected().Check(fragmentActivity)) {
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


