package grogers.message.data;

import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.code.morphia.annotations.Id;

public class BaseBean {
    @Id
    ObjectId id;
    
    public BaseBean() {
    }

    public BaseBean(BaseBean other) {
        this.id = new ObjectId(other.getId().toString());
    }

    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        if (getId() != null)
            json.put("id", getId().toString());
        return json;
    }
    
    public String toJsonString() throws JSONException {
        return toJson().toString();
    }
    
    public void fromJson(JSONObject json) throws JSONException {
        if (json.has("id")) {
            setId(new ObjectId(json.getString("id")));
        }
    }
    
    public void fromJson(String jsonStr) throws JSONException {
        fromJson( new JSONObject(jsonStr) );
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }
    
}
