package rest;

import grogers.message.data.GroupBean;
import grogers.message.data.MessageBean;
import grogers.message.data.NamedReference;
import grogers.message.data.UserBean;
import grogers.message.data.MessageBean.MessageStatus;

import javax.ws.rs.core.Response.Status;

import org.json.JSONException;


public class MessageTestCase extends BaseTestCase {
    
    public void testSending() throws JSONException {
        UserBean receiver = createUser();
        UserBean groupReceiver = createUser();
        UserBean sender = createUser();
        GroupBean group = createGroup();
        NamedReference groupRef = new NamedReference(group);
        groupReceiver.getGroupRefs().add(groupRef);
        groupReceiver = updateUser(groupReceiver, Status.OK.getStatusCode());
        sender.getGroupRefs().add(groupRef);
        sender = updateUser(sender, Status.OK.getStatusCode());
        
        String content = "你好世界";
        MessageBean message = createMessage(sender, receiver, group, MessageBean.MessageStatus.draft, content, Status.OK.getStatusCode());
        
        MessageBean message2 = getMessageById(message.getId(), Status.OK.getStatusCode());
        assertEquals(message.getReceiver(), message2.getReceiver());
        assertEquals(message.getSender(), message2.getSender());
        assertEquals(message.getGroup(), message2.getGroup());
        assertEquals(message.getContent(), message2.getContent());
        assertEquals(content, message2.getContent());
        assertEquals(message.getStatus(), message2.getStatus());
        
        message = message2;
        message.setStatus(MessageStatus.send);
        message2 = updateMessage(message, Status.OK.getStatusCode());
        assertEquals(MessageStatus.sent, message2.getStatus());
        
    }
}
