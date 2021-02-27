package pl.umk.mat.plas.service;

import pl.umk.mat.plas.dao.factory.DAOFactory;
import pl.umk.mat.plas.dao.interfaces.InvitationDAO;
import pl.umk.mat.plas.dao.interfaces.MessageDAO;

public class MessageService {
    private MessageDAO messageDAO;

    public MessageService()  {
        DAOFactory daoFactory = DAOFactory.getDAOFactory();
        this.messageDAO = daoFactory.getMessageDAO();
    }

    public void addMessageInRoomThatHaveIdRoom(int idRoom, String message){
        messageDAO.addMessage(idRoom, message);
    }
}
