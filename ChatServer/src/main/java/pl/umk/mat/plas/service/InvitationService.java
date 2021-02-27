package pl.umk.mat.plas.service;

import pl.umk.mat.plas.dao.factory.DAOFactory;
import pl.umk.mat.plas.dao.interfaces.ConnectionDAO;
import pl.umk.mat.plas.dao.interfaces.InvitationDAO;

import java.util.ArrayList;

public class InvitationService {
    private InvitationDAO invitationDAO;

    public InvitationService()  {
        DAOFactory daoFactory = DAOFactory.getDAOFactory();
        this.invitationDAO = daoFactory.getInvitationDAO();
    }

    public boolean checkIfDontExist(String nickname, String invitedNick){
        return invitationDAO.checkIfDontExist(nickname, invitedNick);
    }

    public boolean saveInvitation(String nickname, String invitedNick){
        return invitationDAO.saveInvitation(nickname, invitedNick);
    }

    public ArrayList<String> readInvitationListForUser(String nick){
        return invitationDAO.readInvitationListForUser(nick);
    }

    public void deleteInvitation(String nick, String chosenNick){
         invitationDAO.deleteInvitation(nick, chosenNick);
    }
}
