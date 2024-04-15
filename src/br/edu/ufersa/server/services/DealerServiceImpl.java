package br.edu.ufersa.server.services;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import br.edu.ufersa.entities.Car;
import br.edu.ufersa.entities.Message;
import br.edu.ufersa.entities.Request;
import br.edu.ufersa.security.RSAImpl;
import br.edu.ufersa.security.SecurityCipher;
import br.edu.ufersa.server.services.skeletons.DealerService;
import br.edu.ufersa.server.services.skeletons.SessionService;
import br.edu.ufersa.utils.RSAKey;
import br.edu.ufersa.utils.ServicePorts;

public class DealerServiceImpl implements DealerService {

    private static Socket databaseConnection;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private static SessionService sessionStub;
    private static RSAImpl rsa;

    public DealerServiceImpl() {
        rsa = new RSAImpl();
        this.init();
    }

    @Override
    public Message receive(String username, Message message) throws RemoteException {               
        if (message == null || message.getContent() == null || message.getHash() == null) {
            return null;
        }
        
        RSAKey clientPuKey = sessionStub.getRSAKey(username);

        String receivedHash = rsa.checkSign(message.getHash(), clientPuKey);
        SecurityCipher bc = null;
        String content = "";

        try {
            bc = new SecurityCipher(sessionStub.getAESKey(username));
            
            if(!bc.genHash(message.getContent()).equals(receivedHash)) {
                System.err.println("failed to receive this request, please try again");
                System.err.println("(stop try, weirdo)");
                return null;
            }
            
            content = bc.dec(message.getContent());

        } catch (InvalidKeyException e) {
            e.printStackTrace();
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
        }

        Request req = Request.fromString(content);

        System.out.println("DEALER: Request from " + username + " -> " + req.toString());

        Message response = null;

        if (req.getUserType() == 0) {
            switch (req.getOpType()) {
                case 1:
                    response = (req.getCategory() == 1) ? 
                    this.searchByRenavam(req, bc) : 
                    this.searchByName(req, bc);

                    break;
                case 2:
                    response = (req.getCategory() == 1) ? 
                    this.list(req, bc) : 
                    this.listByCategory(req, bc);

                    break;
                case 3:
                    response = (req.getCategory() == 1) ? 
                    this.stock(req, bc) : 
                    this.stockByName(req, bc);

                    break;
                case 4:
                    response = this.buy(req, bc);

                    break;
                default:
                    return null;
            }
        } else if (req.getUserType() == 1) {
            switch (req.getOpType()) {
                case 1:
                    response = (req.getCategory() == 1) ? 
                    this.searchByRenavam(req, bc) : 
                    this.searchByName(req, bc);

                    break;
                case 2:
                    response = (req.getCategory() == 1) ? 
                    this.list(req, bc) : 
                    this.listByCategory(req, bc);

                    break;
                case 3:
                    response = (req.getCategory() == 1) ? 
                    this.stock(req, bc) : 
                    this.stockByName(req, bc);

                    break;
                case 4:
                    response = this.buy(req, bc);
                    
                    break;
                case 5:
                    response = this.add(req, bc);

                    break;
                case 6:
                    response = this.update(req, bc);
            
                    break;
                case 7:
                    response = this.remove(req, bc);

                    break;
                default:
                    return null;
            }
        } else {
            return null;
        }

        return response;
    }

    @Override
    public RSAKey getPuKey() throws RemoteException {
        return rsa.getPublicKey();
    }

    private Message searchByName(Request req, SecurityCipher bc) throws RemoteException {
        Message response = new Message("", "");        
        
        List<Car> named_cars = dbQueryCar(req);
        
        if (named_cars.isEmpty()) {
            response.setContent("no cars available for this name");
        } else {
            StringBuilder responseString = new StringBuilder();
            for (Car car : named_cars) {
                responseString.append(car.toString());
                responseString.append("\n");
            }
            
            response.setContent(responseString.toString());
        }

        this.prepare(response, bc);

        return response;
    }

    private Message searchByRenavam(Request req, SecurityCipher bc) throws RemoteException {
        Message response = new Message("", "");
        
        Car searchedCar = dbQuery(req);
        
        if (searchedCar == null) {
            response.setContent("cannot find the vehicle for renavam " + req.getRenavam());
        } else {
            response.setContent(searchedCar.toString());
        }
        
        this.prepare(response, bc);

        return response;
    }

    private Message list(Request req, SecurityCipher bc) throws RemoteException {
        Message response = new Message("", "");
                
        List<Car> car_list = dbSortedList(req);
        
        if (car_list.isEmpty()) {
            response.setContent("no cars available in the system");
        } else {
            StringBuilder responseString = new StringBuilder();
            for (Car car : car_list) {
                responseString.append(car.toString());
                responseString.append("\n");
            }

            response.setContent(responseString.toString());
        }

        this.prepare(response, bc);

        return response;
    }

    private Message listByCategory(Request req, SecurityCipher bc) throws RemoteException {
        Message response = new Message("", "");

        List<Car> economy_cars = new LinkedList<>();
        List<Car> intermediate_cars = new LinkedList<>();
        List<Car> executive_cars = new LinkedList<>();
        
        List<Car> car_list = dbSortedList(req);
        
        
        if (car_list.isEmpty()) {
            response.setContent("no cars available in the system");
        } else {
            
            for (Car car : car_list) {
                switch (car.getCategoria()) {
                    case ECONOMY:
                    economy_cars.add(car);
                    break;
                    case INTERMEDIATE:
                    intermediate_cars.add(car);
                        break;
                        case EXECUTIVE:
                        executive_cars.add(car);
                        break;
                    }
                }

                StringBuilder responseString = new StringBuilder();
                
                responseString.append("ECONOMY CARS:\n");
                for (Car car : economy_cars) {
                responseString.append(car.toString());
                responseString.append("\n");
            }
            responseString.append("\n");

            responseString.append("INTERMEDIATE CARS:\n");
            for (Car car : intermediate_cars) {
                responseString.append(car.toString());
                responseString.append("\n");
            }
            responseString.append("\n");

            responseString.append("EXECUTIVE CARS:\n");
            for (Car car : executive_cars) {
                responseString.append(car.toString());
                responseString.append("\n");
            }
            responseString.append("\n");

            response.setContent(responseString.toString());
        }

        this.prepare(response, bc);

        return response;
    }
    
    private Message add(Request req, SecurityCipher bc) throws RemoteException {
        Message response = new Message("", "");

        boolean attempt = dbCud(req);
        
        if (attempt) {
            response.setContent("Successfully add the car\nPlease check the stock");
        } else {
            response.setContent("This car is already registred");
        }

        this.prepare(response, bc);

        return response;
    }

    private Message remove(Request req, SecurityCipher bc) throws RemoteException {
        Message response = new Message("", "");
        
        boolean attempt = dbCud(req);

        if (attempt) {
            response.setContent("Successfully remove the car\nPlease check the stock");
        } else {
            response.setContent("Cannot remove the car");
        }
        
        this.prepare(response, bc);

        return response;
    }

    private Message update(Request req, SecurityCipher bc) throws RemoteException {        
        Message response  = new Message("", "");
        
        boolean attempt = dbCud(req);

        if (attempt) {
            response.setContent("Successfully update the car\nPlease check the stock");
        } else {
            response.setContent("Cannot update the car");
        }
        
        this.prepare(response, bc);

        return response;
    }
    
    private Message stock(Request req, SecurityCipher bc) throws RemoteException {
        Message response = new Message("", "");

        String quant = dbQueryStock(req);

        if (quant == null) {
            response.setContent("There is no cars registred...");
        } else {
            StringBuilder responseString = new StringBuilder();
    
            responseString.append("""
                    = = = = = = = =  CAR STOCK  = = = = = = = =
                    """);
            responseString.append("\tThere is " + quant + " cars on stock\n");
            responseString.append("""
                    = = = = = = = = = = = = = = = = = = = = = =
                    """);
    
            response.setContent(responseString.toString());
        }

        this.prepare(response, bc);

        return response;
    }

    private Message stockByName(Request req, SecurityCipher bc) throws RemoteException {
        Message response = new Message("", "");

        String quant = dbQueryStock(req);

        if (quant == null) {
            response.setContent("There is no cars registred...");
        } else {
            response.setContent(quant);
        }

        this.prepare(response, bc);

        return response;
    }

    private Message buy(Request req, SecurityCipher bc) throws RemoteException {
        Message response = new Message("", "");
        
        Car purchased_car = dbQuery(new Request(1, req.getUserType(), req.getLogin(), req.getRenavam(), req.getName(), req.getFab(), req.getPrice(), 1));

        System.out.println("Cheguei aqui");
        
        if (purchased_car == null) {
            response.setContent("This car isn't available");
        } else {
            System.out.println("Antes de remover o carro");
            dbCud(new Request(7, req.getUserType(), req.getLogin(), req.getRenavam(), req.getName(), req.getFab(), req.getPrice(), req.getCategory()));
            System.out.println("Depois de remover o carro");

            StringBuilder responseString = new StringBuilder(); 
            
            responseString.append("Acquired car:\n");
            responseString.append(purchased_car);
            responseString.append("\n");
    
            response.setContent(responseString.toString());
        }

        this.prepare(response, bc);

        return response;
    }

    private void prepare(Message message, SecurityCipher bc){
        try {

            message.setContent(bc.enc(message.getContent()));
            message.setHash(bc.genHash(message.getContent()));
            message.setHash(rsa.sign(message.getHash()));

        } catch (InvalidKeyException e) {
            e.printStackTrace();
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
        }
    }

    private Car dbQuery(Request req) {
        Car car = null;

        try {

            output.writeObject(req);
            car = (Car) input.readObject();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return car;
    }

    @SuppressWarnings("unchecked")
    private List<Car> dbQueryCar(Request req) {
        List<Car> named_cars_list = null;

        try {

            output.writeObject(req);
            named_cars_list = (List<Car>) input.readObject();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return named_cars_list;
    }

    private String dbQueryStock(Request req) {
        String quant = "";

        try {

            output.writeObject(req);
            quant = (String) input.readObject();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return quant;
    }

    @SuppressWarnings("unchecked")
    private List<Car> dbSortedList(Request req) {
        List<Car> cars_list = null;

        try {

            output.writeObject(req);
            cars_list = (List<Car>) input.readObject();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return cars_list;
    }


    private boolean dbCud(Request req) {
        boolean attempt = false;

        try {

            output.writeObject(req);
            attempt = (Boolean) input.readObject();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return attempt;
    }

    private void init() {

        try {

            Registry reg = LocateRegistry.getRegistry("localhost", ServicePorts.SESSION_PORT.getValue());
            sessionStub = (SessionService) reg.lookup("Session");

            databaseConnection = new Socket("localhost", ServicePorts.DATABASE_PORT.getValue());
            output = new ObjectOutputStream(databaseConnection.getOutputStream());
            input = new ObjectInputStream(databaseConnection.getInputStream());
            
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                input.close();
                output.close();
                databaseConnection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}