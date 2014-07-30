#
# Project MOST - Moving Outcomes to Standard Telemedicine Practice
# http://most.crs4.it/
#
# Copyright 2014, CRS4 srl. (http://www.crs4.it/)
# Dual licensed under the MIT or GPL Version 2 licenses.
# See license-GPLv2.txt or license-MIT.txt
#


from django.contrib import admin
from most.web.voip.models import SipServer, TurnServer, Account, Buddy


class MostSipServerAdmin(admin.ModelAdmin):
    pass


class MostTurnServerAdmin(admin.ModelAdmin):
    pass


class AccountAdmin(admin.ModelAdmin):
    pass


class BuddyAdmin(admin.ModelAdmin):
    pass


# Register your models here.
admin.site.register(SipServer, MostSipServerAdmin)
admin.site.register(TurnServer, MostTurnServerAdmin)
admin.site.register(Account, AccountAdmin)
admin.site.register(Buddy, BuddyAdmin)
