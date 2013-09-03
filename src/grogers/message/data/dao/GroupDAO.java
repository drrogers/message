package grogers.message.data.dao;

import grogers.message.data.GroupBean;
import grogers.message.utils.MongoUtils;

import java.net.UnknownHostException;

import org.bson.types.ObjectId;


public class GroupDAO extends BaseDAO<GroupBean, ObjectId> {

    public GroupDAO() throws UnknownHostException {
        super(GroupBean.class, MongoUtils.getInstance().getDatastore());
    }

}
