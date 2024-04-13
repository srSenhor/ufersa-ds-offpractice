package br.edu.ufersa.server.services;

import java.rmi.RemoteException;

import br.edu.ufersa.server.services.skeletons.DealerService;
import br.edu.ufersa.utils.GUI;

public class ThreadBuy implements Runnable {

    private DealerService stub;
    private String name;
    private String cars_available;

    public ThreadBuy(DealerService stub, String name, String cars_available) {
        this.stub = stub;
        this.name = name;
        this.cars_available = cars_available;
    }

    // TODO: Melhorar a visualização da tela
    @Override
    public void run() {
        try {

            do {
                GUI.clearScreen();
                // TODO: arrumar isso pra ele conseguir exibir de novo
                // cars_available = stub.searchByName(name);
                
                System.out.println("""
                    This cars are available
                    """);
                    
                System.out.println(cars_available);
                   
                System.out.print("""
                        Please enter renavam and your password to complete the purchase
                        
                        Renavam: """);
                        
                Thread.sleep(10000l);
            } while (!cars_available.equals("This car isn't available"));
                        
        } catch (InterruptedException e) {
            e.printStackTrace();
        // } catch (RemoteException e) {
        //     e.printStackTrace();
        }

    }
}
