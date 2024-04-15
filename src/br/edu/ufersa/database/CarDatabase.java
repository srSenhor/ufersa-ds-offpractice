package br.edu.ufersa.database;

import br.edu.ufersa.utils.ServicePorts;

public class CarDatabase {
    public static void main(String[] args) {
        new CarDatabaseImpl(ServicePorts.DATABASE_PORT.getValue());
    }
}
