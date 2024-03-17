package br.edu.ufersa.services.skeletons;

import java.rmi.Remote;
import java.rmi.RemoteException;

import br.edu.ufersa.utils.UserType;

public interface AuthService extends Remote {

    UserType authUser(String login, String password) throws RemoteException;
    void recordClient(String login, String password, UserType type) throws RemoteException;
    
}
