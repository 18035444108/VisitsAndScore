package cn.eversec.amc.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Arrays;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

/**
 * 本示例提供秘钥对（公钥、私钥）加密解密演示
 * 公钥、私钥属于非对称加密算法，AES属于对称加密算法，
 * 非对称加密的的特点就是，公钥加密只有私钥才能解密、私钥加密只有公钥才能解密
 */
public class CertificateEncrypt2 {

    /**
     * 使用私钥加密，使用公钥解密
     *
     * @param privateKey 私钥
     * @param data	待加密明文
     * @return 加密后的数据
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(PrivateKey privateKey, byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] cipherText = cipher.doFinal(data);
        return cipherText;
    }

    /**
     * 使用公钥加密，使用私钥才能解密
     *
     * @param publicKey 公钥
     * @param data	待加密明文
     * @return 加密后的数据
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(PublicKey publicKey, byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] cipherText = cipher.doFinal(data);
        return cipherText;
    }

    /**
     * 使用公钥解密，只能解私钥加密后的内容
     *
     * @param publicKey     公钥
     * @param encryptedData 私钥加密后的数据
     * @return 解密后的明文数据
     * @throws Exception
     */
    public static byte[] decryptByPublicKey(PublicKey publicKey, byte[] encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] plainText = cipher.doFinal(encryptedData);
        return plainText;
    }


    /**
     * 使用私钥解密，只能解公钥加密后的内容
     *
     * @param privateKey    私钥
     * @param encryptedData 公钥加密后的数据
     * @return 解密后的明文数据
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(PrivateKey privateKey, byte[] encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] plainText = cipher.doFinal(encryptedData);
        return plainText;
    }

    /**
     * 签名
     * @param privateKey 私钥
     * @param data 待签名数据（指令明文）
     * @return 签名
     * @throws Exception
     */
    public static byte[] signature(PrivateKey privateKey, byte[] data) throws Exception{
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(data);
        return signature.sign();
    }


    /**
     * 验证签名
     * @param publicKey 公钥
     * @param data 指令明文
     * @param signatureData 签名 （此数据是由私钥签名产生的）
     * @return true 验证签名成功  false 验证签名失败
     * @throws Exception
     */
    public static boolean verifySignature(PublicKey publicKey, byte[] data, byte[] signatureData) throws Exception{
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(signatureData);
    }
    
	
    public static void main(String[] args) throws Exception {
    	
        //加载Jks文件
//        KeyStore keyStore = KeyStore.getInstance("JKS");
//        String jksPassword = "123456"; //jks 密码
//        keyStore.load(new FileInputStream(new File("D:\\demo.jks")), jksPassword.toCharArray());
//        String alias = "sun";//别名
//        String keyPasswork = "123456"; //私钥密码
//        
//        //读取私钥，读取私钥需要使用密码
//        PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, keyPasswork.toCharArray());
//        
//        //AES密码
//        String password = "e956f70ffbe8c204f2661076137e755e";
//        System.out.println("password:"+password);
//
//        //将待加密数据转换为utf-8 二进制数组
//        byte[] passwordData = password.getBytes("utf-8");
//        
//        //使用私钥加密
//        byte[] encryptByPrivateKeyData = CertificateEncrypt.encryptByPrivateKey(privateKey, passwordData);
//        System.out.println("加密后的数据：" + Arrays.toString(encryptByPrivateKeyData));
//        System.out.println("加密后的数据base64编码：" + new String(Base64.encodeBase64(encryptByPrivateKeyData)));
        
        //使用cer公钥解密
        InputStream is = new FileInputStream(new File("D:\\nifa.cer"));
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Certificate cerCert = cf.generateCertificate(is);
        PublicKey publicKey1 = cerCert.getPublicKey();
        
        String aa = "Gq2mCH0dXrHIkuRzLU8fqX/TBei7e3MvmVVI6B911VL/oyYzNlczWCNKPlpxcxAfMGvFesNosj5PujUJWczyv3CW1nD2CzBQouubqpZej7OvdBWA1P7uZWnKUVW5kRw8RUYuYtU4ilLM/NPeSV3fHTqxuYaTKf5IH0CEIpGc8l0=";
        byte[] databyte = Base64.decodeBase64(aa);
        byte[] decryptByPublicKeyData1 = CertificateEncrypt.decryptByPublicKey(publicKey1, databyte);
        System.out.println("解密后的数据1：" + Arrays.toString(decryptByPublicKeyData1));
        System.out.println("解密后的数据内容1：" + new String(decryptByPublicKeyData1));
        
        String data = "1oXfZgHE4+NYfKrjSn2T80Z6Z/+TvsZz7Lq0uaGqJ0lbNh3bN8ia/XmPHtcvWEcpm7iatT/2ruYRTsVnJgfYasF7UxZSpscKWT3gbj8ek4s=";
        byte[] datas = Base64.decodeBase64(data);
        System.out.println(datas);
        
        byte[] ivBinaryArray = new byte[16]; //偏移量
        //解密
        byte[] decryptedData = AESUtil.decrypt(datas, decryptByPublicKeyData1, ivBinaryArray);
        System.out.println("解密后的内容:" + Arrays.toString(decryptedData));
        System.out.println("解密后的内容:" + new String(decryptedData, "utf-8"));
        
        
        
        //签名及验签
//        String data = "{'name:'test.com','registrantName':'域名注册人名称','registrar':'域名注册商名称','registrantAddress':'注册人联系地址'}";
//        //将指令转换为utf-8 二进制数组
//        byte[] dataB = data.getBytes("utf-8");
//        
//        //对指令进行签名 signatureData为签名数据
//        byte[] signatureData = CertificateEncrypt.signature(privateKey, dataB);
//        System.out.println("签名base64编码：" + new String(Base64.encodeBase64(signatureData)));
//        //验证签名
//        boolean verifyResult = CertificateEncrypt.verifySignature(publicKey1, dataB, signatureData);
//        if (verifyResult) {
//            System.out.println("签名验证通过");
//        } else {
//            System.out.println("签名验证失败");
//        }
        
    }

}
