package edu.sfsu.cs.orange.ocr.language;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.app.labelswhispering.CaptureActivity;
import com.app.labelswhispering.MedicineDetail_Activity;
import com.app.labelswhispering.Model.Medicine;
import com.app.labelswhispering.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Class to perform translations in the background.
 */
public final class SearchWordInDatabaseAsyncTask extends AsyncTask<String, String, Boolean> {

    private static final String TAG = SearchWordInDatabaseAsyncTask.class.getSimpleName();

    private CaptureActivity activity;
    private View progressView;
    private String sourceText;


    public SearchWordInDatabaseAsyncTask(CaptureActivity activity, String sourceText) {
        this.activity = activity;
        this.sourceText = sourceText;
        progressView = activity.findViewById(R.id.indeterminate_progress_indicator_view);
    }

    @Override
    protected Boolean doInBackground(String... arg0) {

        doMySearch(sourceText);
        //translatedText = Translator.translate(activity, sourceLanguageCode, targetLanguageCode, sourceText);

        // Check for failed translations.
        // return !translatedText.equals(Translator.BAD_TRANSLATION_MSG);
        return null;
    }

    @Override
    protected synchronized void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        // Turn off the indeterminate progress indicator
        if (progressView != null) {
            progressView.setVisibility(View.GONE);
        }
    }


    private void doMySearch(String word) {
        //pattern query
        ParseQuery<Medicine> query = ParseQuery.getQuery(Medicine.class);
        query.whereEqualTo("name", word);

        query.findInBackground(new FindCallback<Medicine>() {
            @Override
            public void done(List<Medicine> items, ParseException error) {
                //check if list task have some data = clear
                if (error == null) {
                    if (items.size() != 0) {
                        Intent intent = new Intent(activity, MedicineDetail_Activity.class);
                        intent.putExtra("medicineID", items.get(0).getObjectId());
                        activity.startActivity(intent);
                        activity.finish();

                    } else {
                        Toast.makeText(activity, "Don't found", Toast.LENGTH_SHORT).show();
                    }
                    Log.d(TAG, "updated data");
                } else {

                    Log.e(TAG, "ParseException : " + error);
                }

            }
        });

    }
}
