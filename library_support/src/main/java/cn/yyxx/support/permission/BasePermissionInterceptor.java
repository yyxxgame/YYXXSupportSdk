package cn.yyxx.support.permission;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * @author #Suyghur.
 * Created on 2021/10/31
 */
public class BasePermissionInterceptor implements IPermissionInterceptor {
    @Override
    public void requestPermissions(FragmentActivity activity, List<String> permissions, IPermissionCallback callback) {
        PermissionKitFragment.beginRequest(activity, new ArrayList<>(permissions), callback);

    }

    @Override
    public void grantedPermissions(FragmentActivity activity, List<String> permissions, boolean all, IPermissionCallback callback) {
        callback.onGranted(permissions, all);
        if (all) {
            PermissionKitActivity.finish(activity);
        }
    }

    @Override
    public void deniedPermissions(FragmentActivity activity, List<String> permissions, boolean never, IPermissionCallback callback) {
        if (never) {
            showPermissionDialog(activity, permissions);
            return;
        }
        if (permissions.size() == 1 && Permission.ACCESS_BACKGROUND_LOCATION.equals(permissions.get(0))) {
            Toast.makeText(activity, "没有授予后台定位权限，请您选择\"始终允许\"", Toast.LENGTH_SHORT).show();
            PermissionKitActivity.finish(activity);
            return;
        }
        Toast.makeText(activity, "授权失败，请正确授予权限", Toast.LENGTH_SHORT).show();
        PermissionKitActivity.finish(activity);
    }

    /**
     * 显示授权对话框
     */
    protected void showPermissionDialog(FragmentActivity activity, List<String> permissions) {
        // 这里的 Dialog 只是示例，没有用 DialogFragment 来处理 Dialog 生命周期
        new AlertDialog.Builder(activity)
                .setTitle("授权提醒")
                .setCancelable(false)
                .setMessage(getPermissionHint(permissions))
                .setPositiveButton("前往授权", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        PermissionKit.startPermissionActivity(activity, permissions);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        PermissionKitActivity.finish(activity);
                    }
                })
                .show();
    }

    private String getPermissionHint(List<String> permissions) {
        if (permissions == null || permissions.size() == 0) {
            return "获取权限失败，请手动授予权限";
        }
        ArrayList<String> hints = new ArrayList<>();
        String hint = "";
        for (String permission : permissions) {
            switch (permission) {
                case Permission.READ_EXTERNAL_STORAGE:
                case Permission.WRITE_EXTERNAL_STORAGE:
                case Permission.MANAGE_EXTERNAL_STORAGE:
                    hint = "存储权限";
                    if (!hints.contains(hint)) {
                        hints.add(hint);
                    }
                    break;
                case Permission.CAMERA:
                    hint = "相机权限";
                    if (!hints.contains(hint)) {
                        hints.add(hint);
                    }
                    break;
                case Permission.RECORD_AUDIO: {
                    hint = "麦克风权限";
                    if (!hints.contains(hint)) {
                        hints.add(hint);
                    }
                }
                break;
                case Permission.ACCESS_FINE_LOCATION:
                case Permission.ACCESS_COARSE_LOCATION:
                case Permission.ACCESS_BACKGROUND_LOCATION:
                    if (!permissions.contains(Permission.ACCESS_FINE_LOCATION) && !permissions.contains(Permission.ACCESS_COARSE_LOCATION)) {
                        hint = "后台定位权限";
                    } else {
                        hint = "定位权限";
                    }
                    if (!hints.contains(hint)) {
                        hints.add(hint);
                    }
                    break;
                case Permission.READ_PHONE_STATE:
                case Permission.CALL_PHONE:
                case Permission.ADD_VOICEMAIL:
                case Permission.USE_SIP:
                case Permission.READ_PHONE_NUMBERS:
                case Permission.ANSWER_PHONE_CALLS:
                    hint = "电话权限";
                    if (!hints.contains(hint)) {
                        hints.add(hint);
                    }
                    break;
                case Permission.GET_ACCOUNTS:
                case Permission.READ_CONTACTS:
                case Permission.WRITE_CONTACTS:
                    hint = "通讯录权限";
                    if (!hints.contains(hint)) {
                        hints.add(hint);
                    }
                    break;
                case Permission.READ_CALENDAR:
                case  Permission.WRITE_CALENDAR:
                    hint = "日历权限";
                    if (!hints.contains(hint)) {
                        hints.add(hint);
                    }
                    break;
                case Permission.READ_CALL_LOG:
                case Permission.WRITE_CALL_LOG:
                case Permission.PROCESS_OUTGOING_CALLS:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        hint = "通话记录权限";
                    } else {
                        hint = "电话权限";
                    }
                    if (!hints.contains(hint)) {
                        hints.add(hint);
                    }
                    break;
                case Permission.BODY_SENSORS:
                    hint = "身体传感器权限";
                    if (!hints.contains(hint)) {
                        hints.add(hint);
                    }
                    break;
                case Permission.ACTIVITY_RECOGNITION:
                    hint = "健身运动权限";
                    if (!hints.contains(hint)) {
                        hints.add(hint);
                    }
                    break;
                case Permission.SEND_SMS:
                case Permission.RECEIVE_SMS:
                case Permission.READ_SMS:
                case Permission.RECEIVE_WAP_PUSH:
                case Permission.RECEIVE_MMS:
                    hint = "短信权限";
                    if (!hints.contains(hint)) {
                        hints.add(hint);
                    }
                    break;
                case Permission.REQUEST_INSTALL_PACKAGES:
                    hint = "安装应用权限";
                    if (!hints.contains(hint)) {
                        hints.add(hint);
                    }
                    break;
                case Permission.NOTIFICATION_SERVICE:
                    hint = "通知栏权限";
                    if (!hints.contains(hint)) {
                        hints.add(hint);
                    }
                    break;
                case Permission.SYSTEM_ALERT_WINDOW:
                    hint = "悬浮窗权限";
                    if (!hints.contains(hint)) {
                        hints.add(hint);
                    }
                    break;
                case Permission.WRITE_SETTINGS:
                    hint = "系统设置权限";
                    if (!hints.contains(hint)) {
                        hints.add(hint);
                    }
                    break;
            }
        }
        if (hints.size() != 0) {
            StringBuilder builder = new StringBuilder();
            for (String text : hints) {
                if (builder.length() == 0) {
                    builder.append(text);
                } else {
                    builder.append("、").append(text);
                }
            }
            builder.append(" ");
            return "获取权限失败，请手动授予" + builder.toString();
        }
        return "获取权限失败，请手动授予";
    }
}
