package pa.am.scipioutils_android.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * Class: HttpStreamUtil
 * Description:
 * Author: Alan Min
 * Create Date: 2019/7/25
 */
public class HttpStreamUtil extends AbstractHttpUtil {

    //默认构造方法
    public HttpStreamUtil(){}

    /**
     * 构造方法
     * @param reqHeaderParam 自定义请求头参数
     */
    public HttpStreamUtil (String userAgent, Map<String,String> reqHeaderParam)
    {
        setUserAgent(userAgent);
        setReqHeaderParam(reqHeaderParam);
    }

    /**
     * 发起http请求，获取输入流结果 - 默认不传参
     * @param urlPath 请求的url地址
     * @param requestMethod get请求还是post请求
     */
    public InputStream requestStream(String urlPath, String requestMethod)
            throws NoSuchAlgorithmException, IOException, KeyManagementException
    {
        return (InputStream) doRequest(urlPath,requestMethod, HttpConst.OUTDATA_NONE,null,
                HttpConst.GETDATA_ONLY_STREAM,null);
    }

    /**
     * 发起http请求，获取输入流结果 - 默认传参方式(比如x=1&y=2...)
     * @param urlPath 请求的url地址
     * @param requestMethod get请求还是post请求
     * @param params 请求带的参数
     */
    public InputStream requestStream( String urlPath, String requestMethod,Map<String,String> params)
            throws NoSuchAlgorithmException,IOException,KeyManagementException
    {
        return (InputStream) doRequest(urlPath,requestMethod,HttpConst.OUTDATA_DEFAULT,params,
                HttpConst.GETDATA_ONLY_STREAM,null);
    }

    /**
     * 发起http请求，获取输入流结果 - 指定传参方式
     * @param urlPath 请求的url地址
     * @param requestMethod get请求还是post请求
     * @param setParamMode 传参方式，json还是html
     * @param params 请求带的参数
     */
    public InputStream requestStream( String urlPath, String requestMethod, int setParamMode,String params)
            throws NoSuchAlgorithmException,IOException,KeyManagementException
    {
        if(setParamMode<0 || setParamMode>4)
            throw new RuntimeException("Error setParamMode!!!");
        return (InputStream) doRequest(urlPath,requestMethod,setParamMode,params,
                HttpConst.GETDATA_ONLY_STREAM,null);
    }

    /**
     * 上传文件并获得响应的输入流
     * @param urlPath 上传url
     * @param queryParams 要传递的普通字符串参数(可为null)
     * @param file 要上传的文件
     * @return 响应的输入流
     */
    public InputStream uploadFile(String urlPath, Map<String,String> queryParams , File file)
            throws IOException, KeyManagementException, NoSuchAlgorithmException
    {
        if( !file.exists() || file.isDirectory())
        {
            throw new IOException("file not exists or is a directory");
        }
        return (InputStream) doFileRequest(urlPath,queryParams,file,HttpConst.GETDATA_ONLY_STREAM);
    }

    /**
     * 上传多个文件并获得响应
     * @param urlPath 上传url
     * @param queryParams 要传递的普通字符串参数(可为null)
     * @param files 要上传的文件群
     * @return 响应数据
     */
    public InputStream uploadMultiFiles(String urlPath, Map<String,String> queryParams , Map<String,File> files)
            throws IOException, KeyManagementException, NoSuchAlgorithmException
    {
        for( Map.Entry<String,File> entry : files.entrySet() )
        {
            if( !entry.getValue().exists() || entry.getValue().isDirectory())
            {
                throw new IOException("file not exists or is a directory which key is ["+entry.getKey()+"]");
            }
        }
        return (InputStream) doFileRequest(urlPath,queryParams,files,HttpConst.GETDATA_ONLY_STREAM);
    }

}
