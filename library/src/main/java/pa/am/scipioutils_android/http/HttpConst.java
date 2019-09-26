package pa.am.scipioutils_android.http;

/**
 * Class: HttpConst
 * Description:http常量
 * Author: Alan Min
 * Create Date: 2019/7/25
 */
public class HttpConst {

    //请求方式(get/post)
    public static final String REQUEST_GET="GET";
    public static final String REQUEST_POST="POST";

    //网络连接采取的数据发送方式
    public static final int OUTDATA_NONE=0;//不传参
    public static final int OUTDATA_DEFAULT=1;//默认传参方式(比如x=1&y=2...)
    public static final int OUTDATA_JSON=2;//直接输出json数据
    public static final int OUTDATA_XML=3;//直接输出xml数据

    //是用http连接还是https连接
    public static final int CONNECT_HTTP=0;
    public static final int CONNECT_HTTPS=1;

    //选择获取返回数据的方式
    public static final int GETDATA_DEFAULT=0;//默认只获取返回的数据
    public static final int GETDATA_PLUS_ALL_HEADER=1;//加上所有header信息
    public static final int GETDATA_PLUS_ONE_HEADER=2;//加上指定key的header信息
    public static final int GETDATA_PLUS_CONTENT_ENCODING=3;//加上content-encoding信息
    public static final int GETDATA_PLUS_CONTENT_LENGTH=4;//加上content-length信息
    public static final int GETDATA_ONLY_CONN_OBJ=5;//直接返回URLConnection对象
    public static final int GETDATA_NONE=6;//不需要响应体数据
    public static final int GETDATA_ONLY_STREAM=7;//直接返回InputStream对象

    //获取返回数据的key(如果是GETDATA_DEFAULT模式是用不到的)
    public static final String GETDATA_KEY_RESPONSE_BODY="respBody";//返回的响应体数据
    //public static final String GETDATA_KEY_ALL_HEADER="allHeader";//所有header,弃用，直接以header的key存放
    public static final String GETDATA_KEY_ONE_HEADER="oneHeader";//指定key的header
    public static final String GETDATA_KEY_CONTENT_ENCODING="contentEncoding";//content-encoding
    public static final String GETDATA_KEY_CONTENT_LENGTH="contentLength";//content-length

    //预置user-agent
    public static final String UA_CHROME66_MAC="Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36";
    public static final String UA_SAFARI5_MAC="Mozilla/5.0(Macintosh;U;IntelMacOSX10_6_8;en-us)AppleWebKit/534.50(KHTML,likeGecko)Version/5.1Safari/534.50";
    public static final String UA_IE9_WIN7="Mozilla/5.0(compatible;MSIE9.0;WindowsNT6.1;Trident/5.0";
    public static final String UA_FIREFOX4_MAC="Mozilla/5.0(Macintosh;IntelMacOSX10.6;rv:2.0.1)Gecko/20100101Firefox/4.0.1";
    public static final String UA_OPERA11_WIN7="Opera/9.80(WindowsNT6.1;U;en)Presto/2.8.131Version/11.11";
    public static final String UA_OPERA11_ANDROID2="Opera/9.80(Android2.3.4;Linux;OperaMobi/build-1107180945;U;en-GB)Presto/2.8.149Version/11.10";

}
