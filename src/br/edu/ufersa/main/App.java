package br.edu.ufersa.main;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import br.edu.ufersa.client.Client;
import br.edu.ufersa.client.Employee;
import br.edu.ufersa.services.skeletons.AuthService;
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

            do {
                GUI.clearScreen();
                GUI.loginScreen();
    
                System.out.print("Login:\t\t");
                String login = cin.nextLine();
                
                System.out.print("Password:\t");
                String password = cin.nextLine();

                UserType type = stub.authUser(login, password);

                if ( type != UserType.UNDEFINED ) {
                    System.out.println("Successful logged in!");
                    trying = false;
                    cin.nextLine(); //TODO: substituir isso por algo mais intuitivo
                    mainMenu(type);
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


    //Por enquanto, é apenas um teste para verificar se ele tá pegando o usuário e exibindo o respectivo menu
    private void mainMenu(UserType type){

        switch (type) {
            case CLIENT:
                new Client(true);
                break;
            case EMPLOYEE:
                new Employee(true);
                break;
            default:
                System.err.println("Undefined type");
                break;
        }

    }
}
