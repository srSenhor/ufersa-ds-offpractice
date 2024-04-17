package br.edu.ufersa.server.services.skeletons;

import java.rmi.Remote;
import java.rmi.RemoteException;

import br.edu.ufersa.client.Client;
import br.edu.ufersa.entities.Message;

public interface Proxy extends Remote {
    Message receive(Client client, int op, Message message) throws RemoteException;
    // SessionLogin auth(String username, String password) throws RemoteException;
    // SessionLogin signup(String username, String password, UserType type) throws RemoteException;
    // boolean logout(SessionLogin login) throws RemoteException;   
}
