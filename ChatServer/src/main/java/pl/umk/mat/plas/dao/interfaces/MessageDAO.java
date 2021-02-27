package pl.umk.mat.plas.dao.interfaces;

import java.util.ArrayList;

public interface MessageDAO {
    ArrayList<String> readRoomMessages(int idRoom);

    void addMessage(int idRoom, String message);
}
