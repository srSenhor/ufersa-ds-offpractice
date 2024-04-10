package br.edu.ufersa.security;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class AESImpl {

    private static final String ALG = "AES/ECB/PKCS5Padding";
    private KeyGenerator keygen;
    private SecretKey skey;
    private String message;
    private String encryptedMessage;

    public AESImpl(int keySize) throws NoSuchAlgorithmException {
        this.generateKey();
    }

    public AESImpl(SecretKey skey) throws NoSuchAlgorithmException {
        this.skey = skey;
    }

    public SecretKey getKey() {
        return this.skey;
    }

    private void generateKey() throws NoSuchAlgorithmException {
        keygen = KeyGenerator.getInstance("AES");
        skey = keygen.generateKey();
    }

    public String encrypt(String message) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

        byte[] encryptedMessageBytes;
        Cipher cipher;

        this.message = message;

        cipher = Cipher.getInstance(ALG);
        cipher.init(Cipher.ENCRYPT_MODE, skey);
    
        encryptedMessageBytes = cipher.doFinal(this.message.getBytes());

        this.encryptedMessage = code(encryptedMessageBytes);
        return this.encryptedMessage;
    }

    public String decrypt(String message) throws InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {

        byte[] encryptedMessageBytes = decode(message);
        
        Cipher decipher = Cipher.getInstance(ALG);
        decipher.init(Cipher.DECRYPT_MODE, skey);
        
        byte[] decryptedMessageBytes = decipher.doFinal(encryptedMessageBytes);
        String decryptedMessage = new String(decryptedMessageBytes);
        
        this.message = decryptedMessage;
        return this.message;
    }

    private byte[] decode(String codedMessage) {
        return Base64.getDecoder().decode(codedMessage);
    }
    private String code(byte[] encryptedMessage) {
        return Base64.getEncoder().encodeToString(encryptedMessage);
    }


}

