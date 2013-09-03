package grogers.message.data;


import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.code.morphia.annotations.PrePersist;

/** Base class for beans persisted as top level documents in collections.
 */
public class BaseCollectionBean extends BaseBean {

	// Date when object was created; auto-filled by prePersist
	Date createdAt;

	public BaseCollectionBean() {
	    super();
	}
	
    public BaseCollectionBean(MessageBean other) {
        super(other);
        this.createdAt = new Date(other.getCreatedAt().getTime());
    }

    @PrePersist
    protected void prePersist() {
    	if (this.createdAt == null) {
    		this.setCreatedAt(new Date());
    	}
    }

    public JSONObject toJson() throws JSONException {
        JSONObject json = super.toJson();
        if (getCreatedAt() != null)
            json.put("createdAt", getCreatedAt().getTime());
        return json;
    }
    
    public void fromJson(JSONObject json) throws JSONException {
        super.fromJson(json);
        if (json.has("createdAt")) {
            setCreatedAt(new Date(json.getLong("createdAt")));
        }
    }

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
}