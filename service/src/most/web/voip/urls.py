#
# Project MOST - Moving Outcomes to Standard Telemedicine Practice
# http://most.crs4.it/
#
# Copyright 2014, CRS4 srl. (http://www.crs4.it/)
# Dual licensed under the MIT or GPL Version 2 licenses.
# See license-GPLv2.txt or license-MIT.txt
#

from django.conf.urls import patterns, include, url

from django.contrib import admin
admin.autodiscover()

urlpatterns = patterns('',
    url(r'test/$',  "most.web.voip.views.test"),
    url(r'accounts/$',  "most.web.voip.views.get_accounts"),
    url(r'accounts/(?P<account_uid>.*)/$',  "most.web.voip.views.get_account"),
    url(r'buddies/add/(?P<account_uid>.*)/$',  "most.web.voip.views.add_buddy"),
    url(r'buddies/(?P<account_uid>.*)/$',  "most.web.voip.views.get_buddies"),

)
