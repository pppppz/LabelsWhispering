package com.app.labelswhispering.Controller;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.ClipboardManager;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.labelswhispering.Controller.Preference.SettingsActivity;
import com.app.labelswhispering.Model.Medicine;
import com.app.labelswhispering.OtherClass.isNetworkConnected;
import com.app.labelswhispering.R;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.sfsu.cs.orange.ocr.BeepManager;
import edu.sfsu.cs.orange.ocr.CaptureActivityHandler;
import edu.sfsu.cs.orange.ocr.DecodeHandler;
import edu.sfsu.cs.orange.ocr.FinishListener;
import edu.sfsu.cs.orange.ocr.HelpActivity;
import edu.sfsu.cs.orange.ocr.OcrCharacterHelper;
import edu.sfsu.cs.orange.ocr.OcrInitAsyncTask;
import edu.sfsu.cs.orange.ocr.OcrResult;
import edu.sfsu.cs.orange.ocr.OcrResultFailure;
import edu.sfsu.cs.orange.ocr.OcrResultText;
import edu.sfsu.cs.orange.ocr.ViewfinderView;
import edu.sfsu.cs.orange.ocr.camera.CameraManager;
import edu.sfsu.cs.orange.ocr.camera.ShutterButton;
import edu.sfsu.cs.orange.ocr.language.LanguageCodeHelper;

/**
 * This activity opens the camera and does the actual scanning on a background thread. It draws a
 * viewfinder to help the user place the text correctly, shows feedback as the image processing
 * is happening, and then overlays the results when a scan is successful.
 * <p/>
 * The code for this class was adapted from the ZXing project: http://code.google.com/p/zxing/
 */
public final class ScanOCR_Activity extends AppCompatActivity implements SurfaceHolder.Callback,
        ShutterButton.OnShutterButtonListener {

    /**
     * ISO 639-3 language code indicating the default recognition language.
     */
    public static final String DEFAULT_SOURCE_LANGUAGE_CODE = "eng";

    // Note: These constants will be overridden by any default values defined in preferences.xml.
    /**
     * The default OCR engine to use.
     */
    public static final String DEFAULT_OCR_ENGINE_MODE = "Both";
    /**
     * The default page segmentation mode to use.
     */
    public static final String DEFAULT_PAGE_SEGMENTATION_MODE = "Auto";
    /**
     * Whether to use autofocus by default.
     */
    public static final boolean DEFAULT_TOGGLE_AUTO_FOCUS = true;
    /**
     * Whether to initially disable continuous-picture and continuous-video focus modes.
     */
    public static final boolean DEFAULT_DISABLE_CONTINUOUS_FOCUS = true;
    /**
     * Whether to beep by default when the shutter button is pressed.
     */
    public static final boolean DEFAULT_TOGGLE_BEEP = false;
    /**
     * Whether to initially show a looping, real-time OCR display.
     */
    public static final boolean DEFAULT_TOGGLE_CONTINUOUS = false;
    /**
     * Whether to initially reverse the image returned by the camera.
     */
    public static final boolean DEFAULT_TOGGLE_REVERSED_IMAGE = false;
    /**
     * Whether the light should be initially activated by default.
     */
    public static final boolean DEFAULT_TOGGLE_LIGHT = false;
    /**
     * Languages for which Cube data is available.
     */
    public static final String[] CUBE_SUPPORTED_LANGUAGES = {
            "ara", // Arabic
            "eng", // English
            "hin" // Hindi
    };
    /**
     * Resource to use for data file downloads.
     */
    public static final String DOWNLOAD_BASE = "http://tesseract-ocr.googlecode.com/files/";
    /**
     * Download filename for orientation and script detection (OSD) data.
     */
    public static final String OSD_FILENAME = "tesseract-ocr-3.01.osd.tar";
    /**
     * Destination filename for orientation and script detection (OSD) data.
     */
    public static final String OSD_FILENAME_BASE = "osd.traineddata";
    /**
     * Minimum mean confidence score necessary to not reject single-shot OCR result. Currently unused.
     */
    static final int MINIMUM_MEAN_CONFIDENCE = 0; // 0 means don't reject any scored results
    private static final String TAG = ScanOCR_Activity.class.getSimpleName();
    /**
     * Flag to display the real-time recognition results at the top of the scanning screen.
     */
    private static final boolean CONTINUOUS_DISPLAY_RECOGNIZED_TEXT = true;
    /**
     * Flag to display recognition-related statistics on the scanning screen.
     */
    private static final boolean CONTINUOUS_DISPLAY_METADATA = true;
    /**
     * Flag to enable display of the on-screen shutter button.
     */
    private static final boolean DISPLAY_SHUTTER_BUTTON = true;
    /**
     * Languages that require Cube, and cannot run using Tesseract.
     */
    private static final String[] CUBE_REQUIRED_LANGUAGES = {
            "ara" // Arabic
    };
    // Context menu
    private static final int SETTINGS_ID = Menu.FIRST;
    private static final int ABOUT_ID = Menu.FIRST + 1;

    // Options menu, for copy to clipboard
    private static final int OPTIONS_COPY_RECOGNIZED_TEXT_ID = Menu.FIRST;
    private static final int OPTIONS_COPY_TRANSLATED_TEXT_ID = Menu.FIRST + 1;
    private static final int OPTIONS_SHARE_RECOGNIZED_TEXT_ID = Menu.FIRST + 2;
    private static final int OPTIONS_SHARE_TRANSLATED_TEXT_ID = Menu.FIRST + 3;
    private static boolean isFirstLaunch; // True if this is the first time the app is being run
    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private SurfaceHolder surfaceHolder;
    private TextView statusViewBottom;
    private TextView statusViewTop;
    private TextView ocrResultView;
    private TextView translationView;
    private View cameraButtonView;
    private View resultView;
    private View progressView;
    private OcrResult lastResult;
    private boolean hasSurface;
    private BeepManager beepManager;
    private TessBaseAPI baseApi; // Java interface for the Tesseract OCR engine
    private String sourceLanguageCodeOcr; // ISO 639-3 language code
    private String sourceLanguageReadable; // Language name, for example, "English"
    private String sourceLanguageCodeTranslation; // ISO 639-1 language code
    private int pageSegmentationMode = TessBaseAPI.PageSegMode.PSM_AUTO_OSD;
    private int ocrEngineMode = TessBaseAPI.OEM_TESSERACT_ONLY;
    //private String characterBlacklist;
    //private String characterWhitelist;
    private ShutterButton shutterButton;
    private boolean isTranslationActive; // Whether we want to show translations
    private boolean isContinuousModeActive; // Whether we are doing OCR in continuous mode
    private SharedPreferences prefs;
    private OnSharedPreferenceChangeListener listener;
    private ProgressDialog dialog; // for initOcr - language download & unzip
    private ProgressDialog indeterminateDialog; // also for initOcr - init OCR engine
    private boolean isEngineReady;
    private boolean isPaused;
    private List<Medicine> medicineList = new ArrayList<>();
    private TextView labelTextView;
    private Button buttonGoToDetails;
    private TextView metaText;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    static boolean getFirstLaunch() {
        return isFirstLaunch;
    }

    public Handler getHandler() {
        return handler;
    }

    public TessBaseAPI getBaseApi() {
        return baseApi;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        checkFirstLaunch();

        if (isFirstLaunch) {
            setDefaultPreferences();
        }

        if (!new isNetworkConnected().Check(this)) {
            Toast.makeText(this, "Please check your connection.", Toast.LENGTH_LONG).show();
            finish();
        }

        /*Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);*/
        setContentView(R.layout.scan_ocr);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_scan_ocr);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        cameraButtonView = findViewById(R.id.camera_button_view);
        resultView = findViewById(R.id.result_view);

        statusViewBottom = (TextView) findViewById(R.id.status_view_bottom);
        registerForContextMenu(statusViewBottom);
        statusViewTop = (TextView) findViewById(R.id.status_view_top);
        registerForContextMenu(statusViewTop);


        handler = null;
        lastResult = null;
        hasSurface = false;
        beepManager = new BeepManager(this);

        // Camera shutter button
        if (DISPLAY_SHUTTER_BUTTON) {
            shutterButton = (ShutterButton) findViewById(R.id.shutter_button);
            shutterButton.setOnShutterButtonListener(this);
        }

        ocrResultView = (TextView) findViewById(R.id.ocr_result_text_view);
        registerForContextMenu(ocrResultView);

        //  progressView = findViewById(R.id.indeterminate_progress_indicator_view);

        cameraManager = new CameraManager(getApplication());
        viewfinderView.setCameraManager(cameraManager);

        // Set listener to change the size of the viewfinder rectangle.
        viewfinderView.setOnTouchListener(new View.OnTouchListener() {
            int lastX = -1;
            int lastY = -1;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = -1;
                        lastY = -1;
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        int currentX = (int) event.getX();
                        int currentY = (int) event.getY();

                        try {
                            Rect rect = cameraManager.getFramingRect();

                            final int BUFFER = 50;
                            final int BIG_BUFFER = 60;
                            if (lastX >= 0) {
                                // Adjust the size of the viewfinder rectangle. Check if the touch event occurs in the corner areas first, because the regions overlap.
                                if (rect != null) {
                                    if (((currentX >= rect.left - BIG_BUFFER && currentX <= rect.left + BIG_BUFFER) || (lastX >= rect.left - BIG_BUFFER && lastX <= rect.left + BIG_BUFFER))
                                            && ((currentY <= rect.top + BIG_BUFFER && currentY >= rect.top - BIG_BUFFER) || (lastY <= rect.top + BIG_BUFFER && lastY >= rect.top - BIG_BUFFER))) {
                                        // Top left corner: adjust both top and left sides
                                        cameraManager.adjustFramingRect(2 * (lastX - currentX), 2 * (lastY - currentY));
                                        viewfinderView.removeResultText();
                                    } else if (((currentX >= rect.right - BIG_BUFFER && currentX <= rect.right + BIG_BUFFER) || (lastX >= rect.right - BIG_BUFFER && lastX <= rect.right + BIG_BUFFER))
                                            && ((currentY <= rect.top + BIG_BUFFER && currentY >= rect.top - BIG_BUFFER) || (lastY <= rect.top + BIG_BUFFER && lastY >= rect.top - BIG_BUFFER))) {
                                        // Top right corner: adjust both top and right sides
                                        cameraManager.adjustFramingRect(2 * (currentX - lastX), 2 * (lastY - currentY));
                                        viewfinderView.removeResultText();
                                    } else if (((currentX >= rect.left - BIG_BUFFER && currentX <= rect.left + BIG_BUFFER) || (lastX >= rect.left - BIG_BUFFER && lastX <= rect.left + BIG_BUFFER))
                                            && ((currentY <= rect.bottom + BIG_BUFFER && currentY >= rect.bottom - BIG_BUFFER) || (lastY <= rect.bottom + BIG_BUFFER && lastY >= rect.bottom - BIG_BUFFER))) {
                                        // Bottom left corner: adjust both bottom and left sides
                                        cameraManager.adjustFramingRect(2 * (lastX - currentX), 2 * (currentY - lastY));
                                        viewfinderView.removeResultText();
                                    } else if (((currentX >= rect.right - BIG_BUFFER && currentX <= rect.right + BIG_BUFFER) || (lastX >= rect.right - BIG_BUFFER && lastX <= rect.right + BIG_BUFFER))
                                            && ((currentY <= rect.bottom + BIG_BUFFER && currentY >= rect.bottom - BIG_BUFFER) || (lastY <= rect.bottom + BIG_BUFFER && lastY >= rect.bottom - BIG_BUFFER))) {
                                        // Bottom right corner: adjust both bottom and right sides
                                        cameraManager.adjustFramingRect(2 * (currentX - lastX), 2 * (currentY - lastY));
                                        viewfinderView.removeResultText();
                                    } else if (((currentX >= rect.left - BUFFER && currentX <= rect.left + BUFFER) || (lastX >= rect.left - BUFFER && lastX <= rect.left + BUFFER))
                                            && ((currentY <= rect.bottom && currentY >= rect.top) || (lastY <= rect.bottom && lastY >= rect.top))) {
                                        // Adjusting left side: event falls within BUFFER pixels of left side, and between top and bottom side limits
                                        cameraManager.adjustFramingRect(2 * (lastX - currentX), 0);
                                        viewfinderView.removeResultText();
                                    } else if (((currentX >= rect.right - BUFFER && currentX <= rect.right + BUFFER) || (lastX >= rect.right - BUFFER && lastX <= rect.right + BUFFER))
                                            && ((currentY <= rect.bottom && currentY >= rect.top) || (lastY <= rect.bottom && lastY >= rect.top))) {
                                        // Adjusting right side: event falls within BUFFER pixels of right side, and between top and bottom side limits
                                        cameraManager.adjustFramingRect(2 * (currentX - lastX), 0);
                                        viewfinderView.removeResultText();
                                    } else if (((currentY <= rect.top + BUFFER && currentY >= rect.top - BUFFER) || (lastY <= rect.top + BUFFER && lastY >= rect.top - BUFFER))
                                            && ((currentX <= rect.right && currentX >= rect.left) || (lastX <= rect.right && lastX >= rect.left))) {
                                        // Adjusting top side: event falls within BUFFER pixels of top side, and between left and right side limits
                                        cameraManager.adjustFramingRect(0, 2 * (lastY - currentY));
                                        viewfinderView.removeResultText();
                                    } else if (((currentY <= rect.bottom + BUFFER && currentY >= rect.bottom - BUFFER) || (lastY <= rect.bottom + BUFFER && lastY >= rect.bottom - BUFFER))
                                            && ((currentX <= rect.right && currentX >= rect.left) || (lastX <= rect.right && lastX >= rect.left))) {
                                        // Adjusting bottom side: event falls within BUFFER pixels of bottom side, and between left and right side limits
                                        cameraManager.adjustFramingRect(0, 2 * (currentY - lastY));
                                        viewfinderView.removeResultText();
                                    }
                                }
                            }
                        } catch (NullPointerException e) {
                            Log.e(TAG, "Framing rect not available", e);
                        }
                        v.invalidate();
                        lastX = currentX;
                        lastY = currentY;
                        return true;
                    case MotionEvent.ACTION_UP:
                        lastX = -1;
                        lastY = -1;
                        return true;
                }
                return false;
            }
        });

        isEngineReady = false;
    }


    private boolean isFlashSupported() {
        PackageManager pm = getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetStatusView();

        String previousSourceLanguageCodeOcr = sourceLanguageCodeOcr;
        int previousOcrEngineMode = ocrEngineMode;

        retrievePreferences();

        // Set up the camera preview surface.
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        surfaceHolder = surfaceView.getHolder();
        if (!hasSurface) {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        // Comment out the following block to test non-OCR functions without an SD card

        // Do OCR engine initialization, if necessary
        boolean doNewInit = (baseApi == null) || !sourceLanguageCodeOcr.equals(previousSourceLanguageCodeOcr) ||
                ocrEngineMode != previousOcrEngineMode;
        if (doNewInit) {
            // Initialize the OCR engine
            File storageDirectory = getStorageDirectory();
            if (storageDirectory != null) {
                initOcrEngine(storageDirectory, sourceLanguageCodeOcr, sourceLanguageReadable);
            }
        } else {
            // We already have the engine initialized, so just start the camera.
            resumeOCR();
        }
    }

    /**
     * Method to start or restart recognition after the OCR engine has been initialized,
     * or after the app regains focus. Sets state related settings and OCR engine parameters,
     * and requests camera initialization.
     */
    public void resumeOCR() {
        Log.d(TAG, "resumeOCR()");

        // This method is called when Tesseract has already been successfully initialized, so set
        // isEngineReady = true here.
        isEngineReady = true;

        isPaused = false;

        if (handler != null) {
            handler.resetState();
        }
        if (baseApi != null) {
            baseApi.setPageSegMode(pageSegmentationMode);
            // baseApi.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, characterBlacklist);
            // baseApi.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, characterWhitelist);
        }

        if (hasSurface) {
            // The activity was paused but not stopped, so the surface still exists. Therefore
            // surfaceCreated() won't be called, so init the camera here.
            initCamera(surfaceHolder);
        }
    }

    /**
     * Called when the shutter button is pressed in continuous mode.
     */
    void onShutterButtonPressContinuous() {
        isPaused = true;
        handler.stop();
        beepManager.playBeepSoundAndVibrate();
        if (lastResult != null) {
            handleOcrDecode(lastResult);
        } else {
            Toast toast = Toast.makeText(this, "OCR failed. Please try again.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 0);
            toast.show();
            resumeContinuousDecoding();
        }
    }

    /**
     * Called to resume recognition after translation in continuous mode.
     */
    @SuppressWarnings("unused")
    void resumeContinuousDecoding() {
        isPaused = false;
        resetStatusView();
        setStatusViewForContinuous();
        DecodeHandler.resetDecodeState();
        handler.resetState();
        if (shutterButton != null && DISPLAY_SHUTTER_BUTTON) {
            shutterButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated()");

        if (holder == null) {
            Log.e(TAG, "surfaceCreated gave us a null surface");
        }

        // Only initialize the camera if the OCR engine is ready to go.
        if (!hasSurface && isEngineReady) {
            Log.d(TAG, "surfaceCreated(): calling initCamera()...");
            initCamera(holder);
        }
        hasSurface = true;
    }

    /**
     * Initializes the camera and starts the handler to begin previewing.
     */
    private void initCamera(SurfaceHolder surfaceHolder) {
        Log.d(TAG, "initCamera()");
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        try {
            // Open and initialize the camera
            cameraManager.openDriver(surfaceHolder);

            // Creating the handler starts the preview, which can also throw a RuntimeException.
            handler = new CaptureActivityHandler(this, cameraManager, isContinuousModeActive);

        } catch (IOException | RuntimeException ioe) {
            showErrorMessage("Error", "Could not initialize camera. Please try restarting device.");
        }
    }

    @Override
    protected void onPause() {
        if (handler != null) {
            handler.quitSynchronously();
        }

        // Stop using the camera, to avoid conflicting with other camera-based apps
        cameraManager.closeDriver();

        if (!hasSurface) {
            SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.removeCallback(this);
        }
        super.onPause();
    }

    public void stopHandler() {
        if (handler != null) {
            handler.stop();
        }
    }

    @Override
    protected void onDestroy() {
        if (baseApi != null) {
            baseApi.end();
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            // First check if we're paused in continuous mode, and if so, just unpause.
            if (isPaused) {
                Log.d(TAG, "only resuming continuous recognition, not quitting...");
                resumeContinuousDecoding();
                return true;
            }

            // Exit the app if we're not viewing an OCR result.
            if (lastResult == null) {
                setResult(RESULT_CANCELED);
                finish();
                return true;
            } else {
                // Go back to previewing in regular OCR mode.
                resetStatusView();
                if (handler != null) {
                    handler.sendEmptyMessage(R.id.restart_preview);
                }
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_CAMERA) {
            if (isContinuousModeActive) {
                onShutterButtonPressContinuous();
            } else {
                handler.hardwareShutterButtonClick();
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_FOCUS) {
            // Only perform autofocus if user is not holding down the button.
            if (event.getRepeatCount() == 0) {
                cameraManager.requestAutoFocus(500L);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    /**
     * Sets the necessary language code values for the given OCR language.
     */
    private boolean setSourceLanguage(String languageCode) {
        sourceLanguageCodeOcr = languageCode;
        sourceLanguageCodeTranslation = LanguageCodeHelper.mapLanguageCode(languageCode);
        sourceLanguageReadable = LanguageCodeHelper.getOcrLanguageName(this, languageCode);
        return true;
    }

    /**
     * Finds the proper location on the SD card where we can save files.
     */
    private File getStorageDirectory() {
        //Log.d(TAG, "getStorageDirectory(): API level is " + Integer.valueOf(android.os.Build.VERSION.SDK_INT));

        String state = null;
        try {
            state = Environment.getExternalStorageState();
        } catch (RuntimeException e) {
            Log.e(TAG, "Is the SD card visible?", e);
            showErrorMessage("Error", "Required external storage (such as an SD card) is unavailable.");
        }

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            // We can read and write the media
            //    	if (Integer.valueOf(android.os.Build.VERSION.SDK_INT) > 7) {
            // For Android 2.2 and above

            try {
                return getExternalFilesDir(Environment.MEDIA_MOUNTED);
            } catch (NullPointerException e) {
                // We get an error here if the SD card is visible, but full
                Log.e(TAG, "External storage is unavailable");
                showErrorMessage("Error", "Required external storage (such as an SD card) is full or unavailable.");
            }

            //        } else {
            //          // For Android 2.1 and below, explicitly give the path as, for example,
            //          // "/mnt/sdcard/Android/data/edu.sfsu.cs.orange.ocr/files/"
            //          return new File(Environment.getExternalStorageDirectory().toString() + File.separator +
            //                  "Android" + File.separator + "data" + File.separator + getPackageName() +
            //                  File.separator + "files" + File.separator);
            //        }

        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            Log.e(TAG, "External storage is read-only");
            showErrorMessage("Error", "Required external storage (such as an SD card) is unavailable for data storage.");
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            // to know is we can neither read nor write
            Log.e(TAG, "External storage is unavailable");
            showErrorMessage("Error", "Required external storage (such as an SD card) is unavailable or corrupted.");
        }
        return null;
    }

    /**
     * Requests initialization of the OCR engine with the given parameters.
     *
     * @param storageRoot  Path to location of the tessdata directory to use
     * @param languageCode Three-letter ISO 639-3 language code for OCR
     * @param languageName Name of the language for OCR, for example, "English"
     */
    private void initOcrEngine(File storageRoot, String languageCode, String languageName) {
        isEngineReady = false;

        // Set up the dialog box for the thermometer-style download progress indicator
        if (dialog != null) {
            dialog.dismiss();
        }
        dialog = new ProgressDialog(this);

        // If we have a language that only runs using Cube, then set the ocrEngineMode to Cube
        if (ocrEngineMode != TessBaseAPI.OEM_CUBE_ONLY) {
            for (String s : CUBE_REQUIRED_LANGUAGES) {
                if (s.equals(languageCode)) {
                    ocrEngineMode = TessBaseAPI.OEM_CUBE_ONLY;
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    prefs.edit().putString(SettingsActivity.KEY_OCR_ENGINE_MODE, getOcrEngineModeName()).apply();
                }
            }
        }

        // If our language doesn't support Cube, then set the ocrEngineMode to Tesseract
        if (ocrEngineMode != TessBaseAPI.OEM_TESSERACT_ONLY) {
            boolean cubeOk = false;
            for (String s : CUBE_SUPPORTED_LANGUAGES) {
                if (s.equals(languageCode)) {
                    cubeOk = true;
                }
            }
            if (!cubeOk) {
                ocrEngineMode = TessBaseAPI.OEM_TESSERACT_ONLY;
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                prefs.edit().putString(SettingsActivity.KEY_OCR_ENGINE_MODE, getOcrEngineModeName()).apply();
            }
        }

        // Display the name of the OCR engine we're initializing in the indeterminate progress dialog box
        indeterminateDialog = new ProgressDialog(this);
        indeterminateDialog.setTitle("Please wait");
        String ocrEngineModeName = getOcrEngineModeName();
       /* if (ocrEngineModeName.equals("Both")) {
            indeterminateDialog.setMessage("Initializing Cube and Tesseract OCR engines for " + languageName + "...");
        } else {
            indeterminateDialog.setMessage("Initializing " + ocrEngineModeName + " OCR engine for " + languageName + "...");
        }*/
        indeterminateDialog.setMessage("Initializing " + ocrEngineModeName + " OCR engine for " + languageName + "...");
        indeterminateDialog.setCancelable(false);
        indeterminateDialog.show();

        if (handler != null) {
            handler.quitSynchronously();
        }

        // Disable continuous mode if we're using Cube. This will prevent bad states for devices
        // with low memory that crash when running OCR with Cube, and prevent unwanted delays.
        if (ocrEngineMode == TessBaseAPI.OEM_CUBE_ONLY || ocrEngineMode == TessBaseAPI.OEM_TESSERACT_CUBE_COMBINED) {
            Log.d(TAG, "Disabling continuous preview");
            isContinuousModeActive = false;
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            prefs.edit().putBoolean(SettingsActivity.KEY_CONTINUOUS_PREVIEW, false);
        }

        // Start AsyncTask to install language data and init OCR
        baseApi = new TessBaseAPI();
        new OcrInitAsyncTask(this, baseApi, dialog, indeterminateDialog, languageCode, languageName, ocrEngineMode)
                .execute(storageRoot.toString());
    }

    /**
     * Displays information relating to the result of OCR, and requests a translation if necessary.
     *
     * @param ocrResult Object representing successful OCR results
     * @return True if a non-null result was received for OCR
     */
    public boolean handleOcrDecode(OcrResult ocrResult) {
        lastResult = ocrResult;

        // Test whether the result is null
        if (ocrResult.getText() == null || ocrResult.getText().equals("")) {
            Toast toast = Toast.makeText(this, "OCR failed. Please try again.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 0);
            toast.show();
            return false;
        }

        // Turn off capture-related UI elements
        shutterButton.setVisibility(View.GONE);
        statusViewBottom.setVisibility(View.GONE);
        statusViewTop.setVisibility(View.GONE);
        cameraButtonView.setVisibility(View.GONE);
        viewfinderView.setVisibility(View.GONE);
        resultView.setVisibility(View.VISIBLE);

        ImageView bitmapImageView = (ImageView) findViewById(R.id.image_view);
        Bitmap lastBitmap = ocrResult.getBitmap();
        if (lastBitmap == null) {
            bitmapImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        } else {
            bitmapImageView.setImageBitmap(lastBitmap);
        }

        // Display the recognized text
        TextView sourceLanguageTextView = (TextView) findViewById(R.id.source_language_text_view);
        sourceLanguageTextView.setText(sourceLanguageReadable);
        TextView ocrResultTextView = (TextView) findViewById(R.id.ocr_result_text_view);
        ocrResultTextView.setText(ocrResult.getText());
        // Crudely scale betweeen 22 and 32 -- bigger font for shorter text
        int scaledSize = Math.max(22, 32 - ocrResult.getText().length() / 4);
        ocrResultTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, scaledSize);

        labelTextView = (TextView) findViewById(R.id.translation_language_label_text_view);
        buttonGoToDetails = (Button) findViewById(R.id.buttonGoToDetails);
        metaText = (TextView) findViewById(R.id.tv_meta);

        doMySearch(lastResult.getText());
        return true;
    }

    private void doMySearch(String word) {
        //pattern query
        ParseQuery<Medicine> query = ParseQuery.getQuery(Medicine.class);
        query.whereEqualTo("name", word);
        medicineList.clear();
        query.findInBackground(new FindCallback<Medicine>() {
            @Override
            public void done(List<Medicine> items, ParseException error) {
                //check if list task have some data = clear
                if (error == null) {
                    if (items.size() != 0) {
                        for (Medicine medicine : items) {
                            medicineList.add(medicine);
                        }
                        labelTextView.setText(medicineList.get(0).getName());
                        String useFor = medicineList.get(0).getUseFor();
                        metaText.setText(useFor);

                        buttonGoToDetails.setVisibility(View.VISIBLE);
                        buttonGoToDetails.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(ScanOCR_Activity.this, MedicineDetails_Activity.class);
                                intent.putExtra("medicineID", medicineList.get(0).getObjectId());
                                startActivity(intent);
                                finish();
                            }
                        });
                    } else {
                        Toast.makeText(ScanOCR_Activity.this, "Don't found", Toast.LENGTH_SHORT).show();
                    }
                    Log.d(TAG, "updated data");
                } else {

                    Log.e(TAG, "ParseException : " + error);
                }

            }
        });

    }

    /**
     * Displays information relating to the results of a successful real-time OCR request.
     *
     * @param ocrResult Object representing successful OCR results
     */
    public void handleOcrContinuousDecode(OcrResult ocrResult) {

        lastResult = ocrResult;

        // Send an OcrResultText object to the ViewfinderView for text rendering
        viewfinderView.addResultText(new OcrResultText(ocrResult.getText(),
                ocrResult.getWordConfidences(),
                ocrResult.getMeanConfidence(),
                ocrResult.getBitmapDimensions(),
                ocrResult.getRegionBoundingBoxes(),
                ocrResult.getTextlineBoundingBoxes(),
                ocrResult.getStripBoundingBoxes(),
                ocrResult.getWordBoundingBoxes(),
                ocrResult.getCharacterBoundingBoxes()));

        Integer meanConfidence = ocrResult.getMeanConfidence();

        if (CONTINUOUS_DISPLAY_RECOGNIZED_TEXT) {
            // Display the recognized text on the screen
            statusViewTop.setText(ocrResult.getText());
            int scaledSize = Math.max(22, 32 - ocrResult.getText().length() / 4);
            statusViewTop.setTextSize(TypedValue.COMPLEX_UNIT_SP, scaledSize);
            statusViewTop.setTextColor(Color.BLACK);
            statusViewTop.setBackgroundResource(R.color.status_top_text_background);

            statusViewTop.getBackground().setAlpha(meanConfidence * (255 / 100));
        }

        if (CONTINUOUS_DISPLAY_METADATA) {
            // Display recognition-related metadata at the bottom of the screen
            long recognitionTimeRequired = ocrResult.getRecognitionTimeRequired();
            statusViewBottom.setTextSize(14);
            statusViewBottom.setText("OCR: " + sourceLanguageReadable + " - Mean confidence: " +
                    meanConfidence.toString() + " - Time required: " + recognitionTimeRequired + " ms");
        }
    }

    /**
     * Version of handleOcrContinuousDecode for failed OCR requests. Displays a failure message.
     *
     * @param obj Metadata for the failed OCR request.
     */
    public void handleOcrContinuousDecode(OcrResultFailure obj) {
        lastResult = null;
        viewfinderView.removeResultText();

        // Reset the text in the recognized text box.
        statusViewTop.setText("");

        if (CONTINUOUS_DISPLAY_METADATA) {
            // Color text delimited by '-' as red.
            statusViewBottom.setTextSize(14);
            CharSequence cs = setSpanBetweenTokens("OCR: " + sourceLanguageReadable + " - OCR failed - Time required: "
                    + obj.getTimeRequired() + " ms", "-", new ForegroundColorSpan(0xFFFF0000));
            statusViewBottom.setText(cs);
        }
    }

    /**
     * Given either a Spannable String or a regular String and a token, apply
     * the given CharacterStyle to the span between the tokens.
     * <p/>
     * NOTE: This method was adapted from:
     * http://www.androidengineer.com/2010/08/easy-method-for-formatting-android.html
     * <p/>
     * <p/>
     * For example, {@code setSpanBetweenTokens("Hello ##world##!", "##", new
     * ForegroundColorSpan(0xFFFF0000));} will return a CharSequence {@code
     * "Hello world!"} with {@code world} in red.
     */
    private CharSequence setSpanBetweenTokens(CharSequence text, String token,
                                              CharacterStyle... cs) {
        // Start and end refer to the points where the span will apply
        int tokenLen = token.length();
        int start = text.toString().indexOf(token) + tokenLen;
        int end = text.toString().indexOf(token, start);

        if (start > -1 && end > -1) {
            // Copy the spannable string to a mutable spannable string
            SpannableStringBuilder ssb = new SpannableStringBuilder(text);
            for (CharacterStyle c : cs)
                ssb.setSpan(c, start, end, 0);
            text = ssb;
        }
        return text;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.equals(ocrResultView)) {
            menu.add(Menu.NONE, OPTIONS_COPY_RECOGNIZED_TEXT_ID, Menu.NONE, "Copy recognized text");
            menu.add(Menu.NONE, OPTIONS_SHARE_RECOGNIZED_TEXT_ID, Menu.NONE, "Share recognized text");
        } else if (v.equals(translationView)) {
            menu.add(Menu.NONE, OPTIONS_COPY_TRANSLATED_TEXT_ID, Menu.NONE, "Copy translated text");
            menu.add(Menu.NONE, OPTIONS_SHARE_TRANSLATED_TEXT_ID, Menu.NONE, "Share translated text");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        switch (item.getItemId()) {

            case OPTIONS_COPY_RECOGNIZED_TEXT_ID:
                clipboardManager.setText(ocrResultView.getText());
                if (clipboardManager.hasText()) {
                    Toast toast = Toast.makeText(this, "Text copied.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                    toast.show();
                }
                return true;
            case OPTIONS_SHARE_RECOGNIZED_TEXT_ID:
                Intent shareRecognizedTextIntent = new Intent(Intent.ACTION_SEND);
                shareRecognizedTextIntent.setType("text/plain");
                shareRecognizedTextIntent.putExtra(Intent.EXTRA_TEXT, ocrResultView.getText());
                startActivity(Intent.createChooser(shareRecognizedTextIntent, "Share via"));
                return true;
            case OPTIONS_COPY_TRANSLATED_TEXT_ID:
                clipboardManager.setText(translationView.getText());
                if (clipboardManager.hasText()) {
                    Toast toast = Toast.makeText(this, "Text copied.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                    toast.show();
                }
                return true;
            case OPTIONS_SHARE_TRANSLATED_TEXT_ID:
                Intent shareTranslatedTextIntent = new Intent(Intent.ACTION_SEND);
                shareTranslatedTextIntent.setType("text/plain");
                shareTranslatedTextIntent.putExtra(Intent.EXTRA_TEXT, translationView.getText());
                startActivity(Intent.createChooser(shareTranslatedTextIntent, "Share via"));
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    /**
     * Resets view elements.
     */
    private void resetStatusView() {
        resultView.setVisibility(View.GONE);
        if (CONTINUOUS_DISPLAY_METADATA) {
            statusViewBottom.setText("");
            statusViewBottom.setTextSize(14);
            statusViewBottom.setTextColor(getResources().getColor(R.color.status_text));
            statusViewBottom.setVisibility(View.VISIBLE);
        }
        if (CONTINUOUS_DISPLAY_RECOGNIZED_TEXT) {
            statusViewTop.setText("");
            statusViewTop.setTextSize(14);
            statusViewTop.setVisibility(View.VISIBLE);
        }
        viewfinderView.setVisibility(View.VISIBLE);
        cameraButtonView.setVisibility(View.VISIBLE);
        if (DISPLAY_SHUTTER_BUTTON) {
            shutterButton.setVisibility(View.VISIBLE);
        }
        lastResult = null;
        viewfinderView.removeResultText();
    }

    /**
     * Displays a pop-up message showing the name of the current OCR source language.
     */
    public void showLanguageName() {
        Toast toast = Toast.makeText(this, "OCR: " + sourceLanguageReadable, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
    }

    /**
     * Displays an initial message to the user while waiting for the first OCR request to be
     * completed after starting realtime OCR.
     */
    public void setStatusViewForContinuous() {
        viewfinderView.removeResultText();
        if (CONTINUOUS_DISPLAY_METADATA) {
            statusViewBottom.setText("OCR: " + sourceLanguageReadable + " - waiting for OCR...");
        }
    }

    /**
     *
     * @param visible
     */
    @SuppressWarnings("unused")
    public void setButtonVisibility(boolean visible) {
        if (shutterButton != null && visible == true && DISPLAY_SHUTTER_BUTTON) {
            shutterButton.setVisibility(View.VISIBLE);
        } else if (shutterButton != null) {
            shutterButton.setVisibility(View.GONE);
        }
    }

    /**
     * Enables/disables the shutter button to prevent double-clicks on the button.
     *
     * @param clickable True if the button should accept a click
     */
    public void setShutterButtonClickable(boolean clickable) {
        shutterButton.setClickable(clickable);
    }

    /**
     * Request the viewfinder to be invalidated.
     */
    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

    @Override
    public void onShutterButtonClick(ShutterButton b) {
        if (isContinuousModeActive) {
            onShutterButtonPressContinuous();
        } else {
            if (handler != null) {
                handler.shutterButtonClick();
            }
        }
    }

    @Override
    public void onShutterButtonFocus(ShutterButton b, boolean pressed) {
        requestDelayedAutoFocus();
    }

    /**
     * Requests autofocus after a 350 ms delay. This delay prevents requesting focus when the user
     * just wants to click the shutter button without focusing. Quick button press/release will
     * trigger onShutterButtonClick() before the focus kicks in.
     */
    private void requestDelayedAutoFocus() {
        // Wait 350 ms before focusing to avoid interfering with quick button presses when
        // the user just wants to take a picture without focusing.
        cameraManager.requestAutoFocus(350L);
    }

    /**
     * We want the help screen to be shown automatically the first time a new version of the app is
     * run. The easiest way to do this is to check android:versionCode from the manifest, and compare
     * it to a value stored as a preference.
     */
    private boolean checkFirstLaunch() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            int currentVersion = info.versionCode;
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            int lastVersion = prefs.getInt(SettingsActivity.KEY_HELP_VERSION_SHOWN, 0);
            isFirstLaunch = lastVersion == 0;
            if (currentVersion > lastVersion) {

                // Record the last version for which we last displayed the What's New (Help) page
                prefs.edit().putInt(SettingsActivity.KEY_HELP_VERSION_SHOWN, currentVersion).apply();
                Intent intent = new Intent(this, HelpActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

                // Show the default page on a clean install, and the what's new page on an upgrade.
                String page = lastVersion == 0 ? HelpActivity.DEFAULT_PAGE : HelpActivity.WHATS_NEW_PAGE;
                intent.putExtra(HelpActivity.REQUESTED_PAGE_KEY, page);
                startActivity(intent);
                return true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, e);
        }
        return false;
    }

    /**
     * Returns a string that represents which OCR engine(s) are currently set to be run.
     *
     * @return OCR engine mode
     */
    String getOcrEngineModeName() {
        String ocrEngineModeName = "";
        String[] ocrEngineModes = getResources().getStringArray(R.array.ocrenginemodes);
        if (ocrEngineMode == TessBaseAPI.OEM_TESSERACT_ONLY) {
            ocrEngineModeName = ocrEngineModes[0];
        } else if (ocrEngineMode == TessBaseAPI.OEM_CUBE_ONLY) {
            ocrEngineModeName = ocrEngineModes[1];
        } else if (ocrEngineMode == TessBaseAPI.OEM_TESSERACT_CUBE_COMBINED) {
            ocrEngineModeName = ocrEngineModes[2];
        }
        return ocrEngineModeName;
    }

    /**
     * Gets values from shared preferences and sets the corresponding data members in this activity.
     */
    private void retrievePreferences() {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Retrieve from preferences, and set in this Activity, the language preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        setSourceLanguage(prefs.getString(SettingsActivity.KEY_SOURCE_LANGUAGE_PREFERENCE, ScanOCR_Activity.DEFAULT_SOURCE_LANGUAGE_CODE));
        isTranslationActive = prefs.getBoolean(SettingsActivity.KEY_TOGGLE_TRANSLATION, false);

        // Retrieve from preferences, and set in this Activity, the capture mode preference
        isContinuousModeActive = prefs.getBoolean(SettingsActivity.KEY_CONTINUOUS_PREVIEW, ScanOCR_Activity.DEFAULT_TOGGLE_CONTINUOUS);

        // Retrieve from preferences, and set in this Activity, the page segmentation mode preference
        String[] pageSegmentationModes = getResources().getStringArray(R.array.pagesegmentationmodes);
        String pageSegmentationModeName = prefs.getString(SettingsActivity.KEY_PAGE_SEGMENTATION_MODE, pageSegmentationModes[0]);
        if (pageSegmentationModeName.equals(pageSegmentationModes[0])) {
            pageSegmentationMode = TessBaseAPI.PageSegMode.PSM_AUTO_OSD;
        } else if (pageSegmentationModeName.equals(pageSegmentationModes[1])) {
            pageSegmentationMode = TessBaseAPI.PageSegMode.PSM_AUTO;
        } else if (pageSegmentationModeName.equals(pageSegmentationModes[2])) {
            pageSegmentationMode = TessBaseAPI.PageSegMode.PSM_SINGLE_BLOCK;
        } else if (pageSegmentationModeName.equals(pageSegmentationModes[3])) {
            pageSegmentationMode = TessBaseAPI.PageSegMode.PSM_SINGLE_CHAR;
        } else if (pageSegmentationModeName.equals(pageSegmentationModes[4])) {
            pageSegmentationMode = TessBaseAPI.PageSegMode.PSM_SINGLE_COLUMN;
        } else if (pageSegmentationModeName.equals(pageSegmentationModes[5])) {
            pageSegmentationMode = TessBaseAPI.PageSegMode.PSM_SINGLE_LINE;
        } else if (pageSegmentationModeName.equals(pageSegmentationModes[6])) {
            pageSegmentationMode = TessBaseAPI.PageSegMode.PSM_SINGLE_WORD;
        } else if (pageSegmentationModeName.equals(pageSegmentationModes[7])) {
            pageSegmentationMode = TessBaseAPI.PageSegMode.PSM_SINGLE_BLOCK_VERT_TEXT;
        } else if (pageSegmentationModeName.equals(pageSegmentationModes[8])) {
            pageSegmentationMode = TessBaseAPI.PageSegMode.PSM_SPARSE_TEXT;
        }

        // Retrieve from preferences, and set in this Activity, the OCR engine mode
        String[] ocrEngineModes = getResources().getStringArray(R.array.ocrenginemodes);
        String ocrEngineModeName = prefs.getString(SettingsActivity.KEY_OCR_ENGINE_MODE, ocrEngineModes[0]);
        if (ocrEngineModeName.equals(ocrEngineModes[0])) {
            ocrEngineMode = TessBaseAPI.OEM_TESSERACT_ONLY;
        } else if (ocrEngineModeName.equals(ocrEngineModes[1])) {
            ocrEngineMode = TessBaseAPI.OEM_CUBE_ONLY;
        } else if (ocrEngineModeName.equals(ocrEngineModes[2])) {
            ocrEngineMode = TessBaseAPI.OEM_TESSERACT_CUBE_COMBINED;
        }

        // Retrieve from preferences, and set in this Activity, the character blacklist and whitelist
        //  characterBlacklist = OcrCharacterHelper.getBlacklist(prefs, sourceLanguageCodeOcr);
        //characterWhitelist = OcrCharacterHelper.getWhitelist(prefs, sourceLanguageCodeOcr);

        prefs.registerOnSharedPreferenceChangeListener(listener);

        beepManager.updatePrefs();
    }

    /**
     * Sets default values for preferences. To be called the first time this app is run.
     */
    private void setDefaultPreferences() {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Continuous preview
        prefs.edit().putBoolean(SettingsActivity.KEY_CONTINUOUS_PREVIEW, ScanOCR_Activity.DEFAULT_TOGGLE_CONTINUOUS).apply();

        // Recognition language
        prefs.edit().putString(SettingsActivity.KEY_SOURCE_LANGUAGE_PREFERENCE, ScanOCR_Activity.DEFAULT_SOURCE_LANGUAGE_CODE).apply();

        // OCR Engine
        prefs.edit().putString(SettingsActivity.KEY_OCR_ENGINE_MODE, ScanOCR_Activity.DEFAULT_OCR_ENGINE_MODE).apply();

        // Autofocus
        prefs.edit().putBoolean(SettingsActivity.KEY_AUTO_FOCUS, ScanOCR_Activity.DEFAULT_TOGGLE_AUTO_FOCUS).apply();

        // Disable problematic focus modes
        prefs.edit().putBoolean(SettingsActivity.KEY_DISABLE_CONTINUOUS_FOCUS, ScanOCR_Activity.DEFAULT_DISABLE_CONTINUOUS_FOCUS).apply();

        // Beep
        prefs.edit().putBoolean(SettingsActivity.KEY_PLAY_BEEP, ScanOCR_Activity.DEFAULT_TOGGLE_BEEP).apply();

        // Character blacklist
        prefs.edit().putString(SettingsActivity.KEY_CHARACTER_BLACKLIST,
                OcrCharacterHelper.getDefaultBlacklist(ScanOCR_Activity.DEFAULT_SOURCE_LANGUAGE_CODE)).apply();

        // Character whitelist
        prefs.edit().putString(SettingsActivity.KEY_CHARACTER_WHITELIST,
                OcrCharacterHelper.getDefaultWhitelist(ScanOCR_Activity.DEFAULT_SOURCE_LANGUAGE_CODE)).apply();

        // Page segmentation mode
        prefs.edit().putString(SettingsActivity.KEY_PAGE_SEGMENTATION_MODE, ScanOCR_Activity.DEFAULT_PAGE_SEGMENTATION_MODE).apply();

        // Reversed camera image
        prefs.edit().putBoolean(SettingsActivity.KEY_REVERSE_IMAGE, ScanOCR_Activity.DEFAULT_TOGGLE_REVERSED_IMAGE).apply();

        // Light
        prefs.edit().putBoolean(SettingsActivity.KEY_TOGGLE_LIGHT, ScanOCR_Activity.DEFAULT_TOGGLE_LIGHT).apply();
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

    public void displayProgressDialog() {
        // Set up the indeterminate progress dialog box
        indeterminateDialog = new ProgressDialog(this);
        indeterminateDialog.setTitle("Please wait");
        String ocrEngineModeName = getOcrEngineModeName();
        if (ocrEngineModeName.equals("Both")) {
            indeterminateDialog.setMessage("Performing OCR using Cube and Tesseract...");
        } else {
            indeterminateDialog.setMessage("Performing OCR using " + ocrEngineModeName + "...");
        }
        indeterminateDialog.setCancelable(false);
        indeterminateDialog.show();
    }

    public ProgressDialog getProgressDialog() {
        return indeterminateDialog;
    }

    /**
     * Displays an error message dialog box to the user on the UI thread.
     *
     * @param title   The title for the dialog box
     * @param message The error message to be displayed
     */
    public void showErrorMessage(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setOnCancelListener(new FinishListener(this))
                .setPositiveButton("Done", new FinishListener(this))
                .show();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
      /*  client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ScanOCR_ Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.app.labelswhispering/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);*/
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
      /*  Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ScanOCR_ Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                //Uri.parse("http://host/path"),
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.app.labelswhispering/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();*/
    }
}