package grogers.message.data;


import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Index;
import com.google.code.morphia.annotations.Indexes;
import com.google.common.collect.Sets;

/** Basic user profile.
 */
@Entity(value="users", noClassnameStored=true)
@Indexes( {@Index(value="loginName", name="user_login", unique=true) } )
public class UserBean extends BaseCollectionBean {

	String loginName;

	Set<NamedReference> groupRefs = Sets.newHashSet();
	
    public JSONObject toJson() throws JSONException {
        JSONObject json = super.toJson();
        json.put("loginName", getLoginName());
        
        JSONArray refs = new JSONArray();
        for( NamedReference r : getGroupRefs() ) {
            refs.put(r.toJson());
        }
        json.put("groupRefs", refs);
        return json;
    }
    
    public void fromJson(JSONObject json) throws JSONException {
        super.fromJson(json);
        if (json.has("loginName")) {
            setLoginName(json.getString("loginName"));
        }
        // TODO: incoming group ids need to be validated.
        if (json.has("groupRefs")) {
            JSONArray refs = json.getJSONArray("groupRefs");
            groupRefs.clear();
            for (int i=0; i<refs.length(); i++) {
                NamedReference ref = new NamedReference();
                ref.fromJson(refs.getJSONObject(i));
                groupRefs.add(ref);
            }
        }
    }

    public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public Set<NamedReference> getGroupRefs() {
        return groupRefs;
    }

    public void setGroupRefs(Set<NamedReference> groupRefs) {
        this.groupRefs = groupRefs;
    }

}
