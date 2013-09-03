package rest;

import java.util.List;

import grogers.message.data.BaseBean;
import grogers.message.data.GroupBean;
import grogers.message.data.MessageBean;
import grogers.message.data.MessageBean.MessageStatus;
import grogers.message.data.NamedReference;
import grogers.message.data.UserBean;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.bson.types.ObjectId;
import org.glassfish.jersey.client.ClientConfig;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;

import com.google.common.collect.Lists;

import junit.framework.TestCase;

public abstract class BaseTestCase extends TestCase {
    ClientConfig config;
    Client client;
    WebTarget webTarget;

    @Before
    public void setUp() {
        config = new ClientConfig();
        
        client = ClientBuilder.newClient(config);
        webTarget = client.target("http://localhost:8080/grogers.message/rest");
    }
    
    protected BaseBean updateBean(Class cls, String resourceName, BaseBean bean, int expectedStatus) throws JSONException {
        String json = bean.toJson().toString();
        
        Response response = webTarget.path(resourceName).path(bean.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(json, MediaType.APPLICATION_JSON));

        assertEquals(expectedStatus, response.getStatus());
        BaseBean resultBean = null;
        if (Status.OK.getStatusCode() == expectedStatus) {
            String content = response.readEntity(String.class);
            JSONObject jsonResponse = new JSONObject(content);
            assertTrue(jsonResponse.has("id"));
 
            try {
                resultBean = (BaseBean)cls.newInstance();
            } catch (InstantiationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                fail();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                fail();
            }
            resultBean.fromJson(jsonResponse);
            assertEquals(bean.getId(), resultBean.getId());
        }
        return resultBean;
    }
    
    
    protected BaseBean getBeanById(Class cls, String resourceName, ObjectId id, int expectedStatus) throws JSONException {
        Response response = webTarget.path(resourceName).path(id.toString())
                .request(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(expectedStatus, response.getStatus());
        BaseBean bean = null;
        try {
            bean = (BaseBean)cls.newInstance();
            if (Status.OK.getStatusCode() == expectedStatus) {
                String content = response.readEntity(String.class);
                JSONObject jsonResponse = new JSONObject(content);
                bean.fromJson(jsonResponse);
            }
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail();
        }
        return bean;
    }

    
    protected GroupBean createGroup(String name, int expectedStatus) throws JSONException {
        GroupBean group = new GroupBean(); 
        if (name == null) {
            name = "unittest_" + new ObjectId().toString();
        }
        group.setName(name);
        String json = group.toJson().toString();
        
        Response response = webTarget.path("group").request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(json, MediaType.APPLICATION_JSON));
    
        assertEquals(expectedStatus, response.getStatus());
        if (Status.OK.getStatusCode() == expectedStatus) {
            String content = response.readEntity(String.class);
            JSONObject jsonResponse = new JSONObject(content);
            assertTrue(jsonResponse.has("id"));
            assertEquals(group.getName(), jsonResponse.getString("name"));
    
            group.fromJson(jsonResponse);
        }
        else {
            group = null;
        }
        return group;
    }

    protected GroupBean createGroup() throws JSONException {
        return createGroup(null, Status.OK.getStatusCode());
    }

    protected GroupBean getGroupById(ObjectId id, int expectedStatus) throws JSONException {
        return (GroupBean) getBeanById(GroupBean.class, "group", id, expectedStatus);
    }

    
    protected UserBean createUser(String loginName, int expectedStatus) throws JSONException {
        UserBean user = new UserBean(); 
        if (loginName == null) {
            loginName = "unittest_" + new ObjectId().toString();
        }
        user.setLoginName(loginName);
        String json = user.toJson().toString();
        
        Response response = webTarget.path("user").request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(json, MediaType.APPLICATION_JSON));

        assertEquals(expectedStatus, response.getStatus());
        if (Status.OK.getStatusCode() == expectedStatus) {
            String content = response.readEntity(String.class);
            JSONObject jsonResponse = new JSONObject(content);
            assertTrue(jsonResponse.has("id"));
            assertEquals(user.getLoginName(), jsonResponse.getString("loginName"));
    
            user.fromJson(jsonResponse);
        }
        else {
            user = null;
        }
        return user;
    }
    public UserBean createUser() throws JSONException {
        return createUser(null, Status.OK.getStatusCode());
    }
    

    protected UserBean updateUser(UserBean user, int expectedStatus) throws JSONException {
        return (UserBean)updateBean(UserBean.class, "user", user, expectedStatus);
    }
    public UserBean getUserById(ObjectId id, int expectedStatus) throws JSONException {
        return (UserBean) getBeanById(UserBean.class, "user", id, expectedStatus);
    }

    
    protected MessageBean createMessage(UserBean sender, UserBean receiver, GroupBean group, MessageBean.MessageStatus status, String content, int expectedStatus) throws JSONException {

        MessageBean message = new MessageBean();
        if (sender != null)
            message.setSender(new NamedReference(sender));
        if (receiver != null)
            message.setReceiver(new NamedReference(receiver));
        if (group != null)
            message.setGroup(new NamedReference(group));
        message.setStatus(status);
        message.setContent(content);
        
        String json = message.toJsonString();
        
        Response response = webTarget.path("message").request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(json, MediaType.APPLICATION_JSON));

        assertEquals(expectedStatus, response.getStatus());
        if (Status.OK.getStatusCode() == expectedStatus) {
            String responseContent = response.readEntity(String.class);
            JSONObject jsonResponse = new JSONObject(responseContent);
            assertTrue(jsonResponse.has("id"));
            assertEquals(message.getContent(), jsonResponse.getString("content"));
    
            message.fromJson(jsonResponse);
        }
        else {
            message = null;
        }
        return message;
    }


    protected MessageBean updateMessage(MessageBean message, int expectedStatus) throws JSONException {
        return (MessageBean)updateBean(MessageBean.class, "message", message, expectedStatus);
    }
    public MessageBean getMessageById(ObjectId id, int expectedStatus) throws JSONException {
        return (MessageBean) getBeanById(MessageBean.class, "message", id, expectedStatus);
    }
    
    public List<MessageBean> searchMessages(UserBean receiver, MessageStatus status, int expectedMessageCount) throws JSONException {
        WebTarget wt = webTarget.path("message/search").queryParam("userId", receiver.getId().toString());
        if (status != null) {
            wt = wt.queryParam("status", status.toString());
        }
        
        Response response = wt.request(MediaType.APPLICATION_JSON).get(Response.class);
        assertEquals(Status.OK.getStatusCode(), response.getStatus());

        String content = response.readEntity(String.class);
        JSONObject json = new JSONObject(content);
        assertTrue(json.has("receiver"));
        assertEquals(json.getJSONObject("receiver").get("id"), receiver.getId().toString());

        if (status != null) {
            assertTrue(json.has("status"));
            assertEquals(status.toString(), json.getString("status"));
        }
        assertTrue(json.has("messages"));
        assertEquals(expectedMessageCount, json.getJSONArray("messages").length());
        
        JSONArray jsonMessages = json.getJSONArray("messages"); 
        List<MessageBean> messages = Lists.newArrayList();
        for ( int i=0; i<jsonMessages.length(); i++) {
            JSONObject jsonMessage = jsonMessages.getJSONObject(i);
            messages.add(new MessageBean(jsonMessage));
        }
        return messages;
    }
    
}