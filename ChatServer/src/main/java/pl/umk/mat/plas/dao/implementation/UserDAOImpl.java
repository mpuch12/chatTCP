package pl.umk.mat.plas.dao.implementation;

import pl.umk.mat.plas.dao.interfaces.UserDAO;

import pl.umk.mat.plas.service.RoomService;
import pl.umk.mat.plas.util.MyDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAOImpl implements UserDAO {
    private Connection connection;

    public UserDAOImpl() throws SQLException {
        connection = MyDataSource.getConnection();
    }

    @Override
    public boolean logInUser(String nickname, String password) {
        final String FIND_USER_BY_NICK_AND_PASSWORD = "SELECT nickname, password FROM user WHERE nickname = ? AND password = ?;";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_USER_BY_NICK_AND_PASSWORD);
            preparedStatement.setString(1,nickname);
            preparedStatement.setString(2,password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                setUserActive(nickname);
                return true;
            }else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }

    private void setUserActive(String nick){
        final String UPDATE = "UPDATE user SET isActive = 1 WHERE nickname = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE);
            preparedStatement.setString(1,nick);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void logOutUser(String nick) {
        final String UPDATE = "UPDATE user SET isActive = 0 WHERE nickname = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE);
            preparedStatement.setString(1, nick);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean registerUser(String nick, String password){
        RoomService roomService = new RoomService();
        final String REGISTER_USER = "INSERT INTO user VALUES(?,?,0)";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(REGISTER_USER);
            preparedStatement.setString(1, nick);
            preparedStatement.setString(2, password);
            int temp =  preparedStatement.executeUpdate();
            return temp > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
