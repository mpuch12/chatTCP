package pl.umk.mat.plas.dao.interfaces;

import pl.umk.mat.plas.model.Link;

import java.util.ArrayList;

public interface ConnectionDAO {
    boolean create(String nick, int IDRoom);

    ArrayList<Integer> readUserRoomsID(String nick);

    ArrayList<String> nicksFromRoomID(int idRoom);

    ArrayList<Link> readLinkList();

    void deleteConnection(int idRoom, String nickname);

}
