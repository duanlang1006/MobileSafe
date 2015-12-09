package lang.com.mobilesafe.flowstatistics;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import lang.com.mobilesafe.R;
import lang.com.mobilesafe.utils.FlowInfo;
import lang.com.mobilesafe.utils.FlowInfoProvide;

/**
 * Created by android on 12/7/15.
 */
public class FlowStatisticsActivity extends Activity {
    private final String LOG_TAG = "FlowStatisticsActivity";

    private ListView flow_listview;
    private View flow_loading;

    private FlowAdapter flowAdapter;

    private FlowInfoProvide flowInfoProvide;

    private List<FlowInfo> flowInfos;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            flow_loading.setVisibility(View.GONE);
            flow_listview.setAdapter(flowAdapter);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flowstatistics_layout);
        init();
        initView();
    }

    private void initView() {
        flow_loading = (View) findViewById(R.id.flow_info_loading);
        flow_loading.setVisibility(View.VISIBLE);
        flow_listview = (ListView) findViewById(R.id.flow_listview);
    }

    private void init() {
        flowInfoProvide = new FlowInfoProvide(this);
        flowAdapter = new FlowAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AsyncTask.execute(mRefreshFlowRunnable);
    }

    private final Runnable mRefreshFlowRunnable = new Runnable() {
        @Override
        public void run() {
            flowInfos = flowInfoProvide.getFlowInfo();
            handler.sendEmptyMessage(0);
        }
    };

    private class FlowAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return flowInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return flowInfos.get(position);
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
                view = View.inflate(getApplicationContext(), R.layout.flow_item, null);
                holder = new ViewHolder();
                holder.icon = (ImageView) view.findViewById(R.id.flow_app_icon);
                holder.appname = (TextView) view.findViewById(R.id.flow_app_name);
                holder.packagename = (TextView) view.findViewById(R.id.flow_app_packagename);
                holder.tx = (TextView) view.findViewById(R.id.flow_upload);
                holder.rx = (TextView) view.findViewById(R.id.flow_download);
                holder.size = (TextView) view.findViewById(R.id.flow_totle_size);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }

            FlowInfo info = flowInfos.get(position);

            holder.icon.setImageDrawable(info.getIcon());
            holder.appname.setText(info.getAppname());
            holder.packagename.setText(info.getPackagename());
            holder.tx.setText(Formatter.formatFileSize(getApplicationContext(), info.getTx()));
            holder.rx.setText(Formatter.formatFileSize(getApplicationContext(), info.getRx()));
            holder.size.setText(Formatter.formatFileSize(getApplicationContext(), info.getTx() + info.getRx()));

            return view;
        }
    }

    private static class ViewHolder {
        ImageView icon;
        TextView appname;
        TextView packagename;
        TextView tx;
        TextView rx;
        TextView size;
    }
}
