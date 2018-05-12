package com.mutenlab.sudoit.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.mutenlab.sudoit.BuildConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author icerrate
 */
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new OCRInitAsync().execute();
    }

    public class OCRInitAsync extends AsyncTask<Void, Void, Void> {

        private static final String TRAINED_DATA_DIRECTORY = "tessdata/";

        private static final String TRAINED_DATA_FILENAME = "eng.traineddata";

        private final String DATA_PATH = Environment
                .getExternalStorageDirectory().toString()
                + "/Android/data/"
                + BuildConfig.APPLICATION_ID + "/Files/";

        private static final String TAG_DIR_CREATE_SUCCESS = "dir created success";

        private static final String TAG_DIR_CREATE_FAIL = "dir failed create";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg) {
            copyTessFileToStorage();
            return null;
        }

        @Override
        protected void onPostExecute(Void ready) {
            super.onPostExecute(ready);
            launchHome();
        }

        private void copyTessFileToStorage() {
            try {
                // initializes file and parent directory of file
                File dir = new File(DATA_PATH + TRAINED_DATA_DIRECTORY);
                File file = new File(DATA_PATH + TRAINED_DATA_DIRECTORY
                        + TRAINED_DATA_FILENAME);

                // checks if file already exists
                if (!file.exists()) {
                    // copies file in assets folder to stream
                    InputStream in = getAssets().open(
                            TRAINED_DATA_DIRECTORY + TRAINED_DATA_FILENAME);

                    // create parent directories
                    if (dir.mkdirs()) {
                        Log.d(TAG_DIR_CREATE_SUCCESS, dir.toString());
                    } else {
                        Log.d(TAG_DIR_CREATE_FAIL, dir.toString());
                    }

                    // set outputstream to the destination in external storage
                    // copies inputstream to outputstream
                    byte[] buffer = new byte[1024];
                    FileOutputStream out = new FileOutputStream(file);

                    int length;
                    while ((length = in.read(buffer)) > 0) {
                        out.write(buffer, 0, length);
                    }

                    out.close();
                    in.close();
                    Log.d("file copied", " tess success");
                }

            } catch (IOException e) {
                Log.d("file error TessOCR", e.toString());
                e.printStackTrace();
            }
        }
    }

    public void launchHome() {
        Intent i = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(i);

        finish();
    }
}
