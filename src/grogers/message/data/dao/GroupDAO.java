package grogers.message.data.dao;

import grogers.message.data.GroupBean;
import grogers.message.utils.MongoUtils;

import java.net.UnknownHostException;

import org.bson.types.ObjectId;

import com.google.code.morphia.query.Query;


public class GroupDAO extends BaseDAO<GroupBean, ObjectId> {

    public GroupDAO() throws UnknownHostException {
        super(GroupBean.class, MongoUtils.getInstance().getDatastore());
    }

    /**
     * Find group by name
     */
    public GroupBean getByName( String name ) {
        Query<GroupBean> query = ds.createQuery(GroupBean.class);
        query = query.field("name").equal(name);
        GroupBean user = query.get();
        return user;
    }
}
