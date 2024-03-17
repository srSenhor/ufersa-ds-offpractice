package br.edu.ufersa.entities;

import java.io.Serializable;

import br.edu.ufersa.utils.UserType;

public class User implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private boolean isLogged;
    private String login;
    private String password;
    private UserType type;

    public User(String login, String password, UserType type) {
        this.isLogged = false;
        this.login = login;
        this.password = password;
        this.type = type;
    }

    

    public boolean isLogged() {
        return isLogged;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public UserType getType() {
        return type;
    }

    public void userLoggedIn(){
        this.isLogged = true;
    }
    public void userLoggedOut(){
        this.isLogged = false;
    }
}
