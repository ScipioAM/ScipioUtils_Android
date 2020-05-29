package pa.am.scipioutils_android.codec;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Class: CryptUtil
 * Description:
 * Author: Alan Min
 * Createtime: 2018/6/1
 */
public class EncryptUtil extends AbstractEncryptUtil {

    /**
     * 如果要将该工具类加入到Spring框架，由Spring来管理其生命周期，则需要做如下改写：
     * 1.取消getInstance方法，放开构造函数
     * 2.编写该工具类的接口
     * 3.打上@Component注解使其能够被Spring管理
     */

    private String charset;//字符集，默认UTF-8编码
    private SymmetricEncrypt scObject;//对称加解密
    private  MyMessageDigest mmdObject;//信息摘要算法
    
    public EncryptUtil(){
        init(null);
    }

    public EncryptUtil(String charset) {
        init(charset);
    }

    private void init(String charset)
    {
        if(charset==null || charset.equals(""))
            this.charset= CHARSET_UTF8;
        else
            this.charset=charset;
         scObject=new SymmetricEncrypt();
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

    //AES加密
    public String encryptAES(String key, String content)
            throws NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException,
            IllegalBlockSizeException, UnsupportedEncodingException, InvalidKeyException {
        return encryptAES(key,content,false);
    }

    //ASE解密
    public String decryptAES(String key, String content,boolean isRandomKey)
            throws NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException,
            IllegalBlockSizeException, UnsupportedEncodingException, InvalidKeyException {
        return  scObject.symmetricCrypt( ALGORITHM_AES,charset,key,content,
                isRandomKey, CRYPTMODE_DECRYPTION);
    }

    //ASE解密
    public String decryptAES(String key, String content)
            throws NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException,
            IllegalBlockSizeException, UnsupportedEncodingException, InvalidKeyException {
        return decryptAES(key, content,false);
    }

    //3DES加密
    public String encrypt3DES(String key, String content,boolean isRandomKey)
            throws NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException,
            IllegalBlockSizeException, UnsupportedEncodingException, InvalidKeyException {
        return  scObject.symmetricCrypt( ALGORITHM_DESEDE,charset,key,content,
                isRandomKey, CRYPTMODE_ENCRYPTION);
    }

    //3DES加密
    public String encrypt3DES(String key, String content)
            throws NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException,
            IllegalBlockSizeException, UnsupportedEncodingException, InvalidKeyException {
        return encrypt3DES(key, content,false);
    }

    //3DES解密
    public String decrypt3DES(String key, String content,boolean isRandomKey)
            throws NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException,
            IllegalBlockSizeException, UnsupportedEncodingException, InvalidKeyException {
        return  scObject.symmetricCrypt( ALGORITHM_DESEDE,charset,key,content,
                isRandomKey, CRYPTMODE_DECRYPTION);
    }

    //3DES解密
    public String decrypt3DES(String key, String content)
            throws NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException,
            IllegalBlockSizeException, UnsupportedEncodingException, InvalidKeyException {
        return decrypt3DES(key,content,false);
    }

    //DES加密
    public String encryptDES(String key, String content,boolean isRandomKey)
            throws NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException,
            IllegalBlockSizeException, UnsupportedEncodingException, InvalidKeyException {
        return  scObject.symmetricCrypt( ALGORITHM_DES,charset,key,content,
                isRandomKey, CRYPTMODE_ENCRYPTION);
    }

    //DES加密
    public String encryptDES(String key, String content)
            throws NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException,
            IllegalBlockSizeException, UnsupportedEncodingException, InvalidKeyException {
        return encryptDES(key, content, false);
    }

    //DES加密
    public String decryptDES(String key, String content,boolean isRandomKey)
            throws NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException,
            IllegalBlockSizeException, UnsupportedEncodingException, InvalidKeyException {
        return  scObject.symmetricCrypt( ALGORITHM_DES,charset,key,content,
                isRandomKey, CRYPTMODE_DECRYPTION);
    }

    //DES加密
    public String decryptDES(String key, String content)
            throws NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException,
            IllegalBlockSizeException, UnsupportedEncodingException, InvalidKeyException {
        return decryptDES(key, content,false);
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
    public String md5(String content) {
        return md5(content,SALT_LEVEL_NONE,CONVERT_BYTE2HEX_BITWISE,false);
    }

    public String md5(String content,int saltLevel,Boolean isFixedSalt) {
        return md5(content,saltLevel,CONVERT_BYTE2HEX_BITWISE,isFixedSalt);
    }

    public String md5(String content,int saltLevel,int convertType,Boolean isFixedSalt) {
        String secretContent =null;
        try {
            secretContent = mmdObject.saltEncode( ALGORITHM_MD5,content,convertType,saltLevel,isFixedSalt);
        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return secretContent;
    }

    //SHA-1 加密
    public String sha_1(String content) {
        return sha_1(content,SALT_LEVEL_NONE,CONVERT_BYTE2HEX_BITWISE,false);
    }

    public String sha_1(String content,int saltLevel,Boolean isFixedSalt) {
        return sha_1(content,saltLevel,CONVERT_BYTE2HEX_BITWISE,isFixedSalt);
    }

    public String sha_1(String content,int saltLevel,int convertType,Boolean isFixedSalt) {
        String secretContent =null;
        try {
            secretContent = mmdObject.saltEncode( ALGORITHM_SHA1,content,convertType,saltLevel,isFixedSalt);
        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return secretContent;
    }

    //SHA-256 加密
    public String sha_256(String content) throws NoSuchAlgorithmException {
        return sha_256(content,SALT_LEVEL_NONE,CONVERT_BYTE2HEX_BITWISE,false);
    }

    public String sha_256(String content,int saltLevel,Boolean isFixedSalt) {
        return sha_256(content,saltLevel,CONVERT_BYTE2HEX_BITWISE,isFixedSalt);
    }

    public String sha_256(String content,int saltLevel,int convertType,Boolean isFixedSalt) {
        String secretContent =null;
        try {
            secretContent = mmdObject.saltEncode( ALGORITHM_SHA256,content,convertType,saltLevel,isFixedSalt);
        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return secretContent;
    }

    //**********************************************************

    public String getCharset(){
        return charset;
    }

    public void setCharset(String charset){
        this.charset=charset;
    }

    /**
     * 获取对称加密的最终key
     */
    public String getFinalKey(){
        return scObject.finalKey;
    }
    
}
