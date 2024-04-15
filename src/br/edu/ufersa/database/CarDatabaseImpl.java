package br.edu.ufersa.database;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.HashMap;

import br.edu.ufersa.entities.Car;
import br.edu.ufersa.entities.Request;
import br.edu.ufersa.utils.CarType;

public class CarDatabaseImpl  {
    
    private ServerSocket server;
    private Socket client;
    private InetAddress inet;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private int port;

    private static HashMap<Long, Car> cars;
    private static HashMap<String, Integer> cars_stock;
    
    public CarDatabaseImpl(int port) {
        this.port = port;
        cars = new HashMap<>();
        cars_stock = new HashMap<>();

        this.init();
        this.exec();
    }

    public void exec() {
        
        try {
            
            server = new ServerSocket(port);
            this.inet = server.getInetAddress();

            System.out.println("DATABASE: Initialized at " 
            + inet.getHostName() 
            + ":"
            + this.port);

            System.out.println("DATABASE: Waiting for connections...");
            
            client = server.accept();
            System.out.println("DATABASE: Connected with " 
            + client.getInetAddress().getHostName()
            + ":"
            + client.getPort());
            

            output = new ObjectOutputStream(client.getOutputStream());
            input = new ObjectInputStream(client.getInputStream());

            
            while (true) {
                Request req = (Request) input.readObject();

                switch (req.getOpType()) {
                    case 1:
                        output.writeObject(find(req.getRenavam()));
                        break;
                    case 3:
                        String quant = (req.getCategory() == 1) ?
                        Integer.toString(cars.size()) :
                        this.getStock();

                        output.writeObject(quant);
                        break;
                    case 5:
                        output.writeObject(create(req.getCategory(), req.getRenavam(), req.getName(), req.getFab(), req.getPrice()));
                        break;
                    case 6:
                        output.writeObject(update(req.getCategory(), req.getRenavam(), req.getName(), req.getFab(), req.getPrice()));
                        break;
                    case 7:
                        output.writeObject(delete(req.getRenavam()));
                        break;
                    default:
                        break;
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                input.close();
                output.close();
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    private Car find(long renavam) {
        Car car = cars.get(renavam);

        if (car != null) {
            System.out.println("DATABASE: Founded -> \n" + car.toString());
            return car;
        }
        
        System.err.println("DATABASE: ERROR! This car isn't in the database");
        return car;
    }
    
    private String getStock() {
        if (cars.size() == 0) {
            System.err.println("DATABASE: ERROR! There are no cars in database");
            return "There is no cars registred...";
        }

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
        
        System.out.println("DATABASE: Sending stock");
        return responseString.toString();
        
    }

    private boolean create(int carType, long renavam, String nome, int fab, float price) throws RemoteException {

        Car car = cars.get(renavam);

        if (car != null) {
            System.err.println("DATABASE: ERROR! This car already exists in the database");
            return false;
        }
        
        car = new Car(CarType.values()[carType], renavam, nome.toUpperCase(), fab, price);
        cars.put(renavam, car);

        Integer quant_car = cars_stock.get(nome.toUpperCase());
        if (quant_car == null) {
            cars_stock.put(nome.toUpperCase(), 1);
        } else {
            cars_stock.put(nome.toUpperCase(), ++quant_car);
        }

        return true;

    }

    private boolean update(int carType, long renavam, String nome, int fab, float price) throws RemoteException {
        
        Car car = cars.get(renavam);

        if (car == null) {
            System.err.println("DATABASE: ERROR! This car isn't in the database");
            return false;
        }

        Integer quant_car = cars_stock.get(car.getNome().toUpperCase());

            if (quant_car > 0) {
                cars_stock.put(car.getNome(), --quant_car);
            } 

            car.setAnoFab(fab);
            car.setCategoria(CarType.values()[carType]);
            car.setNome(nome);
            car.setPreco(price);
            
            cars.put(renavam, car);

            quant_car = cars_stock.get(car.getNome());
            
            if (quant_car == null) {
                cars_stock.put(car.getNome().toUpperCase(), 1);
            } else {
                cars_stock.put(car.getNome().toUpperCase(), ++quant_car);
            }

            return true;
    }

    private boolean delete(long renavam) {

        Car car = cars.remove(renavam);
        
        if (car == null) {
            System.err.println("DATABASE: ERROR! Cannot find this car in database");
            return false;
        } else {
            Integer quant_car = cars_stock.get(car.getNome());

            if (quant_car > 0) {
                cars_stock.put(car.getNome(), --quant_car);
            } 

            return true;
        }


    }

    private void init() {
        try {

            this.create(CarType.EXECUTIVE.getValue(), 14926874359l, "audi a3", 2019, 50000f);
            this.create(CarType.ECONOMY.getValue(), 72439120382l, "nissan march", 2012, 86000f);
            this.create(CarType.INTERMEDIATE.getValue(), 51029874625l, "toyota etios", 2017, 70000f);
            this.create(CarType.ECONOMY.getValue(), 14243939238l, "ford ka", 2009, 90000f);
            this.create(CarType.EXECUTIVE.getValue(), 29018475092l, "chevrolet cruze", 2021, 40000f);
            this.create(CarType.ECONOMY.getValue(), 73849201742l, "nissan march", 2015, 65000f);
            this.create(CarType.EXECUTIVE.getValue(), 45829637102l, "toyota corolla", 2022, 42000f);
            this.create(CarType.INTERMEDIATE.getValue(), 37905164293l, "ford ka sedan", 2018, 60000f);
            this.create(CarType.EXECUTIVE.getValue(), 90583274625l, "honda civic", 2020, 45000f);
            this.create(CarType.ECONOMY.getValue(), 62458390271l, "hyundai hb20s", 2012, 80000f);
            this.create(CarType.ECONOMY.getValue(), 87251903416l, "fiat novo uno", 2010, 95000f);
            this.create(CarType.INTERMEDIATE.getValue(), 18573964205l, "renault logan", 2016, 62000f);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
