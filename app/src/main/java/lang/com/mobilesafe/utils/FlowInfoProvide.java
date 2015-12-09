package lang.com.mobilesafe.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android on 12/9/15.
 */
public class FlowInfoProvide {

    private Context mContext;

    private PackageManager pm;

    public FlowInfoProvide(Context context) {
        mContext = context;
        pm = mContext.getPackageManager();
    }

    public List<FlowInfo> getFlowInfo() {
        List<PackageInfo> packageInfos = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);

        List<FlowInfo> flowInfos = new ArrayList<FlowInfo>();

        for (PackageInfo packageInfo : packageInfos) {
            String[] permissions = packageInfo.requestedPermissions;
            if (permissions != null && permissions.length > 0) {
                for (String permission : permissions) {
                    if (permission.equals("android.permission.INTERNET")) {
                        FlowInfo flowInfo = new FlowInfo();

                        flowInfo.setPackagename(packageInfo.packageName);
                        flowInfo.setIcon(packageInfo.applicationInfo.loadIcon(pm));
                        flowInfo.setAppname(packageInfo.applicationInfo.loadLabel(pm).toString());

                        int uid = packageInfo.applicationInfo.uid;
                        flowInfo.setRx(TrafficStats.getUidRxBytes(uid));
                        flowInfo.setTx(TrafficStats.getUidTxBytes(uid));
                        flowInfos.add(flowInfo);
                        flowInfo = null;
                        break;
                    }
                }
            }
        }
        return flowInfos;
    }
}
