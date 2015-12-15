package lang.com.mobilesafe.blacklist.receiver;

import android.annotation.TargetApi;
import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

/**
 * Created by android on 12/15/15.
 */
@TargetApi(Build.VERSION_CODES.FROYO)
public class MyAdmin extends DeviceAdminReceiver {
    public void onReceive(Context context, Intent intent) {
    }
}
