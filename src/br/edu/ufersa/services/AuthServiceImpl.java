package br.edu.ufersa.services;

import java.rmi.RemoteException;
import java.util.HashMap;

import br.edu.ufersa.entities.User;
import br.edu.ufersa.services.skeletons.AuthService;
import br.edu.ufersa.utils.UserType;

public class AuthServiceImpl implements AuthService {

    private static HashMap<String, User> users;

    public AuthServiceImpl() {
        users = new HashMap<>();
        this.init();
    }

    @Override
    public UserType authUser(String login, String password) throws RemoteException {
        User user = users.get(login);
        if (user == null) { return UserType.UNDEFINED; }
        if (user.getPassword().equals(password) && !user.isLogged()) {
            user.userLoggedIn();
            return user.getType();
        }
        return UserType.UNDEFINED;
    }

    @Override
    public void recordClient(String login, String password, UserType type) throws RemoteException {
        users.put(login, new User(login, password, type));
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
