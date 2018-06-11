package cn.eversec.amc.utils;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.golaxy.util.ConfigData;

/**
 * 本示例提供AES（128位）算法加密和解密演示
 * AES 128位算法中秘钥和向量（偏移量）长度为16个字节，需要将32位字符串使用16进制转换成二进制
 */
public class AESUtil {
	private static final Logger logger = Logger.getLogger(AESUtil.class);
	private static final String password=ConfigData.password;
	private static final String userId=ConfigData.userId;
	private static final String offset=ConfigData.offset;
    private static final String AES = "AES";
    private static final String AES_CBC = "AES/CBC/PKCS5Padding";
    private static final char[] DIGITS_LOWER =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    /**
     * 加密
     *
     * @param data 待加密明文数据
     * @param key  加密密码
     * @param iv   加密向量（偏移量）
     * @return
     */
    public static byte[] encrypt(byte[] data, byte[] key, byte[] iv) {
        return aes(data, key, iv, Cipher.ENCRYPT_MODE);
    }

    /**
     * 解密
     *
     * @param encryptedData 加密后的数据
     * @param key           解密密码
     * @param iv            解密向量（偏移量）
     * @return
     */
    public static byte[] decrypt(byte[] encryptedData, byte[] key, byte[] iv) {
        return aes(encryptedData, key, iv, Cipher.DECRYPT_MODE);
    }

    /**
     * 使用AES加密或解密无编码的原始字节数组, 返回无编码的字节数组结果.
     *
     * @param input 原始字节数组
     * @param key   符合AES要求的密钥
     * @param iv    初始向量
     * @param mode  Cipher.ENCRYPT_MODE 或 Cipher.DECRYPT_MODE
     */
    private static byte[] aes(byte[] input, byte[] key, byte[] iv, int mode) {
        try {
            SecretKey secretKey = new SecretKeySpec(key, AES);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance(AES_CBC);
            cipher.init(mode, secretKey, ivSpec);
            return cipher.doFinal(input);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts an array of characters representing hexadecimal values into an array of bytes of those same values. The
     * returned array will be half the length of the passed array, as it takes two characters to represent any given
     * byte. An exception is thrown if the passed char array has an odd number of elements.
     *
     * @param data An array of characters containing hexadecimal digits
     * @return A byte array containing binary data decoded from the supplied char array.
     * @throws Exception Thrown if an odd number or illegal of characters is supplied
     */
    public static byte[] decodeHex(final char[] data) throws Exception {

        final int len = data.length;

        if ((len & 0x01) != 0) {
            throw new Exception("Odd number of characters.");
        }

        final byte[] out = new byte[len >> 1];

        // two characters form the hex value.
        for (int i = 0, j = 0; j < len; i++) {
            int f = toDigit(data[j], j) << 4;
            j++;
            f = f | toDigit(data[j], j);
            j++;
            out[i] = (byte) (f & 0xFF);
        }

        return out;
    }

    /**
     * Hex解码.
     */
    public static byte[] decodeHex(String input) {
        try {
            return decodeHex(input.toCharArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts a hexadecimal character to an integer.
     *
     * @param ch    A character to convert to an integer digit
     * @param index The index of the character in the source
     * @return An integer
     * @throws Exception Thrown if ch is an illegal hex character
     */
    public static int toDigit(final char ch, final int index) throws Exception {
        final int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new Exception("Illegal hexadecimal character " + ch + " at index " + index);
        }
        return digit;
    }

    public static char[] encodeHex(final byte[] data, final char[] toDigits) {
        final int l = data.length;
        final char[] out = new char[l << 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0x0F & data[i]];
        }
        return out;
    }



    public static String encodeHexString(final byte[] data) {
        return new String(encodeHex(data));
    }

    public static char[] encodeHex(final byte[] data){
        return encodeHex(data, DIGITS_LOWER);
    }

    /**
     * 生成AES密钥,可选长度为128,192,256位.
     */
    public static byte[] generateAesKey(int keysize)throws Exception {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(AES);
            keyGenerator.init(keysize);
            SecretKey secretKey = keyGenerator.generateKey();
            return secretKey.getEncoded();
        } catch (GeneralSecurityException e) {
            throw new Exception(e);
        }
    }

    
    /**
     * 获取加密请求体
     * @param cleartext
     * @return
     * @throws UnsupportedEncodingException
     */
    public static JSONObject getEncryptionData(String cleartext) throws UnsupportedEncodingException{
    	JSONObject dataJson = new JSONObject();
        //明文
        byte[] passwordBinaryArray = AESUtil.decodeHex(password); //passwordBinaryArray 长度为16个字节
        byte[] ivBinaryArray = AESUtil.decodeHex(offset);
        //将明文按照utf-8编码转成二进制数组
        String data = cleartext;//明文
        byte[] dataArray = data.getBytes("utf-8");
        //加密
        byte[] encryptedData = AESUtil.encrypt(dataArray, passwordBinaryArray, ivBinaryArray);
        String keyword = new String(Base64.encodeBase64(encryptedData));
        String keywordHash = getSHA256StrJava(data);
        //构造加密的json请求体
        dataJson.put("userId", userId);
        dataJson.put("keyword", keyword);
        dataJson.put("keywordHash", keywordHash);
        dataJson.put("encryptAlgorithm", 1);
        
        return dataJson;
    }
    
    
    /**
     * 获取解密后的返回结果
     * @param dataJson
     * @return
     * @throws Exception
     */
	public static JSONObject getDecodeData(JSONObject dataJson) throws Exception {
		if(dataJson.containsKey("code")){
			if(dataJson.getInteger("code") == 200){
				byte[] passwordBinaryArray = CertificateEncrypt.getPassword(dataJson.getString("password"));
				byte[] ivBinaryArray = new byte[16];
				String data = dataJson.getString("data");
				byte[] encryptedData = Base64.decodeBase64(data);
				byte[] decryptedData = AESUtil.decrypt(encryptedData, passwordBinaryArray, ivBinaryArray);
				JSONObject decodeJson = JSON.parseObject(new String(decryptedData, "utf-8"));
				
				return decodeJson;
			}else{
				logger.error("statusCode:"+dataJson.getInteger("code"));
			}
		}else{
			logger.error("返回结果格式有误？！");
		}
		return null;
	}
	
	 /**
     * 获取批量解密后的返回结果
     * @param dataJson
     * @return
     * @throws Exception
     */
	public static JSONArray getDecodeArrayData(JSONObject dataJson) throws Exception {
		if(dataJson.containsKey("code")){
			if(dataJson.getInteger("code") == 200){
				byte[] passwordBinaryArray = CertificateEncrypt.getPassword(dataJson.getString("password"));
				byte[] ivBinaryArray = new byte[16];
				String data = dataJson.getString("data");
				byte[] encryptedData = Base64.decodeBase64(data);
				byte[] decryptedData = AESUtil.decrypt(encryptedData, passwordBinaryArray, ivBinaryArray);
				JSONArray decodeJsonArray = JSON.parseArray(new String(decryptedData, "utf-8"));
				return decodeJsonArray;
			}else{
				logger.error("statusCode:"+dataJson.getInteger("code"));
			}
		}else{
			logger.error("返回结果格式有误！");
		}
		return null;
	}
	
    
    /**
     * 明文进行SHA256哈希
     */
    public static String getSHA256StrJava(String str){
        MessageDigest messageDigest;
        String encodeStr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            encodeStr = byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodeStr;
    }
    
    
    private static String byte2Hex(byte[] bytes){
        StringBuffer stringBuffer = new StringBuffer();
        String temp = null;
        for (int i=0;i<bytes.length;i++){
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length()==1){
                //1得到一位的进行补0操作
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }
    
    
    
    public static void main(String[] args) throws Exception {
    	
//        //AES解密密码
//        byte[] password = CertificateEncrypt.getPassword("KyymLgMqGFReEIQXYHXYMdwTmOJBUjzCRVCOTtEXZQKcquPpNGoLXvc7xJBs/gMtRu1X5wQVvEbjOJROZdr0XEz/rfuOOF8WwrsjYdAhx5h+G9GcYOGAqTI255SAD0wE6WSUVrb8I6Ae9CocI6VGXX9jiMaFVmGqzqUO8JbHzcY=");
//        System.out.println(password);
//        
//        //解密偏移量
//        byte[] ivBinaryArray = new byte[16];
//        byte[] datas=  Base64.decodeBase64("wN7KslIozdDpYkHfJ7s1ig==");
//        System.out.println(Arrays.toString(datas));
//        byte[] decryptedData = AESUtil.decrypt(datas, password, ivBinaryArray);
//        System.out.println("解密后的内容:" + Arrays.toString(decryptedData));
//        System.out.println("解密后的内容:" + new String(decryptedData, "utf-8"));
////        
//        String batch= "[{'name':'baidu.com'},{'name':'163.com'}]";
//        
//        System.out.println(getEncryptionData("[{'name':'baidu.com'},{'name':'163.com'}]"));
//        System.out.println(getEncryptionData("{'name':'baidu.com'}"));
//        
//        JSONObject dedoce = JSON.parseObject("{\"userId\":\"27cf2011-de8c-420f-a1f8-e37c6a953400\",\"code\":200,\"message\":\"success\",\"data\":\"1oXfZgHE4+NYfKrjSn2T80Z6Z/+TvsZz7Lq0uaGqJ0lbNh3bN8ia/XmPHtcvWEcpm7iatT/2ruYRTsVnJgfYasF7UxZSpscKWT3gbj8ek4s=\",\"password\":\"Gq2mCH0dXrHIkuRzLU8fqX/TBei7e3MvmVVI6B911VL/oyYzNlczWCNKPlpxcxAfMGvFesNosj5PujUJWczyv3CW1nD2CzBQouubqpZej7OvdBWA1P7uZWnKUVW5kRw8RUYuYtU4ilLM/NPeSV3fHTqxuYaTKf5IH0CEIpGc8l0=\",\"sign\":\"nIO4/Sm7kCo/bfKNzDny4glUtmPEcZ1JQ17yUnnou3EvDeWKyPuRBWAQHbZMnlvGKAx/WgGxXd1gp91RZG0Z9ZDVxf1Ip1DfOaoDtc9JuOEKt0G7UjmWHc+QG8rti0UrJnUQFZVXIEkwS3ercp08I/vzrEpdq+mjEhXooqVAEBM=\",\"encryptAlgorithm\":1}");
//        System.out.println(getDecodeData(dedoce));
    }
}
