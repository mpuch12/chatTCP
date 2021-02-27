package pl.umk.mat.plas.service;

import pl.umk.mat.plas.dao.factory.DAOFactory;
import pl.umk.mat.plas.dao.interfaces.ConnectionDAO;
import pl.umk.mat.plas.model.Link;


import java.util.ArrayList;

public class ConnectionService {
    private ConnectionDAO connectionDAO;

    public ConnectionService()  {
        DAOFactory daoFactory = DAOFactory.getDAOFactory();
        this.connectionDAO = daoFactory.getConnectionDAO();
    }

    public boolean createUserConnection(String nick, int idRoom){
        return connectionDAO.create(nick,idRoom);
    }

    public ArrayList<Integer> readUserConnectionsWithRooms(String nick){
        return connectionDAO.readUserRoomsID(nick);
    }

    public void createConnectionBetweenTwoUsers(String nick1, String nick2){

    }

    public ArrayList<String> readNickFromRoomThatHaveIdRoom(int idRoom){
        return connectionDAO.nicksFromRoomID(idRoom);
    }

    public ArrayList<Link> readLinkList() {
        return connectionDAO.readLinkList();
    }

    public void deleteUserConnection(int idRoom, String nickname) {
        connectionDAO.deleteConnection(idRoom, nickname);
    }
}
