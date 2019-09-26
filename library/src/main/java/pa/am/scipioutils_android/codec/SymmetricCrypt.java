package pa.am.scipioutils_android.codec;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Class: SymmetricCrypt
 * Description: 对称加密
 * Author: Alan Min
 * Createtime: 2018/6/1
 */
public class SymmetricCrypt extends AbstractCryptUtil {

    protected SecretKey originalKey;

    /**
     * 核心实现方法
     * param algorithm:采用的加密算法
     * param charset:字符集
     * param key: 用户密钥
     * param content: 需要加解密的内容
     * param isRandomKey: 是否采用随机密钥
     * param cryptMode: 加密还是解密
     */
    protected String symmetricCrypt(String algorithm,String charset,String key,
                                      String content,boolean isRandomKey,int cryptMode)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException
    {

        String result;
        //获取原始密钥
        SecretKey original_key =getOriginalKey(isRandomKey,algorithm,key);
        //生成最终密钥
        SecretKey final_key=new SecretKeySpec(original_key.getEncoded(), algorithm);
        //根据密钥生成密码器
        Cipher cipher=Cipher.getInstance(algorithm);
        //初始化密码器
        if(cryptMode==CRYPTMODE_ENCRYPTION)
        {
            cipher.init(Cipher.ENCRYPT_MODE, final_key);
            byte [] byte_content=content.getBytes(charset);
            byte [] byte_encoded=cipher.doFinal(byte_content);//加密
            result=Base64Util.encodeToStr(byte_encoded);//返回Base64编码的加密结果
        }
        else if(cryptMode==CRYPTMODE_DECRYPTION)
        {
            cipher.init(Cipher.DECRYPT_MODE, final_key);
            byte [] byte_content= Base64Util.decodeToBytes(content);//将加密并编码后的内容解码成字节数组
            byte [] byte_decode=cipher.doFinal(byte_content);//解密
            result=new String(byte_decode,charset);
        }
        else
            throw new RuntimeException("Error:the param [cryptMode] is illegal");
        return result;
    }

    //获取原始密钥
    private SecretKey getOriginalKey(boolean isRandomKey,String algorithm,String key)
            throws NoSuchAlgorithmException
    {
        if(this.originalKey==null)
            this.originalKey=generateOriginalKey(isRandomKey,algorithm,key);
        return this.originalKey;
    }

    //生成原始密钥
    private SecretKey generateOriginalKey(boolean isRandomKey,String algorithm,String key)
            throws NoSuchAlgorithmException
    {
        String rule;
        if(isRandomKey)//动态生成规则
            rule=getSaltContent(key,false,false);
        else//静态生成规则
            rule=getSaltContent(key,true,true);//每个字符间插入固定字符，但有层for循环
            //rule=key+"Q1cA@3>";//直接后面加一串固定字符串
        KeyGenerator keygen=KeyGenerator.getInstance(algorithm);//构造构造密钥生成器
        SecureRandom secureRandom=new SecureRandom();
        secureRandom.setSeed(rule.getBytes());

        //初始化密钥生成器
        switch (algorithm)
        {
            case ALGORITHM_AES:
                keygen.init(128, secureRandom);break;
            case ALGORITHM_DESEDE:
                keygen.init(168, secureRandom);break;
            case ALGORITHM_DES:
                keygen.init(56, secureRandom);break;
            default:
                throw new RuntimeException("Error:the param [algorithm] is illegal");
        }
        return keygen.generateKey();
    }

}