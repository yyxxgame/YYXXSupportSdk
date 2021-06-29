package cn.yyxx.support.device;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

import cn.yyxx.support.FileUtils;
import cn.yyxx.support.emulator.EmulatorFiles;
import cn.yyxx.support.emulator.newfunc.EmulatorCheck;
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
     * 判断是否包含SIM卡
     *
     * @return 状态
     */
    public static boolean hasSimCard(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telephonyManager.getSimState();
        return simState != TelephonyManager.SIM_STATE_ABSENT && simState != TelephonyManager.SIM_STATE_UNKNOWN;
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
        androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
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
     * 获取系统版本
     */
    public static String getDeviceSoftwareVersion() {
        return Build.VERSION.RELEASE;
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

    /**
     * 判断网络是否可用
     *
     * @param context 上下文
     */
    @SuppressLint("MissingPermission")
    public static boolean isAvailable(Context context) {
        if (context == null) {
            return false;
        }
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isAvailable();
    }

    /**
     * 判断网络是否已连接或正在连接
     *
     * @param context 上下文
     */
    @SuppressLint("MissingPermission")
    public static boolean isNetworkConnected(Context context) {
        if (context == null) {
            return false;
        }
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }

    /**
     * 获取运营商CODE
     *
     * @param context
     * @return
     */
    public static String getSimOperatorCode(Context context) {
        return getTelephoneInfo(context, SIM_OPERATOR);
    }

    /**
     * 获取运营商名称
     *
     * @param context
     * @return
     */
    public static String getSimOperatorName(Context context) {
        return getTelephoneInfo(context, SIM_OPERATOR_NAME);
    }

    /**
     * 获取运营商
     * 1、移动；2、联通；3、电信；4、其他
     */
    public static String getSimOperator(Context context) {
        String code = getSimOperatorCode(context);
        if (code.length() > 0) {
            if (code.equals("46000") || code.equals("46002")
                    || code.equals("46007")) {
                // 中国移动
                //LogUtils.d("中国移动");
                return "1";
            } else if (code.equals("46001") || code.equals("46006")) {
                // 中国联通
                //LogUtils.d("中国联通");
                return "2";
            } else if (code.equals("46003") || code.equals("46005")) {
                // 中国电信
                //LogUtils.d("中国电信");
                return "3";
            } else {
                //LogUtils.d("无或其他");
                return "4";
            }
        }
        return "4";
    }

    @SuppressLint("MissingPermission")
    public static String getNetworkClass(Context context) {
        //获取系统的网络服务
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        //如果当前没有网络
        if (null == connManager)
            return "none";

        //获取当前网络类型，如果为空，返回无网络
        NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
        if (activeNetInfo == null || !activeNetInfo.isAvailable()) {
            return "none";
        }

        // 判断是不是连接的是不是wifi
        NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (null != wifiInfo) {
            NetworkInfo.State state = wifiInfo.getState();
            if (null != state)
                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                    return "wifi";
                }
        }

        // 如果不是wifi，则判断当前连接的是运营商的哪种网络2g、3g、4g等
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (null != networkInfo) {
            NetworkInfo.State state = networkInfo.getState();
            String strSubTypeName = networkInfo.getSubtypeName();
            if (null != state)
                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                    switch (activeNetInfo.getSubtype()) {
                        //如果是2g类型
                        case TelephonyManager.NETWORK_TYPE_GPRS: // 联通2g
                        case TelephonyManager.NETWORK_TYPE_CDMA: // 电信2g
                        case TelephonyManager.NETWORK_TYPE_EDGE: // 移动2g
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            return "2G";
                        //如果是3g类型
                        case TelephonyManager.NETWORK_TYPE_EVDO_A: // 电信3g
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            return "3G";
                        //如果是4g类型
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            return "4G";
                        case TelephonyManager.NETWORK_TYPE_NR:
                            return "5G";
                        default:
                            //中国移动 联通 电信 三种3G制式
                            if (strSubTypeName.equalsIgnoreCase("TD-SCDMA") || strSubTypeName.equalsIgnoreCase("WCDMA") || strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                                return "3G";
                            } else {
                                return "wifi";
                            }
                    }
                }
        }
        return "none";
    }

    public static String getNetworkType(Context context) {
        String networkClz = getNetworkClass(context);
        if ("none".equals(networkClz) || "wifi".equals(networkClz)) {
            return "0";
        } else {
            return "1";
        }
    }

    public static boolean isCharged(Context context) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatusIntent = context.registerReceiver(null, ifilter);
        //如果设备正在充电，可以提取当前的充电状态和充电方式（无论是通过 USB 还是交流充电器），如下所示：

        // Are we charging / charged?
        int status = batteryStatusIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        // How are we charging?
        int chargePlug = batteryStatusIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        if (isCharging) {
            if (usbCharge) {
                return false;
            } else {
                return acCharge;
            }
        } else {
            return false;
        }
    }

    /**
     * 判断是不是模拟器
     */
    public static boolean isEmulator(Context context) {
        // readSysProperty 和 hasEmulatorAdb 会对真机造成误伤
//        boolean b = readSysProperty();
//        if (!b) {
//            if (FindEmulator.hasEmulatorAdb()) {
//                return true;
//            }
//        }
        boolean b = EmulatorFiles.hasEmulatorFile();
        if (!b) {
            return DeviceInfoUtils.isPcKernel();
        }
        return b;
    }

    public static boolean isEmulator2(Context context) {
        return EmulatorCheck.readSysProperty(context);
    }


    /**
     * cat /proc/cpuinfo
     * 从cpuinfo中读取cpu架构，检测CPU是否是PC端
     */
    public static boolean isPcKernel() {
        String str = "";
        try {
            Process start = new ProcessBuilder("/system/bin/cat", "/proc/cpuinfo").start();
            StringBuilder stringBuffer = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(start.getInputStream(), StandardCharsets.UTF_8));
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                }
                stringBuffer.append(readLine);
            }
            bufferedReader.close();
            str = stringBuffer.toString().toLowerCase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str.contains("intel") || str.contains("amd");
    }

}
