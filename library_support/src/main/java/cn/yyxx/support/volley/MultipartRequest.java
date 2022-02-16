package cn.yyxx.support.volley;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cn.yyxx.support.hawkeye.LogUtils;
import cn.yyxx.support.volley.entity.FileEntity;
import cn.yyxx.support.volley.source.AuthFailureError;
import cn.yyxx.support.volley.source.NetworkResponse;
import cn.yyxx.support.volley.source.ParseError;
import cn.yyxx.support.volley.source.Request;
import cn.yyxx.support.volley.source.Response;
import cn.yyxx.support.volley.source.toolbox.HttpHeaderParser;

/**
 * @author #Suyghur.
 * Created on 2022/01/11
 */
public class MultipartRequest extends Request<String> {

    private final String MULTIPART_FORM_DATA = "multipart/form-data"; // 数据类型
    private final String BOUNDARY = "---------" + UUID.randomUUID().toString(); // 随机生成边界分隔线
    private final String NEW_LINE = "\r\n"; // 换行符

    private Map<String, Object> mParams;
    private List<FileEntity> mFileEntityList;
    private FileEntity mFileEntity;
    private Response.Listener<String> mListener;
    private Charset mCharSet;

    private boolean isSingleFile = false;

    public MultipartRequest(String url, Map<String, Object> params, Charset charSet, List<FileEntity> fileEntityList, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        this.mParams = params;
        this.mCharSet = charSet;
        this.mFileEntityList = fileEntityList;
        this.mListener = listener;
        this.isSingleFile = false;
    }

    public MultipartRequest(String url, Map<String, Object> params, Charset charSet, FileEntity fileEntity, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        this.mParams = params;
        this.mCharSet = charSet;
        this.mFileEntity = fileEntity;
        this.mListener = listener;
        this.isSingleFile = true;
    }

    public MultipartRequest(String url, Map<String, Object> params, List<FileEntity> fileEntityList, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        this(url, params, Charset.defaultCharset(), fileEntityList, listener, errorListener);
    }

    public MultipartRequest(String url, Map<String, Object> params, FileEntity fileEntity, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        this(url, params, Charset.defaultCharset(), fileEntity, listener, errorListener);
    }


    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            return Response.success(new String(response.data, HttpHeaderParser.parseCharset(response.headers)), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        if (isSingleFile) { // 单文件上传
            return singleFileUp();
        } else { // 多个文件上传
            return multipleFileUp();
        }
    }

    /**
     * 多个文件上传
     */
    private byte[] multipleFileUp() throws AuthFailureError {
        if ((mParams == null || mParams.size() <= 0) && (mFileEntityList == null || mFileEntityList.size() <= 0)) {
            // 没有参数也没有文件
            return super.getBody();
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            if (mParams != null && mParams.size() > 0) {
                // 有参数，先拼接参数
                paramsFormat(bos);
            }
            if (mFileEntityList != null && mFileEntityList.size() > 0) {
                // 有文件，提交文件
                for (FileEntity fileEntity : mFileEntityList) {
                    fileFormat(bos, fileEntity);
                }
            }
            // 所有参数拼接完成，拼接结束行
            endLine(bos);
            return bos.toByteArray();
        }
    }

    /**
     * 单个文件上传
     */
    private byte[] singleFileUp() throws AuthFailureError {
        if ((mParams == null || mParams.size() <= 0) && (mFileEntity == null)) {
            // 没有参数也没有文件
            return super.getBody();
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            if (mParams != null && mParams.size() > 0) {
                // 有参数，先拼接参数
                paramsFormat(bos);
            }
            if (mFileEntity != null) {
                // 有文件，提交文件
                fileFormat(bos, mFileEntity);
            }
            // 所有参数拼接完成，拼接结束行
            endLine(bos);
            return bos.toByteArray();
        }
    }

    /**
     * 结束行内容
     */
    private void endLine(ByteArrayOutputStream bos) {
        String endLine = "--" + BOUNDARY + "--" + NEW_LINE;
        try {
            bos.write(endLine.getBytes(mCharSet));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 格式化上传文件格式
     */
    private void fileFormat(ByteArrayOutputStream bos, FileEntity fileEntity) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("--").append(BOUNDARY).append(NEW_LINE);
        stringBuilder.append("Content-Disposition: form-data; name=\"").append(fileEntity.mName).append("\"").append(";filename=\"").append(fileEntity.mFileName).append("\"").append(NEW_LINE);
        stringBuilder.append("Content-Type: ").append(fileEntity.mMime).append(";charset=").append(mCharSet).append(NEW_LINE);
        stringBuilder.append(NEW_LINE);
        try {
            bos.write(stringBuilder.toString().getBytes(mCharSet));
            bos.write(fileEntity.getFileBytes());
            bos.write(NEW_LINE.getBytes(mCharSet));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 格式化上传参数格式
     */
    private void paramsFormat(ByteArrayOutputStream bos) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String key : mParams.keySet()) {
            Object value = mParams.get(key);
            stringBuilder.append("--").append(BOUNDARY).append(NEW_LINE);
            stringBuilder.append("Content-Disposition: form-data; name=\"").append(key).append("\"").append(NEW_LINE);
            stringBuilder.append(NEW_LINE);
            stringBuilder.append(value).append(NEW_LINE);
        }
        LogUtils.d(stringBuilder.toString());
        try {
            bos.write(stringBuilder.toString().getBytes(mCharSet));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 返回头信息，用于指定上传的内容类型
     */
    @Override
    public String getBodyContentType() {
        // 如果参数和文件都为null时，不会执行这个方法
        // Content-Type: multipart/form-data; boundary=----WebKitFormBoundaryS4nmHw9nb2Eeusll
        return MULTIPART_FORM_DATA + ";boundary=" + BOUNDARY;
    }
}
