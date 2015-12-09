package lang.com.mobilesafe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import lang.com.mobilesafe.applock.AppLockActivity;
import lang.com.mobilesafe.appmanager.AppManagerActivity;
import lang.com.mobilesafe.blacklist.BlackListActivity;
import lang.com.mobilesafe.flowstatistics.FlowStatisticsActivity;
import lang.com.mobilesafe.mobileantivirus.MobileAntivirusActivity;
import lang.com.mobilesafe.servicemanager.ServiceManagerActivity;
import lang.com.mobilesafe.settings.SettingsActivity;
import lang.com.mobilesafe.systemoptimization.SystemOptimizationActivity;
import lang.com.mobilesafe.taskmanager.TaskManagerActivity;
import lang.com.mobilesafe.theftProof.TheftProofActivity;
import lang.com.mobilesafe.tools.ToolsActivity;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener {
    private final String LOG_TAG = "mobilesafe";

    private GridView gridView;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        initView();

    }

    private void initView() {
        gridView = (GridView) findViewById(R.id.gv_main);
        textView = (TextView) findViewById(R.id.textView);

        gridView.setAdapter(new MainGridAdapter(this));
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = null;
        switch (position) {
            case 0:
                //手机防盗
                intent = new Intent(this, TheftProofActivity.class);
                startActivity(intent);
                break;
            case 1:
                //来电黑名单
                intent = new Intent(this, BlackListActivity.class);
                startActivity(intent);
                break;
            case 2:
                //软件管理
                intent = new Intent(this, AppManagerActivity.class);
                startActivity(intent);
                break;
            case 3:
                //进程管理
                intent = new Intent(this, TaskManagerActivity.class);
                startActivity(intent);
                break;
            case 4:
                //服务管理
                intent = new Intent(this, ServiceManagerActivity.class);
                startActivity(intent);
                break;
            case 5:
                //流量统计
                intent = new Intent(this, FlowStatisticsActivity.class);
                startActivity(intent);
                break;
            case 6:
                //手机杀毒
                intent = new Intent(this, MobileAntivirusActivity.class);
                startActivity(intent);
                break;
            case 7:
                //系统优化
                intent = new Intent(this, SystemOptimizationActivity.class);
                startActivity(intent);
                break;
            case 8:
                //系统优化
                intent = new Intent(this, AppLockActivity.class);
                startActivity(intent);
                break;
            case 9:
                //其他工具
                intent = new Intent(this, ToolsActivity.class);
                startActivity(intent);
                break;
            case 10:
                //设置中心
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case 11:
                //其他
                break;

            default:
                break;
        }
    }

    private class MainGridAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private Context context;

        public final int[] icons = {
                R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher,
                R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher,
                R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher,
                R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher,
                R.drawable.ic_launcher
        };

        public final String[] names = {
                "TheftProof",
                "BlackList",
                "AppManager",
                "TaskManager",
                "ServiceManager",
                "FlowStatistics",
                "MobileAntivirus",
                "SystemOptimization",
                "AppLock",
                "Tools",
                "Settings",
                "Else"
        };

        public MainGridAdapter(Context context) {
            this.context = context;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return names.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = inflater.inflate(R.layout.item, null);
            ImageView itemIcon = (ImageView) view.findViewById(R.id.item_icon);
            TextView itemText = (TextView) view.findViewById(R.id.item_name);
            itemIcon.setImageResource(icons[position]);
            itemText.setText(names[position]);

            return view;
        }

    }
}
