import argparse
import sys
import time
import json
from messageapi import MessageApi

class UpdateMessageStatus(MessageApi):

    def __init__(self, parsed_arguments):
        super(UpdateMessageStatus, self).__init__(parsed_arguments)
        self.username = parsed_arguments.login
        self.password = parsed_arguments.password
        self.messageid = parsed_arguments.messageid
        self.status = parsed_arguments.status
        return
    
    def update(self):
        #password not required yet
        userJson = self.getUserByName(self.username)
        userRef = self.namedReferenceJson(userJson, nameName="loginName")
        
        jsonResponse = self.updateMessage(self.messageid, status=self.status)
        
        print jsonResponse
        return
                
if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Create a message')
    parser.add_argument('-l', '--login', help='user login to authenticate', default=None)
    parser.add_argument('-p', '--password', help='password to authenticate', default=None)
    parser.add_argument('-i', '--messageid', help='message unique id', default=None)
    parser.add_argument('-s', '--status', help='message status (draft, unread, read, send, sent, deleted, etc)', default="draft")
    parser.add_argument('-u', '--url', default='http://localhost:8080/grogers.message/rest/', help='url of the message server (including /message/rest/)')
    parser.add_argument('-v', '--verbose', const=True, default=False, action='store_const')
        
    parsed_arguments = parser.parse_args(sys.argv[1:])
    UpdateMessageStatus(parsed_arguments).update()
   

### EOF