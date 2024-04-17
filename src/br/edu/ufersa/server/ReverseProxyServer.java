package br.edu.ufersa.server;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import br.edu.ufersa.server.services.ProxyImpl;
import br.edu.ufersa.server.services.skeletons.Proxy;
import br.edu.ufersa.utils.GUI;
import br.edu.ufersa.utils.ServicePorts;

public class ReverseProxyServer {

    /*
     * Esse server deve rodar entre o cliente e o servidor gateway
     * O cliente envia solicitação à esse server proxy
     * Server proxy envia ao firewall do server gateway
     * firewall faz as devidas verificações e repassa para o gateway, que por sua vez responde às solicitações
     
     TODO Implementar o server proxy com RMI e substituir todas as solicitações pra passar pelo server invés dos métodos
     TODO Usar o mesmo nome de método, só mudar que pede a instancia da classe pra verificar o nível de acesso do cliente
     TODO Implementar uma camada de firewall que fica entre o server de proxy e o gateway, verificando quem está usando o serviço
     */

    public static void main(String[] args) {
            GUI.clearScreen();     

            ProxyImpl proxyObjRef = new ProxyImpl(ServicePorts.DATABASE_PORT.getValue());
            Proxy proxySkeleton;

            System.out.println("Proxy server is running now: ");

            try {

                proxySkeleton = (Proxy) UnicastRemoteObject.exportObject(proxyObjRef, 0);
                LocateRegistry.createRegistry( ServicePorts.PROXY_PORT.getValue() );
                Registry sessionReg = LocateRegistry.getRegistry( ServicePorts.PROXY_PORT.getValue() );
                sessionReg.bind("Proxy", proxySkeleton);        
                
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (AlreadyBoundException e) {
                e.printStackTrace();
            }         
    }

}
