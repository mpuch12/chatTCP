package pl.umk.mat.plas.dao.factory;

import pl.umk.mat.plas.dao.implementation.*;
import pl.umk.mat.plas.dao.interfaces.*;

import java.sql.SQLException;

public class MysqlDAOFactory  extends  DAOFactory{
    @Override
    public ConnectionDAO getConnectionDAO() {
        try {
            return new ConnectionDAOImpl();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public MessageDAO getMessageDAO() {
        try {
            return new MessageDAOImpl();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public RoomDAO getRoomDAO() {
        return new RoomDAOImpl();
    }

    @Override
    public UserDAO getUserDAO() {
        try {
            return new UserDAOImpl();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public InvitationDAO getInvitationDAO() {
        try {
            return new InvitationDAOImpl();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
