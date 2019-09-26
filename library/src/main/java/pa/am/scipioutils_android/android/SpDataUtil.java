package pa.am.scipioutils_android.android;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by AlanMin on 2017-11-20.
 *该类用于Android中对于一些APP配置参数的读写
 */

public class SpDataUtil {

    private Context context;

    public SpDataUtil(Context context) {
        this.context=context;
    }

    //========================================================================

    /**
     * 内部方法：获取全部数据
     * @param fileName 数据文件名
     * @return 所有的数据
     */
    private Map<String ,?> getFullData(String fileName)
    {
        SharedPreferences sp=context.getSharedPreferences(fileName,Context.MODE_PRIVATE);
        Map<String ,?>  data=sp.getAll();
        return data;
    }

    /**
     * 清除所有数据
     * @param fileName 数据文件名
     * @return 是否成功
     */
    public boolean clearData(String fileName)
    {
        boolean isSucceeded=false;
        SharedPreferences sp=context.getSharedPreferences(fileName,Context.MODE_PRIVATE);
        if(sp!=null)
            isSucceeded=sp.edit().clear().commit();
        return isSucceeded;
    }

    /**
     * 获取某个服务器的url
     * @param fileName 数据文件名
     * @param serverName 服务器名
     * @return 该服务器的url
     */
    public String getServerURL(String fileName,String serverName)
    {
        String url;
        Map<String,String> data=getSingleData(fileName,serverName);
        url=data.get(serverName);
        return url;
    }

    /**
     * 获取所有服务器的url数据
     * @param fileName 数据文件名
     * @return 获取结果
     */
    public Map<String,String> getAllServerURL(String fileName)
    {
        Map<String,?> tempData=getFullData(fileName);
        Map<String,String> result=new HashMap<>();
        for(Map.Entry<String,?>  td : tempData.entrySet())
            result.put(td.getKey() , td.getValue().toString());
        return  result;
    }

    /**
     * 写入一个服务器url数据
     * @param fileName 数据文件名
     * @param serverName 服务器名
     * @param serverUrl 服务器url
     * @return 是否成功
     */
    public boolean setServerURL(String fileName,String serverName,String serverUrl)
    {
        Map<String,String> data=getAllServerURL(fileName);
        data.put(serverName,serverUrl);

        if(setData(fileName,data))
            return true;
        else
            return false;
    }

    /**
     * 写入一大堆服务器url数据
     * @param fileName 数据文件名
     * @param data 数据集
     * @return 是否成功
     */
    public boolean setManyServerURL(String fileName,Map<String,String> data)
    {
        if(setData(fileName,data))
            return true;
        else
            return false;
    }

    //========================================================================

    /**
     * 内部方法：写入数据
     * @param fileName 数据文件名
     * @param data 需要写入的数据
     * @return 是否成功
     */
    private boolean setData(String fileName,Map<String,String>data)
    {
        SharedPreferences sp=context.getSharedPreferences(fileName,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        for(Map.Entry<String,String> d : data.entrySet())//遍历数据集并一一写入SharedPreferences对象中
        {
            editor.putString(d.getKey(),d.getValue());
        }
        return editor.commit();//提交修改，返回成功与否的结果
    }

    /**
     * 内部方法：获取单个数据
     * @param fileName 数据文件名
     * @param key 需要查找的数据的键
     * @return 查找结果（一个map包装的数据）
     */
    private Map<String ,String> getSingleData(String fileName,String key)
    {
        Map<String,String> data=new HashMap<>();
        SharedPreferences sp=context.getSharedPreferences(fileName,Context.MODE_PRIVATE);
        data.put(key , sp.getString(key,""));
        return data;
    }

}
