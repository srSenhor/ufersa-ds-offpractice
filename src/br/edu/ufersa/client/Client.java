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
// import br.edu.ufersa.server.services.ThreadBuy;
import br.edu.ufersa.server.services.skeletons.DealerService;
import br.edu.ufersa.utils.GUI;
import br.edu.ufersa.utils.ServicePorts;

public class Client {

    private final Scanner cin = new Scanner(System.in);
    protected SessionLogin login;
    protected DealerService dealerStub;

    public Client() {};

    public Client(SessionLogin login) {
        this.login = login;
        this.exec();
    }


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

    
    protected void send(int opType, int userType, String username, long renavam, String name, int fab, float price, int category) {
        String request = new Request(opType, userType, username, renavam, name, fab, price, category).toString();
        
        try {
            
            SecurityCipher bc = new SecurityCipher(this.login.getAesKey());
            request = bc.enc(request);

            String hash = bc.genHash(request);           
            hash = this.login.getSessionRSA().sign(hash);

            Message response = dealerStub.receive(username, new Message(request, hash));

            if (response == null) {
                System.err.println("cannot do this, please try again...'");
            } else {
                String responseTestHash = this.login.getSessionRSA().checkSign(response.getHash(), this.login.getServerPuKey());
    
                if(!bc.genHash(response.getContent()).equals(responseTestHash)) {
                    System.err.println("an error has ocurred, please try again");
                } else {
                    System.err.println(bc.dec(response.getContent()));
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
    }

}
