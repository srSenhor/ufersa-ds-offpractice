package br.edu.ufersa.server.services.skeletons;

import java.rmi.Remote;
import java.rmi.RemoteException;

import br.edu.ufersa.entities.SessionLogin;
import br.edu.ufersa.utils.UserType;

public interface AuthService extends Remote {

    SessionLogin auth(String login, String password) throws RemoteException;
    boolean logout(SessionLogin login) throws RemoteException;
    void recordClient(String login, String password, UserType type) throws RemoteException;
    
}
