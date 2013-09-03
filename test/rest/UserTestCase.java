package rest;

import grogers.message.data.GroupBean;
import grogers.message.data.NamedReference;
import grogers.message.data.UserBean;

import javax.ws.rs.core.Response.Status;

import org.bson.types.ObjectId;
import org.json.JSONException;


public class UserTestCase  extends BaseTestCase {

    public void testRegistrationHappyPath() throws JSONException {
        createUser(); 
    }

    public void testRegistrationDuplicate() throws JSONException {
        UserBean user = createUser();
        createUser(user.getLoginName(), Status.CONFLICT.getStatusCode());
    }

    public void testGetHappyPath() throws JSONException {
        UserBean user = createUser();
        UserBean user2 = getUserById(user.getId(), Status.OK.getStatusCode());
        assertEquals(user.getId(), user2.getId());
        assertEquals(user.getLoginName(), user2.getLoginName());
    }
    public void testGetNotFound() throws JSONException {
        getUserById(new ObjectId(), Status.NOT_FOUND.getStatusCode());
        
    }
    
    public void testJoinGroup() throws JSONException {
        GroupBean group = createGroup();
        UserBean user = createUser();
        user.getGroupRefs().add(new NamedReference(group.getId(), group.getName()));
        UserBean user2 = updateUser(user, Status.OK.getStatusCode());
        assertEquals(user.getId(), user2.getId());
        assertEquals(user.getGroupRefs().size(), user2.getGroupRefs().size());
        for (NamedReference ref : user.getGroupRefs()) {
            assertTrue("Updated user missing reference: " + ref.toJsonString(), user2.getGroupRefs().contains(ref));
        }
    }

    public void testLeaveGroup() throws JSONException {
        GroupBean group = createGroup();
        UserBean user = createUser();
        NamedReference groupRef = new NamedReference(group.getId(), group.getName());
        // join
        user.getGroupRefs().add(groupRef);
        UserBean user2 = updateUser(user, Status.OK.getStatusCode());
        assertEquals(user.getId(), user2.getId());
        assertEquals(user.getGroupRefs().size(), user2.getGroupRefs().size());
        assertEquals(1, user2.getGroupRefs().size());
        
        //leave
        user = user2;
        user.getGroupRefs().remove(groupRef);
        user2 = updateUser(user, Status.OK.getStatusCode());
        assertEquals(user.getId(), user2.getId());
        assertEquals(user.getGroupRefs().size(), user2.getGroupRefs().size());
        assertEquals(0, user2.getGroupRefs().size());
    }

}
