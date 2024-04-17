package br.edu.ufersa.server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import br.edu.ufersa.client.Client;
import br.edu.ufersa.server.services.AuthServiceImpl;
import br.edu.ufersa.server.services.DealerServiceImpl;
import br.edu.ufersa.server.services.SessionServiceImpl;
import br.edu.ufersa.server.services.skeletons.AuthService;
import br.edu.ufersa.server.services.skeletons.DealerService;
import br.edu.ufersa.server.services.skeletons.SessionService;
import br.edu.ufersa.utils.GUI;
import br.edu.ufersa.utils.ServicePorts;

public class GatewayServer {
    // TODO: Implementar firewall

    private class Firewall {
        public static boolean checkUserOperation(Client client, int operation) {
            if (client.getUserType() == 0 && (operation > 0 && operation < 5)) {
                return true;
            } else if (client.getUserType() == 1 && (operation > 0 && operation < 7)) {
                return true;
            } else {
                return false;
            }
        } 
    }

    public static boolean checkOp(Client client, int op) { return Firewall.checkUserOperation(client, op); }
    public static void main(String[] args) {
        try {
            
            SessionServiceImpl sessionObjRef = new SessionServiceImpl();
            SessionService sessionSkeleton = (SessionService) UnicastRemoteObject.exportObject(sessionObjRef, 0);
            
            LocateRegistry.createRegistry( ServicePorts.SESSION_PORT.getValue() );
            Registry sessionReg = LocateRegistry.getRegistry( ServicePorts.SESSION_PORT.getValue() );
            sessionReg.bind("Session", sessionSkeleton);
            
            DealerServiceImpl dealerObjRef = new DealerServiceImpl();
            DealerService dealerSkeleton = (DealerService) UnicastRemoteObject.exportObject(dealerObjRef, 0);
            
            LocateRegistry.createRegistry( ServicePorts.DEALER_PORT.getValue() );
            Registry dealerReg = LocateRegistry.getRegistry( ServicePorts.DEALER_PORT.getValue() );
            dealerReg.bind("Dealer", dealerSkeleton);
            
            AuthServiceImpl authObjRef = new AuthServiceImpl();
            AuthService authSkeleton = (AuthService) UnicastRemoteObject.exportObject(authObjRef, 0);
            
            LocateRegistry.createRegistry( ServicePorts.AUTH_PORT.getValue() );
            Registry authReg = LocateRegistry.getRegistry( ServicePorts.AUTH_PORT.getValue() );
            authReg.bind("Auth", authSkeleton);
            
            GUI.clearScreen();
            System.out.println("Server is running now: ");

        } catch (Exception e) {
            System.err.println("An error has ocurred in server: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
