package grogers.message.data.dao;

import grogers.message.data.MessageBean;
import grogers.message.data.MessageBean.MessageStatus;
import grogers.message.data.UserBean;
import grogers.message.utils.MongoUtils;

import java.net.UnknownHostException;
import java.util.List;

import org.bson.types.ObjectId;

import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;


public class MessageDAO extends BaseDAO<MessageBean,ObjectId> {
    
    public MessageDAO() throws UnknownHostException {
        super(MessageBean.class, MongoUtils.getInstance().getDatastore());
    }

    /**
     * Returns list of MessageBeans that match the input criteria.
     * @param receiver
     * @return
     */
    public List<MessageBean> search(UserBean receiver, MessageStatus status) {
        Query<MessageBean> query = ds.createQuery(MessageBean.class)
                .field("receiver._id").equal(receiver.getId())
                .field("status").equal(status);
        
        List<MessageBean> messages = query.asList();
        return messages;
    }
    
    public void setStatus(MessageBean message, MessageStatus statusPrereq, MessageStatus newStatus) throws Exception {
        Query<MessageBean> query = ds.createQuery(MessageBean.class)
                .field("id").equal(message.getId())
                .field("status").equal(statusPrereq);
        UpdateOperations<MessageBean> ops = ds.createUpdateOperations(MessageBean.class).set("status", newStatus);
        UpdateResults<MessageBean> results = ds.updateFirst(query, ops);
        if (results.getHadError()) {
            // TODO: define a more specific exception class
            throw new Exception(results.getError());
        }
        if (results.getUpdatedCount() != 1) {
            throw new Exception("unable to set status from: " + statusPrereq.toString() + " to: " + newStatus.toString());
        }
        message.setStatus(newStatus);
    }

    public void setSending(MessageBean message) throws Exception {
        setStatus(message, MessageStatus.send, MessageStatus.sending);
    }
    public void setSent(MessageBean message) throws Exception {
        setStatus(message, MessageStatus.sending, MessageStatus.sent);
    }

}
