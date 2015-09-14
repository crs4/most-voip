#
# Project MOST - Moving Outcomes to Standard Telemedicine Practice
# http://most.crs4.it/
#
# Copyright 2014, CRS4 srl. (http://www.crs4.it/)
# Dual licensed under the MIT or GPL Version 2 licenses.
# See license-GPLv2.txt or license-MIT.txt
#


from django.db import models
#from django.contrib.auth.models import User
from most.web.users.models import MostUser
from Cython.Shadow import address

# Create your models here.
class SipServer(models.Model):
    name = models.CharField(max_length=50)
    address = models.CharField(max_length=50)
    port = models.IntegerField()
    
    def _get_json_dict(self):
        
        result  = { 'name' : self.name, "address" : self.address , "port" : "%s" % self.port}
        return result
    json_dict = property(_get_json_dict)
    

class TurnServer(models.Model):
    name = models.CharField(max_length=50)
    address = models.CharField(max_length=50)
    port = models.IntegerField()
    
    def _get_json_dict(self):
        
        result  = { 'name' : self.name, "address" : self.address , "port" : "%s" % self.port}
        
        return result
    
    json_dict = property(_get_json_dict)

class Account(models.Model):
    user = models.ForeignKey(MostUser)
    name = models.CharField(max_length=50)
    sip_server = models.ForeignKey(SipServer)
    sip_username = models.CharField(max_length=50)
    sip_password = models.CharField(max_length=50)
    sip_transport = models.CharField(max_length=50)
    turn_server = models.ForeignKey(TurnServer)
    turn_username = models.CharField(max_length=50)
    turn_password = models.CharField(max_length=50)
    extension = models.CharField(max_length=50)
    
    
    def _get_json_dict(self):
        
        result  = { 
                      "sip_server" : self.sip_server.json_dict, "sip_transport" : self.sip_transport,
                       "sip_user" : self.sip_username, "sip_password": self.sip_password , "extension": self.extension
                                
                                }

    
        return result
    json_dict = property(_get_json_dict)


class Buddy(models.Model):
    account = models.ForeignKey(Account)
    name = models.CharField(max_length=50)
    extension = models.CharField(max_length=50)
