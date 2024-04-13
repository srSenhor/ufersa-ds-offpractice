package br.edu.ufersa.entities;

// import br.edu.ufersa.utils.UserType;

public class Request {

    private int opType;
    private int userType;
    private String login;
    private long renavam;
    private String name;
    private int fab;
    private float price;
    private int category;

    public Request(int opType, int userType, String login, long renavam, String name, int fab, float price, int category) {
        this.opType = opType;
        this.userType = userType;
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

    public int getUserType() {
        return userType;
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
        return opType + "/" + userType + "/" + login + "/" + renavam + "/" + name + "/" + fab + "/" + price + "/" + category;
    }

    public static Request fromString(String text) {
        String fields[] = text.split("/");
        int opType = Integer.parseInt(fields[0]);
        int userType = Integer.parseInt(fields[1]);
        String login = fields[2];
        long renavam = Long.parseLong(fields[3]);
        String name = fields[4];
        int fab = Integer.parseInt(fields[5]);
        float price = Float.parseFloat(fields[6]);
        int category = Integer.parseInt(fields[7]);

        return new Request(opType, userType, login, renavam, name, fab, price,  category);
    }
}

