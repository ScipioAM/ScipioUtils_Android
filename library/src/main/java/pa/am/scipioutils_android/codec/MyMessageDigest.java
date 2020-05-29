package pa.am.scipioutils_android.codec;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class: MyMessageDigest
 * Description: 信息摘要算法的实现类
 * Author: Alan Min
 * Createtime: 2018/6/1
 */
public class MyMessageDigest extends AbstractEncryptUtil {

    /**
     * 加盐
     * param saltLevel:加盐的层数
     */
    protected String saltEncode(String algorithm,String content,int convertType,int saltLevel,Boolean isFixedSalt)
            throws NoSuchAlgorithmException
    {
        String result,saltContent;
        switch(saltLevel)
        {
            case SALT_LEVEL_NONE:
                result=baseEncode(algorithm,content,convertType);
                break;
            case SALT_LEVEL_1:
                saltContent=getSaltContent(content,isFixedSalt,true);
                result=baseEncode(algorithm,saltContent,convertType);
                break;
            case SALT_LEVEL_2:
                saltContent=getSaltContent(content,isFixedSalt,false);
                String tempResult=baseEncode(algorithm,saltContent,convertType);//第一层加密
                result=baseEncode(algorithm,getSaltContent(tempResult,isFixedSalt,false),convertType);
                break;
            default:
                throw new RuntimeException("Error:the param [saltLevel] is illegal");
        }
        return result;
    }

    /**
     * 加密核心实现方法
     * param algorithm:加密算法
     * param content:需要加密的内容
     * param convertType:转换方式（位运算方式或StringBuilder格式化方式）
     */
    private String baseEncode(String algorithm,String content,int convertType)
            throws NoSuchAlgorithmException
    {
        String result;
        MessageDigest md=MessageDigest.getInstance(algorithm);
        md.update(content.getBytes());// 通过使用 update 方法处理数据,使指定的 byte数组更新摘要
        byte[] byte_encoded=md.digest();//完成哈希计算，得到加密后的字节数组
        if(convertType==CONVERT_BYTE2HEX_BITWISE)
            result=super.byte2HexByBitwise(byte_encoded);
        else if(convertType==CONVERT_BYTE2HEX_FORMAT)
            result=super.byte2HexByFormat(byte_encoded);
        else
            throw new RuntimeException("Error:the param [convertType] is illegal");
        return result;
    }

}
