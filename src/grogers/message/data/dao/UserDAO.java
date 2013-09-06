package grogers.message.data.dao;

import grogers.message.data.UserBean;
import grogers.message.utils.MongoUtils;

import java.net.UnknownHostException;

import org.bson.types.ObjectId;

import com.google.code.morphia.query.Query;



public class UserDAO extends BaseDAO<UserBean, ObjectId> {

    public UserDAO() throws UnknownHostException {
        super(UserBean.class, MongoUtils.getInstance().getDatastore());
    }

    /**
     * Find user by name
     */
    public UserBean getByName( String name ) {
        Query<UserBean> query = ds.createQuery(UserBean.class);
        query = query.field("loginName").equal(name);
        UserBean user = query.get();
        return user;
    }
    
    /**
     * Return an Iterable over Users that are members of the optionally specified group.
     * @param groupId
     * @return
     */
    public Iterable<UserBean> getGroupMembers( ObjectId groupId ) {
        Query<UserBean> query = ds.createQuery(UserBean.class);
        
        if (groupId != null)
            query = query.field("groupRefs._id").equal(groupId);
        
        Iterable<UserBean> itr = query.fetch();
        return itr;
    }

}
