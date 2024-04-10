package br.edu.ufersa.security;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.SecureRandom;

import br.edu.ufersa.utils.RSAKey;

public class RSAImpl implements Serializable {

    private static final long serialVersionUID = 1L;
    private RSAKey puKey;
    private RSAKey prKey;

    public RSAImpl() {
        this.init();    
    }
    
    // Vamos revisar o passo a passo do RSA

    private void init() {

        // Passo 1: Sortear números primos grandes (BigInteger) aleatórios p e q
        BigInteger p = BigInteger.probablePrime(1024, new SecureRandom());
        BigInteger q = p.nextProbablePrime();
        
        // Passo 2: Calcular o módulo n, onde n = p * q
        BigInteger n = p.multiply(q);

        // Passo 3: Calcular phi de Euler de n, considerando que phi(n) = (p-1) * (q-1)
        BigInteger phi = phi(p, q);

        // Passo 4: Calcular o expoente e (chave pública) tal que mdc(e, phi(n)) = 1 e 1 < e < phi(n)
        BigInteger e = generateE(phi);
        
        // Passo 5: Calcular o expoente d (chave privada) tal que (d * e) mod phi(n) = 1
        BigInteger d = e.modInverse(phi);

        // Passo 6: Compôr as chaves pública e privada com o módulo n e os expoentes e e d
        puKey = new RSAKey(e, n);
        prKey = new RSAKey(d, n);

    }


    // Passo 7 (cifragem): Char cifrado = (char mensagem)^e mod n
    public String encrypt(String message, RSAKey puKey){
        StringBuilder encrypted_message = new StringBuilder();

        // TODO: possivelmente melhorar essa forma de conversão
        for (char ch : message.toCharArray()) {
            int i = ch;
            BigInteger m = new BigInteger(Integer.toString(i));
            BigInteger c = m.modPow(puKey.getFactor1(), puKey.getFactor2());
            encrypted_message.append(c + " ");
        }

        return encrypted_message.toString();
    }

    // Passo 8 (decifragem): Char decifrado = (char cifrado)^d mod n
    public String decrypt(String message){
        StringBuilder decrypted_message = new StringBuilder();

        String chars[] = message.split(" ");
        for (String ch : chars) {
            BigInteger c = new BigInteger(ch);
            BigInteger m = c.modPow(this.prKey.getFactor1(), this.prKey.getFactor2());
            char v = (char) m.intValue();
            decrypted_message.append(v);
        }

        return decrypted_message.toString();
    }

    public String sign(String message) {
        StringBuilder encrypted_message = new StringBuilder();

        // TODO: possivelmente melhorar essa forma de conversão
        for (char ch : message.toCharArray()) {
            int i = ch;
            BigInteger m = new BigInteger(Integer.toString(i));
            BigInteger c = m.modPow(this.prKey.getFactor1(), this.prKey.getFactor2());
            encrypted_message.append(c + " ");
        }

        return encrypted_message.toString();
    }

    public String checkSign(String message, RSAKey puKey) {
        StringBuilder decrypted_message = new StringBuilder();

        String chars[] = message.split(" ");
        for (String ch : chars) {
            BigInteger c = new BigInteger(ch);
            BigInteger m = c.modPow(puKey.getFactor1(), puKey.getFactor2());
            char v = (char) m.intValue();
            decrypted_message.append(v);
        }

        return decrypted_message.toString();
    }

    public RSAKey getPublicKey() {
        return this.puKey;
    }

    private static BigInteger phi(BigInteger p, BigInteger q) {
        return p.subtract(new BigInteger("1")).multiply(q.subtract(new BigInteger("1")));
    }

    private static BigInteger generateE(BigInteger phi){
        BigInteger e = new BigInteger("2");
        BigInteger mdc = e.gcd(phi);
        
        // TODO: possivelmente melhorar essa geração
        do {
            if (e.isProbablePrime(1)) {
                mdc = e.gcd(phi);
            }
            e = e.add(BigInteger.ONE);
        } while (mdc.compareTo(new BigInteger("1")) != 0 && e.compareTo(phi) < 0);
        
        return e.subtract(BigInteger.ONE);     
    }
}