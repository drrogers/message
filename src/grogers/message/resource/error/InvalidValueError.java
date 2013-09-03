package grogers.message.resource.error;

import org.json.JSONException;
import org.json.JSONObject;

public class InvalidValueError extends ResourceError {

    String propertyName;
    String value;
    
    public InvalidValueError(String name, String id, String propertyName, String value) {
        super(name, id);
        this.propertyName = propertyName;
        this.value = value;
    }

    public JSONObject toJson() {
        JSONObject json = super.toJson();
        try {
            json.put("propertyName", propertyName);
            json.put("value", value);
        } catch (JSONException e) {
            // TODO proper Logging....
            e.printStackTrace();
        }
        return null;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
