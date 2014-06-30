from django.shortcuts import render
import datetime, json
from django.http import HttpResponse


def test(request):

    return HttpResponse(json.dumps({'success': True, 'data': {'message': 'Hello Voip'}}), content_type="application/json")


def get_accounts(request):

    accounts = [
        {'name': 'account_name_1', 'uid':'1'},
        {'name': 'account_name_2', 'uid':'2'},
        {'name': 'account_name_3', 'uid':'3'},
        {'name': 'account_name_4', 'uid':'4'},
        {'name': 'account_name_5', 'uid':'5'}
    ]
    return HttpResponse(json.dumps({'success': True, 'data': {'accounts': accounts}}), content_type="application/json")


def get_account(request, account_uid):

    account = {}
    if account_uid == '1':
        account = {
            'name': 'account_name_1',
            'sip_server' : {
                'address': '156.148.33.226',
                'port': 5060,
                'transport' : 'udp',
                'user' : 'ste',
                'pwd' : 'ste'
            },
            'turn_server': {
                'address': '156.148.33.226',
                'port': 3478,
                'user': 'ste',
                'pwd': 'ste'
            },
            'extension' : 'ste'
        }
    elif account_uid == '2':
        account = {
            'name': 'account_name_2',
            'sip_server' : {
                'address': '156.148.33.226',
                'port': 5060,
                'transport' : 'udp',
                'user' : 'ste2',
                'pwd' : 'ste2'
            },
            'turn_server': {
                'address': '156.148.33.226',
                'port': 3478,
                'user': 'ste2',
                'pwd': 'ste2'
            },
            'extension' : 'ste2'
        }
    else:
        account = {
            'name': 'account_name_3',
            'sip_server' : {
                'address': '156.148.33.226',
                'port': 5060,
                'transport' : 'udp',
                'user' : 'steand',
                'pwd' : 'ste2'
            },
            'turn_server': {
                'address': '156.148.33.226',
                'port': 3478,
                'user': 'steand',
                'pwd': 'steand'
            },
            'extension' : 'steand'
        }

    return HttpResponse(json.dumps({'success': True, 'data': {'account': account}}), content_type="application/json")


def get_buddies(request, account_uid):

    buddies = [
        {'name':'ste1', 'extension': 'ste'},
        {'name':'ste2', 'extension': 'ste2'},
        {'name':'ste3', 'extension': 'steand'}
    ]
    return HttpResponse(json.dumps({'success': True, 'data': {'buddies': buddies}}), content_type="application/json")


def add_buddy(request):
    return HttpResponse(json.dumps({'success': False, 'error': {'message': 'Not Implemented Error'}}), content_type="application/json")
