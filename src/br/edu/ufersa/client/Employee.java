package br.edu.ufersa.client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import br.edu.ufersa.entities.SessionLogin;
import br.edu.ufersa.server.services.skeletons.DealerService;
import br.edu.ufersa.utils.GUI;
import br.edu.ufersa.utils.ServicePorts;

public class Employee extends Client {

    private Scanner cin = new Scanner(System.in);

    public Employee() {}

    public Employee(SessionLogin login) {
        this.login = login;
        this.exec();
    }
    
    // TODO: Reescrever para funcionar com os requests
    @Override
    protected void exec(){

        int op = 0;

        try {

            Registry reg = LocateRegistry.getRegistry("localhost", ServicePorts.DEALER_PORT.getValue());
            this.dealerStub = (DealerService) reg.lookup("Dealer");
            
            do {
                GUI.clearScreen();
                GUI.employeeMenu();

                op = cin.nextInt();
                cin.nextLine();

                switch (op) {
                    case 1:
                        GUI.searchOps();
                        int searchOption = cin.nextInt();
                        cin.nextLine();

                        if (searchOption == 1) {
                            System.out.print("Renavam: ");
                            long renavam = cin.nextLong();
                            cin.nextLine();

                            send(op, 0, login.getUsername(), renavam, null, -1, -1, searchOption);

                        } else {
                            System.out.print("Name: ");
                            String name = cin.nextLine();

                            send(op, 0, login.getUsername(), -1, name, -1, -1, searchOption);
                        }
                        
                        System.out.println("Press any key to continue...");
                        cin.nextLine();
                        break;
                    case 2:
                        GUI.listOps();
                        int listOption = cin.nextInt();
                        cin.nextLine();

                        send(op, 0, login.getUsername(), -1L, null, -1, -1.0f, listOption);
                        
                        System.out.println("Press any key to continue...");
                        cin.nextLine();
                        break;
                    case 3:
                        GUI.stockOps();
                        int stockOption = cin.nextInt();
                        cin.nextLine();

                        send(op, 0, login.getUsername(), -1L, null, -1, -1.0f, stockOption);
                        
                        System.out.println("Press any key to continue...");
                        cin.nextLine();
                        break;
                    case 4:
                        GUI.buyOps();
                        String name = cin.nextLine();

                        send(1, 0, login.getUsername(), -1, name, -1, -1, 2);

                        // TODO: Recriar o threadbuy pra ficar printando os carros disponíveis
                        
                        System.out.print("Renavam: ");
                        long renavam = cin.nextLong();
                        cin.nextLine();
                        
                        System.out.println("Are you sure you want to buy this car? [y] for yes / [n] for no");
                        String confirm = cin.next(); 

                        if (confirm.toLowerCase().charAt(0) == 'y') {
                            send(op, 0, login.getUsername(), renavam, name, -1, -1, -1);
                            cin.nextLine();
                        } else {
                            System.err.println("Cancelled operation");
                            cin.nextLine();
                        }

                        System.out.println("Press any key to continue...");
                        cin.nextLine();
                        break;
                    case 5:
                        System.out.print("Renavam: ");
                        renavam = cin.nextLong();
                        cin.nextLine();
                        
                        System.out.print("Nome: ");
                        name = cin.nextLine();
                        
                        System.out.print("Ano de Fabricacao: ");
                        int fab = cin.nextInt();
                        cin.nextLine();
                        
                        System.out.print("Preco: ");
                        float price = cin.nextFloat();
                        cin.nextLine();
                        
                        GUI.categoryOps();
                        int cat = cin.nextInt();
                        cin.nextLine();
                        
                        send(op, 1, login.getUsername(), renavam, name, fab, price, cat);

                        System.out.println("Press any key to continue...");
                        cin.nextLine();
                        break;
                    case 6:
                        System.out.print("Renavam: ");
                        renavam = cin.nextLong();
                        cin.nextLine();
                        
                        System.out.print("Nome: ");
                        name = cin.nextLine();
                        
                        System.out.print("Ano de Fabricacao: ");
                        fab = cin.nextInt();
                        cin.nextLine();
                        
                        System.out.print("Preco: ");
                        price = cin.nextFloat();
                        cin.nextLine();
                        
                        GUI.categoryOps();
                        cat = cin.nextInt();
                        cin.nextLine();
                        
                        send(op, 1, login.getUsername(), renavam, name, fab, price, cat);

                        System.out.println("Press any key to continue...");
                        cin.nextLine();
                        break;
                    case 7:
                        System.out.print("Renavam: ");
                        renavam = cin.nextLong();
                        cin.nextLine();
                        
                        System.out.println("You're trying to remove this car:\n");
                        
                        send(1, 0, login.getUsername(), renavam, null, -1, -1, 1);

                        System.out.println("Are you sure you want to buy this car? [y] for yes / [n] for no");
                        confirm = cin.next(); 

                        if (confirm.toLowerCase().charAt(0) == 'y') {
                            send(op, 1, login.getUsername(), renavam, null, -1, -1.0f, -1);
                            cin.nextLine();
                        } else {
                            System.err.println("Cancelled operation");
                            cin.nextLine();
                        }

                        System.out.println("Press any key to continue...");
                        cin.nextLine();
                        break;
                    case 8:
                        System.out.println("bye my friend!");
                        break;
                    default:
                        System.err.println("undefined operation");
                        break;
                }
            } while(op != 8);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cin.close();
        }

    }

    // protected void update(DealerService stub){

    //     try {
            
    //         System.out.print("Renavam: ");
    //         long renavam = cin.nextLong();
    //         cin.nextLine();
            
    //         System.out.print("Nome: ");
    //         String nome = cin.nextLine();
            
    //         System.out.print("Ano de Fabricacao: ");
    //         int ano_fab = cin.nextInt();
    //         cin.nextLine();
            
    //         System.out.print("Preco: ");
    //         float preco = cin.nextFloat();
    //         cin.nextLine();
            
    //         GUI.categoryOps();
    //         int cat = cin.nextInt();
    //         cin.nextLine();
            
    //         CarType categoria = null;
            
    //         switch (cat) {
    //             case 1:
    //                 categoria = CarType.ECONOMY;    
    //                 break;
    //             case 2:
    //                 categoria = CarType.INTERMEDIATE;        
    //                 break;
    //             case 3:
    //                 categoria = CarType.EXECUTIVE;        
    //                 break;
    //             default:
    //                 break;
    //         }

    //         System.out.println(stub.update(categoria, renavam, nome, ano_fab, preco));
    //         cin.nextLine();

    //     } catch (RemoteException e) {
    //         e.printStackTrace();
    //     }        
    // }

}
