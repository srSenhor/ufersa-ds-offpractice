package br.edu.ufersa.client;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import br.edu.ufersa.entities.User;
import br.edu.ufersa.server.services.skeletons.DealerService;
import br.edu.ufersa.utils.CarType;
import br.edu.ufersa.utils.GUI;
import br.edu.ufersa.utils.ServicePorts;

public class Employee extends Client {

    private Scanner cin = new Scanner(System.in);

    public Employee() {}

    public Employee(User user) {
        this.exec(user);
    }
    
    // TODO: Reescrever para funcionar com os requests
    @Override
    protected void exec(User user){

        int op = 0;

        try {

            Registry reg = LocateRegistry.getRegistry("localhost", ServicePorts.DEALER_PORT.getValue());
            DealerService stub = (DealerService) reg.lookup("Dealer");
            
            do {
                GUI.clearScreen();
                GUI.employeeMenu();

                op = cin.nextInt();
                cin.nextLine();

                switch (op) {
                    case 1:
                        add(stub);
                        break;
                    case 2:
                        update(stub);
                        break;
                    case 3:
                        remove(stub);
                        break;
                    case 4:
                        search(stub);
                        break;
                    case 5:
                        list(stub);
                        break;     
                    case 6:
                        checkStock(stub);
                        break; 
                    case 7:
                        buy(stub, user);
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

    protected void add(DealerService stub){

        try {
            
            System.out.print("Renavam: ");
            long renavam = cin.nextLong();
            cin.nextLine();
            
            System.out.print("Nome: ");
            String nome = cin.nextLine();
            
            System.out.print("Ano de Fabricacao: ");
            int ano_fab = cin.nextInt();
            cin.nextLine();
            
            System.out.print("Preco: ");
            float preco = cin.nextFloat();
            cin.nextLine();
            
            GUI.categoryOps();
            int cat = cin.nextInt();
            cin.nextLine();
            
            CarType categoria = null;
            
            switch (cat) {
                case 1:
                    categoria = CarType.ECONOMY;    
                    break;
                case 2:
                    categoria = CarType.INTERMEDIATE;        
                    break;
                case 3:
                    categoria = CarType.EXECUTIVE;        
                    break;
                default:
                    break;
            }

            System.out.println(stub.add(categoria, renavam, nome, ano_fab, preco));
            cin.nextLine();

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    protected void remove(DealerService stub){

        try {
            
            System.out.print("Renavam: ");
            long renavam = cin.nextLong();
            cin.nextLine();
            
            System.out.println(stub.remove(renavam));
            cin.nextLine();

        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    protected void update(DealerService stub){

        try {
            
            System.out.print("Renavam: ");
            long renavam = cin.nextLong();
            cin.nextLine();
            
            System.out.print("Nome: ");
            String nome = cin.nextLine();
            
            System.out.print("Ano de Fabricacao: ");
            int ano_fab = cin.nextInt();
            cin.nextLine();
            
            System.out.print("Preco: ");
            float preco = cin.nextFloat();
            cin.nextLine();
            
            GUI.categoryOps();
            int cat = cin.nextInt();
            cin.nextLine();
            
            CarType categoria = null;
            
            switch (cat) {
                case 1:
                    categoria = CarType.ECONOMY;    
                    break;
                case 2:
                    categoria = CarType.INTERMEDIATE;        
                    break;
                case 3:
                    categoria = CarType.EXECUTIVE;        
                    break;
                default:
                    break;
            }

            System.out.println(stub.update(categoria, renavam, nome, ano_fab, preco));
            cin.nextLine();

        } catch (RemoteException e) {
            e.printStackTrace();
        }        
    }

}
