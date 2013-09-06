
import base64
import httplib2
import json
import os
import random
import sys
import time
from io import BytesIO

class ExampleClientBase(object):
    CA_CERT_FILE = None
    
    def __init__(self, parsed_arguments):
        self.verbose = parsed_arguments.verbose
        self.info = parsed_arguments
        self.baseUrl = parsed_arguments.url
        if not self.baseUrl.endswith("/"):
            self.baseUrl = self.baseUrl + "/" 
        
#         self.disable_ssl_certificate_validation = bool(parsed_arguments.disable_ssl_certificate_validation == "True")
        self.disable_ssl_certificate_validation = True 

        self.username = None
        self.password = None
        self.setCookie = None
        self._random = random.Random()
        
        return

    def random(self):
        """ returns float between -1 and 1.
        """
        return self._random.random() * 2.0 - 1.0
    
    def randomInt(self, min=1, max=100000):
        """ returns float between -1 and 1.
        """
        return self._random.randint(min, max);
        
    def randomDigits(self, n):
        """ returns string of n digits between 1 and 10**(n-1), left zero padded
        """
        v = self.randomInt(1, 10**(n-1))
        return (('0'*n)+str(v))[-n:]
        
    def postJson(self, url, jsonDict):
        jsonStr = json.dumps(jsonDict)
        response, responseContent = self.sendRequest(url, jsonStr, contentType="application/json", method="POST") 
        return self.unwrapJson(response, responseContent)
    
    def putJson(self, url, jsonDict):
        jsonStr = json.dumps(jsonDict)
        response, responseContent = self.sendRequest(url, jsonStr, contentType="application/json", method="PUT") 
        return self.unwrapJson(response, responseContent)

    def getJson(self, url):
        response, responseContent = self.sendRequest(url, None,method="GET") 
        return self.unwrapJson(response, responseContent)
    
    def deleteJson(self, url, jsonDict):
        jsonStr = json.dumps(jsonDict)
        response, responseContent = self.sendRequest(url, jsonStr, contentType="application/json", method="DELETE") 
        return self.unwrapJson(response, responseContent)

    def unwrapJson(self, response, responseContent):
        """ Returns status code and result json as a dictionary when status is 200.
        Returns status code and raw result content otherwise.
        """
        status = int(response['status'])
        try:
            resultData = json.loads(responseContent)
        except:
            resultData = responseContent
        return status, resultData

            
    def sendRequest(self, url, content, contentType="application/json", 
                    method="GET", username=None, password=None):
        """ Send the content to the REST end point.
        The result and result content is returned.
        """
        # Result content defaults to failure
        result_data = {"valid": False}
        
        http, headers = self.setupConnection(contentType=contentType, username=username, password=password)
        
        if not url.startswith("http"):
            url = self.baseUrl + url
        elif self.baseUrl.startswith("https:") and url.startswith(self.baseUrl.replace("https:", "http:", 1)):
            url = url.replace("http:", "https:")
        
        if self.verbose:
            print '#'*10, 'request:', url
        response, responseContent = http.request(url, method, headers=headers, body=content)

        if response.has_key('set-cookie'):
            self.setCookie = response['set-cookie']
            if self.verbose:
                print 'set-cookie received:', self.setCookie
        
        return response, responseContent

    def postFile(self, url, fields, file, username=None, password=None):
        """ Send the file as a multipart form to the REST end point.
        The result and result content is returned.
        """
        # Result content defaults to failure
        result_data = {"valid": False}
        
        content_type, content = self.encodeMultipartFormdata(fields, file) 
        http, headers = self.setupConnection(contentType=content_type, username=username, password=password)
        
        if not url.startswith("http"):
            url = self.baseUrl + url
        elif self.baseUrl.startswith("https:") and url.startswith(self.baseUrl.replace("https:", "http:", 1)):
            url = url.replace("http:", "https:")
        
        print '#'*10, 'request:', url

        response, responseContent = http.request(url, "POST", headers=headers, body=buffer(content))
        
        if response.has_key('set-cookie'):
            self.setCookie = response['set-cookie']
            if self.verbose:
                print 'set-cookie received:', self.setCookie
        
        return response, responseContent
                
    def setupConnection(self, contentType="application/json", username=None, password=None):
        """ Create an HTTP connection credentials.
        Returns an Http object.
        """
        ca_certs = None
        if self.CA_CERT_FILE != None:
            if os.path.exists(self.CA_CERT_FILE):
                ca_certs = self.CA_CERT_FILE
            else:
                print "Cannot find CA cert file: %s", self.CA_CERT_FILE
    
        proxy_info = None
            
        http = httplib2.Http(ca_certs=ca_certs, proxy_info=proxy_info, disable_ssl_certificate_validation=self.disable_ssl_certificate_validation)
        http.add_credentials(username, password)

        headers = {}
        
        if self.username != None or self.password != None:
            headers['authorization'] = 'Basic ' + base64.b64encode("%s:%s" % (self.username,self.password)).strip()
        
        if self.setCookie is not None:
           headers['Cookie'] = self.setCookie

        headers['Content-type'] = contentType

        return http, headers
    
    def encodeMultipartFormdata(self, fields, file):
        """
        fields is a sequence of (name, value) elements for regular form fields.
        files is a sequence of (name, filename, value) elements for data to be uploaded as files
        Return (content_type, body) ready for httplib.HTTP instance
        """
        BOUNDARY = '----------ThIs_Is_tHe_bouNdaRY_$'
        CRLF = '\r\n'
        L = []
        L.append('Content-Type: multipart/form-data; boundary=%s' % BOUNDARY)
        L.append('')
        for (key, value) in fields.iteritems():
            L.append('--' + BOUNDARY)
            L.append('Content-Disposition: form-data; name="%s"' % key)
            L.append('')
            L.append(value)
        # Attach the file
        L.append('--' + BOUNDARY)
        L.append('Content-Disposition: form-data; name="file"; filename="widget.wgt"')
        L.append('Content-Type: application/octet-stream')
        L.append('')
        L.append(file.read())
        L.append('--' + BOUNDARY + '--')
        L.append('')

        s = BytesIO()
        for element in L:
            s.write(str(element))
            s.write(CRLF)
        body = s.getvalue()

        content_type = 'multipart/form-data; boundary=%s' % BOUNDARY
        return content_type, body
    
    def getContentType(self, filename):
        return mimetypes.guess_type(filename)[0] or 'application/octet-stream'
        
#     def uniqueLogin(self):
#         return "joe.example." + str(time.time())
    
    def failUnless(self, expectedStatus, api,   status, responseContent, method="GET", input=None):
        if expectedStatus != status:
            msg = "Expected " + str(expectedStatus) + " got " + str(status) + " from " + api + " input: " + str(input) + " output:" + str(responseContent)
            raise Exception(msg)
        return

    def printJson(self, label, jsonDict):
        if self.verbose:
            print '>'*10, label
            print json.dumps(jsonDict, indent=4)
        return

#     @staticmethod
#     def printUsage():
#         print "options: -v  for verbose output\n         -i <count> number of itrations\n         -url <base host ur>"
#         print "example: -v -i 10 -url http://chatty.appconomy.com:8080"
#         return
    
    @staticmethod
    def getArgs():
        """ returns verbose, iteration count, cloud url
        """
        itc = 1
        url = "http://localhost:8080/message/rest/"
        verbose = False
        
        i = 1
        while i < len(sys.argv):
            if sys.argv[i] == "-v":
                verbose = True
            elif sys.argv[i] == "-i":
                i+=1
                itc = int(sys.argv[i])
            elif sys.argv[i] == "-url":
                i+=1
                url = sys.argv[i]
            elif sys.argv[i] == "-help":
                ExampleClientBase.printUsage();
                itc = 0
            elif sys.argv[i] == "-certs":
                i+=1
                ExampleClientBase.CA_CERT_FILE = sys.argv[i]
            else:
                print 'unknown argument ', sys.argv[i]
                ExampleClientBase.printUsage();
            i += 1

        return verbose, itc, url
    
