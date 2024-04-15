package br.edu.ufersa.server.services.skeletons;

import java.rmi.Remote;
import java.rmi.RemoteException;

import br.edu.ufersa.entities.Message;
import br.edu.ufersa.utils.RSAKey;

public interface DealerService extends Remote {
    // TODO: passar o ip do client junto pra testar no firewall
    Message receive(String username, Message message) throws RemoteException;
    RSAKey getPuKey() throws RemoteException;
}
