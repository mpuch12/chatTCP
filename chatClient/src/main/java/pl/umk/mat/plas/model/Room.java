package pl.umk.mat.plas.model;

import pl.umk.mat.plas.networking.ManageConnection;

import java.io.Serializable;
import java.util.ArrayList;

public class Room implements Serializable {
    static final long serialVersionUID = 1L;
    private int idRoom;
    private String name;
    private boolean isGroupRoom;
    private ArrayList<String> roomMessages;

    public boolean isGroupRoom() {
        return isGroupRoom;
    }

    public void setGroupRoom(boolean groupRoom) {
        isGroupRoom = groupRoom;
    }

    public int getIdRoom() {
        return idRoom;
    }

    public void setIdRoom(int idRoom) {
        this.idRoom = idRoom;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getRoomMessages() {
        return roomMessages;
    }

    public void setRoomMessages(ArrayList<String> roomMessages) {
        this.roomMessages = roomMessages;
    }

    public Room(int idRoom, String name, ArrayList<String> roomMessages) {
        this.idRoom = idRoom;
        this.name = name;
        this.roomMessages = roomMessages;
    }

    @Override
    public String toString() {
        String actualUserNick = ManageConnection.clientApp.getNick();
        String[] regex = name.split("#");
        if(isGroupRoom == true)
            return name;
        else {
            if(regex[0].equals(actualUserNick))
                return regex[1];
            else
                return regex[0];
        }
    }
}
