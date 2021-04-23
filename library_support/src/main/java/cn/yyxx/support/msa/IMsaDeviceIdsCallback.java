package cn.yyxx.support.msa;

import java.util.Map;

/**
 * @author #Suyghur.
 * Created on 2021/04/23
 */
public interface IMsaDeviceIdsCallback {

    void onIdsRead(int code, String msg, Map<String, String> ids);
}
