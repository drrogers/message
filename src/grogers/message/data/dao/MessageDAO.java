package grogers.message.data.dao;

import grogers.message.data.MessageBean;
import grogers.message.data.MessageBean.MessageStatus;
import grogers.message.utils.MongoUtils;

import java.net.UnknownHostException;

import org.bson.types.ObjectId;

import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.mongodb.QueryOperators;


public class MessageDAO extends BaseDAO<MessageBean,ObjectId> {
    
    public MessageDAO() throws UnknownHostException {
        super(MessageBean.class, MongoUtils.getInstance().getDatastore());
    }

    public void setStatus(MessageBean message, MessageStatus statusPrereq, MessageStatus newStatus) throws Exception {
        Query<MessageBean> query = ds.createQuery(MessageBean.class)
                .field("id").equal(message.getId())
                .field("status").equal(statusPrereq.toString());
        UpdateOperations<MessageBean> ops = ds.createUpdateOperations(MessageBean.class).set("status", newStatus.toString());
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
