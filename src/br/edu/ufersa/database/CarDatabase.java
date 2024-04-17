package br.edu.ufersa.database;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import br.edu.ufersa.server.services.DatabaseServiceImpl;
import br.edu.ufersa.server.services.skeletons.DatabaseService;
import br.edu.ufersa.utils.GUI;
import br.edu.ufersa.utils.ServicePorts;

public class CarDatabase {
    public static void main(String[] args) {
        GUI.clearScreen();

        try {

            DatabaseServiceImpl dbObjRef = new DatabaseServiceImpl();
            DatabaseService dbSkeleton = (DatabaseService) UnicastRemoteObject.exportObject(dbObjRef, 0);

            LocateRegistry.createRegistry( ServicePorts.DATABASE_PORT.getValue() );
            Registry dbReg = LocateRegistry.getRegistry( ServicePorts.DATABASE_PORT.getValue() );
            dbReg.bind("Database", dbSkeleton);

            System.out.println("Database is running now: ");

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        }
    }
}
