package rest;

import grogers.message.data.GroupBean;

import javax.ws.rs.core.Response.Status;

import org.bson.types.ObjectId;
import org.json.JSONException;



public class GroupTestCase extends BaseTestCase {
    
    public void testRegistrationHappyPath() throws JSONException {
        createGroup(); 
    }

    public void testRegistrationDuplicate() throws JSONException {
        GroupBean group = createGroup();
        createGroup(group.getName(), Status.CONFLICT.getStatusCode());
    }

    public void testGetHappyPath() throws JSONException {
        GroupBean group = createGroup();
        GroupBean group2 = getGroupById(group.getId(), Status.OK.getStatusCode());
        assertEquals(group.getId(), group2.getId());
        assertEquals(group.getName(), group2.getName());
    }
    public void testGetNotFound() throws JSONException {
        getGroupById(new ObjectId(), Status.NOT_FOUND.getStatusCode());
        
    }
}
