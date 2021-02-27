package pl.umk.mat.plas.service;

import pl.umk.mat.plas.dao.factory.DAOFactory;
import pl.umk.mat.plas.dao.interfaces.UserDAO;

import java.sql.SQLException;

public class UserService {
    private UserDAO userDAO;

    public UserService(){
        DAOFactory daoFactory = DAOFactory.getDAOFactory();
        this.userDAO = daoFactory.getUserDAO();
    }
    public boolean tryLoginUser(String response){
        String [] data = response.split("#");
        String nick = data[1];
        String password = data[2];

        return userDAO.logInUser(nick, password);
    }

    public boolean tryRegisterUser(String response)  {
        String [] data = response.split("#");
        String nick = data[1];
        String password = data[2];
        boolean done = false;
        try {
            done = userDAO.registerUser(nick, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return done;
    }


    public void logoutUser(String nick) {
        userDAO.logOutUser(nick);
    }
}
