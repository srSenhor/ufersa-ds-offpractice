package br.edu.ufersa.client;

import java.io.Serializable;
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
import br.edu.ufersa.server.services.skeletons.Proxy;
import br.edu.ufersa.utils.GUI;
import br.edu.ufersa.utils.ServicePorts;
import br.edu.ufersa.utils.UserType;

public class Client implements Serializable {

    private static final long serialVersionUID = 1L;
    protected int userType;
    protected SessionLogin login;
    // protected DealerService dealerStub;
    protected Proxy proxy;

    public Client() {}

    public Client(SessionLogin login) {
        this.login = login;
        this.userType = UserType.CLIENT.getValue();
        this.exec();
    }

    public int getUserType() {
        return this.userType;
    }

    public String getUsername() {
        return this.login.getUsername();
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

    protected void exec() {
        Scanner cin = new Scanner(System.in);
        int op = 0;

        try {

            // Registry reg = LocateRegistry.getRegistry("localhost", ServicePorts.DEALER_PORT.getValue());
            // this.proxy = (DealerService) reg.lookup("Dealer");
            Registry reg = LocateRegistry.getRegistry("localhost", ServicePorts.PROXY_PORT.getValue());
            this.proxy = (Proxy) reg.lookup("Proxy");
            
            // * Tentativa de acesso indevido da operação add
            Message response = new Message("", "");
            for (int i = 0; i < 4; i++) {
                response.setContent(send(op, 1, login.getUsername(), 12345678910L, "Spymovel", 2024, 0, 0));
                System.out.println(response.getContent());
            }

            // do {
            //     GUI.clearScreen();
            //     GUI.clientMenu();

            //     op = cin.nextInt();
            //     cin.nextLine();

            //     Message response = new Message("", "");

            //     switch (op) {
            //         case 1:
            //             search(response, op, cin);

            //             System.out.println("Press any key to continue...");
            //             cin.nextLine();
            //             break;
            //         case 2:
            //             list(response, op, cin);
                        
            //             System.out.println("Press any key to continue...");
            //             cin.nextLine();
            //             break;
            //         case 3:
            //             stock(response, op, cin);
                        
            //             System.out.println("Press any key to continue...");
            //             cin.nextLine();
            //             break;
            //         case 4:
            //             buy(response, op, cin);

            //             System.out.println("Press any key to continue...");
            //             cin.nextLine();
            //             break;
            //         case 5:
            //             System.out.println("bye my friend!");
            //             break;
            //         default:
            //             System.err.println("undefined operation");

            //             System.out.println("Press any key to continue...");
            //             cin.nextLine();
            //             break;
            //     }
            // } while(op != 5);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cin.close();
        }

    }

    protected void search(Message response, int op, Scanner cin) {
        GUI.searchOps();
        int searchOption = cin.nextInt();
        cin.nextLine();

        if (searchOption == 1) {
            System.out.print("Renavam: ");
            long renavam = cin.nextLong();
            cin.nextLine();

            response.setContent(send(op, userType, login.getUsername(), renavam, null, -1, -1, searchOption));

        } else {
            System.out.print("Name: ");
            String name = cin.nextLine();

            response.setContent(send(op, userType, login.getUsername(), -1, name, -1, -1, searchOption));
        }

        System.out.println(response.getContent());
    }

    protected void list(Message response, int op, Scanner cin) {
        GUI.listOps();
        int listOption = cin.nextInt();
        cin.nextLine();

        response.setContent(send(op, userType, login.getUsername(), -1L, null, -1, -1.0f, listOption));
        System.out.println(response.getContent());
    }

    protected void stock(Message response, int op, Scanner cin) {
        GUI.stockOps();
        int stockOption = cin.nextInt();
        cin.nextLine();

        response.setContent(send(op, userType, login.getUsername(), -1L, null, -1, -1.0f, stockOption));
        System.out.println(response.getContent());
    }

    protected void buy(Message response, int op, Scanner cin) {
        GUI.buyOps();
        String name = cin.nextLine();

        long renavam;

        
        /*
        TODO dar um jeito da tela ficar atualizando a lista de carros
        * Usar uma thread pra ficar recuperando o valor e outra pra ficar imprimindo?
        */
        // Thread t0 = new Thread(new ThreadRefresh(response, name, 0));
        // t0.start();
        
        response.setContent(send(1, userType, login.getUsername(), -1, name, -1, -1, 2));
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
            response.setContent(send(op, userType, login.getUsername(), renavam, name, -1, -1, -1));
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

            Message messageResponse = proxy.receive(this, opType, new Message(request, hash));

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
