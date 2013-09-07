import argparse
import sys
import time
import json
from messageapi import MessageApi

class SearchMessages(MessageApi):

    def __init__(self, parsed_arguments):
        super(SearchMessages, self).__init__(parsed_arguments)
        self.username = parsed_arguments.login
        self.password = parsed_arguments.password
        self.issender = parsed_arguments.issender
        self.status = parsed_arguments.status
        return
    
    def search(self):
        #TODO: server accepts senderId and receiverId, add allow search for sent X to by Y 
        #password not required yet
        userJson = self.getUserByName(self.username)
        if self.issender:
            receiverId = None
            senderId = userJson["id"]
        else:
            receiverId = userJson["id"]
            senderId = None
            
        jsonResponse = self.searchMessage(receiverId=receiverId, senderId=senderId, status=self.status)
        
        self.printJson("search results", jsonResponse)
        return
                
if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Create a message')
    parser.add_argument('-l', '--login', help='user login to authenticate', default=None)
    parser.add_argument('-p', '--password', help='password to authenticate', default=None)
    parser.add_argument('--issender', help='user is sender', action="store_true")
    parser.add_argument('-s', '--status', help='message status (draft, unread, read, send, sent, deleted, etc)', default=None)
    parser.add_argument('-u', '--url', default='http://ec2-54-200-9-5.us-west-2.compute.amazonaws.com:8080/message/rest', help='url of the message server (including /message/rest/)')
#     parser.add_argument('-u', '--url', default='http://localhost:8080/grogers.message/rest', help='url of the message server (including /message/rest/)')
    parser.add_argument('-v', '--verbose', const=True, default=False, action='store_const')
        
    parsed_arguments = parser.parse_args(sys.argv[1:])
    SearchMessages(parsed_arguments).search()

### EOF