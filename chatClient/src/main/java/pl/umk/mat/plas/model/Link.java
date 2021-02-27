package pl.umk.mat.plas.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class Link implements Serializable {
    private static final long serialVersionUID = 2L;
    private int idRoom;
    private ArrayList<String> nicknames;

    public Link(){}

    public Link(int idRoom, ArrayList<String> nicknames) {
        this.idRoom = idRoom;
        this.nicknames = nicknames;
    }

    public int getIdRoom() {
        return idRoom;
    }

    public void setIdRoom(int idRoom) {
        this.idRoom = idRoom;
    }

    public ArrayList<String> getNicknames() {
        return nicknames;
    }

    public void setNicknames(ArrayList<String> nicknames) {
        this.nicknames = nicknames;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Link link = (Link) o;
        return idRoom == link.idRoom &&
                Objects.equals(nicknames, link.nicknames);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idRoom, nicknames);
    }

    @Override
    public String toString() {
        return "Link{" +
                "idRoom=" + idRoom +
                ", nicknames=" + nicknames.toString() +
                '}';
    }
}
