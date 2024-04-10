package br.edu.ufersa.server.services.skeletons;

import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.crypto.SecretKey;

import br.edu.ufersa.entities.SessionLogin;
import br.edu.ufersa.utils.RSAKey;

public interface SessionService extends Remote {

    RSAKey getRSAKey(String username) throws RemoteException;
    SecretKey getAESKey(String username) throws RemoteException;
    void openSession(String username, SessionLogin login) throws RemoteException;
    void closeSession(String username) throws RemoteException;
    

}
