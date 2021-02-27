package pl.umk.mat.plas.dao.factory;

import pl.umk.mat.plas.dao.interfaces.*;
import pl.umk.mat.plas.exception.NoSuchDbTypeException;

public abstract class DAOFactory {
    public static final int MYSQL_DAO_FACTORY = 1;

    public abstract ConnectionDAO getConnectionDAO();
    public abstract MessageDAO getMessageDAO();
    public abstract RoomDAO getRoomDAO();
    public abstract UserDAO getUserDAO();
    public abstract InvitationDAO getInvitationDAO();


    public static DAOFactory getDAOFactory() {
        DAOFactory factory = null;
        try {
            factory = getDAOFactory(MYSQL_DAO_FACTORY);
        } catch (NoSuchDbTypeException e) {
            e.printStackTrace();
        }
        return factory;
    }

    private static DAOFactory getDAOFactory(int type) throws NoSuchDbTypeException {
        switch (type) {
            case MYSQL_DAO_FACTORY:
                return new MysqlDAOFactory();
            default:
                throw new NoSuchDbTypeException();
        }
    }


}
