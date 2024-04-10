package br.edu.ufersa.security;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import br.edu.ufersa.utils.ByHexConversor;

public class HMACImpl {

    private static final String ALG = "HmacSHA256";

    public static String hMac(String key, String message) {

        String result = "";

        try {
            
            Mac shaHMAC = Mac.getInstance(ALG);
            SecretKeySpec keyHMAC = new SecretKeySpec(key.getBytes("UTF-8"), ALG);
    
            shaHMAC.init(keyHMAC);
    
            byte[] bytesHMAC = shaHMAC.doFinal(message.getBytes("UTF-8"));
            result = ByHexConversor.convert(bytesHMAC);

        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }
    
}
