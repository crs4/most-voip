#
# Project MOST - Moving Outcomes to Standard Telemedicine Practice
# http://most.crs4.it/
#
# Copyright 2014, CRS4 srl. (http://www.crs4.it/)
# Dual licensed under the MIT or GPL Version 2 licenses.
# See license-GPLv2.txt or license-MIT.txt
#


import json

from django.core.exceptions import ObjectDoesNotExist
from django.http import HttpResponse
from most.web.authentication.decorators import oauth2_required
from most.web.voip.models import Account, Buddy


def test(request):
    return HttpResponse(json.dumps({'success': True, 'data': {'message': 'Hello Voip'}}),
                        content_type="application/json")


@oauth2_required
def get_accounts(request):
    # Get accounts for current user
    accounts = Account.objects.filter(user=request.user)

    result = []
    for account in accounts:
        result.append({
            'name': account.name,
            'uid': account.id
        })

    return HttpResponse(json.dumps({'success': True, 'data': {'accounts': result}}), content_type="application/json")


@oauth2_required
def get_account(request, account_uid):
    # Get account
    account = Account.objects.filter(id=int(account_uid))

    if account:

        account = account.get()
        account_data = {
            'name': account.name,
            'sip_server': {
                'address': account.sip_server.address,
                'port': account.sip_server.port,
                'transport': account.sip_transport,
                'user': account.sip_username,
                'pwd': account.sip_password
            },
            'turn_server': {
                'address': account.turn_server.address,
                'port': account.turn_server.port,
                'user': account.turn_username,
                'pwd': account.turn_password
            },
            'extension': account.extension
        }

        return HttpResponse(json.dumps({'success': True, 'data': {'account': account_data}}),
                            content_type="application/json")

    else:

        return HttpResponse(json.dumps({'success': False, 'error': {'text': 'account not found'}}),
                            content_type="application/json")


@oauth2_required
def get_buddies(request, account_uid):
    try:
        account = Account.objects.get(id=int(account_uid))
        buddies = Buddy.objects.filter(account=account)
        buddies_data = []
        for buddy in buddies:
            buddies_data.append(
                {
                    'name': buddy.name,
                    'extension': buddy.extension
                }
            )

        return HttpResponse(json.dumps({'success': True, 'data': {'buddies': buddies_data}}),
                            content_type="application/json")

    except ObjectDoesNotExist, ex:
        return HttpResponse(json.dumps({'success': False, 'error': {'text': 'account not found'}}),
                            content_type="application/json")


@oauth2_required
def add_buddy(request, account_uid):
    account = Account.objects.get(id=int(account_uid))

    if 'name' not in request.REQUEST or not 'extension' in request.REQUEST:
        return HttpResponse(json.dumps({'success': False, 'error': {'text': 'invalid parameters'}}),
                            content_type="application/json")

    name = request.REQUEST['name']
    extension = request.REQUEST['extension']

    if Buddy.objects.filter(name=name).count() > 0:
        return HttpResponse(json.dumps({'success': False, 'error': {'text': 'buddy name already exists'}}),
                            content_type="application/json")
    else:
        buddy = Buddy()
        buddy.account = account
        buddy.name = name
        buddy.extension = extension
        buddy.save()

        return HttpResponse(json.dumps({'success': True, 'data': {'message': 'Saved'}}),
                            content_type="application/json")
