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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
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
import br.edu.ufersa.utils.CarType;
import br.edu.ufersa.utils.RSAKey;
import br.edu.ufersa.utils.ServicePorts;

public class DealerServiceImpl implements DealerService {

    // TODO: Separar o banco de dados do serviço
    private static HashMap<Long, Car> cars;
    private static HashMap<String, Integer> cars_stock;
    private static Socket databaseConnection;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private static SessionService sessionStub;
    private static RSAImpl rsa;

    public DealerServiceImpl() {
        cars = new HashMap<>();
        cars_stock = new HashMap<>();
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
                    this.searchByRenavam(req.getRenavam(), req, bc) : 
                    this.searchByName(req.getName(), bc);

                    break;
                case 2:
                    response = (req.getCategory() == 1) ? 
                    this.list(bc) : 
                    this.listByCategory(bc);

                    break;
                case 3:
                    response = (req.getCategory() == 1) ? 
                    this.stock(req, bc) : 
                    this.stockByName(req, bc);

                    break;
                case 4:
                    // TODO: Dar uma mexida pra ele conseguir adicionar os carros ao cliente
                    response = this.buy(req.getRenavam(), bc);
                    //response = this.buy(req.getRenavam(), null);

                    break;
                default:
                    return null;
            }
        } else if (req.getUserType() == 1) {
            switch (req.getOpType()) {
                case 1:
                    response = (req.getCategory() == 1) ? 
                    this.searchByRenavam(req.getRenavam(), req, bc) : 
                    this.searchByName(req.getName(), bc);

                    break;
                case 2:
                    response = (req.getCategory() == 1) ? 
                    this.list(bc) : 
                    this.listByCategory(bc);

                    break;
                case 3:
                    response = (req.getCategory() == 1) ? 
                    this.stock(req, bc) : 
                    this.stockByName(req, bc);

                    break;
                case 4:
                    // TODO: Dar uma mexida pra ele conseguir adicionar os carros ao cliente
                    response = this.buy(req.getRenavam(), bc);
                    //response = this.buy(req.getRenavam(), null);

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

    // TODO fazer ele acessar o database separado
    private Message searchByName(String name, SecurityCipher bc) throws RemoteException {
        
        StringBuilder responseString = new StringBuilder();
        
        List<Car> named_cars = new ArrayList<>();
        
        cars.forEach((Long renavam, Car car) -> {
            if(car.getNome().equalsIgnoreCase(name)) {
                named_cars.add(car);
            }
        });

        Message response = new Message("", "");
        
        if (named_cars.isEmpty()) {
            response.setContent("no cars available for this name");
        } else {
            for (Car car : named_cars) {
                responseString.append(car.toString());
                responseString.append("\n");
            }
            
            response.setContent(responseString.toString());
        }

        this.prepare(response, bc);

        return response;

    }

    private Message searchByRenavam(long renavam, Request req, SecurityCipher bc) throws RemoteException {

        Car searchedCar = dbQueryCar(req);

        Message response = new Message("", "");
        
        if (searchedCar == null) {
            response.setContent("cannot find the vehicle for renavam " + renavam);
        } else {
            response.setContent(searchedCar.toString());
        }
        
        this.prepare(response, bc);

        return response;

    }

    // TODO fazer ele acessar o database separado
    private Message list(SecurityCipher bc) throws RemoteException {
        
        StringBuilder responseString = new StringBuilder();

        List<Car> car_list = getSorted();

        Message response = new Message("", "");

        if (car_list.isEmpty()) {
            response.setContent("no cars available in the system");
        } else {
            for (Car car : car_list) {
                responseString.append(car.toString());
                responseString.append("\n");
            }

            response.setContent(responseString.toString());
        }

        this.prepare(response, bc);

        return response;

    }

    // TODO fazer ele acessar o database separado
    private Message listByCategory(SecurityCipher bc) throws RemoteException {

        StringBuilder responseString = new StringBuilder();

        List<Car> economy_cars = new LinkedList<>();
        List<Car> intermediate_cars = new LinkedList<>();
        List<Car> executive_cars = new LinkedList<>();

        List<Car> car_list = getSorted();
        
        Message response = new Message("", "");

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

        boolean attempt = dbCrud(req);
        
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
        
        boolean attempt = dbCrud(req);

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
        
        boolean attempt = dbCrud(req);

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

    // TODO fazer ele acessar o database separado
    private Message buy(long renavam, SecurityCipher bc) throws RemoteException {
        
        Car purchased_car = cars.remove(renavam);
        
        Message response = new Message("", "");

        if (purchased_car == null) {
            response.setContent("This car isn't available");
        } else {
            // user.acquireCar(purchased_car);
            
            Integer quant_car = cars_stock.get(purchased_car.getNome());
            
            if (quant_car > 0) {
                cars_stock.put(purchased_car.getNome(), --quant_car);
            }
            
            StringBuilder responseString = new StringBuilder(); 
            // responseString.append("User " + user.getLogin() + " acquired:\n");
            responseString.append("Acquired car:\n");
            responseString.append(purchased_car);
            responseString.append("\n");
    
            response.setContent(responseString.toString());
        }

        this.prepare(response, bc);

        return response;

    }

    // TODO Mover para o database
    private List<Car> getSorted(){
        List<Car> car_list = new LinkedList<>();

        cars.forEach((Long renavam, Car car) -> {
            car_list.add(car);
        });

        car_list.sort(new Comparator<Car>() {
            @Override
            public int compare(Car o1, Car o2) {
                return o1.getNome().compareTo(o2.getNome());

            }            
        });

        return car_list;
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
    
    // TODO Remover isso
    private void record(int carType, long renavam, String nome, int fab, float price) throws RemoteException {
        
        Car car = new Car(CarType.values()[carType], renavam, nome.toUpperCase(), fab, price);
        cars.put(renavam, car);

        Integer quant_car = cars_stock.get(nome.toUpperCase());
        if (quant_car == null) {
            cars_stock.put(nome.toUpperCase(), 1);
        } else {
            cars_stock.put(nome.toUpperCase(), ++quant_car);
        }

    }

    private Car dbQueryCar(Request req) {
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

    private boolean dbCrud(Request req) {
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


            // TODO Remover essa inicialização assim que terminar de separar o bd
            this.record(CarType.ECONOMY.getValue(), 72439120382l, "nissan march", 2012, 86000f);
            this.record(CarType.INTERMEDIATE.getValue(), 51029874625l, "toyota etios", 2017, 70000f);
            this.record(CarType.ECONOMY.getValue(), 14243939238l, "ford ka", 2009, 90000f);
            this.record(CarType.EXECUTIVE.getValue(), 29018475092l, "chevrolet cruze", 2021, 40000f);
            this.record(CarType.ECONOMY.getValue(), 73849201742l, "nissan march", 2015, 65000f);
            this.record(CarType.EXECUTIVE.getValue(), 45829637102l, "toyota corolla", 2022, 42000f);
            this.record(CarType.INTERMEDIATE.getValue(), 37905164293l, "ford ka sedan", 2018, 60000f);
            this.record(CarType.EXECUTIVE.getValue(), 90583274625l, "honda civic", 2020, 45000f);
            this.record(CarType.ECONOMY.getValue(), 62458390271l, "hyundai hb20s", 2012, 80000f);
            this.record(CarType.ECONOMY.getValue(), 87251903416l, "fiat novo uno", 2010, 95000f);
            this.record(CarType.INTERMEDIATE.getValue(), 18573964205l, "renault logan", 2016, 62000f);
            this.record(CarType.EXECUTIVE.getValue(), 14926874359l, "audi a3", 2019, 50000f);
            
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}