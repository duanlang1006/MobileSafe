package lang.com.mobilesafe.servicemanager;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import lang.com.mobilesafe.R;
import lang.com.mobilesafe.utils.ServiceInfo;
import lang.com.mobilesafe.utils.ServiceInfoProvide;

/**
 * Created by android on 12/9/15.
 */
public class ServiceManagerActivity extends Activity implements AdapterView.OnItemClickListener {
    private final String LOG_TAG = "servicemanager_log";

    private ListView servicelistview;

    private PackageManager pm;

    private List<ServiceInfo> runningServices;

    private ServiceInfoProvide serviceInfoProvide;

    private ServiceAdapter serviceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.servicemanager_layout);
        init();
        initView();
    }

    private void initView() {
        servicelistview = (ListView) findViewById(R.id.service_list);
        servicelistview.setOnItemClickListener(this);
        servicelistview.setAdapter(serviceAdapter);
    }

    private void init() {
        serviceInfoProvide = new ServiceInfoProvide(this);
        runningServices = serviceInfoProvide.getRunningService();
        serviceAdapter = new ServiceAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String clickItemPackageName = null;
        Object object = runningServices.get(position);

        if (object != null) {
            ServiceInfo serviceInfo = (ServiceInfo) object;
            clickItemPackageName = serviceInfo.getPkgName();
            forceStopAPK(clickItemPackageName);
        }
    }

    //强制杀死服务所在应用程序
    private void forceStopAPK(String pkgName) {
        Process sh = null;
        DataOutputStream os = null;
        try {
            sh = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(sh.getOutputStream());
            final String Command = "am force-stop " + pkgName + "\n";
            os.writeBytes(Command);
            os.flush();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

//        try {
//            sh.waitFor();
//        } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
    }


    private class ServiceAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return runningServices.size();
        }

        @Override
        public Object getItem(int position) {
            return runningServices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder holder;

            if (convertView == null) {
                view = View.inflate(getApplicationContext(), R.layout.service_item, null);
                holder = new ViewHolder();
                holder.appIcon = (ImageView) view.findViewById(R.id.imgApp);
                holder.tvAppLabel = (TextView) view.findViewById(R.id.tvAppLabel);
                holder.tvProcessId = (TextView) view.findViewById(R.id.tvProcessId);
                holder.tvProcessName = (TextView) view.findViewById(R.id.tvProcessName);
                holder.tvServiceName = (TextView) view.findViewById(R.id.tvServiceName);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }

            ServiceInfo serviceInfo = runningServices.get(position);

            holder.appIcon.setImageDrawable(serviceInfo.getAppIcon());
            holder.tvServiceName.setText(serviceInfo.getServiceName());
            holder.tvProcessName.setText(serviceInfo.getProcessName());
            holder.tvProcessId.setText(String.valueOf(serviceInfo.getPid()));
            holder.tvAppLabel.setText(serviceInfo.getAppLabel());

            return view;
        }

        private class ViewHolder {
            private ImageView appIcon;
            private TextView tvServiceName;
            private TextView tvAppLabel;
            private TextView tvProcessId;
            private TextView tvProcessName;
        }
    }
}
