package cn.yyxx.support.permission;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @author #Suyghur.
 * Created on 2021/10/31
 */
public class PermissionKitActivity extends FragmentActivity {
    private FrameLayout layout = null;
    private static List<String> mPermissions = null;
    private static IPermissionCallback mCallback = null;

    public static void start(Context context, List<String> permissions, IPermissionCallback callback) {
        if (mPermissions != null) {
            mPermissions.clear();
            mPermissions = null;
        }
        if (mCallback != null) {
            mCallback = null;
        }
        mPermissions = permissions;
        mCallback = callback;
        context.startActivity(new Intent(context, PermissionKitActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public static void finish(Context context) {
        if (context instanceof PermissionKitActivity) {
            ((PermissionKitActivity) context).finish();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layout = new FrameLayout(this);
        setContentView(layout);
        doRequestPermissions();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPermissions != null) {
            mPermissions.clear();
            mPermissions = null;
        }
        mCallback = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PermissionKit.REQUEST_CODE && !isFinishing()) {
            if (mCallback != null) {
                mCallback.onProxyFinish();
                finish();
            }
        }
    }

    private void doRequestPermissions() {
        if (mPermissions == null || mPermissions.isEmpty()) {
            return;
        }
        if (mCallback == null) {
            return;
        }
        // 权限请求列表（为什么直接不用字段？因为框架要兼容新旧权限，在低版本下会自动添加旧权限申请）
        List<String> permissions = new ArrayList<>(mPermissions);

        // 当前是否为调试模式
        boolean debugMode = (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;

        // 检查当前 Activity 状态是否是正常的，如果不是则不请求权限
        FragmentActivity activity = PermissionUtils.findActivity(this);
        if (!PermissionChecker.checkActivityStatus(activity, debugMode)) {
            return;
        }

        // 必须要传入正常的权限或者权限组才能申请权限
        if (!PermissionChecker.checkPermissionArgument(permissions, debugMode)) {
            return;
        }

        if (debugMode) {
            // 检查申请的存储权限是否符合规范
            PermissionChecker.checkStoragePermission(this, permissions);
            // 检查申请的定位权限是否符合规范
            PermissionChecker.checkLocationPermission(this, permissions);
            // 检查申请的权限和 targetSdk 版本是否能吻合
            PermissionChecker.checkTargetSdkVersion(this, permissions);
        }

        if (debugMode) {
            // 检测权限有没有在清单文件中注册
            PermissionChecker.checkManifestPermissions(this, permissions);
        }

        // 优化所申请的权限列表
        PermissionChecker.optimizeDeprecatedPermission(permissions);

        if (PermissionUtils.isGrantedPermissions(this, permissions)) {
            // 证明这些权限已经全部授予过，直接回调成功
            if (mCallback != null) {
                mCallback.onGranted(mPermissions, true);
                activity.finish();
            }
            return;
        }

        // 申请没有授予过的权限
        PermissionKit.getInterceptor().requestPermissions(activity, permissions, mCallback);
    }
}
