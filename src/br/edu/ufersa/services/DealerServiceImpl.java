package br.edu.ufersa.services;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import br.edu.ufersa.entities.Car;
import br.edu.ufersa.services.skeletons.DealerService;
import br.edu.ufersa.utils.CarType;

public class DealerServiceImpl implements DealerService {

    private static HashMap<Long, Car> cars;

    public DealerServiceImpl() {
        cars = new HashMap<>();
        this.init();
    }

    @Override
    public String searchByName(String name) throws RemoteException {

        StringBuilder response = new StringBuilder();

        List<Car> named_cars = new ArrayList<>();

        cars.forEach((Long renavam, Car car) -> {
            if(car.getNome().equalsIgnoreCase(name)) {
                named_cars.add(car);
            }
        });

        if (named_cars.isEmpty()) {
            return "no cars available for this name";
        } else {
            for (Car car : named_cars) {
                response.append(car.toString());
                response.append("\n");
            }

            return response.toString();
        }
    }

    @Override
    public String searchByRenavam(long renavam) throws RemoteException {

        Car searchedCar = cars.get(renavam);

        if (searchedCar == null) {
            return "cannot find the vehicle for renavam " + renavam;
        } else {
            return searchedCar.toString();
        }

    }

    @Override
    public String list() throws RemoteException {
        
        StringBuilder response = new StringBuilder();

        List<Car> car_list = getSorted();

        if (car_list.isEmpty()) {
            return "no cars available in the system";
        } else {
            for (Car car : car_list) {
                response.append(car.toString());
                response.append("\n");
            }

            return response.toString();
        }
    }

    @Override
    public String listByCategory() throws RemoteException {

        StringBuilder response = new StringBuilder();

        List<Car> economy_cars = new LinkedList<>();
        List<Car> intermediate_cars = new LinkedList<>();
        List<Car> executive_cars = new LinkedList<>();

        List<Car> car_list = getSorted();

        if (car_list.isEmpty()) {
            return "no cars available in the system";
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

            response.append("ECONOMY CARS:\n");
            for (Car car : economy_cars) {
                response.append(car.toString());
                response.append("\n");
            }
            response.append("\n");

            response.append("INTERMEDIATE CARS:\n");
            for (Car car : intermediate_cars) {
                response.append(car.toString());
                response.append("\n");
            }
            response.append("\n");

            response.append("EXECUTIVE CARS:\n");
            for (Car car : executive_cars) {
                response.append(car.toString());
                response.append("\n");
            }
            response.append("\n");

            return response.toString();
        }
    }
    
    @Override
    public String add(CarType categoria, long renavam, String nome, int ano_fab, float preco) throws RemoteException {

        Car car = cars.get(renavam);

        if (car == null) {
            car = new Car(categoria, renavam, nome, ano_fab, preco);
            cars.put(renavam, car);
            return "New car: " + car.toString();
        } else {
            return "This car is already registred";
        }

    }

    @Override
    public String remove(long renavam) throws RemoteException {
        
        Car car = cars.get(renavam);

        if (car == null) {
            return "This car isn't registred";
        } else {
            cars.remove(renavam);
            return "Removed car: " + car;
        }

    }

    @Override
    public String update(CarType categoria, long renavam, String nome, int ano_fab, float preco) throws RemoteException {
        
        Car car = cars.get(renavam);

        if (car == null) {
            return "This car isn't registred";
        } else {
            car.setAnoFab(ano_fab);
            car.setCategoria(categoria);
            car.setNome(nome);
            car.setPreco(preco);
            
            cars.put(renavam, car);
            return "Updated car: " + car.toString();
        }

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

    private void init() {

        try {

            add(CarType.ECONOMY, 72439120382l, "nissan march", 2012, 86000f);
            add(CarType.INTERMEDIATE, 51029874625l, "toyota etios", 2017, 70000f);
            add(CarType.ECONOMY, 14243939238l, "ford ka", 2009, 90000f);
            add(CarType.EXECUTIVE, 29018475092l, "chevrolet cruze", 2021, 40000f);
            add(CarType.ECONOMY, 73849201742l, "chevrolet onix", 2015, 65000f);
            add(CarType.EXECUTIVE, 45829637102l, "toyota corolla", 2022, 42000f);
            add(CarType.INTERMEDIATE, 37905164293l, "ford ka sedan", 2018, 60000f);
            add(CarType.EXECUTIVE, 90583274625l, "honda civic", 2020, 45000f);
            add(CarType.ECONOMY, 62458390271l, "hyundai hb20s", 2012, 80000f);
            add(CarType.ECONOMY, 87251903416l, "fiat novo uno", 2010, 95000f);
            add(CarType.INTERMEDIATE, 18573964205l, "renault logan", 2016, 62000f);
            add(CarType.EXECUTIVE, 14926874359l, "audi a3", 2019, 50000f);
            
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }


}
