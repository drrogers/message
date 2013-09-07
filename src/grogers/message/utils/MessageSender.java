package grogers.message.utils;

import java.net.UnknownHostException;

import grogers.message.data.MessageBean;
import grogers.message.data.MessageBean.MessageStatus;
import grogers.message.data.NamedReference;
import grogers.message.data.UserBean;
import grogers.message.data.dao.MessageDAO;
import grogers.message.data.dao.UserDAO;

/**
 * Standin for a back end process that will send message in a reliable scalable way.
 */
public class MessageSender {

    public void sendMessage(MessageBean message) throws UnknownHostException {
        if (message == null || message.getStatus() != MessageStatus.send)
            return;
    
        MessageDAO dao = new MessageDAO();
        try {
            dao.setSending(message);
        } catch (Exception e) {
            // TODO: proper logging
            e.printStackTrace();
            return;
        }

        try {
            if (message.getReceiver() != null) {
                send(message, message.getReceiver());
            }
            if (message.getGroup() != null) {
                sendToGroup(message);
            }
            dao.setSent(message);
        } catch (Exception e) {
            // TODO: proper logging
            e.printStackTrace();
            try {
                dao.setStatus(message, MessageStatus.sending, MessageStatus.sendingFailed);
            } catch (Exception ex) {
                // TODO: proper logging
                ex.printStackTrace();
            }
        }
    }
    
    /*
     * "Send" a message to a receiver by cloning the message, clearing the id, 
     * and setting the status to "unread".
     */
    void send(MessageBean message, NamedReference receiver) throws UnknownHostException {
        MessageBean receiversMessage = new MessageBean(message);
        receiversMessage.setId(null);
        receiversMessage.setStatus(MessageStatus.unread);
        new MessageDAO().save(receiversMessage);        
    }
    
    /*
     * "Send" a unique copy of message to each user that is a member of the message group.
     * Don't send to sender if also a member.
     */
    void sendToGroup(MessageBean message) throws UnknownHostException {
        Iterable<UserBean> itr = null;
        if (message.getGroup().getName().equals("all"))
            // get all users
            itr = new UserDAO().getGroupMembers(null);
        else
            itr = new UserDAO().getGroupMembers(message.getGroup().getId());

        // Save a unique copy of the message for every user.
        MessageDAO mdao = new MessageDAO();
        MessageBean receiversMessage = new MessageBean(message);
        for (UserBean user : itr) {
            if (!user.getId().equals(message.getSender().getId())) {
                receiversMessage.setId(null);
                receiversMessage.setReceiver(new NamedReference(user));
                receiversMessage.setStatus(MessageStatus.unread);
                mdao.save(receiversMessage);
            }
        }
    }
}
