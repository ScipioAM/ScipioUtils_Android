package pa.am.scipioutils_android.codec;

import java.util.Random;

/**
 * Class: AbstractCryptUtil
 * Description:
 * Author: Alan Min
 * Createtime: 2018/6/1
 */
public abstract class AbstractCryptUtil {

    //******************* 字符集 *******************
    public static final String CHARSET_UTF8="utf-8";
    public static final String CHARSET_GB2312="gb2312";
    public static final String CHARSET_GBK="gbk";

    //******************* 加密算法 *******************
    public static final String ALGORITHM_AES="AES";
    public static final String ALGORITHM_DES="DES";
    public static final String ALGORITHM_DESEDE="DESede";

    public static final String ALGORITHM_MD5="MD5";
    public static final String ALGORITHM_SHA1="SHA-1";
    public static final String ALGORITHM_SHA256="SHA-256";

    //******************* 加解密模式 *******************
    public static final int CRYPTMODE_ENCRYPTION=0;//加密模式
    public static final int CRYPTMODE_DECRYPTION=1;//解密模式

    //******************* 信息摘要算法，字节数组转字符串的方式选择 *******************
    public static final int CONVERT_BYTE2HEX_FORMAT=0;//格式化方式
    public static final int CONVERT_BYTE2HEX_BITWISE=1;//位运算方式

    //******************* 加盐层数 *******************
    public static final int SALT_LEVEL_NONE=0;//不加盐
    public static final int SALT_LEVEL_1=1;
    public static final int SALT_LEVEL_2=2;

    //---------------------------------------------------------------------------------

    //加盐 - 调用接口方法
    protected String getSaltContent(String content,boolean isFixed,boolean isRegularLength)
    {
        if(isFixed)//固定盐
            return getSaltFixed(content);
        else//随机盐
            return (content+getSaltRandom(isRegularLength));
    }

    /**
     * 格式化方式:字节数组转16进制字符串
     */
    protected String byte2HexByFormat(byte[] bytes)
    {
        StringBuilder sb = new StringBuilder();
        for (byte b:bytes)
        {
            // 10进制转16进制，X 表示以十六进制形式输出，02 表示不足两位前面补0输出
            sb.append(String.format("%02X",b));
        }
        return sb.toString();
    }

    /**
     * 位运算方式:字节数组转16进制字符串
     */
    protected String byte2HexByBitwise(byte[] bytes)
    {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        char chars[] = new char[16 * 2]; // 每个字节用 16 进制表示的话，使用两个字符
        int k = 0; // 表示转换结果中对应的字符位置
        for (int i = 0; i < 16; i++)// 从第一个字节开始，对每一个字节,转换成 16 进制字符的转换
        {
            byte byte0 = bytes[i]; // 取第 i 个字节
            chars[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中高 4 位的数字转换, >>> 为逻辑右移，将符号位一起右移
            chars[k++] = hexDigits[byte0 & 0xf]; // 取字节中低 4 位的数字转换
        }
        return new String(chars);
    }

    //---------------------------------------------------------------------------------

    //加盐 - 随机字符串
    private String getSaltRandom(boolean isRegularLength)
    {
        String salt;
        char[] hex = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        Random random0 = new Random();
        StringBuilder sb = new StringBuilder(16);
        for (int i = 0; i < sb.capacity(); i++)
            sb.append(hex[random0.nextInt(16)]);
        salt=sb.toString();//固定16位长度的随机字符串

        if(!isRegularLength)//不定长的盐
        {
            Random random1=new Random(1000);
            salt+=("@"+random1.nextInt());
        }
        return salt;
    }

    //加盐 - 固定字符串
    private String getSaltFixed(String content)
    {
        char[] arr=content.toCharArray();
        StringBuilder sb=new StringBuilder();
        Integer i=0;
        for(char c:arr)
        {
            sb.append(i);
            sb.append(c);
            i++;
        }
        sb.append("@WQS");
        return sb.toString();
    }

}
