package cn.yyxx.support.msa;

import android.content.Context;

import com.bun.miitmdid.core.ErrorCode;
import com.bun.miitmdid.core.MdidSdkHelper;
import com.bun.miitmdid.interfaces.IIdentifierListener;
import com.bun.miitmdid.interfaces.IdSupplier;

import java.util.HashMap;
import java.util.Map;

import cn.yyxx.support.hawkeye.LogUtils;

/**
 * @author #Suyghur.
 * Created on 2021/04/23
 */
public class MsaDeviceIdsHandler {

    private static final String VERSION = "1.0.23";

    public static String oaid = "";
    public static String vaid = "";
    public static String aaid = "";

    public static void initMsaDeviceIds(Context context, final IMsaDeviceIdsCallback callback) {
        LogUtils.i("attach msa sdk , version : " + VERSION);
        int code = MdidSdkHelper.InitSdk(context, true, new IIdentifierListener() {
            @Override
            public void OnSupport(boolean isSupport, IdSupplier idSupplier) {
                if (isSupport) {
                    Map<String, String> ids = new HashMap<>();
                    oaid = idSupplier.getOAID();
                    vaid = idSupplier.getVAID();
                    aaid = idSupplier.getAAID();
                    ids.put("oaid", oaid);
                    ids.put("vaid", vaid);
                    ids.put("aaid", aaid);
                    callback.onIdsRead(0, "获取MsaIds成功", ids);
                } else {
                    callback.onIdsRead(-1, "不支持的设备", null);
                }
            }
        });

        switch (code) {
            case ErrorCode.INIT_ERROR_DEVICE_NOSUPPORT:
                callback.onIdsRead(-1, "不支持的设备", null);
                break;
            case ErrorCode.INIT_ERROR_LOAD_CONFIGFILE:
                callback.onIdsRead(-1, "加载配置文件出错", null);
                break;
            case ErrorCode.INIT_ERROR_MANUFACTURER_NOSUPPORT:
                callback.onIdsRead(-1, "不支持的设备厂商", null);
                break;
            case ErrorCode.INIT_ERROR_RESULT_DELAY:
                LogUtils.e("initMsaDeviceIds : 获取接口是异步的，结果会在回调中返回，回调执行的回调可能在工作线程");
                break;
            case ErrorCode.INIT_HELPER_CALL_ERROR:
                callback.onIdsRead(-1, "反射调用出错", null);
                break;
        }
    }
}
