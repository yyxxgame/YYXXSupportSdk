package cn.yyxx.support;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author #Suyghur.
 * Created on 2020/7/30
 */
public class JsonUtils {

    private JsonUtils() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static boolean hasJsonKey(JSONObject jsonObject, String key) throws JSONException {
        return jsonObject.has(key) && !jsonObject.getString(key).equals("[]")&&!jsonObject.getString(key).equals("{}");
//        return jsonObject.has(key) && !jsonObject.getString(key).equals("[]");
    }
}
