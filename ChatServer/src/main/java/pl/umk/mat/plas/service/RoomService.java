package pl.umk.mat.plas.service;

import pl.umk.mat.plas.dao.factory.DAOFactory;
import pl.umk.mat.plas.dao.interfaces.MessageDAO;
import pl.umk.mat.plas.dao.interfaces.RoomDAO;
import pl.umk.mat.plas.model.Room;

import java.util.ArrayList;

public class RoomService  {
    private RoomDAO roomDAO;
    private MessageDAO messageDAO;

    public RoomService()  {
        DAOFactory daoFactory = DAOFactory.getDAOFactory();
        this.roomDAO = daoFactory.getRoomDAO();
        this.messageDAO = daoFactory.getMessageDAO();
    }

    public void deleteRoom(int idRoom){
        roomDAO.deleteRoom(idRoom);
    }

    public boolean createRoom(String name, boolean isGroup)  {
        return roomDAO.createRoom(name, isGroup);
    }

    public String readRoomName(int idRoom){
        return roomDAO.readRoomName(idRoom);
    }

    public ArrayList<Room> readUserRooms(String nick){
        ConnectionService connectionService = new ConnectionService();
        return creatRoomObjects(connectionService.readUserConnectionsWithRooms(nick));
    }

    public int readRoomID(String nick){
        return roomDAO.readRoomID(nick);
    }


    private ArrayList<Room> creatRoomObjects(ArrayList<Integer> idRoom){
        ArrayList<Room> userRooms = new ArrayList<>();
        for(int id: idRoom){
            ArrayList<String> messages = messageDAO.readRoomMessages(id);
            String name = readRoomName(id);
            boolean isGroupRoom = readIsGroupRoom(id);
            userRooms.add(new Room(id, name, messages, isGroupRoom));
        }
        return userRooms;
    }

    private boolean readIsGroupRoom(int id) {
        return roomDAO.readRoomIsGroupRoom(id);
    }

    public void renameRoom(int idRoom, String name) {
        roomDAO.renameRoom(idRoom, name);
    }
}
