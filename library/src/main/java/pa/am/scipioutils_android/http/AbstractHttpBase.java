package pa.am.scipioutils_android.http;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import pa.am.scipioutils_android.parser.StreamParser;

/**
 * Class: AbstractHttpBase
 * Description:
 * Author: Alan Min
 * Create Date: 2019/9/3
 */
public abstract class AbstractHttpBase {

    protected String charset="UTF-8";//编码字符集，默认UTF-8

    protected String userAgent=null;

    protected Map<String,String> reqHeaderParam=null;//自定义请求头参数

    protected Proxy proxy=null;//代理

    protected boolean isFollowRedirects=false;//是否关闭重定向以获取跳转后的真实地址,默认false

    protected Integer uploadFileBufferSize;//上传文件时的缓冲区大小

    protected ResponseListener responseListener;//响应监听器（响应时的回调）

    protected UploadListener uploadListener;//上传监听器

    //----------------------------------------------------------------------------------------------

    /**
     * 发起http请求并获取返回的数据(不包括输出文件)
     * @param urlPath url路径
     * @param requestMethod get请求还是post请求
     * @param paramsMode 发起请求时传参的方式，默认(比如x=1&y=2...)
     * @param outputParam 发起请求时传的参（可选）
     * @param getDataMode 获取数据的模式，默认只获取响应体的数据
     * @param oneHeaderKey 获取响应的同属获取一个指定响应头参数（可选）
     * @return 响应体的内容，或者响应体+响应头，或者响应体+URLConnection对象
     */
    protected abstract Object doRequest(  String urlPath,   String requestMethod
            ,   int paramsMode, Object outputParam
            ,   int getDataMode, String oneHeaderKey)
            throws NoSuchAlgorithmException,IOException, KeyManagementException;

    /**
     * 发起http请求，输出文件流（可能还包括其他参数），获取返回的数据
     * @param urlPath 请求的url
     * @param outputParams 请求的字符串参数
     * @param outputFiles 输出的文件参数
     * @param getDataMode 获取响应数据的模式
     * @return 响应体的内容，或者响应体+URLConnection对象
     */
    protected abstract Object doFileRequest( String urlPath, Map<String,String> outputParams,  Object outputFiles , int getDataMode )
            throws IOException, KeyManagementException, NoSuchAlgorithmException;

    //----------------------------------------------------------------------------------------------

    /**
     * 获取当前设置的编码字符集
     */
    public String getCharset() {
        return charset;
    }

    /**
     * 设置请求头的编码字符集
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }

    /**
     *  关闭代理
     * @param isGlobalSet 是否全局设置
     */
    public void setProxyTurnOff(boolean isGlobalSet)
    {
        if(isGlobalSet)
        {
            System.setProperty("http.proxySet", "false");
            System.getProperties().remove("http.proxyHost");
            System.getProperties().remove("http.proxyPort");
            System.getProperties().remove("https.proxyHost");
            System.getProperties().remove("https.proxyPort");
        }
        else
            proxy=null;
    }

    /**
     * 打开代理
     * @param isGlobalSet 是否全局设置
     * @param host 代理服务器地址
     * @param port 代理服务器端口
     */
    public void setProxyTurnOn(boolean isGlobalSet,String host,String port)
    {
        if(isGlobalSet)
        {
            System.setProperty("http.proxySet", "true");
            System.setProperty("http.proxyHost", host);
            System.setProperty("http.proxyPort", port);
            System.setProperty("https.proxyHost", host);
            System.setProperty("https.proxyPort", port);
        }
        else
        {
            SocketAddress sa=new InetSocketAddress(host,Integer.valueOf(port));
            proxy=new Proxy(Proxy.Type.HTTP,sa);
        }
    }

    /**
     * 用fiddler监听java的请求需要设置
     */
    public void setFiddlerProxy()
    {
        System.out.println("============ Set default Fiddler setting for java application ============");
        System.setProperty("proxyPort", "8888");
        System.setProperty("proxyHost", "127.0.0.1");
        System.setProperty("proxySet", "true");
    }

    /**
     * 设置userAgent
     */
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    /**
     * 设置默认userAgent
     */
    public void setDefaultUserAgent() {
        this.userAgent = HttpConst.UA_CHROME66_MAC;
    }

    /**
     * 获取当前设置的userAgent
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * 清空设置过的userAgent
     */
    public void clearUserAgent()
    {
        this.userAgent=null;
    }

    /**
     * 获取自定义请求头参数
     */
    public Map<String, String> getReqHeaderParam() {
        return reqHeaderParam;
    }

    /**
     * 设置自定义请求头参数
     */
    public void setReqHeaderParam(Map<String, String> reqHeaderParam) {
        if(this.reqHeaderParam==null)
            this.reqHeaderParam = reqHeaderParam;
        else
        {
            for (Map.Entry<String,String> entry : reqHeaderParam.entrySet())
                this.reqHeaderParam.put(entry.getKey(),entry.getValue());
        }
    }

    /**
     * 清除自定义请求头参数
     */
    public void clearReqHeaderParam()
    {
        this.reqHeaderParam=null;
    }

    /**
     * 检查当前设置的followRedirects参数情况
     * 参数含义：是否关闭重定向以获取跳转后的真实地址
     */
    public boolean isFollowRedirects() {
        return isFollowRedirects;
    }

    /**
     * 设置followRedirects参数
     * 参数含义：是否关闭重定向以获取跳转后的真实地址
     */
    public void setFollowRedirects(boolean followRedirects) {
        isFollowRedirects = followRedirects;
    }

    /**
     * 清除响应监听器
     */
    public void clearResponseListener() {
        responseListener=null;
    }

    /**
     * 设置响应监听器
     */
    public void setResponseListener(ResponseListener responseListener) {
        this.responseListener = responseListener;
    }

    /**
     * 清除上传监听器
     */
    public void clearUploadListener() {
        uploadListener=null;
    }

    /**
     * 设置上传监听器
     */
    public void setUploadListener(UploadListener uploadListener) {
        this.uploadListener = uploadListener;
    }

    /**
     * 获取当前设置的 上传文件缓冲区大小
     */
    public Integer getUploadFileBufferSize() {
        return uploadFileBufferSize;
    }

    /**
     * 设置上传文件缓冲区大小
     */
    public void setUploadFileBufferSize(Integer uploadFileBufferSize) {
        this.uploadFileBufferSize = uploadFileBufferSize;
    }

    //----------------------------------------------------------------------------------------------

    /**
     * 设置http和https连接共通的部分
     * @param conn http或https连接对象
     * @param contentType 请求头的contentType
     */
    protected void setCommonConnectionData(URLConnection conn, String contentType)
            throws IOException {
        //设置共通的头信息，以及输出请求参数
        conn.setConnectTimeout(1000 * 10);//设置连接超时的时间(毫秒)
        conn.setReadTimeout(1000 * 60);
        conn.setDoInput(true);//设置连接打开输入流
        conn.setDoOutput(true);//设置连接打开输出
        conn.setRequestProperty("Accept-Encoding", "gzip,deflate");//告诉服务器支持gzip压缩
        conn.setRequestProperty("Accept-Charset", charset);
        conn.setRequestProperty("Charset", charset);
        //设置contentType
        if(contentType!=null)
            conn.setRequestProperty("Content-Type", contentType);//可被后面的headerParam覆盖
        //设置user-agent，默认user-agent是Mac系统上66版的chrome浏览器
        if(userAgent!=null && (!userAgent.equals("")) )
            conn.setRequestProperty("User-Agent",userAgent);
        //设置自定义头部参数
        if(reqHeaderParam!=null)
        {
            for(Map.Entry<String,String> entry: reqHeaderParam.entrySet())
                conn.setRequestProperty(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 依据返回的状态码进行处理
     * @param conn 连接对象
     * @param responseCode 响应码
     * @param getDataMode 获取响应数据的模式
     * @param oneHeaderKey 获取响应的同属获取一个指定响应头参数（可选）
     * @param responseListener 响应监听器（响应时的回调）
     */
    protected Object handleResponse(URLConnection conn, Integer responseCode,
                                  int getDataMode, String oneHeaderKey,ResponseListener responseListener)
            throws IOException
    {
        Object result;
        try {
            switch(responseCode)
            {
                //------------------ 成功返回的处理 ------------------
                case HttpsURLConnection.HTTP_OK://状态码：200
                    result=handleSuccess(responseCode,conn,getDataMode,oneHeaderKey);
                    break;
                //------------------ 成功返回的处理 ------------------
                case HttpsURLConnection.HTTP_BAD_REQUEST://状态码：400
                    result="400";
                    //失败后的响应回调
                    if(responseListener!=null)
                        responseListener.onFailure(responseCode,conn);
                    break;
                case HttpsURLConnection.HTTP_FORBIDDEN://状态码：403
                    result="403";
                    //失败后的响应回调
                    if(responseListener!=null)
                        responseListener.onFailure(responseCode,conn);
                    break;
                case HttpsURLConnection.HTTP_NOT_FOUND://状态码：404
                    result="404";
                    //失败后的响应回调
                    if(responseListener!=null)
                        responseListener.onFailure(responseCode,conn);
                    break;
                case HttpsURLConnection.HTTP_INTERNAL_ERROR://状态码：500
                    result="500";
                    //失败后的响应回调
                    if(responseListener!=null)
                        responseListener.onFailure(responseCode,conn);
                    break;
                case HttpsURLConnection.HTTP_BAD_GATEWAY://状态码：502
                    result="502";
                    //失败后的响应回调
                    if(responseListener!=null)
                        responseListener.onFailure(responseCode,conn);
                    break;
                case HttpsURLConnection.HTTP_UNAVAILABLE://状态码：503
                    result="503";
                    //失败后的响应回调
                    if(responseListener!=null)
                        responseListener.onFailure(responseCode,conn);
                    break;
                default :
                    result=responseCode+"";
                    //失败后的响应回调
                    if(responseListener!=null)
                        responseListener.onFailure(responseCode,conn);
            }
        }catch (IOException e){
            if(responseListener!=null)
                responseListener.onError(e,conn);
            throw e;
        }
        return result;
    }

    protected SSLSocketFactory createSSLSocketFactory(HttpsURLConnection httpsConn)
            throws NoSuchAlgorithmException, KeyManagementException
    {
        //创建SSLContext对象，并使用我们指定的信任管理器初始化
        TrustManager[] tm={new MyX509TrustManager()};
        SSLContext sslContext=SSLContext.getInstance("SSL");
        sslContext.init(null, tm, new SecureRandom());
        //从上述SSLContext对象中得到SSLSocketFactory对象
        return sslContext.getSocketFactory();
    }

    /**
     * 文件输出时判断Content-Type，这里仅用在输出的元信息里
     * 注：用到第三方组件：jmimemagic
     * @param file 要输出的文件
     */
    public String getContentTypeByFile(File file) {
        String fileContentType = "application/octet-stream";
        try {
            MagicMatch match = Magic.getMagicMatch(file,false);
            fileContentType=match.getMimeType();
        }catch (Exception e){
            e.printStackTrace();
        }
        return fileContentType;
    }

    //检查get方法下的参数是否正确
    protected boolean checkParamsByGet(String params)
    {
        String regex="((\\w+={0,1})(\\w*)&{0,1})+";
        return Pattern.matches(regex,params);
    }

    //检测连接协议
    protected int checkConnectProtocol(URL url) throws ConnectException {
        String mode=url.getProtocol().toLowerCase();
        int connectProtocol;
        switch(mode)
        {
            case "http":
                connectProtocol=HttpConst.CONNECT_HTTP;break;
            case "https":
                connectProtocol=HttpConst.CONNECT_HTTPS;break;
            default:
                throw new ConnectException("Error:url protocol is not http or https");
        }
        return connectProtocol;
    }

    //替换参数中的特殊字符
    protected String replaceSpecialChar(String s)
    {
        String rslt=s;
        if(rslt.contains("+"))
            rslt=rslt.replace("+","%2B");
        else if(rslt.contains(" "))//空格
            rslt=rslt.replace(" ","%20");
        else if(rslt.contains("&"))
            rslt=rslt.replace("&","%26");
        else if(rslt.contains("="))
            rslt=rslt.replace("+","%3D");
        else if(rslt.contains("%"))
            rslt=rslt.replace("%","%25");
        else if(rslt.contains("/"))
            rslt=rslt.replace("/","%2F");
        else if(rslt.contains("?"))
            rslt=rslt.replace("?","%3F");
        else if(rslt.contains("#"))
            rslt=rslt.replace("#","%23");
        return rslt;
    }

    //----------------------------------------------------------------------------------------------

    /**
     * 响应成功时的处理（响应码:200）
     * @param responseCode 响应码
     * @param conn 连接对象
     * @param getDataMode 获取响应数据的模式
     * @param oneHeaderKey 当只获取一个响应头参数时，参数的key（可选）
     */
    private Object handleSuccess(Integer responseCode,URLConnection conn,int getDataMode,String oneHeaderKey)
            throws IOException
    {
        Object result;
        String respData;
        //成功后的响应回调
        if(responseListener!=null)
            responseListener.onSuccess(responseCode,conn);

        InputStream in=conn.getInputStream();
        //如果需要输入流，则直接返回
        if(getDataMode==HttpConst.GETDATA_ONLY_STREAM)
            return in;
        else if(getDataMode==HttpConst.GETDATA_ONLY_CONN_OBJ)//只需要URLConnection对象
            return conn;
        //解读响应的输入流
        String encoding=conn.getContentEncoding();
        if(encoding!=null && encoding.equals("gzip"))//响应体是否为gzip压缩
            respData= StreamParser.readStreamFromGZIP(in);
        else
            respData=StreamParser.readStream(in);
        //打包响应数据
        if(getDataMode==HttpConst.GETDATA_DEFAULT)//默认获取方式
            result=respData;
        else if(getDataMode==HttpConst.GETDATA_NONE)//不需要响应体数据
            result=responseCode;
        else//除响应体数据外额外添加其他数据
            result=packageResponseData(conn,getDataMode,oneHeaderKey,respData);

        return result;
    }

    /**
     * 打包响应的数据
     * @param conn 连接对象
     * @param getDataMode 打包方式
     * @param oneHeaderKey 当只获取一个响应头参数时，参数的key（可选）
     * @param respData 原始字符串形式的响应数据
     */
    private Map<String,Object> packageResponseData(URLConnection conn,int getDataMode,String oneHeaderKey,String respData)
    {
        Map<String,Object> result=new HashMap<>();
        switch(getDataMode)
        {
            case HttpConst.GETDATA_PLUS_ALL_HEADER: //获取响应体及所有响应头参数
                result.put(HttpConst.GETDATA_KEY_RESPONSE_BODY,respData);
                //把所有响应头参数，以key-value的形式存进map里
                Map<String, List<String>> headerFields=conn.getHeaderFields();
                for( Map.Entry<String,List<String>> header : headerFields.entrySet() )
                {
                    result.put(header.getKey(),header.getValue());
                }
                break;
            case HttpConst.GETDATA_PLUS_ONE_HEADER: //获取响应体及指定一个响应头参数
                result.put(HttpConst.GETDATA_KEY_RESPONSE_BODY,respData);
                result.put(HttpConst.GETDATA_KEY_ONE_HEADER,conn.getHeaderField(oneHeaderKey));
                break;
            case HttpConst.GETDATA_PLUS_CONTENT_LENGTH: //获取响应体及响应体长度
                result.put(HttpConst.GETDATA_KEY_RESPONSE_BODY,respData);
                result.put(HttpConst.GETDATA_KEY_CONTENT_LENGTH,conn.getContentLength());
                break;
            case HttpConst.GETDATA_PLUS_CONTENT_ENCODING: //获取响应体及编码
                result.put(HttpConst.GETDATA_KEY_RESPONSE_BODY,respData);
                result.put(HttpConst.GETDATA_KEY_CONTENT_ENCODING,conn.getContentEncoding());
                break;
        }
        return result;
    }

    //原始不用到第三方组件版：文件输出时判断Content-Type
//    protected String getContentTypeForFile(File file) throws Exception{
//        String streamContentType = "application/octet-stream";
//        String imageContentType = "";
//        ImageInputStream image = null;
//        try {
//            image = ImageIO.createImageInputStream(file);
//            if (image == null) {
//                return streamContentType;
//            }
//            Iterator<ImageReader> it = ImageIO.getImageReaders(image);
//            if (it.hasNext()) {
//                imageContentType = "image/" + it.next().getFormatName();
//                return imageContentType;
//            }
//        } catch (IOException e) {
//            throw new Exception(e);
//        } finally {
//            try{
//                if (image != null) {
//                    image.close();
//                }
//            }catch(IOException e){
//                throw new Exception(e);
//            }
//        }
//        return streamContentType;
//    }

}
