package br.edu.ufersa.utils;

public enum UserType {
    CLIENT(0), EMPLOYEE(1), UNDEFINED(-1);

    private final int value;
    private UserType(int value) { this.value = value; }

    public int getValue() {return this.value; }
}
