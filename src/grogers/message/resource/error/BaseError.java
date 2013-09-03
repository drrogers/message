package grogers.message.resource.error;

import org.json.JSONException;
import org.json.JSONObject;

public class BaseError {

    public BaseError() {
        super();
    }

    /**
     * Convert error to JSON.
     * Subclasses should always override this.
     * @return JSONObject representation of error.
     * @throws JSONException
     */
    public JSONObject toJson() {
        JSONObject json = null;
        try {
            json = new JSONObject().put("message", "unknown server error");
        } catch (JSONException e) {
            // TODO don't want error reporter creating more errors. use proper Logging....
            e.printStackTrace();
        }
        return json;
    }

    /**
     * Convert error to JSON in String form.
     */
    public String toJsonString() {
        JSONObject json = toJson();
        String jsonString;
        if (json == null)
            jsonString = "{\"message\":\"unknown server error\"}";
        else
            jsonString = toJson().toString();
        return jsonString;
    }

}