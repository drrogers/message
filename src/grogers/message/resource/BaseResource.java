package grogers.message.resource;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import grogers.message.data.BaseBean;
import grogers.message.resource.error.ResourceNotFoundError;

import org.bson.types.ObjectId;



public abstract class BaseResource {
    public String getResourceName() {
        return "unknown";
    }

    protected void assertFound(BaseBean bean, String id) throws WebApplicationException {
        if (bean == null) {
            throw new WebApplicationException(
                    new ResourceNotFoundError(getResourceName(), id).toJsonString(),
                    Response.Status.NOT_FOUND.getStatusCode() );
        }
    }

    protected ObjectId newObjectId(String id) throws WebApplicationException {
        try {
            return new ObjectId(id);
        } catch (Exception e) {
            throw new WebApplicationException(
                    new ResourceNotFoundError(getResourceName(), id).toJsonString(),
                    Response.Status.BAD_REQUEST.getStatusCode() );
        }
    }
    

}
