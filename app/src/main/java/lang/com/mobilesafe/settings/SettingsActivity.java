package lang.com.mobilesafe.settings;

import android.app.Activity;
import android.os.Bundle;

import lang.com.mobilesafe.R;

/**
 * Created by android on 12/7/15.
 */
public class SettingsActivity extends Activity {
    private final String LOG_TAG = "mobilesafe";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blacklist_layout);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
