package pl.umk.mat.plas.dao.implementation;

import pl.umk.mat.plas.dao.interfaces.RoomDAO;
import pl.umk.mat.plas.model.Room;
import pl.umk.mat.plas.util.MyDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class RoomDAOImpl implements RoomDAO {
    private Connection connection;

    public RoomDAOImpl ()  {
        connection = MyDataSource.getConnection();
    }

    @Override
    public boolean createRoom(String name, boolean isGroup)  {
        final String CREATE = "INSERT INTO room(name, isGroupRoom) VALUES(?,?)";
        int temp = 0;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(CREATE);
            preparedStatement.setString(1,name);
            preparedStatement.setBoolean(2,isGroup);
            temp = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return temp > 0 ;
    }

    @Override
    public int readRoomID(String nick) {
        final String READ_ID = "SELECT idRoom FROM room WHERE name = ?";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(READ_ID);
            preparedStatement.setString(1,nick);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next())
                return resultSet.getInt("idRoom");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public String readRoomName(int idRoom) {
        final String READ_NAME = "SELECT name FROM room WHERE idRoom = ?";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(READ_NAME);
            preparedStatement.setInt(1,idRoom);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next())
                return resultSet.getString("name");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deleteRoom(int idRoom) {
        final String DELETE = "DELETE FROM room WHERE idRoom = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE);
            preparedStatement.setInt(1,idRoom);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override

    public boolean readRoomIsGroupRoom(int idRoom) {
        final String READ_NAME = "SELECT isGroupRoom FROM room WHERE idRoom = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(READ_NAME);
            preparedStatement.setInt(1,idRoom);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next())
                return resultSet.getBoolean("isGroupRoom");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void renameRoom(int idRoom, String name) {
        final String UPDATE = "UPDATE room SET name = ? WHERE idRoom = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE);
            preparedStatement.setString(1,name);
            preparedStatement.setInt(2, idRoom);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
