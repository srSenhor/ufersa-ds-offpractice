package br.edu.ufersa.client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import br.edu.ufersa.entities.Message;
import br.edu.ufersa.entities.SessionLogin;
import br.edu.ufersa.server.services.skeletons.Proxy;
import br.edu.ufersa.utils.GUI;
import br.edu.ufersa.utils.ServicePorts;
import br.edu.ufersa.utils.UserType;

public class Employee extends Client {

    private static final long serialVersionUID = 1L;

    public Employee() {}

    public Employee(SessionLogin login) {
        this.login = login;
        this.userType = UserType.EMPLOYEE.getValue();
        this.exec();
    }
    
    @Override
    protected void exec() {
        Scanner cin = new Scanner(System.in);
        int op = 0;

        try {

            // Registry reg = LocateRegistry.getRegistry("localhost", ServicePorts.DEALER_PORT.getValue());
            // this.proxy = (DealerService) reg.lookup("Dealer");
            Registry reg = LocateRegistry.getRegistry("localhost", ServicePorts.PROXY_PORT.getValue());
            this.proxy = (Proxy) reg.lookup("Proxy");
            
            Message response = new Message("", "");
            
            do {
                GUI.clearScreen();
                GUI.employeeMenu();

                op = cin.nextInt();
                cin.nextLine();

                switch (op) {
                    case 1:
                        search(response, op, cin);

                        System.out.println("Press any key to continue...");
                        cin.nextLine();
                        break;
                    case 2:
                        list(response, op, cin);
                        
                        System.out.println("Press any key to continue...");
                        cin.nextLine();
                        break;
                    case 3:
                        stock(response, op, cin);
                        
                        System.out.println("Press any key to continue...");
                        cin.nextLine();
                        break;
                    case 4:
                        buy(response, op, cin);

                        System.out.println("Press any key to continue...");
                        cin.nextLine();
                        break;
                    case 5:
                        add(response, op, cin);

                        System.out.println("Press any key to continue...");
                        cin.nextLine();
                        break;
                    case 6:
                        update(response, op, cin);
                        
                        System.out.println("Press any key to continue...");
                        cin.nextLine();
                        break;
                    case 7:
                        delete(response, op, cin);    

                        System.out.println("Press any key to continue...");
                        cin.nextLine();
                        break;
                    case 8:
                        System.out.println("bye my friend!");
                        break;
                    default:
                        System.err.println("undefined operation");

                        System.out.println("Press any key to continue...");
                        cin.nextLine();
                        break;
                }
            } while(op != 8);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cin.close();
        }
    }

    protected void add(Message response, int op, Scanner cin) {
        System.out.print("Renavam: ");
        long renavam = cin.nextLong();
        cin.nextLine();
        
        System.out.print("Nome: ");
        String name = cin.nextLine();
        
        System.out.print("Ano de Fabricacao: ");
        int fab = cin.nextInt();
        cin.nextLine();
        
        System.out.print("Preco: ");
        float price = cin.nextFloat();
        cin.nextLine();
        
        GUI.categoryOps();
        int cat = cin.nextInt();
        cin.nextLine();
        
        response.setContent(send(op, 1, login.getUsername(), renavam, name, fab, price, cat));
        System.out.println(response.getContent());
    }

    protected void update(Message response, int op, Scanner cin) {
        System.out.print("Renavam: ");
        long renavam = cin.nextLong();
        cin.nextLine();
        
        System.out.print("Nome: ");
        String name = cin.nextLine();
        
        System.out.print("Ano de Fabricacao: ");
        int fab = cin.nextInt();
        cin.nextLine();
        
        System.out.print("Preco: ");
        float price = cin.nextFloat();
        cin.nextLine();
        
        GUI.categoryOps();
        int cat = cin.nextInt();
        cin.nextLine();
        
        response.setContent(send(op, 1, login.getUsername(), renavam, name, fab, price, cat));
        System.out.println(response.getContent());
    }

    protected void delete(Message response, int op, Scanner cin) {
        System.out.print("Renavam: ");
        long renavam = cin.nextLong();
        cin.nextLine();
        
        System.out.println("You're trying to remove this car:\n");
        
        System.out.println(send(1, 0, login.getUsername(), renavam, null, -1, -1, 1));

        System.out.println("Are you sure you want to buy this car? [y] for yes / [n] for no");
        String confirm = cin.next(); 

        if (confirm.toLowerCase().charAt(0) == 'y') {
            response.setContent(send(op, 1, login.getUsername(), renavam, null, -1, -1.0f, -1));
            cin.nextLine();
        } else {
            response.setContent(("Cancelled operation"));
            cin.nextLine();
        }

        System.out.println(response.getContent());
    }
}
