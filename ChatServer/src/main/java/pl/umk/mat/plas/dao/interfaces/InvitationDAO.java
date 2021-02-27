package pl.umk.mat.plas.dao.interfaces;

import java.util.ArrayList;

public interface InvitationDAO {

    boolean saveInvitation(String nickname, String invitedNick);

    boolean checkIfDontExist(String nickname, String invitedNick);

    ArrayList<String> readInvitationListForUser(String nick);

    void deleteInvitation(String nick, String chosenNick);
}
