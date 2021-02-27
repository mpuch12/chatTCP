package pl.umk.mat.plas.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

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

    public Room(int idRoom, String name) {
        this.idRoom = idRoom;
        this.name = name;
    }

    public Room(int idRoom, String name, ArrayList<String> roomMessages, boolean isGroupRoom) {
        this.idRoom = idRoom;
        this.name = name;
        this.roomMessages = roomMessages;
        this.isGroupRoom = isGroupRoom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return idRoom == room.idRoom &&
                isGroupRoom == room.isGroupRoom &&
                Objects.equals(name, room.name) &&
                Objects.equals(roomMessages, room.roomMessages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idRoom, name, isGroupRoom, roomMessages);
    }

    @Override
    public String toString() {
        return "Room{" +
                "idRoom=" + idRoom +
                ", name='" + name + '\'' +
                ", roomMessages=" + roomMessages +
                '}';
    }
}
