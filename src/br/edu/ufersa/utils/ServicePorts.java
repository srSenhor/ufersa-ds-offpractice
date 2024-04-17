package br.edu.ufersa.utils;

public enum ServicePorts {
    AUTH_PORT(60000), DEALER_PORT(60050), SESSION_PORT(60100), DATABASE_PORT(60150), PROXY_PORT(60200);

    private final int value;
    private ServicePorts(int value) { this.value = value; }

    public int getValue() {return this.value; }
}
