package pa.am.scipioutils_android.parser;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.DateConverter;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Class: XmlUtil
 * Description: v1.1
 *  xml解析
 *  需要依赖：XStream,dom4j
 * Author: Alan Min
 * Createtime: 2018/5/16
 */
public class XmlParser {

    private XStream xstream;

    //日期转换器
    private static final DateConverter DATE_CONVERTER =
            new DateConverter("yyyy-MM-dd HH:mm:ss", null, TimeZone.getTimeZone("GMT+8"));

    //设置是否扩展CDATA
    private boolean isSetCDATA=false;

    public XmlParser()
    {
        extendCDATA();
    }

    public XmlParser(boolean isSetCDATA) {
        this.isSetCDATA=isSetCDATA;
        extendCDATA();
    }

    /**
     * 扩展XStream使其支持CDATA
     */
    private void extendCDATA()
    {
        xstream = new XStream(new XppDriver() {
            @Override
            public HierarchicalStreamWriter createWriter(Writer out) {
                return new PrettyPrintWriter(out) {
                    public void startNode(String name, Class clazz) {
                        super.startNode(name, clazz);
                    }
                    protected void writeText(QuickWriter writer, String text) {
                        if (isSetCDATA) {
                            writer.write("<![CDATA[");
                            writer.write(text);
                            writer.write("]]>");
                        } else {
                            writer.write(text);
                        }//end of else
                    }//end of writeText()
                };//end of return
            }// end of createWriter()
        });
    }

    //----------------------------------------------------------------------

    /**
     * xml转java对象
     * @param xml 要转换的xml字符串
     * @param clazz 目标java对象
     */
    @SuppressWarnings("unchecked")
    public <T> T xmlToObject(String xml, Class<T> clazz) {
        //XStream1.5之后移除了该方法
        XStream.setupDefaultSecurity(xstream);
        //XStream的安全设置之二
        xstream.allowTypeHierarchy(clazz);
        // 注册日期转换器
        xstream.registerConverter(DATE_CONVERTER);
        xstream.processAnnotations(clazz);
        //开始转换
        return (T) xstream.fromXML(xml);
    }

    /**
     * 从xml文件读取并转java对象
     * @param filePath xml文件的全路径
     * @param clazz 目标java对象
     */
    @SuppressWarnings("unchecked")
    public <T> T xmlFileToObject(String filePath, Class<T> clazz) {
        //XStream1.5之后移除了该方法
        XStream.setupDefaultSecurity(xstream);
        //XStream的安全设置之二
        xstream.allowTypeHierarchy(clazz);
        // 注册日期转换器
        xstream.registerConverter(DATE_CONVERTER);
        xstream.processAnnotations(clazz);
        //开始转换
        return (T) xstream.fromXML(new File(filePath));
    }

    /**
     * java对象转xml
     * @param obj 要转换的java对象
     */
    public String objectToXml(Object obj) {
        //XStream1.5之后移除了该方法
        XStream.setupDefaultSecurity(xstream);
        // 注册日期转换器
        xstream.registerConverter(DATE_CONVERTER);
        xstream.processAnnotations(obj.getClass());
        return xstream.toXML(obj);
    }

    /**
     * java对象转xml并输出到文件
     * @param obj 要转换的java对象
     * @param filePath 输出文件的全路径
     */
    public void objectToXmlFile(Object obj, String filePath) throws FileNotFoundException {
        //XStream1.5之后移除了该方法
        XStream.setupDefaultSecurity(xstream);
        xstream.registerConverter(DATE_CONVERTER);
        xstream.processAnnotations(obj.getClass());
        // create target file
        FileOutputStream fileOutputStream = new FileOutputStream(new File(filePath));
        // output
        xstream.toXML(obj, fileOutputStream);
    }

    /**
     * 设置根节点别名，否则默认类全名
     * @param alias 根节点别名
     * @param clazz 要转换的类
     */
    public XmlParser setAlias(String alias, Class clazz){
        xstream.alias(alias,clazz);
        return this;
    }

    public XStream getXStream() {
        return xstream;
    }

    public void setXStream(XStream xstream) {
        this.xstream = xstream;
    }

    public boolean isSetCDATA() {
        return isSetCDATA;
    }

    public void setCDATA(boolean setCDATA) {
        isSetCDATA = setCDATA;
    }

    //----------------------------------------------------------------------

    /**
     * 从字符串里解析xml数据为键值对(利用dom4j框架)
     * @return 解析得打的结果集(用map存放)
     */
    public Map<String,Object> parseXmlToMap(String xml) throws IOException, DocumentException
    {
        return doParseXml(xml,XML_PARAM_STRING);
    }

    /**
     * 输入流里解析xml数据为键值对(利用dom4j框架)
     * @return 解析得打的结果集(用map存放)
     */
    public Map<String,Object> parseXmlStream2Map(InputStream in) throws IOException, DocumentException
    {
        return doParseXml(in,XML_PARAM_STREAM);
    }

    /**
     * 从文件里解析xml数据为键值对(利用dom4j框架)
     * @return 解析得到的结果集(用map存放)
     */
    public Map<String,Object> parseXmlFile2Map(String filePath) throws IOException, DocumentException
    {
        return doParseXml(filePath,XML_PARAM_FILE_PATH);
    }

    /**
     * 根据指定key从Xml中解析出对应数据
     * @param xml 待解析的xml字符串数据
     * @param key 指定的key
     */
    public Object getValueFromXml(String xml,String key) throws IOException, DocumentException
    {
        Map<String,Object> xmlMap=parseXmlToMap(xml);
        return xmlMap.get(key);
    }

    //----------------------------------------------------------------------
    private static final int XML_PARAM_STRING=0;//要解析的数据是string
    private static final int XML_PARAM_FILE_PATH=1;//要解析的数据是文件
    private static final int XML_PARAM_STREAM=2;//要解析的数据是输入流

    /**
     * 利用dom4j解析xml
     * @param xml 要解析的xml数据
     * @param xmlParamMode xml数据的模式
     */
    @SuppressWarnings("unchecked")
    private Map<String,Object> doParseXml(Object xml,int xmlParamMode) throws DocumentException, IOException
    {
        Map<String,Object> result=new HashMap<>();
        Document document;
        SAXReader reader;
        if(xmlParamMode==XML_PARAM_STRING)
        {
            document= DocumentHelper.parseText((String) xml);
        }
        else if(xmlParamMode==XML_PARAM_STREAM)
        {
            reader=new SAXReader();
            document=reader.read((InputStream) xml);
        }
        else
        {
            reader=new SAXReader();
            document=reader.read(new File((String) xml));
        }

        Element root=document.getRootElement();//获取根节点
        List<Element> list=root.elements();//根据根节点获取所有节点
        for(Element e:list)//遍历，存入结果集
        {
            result.put(e.getName(), e.getData());
        }
        if( xmlParamMode==XML_PARAM_STREAM )
        {
            InputStream in=(InputStream)xml;
            in.close();
            in=null;
        }
        return result;
    }

}
