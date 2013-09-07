import argparse
import sys
import time
import json
from messageapi import MessageApi

class CreateMessage(MessageApi):

    def __init__(self, parsed_arguments):
        super(CreateMessage, self).__init__(parsed_arguments)
        self.username = parsed_arguments.login
        self.password = parsed_arguments.password
        self.receivername = parsed_arguments.receiver
        self.groupname = parsed_arguments.group
        self.content = parsed_arguments.content
        self.status = parsed_arguments.status
        return
    
    def create(self):
        #password not required yet
        userJson = self.getUserByName(self.username)
        userRef = self.namedReferenceJson(userJson, nameName="loginName")
        
        receiverJson = None
        receiverRef = None
        if self.receivername is not None:
            receiverJson = self.getUserByName(self.receivername)
            receiverRef = self.namedReferenceJson(receiverJson, nameName="loginName")
            
        groupJson = None
        groupRef = None
        if self.groupname is not None:
            groupJson = self.getGroupByName(self.groupname)
            groupRef = self.namedReferenceJson(groupJson)

        jsonResponse = self.createMessage(userRef, receiver=receiverRef, group=groupRef, content=self.content, status=self.status)
        
        print jsonResponse
        return
                
if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Create a message')
    parser.add_argument('-l', '--login', help='user login to authenticate', default=None)
    parser.add_argument('-p', '--password', help='password to authenticate', default=None)
    parser.add_argument('-r', '--receiver', help='receiver user name', default=None)
    parser.add_argument('-g', '--group', help='group name', default=None)
    parser.add_argument('-c', '--content', help='message content', default=None)
    parser.add_argument('-s', '--status', help='message status (draft, unread, read, send, sent, deleted, etc)', default="draft")
    parser.add_argument('-u', '--url', default='http://ec2-54-200-9-5.us-west-2.compute.amazonaws.com:8080/message/rest', help='url of the message server (including /message/rest/)')
#     parser.add_argument('-u', '--url', default='http://localhost:8080/grogers.message/rest', help='url of the message server (including /message/rest/)')
    parser.add_argument('-v', '--verbose', const=True, default=False, action='store_const')
        
    parsed_arguments = parser.parse_args(sys.argv[1:])
    CreateMessage(parsed_arguments).create()
   

### EOF