package br.edu.ufersa.main;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import br.edu.ufersa.client.Client;
import br.edu.ufersa.client.Employee;
import br.edu.ufersa.entities.SessionLogin;
import br.edu.ufersa.server.services.skeletons.AuthService;
import br.edu.ufersa.utils.GUI;
import br.edu.ufersa.utils.ServicePorts;
import br.edu.ufersa.utils.UserType;

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

            SessionLogin login;

            do {
                GUI.clearScreen();
                GUI.entryScreen();
                
                int loginOp = cin.nextInt();
                cin.nextLine();
                
                System.out.println("\nPlease enter your credentials\n");
                System.out.print("Login   : ");
                String username = cin.nextLine();
                
                System.out.print("Password: ");
                String password = cin.nextLine();


                if (loginOp == 1) {
                    login = stub.auth(username, password);
                } else {
                    // Os funcionários já são registrados no sistema por padrão
                    login = stub.signup(username, password, UserType.CLIENT);
                }


                if ( login != null ) {
                    System.out.println("Successful logged in!");
                    trying = false;


                    System.out.println("Press any key to continue...");
                    cin.nextLine();
             
                    mainMenu(login);

                    stub.logout(login);
                    System.out.println("Successful logged out!");
                } else {
                    System.out.println("Failed to login, there is something wrong...");
                    
                    System.out.println("Press any key to continue...");
                    cin.nextLine();
                }
            } while (trying);



        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cin.close();
        }
    }

    private void mainMenu(SessionLogin login){

        switch (login.getType()) {
            case CLIENT:
                new Client(login);
                break;
            case EMPLOYEE:
                new Employee(login);
                break;
            default:
                System.err.println("Undefined type");
                break;
        }

    }
}
