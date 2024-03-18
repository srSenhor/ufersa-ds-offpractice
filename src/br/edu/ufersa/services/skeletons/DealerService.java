package br.edu.ufersa.services.skeletons;

import java.rmi.Remote;
import java.rmi.RemoteException;

import br.edu.ufersa.utils.CarType;

public interface DealerService extends Remote {

    String searchByName(String name) throws RemoteException;
    String searchByRenavam(long renavam) throws RemoteException;
    String list() throws RemoteException;
    String listByCategory() throws RemoteException;
    String add(CarType categoria, long renavam, String nome, int ano_fab, float preco) throws RemoteException;
    String remove(long renavam) throws RemoteException;
    String update(CarType categoria, long renavam, String nome, int ano_fab, float preco) throws RemoteException;

}
