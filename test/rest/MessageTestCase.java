package rest;

import java.util.List;

import grogers.message.data.GroupBean;
import grogers.message.data.MessageBean;
import grogers.message.data.NamedReference;
import grogers.message.data.UserBean;
import grogers.message.data.MessageBean.MessageStatus;

import javax.ws.rs.core.Response.Status;

import org.json.JSONException;


public class MessageTestCase extends BaseTestCase {
    
    // TODO: write more isolated test cases for message sending, searching, and status updating
    
    // Not so much a test case as a big test scenario....
    public void testSending() throws JSONException {
        // Create users and group
        UserBean receiver = createUser();
        UserBean groupReceiver = createUser();
        UserBean sender = createUser();
        GroupBean group = createGroup();
        NamedReference groupRef = new NamedReference(group);
        groupReceiver.getGroupRefs().add(groupRef);
        groupReceiver = updateUser(groupReceiver, Status.OK.getStatusCode());
        sender.getGroupRefs().add(groupRef);
        sender = updateUser(sender, Status.OK.getStatusCode());
        
        // Save a draft message
        String content = "你好世界";
        MessageBean message = createMessage(sender, receiver, group, MessageBean.MessageStatus.draft, content, Status.OK.getStatusCode());
        
        // Verify message exists and is correct
        MessageBean message2 = getMessageById(message.getId(), Status.OK.getStatusCode());
        assertEquals(message.getReceiver(), message2.getReceiver());
        assertEquals(message.getSender(), message2.getSender());
        assertEquals(message.getGroup(), message2.getGroup());
        assertEquals(message.getContent(), message2.getContent());
        assertEquals(content, message2.getContent());
        assertEquals(message.getStatus(), message2.getStatus());
        
        // Change status to send causing the message to get sent to receiver and group members.
        message = message2;
        message.setStatus(MessageStatus.send);
        message2 = updateMessage(message, Status.OK.getStatusCode());
        assertEquals(MessageStatus.sent, message2.getStatus());

        // Search for messages for the receivers to validate delivery.
        List<MessageBean> receiverMessages = searchMessages(null, receiver, MessageStatus.unread, 0, 20, 1);
        List<MessageBean> groupReceiverMessages = searchMessages(null, groupReceiver, MessageStatus.unread, 0, 20, 1);
        List<MessageBean> senderMessages = searchMessages(null, sender, MessageStatus.unread, 0, 20, 0);

        // check receivers message lifecycle
        searchMessages(null, receiver, MessageStatus.read, 0, 20, 0);
        searchMessages(null, receiver, MessageStatus.deleted, 0, 20, 0);
        
        MessageBean msg = receiverMessages.get(0);
        msg.setStatus(MessageStatus.read);
        updateMessage(msg, Status.OK.getStatusCode());
        searchMessages(null, receiver, MessageStatus.unread, 0, 20, 0);
        searchMessages(null, receiver, MessageStatus.read, 0, 20, 1);
        searchMessages(null, receiver, MessageStatus.deleted, 0, 20, 0);
        
        msg.setStatus(MessageStatus.deleted);
        updateMessage(msg, Status.OK.getStatusCode());
        searchMessages(null, receiver, MessageStatus.unread, 0, 20, 0);
        searchMessages(null, receiver, MessageStatus.read, 0, 20, 0);
        searchMessages(null, receiver, MessageStatus.deleted, 0, 20, 1);
        
        // receiver didn't impact groupReceiver?
        searchMessages(null, groupReceiver, MessageStatus.unread, 0, 20, 1);
    }
    
    // Not so much a test case as a big test scenario....
    public void testPaging() throws JSONException {
        // Create users and group
        UserBean u1 = createUser();
        UserBean u2 = createUser();
        
        createMessage(u1, u2, null, MessageBean.MessageStatus.send, "wassup?", Status.OK.getStatusCode());
        createMessage(u1, u2, null, MessageBean.MessageStatus.send, "I know you're out there. I can hear you breathing.", Status.OK.getStatusCode());
        createMessage(u1, u2, null, MessageBean.MessageStatus.send, "Why don't you answer me?", Status.OK.getStatusCode());
        createMessage(u1, u2, null, MessageBean.MessageStatus.send, "I really can't keep this up...", Status.OK.getStatusCode());
        createMessage(u1, u2, null, MessageBean.MessageStatus.send, "Screw you guy's, I'm going home.", Status.OK.getStatusCode());

        searchMessages(null, u2, MessageStatus.unread, 0, 20, 5);
        searchMessages(null, u2, MessageStatus.unread, 0, 2, 2);
        searchMessages(null, u2, MessageStatus.unread, 2, 10, 3);
        searchMessages(null, u2, MessageStatus.unread, 5, 10, 0);
        
    }
}
