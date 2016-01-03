
package edu.sfsu.cs.orange.ocr;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.app.labelswhispering.CaptureActivity;
import com.app.labelswhispering.R;
import com.googlecode.leptonica.android.Pixa;
import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.tesseract.android.TessBaseAPI;

/**
 * Class to send bitmap data for OCR.
 * <p/>
 * The code for this class was adapted from the ZXing project: http://code.google.com/p/zxing/
 */
public final class DecodeHandler extends Handler {

    private static boolean isDecodePending;
    private final CaptureActivity activity;
    private final TessBaseAPI baseApi;
    private boolean running = true;
    private BeepManager beepManager;
    private Bitmap bitmap;
    private long timeRequired;

    DecodeHandler(CaptureActivity activity) {
        this.activity = activity;
        baseApi = activity.getBaseApi();
        beepManager = new BeepManager(activity);
        beepManager.updatePrefs();
    }

    public static void resetDecodeState() {
        isDecodePending = false;
    }

    @Override
    public void handleMessage(Message message) {
        if (!running) {
            return;
        }
        switch (message.what) {
            case R.id.ocr_continuous_decode:
                // Only request a decode if a request is not already pending.
                if (!isDecodePending) {
                    isDecodePending = true;
                    ocrContinuousDecode((byte[]) message.obj, message.arg1, message.arg2);
                }
                break;
            case R.id.ocr_decode:
                ocrDecode((byte[]) message.obj, message.arg1, message.arg2);
                break;
            case R.id.quit:
                running = false;
                Looper myLooper = Looper.myLooper();
                if (myLooper != null) {
                    myLooper.quit();
                }
                break;
        }
    }

    /**
     * Launch an AsyncTask to perform an OCR decode for single-shot mode.
     *
     * @param data   Image data
     * @param width  Image width
     * @param height Image height
     */
    private void ocrDecode(byte[] data, int width, int height) {
        beepManager.playBeepSoundAndVibrate();
        activity.displayProgressDialog();

        // Launch OCR asynchronously, so we get the dialog box displayed immediately
        new OcrRecognizeAsyncTask(activity, baseApi, data, width, height).execute();
    }

    /**
     * Perform an OCR decode for realtime recognition mode.
     *
     * @param data   Image data
     * @param width  Image width
     * @param height Image height
     */
    private void ocrContinuousDecode(byte[] data, int width, int height) {
        byte[] rotatedData = new byte[data.length];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++)
                rotatedData[x * height + height - y - 1] = data[x + y * width];
        }
        int tmp = width;
        width = height;
        height = tmp;

        PlanarYUVLuminanceSource source = activity.getCameraManager().buildLuminanceSource(rotatedData, width, height);
        if (source == null) {
            sendContinuousOcrFailMessage();
            return;
        }
        bitmap = source.renderCroppedGreyscaleBitmap();

        OcrResult ocrResult = getOcrResult();
        Handler handler = activity.getHandler();
        if (handler == null) {
            return;
        }

        if (ocrResult == null) {
            try {
                sendContinuousOcrFailMessage();
            } catch (NullPointerException e) {
                activity.stopHandler();
            } finally {
                bitmap.recycle();
                baseApi.clear();
            }
            return;
        }

        try {
            Message message = Message.obtain(handler, R.id.ocr_continuous_decode_succeeded, ocrResult);
            message.sendToTarget();
        } catch (NullPointerException e) {
            activity.stopHandler();
        } finally {
            baseApi.clear();
        }
    }

    @SuppressWarnings("unused")
    private OcrResult getOcrResult() {
        OcrResult ocrResult;
        String textResult;
        long start = System.currentTimeMillis();

        try {
            baseApi.setImage(ReadFile.readBitmap(bitmap));
            textResult = baseApi.getUTF8Text();
            timeRequired = System.currentTimeMillis() - start;

            // Check for failure to recognize text
            if (textResult == null || textResult.equals("")) {
                return null;
            }
            ocrResult = new OcrResult();
            ocrResult.setWordConfidences(baseApi.wordConfidences());
            ocrResult.setMeanConfidence(baseApi.meanConfidence());
            if (ViewfinderView.DRAW_REGION_BOXES) {
                Pixa regions = baseApi.getRegions();
                ocrResult.setRegionBoundingBoxes(regions.getBoxRects());
                regions.recycle();
            }
            if (ViewfinderView.DRAW_TEXTLINE_BOXES) {
                Pixa textlines = baseApi.getTextlines();
                ocrResult.setTextlineBoundingBoxes(textlines.getBoxRects());
                textlines.recycle();
            }
            if (ViewfinderView.DRAW_STRIP_BOXES) {
                Pixa strips = baseApi.getStrips();
                ocrResult.setStripBoundingBoxes(strips.getBoxRects());
                strips.recycle();
            }

            // Always get the word bounding boxes--we want it for annotating the bitmap after the user
            // presses the shutter button, in addition to maybe wanting to draw boxes/words during the
            // continuous mode recognition.
            Pixa words = baseApi.getWords();
            ocrResult.setWordBoundingBoxes(words.getBoxRects());
            words.recycle();

//      if (ViewfinderView.DRAW_CHARACTER_BOXES || ViewfinderView.DRAW_CHARACTER_TEXT) {
//        ocrResult.setCharacterBoundingBoxes(baseApi.getCharacters().getBoxRects());
//      }
        } catch (RuntimeException e) {
            Log.e("OcrRecognizeAsyncTask", "Caught RuntimeException in request to Tesseract. Setting state to CONTINUOUS_STOPPED.");
            e.printStackTrace();
            try {
                baseApi.clear();
                activity.stopHandler();
            } catch (NullPointerException e1) {
                // Continue
            }
            return null;
        }
        timeRequired = System.currentTimeMillis() - start;
        ocrResult.setBitmap(bitmap);
        ocrResult.setText(textResult);
        ocrResult.setRecognitionTimeRequired(timeRequired);
        return ocrResult;
    }

    private void sendContinuousOcrFailMessage() {
        Handler handler = activity.getHandler();
        if (handler != null) {
            Message message = Message.obtain(handler, R.id.ocr_continuous_decode_failed, new OcrResultFailure(timeRequired));
            message.sendToTarget();
        }
    }

}












