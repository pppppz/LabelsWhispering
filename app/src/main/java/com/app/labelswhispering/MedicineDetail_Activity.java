package com.app.labelswhispering;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.app.labelswhispering.Adapter.PagerInMoreDetailAdapter;
import com.app.labelswhispering.Model.Medicine;
import com.app.labelswhispering.Service.isNetworkConnected;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class MedicineDetail_Activity extends AppCompatActivity {


    public static String medicineID;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private FragmentManager fragmentManager;
    private String TAG = MedicineDetail_Activity.class.getSimpleName();
    private ParseUser parseUser;
    private boolean hasAdded = false;
    private CoordinatorLayout rootLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_detail);
        parseUser = ParseUser.getCurrentUser();
        fragmentManager = getSupportFragmentManager();
        GetMedicineID();
        makeUI();

    }

    private void GetMedicineID() {
        Intent intent = getIntent();
        medicineID = intent.getStringExtra("medicineID");
        Log.e(TAG, "ObjectID : " + medicineID);
    }


    private void makeUI() {

        rootLayout = (CoordinatorLayout) findViewById(R.id.rootLayout_medicine_detail);

        /** Setting toolbar */
        toolbar = (Toolbar) findViewById(R.id.toolbar_medicine_detail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //set tab
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout_medicine_detail);
        tabLayout.addTab(tabLayout.newTab().setText("Property"));
        tabLayout.addTab(tabLayout.newTab().setText("How to take"));
        tabLayout.addTab(tabLayout.newTab().setText("Side Effect"));

        viewPager = (ViewPager) findViewById(R.id.pager_detail);
        final PagerInMoreDetailAdapter adapter = new PagerInMoreDetailAdapter(fragmentManager, tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            String[] toolbar_items = getResources().getStringArray(R.array.toolbar_items_detail);

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                toolbar.setTitle(toolbar_items[position]);
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_48; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.medicine_detail_menu, menu);
        if (!new isNetworkConnected(this).CheckNow()) {
            checkIfMedicineHasAddedOffline(menu);
            Snackbar.make(rootLayout, "App's running in offline mode", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }).show();
        } else {
            checkIfMedicineHasAdded(menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                break;
            case R.id.action_fav:
                if (hasAdded) {
                    removeMedicineFromUserList();
                    item.setIcon(R.drawable.ic_favorite_border_pink_24dp);
                    hasAdded = false;
                } else {
                    item.setIcon(R.drawable.ic_favorite_pink_24dp);
                    addMedicineToUserList();
                    hasAdded = true;
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkIfMedicineHasAdded(final Menu menu) {
        Log.e(TAG, "CheckIfMedicineHasAdded");
        ParseRelation<Medicine> relation = parseUser.getRelation("savedMedicine");
        ParseQuery<Medicine> query = relation.getQuery();
        query.whereEqualTo("objectId", medicineID);
        // Run the query
        query.findInBackground(new FindCallback<Medicine>() {
            @Override
            public void done(List<Medicine> savedByList, ParseException e) {
                if (e == null) {
                    // If there are results, update the list of posts
                    if (savedByList.size() == 1) {
                        hasAdded = true;
                        menu.getItem(0).setIcon(R.drawable.ic_favorite_pink_24dp);
                        ParseObject.pinAllInBackground(savedByList);
                    } else {
                        hasAdded = false;
                        Log.e(TAG, "don't have this medicine data in column of medicine list , so fav icon will showing border icon");
                    }
                } else {
                    Log.e("Post retrieval", "Error: " + e.getMessage());
                }
            }
        });

    }

    private void checkIfMedicineHasAddedOffline(final Menu menu) {
        Log.e(TAG, "CheckIfMedicineHasAddedOffline");
        ParseRelation<Medicine> relation = parseUser.getRelation("savedMedicine");
        ParseQuery<Medicine> query = relation.getQuery();
        query.whereEqualTo("objectId", medicineID);
        query.fromLocalDatastore();
        // Run the query
        query.findInBackground(new FindCallback<Medicine>() {
            @Override
            public void done(List<Medicine> savedByList, ParseException e) {
                if (e == null) {
                    // If there are results, update the list of posts
                    if (savedByList.size() == 1) {
                        hasAdded = true;
                        menu.getItem(0).setIcon(R.drawable.ic_favorite_pink_24dp);
                    } else {
                        hasAdded = false;
                        Log.e(TAG, "don't have this medicine data in column of medicine list , so fav icon will showing border icon");
                    }
                } else {
                    Log.e("Post retrieval", "Error: " + e.getMessage());
                }
            }
        });

    }

    public void removeMedicineFromUserList() {
        // set icon to un favourite
        ParseObject medicine = ParseObject.createWithoutData("Medicine", medicineID);
        ParseRelation<ParseObject> relation = parseUser.getRelation("savedMedicine");
        relation.remove(medicine);
        parseUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.e(TAG, "medicine has removed!");
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void addMedicineToUserList() {
        ParseObject medicine = ParseObject.createWithoutData("Medicine", medicineID);
        ParseRelation<ParseObject> relation = parseUser.getRelation("savedMedicine");
        relation.add(medicine);

        parseUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.e(TAG, "Medicine had added!");

                } else {
                    e.printStackTrace();
                }
            }
        });
    }


}
