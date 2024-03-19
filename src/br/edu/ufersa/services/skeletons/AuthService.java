package br.edu.ufersa.services.skeletons;

import java.rmi.Remote;
import java.rmi.RemoteException;

import br.edu.ufersa.entities.User;
import br.edu.ufersa.utils.UserType;

public interface AuthService extends Remote {

    User authUser(String login, String password) throws RemoteException;
    boolean logout(String login) throws RemoteException;
    void recordClient(String login, String password, UserType type) throws RemoteException;
    
}
