package grogers.message.resource;

import grogers.message.data.MessageBean;
import grogers.message.data.UserBean;
import grogers.message.data.MessageBean.MessageStatus;
import grogers.message.data.dao.MessageDAO;
import grogers.message.data.dao.UserDAO;
import grogers.message.resource.error.InvalidValueError;
import grogers.message.resource.error.ResourceNotFoundError;
import grogers.message.utils.MessageSender;

import java.net.UnknownHostException;

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
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.MongoException;


@Path("/message")
public class MessageResource extends BaseResource {
    public String getResourceName() {
        return "message";
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
                    new InvalidValueError(getResourceName(), json.getString("id"), "status", json.getString("status")).toJsonString(),
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
                            new InvalidValueError(getResourceName(), id, "status", json.getString("status")).toJsonString(),
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
