package grogers.message.resource;

import java.util.List;

import grogers.message.data.BaseBean;
import grogers.message.resource.error.InvalidResourceIdError;
import grogers.message.resource.error.ResourceNotFoundError;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;




public abstract class BaseResource {
    @Context HttpServletRequest httpServletRequest;

    public String getResourceName() {
        return "unknown";
    }

    protected String logRequestString(String message) {
        StringBuffer buf = httpServletRequest.getRequestURL();
        String query = httpServletRequest.getQueryString();
        if (query != null)
            buf.append("?").append(query);
        buf.append(", ").append(httpServletRequest.getMethod());
        if (message != null)
            buf.append(", ").append(message);
        return buf.toString();
    }
    protected String logRequestString() {
        return logRequestString(null);
    }
    

    /**
     * Test bean for null.
     * @throws WebApplicationException with ResourceNotFoundError and NOT_FOUND status.
     */
    protected void assertFound(BaseBean bean, String id) throws WebApplicationException {
        if (bean == null) {
            throw new WebApplicationException(
                    new ResourceNotFoundError(getResourceName(), id).toJsonString(),
                    Response.Status.NOT_FOUND.getStatusCode() );
        }
    }

    /**
     * Create a new ObjectId from a string.  
     * @param resourceName - the kind of resource id is for.
     * @throws WebApplicationException with InvalidResourceIdError message and BAD_REQUEST status.
     */
    protected ObjectId newObjectId(String resourceName, String id) throws WebApplicationException {
        try {
            return new ObjectId(id);
        } catch (Exception e) {
            throw new WebApplicationException(
                    new InvalidResourceIdError(resourceName, id).toJsonString(),
                    Response.Status.BAD_REQUEST.getStatusCode() );
        }
    }
    
    /**
     * Create a new ObjectId from a string.
     * When invalid, id is reported as an id for the calling resource class.
     * @throws WebApplicationException with InvalidResourceIdError message and BAD_REQUEST status.
     */
    protected ObjectId newObjectId(String id) throws WebApplicationException {
        return newObjectId(getResourceName(), id);
    }
    
    /**
     * Convert a List of BaseBean into a JSONArray.
     * @throws JSONException 
     */
    protected JSONArray toJson(List<? extends BaseBean> beans) throws JSONException {
        JSONArray ja = new JSONArray();
        for (BaseBean bean : beans) {
            JSONObject j = bean.toJson();
            ja.put(j);
        }
        return ja;
    }
}
