package pl.umk.mat.plas.dao.implementation;

import pl.umk.mat.plas.dao.interfaces.ConnectionDAO;
import pl.umk.mat.plas.model.Link;
import pl.umk.mat.plas.util.MyDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ConnectionDAOImpl implements ConnectionDAO {
    private Connection connection;

    public ConnectionDAOImpl () throws SQLException {
        connection = MyDataSource.getConnection();
    }
    @Override
    public boolean create(String nick, int IDRoom)  {
        final String CREATE = "INSERT INTO connection(nickname,idRoom) VALUES(?,?)";
        int temp = 0;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(CREATE);
            preparedStatement.setString(1,nick);
            preparedStatement.setInt(2,IDRoom);
            temp = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return temp > 0;
    }

    @Override
    public ArrayList<Integer> readUserRoomsID(String nick) {
        final String SELECT = "SELECT idRoom FROM connection WHERE nickname = ?";
        ArrayList<Integer> idRoom = new ArrayList<>();
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT);
            preparedStatement.setString(1, nick);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                idRoom.add(resultSet.getInt("idRoom"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return idRoom;
    }

    @Override
    public ArrayList<String> nicksFromRoomID(int idRoom) {
        final String SELECT = "SELECT nickname FROM connection WHERE idRoom = ?";
        ArrayList<String> nicks = new ArrayList<>();
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT);
            preparedStatement.setInt(1, idRoom);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                nicks.add(resultSet.getString("nickname"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nicks;
    }

    @Override
    public ArrayList<Link> readLinkList() {
        final String READ_ALL_ID = "SELECT DISTINCT idRoom FROM connection";
        final String READ_NICKNAME_IN_ID = "SELECT nickname FROM connection WHERE idRoom = ?";
        ArrayList<Link> linkList = new ArrayList<>();

        try{
            PreparedStatement preparedStatement = connection.prepareStatement(READ_ALL_ID);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                Link link = new Link();
                ArrayList<String> nicknames = new ArrayList<>();
                int idRoom = resultSet.getInt("idRoom");
                link.setIdRoom(idRoom);
                PreparedStatement preparedStatement1 = connection.prepareStatement(READ_NICKNAME_IN_ID);
                preparedStatement1.setInt(1, idRoom);
                ResultSet resultSet1 = preparedStatement1.executeQuery();
                while(resultSet1.next()){
                    nicknames.add(resultSet1.getString("nickname"));
                }
                link.setNicknames(nicknames);
                linkList.add(link);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return  linkList;
    }

    @Override
    public void deleteConnection(int idRoom, String nickname) {
        final String DELETE = "DELETE FROM connection WHERE idRoom = ? AND nickname = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE);
            preparedStatement.setInt(1,idRoom);
            preparedStatement.setString(2,nickname);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
