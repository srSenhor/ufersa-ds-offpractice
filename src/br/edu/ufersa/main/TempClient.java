package br.edu.ufersa.main;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import br.edu.ufersa.services.skeletons.AuthService;
import br.edu.ufersa.utils.GUI;
import br.edu.ufersa.utils.UserType;

public class TempClient {

    private static final int AUTH_PORT = 60000;

    public TempClient() {
        this.login();
    }

    private void login() {
        Scanner cin = new Scanner(System.in);
        boolean trying = true;

        try {
            
            Registry reg = LocateRegistry.getRegistry("localhost", AUTH_PORT);
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
                GUI.clientMenu();
                break;
            case EMPLOYEE:
                GUI.employeeMenu();
                break;
            default:
                System.err.println("This user is undefined");
                break;
        }

    }
}
