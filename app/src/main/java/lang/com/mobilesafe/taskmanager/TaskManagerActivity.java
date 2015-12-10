package lang.com.mobilesafe.taskmanager;

import android.app.Activity;
import android.app.ActivityManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import lang.com.mobilesafe.R;
import lang.com.mobilesafe.utils.ProcessInfo;
import lang.com.mobilesafe.utils.ProcessInfoProvide;

/**
 * Created by android on 12/7/15.
 */
public class TaskManagerActivity extends Activity implements View.OnClickListener {
    private final String LOG_TAG = "mobilesafe";

    // 判断当前显示的列表是用户进程还是系统进程
    private boolean showUserApp = true;

    // 切换用户进程和系统进程的按钮（用于响应“全选”与“一键清理”按钮时：判断是用户进程，还是系统进程）
    private Button btn_user;
    private Button btn_system;

    // 显示用户进程
    private ListView user_listveiw;
    // 显示系统进程
    private ListView system_listview;

    // 用户进程所在列表的适配器
    private UserAdapter useradapter;
    // 系统进程所在列表的适配器
    private SystemAdapter systemadapter;

    // 存放用户进程的集合
    private List<ProcessInfo> userProcessInfos;
    // 存放系统进程的集合
    private List<ProcessInfo> systemProcessInfos;

    // 为系统进程添加的一个Item，该Item上显示“杀死系统进程会导致系统不稳定”文字。
    private TextView system_header;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progressmanager_layout);
        init();
        initView();
    }

    private void init() {
        ProcessInfoProvide processInfoProvide = new ProcessInfoProvide(this);
        userProcessInfos = new ArrayList<>();
        systemProcessInfos = new ArrayList<>();

        List<ProcessInfo> mRunningProcessInfo = processInfoProvide.getProgressInfo();

        for (ProcessInfo info : mRunningProcessInfo) {
            if (info.isUserprocess()) {
                userProcessInfos.add(info);
            } else {
                systemProcessInfos.add(info);
            }
        }
    }

    private void initView() {

        user_listveiw = (ListView) findViewById(R.id.userprocess_listview);
        user_listveiw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        system_listview = (ListView) findViewById(R.id.systemprocess_listview);
        system_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });


        btn_user = (Button) findViewById(R.id.btn_task_user);
        btn_user.setOnClickListener(this);
        btn_user.setBackgroundResource(R.drawable.btn_pressed);
        btn_system = (Button) findViewById(R.id.btn_task_system);
        btn_system.setOnClickListener(this);
        btn_system.setBackgroundResource(R.drawable.btn_normal);

        system_listview.setVisibility(View.GONE);

        useradapter = new UserAdapter();
        user_listveiw.setAdapter(useradapter);

        system_header = new TextView(getApplicationContext());
        system_header.setText("Killing system processes will lead to system instability");
        system_header.setBackgroundColor(Color.RED);
        system_listview.addHeaderView(system_header);

        systemadapter = new SystemAdapter();
        system_listview.setAdapter(systemadapter);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_task_user) {
            if (system_header != null) {
                system_listview.removeHeaderView(system_header);
                system_header = null;
            }
            showUserApp = true;
            btn_user.setBackgroundResource(R.drawable.btn_pressed);
            btn_system.setBackgroundResource(R.drawable.btn_normal);
            user_listveiw.setVisibility(View.VISIBLE);
            system_listview.setVisibility(View.INVISIBLE);
        } else if (v.getId() == R.id.btn_task_system) {
            if (system_header == null) {
                system_header = new TextView(getApplicationContext());
                system_header.setText("Killing system processes will lead to system instability");
                system_header.setBackgroundColor(Color.RED);
                system_listview.addHeaderView(system_header);
            }
            showUserApp = false;
            btn_user.setBackgroundResource(R.drawable.btn_normal);
            btn_system.setBackgroundResource(R.drawable.btn_pressed);
            user_listveiw.setVisibility(View.INVISIBLE);
            system_header.setVisibility(View.VISIBLE);
            system_listview.setVisibility(View.VISIBLE);
        }
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
    protected void onDestroy() {
        super.onDestroy();
    }

    public void selectAll(View view) {
        Log.i(LOG_TAG, "click select all button");
        if (showUserApp) {
            for (ProcessInfo info : userProcessInfos) {
                info.setChecked(true);
                useradapter.notifyDataSetChanged();
            }
        } else {
            for (ProcessInfo info : systemProcessInfos) {
                info.setChecked(true);
                systemadapter.notifyDataSetChanged();
            }
        }
    }

    public void oneKeyClear(View view) {
        Log.i(LOG_TAG, "click clear button");
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        int count = 0;
        long killsize = 0;

        List<ProcessInfo> killProccessInfo = new ArrayList<>();

        if (showUserApp) {
            for (ProcessInfo info : userProcessInfos) {
                if (info.isChecked()) {
                    count++;
                    killsize += info.getSize();
                    activityManager.killBackgroundProcesses(info.getPackname());
                    killProccessInfo.add(info);
                }
            }
        } else {
            for (ProcessInfo info : systemProcessInfos) {
                if (info.isChecked()) {
                    count++;
                    killsize += info.getSize();
                    activityManager.killBackgroundProcesses(info.getPackname());
                    killProccessInfo.add(info);
                }
            }
        }

        //迭代出被杀死的进程，判断哪个集合中包含该进程，如果包含，则移除掉（用于更新列表显示）
        for (ProcessInfo info : killProccessInfo) {
            if (info.isUserprocess()) {
                if (userProcessInfos.contains(info))
                    userProcessInfos.remove(info);
            } else {
                if (systemProcessInfos.contains(info))
                    systemProcessInfos.remove(info);
            }
        }

        //更新数据显示
        if (showUserApp) {
            useradapter.notifyDataSetChanged();
        } else {
            systemadapter.notifyDataSetChanged();
        }

        Toast.makeText(this,
                String.format("killed %d processes , and release %s memory", count, String.valueOf(killsize)),
                Toast.LENGTH_SHORT)
                .show();
    }

    static class ViewHolder {
        ImageView app_icon;
        TextView app_name;
        TextView app_pid;
        TextView app_uid;
        TextView app_memory;
        CheckBox app_status;
    }

    private class UserAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return userProcessInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return userProcessInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            // 使用堆内存中的唯一的一份字节码（ListView的优化）
            ViewHolder holder = new ViewHolder();
            // 复用缓存（ListView的优化）
            if (convertView == null) {
                view = View.inflate(getApplicationContext(), R.layout.task_manager_item, null);
                holder.app_icon = (ImageView) view.findViewById(R.id.iv_taskmanager_icon);
                holder.app_pid = (TextView) view.findViewById(R.id.tv_taskmanager_pid);
                holder.app_uid = (TextView) view.findViewById(R.id.tv_taskmanager_uid);
                holder.app_name = (TextView) view.findViewById(R.id.tv_taskmanager_name);
                holder.app_memory = (TextView) view.findViewById(R.id.tv_taskmanager_mem);
                holder.app_status = (CheckBox) view.findViewById(R.id.cb_taskmanager);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }

            ProcessInfo processInfo = userProcessInfos.get(position);

            if (processInfo.getPackname().equals(getPackageName())) {
                holder.app_status.setVisibility(View.INVISIBLE);
            } else {
                holder.app_status.setVisibility(View.VISIBLE);
            }

            holder.app_icon.setImageDrawable(processInfo.getIcon());
            holder.app_name.setText("Package : " + processInfo.getPackname());
            holder.app_pid.setText("PID : " + String.valueOf(processInfo.getPid()));
            holder.app_uid.setText("UID : " + String.valueOf(processInfo.getUid()));
            holder.app_memory.setText("Memory Size : " + String.valueOf(processInfo.getSize()));
            holder.app_status.setChecked(processInfo.isChecked());

            return view;
        }
    }

    private class SystemAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return systemProcessInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return systemProcessInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder holder = new ViewHolder();
            if (convertView == null) {
                view = View.inflate(getApplicationContext(), R.layout.task_manager_item, null);
                holder.app_icon = (ImageView) view.findViewById(R.id.iv_taskmanager_icon);
                holder.app_pid = (TextView) view.findViewById(R.id.tv_taskmanager_pid);
                holder.app_uid = (TextView) view.findViewById(R.id.tv_taskmanager_uid);
                holder.app_name = (TextView) view.findViewById(R.id.tv_taskmanager_name);
                holder.app_memory = (TextView) view.findViewById(R.id.tv_taskmanager_mem);
                holder.app_status = (CheckBox) view.findViewById(R.id.cb_taskmanager);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }

            ProcessInfo processInfo = systemProcessInfos.get(position);
            holder.app_icon.setImageDrawable(processInfo.getIcon());
            holder.app_name.setText("Package : " + processInfo.getPackname());
            holder.app_pid.setText("PID : " + String.valueOf(processInfo.getPid()));
            holder.app_uid.setText("UID : " + String.valueOf(processInfo.getUid()));
            holder.app_memory.setText("Memory Size : " + String.valueOf(processInfo.getSize()));
            holder.app_status.setChecked(processInfo.isChecked());

            return view;
        }
    }


}
