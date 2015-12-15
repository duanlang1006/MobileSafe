package lang.com.mobilesafe.systemoptimization;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lang.com.mobilesafe.IPackageStatsObserver;
import lang.com.mobilesafe.R;

/**
 * Created by android on 12/7/15.
 */
public class SystemOptimizationActivity extends Activity {
    private final String LOG_TAG = "mobilesafe";

    public static final int ADD_ONE_RESULT = 1;

    private ProgressBar progressBar;
    private TextView cleanCacheStatus;

    private LinearLayout cleanCacheCont;        // 显示所有带有缓存的应用程序信息

    private PackageManager pm;

    private Map<String, Long> cacheinfo;        // 存放缓存信息

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case ADD_ONE_RESULT:    //扫描到一条
                    final String packname = (String) msg.obj;
                    // 获取这些应用程序的图标，名称，展现在界面上。
                    View child = View.inflate(getApplicationContext(), R.layout.cache_item, null);
                    // 为child注册一个监听器。
                    child.setOnClickListener(new View.OnClickListener() {
                        // 点击child时响应的点击事件
                        @Override
                        public void onClick(View v) {
                            // 判断SDK的版本号
                            if (Build.VERSION.SDK_INT >= 9) {
                                // 跳转至“清理缓存”的界面（可以通过：设置-->应用程序-->点击任意应用程序后的界面）
                                Intent intent = new Intent();
                                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                intent.addCategory(Intent.CATEGORY_DEFAULT);
                                intent.setData(Uri.parse("package:" + packname));
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent();
                                intent.setAction("android.intent.action.VIEW");
                                intent.addCategory(Intent.CATEGORY_DEFAULT);
                                intent.addCategory("android.intent.category.VOICE_LAUNCH");
                                intent.putExtra("pkg", packname);
                                startActivity(intent);
                            }
                        }
                    });
                    // 为child中的控件设置数据
                    ImageView iv_icon = (ImageView) child.findViewById(R.id.iv_cache_icon);
                    iv_icon.setImageDrawable(getApplicationIcon(packname));
                    TextView tv_name = (TextView) child.findViewById(R.id.tv_cache_name);
                    tv_name.setText(getApplicationName(packname));
                    TextView tv_size = (TextView) child.findViewById(R.id.tv_cache_size);
                    tv_size.setText("缓存大小 :" + Formatter.formatFileSize(getApplicationContext(), cacheinfo.get(packname)));
                    // 将child添加到ll_clean控件上。
                    cleanCacheCont.addView(child);
                    break;
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.systemoptimization_layout);
        initView();
        init();
    }

    private void initView() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        cleanCacheStatus = (TextView) findViewById(R.id.clean_cache_status);
        cleanCacheCont = (LinearLayout) findViewById(R.id.clean_cache_cont);
    }

    private void init() {
        pm = getPackageManager();
        scanPackages();
    }

    private void scanPackages() {
        new AsyncTask<Void, Integer, Void>() {
            List<PackageInfo> packinfos;

            @Override
            protected void onPreExecute() {
                cacheinfo = new HashMap<String, Long>();
                packinfos = pm.getInstalledPackages(0);
                progressBar.setMax(packinfos.size());
                cleanCacheStatus.setText("开始扫描...");

                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {
                int i = 0;
                for (PackageInfo info : packinfos) {
                    String packagename = info.packageName;
                    getSize(pm, packagename);
                    i++;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    publishProgress(i);
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                progressBar.setMax(values[0]);
                cleanCacheStatus.setText("正在扫描" + values[0] + "条目");
                super.onProgressUpdate(values);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                cleanCacheStatus.setText("扫描完毕..." + "发现有" + cacheinfo.size() + "个缓存信息");
                super.onPostExecute(aVoid);
            }
        }.execute();

    }

    private void getSize(PackageManager pm, String packagename) {
        try {
            // 获取到getPackageSizeInfo。调用getPackageSizeInfo方法需要在清单文件中配置权限信息：
            // <uses-permission android:name="android.permission.GET_PACKAGE_SIZE"/>
            Method method = pm.getClass().getDeclaredMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
            // 执行getPackageSizeInfo方法
            method.invoke(pm, packagename, new MyObersver(packagename));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MyObersver extends IPackageStatsObserver.Stub {
        private String packname;

        public MyObersver(String packname) {
            this.packname = packname;
        }

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
                throws RemoteException {
            // 以下是根据ApplicationsState代码中的SizeInfo对象中定义的
            // 缓存大小
            long cacheSize = pStats.cacheSize;
            // 代码大小
            long codeSize = pStats.codeSize;
            // 数据的大小
            long dataSize = pStats.dataSize;
            // 判断这个包名对应的应用程序是否有缓存，如果有，则存入到集合中。
            if (cacheSize > 0) {
                Message msg = Message.obtain();
                msg.what = ADD_ONE_RESULT;
                msg.obj = packname;
                handler.sendMessage(msg);
                cacheinfo.put(packname, cacheSize);
            }
        }
    }

    private Drawable getApplicationIcon(String packname) {
        try {
            PackageInfo packinfo = pm.getPackageInfo(packname, 0);
            return packinfo.applicationInfo.loadIcon(pm);

        } catch (Exception e) {
            e.printStackTrace();
            return getResources().getDrawable(R.drawable.ic_launcher);
        }
    }

    private String getApplicationName(String packname) {
        try {
            PackageInfo packinfo = pm.getPackageInfo(packname, 0);
            return packinfo.applicationInfo.loadLabel(pm).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return packname;
        }
    }

}
