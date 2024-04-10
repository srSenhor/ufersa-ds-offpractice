package br.edu.ufersa.client;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import br.edu.ufersa.entities.User;
import br.edu.ufersa.server.services.ThreadBuy;
import br.edu.ufersa.server.services.skeletons.DealerService;
import br.edu.ufersa.utils.GUI;
import br.edu.ufersa.utils.ServicePorts;

public class Client {

    private final Scanner cin = new Scanner(System.in);

    public Client() {};

    public Client(User user) {
        this.exec(user);
        
    }

    // TODO: Reescrever para funcionar com os requests
    protected void exec(User user) {

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
                        buy(stub, user);
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
                    cin.nextLine();
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
                    cin.nextLine();
                    break;
            }
        } catch (RemoteException e) {
                e.printStackTrace();
        }
    }
    
    protected void checkStock(DealerService stub){
        GUI.stockOps();
        int op = cin.nextInt();
        cin.nextLine();

        try {
            switch (op) {
                case 1:
                    System.out.println(stub.stock());
                    cin.nextLine();
                    break;
                case 2:
                    System.out.println(stub.stockByName());
                    cin.nextLine();
                    break;
                default:
                    System.err.println("undefined operation");
                    cin.nextLine();
                    break;
            }
        } catch (RemoteException e) {
                e.printStackTrace();
        }    
    }

    protected void buy(DealerService stub, User user){

        GUI.buyOps();
        String name = cin.nextLine();

        String cars_available = "";
        Thread t0 = new Thread(new ThreadBuy(stub, name, cars_available));
        t0.start();

        try {
            
            if (cars_available.equals("This car isn't available")) {
                System.out.println(cars_available);
                return;
            }
            
            long renavam = cin.nextLong();
            cin.nextLine();

            System.out.print("Password: ");
            String attempt_password = cin.nextLine();

            if (attempt_password.equals(user.getPassword())) {
                System.out.println(stub.buy(renavam, user));
                cin.nextLine();
            } else {
                System.err.println("wrong password...");
                cin.nextLine();
            }

            t0.interrupt();
            
        } catch (RemoteException e) {
                e.printStackTrace();
        }
        
    }
}
