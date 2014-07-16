from django.db import models
from django.contrib.auth.models import User

# Create your models here.
class SipServer(models.Model):
    name = models.CharField(max_length=50)
    address = models.CharField(max_length=50)
    port = models.IntegerField()

class TurnServer(models.Model):
    name = models.CharField(max_length=50)
    address = models.CharField(max_length=50)
    port = models.IntegerField()

class Account(models.Model):
    user = models.ForeignKey(User)
    name = models.CharField(max_length=50)
    sip_server = models.ForeignKey(SipServer)
    sip_username = models.CharField(max_length=50)
    sip_password = models.CharField(max_length=50)
    sip_transport = models.CharField(max_length=50)
    turn_server = models.ForeignKey(TurnServer)
    turn_username = models.CharField(max_length=50)
    turn_password = models.CharField(max_length=50)
    extension = models.CharField(max_length=50)

class Buddy(models.Model):
    account = models.ForeignKey(Account)
    name = models.CharField(max_length=50)
    extension = models.CharField(max_length=50)
