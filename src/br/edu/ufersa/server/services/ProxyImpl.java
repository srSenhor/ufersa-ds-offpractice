package br.edu.ufersa.server.services;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.util.concurrent.ConcurrentHashMap;

import br.edu.ufersa.client.Client;
import br.edu.ufersa.entities.Message;
import br.edu.ufersa.server.GatewayServer;
// import br.edu.ufersa.server.services.skeletons.AuthService;
import br.edu.ufersa.server.services.skeletons.DealerService;
import br.edu.ufersa.server.services.skeletons.Proxy;
import br.edu.ufersa.utils.ServicePorts;

public class ProxyImpl implements Proxy {

    private class ClientAddress {
        
        private String ip;
        private int port;

        public ClientAddress(String ip, int port) { 
            this.ip = ip;
            this.port = port;
        }

        // public String getIp() {
        //     return ip;
        // }

        // public int getPort() {
        //     return port;
        // }

        @Override
        public String toString() {
            return ip + ":" + port;
        }
    }

    // private AuthService stubAuth;
    private DealerService stubDealer;
    private ConcurrentHashMap<ClientAddress, Integer> suspiciousClient;
    private ConcurrentHashMap<ClientAddress, Boolean> blockedClient;
    private int port;

    public ProxyImpl(int port) {
        this.blockedClient = new ConcurrentHashMap<>();
        this.suspiciousClient = new ConcurrentHashMap<>();
        this.port = port;
        this.init();
    }

    @Override
    public Message receive(Client client, int op, Message message) throws RemoteException {
        
        ClientAddress attemptIp = null;
        try {
            attemptIp = new ClientAddress(RemoteServer.getClientHost(), this.port);
        } catch (ServerNotActiveException e) {
            e.printStackTrace();
        }
        System.out.println("PROXY: Verifying if the client " + attemptIp + " is blocked");
        
        if (isBlocked(attemptIp)) {
            System.out.println("PROXY: ERROR! This client has been blocked");
            return null;
        }

        System.out.println("PROXY: Checking if this operation is possible");
        if (GatewayServer.checkOp(client, op)) {
            return stubDealer.receive(client.getUsername(), message);
        } else {
            this.increaseSuspiciousAttempt(attemptIp);
            return null;
        }
        
    }
    
    private void increaseSuspiciousAttempt(ClientAddress address) {
        boolean isBlocked = isBlocked(address);
        
        if (!isBlocked) {
            // TODO Descobrir porque o número de tentativas é sempre 0
            Integer attempts = suspiciousClient.get(address);
    
            if (attempts == null) {
                System.out.println("PROXY: Adding client " + address + " to suspicious list");
                suspiciousClient.put(address, 0);

                attempts = suspiciousClient.get(address);
                System.out.println("Attempts by this client: " + attempts);

            } else if (attempts >= 3) {
                System.out.println("PROXY: Blocking the client " + address);
                suspiciousClient.remove(address);
                blockedClient.put(address, true);
            } else {
                System.out.println("PROXY: Increasing client " + address + " suspiciouness");
                suspiciousClient.put(address, ++attempts);
                blockedClient.put(address, false);
            }
        }
    }

    private boolean isBlocked(ClientAddress address) {
        Boolean isIpBlocked = blockedClient.get(address);
        if (isIpBlocked != null) {
            return isIpBlocked;
        }

        return false;
    }
    
    private void init() {
        try {

            // Registry regAuth = LocateRegistry.getRegistry("localhost", ServicePorts.AUTH_PORT.getValue());
            // stubAuth = (AuthService) regAuth.lookup("Auth");
            Registry regDealer = LocateRegistry.getRegistry("localhost", ServicePorts.DEALER_PORT.getValue());
            stubDealer = (DealerService) regDealer.lookup("Dealer");
            
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }
}