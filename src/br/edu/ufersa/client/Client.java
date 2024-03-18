package br.edu.ufersa.client;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import br.edu.ufersa.services.skeletons.DealerService;
import br.edu.ufersa.utils.GUI;
import br.edu.ufersa.utils.ServicePorts;

public class Client {

    private final Scanner cin = new Scanner(System.in);

    public Client() {};

    public Client(boolean execute) {
        if (execute) {
            this.exec();
        } else {
            System.err.println("What do you want ? =P");
        }

    }

    protected void exec() {

        int op = 0;

        try {

            Registry reg = LocateRegistry.getRegistry("localhost", ServicePorts.DEALER_PORT.getValue());
            DealerService stub = (DealerService) reg.lookup("Dealer");
            
            do {
                GUI.clearScreen();
                GUI.clientMenu();

                op = cin.nextInt();
                cin.nextLine();

                switch (op) {
                    case 1:
                        search(stub);
                        break;
                    case 2:
                        list(stub);
                        break;     
                    case 3:
                        checkStock(stub);
                        break; 
                    case 4:
                        buy(stub);
                        break;
                    case 5:
                        System.out.println("bye my friend!");
                        break;
                    default:
                        System.err.println("undefined operation");
                        break;
                }
            } while(op != 5);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cin.close();
        }

    }

    protected void search(DealerService stub){

        GUI.searchOps();
        int op = cin.nextInt();
        cin.nextLine();

        try {
            switch (op) {
                case 1:
                    System.out.print("Renavam: "); 
                    long renavam = cin.nextLong();
                    cin.nextLine();
                    
                    System.out.println(stub.searchByRenavam(renavam));
                    cin.nextLine();
                    break;
                case 2:
                    System.out.print("Name: "); 
                    String name = cin.nextLine();
                    
                    System.out.println(stub.searchByName(name));
                    cin.nextLine();
                    break;
                default:
                    System.err.println("undefined operation");
                    break;
            }
        } catch (RemoteException e) {
                e.printStackTrace();
        }
    }

    protected void list(DealerService stub){

        GUI.listOps();
        int op = cin.nextInt();
        cin.nextLine();

        try {
            switch (op) {
                case 1:
                    System.out.println(stub.list());
                    cin.nextLine();
                    break;
                case 2:
                    System.out.println(stub.listByCategory());
                    cin.nextLine();
                    break;
                default:
                    System.err.println("undefined operation");
                    break;
            }
        } catch (RemoteException e) {
                e.printStackTrace();
        }
    }
    
    protected void checkStock(DealerService stub){
        System.out.println("Coming soon...");
    }

    protected void buy(DealerService stub){
        System.out.println("Coming soon...");
    }
}
