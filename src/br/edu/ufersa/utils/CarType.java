package br.edu.ufersa.utils;

public enum CarType {
    ECONOMY(0), INTERMEDIATE(1), EXECUTIVE(2);

    private final int value;
    private CarType(int value) { this.value = value; }

    public int getValue() {return this.value; }
}
