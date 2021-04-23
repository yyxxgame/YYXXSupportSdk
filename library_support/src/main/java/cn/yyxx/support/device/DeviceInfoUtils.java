package cn.yyxx.support.device;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import cn.yyxx.support.FileUtils;
import cn.yyxx.support.hawkeye.LogUtils;

/**
 * @author #Suyghur.
 * Created on 2021/04/23
 */
public class DeviceInfoUtils {

    private static final int IMEI = 1000;
    private static final int IMSI = 2000;
    private static final int SIM = 3000;
    private static final int SIM_OPERATOR = 4000;
    private static final int SIM_OPERATOR_NAME = 5000;
    private static final int NETWORK_OPERATOR = 6000;
    private static final int NETWORK_OPERATOR_NAME = 7000;
    private static final int DEVICE_SOFTWARE_VERSION = 8000;
    private static final int PHONE_NUMBER = 9000;

    @SuppressLint("MissingPermission")
    private static String getTelephoneInfo(Context context, int type) {
        String info = "";
        try {
            if (context == null) {
                return info;
            }
            TelephonyManager phone = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (phone != null) {
                switch (type) {
                    case IMEI:
                        info = phone.getDeviceId();
                        break;
                    case IMSI:
                        info = phone.getSubscriberId();
                        break;
                    case SIM:
                        info = phone.getSimSerialNumber();
                        break;
                    case SIM_OPERATOR:
                        info = phone.getSimOperator();
                        break;
                    case SIM_OPERATOR_NAME:
                        info = phone.getSimOperatorName();
                        break;
                    case NETWORK_OPERATOR:
                        info = phone.getNetworkOperator();
                        break;
                    case NETWORK_OPERATOR_NAME:
                        info = phone.getNetworkOperatorName();
                        break;
                    case DEVICE_SOFTWARE_VERSION:
                        info = phone.getDeviceSoftwareVersion();
                        break;
                    case PHONE_NUMBER:
                        info = phone.getLine1Number();
                        break;
                }
            }
        } catch (Exception e) {
            LogUtils.e("get phone info error: " + e.getLocalizedMessage());
        }
        return info;
    }

    /**
     * 获取手机品牌
     */
    public static String getDeviceBrand() {
        return Build.BRAND;
    }

    /**
     * 获取手机型号
     */
    public static String getDeviceModel() {
        return Build.MODEL;
    }

    /**
     * 获取手机制造商
     */
    public static String getDeviceManufacturer() {
        return Build.MANUFACTURER;
    }

    /**
     * 获取手机号码
     */
    @SuppressLint("MissingPermission")
    public static String getPhoneNumber(Context context) {
        return getTelephoneInfo(context, PHONE_NUMBER);
    }

    /**
     * 获取imei
     */
    public static String getImei(Context context) {
        String imei = getTelephoneInfo(context, IMEI);
        return TextUtils.isEmpty(imei) ? "0" : imei;
    }

    /**
     * 获取imsi
     */
    public static String getImsi(Context context) {
        return getTelephoneInfo(context, IMSI);
    }


    /**
     * 获得手机sim
     */
    public static String getSim(Context context) {
        return getTelephoneInfo(context, SIM);
    }

    /**
     * 获取手机序列号
     */
    public static String getSerialNumber() {
        String serial = "";
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serial;
    }

    /**
     * 获取安卓设备ID
     */
    public static String getAndroidDeviceId(Context context) {
        String androidId = "";
        if (context == null) {
            return androidId;
        }
        androidId = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        return androidId;
    }

    /**
     * 获取cpu架构
     */
    public static String getCpuAbi() {
        String[] abis;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            abis = Build.SUPPORTED_ABIS;
        } else {
            abis = new String[]{Build.CPU_ABI, Build.CPU_ABI2};
        }
        StringBuilder abiStr = new StringBuilder();
        for (String abi : abis) {
            abiStr.append(abi);
            abiStr.append(',');
        }
        return abiStr.toString();
    }

    /**
     * 获取cpu核数
     */
    public static String getCpuCount() {
        return Runtime.getRuntime().availableProcessors() + "";
    }

    /**
     * 获取手机运行内存ram
     */
    public static String getDeviceRam() {
        String path = "/proc/meminfo";
        String firstLine = null;
        int totalRam = 0;
        try {
            FileReader fileReader = new FileReader(path);
            BufferedReader br = new BufferedReader(fileReader, 8192);
            firstLine = br.readLine().split("\\s+")[1];
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (firstLine != null) {
            totalRam = (int) Math.ceil((Float.valueOf(Float.parseFloat(firstLine) / (1024 * 1024)).doubleValue()));
        }
        //返回1GB/2GB/3GB/4GB
        return totalRam + "GB";
    }

    /**
     * 获取应用可用运行内存Ram
     */
    public static String getAppAvailRam(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        //mi.availMem
        return String.valueOf(mi.availMem / 1024 / 1024);
    }

    /**
     * 获得手机MAC
     *
     * @param context
     * @return
     */
    public static String getMacAddress(Context context) {
        String s = null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            s = getMacFromWifiInfo(context);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            s = getMacFromFile();
        } else {
            s = getMachineHardwareAddress();
        }
        if (TextUtils.isEmpty(s)) {
            s = "";
        }
        return s.toLowerCase();
    }

    /**
     * 6.0以上7.0以下获取mac
     */
    private static String getMacFromFile() {
        String str = "";
        String macSerial = "";
        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if ("".equals(macSerial)) {
            try {
                return FileUtils.loadFileAsString("/sys/class/net/eth0/address").toUpperCase().substring(0, 17);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return macSerial;
    }

    /**
     * 6.0以下获取mac
     */
    private static String getMacFromWifiInfo(Context context) {
        try {
            WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            return info.getMacAddress();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    /**
     * 7.0以上获取mac
     * 获取设备HardwareAddress地址
     */
    public static String getMachineHardwareAddress() {
        Enumeration<NetworkInterface> interfaces = null;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        String hardWareAddress = null;
        NetworkInterface iF = null;
        if (interfaces == null) {
            return null;
        }
        while (interfaces.hasMoreElements()) {
            iF = interfaces.nextElement();
            try {
                if (!iF.getName().equalsIgnoreCase("wlan0")) {
                    continue;
                }
                hardWareAddress = bytes2String(iF.getHardwareAddress());
                if (hardWareAddress != null) {
                    break;
                }
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

        return hardWareAddress;
    }

    /***
     * byte转为String
     */
    private static String bytes2String(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        for (byte b : bytes) {
            buf.append(String.format("%02X:", b));
        }
        if (buf.length() > 0) {
            buf.deleteCharAt(buf.length() - 1);
        }
        return buf.toString();
    }
}
