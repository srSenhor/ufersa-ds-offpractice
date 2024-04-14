package br.edu.ufersa.client;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import br.edu.ufersa.entities.Message;
import br.edu.ufersa.entities.Request;
import br.edu.ufersa.entities.SessionLogin;
import br.edu.ufersa.security.SecurityCipher;
import br.edu.ufersa.server.services.skeletons.DealerService;
import br.edu.ufersa.utils.GUI;
import br.edu.ufersa.utils.ServicePorts;

public class Client {

    private final Scanner cin = new Scanner(System.in);
    private final int USER_TYPE = 0;
    protected SessionLogin login;
    protected DealerService dealerStub;

    public Client() {};

    public Client(SessionLogin login) {
        this.login = login;
        this.exec();
    }

    // class ThreadRefresh implements Runnable {

    //     private Message message;
    //     private String name;
    //     private int userType;
    
    //     public ThreadRefresh(Message message, String name, int userType){
    //         this.message = message;
    //         this.name = name;
    //         this.userType = userType;
    //     }
    
    //     @Override
    //     public void run() {
    //         while (!Thread.currentThread().isInterrupted()) {
    //             try {
                    
    //             message.setContent(send(1, userType, login.getUsername(), -1, name, -1, -1, 2));
    //             Thread.sleep(100L);
                
    //             } catch (InterruptedException e) {
    //                 throw new RuntimeException();
                    
    //             }
    //         }
    //     }
        
    // }

    //TODO voltar ao modelo antigo com várias funções e chamar elas no switch case

    protected void exec() {

        int op = 0;

        try {

            Registry reg = LocateRegistry.getRegistry("localhost", ServicePorts.DEALER_PORT.getValue());
            this.dealerStub = (DealerService) reg.lookup("Dealer");
            
            do {
                GUI.clearScreen();
                GUI.clientMenu();

                op = cin.nextInt();
                cin.nextLine();

                Message response = new Message("", "");

                switch (op) {
                    case 1:
                        search(response, op);

                        System.out.println("Press any key to continue...");
                        cin.nextLine();
                        break;
                    case 2:
                        list(response, op);
                        
                        System.out.println("Press any key to continue...");
                        cin.nextLine();
                        break;
                    case 3:
                        stock(response, op);
                        
                        System.out.println("Press any key to continue...");
                        cin.nextLine();
                        break;
                    case 4:
                        buy(response, op);

                        System.out.println("Press any key to continue...");
                        cin.nextLine();
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

    protected void search(Message response, int op) {
        GUI.searchOps();
        int searchOption = cin.nextInt();
        cin.nextLine();

        if (searchOption == 1) {
            System.out.print("Renavam: ");
            long renavam = cin.nextLong();
            cin.nextLine();

            response.setContent(send(op, USER_TYPE, login.getUsername(), renavam, null, -1, -1, searchOption));

        } else {
            System.out.print("Name: ");
            String name = cin.nextLine();

            response.setContent(send(op, USER_TYPE, login.getUsername(), -1, name, -1, -1, searchOption));
        }

        System.out.println(response.getContent());
    }

    protected void list(Message response, int op) {
        GUI.listOps();
        int listOption = cin.nextInt();
        cin.nextLine();

        response.setContent(send(op, USER_TYPE, login.getUsername(), -1L, null, -1, -1.0f, listOption));
        System.out.println(response.getContent());
    }

    protected void stock(Message response, int op) {
        GUI.stockOps();
        int stockOption = cin.nextInt();
        cin.nextLine();

        response.setContent(send(op, USER_TYPE, login.getUsername(), -1L, null, -1, -1.0f, stockOption));
        System.out.println(response.getContent());
    }

    protected void buy(Message response, int op) {
        GUI.buyOps();
        String name = cin.nextLine();

        long renavam;

        
        /*
        TODO dar um jeito da tela ficar atualizando a lista de carros
        * Usar uma thread pra ficar recuperando o valor e outra pra ficar imprimindo?
        */
        // Thread t0 = new Thread(new ThreadRefresh(response, name, 0));
        // t0.start();
        
        response.setContent(send(1, USER_TYPE, login.getUsername(), -1, name, -1, -1, 2));
        System.out.print("""
                This cars are available
                """ + 
                response.getContent() +
                "Renavam: ");

        
        renavam = cin.nextLong();
        cin.nextLine();

        // t0.interrupt();

        System.out.println("Are you sure you want to buy this car? [y] for yes / [n] for no");
        String confirm = cin.next(); 

        if (confirm.toLowerCase().charAt(0) == 'y') {
            response.setContent(send(op, USER_TYPE, login.getUsername(), renavam, name, -1, -1, -1));
            cin.nextLine();
        } else {
            System.err.println("Cancelled operation");
            cin.nextLine();
        }

        System.out.println(response.getContent());
    }

    protected String send(int opType, int userType, String username, long renavam, String name, int fab, float price, int category) {
        String request = new Request(opType, userType, username, renavam, name, fab, price, category).toString();
        String response = "";
        try {
            
            SecurityCipher bc = new SecurityCipher(this.login.getAesKey());
            request = bc.enc(request);

            String hash = bc.genHash(request);           
            hash = this.login.getSessionRSA().sign(hash);

            Message messageResponse = dealerStub.receive(username, new Message(request, hash));

            if (messageResponse == null) {
                response =  "cannot do this, please try again...'";
            } else {
                String responseTestHash = this.login.getSessionRSA().checkSign(messageResponse.getHash(), this.login.getServerPuKey());
    
                if(!bc.genHash(messageResponse.getContent()).equals(responseTestHash)) {
                    response = "an error has ocurred, please try again";
                } else {
                    response = bc.dec(messageResponse.getContent());
                }
                
            }


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {           
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {            
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {            
            e.printStackTrace();
        } catch (BadPaddingException e) {           
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return response;
    }

}
