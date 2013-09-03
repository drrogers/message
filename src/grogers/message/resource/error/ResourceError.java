package grogers.message.resource.error;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class ResourceError {
    String name;
    String id;

    public ResourceError(String name, String id) {
        if (name == null || name.trim().length() == 0)
            name = "unknown";
        this.name = name;

        if (id == null || id.trim().length() == 0)
            id = "unknown";
        this.id = id;
    }
    
    public JSONObject toJson() {
        JSONObject json = null;
        try {
            json = new JSONObject();
            json.put("id", id);
            json.put("name", name);
            json.put("message", this.getClass().getName());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            // TODO proper Logging....
            e.printStackTrace();
        }
        return null;
    }
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
