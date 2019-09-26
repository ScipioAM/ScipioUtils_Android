package pa.am.scipioutils_android.codec;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * Class: CryptUtil
 * Description:
 * Author: Alan Min
 * Createtime: 2018/6/1
 */
public class CryptUtil extends AbstractCryptUtil {

    /**
     * 如果要将该工具类加入到Spring框架，由Spring来管理其生命周期，则需要做如下改写：
     * 1.取消getInstance方法，放开构造函数
     * 2.编写该工具类的接口
     * 3.打上@Component注解使其能够被Spring管理
     */

    private String charset;//字符集，默认UTF-8编码
    private SymmetricCrypt  scObject;//对称加解密
    private  MyMessageDigest mmdObject;//信息摘要算法
    
    public CryptUtil(){
        init(null);
    }

    public CryptUtil(String charset) {
        init(charset);
    }

    private void init(String charset)
    {
        if(charset==null || charset.equals(""))
            this.charset= CHARSET_UTF8;
        else
            this.charset=charset;
         scObject=new SymmetricCrypt();
        mmdObject=new MyMessageDigest();
    }

    //**********************************************************

    /**
     * 对称加密算法下的加解密
     * param key: 用户密钥
     * param content: 加解密内容
     * param isRandomKey: 是否生成随机加密密钥
     */
    //AES加密
    public String encryptAES(String key, String content,boolean isRandomKey)
            throws NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException,
            IllegalBlockSizeException, UnsupportedEncodingException, InvalidKeyException {
        return  scObject.symmetricCrypt( ALGORITHM_AES,charset,key,content,
                isRandomKey, CRYPTMODE_ENCRYPTION);
    }

    //ASE解密
    public String decryptAES(String key, String content,boolean isRandomKey)
            throws NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException,
            IllegalBlockSizeException, UnsupportedEncodingException, InvalidKeyException {
        return  scObject.symmetricCrypt( ALGORITHM_AES,charset,key,content,
                isRandomKey, CRYPTMODE_DECRYPTION);
    }

    //3DES加密
    public String encrypt3DES(String key, String content,boolean isRandomKey)
            throws NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException,
            IllegalBlockSizeException, UnsupportedEncodingException, InvalidKeyException {
        return  scObject.symmetricCrypt( ALGORITHM_DESEDE,charset,key,content,
                isRandomKey, CRYPTMODE_ENCRYPTION);
    }

    //3DES解密
    public String decrypt3DES(String key, String content,boolean isRandomKey)
            throws NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException,
            IllegalBlockSizeException, UnsupportedEncodingException, InvalidKeyException {
        return  scObject.symmetricCrypt( ALGORITHM_DESEDE,charset,key,content,
                isRandomKey, CRYPTMODE_DECRYPTION);
    }

    //DES加密
    public String encryptDES(String key, String content,boolean isRandomKey)
            throws NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException,
            IllegalBlockSizeException, UnsupportedEncodingException, InvalidKeyException {
        return  scObject.symmetricCrypt( ALGORITHM_DES,charset,key,content,
                isRandomKey, CRYPTMODE_ENCRYPTION);
    }

    //DES加密
    public String decryptDES(String key, String content,boolean isRandomKey)
            throws NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException,
            IllegalBlockSizeException, UnsupportedEncodingException, InvalidKeyException {
        return  scObject.symmetricCrypt( ALGORITHM_DES,charset,key,content,
                isRandomKey, CRYPTMODE_DECRYPTION);
    }

    //**********************************************************

    /**
     * 信息摘要算法下的加密
     * param algorithm:加密算法
     * param content:需要加密的内容
     * param convertType:转换方式（位运算方式或StringBuilder格式化方式）
     * param saltLevel:加盐的层数
     * param isFixedSalt:(如果加盐)是加随机字符串的盐还是固定字符串的盐
     */
    //md5加密
    public String md5(String content) throws NoSuchAlgorithmException {
        return mmdObject.saltEncode( ALGORITHM_MD5,content,
                 CONVERT_BYTE2HEX_BITWISE, SALT_LEVEL_NONE,null);
    }

    public String md5(String content,int saltLevel,Boolean isFixedSalt) throws NoSuchAlgorithmException {
        return mmdObject.saltEncode( ALGORITHM_MD5,content,
                CONVERT_BYTE2HEX_BITWISE,saltLevel,isFixedSalt);
    }

    public String md5(String content,int saltLevel,int convertType,Boolean isFixedSalt) throws NoSuchAlgorithmException {
        return mmdObject.saltEncode( ALGORITHM_MD5,content,convertType,saltLevel,isFixedSalt);
    }

    //SHA-1 加密
    public String sha_1(String content) throws NoSuchAlgorithmException {
        return mmdObject.saltEncode( ALGORITHM_SHA1,content,
                 CONVERT_BYTE2HEX_BITWISE, SALT_LEVEL_NONE,null);
    }

    public String sha_1(String content,int saltLevel,Boolean isFixedSalt) throws NoSuchAlgorithmException {
        return mmdObject.saltEncode( ALGORITHM_SHA1,content,
                 CONVERT_BYTE2HEX_BITWISE,saltLevel,isFixedSalt);
    }

    public String sha_1(String content,int saltLevel,int convertType,Boolean isFixedSalt) throws NoSuchAlgorithmException {
        return mmdObject.saltEncode( ALGORITHM_SHA1,content,convertType,saltLevel,isFixedSalt);
    }

    //SHA-256 加密
    public String sha_256(String content) throws NoSuchAlgorithmException {
        return mmdObject.saltEncode( ALGORITHM_SHA256,content,
                 CONVERT_BYTE2HEX_BITWISE, SALT_LEVEL_NONE,null);
    }

    public String sha_256(String content,int saltLevel,Boolean isFixedSalt) throws NoSuchAlgorithmException {
        return mmdObject.saltEncode( ALGORITHM_SHA256,content,
                CONVERT_BYTE2HEX_BITWISE,saltLevel,isFixedSalt);
    }

    public String sha_256(String content,int saltLevel,int convertType,Boolean isFixedSalt) throws NoSuchAlgorithmException {
        return mmdObject.saltEncode( ALGORITHM_SHA256,content,convertType,saltLevel,isFixedSalt);
    }

    //**********************************************************

    public String getCharset(){
        return charset;
    }

    public void setCharset(String charset){
        this.charset=charset;
    }

    /**
     * 获取加密时生成的密钥对象
     */
    public SecretKey getSecretKey()
    {
        return  scObject.originalKey;
    }
    
}
