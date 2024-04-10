package br.edu.ufersa.server.services;

import java.rmi.RemoteException;
import java.util.HashMap;

import br.edu.ufersa.entities.User;
import br.edu.ufersa.server.services.skeletons.AuthService;
import br.edu.ufersa.utils.UserType;

public class AuthServiceImpl implements AuthService {

    private static HashMap<String, User> users;

    public AuthServiceImpl() {
        users = new HashMap<>();
        this.init();
    }

    @Override
    public User authUser(String login, String password) throws RemoteException {
        User user = users.get(login);
        if (user == null) { return null; }
        if (user.getPassword().equals(password) && !user.isLogged()) {
            user.userLoggedIn();
            return user;
        }
        return null;
    }

    @Override
    public void recordClient(String login, String password, UserType type) throws RemoteException {
        users.put(login, new User(login, password, type));
    }

    @Override
    public boolean logout(String login) throws RemoteException {
        User user = users.get(login);
        if (user == null || user.isLogged() == false ) { return false; }
        user.userLoggedOut();
        return true;
    }

    private void init(){
        try {
            recordClient("babaganush", "senha123", UserType.CLIENT);
            recordClient("silvao", "senha456", UserType.EMPLOYEE);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


}
