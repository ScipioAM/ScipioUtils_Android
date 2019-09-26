package pa.am.scipioutils_android.http;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * Class: HttpUtil
 * Description:
 * Author: Alan Min
 * Create Date: 2019/7/25
 */
public class HttpUtil extends AbstractHttpUtil {

    //默认构造方法
    public HttpUtil(){}

    /**
     * 构造方法
     * @param reqHeaderParam 自定义请求头参数
     */
    public HttpUtil (String userAgent, Map<String,String> reqHeaderParam)
    {
        setUserAgent(userAgent);
        setReqHeaderParam(reqHeaderParam);
    }

    /**
     * 发起http请求，返回响应码（不传参）
     * @param url 请求的url
     * @param requestMethod get请求还是post请求
     * @return http响应码
     */
    public Integer requestHttp(String url,String requestMethod)
            throws NoSuchAlgorithmException, IOException, KeyManagementException
    {
        return (Integer) doRequest(url, requestMethod, HttpConst.OUTDATA_NONE,
                null, HttpConst.GETDATA_NONE,null);
    }

    /**
     * 发起http请求，返回响应码（传参）
     * @param url 请求的url
     * @param requestMethod get请求还是post请求
     * @param params 请求带的参数
     * @return http响应码
     */
    public Integer requestHttp(String url,String requestMethod,Map<String,String> params)
            throws NoSuchAlgorithmException, IOException, KeyManagementException
    {
        return (Integer) doRequest(url, requestMethod, HttpConst.OUTDATA_DEFAULT,
                params, HttpConst.GETDATA_NONE,null);
    }

    /**
     * 发起http请求，获取字符串结果 - 默认不传参
     * @param urlPath url路径
     * @param requestMethod get请求还是post请求
     */
    public String requestString( String urlPath,   String requestMethod)
            throws NoSuchAlgorithmException, IOException, KeyManagementException
    {
        return (String) doRequest( urlPath,requestMethod );
    }

    /**
     * 发起http请求，获取字符串结果 - 默认传参方式(比如x=1&y=2...)
     * @param urlPath 请求的url地址
     * @param requestMethod get请求还是post请求
     * @param params 请求带的参数
     */
    public String requestString(  String urlPath,   String requestMethod, Map<String,String> params)
            throws NoSuchAlgorithmException,IOException,KeyManagementException
    {
        return (String) doRequest(urlPath,requestMethod,params);
    }

    /**
     * 发起http请求，获取字符串结果 - 指定传参方式
     * @param urlPath 请求的url地址
     * @param requestMethod get请求还是post请求
     * @param setParamMode 传参方式，json还是html
     * @param params 请求带的参数
     */
    public String requestString(  String urlPath,   String requestMethod,  int setParamMode,String params)
            throws NoSuchAlgorithmException,IOException,KeyManagementException
    {
        if(setParamMode<0 || setParamMode>4)
            throw new RuntimeException("Error request parameters mode!!!");
        return (String) doRequest(urlPath,requestMethod,setParamMode,
                params, HttpConst.GETDATA_DEFAULT,null);
    }

    /**
     * 发起请求，获取返回数据和response中所有header - 默认不传参
     * @param urlPath 请求的url地址
     * @param requestMethod get请求还是post请求
     * 注：header的key就是map的key
     */
    @SuppressWarnings("unchecked")
    public Map<String,Object> request_allHeaders(  String urlPath, String requestMethod)
            throws NoSuchAlgorithmException,IOException,KeyManagementException
    {
        return (Map<String, Object>) doRequest(urlPath,requestMethod,HttpConst.OUTDATA_NONE,
                null,HttpConst.GETDATA_PLUS_ALL_HEADER ,null);
    }

    /**
     * 发起请求，获取返回数据和response中所有header
     * @param urlPath 请求的url地址
     * @param requestMethod get请求还是post请求
     * @param setParamMode 传参方式，json还是html
     * @param params 请求带的参数
     * 注：header的key就是map的key
     */
    @SuppressWarnings("unchecked")
    public Map<String,Object> request_allHeaders( String urlPath, String requestMethod, int setParamMode, Object params)
            throws NoSuchAlgorithmException,IOException,KeyManagementException
    {
        if(setParamMode<0 || setParamMode>4)
            throw new RuntimeException("Error request parameters mode!!!");
        return (Map<String, Object>) doRequest(urlPath,requestMethod,setParamMode,
                params,HttpConst.GETDATA_PLUS_ALL_HEADER ,null);
    }

    /**
     * 发起请求，获取返回数据和response中指定header的值 - 默认不传参
     * @param urlPath 请求的url地址
     * @param requestMethod get请求还是post请求
     */
    @SuppressWarnings("unchecked")
    public Map<String,Object> request_oneHeader(  String urlPath, String requestMethod, String oneHeaderKey )
            throws NoSuchAlgorithmException,IOException,KeyManagementException
    {
        return (Map<String, Object>) doRequest(urlPath,requestMethod,HttpConst.OUTDATA_NONE,
                null,HttpConst.GETDATA_PLUS_ONE_HEADER,oneHeaderKey);
    }

    /**
     * 发起请求，获取返回数据和response中指定header的值
     * @param urlPath 请求的url地址
     * @param requestMethod get请求还是post请求
     * @param oneHeaderKey 指定header的key
     * @param setParamMode 传参方式，json还是html
     * @param params 请求带的参数
     */
    @SuppressWarnings("unchecked")
    public Map<String,Object> request_oneHeader(  String urlPath, String requestMethod, String oneHeaderKey,
                                                 int setParamMode , Object params)
            throws NoSuchAlgorithmException,IOException,KeyManagementException
    {
        if(setParamMode<0 || setParamMode>4)
            throw new RuntimeException("Error request parameters mode!!!");
        return (Map<String, Object>) doRequest(urlPath,requestMethod,setParamMode ,
                params,HttpConst.GETDATA_PLUS_ONE_HEADER,oneHeaderKey);
    }

    /**
     * 发起请求，并直接获取URLConnection对象 - 默认不传参
     * @param urlPath 请求的url地址
     * @param requestMethod get请求还是post请求
     */
    public URLConnection requestForConnObj(  String urlPath, String requestMethod)
            throws NoSuchAlgorithmException,IOException,KeyManagementException
    {
        return (URLConnection) doRequest(urlPath,requestMethod,HttpConst.OUTDATA_NONE,
                null,HttpConst.GETDATA_ONLY_CONN_OBJ ,null);
    }

    /**
     * 发起请求，并直接获取URLConnection对象
     * @param urlPath 请求的url地址
     * @param requestMethod get请求还是post请求
     * @param setParamMode 传参方式，json还是html
     * @param params 请求带的参数
     */
    public URLConnection requestForConnObj( String urlPath, String requestMethod, int setParamMode, Object params)
            throws NoSuchAlgorithmException,IOException,KeyManagementException
    {
        if(setParamMode<0 || setParamMode>4)
            throw new RuntimeException("Error request parameters mode!!!");
        return (URLConnection) doRequest(urlPath,requestMethod,setParamMode,
                params, HttpConst.GETDATA_ONLY_CONN_OBJ ,null);
    }

    /**
     * 上传文件并返回响应码
     * @param urlPath 上传url
     * @param queryParams 要传递的普通字符串参数(可为null)
     * @param file 要上传的文件
     * @return 响应码
     */
    public Integer uploadFile_respCode(String urlPath, Map<String,String> queryParams , File file )
            throws IOException, KeyManagementException, NoSuchAlgorithmException
    {
        if( !file.exists() || file.isDirectory())
        {
            throw new IOException("file not exists or is a directory");
        }
        return (Integer) doFileRequest(urlPath,queryParams,file,HttpConst.GETDATA_NONE);
    }

    /**
     * 上传文件并获得响应
     * @param urlPath 上传url
     * @param queryParams 要传递的普通字符串参数(可为null)
     * @param file 要上传的文件
     * @return 响应数据
     */
    public URLConnection uploadFileForConnObj(String urlPath, Map<String,String> queryParams , File file)
            throws IOException, KeyManagementException, NoSuchAlgorithmException
    {
        if( !file.exists() || file.isDirectory())
        {
            throw new IOException("file not exists or is a directory");
        }
        return (URLConnection) doFileRequest(urlPath,queryParams,file,HttpConst.GETDATA_ONLY_CONN_OBJ);
    }

    /**
     * 上传文件并获得响应体的内容
     * @param urlPath 上传url
     * @param queryParams 要传递的普通字符串参数(可为null)
     * @param file 要上传的文件
     * @return 响应体里的数据
     */
    public String uploadFile(String urlPath,Map<String,String> queryParams ,File file)
            throws NoSuchAlgorithmException, IOException, KeyManagementException
    {
        if( !file.exists() || file.isDirectory())
        {
            throw new IOException("file not exists or is a directory");
        }
        return (String) doFileRequest(urlPath,queryParams,file,HttpConst.GETDATA_DEFAULT);
    }

    /**
     * 上传多个文件并返回响应码
     * @param urlPath 上传url
     * @param queryParams 要传递的普通字符串参数(可为null)
     * @param files 要上传的文件群
     * @return 响应码
     */
    public Integer uploadMultiFiles_respCode(String urlPath, Map<String,String> queryParams , Map<String,File> files)
            throws IOException, KeyManagementException, NoSuchAlgorithmException
    {
        for( Map.Entry<String,File> entry : files.entrySet() )
        {
            if( !entry.getValue().exists() || entry.getValue().isDirectory())
            {
                throw new IOException("file not exists or is a directory which key is ["+entry.getKey()+"]");
            }
        }
        return (Integer) doFileRequest(urlPath,queryParams,files,HttpConst.GETDATA_NONE);
    }

    /**
     * 上传多个文件并返回Http连接对象
     * @param urlPath 上传url
     * @param queryParams 要传递的普通字符串参数(可为null)
     * @param files 要上传的文件群
     * @return 响应码
     */
    public URLConnection uploadMultiFilesForConnObj(String urlPath, Map<String,String> queryParams , Map<String,File> files)
            throws IOException, KeyManagementException, NoSuchAlgorithmException
    {
        for( Map.Entry<String,File> entry : files.entrySet() )
        {
            if( !entry.getValue().exists() || entry.getValue().isDirectory())
            {
                throw new IOException("file not exists or is a directory which key is ["+entry.getKey()+"]");
            }
        }
        return (URLConnection) doFileRequest(urlPath,queryParams,files,HttpConst.GETDATA_ONLY_CONN_OBJ);
    }

    /**
     * 上传多个文件并获得响应体的内容
     * @param urlPath 上传url
     * @param queryParams 要传递的普通字符串参数(可为null)
     * @param files 要上传的文件
     * @return 响应体里的数据
     */
    public String uploadMultiFiles(String urlPath,Map<String,String> queryParams ,Map<String,File> files)
            throws NoSuchAlgorithmException, IOException, KeyManagementException
    {
        for( Map.Entry<String,File> entry : files.entrySet() )
        {
            if( !entry.getValue().exists() || entry.getValue().isDirectory())
            {
                throw new IOException("file not exists or is a directory which key is ["+entry.getKey()+"]");
            }
        }
        return (String) doFileRequest(urlPath,queryParams,files,HttpConst.GETDATA_DEFAULT);
    }

    /**
     * 设置对响应结果的测试处理
     */
    public void enableHandleResponseForTest()
    {
        setResponseListener(new ResponseListener() {
            //成功时的处理
            @Override
            public void onSuccess(int responseCode, URLConnection conn) {
                System.out.println("=============== http request success:"+responseCode+" ===============");
            }
            //失败时的处理
            @Override
            public void onFailure(int responseCode, URLConnection conn) {
                switch (responseCode)
                {
                    case 400:
                        System.out.println("Error:400 bad request");
                    case 404:
                        System.out.println("Error:404 not found");
                    case 500:
                        System.out.println("Error:500 Server error");
                    case 503:
                        System.out.println("Error:503 Server unavailable");
                    default:
                        System.out.println("Error:response code "+responseCode);
                }
            }
            //异常时的处理
            @Override
            public void onError(IOException e, URLConnection conn) {
                e.printStackTrace();
            }
        });
    }

}
