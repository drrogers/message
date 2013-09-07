
import json
from exampleclientbase import ExampleClientBase

class MessageApi(ExampleClientBase):

    def __init__(self, parsed_arguments):
        super(MessageApi, self).__init__(parsed_arguments)
        if self.baseUrl is None:
            self.baseUrl = "http://ec2-54-200-9-5.us-west-2.compute.amazonaws.com:8080/message/rest/"
        return
    
    def namedReferenceJson(self, obj, nameName="name"):
        json = {}
        self._requiredJson(json, "id", obj["id"])
        self._optionalJson(json, "name", obj[nameName])
        return json

        
    def createGroup(self, name):
        api = "group"
        json = {}
        self._requiredJson(json, "name", name)
        status, jsonResponse = self.postJson(api, json)
        self.failUnless(200, api, status, jsonResponse, method="POST", input=json)
        return jsonResponse


    def getGroupById(self, groupId):
        if groupId is None:
            raise ValueError("groupId required")
        
        api = "group/" + groupId
        status, jsonResponse = self.getJson(api)
        self.failUnless(200, api, status, jsonResponse, method="GET")
        return jsonResponse
    
    
    def getGroupByName(self, name):
        if name is None:
            raise ValueError("name required")
        
        api = "group/name/" + name
        status, jsonResponse = self.getJson(api)
        self.failUnless(200, api, status, jsonResponse, method="GET")
        return jsonResponse
        
    
    def createUser(self, name):
        api = "user"
        json = {}
        self._requiredJson(json, "loginName", name)

        status, jsonResponse = self.postJson(api, json)
        self.failUnless(200, api, status, jsonResponse, method="POST", input=json)
        return jsonResponse


    def getUserById(self, userId):
        if userId is None:
            raise ValueError("userId required")

        api = "user/" + userId
        status, jsonResponse = self.getJson(api)
        self.failUnless(200, api, status, jsonResponse, method="GET")
        return jsonResponse

        
    def getUserByName(self, name):
        if name is None:
            raise ValueError("name required")

        api = "user/name/" + name
        status, jsonResponse = self.getJson(api)
        self.failUnless(200, api, status, jsonResponse, method="GET")
        return jsonResponse
        
        
    def updateUser(self, userId, name, groupRefs):
        api = "user/" + userId
        json = {}
        self._requiredJson(json, "userId", userId)
        self._optionalJson(json, "loginName", name)
        self._optionalJson(json, "groupRefs", groupRefs)

        status, jsonResponse = self.putJson(api, json)
        self.failUnless(200, api, status, jsonResponse, method="PUT", input=json)
        return jsonResponse


    def createMessage(self, sender, receiver=None, group=None, content=None, status="send"):
        api = "message/"
        json = {}
        self._requiredJson(json, "sender", sender)
        self._requiredJson(json, "content", content)
        self._requiredJson(json, "status", status)
        self._optionalJson(json, "receiver", receiver)
        self._optionalJson(json, "group", group)
        
        status, jsonResponse = self.postJson(api, json)
        self.failUnless(200, api, status, jsonResponse, method="POST", input=json)
        return jsonResponse

    
    def updateMessage(self, messageId, sender=None, receiver=None, group=None, content=None, status=None):
        api = "message/" + messageId
        
        json = {"id":messageId}
        self._requiredJson(json, "id", messageId)
        self._optionalJson(json, "sender", sender)
        self._optionalJson(json, "content", content)
        self._optionalJson(json, "status", status)
        self._optionalJson(json, "receiver", receiver)
        self._optionalJson(json, "group", group)
        
        if len(json) < 2:
            raise ValueError("no changes specified")
        
        status, jsonResponse = self.putJson(api, json)
        self.failUnless(200, api, status, jsonResponse, method="PUT", input=json)
        return jsonResponse


    def getMessageById(self, messageId):
        if messageId is None:
            raise ValueError("messageId required")

        api = "message/" + messageId
        status, jsonResponse = self.getJson(api)
        self.failUnless(200, api, status, jsonResponse, method="GET")
        return jsonResponse

    def searchMessage(self, receiverId=None, senderId=None, status="unread", offset=0, limit=20):
        if receiverId is None and senderId is None:
            raise ValueError("one of receiverId or senderId required")

        api = "message/search?" + self._queryParams(receiverId=receiverId, senderId=senderId, status=status, offset=offset, limit=limit)
        status, jsonResponse = self.getJson(api)
        self.failUnless(200, api, status, jsonResponse, method="GET")
        return jsonResponse
    
### EOF