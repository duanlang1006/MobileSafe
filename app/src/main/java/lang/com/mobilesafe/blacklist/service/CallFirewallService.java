package lang.com.mobilesafe.blacklist.service;

import android.app.Service;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import lang.com.mobilesafe.ITelephony;
import lang.com.mobilesafe.blacklist.util.BlackNumberData;

/**
 * Created by android on 12/15/15.
 */
public class CallFirewallService extends Service {
    public static final String TAG = "CallFirewallService";

    public static final int STOP_SMS = 1;
    public static final int STOP_CALL = 2;
    public static final int STOP_SMSCALL = 4;

    private TelephonyManager tm;
    private BlackNumberData blackNumberData;
    private MyPhoneListener listener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        blackNumberData = new BlackNumberData(this);

        // 注册系统的电话状态改变的监听器.
        listener = new MyPhoneListener();

        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        // 系统的电话服务 就监听了 电话状态的变化,
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
    }


    private class MyPhoneListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:       // 手机铃声正在响.
                    int mode = blackNumberData.findNumberMode(incomingNumber);
                    if ((mode & STOP_CALL) != 0) {
                        // 黑名单号码
                        Log.i(TAG, "挂断电话");
                        //挂断电话
                        endcall(incomingNumber);
                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE:           // 手机的空闲状态
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:        // 手机接通通话的状态
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    private void endcall(String incomingNumber) {
        try {
            //使用反射获取系统的service方法
            Method method = Class.forName("android.os.ServiceManager").getMethod("getService", String.class);
            IBinder binder = (IBinder) method.invoke(null, new Object[]{TELEPHONY_SERVICE});

            //通过aidl实现方法的调用
            ITelephony telephony = ITelephony.Stub.asInterface(binder);
//            telephony.endCall();//该方法是一个异步方法，他会新开启一个线程将呼入的号码存入数据库中

            // 注册一个内容观察者 观察uri数据的变化
            getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, true, new MyObserver(new Handler(), incomingNumber));

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


    }

    private class MyObserver extends ContentObserver {
        private String incomingNumber;

        public MyObserver(Handler handler, String incomingNumber) {
            super(handler);
            this.incomingNumber = incomingNumber;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            //立即执行删除操作
            deleteCallLog(incomingNumber);
            //停止数据的观察
            getContentResolver().unregisterContentObserver(this);
        }
    }

    private void deleteCallLog(String incomingNumber) {
        // 呼叫记录内容提供者对应的uri
        Uri uri = Uri.parse("content://call_log/calls");
        // CallLog.Calls.CONTENT_URI;
        Cursor cursor = getContentResolver().query(uri, new String[]{"_id"}, "number=?", new String[]{incomingNumber}, null);
        while (cursor.moveToNext()) {
            String id = cursor.getString(0);
            getContentResolver().delete(uri, "_id=?", new String[]{id});
        }
        cursor.close();
    }
}
