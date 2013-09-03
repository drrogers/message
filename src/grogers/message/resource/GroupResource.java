package grogers.message.resource;

import grogers.message.data.GroupBean;
import grogers.message.data.dao.GroupDAO;

import java.net.UnknownHostException;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bson.types.ObjectId;
import org.json.JSONException;

import com.mongodb.MongoException;


@Path("/group")
public class GroupResource extends BaseResource {
    public String getResourceName() {
        return "group";
    }
    
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public String getGroup(@PathParam("id") String id) throws UnknownHostException, JSONException {
        ObjectId objectId = newObjectId(id);
        GroupBean group = new GroupDAO().get(objectId);
        assertFound(group, id);
        return group.toJsonString();
    }
    
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    public String registoreGroup(String jsonData) throws UnknownHostException, JSONException {
        try {
            GroupBean group = new GroupBean();
            group.fromJson(jsonData);
            new GroupDAO().save(group);
            return group.toJsonString();
        } catch(MongoException.DuplicateKey e) {
            throw new WebApplicationException(Response.Status.CONFLICT);
        }
    }
}



