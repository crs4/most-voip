from django.conf.urls import patterns, include, url

from django.contrib import admin
admin.autodiscover()

urlpatterns = patterns('',
    url(r'test/$',  "most.web.voip.views.test"),
    url(r'accounts/$',  "most.web.voip.views.get_accounts"),
    url(r'accounts/(?P<account_uid>.*)/$',  "most.web.voip.views.get_account"),
    url(r'buddies/add/$',  "most.web.voip.views.add_buddy"),
    url(r'buddies/(?P<account_uid>.*)/$',  "most.web.voip.views.get_buddies"),

)
