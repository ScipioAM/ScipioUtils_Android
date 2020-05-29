package pa.am.scipioutils_android.common;

import java.util.regex.Pattern;

/**
 * Class: AppUtil
 * Description: v1.2
 * 通用小工具类
 * Author: Alan Min
 * Createtime: 2018/4/20
 */
public class StringUtil {

    private StringUtil(){}

    /*判断字符串是否不为空*/
    public static Boolean isNotNull(String s)
    {
        return ( (s!=null) && (!s.equals("")) );
    }

    /*判断字符串是否为空*/
    public static Boolean isNull(String s)
    {
        return ( (s==null) || (s.equals("")) );
    }

    /*判断字符串是否为数字*/
    public static Boolean isNumeric(String str) {
        Pattern pattern=Pattern.compile("\\d+(\\.\\d+)?");
        return pattern.matcher(str).matches();
    }

    /*判断字符串是否为整数*/
    public static Boolean isIntNumeric(String str) {
        Pattern pattern=Pattern.compile("\\d+");
        return pattern.matcher(str).matches();
    }
	
	/*把byte转为字符串的bit*/
    public static String byteToBit(byte b) {
        return ""
                + (byte) ((b >> 7) & 0x1)
                + (byte) ((b >> 6) & 0x1)
                + (byte) ((b >> 5) & 0x1)
                + (byte) ((b >> 4) & 0x1)
                + (byte) ((b >> 3) & 0x1)
                + (byte) ((b >> 2) & 0x1)
                + (byte) ((b >> 1) & 0x1)
                + (byte) ((b) & 0x1);
    }

    /*bit字符串转byte*/
    public static byte bitToByte(String bit) {
        int re, len;
        if (null == bit)
            return 0;
        len = bit.length();
        if (len != 4 && len != 8)
            return 0;
        if (len == 8) // 8 bit处理
        {
            if (bit.charAt(0) == '0') // 正数
                re = Integer.parseInt(bit, 2);//输出2进制数在10进制下的结果
            else// 负数
                re = Integer.parseInt(bit, 2) - 256;
        }
        else //4 bit处理
        {
            re = Integer.parseInt(bit, 2);
        }
        return (byte) re;
    }

}
