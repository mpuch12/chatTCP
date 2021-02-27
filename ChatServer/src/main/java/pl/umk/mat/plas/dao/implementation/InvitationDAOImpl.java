package pl.umk.mat.plas.dao.implementation;

import pl.umk.mat.plas.dao.interfaces.InvitationDAO;
import pl.umk.mat.plas.util.MyDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class InvitationDAOImpl implements InvitationDAO {
    private Connection connection;

    public InvitationDAOImpl () throws SQLException {
        connection = MyDataSource.getConnection();
    }

    public boolean saveInvitation(String nickname, String invitedNick){
        final String INSERT = "INSERT INTO invitation(nickname, invitedNickname) VAlUES (?,?)";
        int temp = 0;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT);
            preparedStatement.setString(1,nickname);
            preparedStatement.setString(2,invitedNick);
            temp = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return temp > 0;
    }

    @Override
    public boolean checkIfDontExist(String nickname, String invitedNick) {
        final String SELECT = "SELECT * FROM invitation WHERE nickname = ? AND invitedNickname = ?";
        int temp = 0;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT);
            preparedStatement.setString(1,nickname);
            preparedStatement.setString(2,invitedNick);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                temp++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return temp == 0;
    }

    @Override
    public ArrayList<String> readInvitationListForUser(String nick) {
        final String SELECT = "SELECT * FROM invitation WHERE nickname != ? AND invitedNickname = ?";
        ArrayList<String> invitationList = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT);
            preparedStatement.setString(1,nick);
            preparedStatement.setString(2,nick);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                invitationList.add(resultSet.getString("nickname"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return invitationList;
    }

    @Override
    public void deleteInvitation(String nick, String chosenNick) {
        final String DELETE = "DELETE FROM invitation WHERE nickname = ? AND invitedNickname = ?";
        try {

            PreparedStatement preparedStatement = connection.prepareStatement(DELETE);
            preparedStatement.setString(1,chosenNick);
            preparedStatement.setString(2,nick);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
