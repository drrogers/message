package grogers.message.resource;

import grogers.message.data.MessageBean;
import grogers.message.data.MessageBean.MessageStatus;
import grogers.message.data.NamedReference;
import grogers.message.data.UserBean;
import grogers.message.data.dao.MessageDAO;
import grogers.message.data.dao.UserDAO;
import grogers.message.resource.error.InvalidResourcePropertyValueError;
import grogers.message.resource.error.InvalidValueError;
import grogers.message.utils.MessageSender;

import java.net.UnknownHostException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.MongoException;


@Path("/message")
public class MessageResource extends BaseResource {
    public String getResourceName() {
        return "message";
    }

    /**
     * Convert string representation of MessageStatus to enum.
     * @param status - null allowed, returns null.
     * @throws WebApplicationException with InvalidValueError and BAD_REQUEST status on failure.
     * @return
     */
    protected MessageStatus validMessageStatus(String status) {
        MessageStatus msgStatus = null;
        try {
            if (status != null)
                msgStatus = MessageStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new WebApplicationException(
                    new InvalidValueError("status", status).toJsonString(),
                    Response.Status.BAD_REQUEST.getStatusCode() );
        }
        return msgStatus;
    }
        
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public String getMessage(@PathParam("id") String id) throws UnknownHostException, JSONException {
        ObjectId objectId = newObjectId(id);
        MessageBean message = new MessageDAO().get(objectId);
        assertFound(message, id);
        return message.toJsonString();
    }

    /**
     * Search for messages with various filters.  E.g. the inbox
     * @param userId - receiver of messages
     * @return JSON that includes input filter settings and output list of matching messages.
     * @throws UnknownHostException
     * @throws JSONException
     */
    @GET
    @Path("search")
    @Produces({MediaType.APPLICATION_JSON})
    public String search(@QueryParam("userId") String userId, 
                        @QueryParam("status") String status) throws UnknownHostException, JSONException {
        // TODO: add offset, limit, sender, etc.
        // TODO: after login required, userId should be matched against user in session and only allowed
        // TODO: access when they match or session user has sufficient privileges.
        ObjectId id = newObjectId("user", userId);
        UserBean user = new UserDAO().get(id);
        assertFound(user, userId);
        
        MessageStatus msgStatus = validMessageStatus(status);
        
        List<MessageBean> messages = new MessageDAO().search(user, msgStatus);
        JSONObject json = new JSONObject();
        JSONArray jsonMessages = new JSONArray();
        for (MessageBean message : messages) {
            jsonMessages.put(message.toJson());
        }
        json.put("messages", jsonMessages);
        json.put("receiver", new NamedReference(user).toJson());
        if (msgStatus != null)
            json.put("status", msgStatus.toString());
        return json.toString();
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON})
    public String createMessage(String jsonData) throws UnknownHostException, JSONException {
        JSONObject json = null;
        try {
            MessageBean message = new MessageBean();
            json = new JSONObject(jsonData);
            message.fromJson(json);
            new MessageDAO().save(message);
            if (message.getStatus() == MessageStatus.send) {
                new MessageSender().sendMessage(message);
                // Queue up message for sending
            }
            return message.toJsonString();
        } catch (IllegalArgumentException e) {
            throw new WebApplicationException(
                    new InvalidResourcePropertyValueError(getResourceName(), json.getString("id"), "status", json.getString("status")).toJsonString(),
                    Response.Status.BAD_REQUEST.getStatusCode() );
        } catch(MongoException.DuplicateKey e) {
            // loginName in use already.
            throw new WebApplicationException(Response.Status.CONFLICT);
        }
    }

    /**
     * Update a message.  When status is not draft, only status changes are allowed.
     */
    @PUT
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public String updateMessage(String jsonData, @PathParam("id") String id) throws UnknownHostException, JSONException {
        try {
            ObjectId objectId = newObjectId(id);
            MessageDAO dao = new MessageDAO();
            MessageBean message = dao.get(objectId);
            assertFound(message, id);

            // put not allowed to change id
            JSONObject json = new JSONObject(jsonData);
            if (json.has("id"))
                json.remove("id");
            if (message.getStatus() == MessageBean.MessageStatus.draft) {
                message.fromJson(json);
            }
            else {
                // ignore properties other than status
                // TODO: consider throwing this back as an bad request if other properties are changed
                try {
                    message.setStatus(json.getString("status"));
                } catch (IllegalArgumentException e) {
                    throw new WebApplicationException(
                            new InvalidResourcePropertyValueError(getResourceName(), id, "status", json.getString("status")).toJsonString(),
                            Response.Status.BAD_REQUEST.getStatusCode() );
                }
            }
            dao.save(message);
            if (message.getStatus() == MessageStatus.send) {
                // Queue up message for sending
                new MessageSender().sendMessage(message);
            }
            return message.toJsonString();
        } catch(MongoException.DuplicateKey e) {
            // loginName change failed
            throw new WebApplicationException(Response.Status.CONFLICT);
        }
    }

}
