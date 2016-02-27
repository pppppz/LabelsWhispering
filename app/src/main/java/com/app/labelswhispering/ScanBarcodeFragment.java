package com.app.labelswhispering;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.app.labelswhispering.Model.Barcode;
import com.app.labelswhispering.detail_fragment.Main_Medicine_Details_Activity;
import com.google.zxing.Result;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanBarcodeFragment extends Fragment implements ZXingScannerView.ResultHandler {
    private static final String FLASH_STATE = "FLASH_STATE";
    private static final String AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE";
    private static final String SELECTED_FORMATS = "SELECTED_FORMATS";
    private static final String CAMERA_ID = "CAMERA_ID";
    private final String TAG = ScanBarcodeFragment.class.getSimpleName();
    MenuItem Flash;
    MenuItem AutoFocus;
    private ZXingScannerView mScannerView;
    private boolean mFlash;
    private boolean mAutoFocus;
    private ArrayList<Integer> mSelectedIndices;
    private int mCameraId = -1;
    private FragmentActivity fActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        mScannerView = new ZXingScannerView(getActivity());
        if (state != null) {
            mFlash = state.getBoolean(FLASH_STATE, false);
            mAutoFocus = state.getBoolean(AUTO_FOCUS_STATE, true);
            mSelectedIndices = state.getIntegerArrayList(SELECTED_FORMATS);
            mCameraId = state.getInt(CAMERA_ID, -1);
        } else {
            mFlash = false;
            mAutoFocus = true;
            mSelectedIndices = null;
            mCameraId = -1;
        }
        fActivity = getActivity();
        return mScannerView;
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        //Flash = menu.add(R.string.FlashLightOn).setIcon(R.drawable.ic_flash_off_white_24dp);
        //FlashOff = menu.add(R.string.FlashLightOff).setIcon(R.drawable.ic_favorite_border_pink_24dp).setVisible(false);

        //  AutoFocus = menu.add(R.string.AutoFocusOn).setIcon(R.drawable.ic_center_focus_weak_white_24dp);
        // AutoFocusOff = menu.add(R.string.AutoFocusOff).setIcon(R.drawable.ic_menu_gray_24dp).setVisible(false);
        Flash = menu.getItem(0).setIcon(R.drawable.ic_flash_off_white_24dp);
        AutoFocus = menu.getItem(1).setIcon(R.drawable.ic_auto_focus_on);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_flash:
                if (!mFlash) {
                    Flash.setIcon(R.drawable.ic_flash_on_white_24dp);
                } else {
                    Flash.setIcon(R.drawable.ic_flash_off_white_24dp);
                }
                mFlash = !mFlash;
                mScannerView.setFlash(mFlash);
                //  Flash.setVisible(mFlash);

               /* FlashOn.setVisible(true);
                FlashOff.setVisible(false);*/
                return true;


            case R.id.action_focus:
                if (mAutoFocus) {
                    AutoFocus.setIcon(R.drawable.ic_auto_focus_off);
                } else {
                    AutoFocus.setIcon(R.drawable.ic_auto_focus_on);
                }
                mAutoFocus = !mAutoFocus;
                mScannerView.setAutoFocus(mAutoFocus);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera(mCameraId);
        mScannerView.setFlash(mFlash);
        mScannerView.setAutoFocus(mAutoFocus);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FLASH_STATE, mFlash);
        outState.putBoolean(AUTO_FOCUS_STATE, mAutoFocus);
        outState.putIntegerArrayList(SELECTED_FORMATS, mSelectedIndices);
        outState.putInt(CAMERA_ID, mCameraId);
    }

    @Override
    public void handleResult(Result rawResult) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getActivity().getApplicationContext(), notification);
            r.play();
        } catch (Exception ignored) {
        }

        // DO SOMETHING
        long barcodeId = Long.parseLong(rawResult.getText());
        try {
            checkIfBarcodeEqualMedicineData(barcodeId);
        } catch (Exception e) {
            mScannerView.startCamera();
        }

    }


    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    private void checkIfBarcodeEqualMedicineData(long barcodeId) {

        ParseQuery<Barcode> query = ParseQuery.getQuery(Barcode.class);
        query.whereEqualTo("barcodeId", barcodeId);
        query.findInBackground(new FindCallback<Barcode>() {

            @Override
            public void done(List<Barcode> medicineName, ParseException e) {
                Log.e(TAG, " in done");
                if (e == null) {
                    // If there are results, update the list of posts
                    if (medicineName.size() == 1) {
                        Intent intent = new Intent(fActivity, Main_Medicine_Details_Activity.class);
                        intent.putExtra("medicineID", medicineName.get(0).getMedicineId());
                        Log.e(TAG, "Medicine ID : " + medicineName.get(0).getMedicineId());
                        startActivity(intent);
                        fActivity.finish();
                    } else {
                        Log.e(TAG, "Medicine Size is " + medicineName.size());
                        mScannerView.startCamera();
                    }
                } else {
                    Log.e("Post retrieval", "Error: " + e.getMessage());
                }

            }

        });

    }
}