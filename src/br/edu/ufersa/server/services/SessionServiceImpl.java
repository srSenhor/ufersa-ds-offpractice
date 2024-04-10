package br.edu.ufersa.server.services;

import java.rmi.RemoteException;
import java.util.HashMap;

import javax.crypto.SecretKey;

import br.edu.ufersa.entities.SessionLogin;
import br.edu.ufersa.server.services.skeletons.SessionService;
import br.edu.ufersa.utils.RSAKey;

public class SessionServiceImpl implements SessionService {

    private static HashMap<String, RSAKey> session_pukeys;
    private static HashMap<String, SecretKey> session_aes_keys;

    public SessionServiceImpl() {
        session_pukeys = new HashMap<>();
        session_aes_keys = new HashMap<>();
    }
    
    @Override
    public RSAKey getRSAKey(String username) throws RemoteException {
        return session_pukeys.get(username);
    }

    @Override
    public SecretKey getAESKey(String username) throws RemoteException {
        return session_aes_keys.get(username);
    }

    @Override
    public void openSession(String username, SessionLogin login) throws RemoteException {
        session_pukeys.put(username, login.getSessionRSA().getPublicKey());
        session_aes_keys.put(username, login.getAesKey());
    }

    @Override
    public void closeSession(String username) throws RemoteException {
        session_pukeys.remove(username);
        session_aes_keys.remove(username);
    }

}
