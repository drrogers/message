import argparse
import sys
import time
import json
from messageapi import MessageApi

class GetUser(MessageApi):

    def __init__(self, parsed_arguments):
        super(GetUser, self).__init__(parsed_arguments)
        self.userId = parsed_arguments.userId
        self.username = parsed_arguments.login
        self.password = parsed_arguments.password
        return
    
    def get(self):
        if self.userId is not None:
            json = self.getUserById(self.userId)
        else:
            json = self.getUserByName(self.username)
        print json
                
if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Get user profile from user id.')
    parser.add_argument('-l', '--login', help='user login to authenticate', default=None)
    parser.add_argument('-p', '--password', help='password to authenticate', default=None)
    parser.add_argument('-i', '--userId', help='user id', default=None)
    parser.add_argument('-u', '--url', default='http://ec2-54-200-9-5.us-west-2.compute.amazonaws.com:8080/message/rest', help='url of the message server (including /message/rest/)')
#     parser.add_argument('-u', '--url', default='http://localhost:8080/grogers.message/rest', help='url of the message server (including /message/rest/)')
    parser.add_argument('-v', '--verbose', const=True, default=False, action='store_const')
        
    parsed_arguments = parser.parse_args(sys.argv[1:])
    GetUser(parsed_arguments).get()
   

### EOF