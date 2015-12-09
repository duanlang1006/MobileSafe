package lang.com.mobilesafe.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android on 12/9/15.
 */
public class ServiceInfoProvide {
    private final String LOG_TAG = "ServiceInfoProvide";

    private final int DEFAULT_NUM = 20;

    private Context mContext = null;
    private PackageManager pm = null;
    private ActivityManager am = null;

    private ArrayList<ServiceInfo> serviceInfos = null;

    public ServiceInfoProvide(Context context) {
        mContext = context;
        pm = context.getPackageManager();
        am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    }

    public List<ServiceInfo> getRunningService() {
        PackageManager pm = mContext.getPackageManager();
        List<ActivityManager.RunningServiceInfo> runningServiceInfos = am.getRunningServices(DEFAULT_NUM);
        Log.i(LOG_TAG, "runningServiceInfos : " + runningServiceInfos);
        serviceInfos = new ArrayList<ServiceInfo>();

        for (ActivityManager.RunningServiceInfo info : runningServiceInfos) {
            ServiceInfo serviceInfo = new ServiceInfo();

            int pid = info.pid;
            int uid = info.uid;
            String processName = info.process;
            long activeSince = info.activeSince;
            int clientCount = info.clientCount;

            ComponentName serviceCMP = info.service;
            String service = serviceCMP.getShortClassName();
            String packageName = serviceCMP.getPackageName();

            try {
                ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
                serviceInfo.setAppIcon(applicationInfo.loadIcon(pm));
                serviceInfo.setAppLabel(String.valueOf(applicationInfo.loadLabel(pm)));
                serviceInfo.setServiceName(service);
                serviceInfo.setPkgName(packageName);

                Intent intent = new Intent();
                intent.setComponent(serviceCMP);
                serviceInfo.setIntent(intent);

                serviceInfo.setPid(pid);
                serviceInfo.setUid(uid);
                serviceInfo.setProcessName(processName);
                serviceInfo.setProcessName(processName);

                serviceInfo.setActiveSince(activeSince);
                serviceInfo.setClientCount(clientCount);

                serviceInfos.add(serviceInfo);
                serviceInfo = null;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        return serviceInfos;
    }

}
