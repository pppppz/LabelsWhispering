package com.app.labelswhispering.Controller;

import android.support.v7.app.AppCompatActivity;

public class Edit_Schedule_backup extends AppCompatActivity {
/*
    private static final String TAG = Edit_Schedule_backup.class.getSimpleName();
    private SwitchCompat switch_alert;
    private CheckBox Morning, Noon, Evening, Bedtime, beforeMeal, afterMeal;
    private EditText editText_NameMedicine, editTextAmount;
    private String scheduleId;
    private int popup_type_medicine;
    private TextView tvMedicineType;
    private ArrayAdapter<CharSequence> adapter;
    private RecyclerView mRecyclerView;
    private List<Schedule> scheduleList = new ArrayList<>();
    private RecyclerView.Adapter mAdapter;
    LinearLayoutCompat.OnClickListener showPopup = new LinearLayoutCompat.OnClickListener() {
        @Override
        public void onClick(View v) {
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            final View popupView = layoutInflater.inflate(R.layout.popup_medicine_type, null);

            final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            mRecyclerView = (RecyclerView) popupView.findViewById(R.id.recycler_view_type);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getBaseContext(), R.drawable.divider));

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(mLayoutManager);

            mAdapter = new TypeAdapter(getBaseContext(), adapter);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getBaseContext(), new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    popup_type_medicine = position;
                    Log.e(TAG, "type medicine " + popup_type_medicine);
                    tvMedicineType.setText(adapter.getItem(popup_type_medicine));
                    popupWindow.dismiss();
                }
            }));
            popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        }
    };
    private CoordinatorLayout rootLayout;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_schedule_fm);

        String locale_lang = Locale.getDefault().getDisplayLanguage();
        if (locale_lang.equals("th") || locale_lang.equals("???")) {
            adapter = ArrayAdapter.createFromResource(getBaseContext(), R.array.medicine_type_th, android.R.layout.simple_spinner_item);
        } else {
            adapter = ArrayAdapter.createFromResource(getBaseContext(), R.array.medicine_type, android.R.layout.simple_spinner_item);
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        GetMedicineID();
        makeUI();


    }

    private void GetMedicineID() {
        Intent intent = getIntent();
        scheduleId = intent.getStringExtra("scheduleId");
        Log.e(TAG, "ObjectId : " + scheduleId);
    }

    private void makeUI() {
        //Finally, let's add the Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.schedule_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        rootLayout = (CoordinatorLayout) findViewById(R.id.rootLayout_schedule_detail);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_schedule);

        editText_NameMedicine = (EditText) findViewById(R.id.et_nameMedicine);
        editTextAmount = (EditText) findViewById(R.id.ed_amount);
        tvMedicineType = (TextView) findViewById(R.id.txtMedicineType);


        Morning = (CheckBox) findViewById(R.id.cb_morning);
        Noon = (CheckBox) findViewById(R.id.cb_noon);
        Evening = (CheckBox) findViewById(R.id.cb_evening);
        Bedtime = (CheckBox) findViewById(R.id.cb_bedtime);
        beforeMeal = (CheckBox) findViewById(R.id.checkBox_BeforeMeal);
        afterMeal = (CheckBox) findViewById(R.id.checkBox_afterMeal);

        switch_alert = (SwitchCompat) findViewById(R.id.switch_alert);

        LinearLayoutCompat linearLayoutCompat_MedicineType = (LinearLayoutCompat) findViewById(R.id.ll_medicine_type);
        linearLayoutCompat_MedicineType.setOnClickListener(showPopup);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_schedule, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                SaveSchedule();
                break;
            case R.id.menu_delete:
                DeleteSchedule();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void DeleteSchedule() {

        ParseQuery<Schedule> query = ParseQuery.getQuery(Schedule.class);
        query.whereEqualTo("objectId", scheduleId);
        if (new isNetworkConnected().Check(this)) {
            query.fromLocalDatastore();
        }
        query.findInBackground(new FindCallback<Schedule>() {
            @Override
            public void done(final List<Schedule> schedules, ParseException e) {
                if (e == null) {
                    final AlertDialog.Builder dDialog = new AlertDialog.Builder(Edit_Schedule_backup.this);
                    dDialog.setMessage(R.string.remove_this_alarm);
                    dDialog.setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            for (final Schedule object : schedules) {
                                object.deleteInBackground(new DeleteCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        object.pinInBackground();
                                        Snackbar.make(MainActivity.rootLayout, R.string.schedule_has_removed, Snackbar.LENGTH_LONG).show();
                                        finish();
                                    }
                                });
                            }
                        }
                    });
                    dDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }

                    });
                    dDialog.show();
                } else {
                    Log.e(TAG, "delete schedule : " + e);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Query();
    }

    private void SaveSchedule() {
        ParseQuery<Schedule> query = ParseQuery.getQuery(Schedule.class);
        // Retrieve the object by id
        query.getInBackground(scheduleId, new GetCallback<Schedule>() {
            public void done(Schedule object, ParseException e) {
                if (e == null) {
                    // Current let's update it with some new data. In this case, only cheatMode and score
                    // will get sent to the Parse Cloud. playerName hasn't changed.

                    object.setName(String.valueOf(editText_NameMedicine.getText()));
                    object.setAlert(switch_alert.isChecked());
                    object.setAmount(Integer.parseInt(editTextAmount.getText().toString()));
                    object.setMorning(Morning.isChecked());
                    object.setNoon(Noon.isChecked());
                    object.setEvening(Evening.isChecked());
                    object.setBedtime(Bedtime.isChecked());
                    object.setBeforeMeal(beforeMeal.isChecked());
                    object.setAfterMeal(afterMeal.isChecked());
                    object.setType(popup_type_medicine);
                    object.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Snackbar.make(rootLayout, "Schedule saved.", Snackbar.LENGTH_LONG).show();
                                finish();
                            } else {
                                Log.e(TAG, "save error " + e.getLocalizedMessage());
                            }

                        }
                    });
                } else {
                    Log.e(TAG, "error : " + e);
                }
            }
        });
    }


    // When offline
    private void updateListOffline() {
        ParseQuery<Schedule> query = ParseQuery.getQuery(Schedule.class);
        query.whereEqualTo("objectId", scheduleId);
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<Schedule>() {
            @Override
            public void done(List<Schedule> savedByList, ParseException e) {
                if (e == null) {
                    // If there are results, update the list of posts
                    // and notify the adapter
                    for (Schedule schedule : savedByList) {
                        scheduleList.add(schedule);
                    }
                    ParseObject.pinAllInBackground(savedByList);
                } else {
                    Log.e("Post retrieval", "Error: " + e.getMessage());
                }
                setDataToUI();
            }
        });
    }

    //query internet connection , if  online query in parse , if offline query in parse local store.
    private void Query() {
        progressBar.setVisibility(View.VISIBLE);
        scheduleList.clear();
        if (new isNetworkConnected().Check(this)) {
            updateListOnLine();
        } else {
            updateListOffline();
            Snackbar.make(rootLayout, "Offline mode", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    })
                    .show();
        }
    }

    private void updateListOnLine() {

        //pattern query
        ParseQuery<Schedule> query = ParseQuery.getQuery(Schedule.class);
        query.whereEqualTo("objectId", scheduleId);
        query.findInBackground(new FindCallback<Schedule>() {
            @Override
            public void done(List<Schedule> items, ParseException error) {
                //check if list task have some data = clear
                if (error == null) {
                    scheduleList.clear();
                    for (Schedule schedule : items) {
                        scheduleList.add(schedule);
                    }
                    if (scheduleList != null) {
                        setDataToUI();
                    } else {
                        Log.e(TAG, "null");
                    }

                } else {
                    Log.e("ParseException : ", String.valueOf(error));
                }

            }
        });

    }

    private void setDataToUI() {
        editText_NameMedicine.setText(scheduleList.get(0).getName());
        editTextAmount.setText(String.valueOf(scheduleList.get(0).getAmount()));
        Morning.setChecked(scheduleList.get(0).isMorning());
        Noon.setChecked(scheduleList.get(0).isNoon());
        Evening.setChecked(scheduleList.get(0).isEvening());
        Bedtime.setChecked(scheduleList.get(0).isBedtime());
        tvMedicineType.setText(adapter.getItem(scheduleList.get(0).getType()));
        switch_alert.setChecked(scheduleList.get(0).isAlert());
        beforeMeal.setChecked(scheduleList.get(0).isBeforeMeal());
        afterMeal.setChecked(scheduleList.get(0).isAfterMeal());
        progressBar.setVisibility(View.INVISIBLE);
    }
*/

}
