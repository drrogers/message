package grogers.message.data;


import org.json.JSONException;
import org.json.JSONObject;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Index;
import com.google.code.morphia.annotations.Indexes;


@Entity(value="message", noClassnameStored=true)
@Indexes( {@Index(value="receiver.id,status,-createdAt", name="message_receiverid_status_createdat") } )
public class MessageBean extends BaseCollectionBean {

    public enum MessageStatus {
        draft,  // app layer allows changes to all properties while in draft 
        send,   // signals backend to copy message to recipient or group members
        sending, // backing in process of sending message to receiver or group members
        sendingFailed, // failed to complete sending - needs attention!
        sent,   // sender's original message after send is completed
        unread, // receiver's copy of message, unread
        read,   // receiver's copy after reading
        deleted,// ui would presumably not often ask for or show these 
    };

    NamedReference receiver; // User
    String content;
    MessageStatus status = MessageStatus.draft;
    NamedReference sender;  // User
    NamedReference group;   // if not null, set to Group members

    public MessageBean() {
        super();
    }

    public MessageBean(MessageBean other) {
        super(other);
        if (other.getReceiver() != null)
            this.receiver = new NamedReference(other.getReceiver());
        if (other.getSender() != null)
            this.sender = new NamedReference(other.getSender());
        if (other.getGroup() != null)
            this.sender = new NamedReference(other.getGroup());
        this.content = other.content;
        this.status = other.status;
    }
    
    public MessageBean(JSONObject json) throws JSONException {
        this();
        fromJson(json);
    }

    public JSONObject toJson() throws JSONException {
        JSONObject json = super.toJson();
        json.put("status", getStatus().toString());
        json.put("content", getContent());
        json.put("sender", getSender().toJson());
        
        if (getReceiver() != null)
            json.put("receiver", getReceiver().toJson());
        if (getGroup() != null)
            json.put("group", getGroup().toJson());
        
        return json;
    }
    
    public void fromJson(JSONObject json) throws JSONException {
        super.fromJson(json);
        if (json.has("status"))
            setStatus(json.getString("status"));
        if (json.has("content"))
            setContent(json.getString("content"));
        if (json.has("sender"))
            setSender( new NamedReference(json.getJSONObject("sender")));
        if (json.has("receiver"))
            setReceiver( new NamedReference(json.getJSONObject("receiver")));
        if (json.has("group"))
            setGroup( new NamedReference(json.getJSONObject("group")));
    }

    
    
    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }
    public void setStatus(String status) {
        setStatus( MessageStatus.valueOf(status));
    }

    public NamedReference getReceiver() {
        return receiver;
    }
    public void setReceiver(NamedReference receiver) {
        this.receiver = receiver;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public NamedReference getSender() {
        return sender;
    }
    public void setSender(NamedReference sender) {
        this.sender = sender;
    }
    public NamedReference getGroup() {
        return group;
    }
    public void setGroup(NamedReference group) {
        this.group = group;
    }

    
    
}
