
import argparse
import sys
import time
import json
from messageapi import MessageApi

class CreateUser(MessageApi):

    def __init__(self, parsed_arguments):
        super(CreateUser, self).__init__(parsed_arguments)
        self.username = parsed_arguments.login
        self.password = parsed_arguments.password
        return
    
    def create(self):
        #password not required yet
        json = self.createUser(self.username)
        print json
                
if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Create a new user.')
    parser.add_argument('-l', '--login', help='user login to authenticate', default=None)
    parser.add_argument('-p', '--password', help='password to authenticate', default=None)
#     parser.add_argument('-u', '--url', default='http://ec2-54-200-9-5.us-west-2.compute.amazonaws.com:8080/message/rest', help='url of the message server (including /message/rest/)')
    parser.add_argument('-u', '--url', default='http://localhost:8080/grogers.message/rest', help='url of the message server (including /message/rest/)')
    parser.add_argument('--certs', type=file, help='File containing the CA certs.')
    parser.add_argument('--disable_ssl_certificate_validation', default=False, help='if server SSL cert is incorrect disable_ssl_certificate_validation=True')
    parser.add_argument('-v', '--verbose', const=True, default=False, action='store_const')
        
    parsed_arguments = parser.parse_args(sys.argv[1:])
    CreateUser(parsed_arguments).create()
   

### EOF