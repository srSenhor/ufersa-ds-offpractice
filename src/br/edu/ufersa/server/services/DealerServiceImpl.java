package br.edu.ufersa.server.services;

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
// import br.edu.ufersa.entities.User;
import br.edu.ufersa.security.RSAImpl;
import br.edu.ufersa.security.SecurityCipher;
import br.edu.ufersa.server.services.skeletons.DealerService;
import br.edu.ufersa.server.services.skeletons.SessionService;
import br.edu.ufersa.utils.CarType;
import br.edu.ufersa.utils.RSAKey;
import br.edu.ufersa.utils.ServicePorts;
// import br.edu.ufersa.utils.UserType;

public class DealerServiceImpl implements DealerService {

    // TODO: Separar o banco de dados do serviço
    private static HashMap<Long, Car> cars;
    private static HashMap<String, Integer> cars_stock;
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
                    this.searchByRenavam(req.getRenavam(), bc) : 
                    this.searchByName(req.getName(), bc);

                    break;
                case 2:
                    response = (req.getCategory() == 1) ? 
                    this.list(bc) : 
                    this.listByCategory(bc);

                    break;
                case 3:
                    response = (req.getCategory() == 1) ? 
                    this.stock(bc) : 
                    this.stockByName(bc);

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
                    this.searchByRenavam(req.getRenavam(), bc) : 
                    this.searchByName(req.getName(), bc);

                    break;
                case 2:
                    response = (req.getCategory() == 1) ? 
                    this.list(bc) : 
                    this.listByCategory(bc);

                    break;
                case 3:
                    response = (req.getCategory() == 1) ? 
                    this.stock(bc) : 
                    this.stockByName(bc);

                    break;
                case 4:
                    // TODO: Dar uma mexida pra ele conseguir adicionar os carros ao cliente
                    response = this.buy(req.getRenavam(), bc);
                    //response = this.buy(req.getRenavam(), null);

                    break;
                case 5:
                    response = this.add(CarType.values()[req.getCategory()], req.getRenavam(), req.getName(), req.getFab(), req.getPrice(), bc);

                    break;
                case 6:
                    response = this.update(CarType.values()[req.getCategory()], req.getRenavam(), req.getName(), req.getFab(), req.getPrice(), bc);
            
                    break;
                case 7:
                    response = this.remove(req.getRenavam(), bc);

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

    private Message searchByRenavam(long renavam, SecurityCipher bc) throws RemoteException {

        Car searchedCar = cars.get(renavam);

        Message response = new Message("", "");
        
        if (searchedCar == null) {
            response.setContent("cannot find the vehicle for renavam " + renavam);
        } else {
            response.setContent(searchedCar.toString());
        }
        
        this.prepare(response, bc);

        return response;

    }

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
    
    private Message add(CarType categoria, long renavam, String nome, int ano_fab, float preco, SecurityCipher bc) throws RemoteException {

        Car car = cars.get(renavam);

        Message response = new Message("", "");
        
        if (car == null) {
            car = new Car(categoria, renavam, nome, ano_fab, preco);
            cars.put(renavam, car);

            Integer quant_car = cars_stock.get(nome.toUpperCase());
            if (quant_car == null) {
                cars_stock.put(nome.toUpperCase(), 1);
            } else {
                cars_stock.put(nome.toUpperCase(), ++quant_car);
            }

            response.setContent("New car: \n" + car);
        } else {
            response.setContent("This car is already registred");
        }

        this.prepare(response, bc);

        return response;

    }

    private Message remove(long renavam, SecurityCipher bc) throws RemoteException {
        
        Car car = cars.remove(renavam);

        Message response = new Message("", "");

        if (car == null) {
            response.setContent("This car isn't registred");
        } else {
            Integer quant_car = cars_stock.get(car.getNome());

            if (quant_car > 0) {
                cars_stock.put(car.getNome(), --quant_car);
            } 

            response.setContent("Removed car: \n" + car);
        }

        this.prepare(response, bc);

        return response;

    }

    private Message update(CarType categoria, long renavam, String nome, int ano_fab, float preco, SecurityCipher bc) throws RemoteException {
        
        Car car = cars.get(renavam);

        Message response  = new Message("", "");
        
        if (car == null) {
            response.setContent("This car isn't registred");
        } else {
            Integer quant_car = cars_stock.get(car.getNome().toUpperCase());

            if (quant_car > 0) {
                cars_stock.put(car.getNome(), --quant_car);
            } 

            car.setAnoFab(ano_fab);
            car.setCategoria(categoria);
            car.setNome(nome);
            car.setPreco(preco);
            
            cars.put(renavam, car);

            quant_car = cars_stock.get(car.getNome());
            
            if (quant_car == null) {
                cars_stock.put(car.getNome().toUpperCase(), 1);
            } else {
                cars_stock.put(car.getNome().toUpperCase(), ++quant_car);
            }

            response.setContent("Updated car: \n" + car);
        }

        this.prepare(response, bc);

        return response;

    }
    
    private Message stock(SecurityCipher bc) throws RemoteException {

        Message response = new Message("", "");

        if (cars_stock.isEmpty()) {
            response.setContent("There is no cars registred...");
        } else {
            StringBuilder responseString = new StringBuilder();
    
            responseString.append("""
                    = = = = = = = =  CAR STOCK  = = = = = = = =
                    """);
            responseString.append("\tThere is " + cars.size() + " cars on stock\n");
            responseString.append("""
                    = = = = = = = = = = = = = = = = = = = = = =
                    """);
    
            response.setContent(responseString.toString());
        }

        this.prepare(response, bc);

        return response;

    }

    private Message stockByName(SecurityCipher bc) throws RemoteException {

        Message response = new Message("", "");

        if (cars_stock.isEmpty()) {
            response.setContent("There is no cars registred...");
        } else {
            StringBuilder responseString = new StringBuilder();
    
            responseString.append("""
                    = = = = = =  CAR STOCK  = = = = = =
                    """);
    
            cars_stock.forEach((String name, Integer quant) -> {
                responseString.append(name);
                responseString.append("\t");
                responseString.append("x" + quant);
                responseString.append("\n");
            });
    
            responseString.append("""
                    = = = = = = = = = = = = = = = = = =
                    """);
    
            response.setContent(responseString.toString());
        }

        this.prepare(response, bc);

        return response;

    }

    // TODO: Fazer um bd de usuários?
    // private Message buy(long renavam, User user) throws RemoteException {
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

    private void init() {

        try {

            Registry reg = LocateRegistry.getRegistry("localhost", ServicePorts.SESSION_PORT.getValue());
            sessionStub = (SessionService) reg.lookup("Session");

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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }




}
