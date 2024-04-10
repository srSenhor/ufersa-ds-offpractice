package br.edu.ufersa.entities;

import java.io.Serializable;
import javax.crypto.SecretKey;

import br.edu.ufersa.security.RSAImpl;
import br.edu.ufersa.utils.RSAKey;

public class SessionLogin implements Serializable{

    private static final long serialVersionUID = 1L;
    private String login;
    private RSAImpl sessionRSA;
    private RSAKey serverPuKey;
    private SecretKey aesKey;

    public SessionLogin(String login, RSAImpl sessionRSA, RSAKey serverPuKey, SecretKey sKey) {
        this.login = login;
        this.setSessionRSA(sessionRSA);
        this.setSKey(sKey);
        this.setServerPuKey(serverPuKey);
    }

    private void setSessionRSA(RSAImpl sessionRSA) {
        if (sessionRSA != null) {
            this.sessionRSA = sessionRSA;
        } else {
            throw new RuntimeException("Erro ao inicializar RSA da sessão");
        }
    }

    public void setServerPuKey(RSAKey serverPuKey) {
        if (serverPuKey != null) {
            this.serverPuKey = serverPuKey;
        } else {
            throw new RuntimeException("Erro ao recuperar chave pública do servidor");
        }
    }

    private void setSKey(SecretKey sKey) {
        if (sKey != null) {
            this.aesKey = sKey;
        } else {
            throw new RuntimeException("Erro ao inicializar chave do AES da sessão");
        }
    }

    public String getLogin() {
        return login;
    }

    public RSAImpl getSessionRSA() {
        return sessionRSA;
    }

    public RSAKey getServerPuKey() {
        return serverPuKey;
    }

    public SecretKey getAesKey() {
        return aesKey;
    }

    // Para fins de debug
    @Override
    public String toString() {
        return "SessionLogin [login=" + login + ", sessionRSA=" + sessionRSA + ", serverPuKey=" + serverPuKey
                + ", aesKey=" + aesKey + "]";
    }

}
