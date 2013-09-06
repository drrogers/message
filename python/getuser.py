import argparse
import sys
import time
import json
from messageapi import MessageApi

class GetUser(MessageApi):

    def __init__(self, parsed_arguments):
        super(GetUser, self).__init__(parsed_arguments)
        self.userId = parsed_arguments.userId
        return
    
    def get(self):
        #password not required yet
        json = self.getUserById(self.userId)
        print json
                
if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Get user profile from user id.')
    parser.add_argument('-i', '--userId', help='user id', default=None)
    parser.add_argument('-u', '--url', default='http://localhost:8080/grogers.message/rest/', help='url of the message server (including /message/rest/)')
    parser.add_argument('-v', '--verbose', const=True, default=False, action='store_const')
        
    parsed_arguments = parser.parse_args(sys.argv[1:])
    GetUser(parsed_arguments).get()
   

### EOF