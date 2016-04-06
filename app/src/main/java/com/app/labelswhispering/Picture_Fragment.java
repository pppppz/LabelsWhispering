package com.app.labelswhispering;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.labelswhispering.Adapter.PictureAdapter;
import com.app.labelswhispering.Function.isNetworkConnected;
import com.app.labelswhispering.Listener.RecyclerItemClickListener;
import com.app.labelswhispering.Listener.RecyclerViewOnScrollListener;
import com.app.labelswhispering.Model.Pictures;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class Picture_Fragment extends Fragment {

    private FragmentActivity fragmentActivity;
    private List<Pictures> picturesList = new ArrayList<>();
    RecyclerItemClickListener recyclerItemClickListener = new RecyclerItemClickListener(fragmentActivity, new RecyclerItemClickListener.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {

            try {
                String imgId = picturesList.get(position).getObjectId();
                //Create intent
                Intent intent = new Intent(fragmentActivity, DisplayImg_Activity.class);
                intent.putExtra("imgId", imgId);
                //Start details activity
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                Snackbar.make(MedicineDetails_Activity.rootLayout, "Something went wrong.", Snackbar.LENGTH_SHORT).show();
            }

        }
    });
    private RecyclerView.Adapter mAdapter;
    private String TAG = Picture_Fragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentActivity = getActivity();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_picture, container, false);
        /**declare list view and set adapter to list view (UI)**/
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_picture);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(fragmentActivity, 4);
        mRecyclerView.setLayoutManager(mLayoutManager);

        /** create adapter for put on array from class schedule **/
        mAdapter = new PictureAdapter(fragmentActivity, picturesList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addOnItemTouchListener(recyclerItemClickListener);

        /**set list view can listen the event when click some row **/
        mRecyclerView.addOnScrollListener(new RecyclerViewOnScrollListener());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Query();
    }

    //query internet connection , if  online query in parse , if offline query in parse local store.
    private void Query() {
        MedicineDetails_Activity.progressBar_Medicine_Detail.setVisibility(View.VISIBLE);

        picturesList.clear();
        if (new isNetworkConnected().Check(fragmentActivity)) {
            loadImg();
        } else {
            loadImg_Offline();
            Snackbar.make(MedicineDetails_Activity.rootLayout, "App's running in offline mode", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    })
                    .show();
        }
    }

    private void loadImg() {
        ParseQuery<Pictures> query = ParseQuery.getQuery(Pictures.class);
        query.whereEqualTo("medicineId", MedicineDetails_Activity.medicineID);
        Log.e(TAG, "medicineId " + MedicineDetails_Activity.medicineID);
        query.findInBackground(new FindCallback<Pictures>() {
            @Override
            public void done(List<Pictures> items, ParseException error) {
                //check if list task have some data = clear
                Log.e(TAG, "query done");
                if (error == null) {
                    picturesList.clear();
                    picturesList.addAll(items);
                    mAdapter.notifyDataSetChanged();
                    Log.e(TAG, "notify");
                } else {
                    Log.e(TAG, "ParseException : " + error);
                }
                Log.e(TAG, "end loop");
                MedicineDetails_Activity.progressBar_Medicine_Detail.setVisibility(View.INVISIBLE);
            }

        });
    }

    private void loadImg_Offline() {
        ParseQuery<Pictures> query = ParseQuery.getQuery(Pictures.class);
        query.whereEqualTo("medicineId", MedicineDetails_Activity.medicineID);
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<Pictures>() {
            @Override
            public void done(List<Pictures> items, ParseException error) {
                //check if list task have some data = clear
                Log.e(TAG, "query done");
                if (error == null) {
                    picturesList.clear();
                    picturesList.addAll(items);
                    mAdapter.notifyDataSetChanged();
                    Log.e(TAG, "notify");
                } else {
                    Log.e(TAG, "ParseException : " + error);
                }
                Log.e(TAG, "end loop");
                MedicineDetails_Activity.progressBar_Medicine_Detail.setVisibility(View.INVISIBLE);
            }

        });
    }


}
