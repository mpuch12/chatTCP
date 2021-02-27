package pl.umk.mat.plas.dao.implementation;

import pl.umk.mat.plas.dao.interfaces.MessageDAO;
import pl.umk.mat.plas.util.MyDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MessageDAOImpl implements MessageDAO {
    private Connection connection;

    public MessageDAOImpl () throws SQLException {
        connection = MyDataSource.getConnection();
    }

    @Override
    public ArrayList<String> readRoomMessages(int idRoom) {
        final String SELECT = "SELECT message FROM messageList WHERE idRoom = ?";
        ArrayList<String> messages = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT);
            preparedStatement.setInt(1, idRoom);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                messages.add(resultSet.getString("message"));
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;

    }

    @Override
    public void addMessage(int idRoom, String message) {
        final String INSERT = "INSERT INTO messageList VALUES (?,?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT);
            preparedStatement.setInt(2, idRoom);
            preparedStatement.setString(1, message);
            preparedStatement.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
