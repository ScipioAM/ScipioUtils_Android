package pa.am.scipioutils_android.http;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Class: HttpConnectionBase
 * Description:
 * Author: Alan Min
 * Createtime: 2018/6/28
 */
public abstract class AbstractHttpUtil extends AbstractHttpBase {

    //传输文件要用到
    private String TOW_HYPHENS ="--";
    private String END = "\r\n";
    //边界符-传输文件要用到
    private String BOUNDARY=null;

    /**
     * 发起http请求并获取返回的数据(不包括输出文件)
     * @param urlPath url路径
     * @param requestMethod get请求还是post请求
     * @param paramsMode 发起请求时传参的方式，默认(比如x=1&y=2...)
     * @param outputParam 发起请求时传的参（可选）
     * @param getDataMode 获取数据的模式，默认只获取响应体的数据
     * @param oneHeaderKey 获取响应的同属获取一个指定响应头参数（可选）
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Object doRequest(  String urlPath,   String requestMethod
            ,   int paramsMode, Object outputParam
            ,   int getDataMode, String oneHeaderKey)
            throws NoSuchAlgorithmException,IOException,KeyManagementException
    {
        Object result;//连接后获取的响应结果
        String currentOutputString=null;//要输出的字符串数据(可选)
        String currentUrl=urlPath;//最终的url地址
        String contentType=null;
        //准备请求参数
        if(requestMethod.equals(HttpConst.REQUEST_GET))//为get方法拼接参数
        {
            if(paramsMode!=HttpConst.OUTDATA_NONE)
            {
                if(outputParam instanceof String)
                {
                    if( super.checkParamsByGet((String)outputParam))
                        currentUrl+=("?"+outputParam);
                    else
                        throw new RuntimeException("Error:wrong params by using GET method");
                }
                else if (outputParam instanceof HashMap)
                    currentUrl=setUrlByGet(urlPath, (HashMap<String,String>)outputParam);
                else
                    throw new RuntimeException("Error:wrong parmas by using GET method");
            }
        }
        else//为post方法准备数据
        {
            switch (paramsMode)
            {
                case HttpConst.OUTDATA_DEFAULT:
                    currentOutputString=setParamsByPost((HashMap<String,String>)outputParam);
                    break;
                case HttpConst.OUTDATA_JSON:
                    contentType="application/json";
                    currentOutputString= (String) outputParam;
                    break;
                case HttpConst.OUTDATA_XML:
                    contentType="text/xml";
                    currentOutputString= (String) outputParam;
                    break;
                case HttpConst.OUTDATA_NONE:
                    break;
                default:
                    throw new RuntimeException("Wrong output data mode!");
            }
        }

        URL url=new URL(currentUrl);
        int connectProtocol=super.checkConnectProtocol(url);//检查是http还是https

        if(connectProtocol==HttpConst.CONNECT_HTTPS)//如果是https必要的处理
        {
            HttpsURLConnection httpsConn;
            if(proxy!=null)
                httpsConn= (HttpsURLConnection) url.openConnection(proxy);
            else
                httpsConn= (HttpsURLConnection) url.openConnection();
            httpsConn.setSSLSocketFactory(super.createSSLSocketFactory(httpsConn));//设置SSLSocketFactory

            httpsConn.setRequestMethod(requestMethod);//设置连接方法
            httpsConn.setInstanceFollowRedirects(isFollowRedirects);//是否关闭重定向以获取跳转后的真实地址
            //公共的请求头设置
            setCommonConnectionData(httpsConn,contentType);

            //如果是非默认传参方式且为POST请求，则通过输出流输出参数
            if( requestMethod.equals(HttpConst.REQUEST_POST) && (paramsMode!=HttpConst.OUTDATA_NONE) )
            {
                OutputStream os = httpsConn.getOutputStream();
                os.write(currentOutputString.getBytes(StandardCharsets.UTF_8));//将数据输出
                os.close();
            }

            //处理响应结果
            int responseCode=httpsConn.getResponseCode();//获取返回的状态码
            result=super.handleResponse(httpsConn,responseCode,getDataMode,oneHeaderKey,responseListener);//获取返回的数据
        }
        else
        {
            HttpURLConnection httpConn;
            if(proxy!=null)
                httpConn= (HttpURLConnection) url.openConnection(proxy);
            else
                httpConn= (HttpURLConnection) url.openConnection();

            httpConn.setRequestMethod(requestMethod);//设置连接方法
            httpConn.setInstanceFollowRedirects(isFollowRedirects);//是否关闭重定向以获取跳转后的真实地址
            //公共设置
            super.setCommonConnectionData(httpConn,contentType);

            //如果是非默认传参方式且为POST请求，则通过输出流输出参数
            if( requestMethod.equals(HttpConst.REQUEST_POST) && (paramsMode!=HttpConst.OUTDATA_NONE) )
            {
                OutputStream os = httpConn.getOutputStream();
                os.write(currentOutputString.getBytes(StandardCharsets.UTF_8));//将数据输出
                os.close();
            }

            //处理响应结果
            int responseCode=httpConn.getResponseCode();//获取返回的状态码
            result=handleResponse(httpConn,responseCode,getDataMode,oneHeaderKey,responseListener);//获取返回的数据
        }
        return result;
    }

    /**
     * 以默认设置进行(仍要传参)
     * 发起http请求并获取返回的数据
     * @param urlPath url路径
     * @param requestMethod get请求还是post请求
     * @param params 发起请求时传的参（可选）
     *  注：发起请求时传参的方式是(比如x=1&y=2...)
     */
    protected Object doRequest(  String urlPath,   String requestMethod , Object params)
            throws NoSuchAlgorithmException,IOException,KeyManagementException
    {
        return doRequest(urlPath,requestMethod,HttpConst.OUTDATA_DEFAULT,params,
                HttpConst.GETDATA_DEFAULT,null);
    }

    /**
     * 以默认设置进行(不传参)
     * 发起http请求并获取返回的数据
     * @param urlPath url路径
     * @param requestMethod get请求还是post请求
     */
    protected Object doRequest(  String urlPath,   String requestMethod )
            throws NoSuchAlgorithmException,IOException,KeyManagementException
    {
        return doRequest(urlPath,requestMethod,HttpConst.OUTDATA_NONE,
                null,HttpConst.GETDATA_DEFAULT,null);
    }

    //----------------------------------------------------------------------------------------------

    /**
     * 发起http请求，输出文件流（可能还包括其他参数），获取返回的数据
     * @param urlPath 请求的url
     * @param outputParams 请求的字符串参数,不需要传递时为null
     * @param outputFiles 输出的文件（单个File对象或key-File的HashMap对象）
     * @param getDataMode 获取响应数据的模式
     * @return 响应体的内容，或者响应体+URLConnection对象
     */
    @Override
    protected Object doFileRequest( String urlPath, Map<String,String> outputParams,  Object outputFiles , int getDataMode )
            throws IOException, KeyManagementException, NoSuchAlgorithmException {
        //返回的响应结果
        Object result;
        //定义边界符
        BOUNDARY="**********"+System.currentTimeMillis();
        //Content-Type的定义
        String contentType="multipart/form-data; boundary="+BOUNDARY;

        URL url=new URL(urlPath);
        int connectProtocol=super.checkConnectProtocol(url);//检查是http还是https
        //https的情况下
        if(connectProtocol==HttpConst.CONNECT_HTTPS)
        {
            HttpsURLConnection httpsConn;
            if(proxy!=null)
                httpsConn= (HttpsURLConnection) url.openConnection(proxy);
            else
                httpsConn= (HttpsURLConnection) url.openConnection();
            
            httpsConn.setSSLSocketFactory(super.createSSLSocketFactory(httpsConn));//设置SSLSocketFactory
            httpsConn.setInstanceFollowRedirects(isFollowRedirects);//是否关闭重定向以获取跳转后的真实地址
            httpsConn.setRequestMethod(HttpConst.REQUEST_POST);
            httpsConn.setUseCaches(false);
            //公共的请求头设置
            setCommonConnectionData(httpsConn,contentType);

            //文件上传开始时的回调
            if(uploadListener!=null)
            {
                uploadListener.onStart();
            }
            //执行数据输出操作
            doMultipartOutput(httpsConn.getOutputStream(),outputParams,outputFiles);

            //获取响应码
            int responseCode=httpsConn.getResponseCode();
            //处理返回的数据
            result=super.handleResponse(httpsConn,responseCode, getDataMode, null,responseListener);
        }
        //http的情况下
        else
        {
            HttpURLConnection httpConn;
            if(proxy!=null)
                httpConn= (HttpsURLConnection) url.openConnection(proxy);
            else
                httpConn= (HttpsURLConnection) url.openConnection();

            httpConn.setInstanceFollowRedirects(isFollowRedirects);//是否关闭重定向以获取跳转后的真实地址
            httpConn.setRequestMethod(HttpConst.REQUEST_POST);

            //执行数据输出操作
            doMultipartOutput(httpConn.getOutputStream(),outputParams,outputFiles);

            //获取响应码
            int responseCode=httpConn.getResponseCode();
            //处理返回的数据
            result=super.handleResponse(httpConn,responseCode, getDataMode, null,responseListener);
        }
        return result;
    }

    //----------------------------------------------------------------------------------------------

    /**
     * 执行数据输出操作
     * @param os 连接的输出流对象
     * @param outputParams 要输出的字符串参数（可选）
     * @param outputFiles 要输出的文件对象（单个File对象或key-File的HashMap对象）
     */
    private void doMultipartOutput( OutputStream os , Map<String,String> outputParams, Object outputFiles)
            throws IOException
    {
        // 往服务器端写内容 也就是发起http请求需要带的参数
        DataOutputStream dos = new DataOutputStream(os);
        // 请求参数部分
        writeParams(outputParams, dos);
        // 请求上传文件部分
        writeFiles(outputFiles, dos);
        // 请求结束标志
        String endTarget = TOW_HYPHENS + BOUNDARY + TOW_HYPHENS + END;
        dos.writeBytes(endTarget);
        dos.flush();
        dos.close();
    }

    /**
     * 传输文件专用 - 对字符串参数进行编码处理并输出数据流中
     * @param outputParams 要传输的参数
     * @param dos 数据输出流
     */
    private void writeParams(Map<String,String> outputParams, DataOutputStream dos) throws IOException
    {
        if( outputParams==null || outputParams.isEmpty() )
        {
            System.out.println("发送的字符串参数为空");//test
            return;
        }
        
        StringBuilder params=new StringBuilder();
        for( Map.Entry<String,String> entry : outputParams.entrySet() )
        {
            //每段开头
            params.append(TOW_HYPHENS).append(BOUNDARY).append(END);
            //参数头
            params.append("Content-Disposition: form-data; name=\"")
                    .append(entry.getKey()).append("\"");
            params.append(END);
            params.append("Content-Type: text/plain; charset=utf-8");
            params.append(END);
            params.append(END);// 参数头设置完以后需要两个换行，然后才是参数内容
            params.append(entry.getValue());
            params.append(END);
        }
        dos.writeBytes(params.toString());
        dos.flush();
    }

    /**
     * 传输文件专用 - 将文件输出到数据流中
     * @param outputFiles 要输出的文件（单个File对象或key-File的HashMap对象）
     * @param dos 数据输出流
     */
    @SuppressWarnings("unchecked")
    private void writeFiles(Object outputFiles, DataOutputStream dos) throws IOException
    {
        File file;
        Map<String,File> fileMap;
        if( outputFiles==null )
        {
            return;
        }
        try {
            if( outputFiles instanceof File )
            {
                file=(File)outputFiles;
                writeSingleFile(dos,"file",file,true);
                //上传完成的回调
                if(uploadListener!=null)
                {
                    uploadListener.onCompleted();
                }
            }
            else if( outputFiles instanceof HashMap )
            {
                double loopCount=1.0;
                fileMap=(HashMap<String,File>)outputFiles;
                boolean isSingleFile = ( fileMap.size()==1 );
                for( Map.Entry<String,File> entry : fileMap.entrySet() )
                {
                    writeSingleFile(dos,entry.getKey(),entry.getValue(),isSingleFile);
                    if( !isSingleFile && uploadListener!=null)
                    {
                        double percentage=loopCount/fileMap.size();
                        uploadListener.onProcess(percentage);
                    }
                    loopCount++;
                }//end of for
                //上传完成的回调
                if(uploadListener!=null)
                {
                    uploadListener.onCompleted();
                }
            }//end of elseif
        }catch (IOException e){
            if(uploadListener!=null)
            {
                uploadListener.onError(e);
            }
            throw e;
        }
    }//end of writeFiles()

    /**
     * 对单个文件的输出操作
     * @param dos 数据输出流
     * @param key 元信息里的name的值
     * @param file 要输出的文件
     * @param isSingleUpload 是否是单个文件输出（关系到响应监听器）
     */
    private void writeSingleFile( DataOutputStream dos, String key, File file , boolean isSingleUpload)
            throws IOException
    {
        StringBuilder headParams=new StringBuilder();
        headParams.append(TOW_HYPHENS).append(BOUNDARY).append(END);
        headParams.append("Content-Disposition: form-data; name=\"")
                .append(key).append("\"; filename=\"")
                .append(file.getName()).append("\"");
        headParams.append(END);
        headParams.append("Content-Type:")
                .append(super.getContentTypeByFile(file));
        headParams.append(END);
        headParams.append(END);// 参数头设置完以后需要两个换行，然后才是参数内容

        dos.writeBytes(headParams.toString());

        FileInputStream fis = new FileInputStream(file);
        int len;
        int readedLen=0;//已经读取的长度
        double fileLength=file.length();//文件总长度
        //确定上传文件缓冲区的大小
        if( uploadFileBufferSize==null || uploadFileBufferSize==0 )
        {
            setDefaultBufferSize(file.length());
        }
        byte[] buffer = new byte[uploadFileBufferSize];
        //开始上传
        while ((len = fis.read(buffer)) != -1) {
            dos.write(buffer, 0, len);
            readedLen += len;//已输出的长度
            //上传进度的回调-针对单个上传文件
            if(isSingleUpload && uploadListener!=null)
            {
                double percentage=readedLen/fileLength;
                uploadListener.onProcess(percentage);
            }
        }
        dos.writeBytes(END);
        dos.flush();
    }

    /**
     * 根据文件大小调整缓冲区的大小
     * @param fileLength 文件总长度，单位是byte
     */
    private void setDefaultBufferSize(long fileLength)
    {
        if( fileLength<=(1024*1024) )
            uploadFileBufferSize=1024;
        else if ( fileLength<=(1024*1024*10) )
            uploadFileBufferSize=4096;
        else
            uploadFileBufferSize=1024*1024;
    }

    /**
     * 为get方法准备好带参的url地址
     * @param address 地址
     * @param params 参数
     * @return 一拼接好的地址字符串
     */
    private String setUrlByGet(String address, Map<String,String> params)
    {
        if(params==null)
            return address;

        String url=address+"?";
        StringBuilder temp=new StringBuilder();
        for(Map.Entry<String,String> p : params.entrySet())
        {
            temp.append( super.replaceSpecialChar(p.getKey()));
            temp.append("=");
            temp.append( super.replaceSpecialChar(p.getValue()));
            temp.append("&");
        }
        url+=temp.substring(0,  (temp.length()-1) );
        return url;
    }


    /**
     * 将参数对象转换成适应post提交的字符串
     * @param params 需要传递的参数
     * @return 已经转换适成应post提交的字符串
     */
    private String setParamsByPost(Map<String,String> params)
    {
        if(params==null)
            return null;

        StringBuilder temp=new StringBuilder();
        String postData;
        for(Map.Entry<String,String> p : params.entrySet())
        {
            temp.append( super.replaceSpecialChar(p.getKey()));
            temp.append("=");
            temp.append( super.replaceSpecialChar(p.getValue()));
            temp.append("&");
        }
        postData=temp.substring(0 , temp.length()-1);
        return postData;
    }

}
