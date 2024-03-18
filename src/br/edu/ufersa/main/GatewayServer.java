package br.edu.ufersa.main;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import br.edu.ufersa.services.AuthServiceImpl;
import br.edu.ufersa.services.DealerServiceImpl;
import br.edu.ufersa.services.skeletons.AuthService;
import br.edu.ufersa.services.skeletons.DealerService;
import br.edu.ufersa.utils.ServicePorts;

public class GatewayServer {

    public static void main(String[] args) {

        try {
            
            AuthServiceImpl authObjRef = new AuthServiceImpl();
            AuthService authSkeleton = (AuthService) UnicastRemoteObject.exportObject(authObjRef, 0);

            LocateRegistry.createRegistry( ServicePorts.AUTH_PORT.getValue() );
            Registry authReg = LocateRegistry.getRegistry( ServicePorts.AUTH_PORT.getValue() );
            authReg.bind("Auth", authSkeleton);

            DealerServiceImpl dealerObjRef = new DealerServiceImpl();
            DealerService dealerSkeleton = (DealerService) UnicastRemoteObject.exportObject(dealerObjRef, 0);

            LocateRegistry.createRegistry( ServicePorts.DEALER_PORT.getValue() );
            Registry dealerReg = LocateRegistry.getRegistry( ServicePorts.DEALER_PORT.getValue() );
            dealerReg.bind("Dealer", dealerSkeleton);

            System.out.println("Server is running now: ");

        } catch (Exception e) {
            System.err.println("An error has ocurred in server: " + e.getMessage());
            e.printStackTrace();
        }

    }

}
