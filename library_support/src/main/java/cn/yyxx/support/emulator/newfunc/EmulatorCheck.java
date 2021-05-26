package cn.yyxx.support.emulator.newfunc;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.text.TextUtils;

import static android.content.Context.SENSOR_SERVICE;


public class EmulatorCheck {

    //可能是模拟器
    private static final int RESULT_MAYBE_EMULATOR = 0;
    //模拟器
    private static final int RESULT_EMULATOR = 1;
    //可能是真机
    private static final int RESULT_UNKNOWN = 2;

    private EmulatorCheck() {

    }

    public static boolean readSysProperty(Context context) {

        int suspectCount = 0;

        //检测硬件名称
        switch (checkFeaturesByHardware()) {
            case RESULT_MAYBE_EMULATOR:
                ++suspectCount;
                break;
            case RESULT_EMULATOR:
                return true;
        }

        //检测渠道
        switch (checkFeaturesByFlavor()) {
            case RESULT_MAYBE_EMULATOR:
                ++suspectCount;
                break;
            case RESULT_EMULATOR:
                return true;
        }

        //检测设备型号
        switch (checkFeaturesByModel()) {
            case RESULT_MAYBE_EMULATOR:
                ++suspectCount;
                break;
            case RESULT_EMULATOR:
                return true;
        }

        //检测硬件制造商
        switch (checkFeaturesByManufacturer()) {
            case RESULT_MAYBE_EMULATOR:
                ++suspectCount;
                break;
            case RESULT_EMULATOR:
                return true;
        }

        //检测主板名称
        switch (checkFeaturesByBoard()) {
            case RESULT_MAYBE_EMULATOR:
                ++suspectCount;
                break;
            case RESULT_EMULATOR:
                return true;
        }

        //检测主板平台
        switch (checkFeaturesByPlatform()) {
            case RESULT_MAYBE_EMULATOR:
                ++suspectCount;
                break;
            case RESULT_EMULATOR:
                return true;
        }

        //检测基带信息
        switch (checkFeaturesByBaseBand()) {
            case RESULT_MAYBE_EMULATOR:
                suspectCount += 2;//模拟器基带信息为null的情况概率相当大
                break;
            case RESULT_EMULATOR:
                return true;
        }

        //检测传感器数量
        int sensorNumber = getSensorNumber(context);
        if (sensorNumber <= 7) ++suspectCount;

        //检测已安装第三方应用数量
        int userAppNumber = getUserAppNumber();
        if (userAppNumber <= 5) ++suspectCount;

        //检测是否支持闪光灯
        boolean supportCameraFlash = supportCameraFlash(context);
        if (!supportCameraFlash) ++suspectCount;
        //检测是否支持相机
        boolean supportCamera = supportCamera(context);
        if (!supportCamera) ++suspectCount;
        //检测是否支持蓝牙
        boolean supportBluetooth = supportBluetooth(context);
        if (!supportBluetooth) ++suspectCount;

        //检测光线传感器
        boolean hasLightSensor = hasLightSensor(context);
        if (!hasLightSensor) {
            ++suspectCount;
        }

        //检测进程组信息
        if (checkFeaturesByCgroup() == RESULT_MAYBE_EMULATOR) {
            ++suspectCount;
        }
        //嫌疑值大于3，认为是模拟器
        return suspectCount > 3;
    }

    private static int getUserAppNum(String userApps) {
        if (TextUtils.isEmpty(userApps)) return 0;
        String[] result = userApps.split("package:");
        return result.length;
    }

    private static String getProperty(String propName) {
        String property = CommandUtils.getProperty(propName);
        return TextUtils.isEmpty(property) ? null : property;
    }

    /**
     * 特征参数-硬件名称
     *
     * @return 0表示可能是模拟器，1表示模拟器，2表示可能是真机
     */
    private static int checkFeaturesByHardware() {
        String hardware = getProperty("ro.hardware");
        if (TextUtils.isEmpty(hardware))
            return RESULT_MAYBE_EMULATOR;
        int result;
        String tempValue = hardware.toLowerCase();
        switch (tempValue) {
            case "ttvm"://天天模拟器
            case "nox"://夜神模拟器
            case "cancro"://网易MUMU模拟器
            case "intel"://逍遥模拟器
            case "vbox":
            case "vbox86"://腾讯手游助手
            case "android_x86"://雷电模拟器
                result = RESULT_EMULATOR;
                break;
            default:
                result = RESULT_UNKNOWN;
                break;
        }
        return result;
    }

    /**
     * 特征参数-渠道
     *
     * @return 0表示可能是模拟器，1表示模拟器，2表示可能是真机
     */
    private static int checkFeaturesByFlavor() {
        String flavor = getProperty("ro.build.flavor");
        if (TextUtils.isEmpty(flavor))
            return RESULT_MAYBE_EMULATOR;
        int result;
        String tempValue = flavor.toLowerCase();
        if (tempValue.contains("vbox")) {
            result = RESULT_EMULATOR;
        } else if (tempValue.contains("sdk_gphone")) {
            result = RESULT_EMULATOR;
        } else {
            result = RESULT_UNKNOWN;
        }
        return result;
    }

    /**
     * 特征参数-设备型号
     *
     * @return 0表示可能是模拟器，1表示模拟器，2表示可能是真机
     */
    private static int checkFeaturesByModel() {
        String model = getProperty("ro.product.model");
        if (TextUtils.isEmpty(model)) {
            return RESULT_MAYBE_EMULATOR;
        }
        int result;
        String tempValue = model.toLowerCase();
        if (tempValue.contains("google_sdk")) {
            result = RESULT_EMULATOR;
        } else if (tempValue.contains("emulator")) {
            result = RESULT_EMULATOR;
        } else if (tempValue.contains("android sdk built for x86")) {
            result = RESULT_EMULATOR;
        } else {
            result = RESULT_UNKNOWN;
        }
        return result;
    }

    /**
     * 特征参数-硬件制造商
     *
     * @return 0表示可能是模拟器，1表示模拟器，2表示可能是真机
     */
    private static int checkFeaturesByManufacturer() {
        String manufacturer = getProperty("ro.product.manufacturer");
        if (TextUtils.isEmpty(manufacturer)) {
            return RESULT_MAYBE_EMULATOR;
        }
        int result;
        String tempValue = manufacturer.toLowerCase();
        if (tempValue.contains("genymotion")) {
            result = RESULT_EMULATOR;
        } else if (tempValue.contains("netease")) {
            result = RESULT_EMULATOR;//网易MUMU模拟器
        } else {
            result = RESULT_UNKNOWN;
        }
        return result;
    }

    /**
     * 特征参数-主板名称
     *
     * @return 0表示可能是模拟器，1表示模拟器，2表示可能是真机
     */
    private static int checkFeaturesByBoard() {
        String board = getProperty("ro.product.board");
        if (TextUtils.isEmpty(board)) {
            return RESULT_MAYBE_EMULATOR;
        }
        int result;
        String tempValue = board.toLowerCase();
        if (tempValue.contains("android")) {
            result = RESULT_EMULATOR;
        } else if (tempValue.contains("goldfish")) {
            result = RESULT_EMULATOR;
        } else {
            result = RESULT_UNKNOWN;
        }
        return result;
    }

    /**
     * 特征参数-主板平台
     *
     * @return 0表示可能是模拟器，1表示模拟器，2表示可能是真机
     */
    private static int checkFeaturesByPlatform() {
        String platform = getProperty("ro.board.platform");
        if (TextUtils.isEmpty(platform)) {
            return RESULT_MAYBE_EMULATOR;
        }
        int result;
        String tempValue = platform.toLowerCase();
        if (tempValue.contains("android")) {
            result = RESULT_EMULATOR;
        } else {
            result = RESULT_UNKNOWN;
        }
        return result;
    }

    /**
     * 特征参数-基带信息
     *
     * @return 0表示可能是模拟器，1表示模拟器，2表示可能是真机
     */
    private static int checkFeaturesByBaseBand() {
        String baseBandVersion = getProperty("gsm.version.baseband");
        if (TextUtils.isEmpty(baseBandVersion)) {
            return RESULT_MAYBE_EMULATOR;
        }
        int result;
        if (baseBandVersion.contains("1.0.0.0")) {
            result = RESULT_EMULATOR;
        } else {
            result = RESULT_UNKNOWN;
        }
        return result;
    }

    /**
     * 获取传感器数量
     */
    private static int getSensorNumber(Context context) {
        SensorManager sm = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        return sm.getSensorList(Sensor.TYPE_ALL).size();
    }

    /**
     * 获取已安装第三方应用数量
     */
    private static int getUserAppNumber() {
        String userApps = CommandUtils.exec("pm list package -3");
        return getUserAppNum(userApps);
    }

    /**
     * 是否支持相机
     */
    private static boolean supportCamera(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    /**
     * 是否支持闪光灯
     */
    private static boolean supportCameraFlash(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    /**
     * 是否支持蓝牙
     */
    private static boolean supportBluetooth(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
    }

    /**
     * 判断是否存在光传感器来判断是否为模拟器
     * 部分真机也不存在温度和压力传感器。其余传感器模拟器也存在。
     *
     * @return false为模拟器
     */
    private static boolean hasLightSensor(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT); //光线传感器
        return null != sensor;
    }

    /**
     * 特征参数-进程组信息
     */
    private static int checkFeaturesByCgroup() {
        String filter = CommandUtils.exec("cat /proc/self/cgroup");
        if (TextUtils.isEmpty(filter)) {
            return RESULT_MAYBE_EMULATOR;
        }
        return RESULT_UNKNOWN;
    }
}
