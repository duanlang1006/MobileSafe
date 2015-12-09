package lang.com.mobilesafe.appmanager;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lang.com.mobilesafe.R;
import lang.com.mobilesafe.utils.AppInfo;
import lang.com.mobilesafe.utils.AppInfoProvide;
import lang.com.mobilesafe.utils.DensityUtil;

/**
 * Created by android on 12/7/15.
 */
public class AppManagerActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener, AbsListView.OnScrollListener {
    private final String LOG_TAG = "AppManager_log";

    private PackageManager pm;

    private View app_listview_loading;

    private ListView appmanager_listview;

    private TextView system_free_mem;
    private TextView sd_free_mem;

    public static final int LOAD_DONE = 0;

    private List<AppInfo> appInfos;
    private AppInfoProvide appInfoProvide;

    private List<AppInfo> userAppInfo;
    private List<AppInfo> systemAppInfo;

    private PopupWindow popupWindow;

    private String clickItemPackageName;
    private String clickItemAppName;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == LOAD_DONE) {
                app_listview_loading.setVisibility(View.INVISIBLE);
                appmanager_listview.setAdapter(new AppManagerAdapter());
            }
        }
    };

//    static class mhandler extends Handler {
//        WeakReference<AppManagerActivity> mActivity;
//
//        mhandler(AppManagerActivity activity) {
//            mActivity = new WeakReference<AppManagerActivity>(activity);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            AppManagerActivity activity = mActivity.get();
//            if (msg.what == LOAD_DONE) {
//                activity.app_listview_loading.setVisibility(View.INVISIBLE);
//                activity.appmanager_listview.setAdapter(new AppManagerAdapter());
//            }
//        }
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "onCreate ");
        setContentView(R.layout.appmanager_layout);
        initViews();
        init();
    }

    private void init() {
        pm = this.getPackageManager();
        appInfoProvide = new AppInfoProvide(this);
    }

    private void initViews() {

        system_free_mem = (TextView) findViewById(R.id.system_free_mem);
        sd_free_mem = (TextView) findViewById(R.id.sd_free_mem);
        app_listview_loading = findViewById(R.id.appmanager_loading);

        appmanager_listview = (ListView) findViewById(R.id.appmanager_listview);
        appmanager_listview.setOnItemClickListener(this);
        appmanager_listview.setOnScrollListener(this);

        system_free_mem.setText("SystemMemory : " + getAvailROMSize());
        sd_free_mem.setText("SDcardMemory : " + getAvailSDSize());
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadAppsList();
    }

    private void loadAppsList() {
        app_listview_loading.setVisibility(View.VISIBLE);
        AsyncTask.execute(mCollectAllAppsRunnable);
    }

    //获取系统中所有app
    private final Runnable mCollectAllAppsRunnable = new Runnable() {
        @Override
        public void run() {
            appInfos = appInfoProvide.getInstalledApps();
            initAppInfo();
//            Log.i(LOG_TAG, "appInfos : " + appInfos);
            Message msg = Message.obtain();
            msg.what = LOAD_DONE;
            handler.sendMessage(msg);
        }
    };

    private void initAppInfo() {
        Log.i(LOG_TAG, "initAppInfo ");
        userAppInfo = new ArrayList<>();
        systemAppInfo = new ArrayList<>();

        for (AppInfo appInfo : appInfos) {
            if (appInfo.isUserpp()) {
                userAppInfo.add(appInfo);
            } else {
                systemAppInfo.add(appInfo);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 100) {
            loadAppsList();
            system_free_mem.setText("SystemMemory : " + getAvailROMSize());
            sd_free_mem.setText("SDcardMemory : " + getAvailSDSize());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String getAvailROMSize() {
        File path = Environment.getDataDirectory();
        StatFs statFs = new StatFs(path.getPath());
        long blockSize = statFs.getBlockSize();
        long availableBlocks = statFs.getAvailableBlocks();
        return Formatter.formatFileSize(this, availableBlocks * blockSize);
    }

    private String getAvailSDSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs statFs = new StatFs(path.getPath());
        long blockSize = statFs.getBlockSize();
        long availableBlocks = statFs.getAvailableBlocks();
        return Formatter.formatFileSize(this, availableBlocks * blockSize);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.i(LOG_TAG, "onDestroy ");
        dismissPopupWindow();
        super.onDestroy();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        dismissPopupWindow();

        View contentView = View.inflate(getApplicationContext(), R.layout.app_manager_popup, null);
        Button btn_uninstall = (Button) contentView.findViewById(R.id.btn_uninstall);
        Button btn_launch = (Button) contentView.findViewById(R.id.btn_launch);
        Button btn_share = (Button) contentView.findViewById(R.id.btn_share);

        btn_uninstall.setOnClickListener(this);
        btn_launch.setOnClickListener(this);
        btn_share.setOnClickListener(this);

        Object obj = appmanager_listview.getItemAtPosition(position);

        if (obj != null) {
            AppInfo appinfo = (AppInfo) obj;
            clickItemPackageName = appinfo.getPackname();
            clickItemAppName = appinfo.getAppname();
            Log.i(LOG_TAG, "clickItemPackageName : " + clickItemPackageName);
            if (appinfo.isUserpp()) {
                btn_uninstall.setTag(true);
            } else {
                btn_uninstall.setTag(false);
            }
        }

        int top = view.getTop();
        int bottom = view.getBottom();

        popupWindow = new PopupWindow(contentView, DensityUtil.dip2px(getApplicationContext(), 250), bottom - top
                - DensityUtil.dip2px(getApplicationContext(), 20));
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.BLUE));
        int[] location = new int[2];
        view.getLocationInWindow(location);
        popupWindow.showAtLocation(view, Gravity.TOP | Gravity.START, location[0] + 20, location[1]);
    }

    private void dismissPopupWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_uninstall:
                boolean result = (Boolean) v.getTag();
                Log.i(LOG_TAG, "result : " + result);
                if (result) {
                    uninstallApplication();
                } else {
                    Toast.makeText(this, "System app, can not uninstall", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_launch:
                startApplicatio();
                break;
            case R.id.btn_share:
                shareApplication();
                break;
            default:
                break;
        }
    }

    private void shareApplication() {
        Log.i(LOG_TAG, "shareApplication ");
        Intent intent = new Intent();
        intent.setAction("android.intent.action.SEND");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("text/plain");
        intent.putExtra("subject", clickItemAppName);
        intent.putExtra("sms_body", "推荐你使用一款软件 " + clickItemAppName);
        intent.putExtra(Intent.EXTRA_TEXT, "推荐你使用一款软件 " + clickItemAppName);
        startActivity(intent);
    }

    private void uninstallApplication() {
        dismissPopupWindow();
        Intent intent = new Intent();
        intent.setAction("android.intent.action.DELETE");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(Uri.parse("package:" + clickItemPackageName));
        startActivityForResult(intent, 100);
    }

    private void startApplicatio() {
        Log.i(LOG_TAG, "startApplicatio ");
        dismissPopupWindow();
        Intent intent = new Intent();
        PackageInfo packinfo;

        try {
            packinfo = pm.getPackageInfo(clickItemPackageName, PackageManager.GET_ACTIVITIES);
            ActivityInfo[] activityinfos = packinfo.activities;
            Log.i(LOG_TAG, "activityinfos : " + activityinfos);

            if (activityinfos != null && activityinfos.length > 0) {
                String className = activityinfos[0].name;
                intent.setClassName(clickItemPackageName, className);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);             //new task to get activitytask
                startActivity(intent);
            } else {
                Toast.makeText(this, "launch fail", Toast.LENGTH_SHORT).show();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "launch fail", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //// TODO: 12/8/15
        Log.i(LOG_TAG, "onScrollStateChanged  scrollState : " + scrollState);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        Log.i(LOG_TAG, "onScroll firstVisibleItem : " + firstVisibleItem +
                "  visibleItemCount : " + visibleItemCount +
                "  totalItemCount : " + totalItemCount);
        dismissPopupWindow();
    }

    private class AppManagerAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return 1 + userAppInfo.size() + 1 + systemAppInfo.size();
        }

        @Override
        public Object getItem(int position) {
            if (position == 0) {
                return position;
            } else if (position <= userAppInfo.size()) {
                return userAppInfo.get(position - 1);
            } else if (position == userAppInfo.size() + 1) {
                return position;
            } else {
                return systemAppInfo.get(position - userAppInfo.size() - 2);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean isEnabled(int position) {
            return !(position == 0 || position == (userAppInfo.size() + 1)) && super.isEnabled(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder holder;


            if (position == 0) {
                TextView tv = new TextView(getApplicationContext());
                tv.setTextSize(20);
                tv.setTextColor(Color.RED);
                tv.setText("UserApp Num (" + userAppInfo.size() + ")");
                return tv;
            } else if (position <= userAppInfo.size()) {
                if (null == convertView || convertView instanceof TextView) {
                    view = View.inflate(getApplicationContext(), R.layout.appmanager_list_item, null);
                    holder = new ViewHolder();
                    holder.app_icon = (ImageView) view.findViewById(R.id.app_manager_item_icon);
                    holder.app_name = (TextView) view.findViewById(R.id.app_manager_item_name);
                    holder.app_version = (TextView) view.findViewById(R.id.app_manager_item_version);
                    holder.app_packagename = (TextView) view.findViewById(R.id.app_manager_item_packagename);
                    view.setTag(holder);
                } else {
                    view = convertView;
                    holder = (ViewHolder) view.getTag();
                }
                AppInfo appInfo = userAppInfo.get(position - 1);
                holder.app_icon.setImageDrawable(appInfo.getAppicon());
                holder.app_name.setText(appInfo.getAppname());
                holder.app_version.setText("version : " + appInfo.getVersion());
                holder.app_packagename.setText(appInfo.getPackname());
                return view;
            } else if (position == userAppInfo.size() + 1) {
                TextView tv = new TextView(getApplicationContext());
                tv.setTextSize(20);
                tv.setTextColor(Color.RED);
                tv.setText("SystemApp Num (" + systemAppInfo.size() + ")");
                return tv;
            } else {
                if (null == convertView || convertView instanceof TextView) {
                    view = View.inflate(getApplicationContext(), R.layout.appmanager_list_item, null);
                    holder = new ViewHolder();
                    holder.app_icon = (ImageView) view.findViewById(R.id.app_manager_item_icon);
                    holder.app_name = (TextView) view.findViewById(R.id.app_manager_item_name);
                    holder.app_version = (TextView) view.findViewById(R.id.app_manager_item_version);
                    holder.app_packagename = (TextView) view.findViewById(R.id.app_manager_item_packagename);
                    view.setTag(holder);
                } else {
                    view = convertView;
                    holder = (ViewHolder) view.getTag();
                }

                AppInfo appInfo = systemAppInfo.get(position - userAppInfo.size() - 2);

                holder.app_icon.setImageDrawable(appInfo.getAppicon());
                holder.app_name.setText(appInfo.getAppname());
                holder.app_version.setText("version : " + appInfo.getVersion());
                holder.app_packagename.setText(appInfo.getPackname());
                return view;
            }
        }

        private class ViewHolder {
            ImageView app_icon;
            TextView app_name;
            TextView app_version;
            TextView app_packagename;
        }
    }
}
