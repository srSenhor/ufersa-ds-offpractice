package br.edu.ufersa.main;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import br.edu.ufersa.client.Client;
import br.edu.ufersa.client.Employee;
import br.edu.ufersa.entities.User;
import br.edu.ufersa.server.services.skeletons.AuthService;
import br.edu.ufersa.utils.GUI;
import br.edu.ufersa.utils.ServicePorts;

public class App {

    public App() {
        this.init();
    }

    private void init() {

        // Usuário loga no sistema

        Scanner cin = new Scanner(System.in);
        boolean trying = true;

        try {
            
            Registry reg = LocateRegistry.getRegistry("localhost", ServicePorts.AUTH_PORT.getValue());
            AuthService stub = (AuthService) reg.lookup("Auth");

            do {
                GUI.clearScreen();
                GUI.loginScreen();
    
                System.out.print("Login   : ");
                String login = cin.nextLine();
                
                System.out.print("Password: ");
                String password = cin.nextLine();

                User user = stub.authUser(login, password);

                if ( user != null ) {
                    System.out.println("Successful logged in!");
                    trying = false;
                    cin.nextLine(); //TODO: substituir isso por algo mais intuitivo
                    
                    mainMenu(user);

                    stub.logout(login);
                    System.out.println("Successful logged out!");
                } else {
                    System.out.println("Failed to login, there is something wrong...");
                    cin.nextLine(); //TODO: substituir isso por algo mais intuitivo
                }
            } while (trying);



        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cin.close();
        }
    }

    private void mainMenu(User user){

        switch (user.getType()) {
            case CLIENT:
                new Client(user);
                break;
            case EMPLOYEE:
                new Employee(user);
                break;
            default:
                System.err.println("Undefined type");
                break;
        }

    }
}
