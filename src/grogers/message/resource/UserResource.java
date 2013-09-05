package grogers.message.resource;

import grogers.message.data.UserBean;
import grogers.message.data.dao.UserDAO;

import java.net.UnknownHostException;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.MongoException;


@Path("/user")
public class UserResource extends BaseResource {
    static Logger log = Logger.getLogger(UserResource.class);

    public String getResourceName() {
        return "user";
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public String getUser(@PathParam("id") String id) throws UnknownHostException, JSONException {
        log.info(logRequestString());

        ObjectId objectId = newObjectId(id);
        UserBean user = new UserDAO().get(objectId);
        assertFound(user, id);
        return user.toJsonString();
    }
    
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    public String createUser(String jsonData) throws UnknownHostException, JSONException {
        log.info(logRequestString("json:" + jsonData));

        try {
            UserBean user = new UserBean();
            user.fromJson(jsonData);
            new UserDAO().save(user);
            return user.toJsonString();
        } catch(MongoException.DuplicateKey e) {
            log.info("PUT " + getResourceName() + " createUser failed: DuplicateKey. input: " + jsonData);
            // loginName in use already.
            throw new WebApplicationException(Response.Status.CONFLICT);
        }
    }

    @PUT
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public String updateUser(String jsonData, @PathParam("id") String id) throws UnknownHostException, JSONException {
        log.info(logRequestString("json:" + jsonData));
      
        try {
            ObjectId objectId = newObjectId(id);
            UserDAO dao = new UserDAO();
            UserBean user = dao.get(objectId);
            assertFound(user, id);

            // put not allowed to change id
            JSONObject json = new JSONObject(jsonData);
            if (json.has("id"))
                json.remove("id");
            user.fromJson(json);
            dao.save(user);
            return user.toJsonString();
        } catch(MongoException.DuplicateKey e) {
            log.info("PUT " + getResourceName() + " updateUser failed: DuplicateKey. input: " + jsonData);
            // loginName change failed
            throw new WebApplicationException(Response.Status.CONFLICT);
        }
    }
}



