package br.edu.ufersa.server.services.skeletons;

import java.rmi.Remote;
import java.rmi.RemoteException;

import br.edu.ufersa.entities.Message;
import br.edu.ufersa.utils.RSAKey;

public interface DealerService extends Remote {

    // TODO: Deixar só o processo que vai cuidar do request
    // String searchByName(String name) throws RemoteException;
    // String searchByRenavam(long renavam) throws RemoteException;
    // String list() throws RemoteException;
    // String listByCategory() throws RemoteException;
    // String add(CarType categoria, long renavam, String nome, int ano_fab, float preco) throws RemoteException;
    // String remove(long renavam) throws RemoteException;
    // String update(CarType categoria, long renavam, String nome, int ano_fab, float preco) throws RemoteException;
    // String stock() throws RemoteException;
    // String stockByName() throws RemoteException;
    // String buy(long renavam, User user) throws RemoteException;


    // TODO: passar o ip do client junto pra testar no firewall
    Message receive(String username, Message message) throws RemoteException;
    // SessionLogin record(String className, String pass, String cpf, String name, String addr, String phone) throws RemoteException;
    RSAKey getPuKey() throws RemoteException;

}
