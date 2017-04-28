#
# Project MOST - Moving Outcomes to Standard Telemedicine Practice
# http://most.crs4.it/
#
# Copyright 2014, CRS4 srl. (http://www.crs4.it/)
# Dual licensed under the MIT or GPL Version 2 licenses.
# See license-GPLv2.txt or license-MIT.txt
#
from django.core.exceptions import ValidationError
from django.db import models
from most.web.users.models import MostUser


# from Cython.Shadow import address

# Create your models here.
class SipServer(models.Model):
    name = models.CharField(max_length=50)
    address = models.CharField(max_length=50)
    port = models.IntegerField()

    def _get_json_dict(self):
        result = {'name': self.name, "address": self.address, "port": "%s" % self.port}
        return result

    json_dict = property(_get_json_dict)

    def __unicode__(self):
        return '[Sip Server: {name}]'.format(name=self.name)


class TurnServer(models.Model):
    name = models.CharField(max_length=50)
    address = models.CharField(max_length=50)
    port = models.IntegerField()

    def _get_json_dict(self):
        result = {'name': self.name, "address": self.address, "port": "%s" % self.port}

        return result

    json_dict = property(_get_json_dict)

    def __unicode__(self):
        return '[Turn Server: {name}]'.format(name=self.name)


class Account(models.Model):
    user = models.ForeignKey(MostUser)
    name = models.CharField(max_length=50)
    sip_server = models.ForeignKey(SipServer)
    sip_username = models.CharField(max_length=50)
    sip_password = models.CharField(max_length=50)
    sip_transport = models.CharField(max_length=50)
    turn_server = models.ForeignKey(TurnServer, null=True, blank=True)
    turn_username = models.CharField(max_length=50, null=True, blank=True)
    turn_password = models.CharField(max_length=50, null=True, blank=True)
    extension = models.CharField(max_length=50)

    def clean(self):
        if self.turn_server and (not self.turn_username or not self.turn_password):
            raise ValidationError("Turn username and turn password are required")

    def _get_json_dict(self):
        result = {
            "sip_server": self.sip_server.json_dict, "sip_transport": self.sip_transport,
            "sip_user": self.sip_username, "sip_password": self.sip_password, "extension": self.extension,
            "turn_server": None if self.turn_server is None else self.turn_server.json_dict,
            "turn_user": self.turn_username, "turn_password": self.turn_password
        }

        return result

    def __unicode__(self):
        return '[Sip Account: {name}]'.format(name=self.name)

    json_dict = property(_get_json_dict)


class Buddy(models.Model):
    account = models.ForeignKey(Account)
    name = models.CharField(max_length=50)
    extension = models.CharField(max_length=50)

    def __unicode__(self):
        return '[Buddy: {name} : {extension}]'.format(name=self.name, extension=self.extension)
