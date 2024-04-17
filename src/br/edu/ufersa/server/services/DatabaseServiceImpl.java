package br.edu.ufersa.server.services;

import java.rmi.RemoteException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import br.edu.ufersa.entities.Car;
import br.edu.ufersa.server.services.skeletons.DatabaseService;
import br.edu.ufersa.utils.CarType;

public class DatabaseServiceImpl implements DatabaseService {

    // TODO Transformar tudo em RMI

    private static ConcurrentHashMap<Long, Car> cars;
    private static ConcurrentHashMap<String, Integer> cars_stock;
    
    public DatabaseServiceImpl() {
        cars = new ConcurrentHashMap<>();
        cars_stock = new ConcurrentHashMap<>();

        this.init();
    }

    public Car find(long renavam) {
        Car car = cars.get(renavam);

        if (car != null) {
            System.out.println("DATABASE: Founded -> \n" + car.toString());
            return car;
        }
        
        System.err.println("DATABASE: ERROR! This car isn't in the database");
        return car;
    }
    
    public List<Car> findCar(String name) {
        List<Car> car_list = new LinkedList<>();
        
        if (cars.isEmpty()) {
            System.out.println("DATABASE: ERROR! There is no cars in the database");
        } else {
            cars.forEach((Long renavam, Car car) -> {
                if (car.getNome().equalsIgnoreCase(name)) {
                    car_list.add(car);
                }
            });

            System.out.println("DATABASE: Returning the list of car " + name);
        }

        return car_list;
    }

    public String getStock() {
        if (cars.size() == 0) {
            System.err.println("DATABASE: ERROR! There is no cars in database");
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

    public boolean create(int carType, long renavam, String nome, int fab, float price) throws RemoteException {

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

    public boolean update(int carType, long renavam, String nome, int fab, float price) throws RemoteException {
        
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

    public boolean delete(long renavam) {

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

    public List<Car> getSorted(){
        List<Car> car_list = new LinkedList<>();
        
        if (cars.isEmpty()) {
            System.out.println("DATABASE: ERROR! There is no cars in the database");
        } else {
            cars.forEach((Long renavam, Car car) -> {
                car_list.add(car);
            });

            car_list.sort(new Comparator<Car>() {
                @Override
                public int compare(Car o1, Car o2) {
                    return o1.getNome().compareTo(o2.getNome());
                    
                }            
            });
            System.out.println("DATABASE: Returning the sorted list of cars");
        }
        
        return car_list;
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
