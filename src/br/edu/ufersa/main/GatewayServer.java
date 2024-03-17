package br.edu.ufersa.main;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import br.edu.ufersa.services.AuthServiceImpl;
import br.edu.ufersa.services.skeletons.AuthService;

public class GatewayServer {

    private static final int AUTH_PORT = 60000;

    public static void main(String[] args) {

        try {
            
            AuthServiceImpl remoteObjRef = new AuthServiceImpl();
            AuthService skeleton = (AuthService) UnicastRemoteObject.exportObject(remoteObjRef, 0);

            LocateRegistry.createRegistry( AUTH_PORT );
            Registry reg = LocateRegistry.getRegistry( AUTH_PORT );
            reg.bind("Auth", skeleton);

            System.out.println("Server is running now: ");

        } catch (Exception e) {
            System.err.println("An error has ocurred in server: " + e.getMessage());
            e.printStackTrace();
        }

    }

}
