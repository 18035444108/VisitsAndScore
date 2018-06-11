package cn.eversec.amc.utils;

import java.security.GeneralSecurityException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * ��ʾ���ṩAES��128λ���㷨���ܺͽ�����ʾ
 * AES 128λ�㷨����Կ��������ƫ����������Ϊ16���ֽڣ���Ҫ��32λ�ַ���ʹ��16����ת���ɶ�����
 */
public class AESUtil2 {

    private static final String AES = "AES";
    private static final String AES_CBC = "AES/CBC/PKCS5Padding";
    private static final char[] DIGITS_LOWER =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    /**
     * ����
     *
     * @param data ��������������
     * @param key  ��������
     * @param iv   ����������ƫ������
     * @return
     */
    public static byte[] encrypt(byte[] data, byte[] key, byte[] iv) {
        return aes(data, key, iv, Cipher.ENCRYPT_MODE);
    }

    /**
     * ����
     *
     * @param encryptedData ���ܺ������
     * @param key           ��������
     * @param iv            ����������ƫ������
     * @return
     */
    public static byte[] decrypt(byte[] encryptedData, byte[] key, byte[] iv) {
        return aes(encryptedData, key, iv, Cipher.DECRYPT_MODE);
    }

    /**
     * ʹ��AES���ܻ�����ޱ����ԭʼ�ֽ�����, �����ޱ�����ֽ�������.
     *
     * @param input ԭʼ�ֽ�����
     * @param key   ����AESҪ�����Կ
     * @param iv    ��ʼ����
     * @param mode  Cipher.ENCRYPT_MODE �� Cipher.DECRYPT_MODE
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
     * Hex����.
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
     * ����AES��Կ,��ѡ����Ϊ128,192,256λ.
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

    public static void main(String[] args) throws Exception {
    	
    	String password = "3518ba028cc65a2d9abb8aed87a371a4";
//    	String password = AESUtil.encodeHexString(AESUtil.generateAesKey(128));
    	System.out.println("password:"+password);
            
        byte[] passwordBinaryArray = AESUtil.decodeHex(password); //passwordBinaryArray ����Ϊ16���ֽ�
        byte[] ivBinaryArray = AESUtil.decodeHex("46183ab12f88d09f54a8eeb278797810");
//        byte[] ivBinaryArray = new byte[16]; //ƫ����
        
        //�����İ���utf-8����ת�ɶ���������
        String data = "[{'name':'163.com'},{'name':'baidu.com'},{'name':'sina.com'}]";//����
//        String data = "{'name:'test.com','registrantName':'����ע��������','registrar':'����ע��������','registrantAddress':'ע������ϵ��ַ'}";
        byte[] dataArray = data.getBytes("utf-8");

        //����
        byte[] encryptedData = AESUtil.encrypt(dataArray, passwordBinaryArray, ivBinaryArray);
        System.out.println("���ܺ������:" + Arrays.toString(encryptedData));
        System.out.println("���ܺ������base64����:" + new String(Base64.encodeBase64(encryptedData)));

        //����
        byte[] decryptedData = AESUtil.decrypt(encryptedData, passwordBinaryArray, ivBinaryArray);
        System.out.println("���ܺ������:" + Arrays.toString(decryptedData));
        System.out.println("���ܺ������:" + new String(decryptedData, "utf-8"));

    }
}
