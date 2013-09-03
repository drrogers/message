package grogers.message.data;


import org.json.JSONException;
import org.json.JSONObject;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Index;
import com.google.code.morphia.annotations.Indexes;

/**
 * Group - a collection of Users.
 * Membership is recorded via User references back to the Group.
 * 
 */
@Entity(value="group", noClassnameStored=true)
@Indexes( {@Index(value="name", name="group_name", unique=true) } )
public class GroupBean extends BaseCollectionBean {
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public JSONObject toJson() throws JSONException {
        JSONObject json = super.toJson();
        json.put("name", getName());
        return json;
    }
    public void fromJson(JSONObject json) throws JSONException {
        super.fromJson(json);
        if (json.has("name")) {
            setName(json.getString("name"));
        }
    }

}
