package pl.umk.mat.plas.dao.interfaces;

import pl.umk.mat.plas.model.Room;

import java.sql.SQLException;
import java.util.ArrayList;

public interface RoomDAO {
    //ArrayList<Room> getAllRooms();

    boolean createRoom(String name, boolean isGroup);

    int readRoomID(String nick);

    String readRoomName(int idRoom);

    void deleteRoom(int idRoom);

    boolean readRoomIsGroupRoom(int id);

    void renameRoom(int idRoom, String name);

}
