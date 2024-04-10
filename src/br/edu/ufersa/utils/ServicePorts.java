package br.edu.ufersa.utils;

public enum ServicePorts {
    AUTH_PORT(60000), DEALER_PORT(60001), SESSION_PORT(60002), DATABASE_PORT(60003);

    private final int value;
    private ServicePorts(int value) { this.value = value; }

    public int getValue() {return this.value; }
}
