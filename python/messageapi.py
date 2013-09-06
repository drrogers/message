
import json
from exampleclientbase import ExampleClientBase

class MessageApi(ExampleClientBase):

    def __init__(self, parsed_arguments):
        super(MessageApi, self).__init__(parsed_arguments)
        if self.baseUrl is None:
            self.baseUrl = "http://ec2-54-200-9-5.us-west-2.compute.amazonaws.com:8080/message/rest/"
        return
    
    def createGroup(self, name):
        api = "group"
        json = { "name" : name }
        status, jsonResponse = self.postJson(api, json)
        self.failUnless(200, api, status, jsonResponse, method="POST", input=json)
        return jsonResponse

    def getGroupById(self, id):
        api = "group/" + id
        status, jsonResponse = self.getJson(api)
        self.failUnless(200, api, status, jsonResponse, method="GET")
        return jsonResponse
    
    def getGroupByName(self, name):
        api = "group/name/" + name
        status, jsonResponse = self.getJson(api)
        self.failUnless(200, api, status, jsonResponse, method="GET")
        return jsonResponse
        
    
    def createUser(self, name):
        api = "user"
        json = { "loginName" : name }
        status, jsonResponse = self.postJson(api, json)
        self.failUnless(200, api, status, jsonResponse, method="POST", input=json)
        return jsonResponse

    def getUserById(self, userId):
        api = "user/" + userId
        status, jsonResponse = self.getJson(api)
        self.failUnless(200, api, status, jsonResponse, method="GET")
        return jsonResponse
        
    def getUserByName(self, name):
        api = "user/name/" + name
        status, jsonResponse = self.getJson(api)
        self.failUnless(200, api, status, jsonResponse, method="GET")
        return jsonResponse
        
    def updateUser(self, userId, name, groupRefs):
        api = "user/" + userId
        json = { "id" : userId,
                "name" : name,
                "groupRefs" : groupRefs
                }
        status, jsonResponse = self.putJson(api, json)
        self.failUnless(200, api, status, jsonResponse, method="PUT", input=json)
        return jsonResponse

### EOF