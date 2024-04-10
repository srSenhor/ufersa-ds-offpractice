package br.edu.ufersa.entities;

public class Request {

    private int opType;
    private String login;
    private long renavam;
    private String name;
    private int fab;
    private float price;
    private int category;

    public Request(int opType, String login, long renavam, String name, int fab, float price, int category) {
        this.opType = opType;
        this.login = login;
        this.renavam = renavam;
        this.name = name;
        this.fab = fab;
        this.price = price;
        this.category = category;
    }

    public int getOpType() {
        return opType;
    }

    public String getLogin() {
        return login;
    }

    public long getRenavam() {
        return renavam;
    }

    public String getName() {
        return name;
    }

    public int getFab() {
        return fab;
    }

    public float getPrice() {
        return price;
    }

    public int getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return opType + "/" + login + "/" + renavam + "/" + name + "/" + fab + "/" + price + "/" + category;
    }

    public static Request fromString(String text) {
        String fields[] = text.split("/");
        int opType = Integer.parseInt(fields[0]);
        String login = fields[1];
        long renavam = Long.parseLong(fields[2]);
        String name = fields[3];
        int fab = Integer.parseInt(fields[4]);
        float price = Float.parseFloat(fields[5]);
        int category = Integer.parseInt(fields[6]);

        return new Request(opType, login, renavam, name, fab, price,  category);
    }
}

