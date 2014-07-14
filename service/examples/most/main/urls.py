from django.conf.urls import patterns, include, url

from django.contrib import admin
admin.autodiscover()

urlpatterns = patterns('',
    url(r'^admin/', include(admin.site.urls)),
    url(r'^oauth2/', include('provider.oauth2.urls', namespace = 'oauth2')),
    url(r'^accounts/login/$', 'django.contrib.auth.views.login'),
    url(r'^voip/', include('most.web.voip.urls', namespace = 'voip')),

    url(r'^test$',  "most.web.authentication.views.test_auth"),

)
