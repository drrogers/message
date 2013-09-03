package grogers.message.data;


import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.code.morphia.annotations.Embedded;


/**
 * An embedded reference to a collection persisted bean.
 * 
 * Contains id and name so that object reference can be presented
 * to users without the need to load the object itself. The type
 * of object is assumed to be know to the referencer by context.
 * 
 */
@Embedded
public class NamedReference extends BaseBean {
    String name;

    public NamedReference() {
        super();
    }
    public NamedReference(NamedReference other) {
        super(other);
        this.name = other.name;
    }
    public NamedReference(ObjectId id, String name) {
        setId(id);
        setName(name);
    }
    public NamedReference(JSONObject json) throws JSONException {
        setId(new ObjectId(json.getString("id")));
        setName(json.getString("name"));
    }
    
    public NamedReference(UserBean user) {
        this(user.getId(), user.getLoginName());
    }
    public NamedReference(GroupBean group) {
        this(group.getId(), group.getName());
    }
    
    /** hash based on the hash of the id.
     */
    public int hashCode() {
        if (getId() != null) {
            return getId().toString().hashCode();
        }
        return super.hashCode();        
    };    

    /** References to the same object (by Id) are considered equal.
     */
    public boolean equals(Object obj) {
        if(obj instanceof NamedReference) {
            return getId() != null && getId().equals(((NamedReference) obj).getId());
        }
        return false;
    }

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
