package lang.com.mobilesafe.utils;

import android.graphics.drawable.Drawable;

/**
 * Created by android on 12/9/15.
 */
public class ProcessInfo {

    //应用程序包名
    private String packname;
    //应用程序图标
    private Drawable icon;
    //应用程序所占用的内存空间，单位是byte
    private long size;
    //是否属于用户进程
    private boolean userprocess;
    //进程的pid（进程的标记）
    private int pid;
    //进程的uid（进程所在UID）
    private int uid;
    //程序名称
    private String appname;
    //应用程序在Item中是否处于被选中状态（默认下没有被选中）
    private boolean checked;

    public String getPackname() {
        return packname;
    }

    public void setPackname(String packname) {
        this.packname = packname;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isUserprocess() {
        return userprocess;
    }

    public void setUserprocess(boolean userprocess) {
        this.userprocess = userprocess;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
