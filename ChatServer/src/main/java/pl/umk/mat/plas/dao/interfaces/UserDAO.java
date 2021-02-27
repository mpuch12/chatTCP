package pl.umk.mat.plas.dao.interfaces;

import java.sql.SQLException;

public interface UserDAO {

    boolean logInUser(String nick, String password);

    void logOutUser(String nick);

    boolean registerUser(String nick, String password) throws SQLException;

}
