package grogers.message.resource.error;

import org.json.JSONException;
import org.json.JSONObject;

public class InvalidValueError extends BaseError {

    String name;
    String value;
    
    public InvalidValueError(String name, String value) {
        super();
        this.name = name;
        this.value = value;
    }

    public JSONObject toJson() {
        JSONObject json = null;
        try {
            json = new JSONObject();
            json.put("name", name);
            json.put("value", value);
        } catch (JSONException e) {
            // TODO proper Logging....
            e.printStackTrace();
        }
        return json;
    }

    public String getPropertyName() {
        return name;
    }

    public void setPropertyName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
