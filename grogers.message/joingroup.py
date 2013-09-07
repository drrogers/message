import argparse
import sys
import time
import json
from messageapi import MessageApi

class JoinGroup(MessageApi):

    def __init__(self, parsed_arguments):
        super(JoinGroup, self).__init__(parsed_arguments)
        self.groupname = parsed_arguments.group
        self.username = parsed_arguments.login
        self.password = parsed_arguments.password
        return
    
    def joinGroup(self):
        #password not required yet
        userJson = self.getUserByName(self.username)
        print json
        groupJson = self.getGroupByName(self.groupname)
        print json

        groupRefs = userJson["groupRefs"]
        if groupRefs is None:
            groupRefs = []
            
        groupRefs.append( {"id":groupJson["id"], "name":groupJson["name"]} )
        
        updatedUserJson = self.updateUser(userJson["id"], userJson["loginName"], groupRefs)
        print updatedUserJson
        return
                
if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Have user join a group')
    parser.add_argument('-g', '--group', help='group name', default=None)
    parser.add_argument('-l', '--login', help='user login to authenticate', default=None)
    parser.add_argument('-p', '--password', help='password to authenticate', default=None)
    parser.add_argument('-u', '--url', default='http://ec2-54-200-9-5.us-west-2.compute.amazonaws.com:8080/message/rest', help='url of the message server (including /message/rest/)')
#     parser.add_argument('-u', '--url', default='http://localhost:8080/grogers.message/rest', help='url of the message server (including /message/rest/)')
    parser.add_argument('-v', '--verbose', const=True, default=False, action='store_const')
        
    parsed_arguments = parser.parse_args(sys.argv[1:])
    JoinGroup(parsed_arguments).joinGroup()
   

### EOF