package br.edu.ufersa.security;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class SecurityCipher {

    private AESImpl aes;
    private SecretKey skey;

    public SecurityCipher() throws NoSuchAlgorithmException {
        this.aes = new AESImpl(192);
        this.skey = aes.getKey();
    }

    public SecurityCipher(SecretKey skey) throws NoSuchAlgorithmException {
        this.aes = new AESImpl(skey);
        this.skey = skey;
    }

    public SecretKey getKey() {
        return skey;
    }
    
    public String enc(String message) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException { 
        return aes.encrypt(message); 
    }

    public String dec(String message) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException { 
        return aes.decrypt(message);
    }

    public String genHash(String message) {
        return HMACImpl.hMac(skey.toString(), message);
    }
}
