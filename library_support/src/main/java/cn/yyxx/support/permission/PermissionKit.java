package cn.yyxx.support.permission;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.support.v4.app.FragmentActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2018/06/15
 * desc   : Android 危险权限请求类
 */
@SuppressWarnings({"unused", "deprecation"})
public final class PermissionKit {

    /**
     * 权限设置页跳转请求码
     */
    public static final int REQUEST_CODE = 1024 + 1;

    /**
     * 权限请求拦截器
     */
    private static IPermissionInterceptor sInterceptor;

    /**
     * 当前是否为调试模式
     */
    private static Boolean sDebugMode;

    /**
     * 设置请求的对象
     */
    public static PermissionKit with() {
        return new PermissionKit();
    }

    /**
     * 是否为调试模式
     */
    public static void setDebugMode(boolean debug) {
        sDebugMode = debug;
    }

    private static boolean isDebugMode(Context context) {
        if (sDebugMode == null) {
            sDebugMode = (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        }
        return sDebugMode;
    }

    /**
     * 设置全局权限请求拦截器
     */
    public static void setInterceptor(IPermissionInterceptor interceptor) {
        sInterceptor = interceptor;
    }

    /**
     * 获取全局权限请求拦截器
     */
    public static IPermissionInterceptor getInterceptor() {
        if (sInterceptor == null) {
            sInterceptor = new BasePermissionInterceptor() {
            };
        }
        return sInterceptor;
    }


    /**
     * 权限列表
     */
    private List<String> mPermissions;

    /**
     * 权限请求拦截器
     */
    private IPermissionInterceptor mInterceptor;

    /**
     * 私有化构造函数
     */
    private PermissionKit() {

    }

    /**
     * 设置权限请求拦截器
     */
    public PermissionKit interceptor(IPermissionInterceptor interceptor) {
        mInterceptor = interceptor;
        return this;
    }

    /**
     * 添加权限组
     */
    public PermissionKit permission(String... permissions) {
        return permission(PermissionUtils.asArrayList(permissions));
    }

    public PermissionKit permission(String[]... permissions) {
        return permission(PermissionUtils.asArrayLists(permissions));
    }

    public PermissionKit permission(List<String> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return this;
        }
        if (mPermissions == null) {
            mPermissions = new ArrayList<>(permissions);
            return this;
        }

        for (String permission : permissions) {
            if (mPermissions.contains(permission)) {
                continue;
            }
            mPermissions.add(permission);
        }
        return this;
    }

    /**
     * 请求权限
     */
    public void request(Context context, IPermissionCallback callback) {

        if (mInterceptor == null) {
            mInterceptor = getInterceptor();
        }
        PermissionKitActivity.start(context, mPermissions, callback);
    }

    /**
     * 判断一个或多个权限是否全部授予了
     */
    public static boolean isGranted(Context context, String... permissions) {
        return isGranted(context, PermissionUtils.asArrayList(permissions));
    }

    public static boolean isGranted(Context context, String[]... permissions) {
        return isGranted(context, PermissionUtils.asArrayLists(permissions));
    }

    public static boolean isGranted(Context context, List<String> permissions) {
        return PermissionUtils.isGrantedPermissions(context, permissions);
    }

    /**
     * 获取没有授予的权限
     */
    public static List<String> getDenied(Context context, String... permissions) {
        return getDenied(context, PermissionUtils.asArrayList(permissions));
    }

    public static List<String> getDenied(Context context, String[]... permissions) {
        return getDenied(context, PermissionUtils.asArrayLists(permissions));
    }

    public static List<String> getDenied(Context context, List<String> permissions) {
        return PermissionUtils.getDeniedPermissions(context, permissions);
    }

    /**
     * 判断某个权限是否为特殊权限
     */
    public static boolean isSpecial(String permission) {
        return PermissionUtils.isSpecialPermission(permission);
    }

    /**
     * 判断一个或多个权限是否被永久拒绝了（注意不能在请求权限之前调用，应该在 {@link IPermissionCallback#onDenied(List, boolean)} 方法中调用）
     */
    public static boolean isPermanentDenied(Activity activity, String... permissions) {
        return isPermanentDenied(activity, PermissionUtils.asArrayList(permissions));
    }

    public static boolean isPermanentDenied(Activity activity, String[]... permissions) {
        return isPermanentDenied(activity, PermissionUtils.asArrayLists(permissions));
    }

    public static boolean isPermanentDenied(Activity activity, List<String> permissions) {
        return PermissionUtils.isPermissionPermanentDenied(activity, permissions);
    }

    /* android.content.Context */

    public static void startPermissionActivity(Context context) {
        startPermissionActivity(context, (List<String>) null);
    }

    public static void startPermissionActivity(Context context, String... permissions) {
        startPermissionActivity(context, PermissionUtils.asArrayList(permissions));
    }

    public static void startPermissionActivity(Context context, String[]... permissions) {
        startPermissionActivity(context, PermissionUtils.asArrayLists(permissions));
    }

    /**
     * 跳转到应用权限设置页
     *
     * @param permissions 没有授予或者被拒绝的权限组
     */
    public static void startPermissionActivity(Context context, List<String> permissions) {
        Activity activity = PermissionUtils.findActivity(context);
        if (activity != null) {
            startPermissionActivity(activity, permissions);
            return;
        }
        Intent intent = PermissionSettingPage.getSmartPermissionIntent(context, permissions);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    /* android.app.Activity */
    public static void startPermissionActivity(Activity activity) {
        startPermissionActivity(activity, (List<String>) null);
    }

    public static void startPermissionActivity(Activity activity, String... permissions) {
        startPermissionActivity(activity, PermissionUtils.asArrayList(permissions));
    }

    public static void startPermissionActivity(Activity activity, String[]... permissions) {
        startPermissionActivity(activity, PermissionUtils.asArrayLists(permissions));
    }

    public static void startPermissionActivity(Activity activity, List<String> permissions) {
        startPermissionActivity(activity, permissions, REQUEST_CODE);
    }

    /**
     * 跳转到应用权限设置页
     *
     * @param permissions 没有授予或者被拒绝的权限组
     * @param requestCode Activity 跳转请求码
     */
    public static void startPermissionActivity(Activity activity, List<String> permissions, int requestCode) {
        activity.startActivityForResult(PermissionSettingPage.getSmartPermissionIntent(activity, permissions), requestCode);
    }

    /* android.app.Fragment */

    public static void startPermissionActivity(Fragment fragment) {
        startPermissionActivity(fragment, (List<String>) null);
    }

    public static void startPermissionActivity(Fragment fragment, String... permissions) {
        startPermissionActivity(fragment, PermissionUtils.asArrayList(permissions));
    }

    public static void startPermissionActivity(Fragment fragment, String[]... permissions) {
        startPermissionActivity(fragment, PermissionUtils.asArrayLists(permissions));
    }

    public static void startPermissionActivity(Fragment fragment, List<String> permissions) {
        startPermissionActivity(fragment, permissions, REQUEST_CODE);
    }

    /**
     * 跳转到应用权限设置页
     *
     * @param permissions 没有授予或者被拒绝的权限组
     * @param requestCode Activity 跳转请求码
     */
    public static void startPermissionActivity(Fragment fragment, List<String> permissions, int requestCode) {
        Activity activity = fragment.getActivity();
        if (activity == null) {
            return;
        }
        fragment.startActivityForResult(PermissionSettingPage.getSmartPermissionIntent(activity, permissions), requestCode);
    }

    /* android.support.v4.app.Fragment */

    public static void startPermissionActivity(android.support.v4.app.Fragment fragment) {
        startPermissionActivity(fragment, (List<String>) null);
    }

    public static void startPermissionActivity(android.support.v4.app.Fragment fragment, String... permissions) {
        startPermissionActivity(fragment, PermissionUtils.asArrayList(permissions));
    }

    public static void startPermissionActivity(android.support.v4.app.Fragment fragment, String[]... permissions) {
        startPermissionActivity(fragment, PermissionUtils.asArrayLists(permissions));
    }

    public static void startPermissionActivity(android.support.v4.app.Fragment fragment, List<String> permissions) {
        startPermissionActivity(fragment, permissions, REQUEST_CODE);
    }

    /**
     * 跳转到应用权限设置页
     *
     * @param permissions 没有授予或者被拒绝的权限组
     * @param requestCode Activity 跳转请求码
     */
    public static void startPermissionActivity(android.support.v4.app.Fragment fragment, List<String> permissions, int requestCode) {
        Activity activity = fragment.getActivity();
        if (activity == null) {
            return;
        }
        fragment.startActivityForResult(PermissionSettingPage.getSmartPermissionIntent(activity, permissions), requestCode);
    }
}