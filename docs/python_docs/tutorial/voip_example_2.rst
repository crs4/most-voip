
Most Voip Library
=================

Introduction
------------

This second example of the Most Voip Library shows how to listen for and
to answer to incoming calls.

Note that this example, to work, requires a Sip Server (e.g Asterisk)
installed and running on a reachable PC. For getting instructions about
the Asterisk configuration, click
`here <asterisk_configuration.ipynb>`__

Example 2: Answering a Call
---------------------------

This second example shows how to answer an incoming call using the Voip
Library. It assumes that you have the Asterisk Sip Server installed on a
reacheable machine. First of all, you have to perform the 3 following
steps:

1. Import and instance the Most Voip Library
2. Implement the **notify\_events(voip\_event\_type, voip\_event,
   params)** method where to receive all notifications
3. Initialize the Voip Library and Register the account
4. Write a loop for waiting for incoming calls
5. Open a CLI asterisk console and type the command for making a call to
   the user registered at the previous step

Step 1: Import and instance the voip lib
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

These steps have been already explained in the previous example. However
note that, this time, we also import the **VoipEvent** class, that will
be used in the callback method **notify\_events** for detecting the type
of the incoming events.

.. code:: python

    # append the most voip library location to the pythonpath
    import sys
    sys.path.append("../src/")
    
    
    # import the Voip Library
    from most.voip.api import VoipLib
    from most.voip.constants import VoipEvent
    # instanziate the lib
    my_voip = VoipLib()
    
    # build a dictionary containing all parameters needed for the Lib initialization
    
    voip_params = {u'username': u'ste', 
                       u'sip_server_pwd': u'ste', 
                       u'sip_server_address': u'1192.168.1.100' ,  
                       u'sip_server_user': u'ste', 
                       u'sip_server_transport' :u'udp',
                       u'log_level' : 1,
                       u'debug' : False }
Step 2: Implement the CallBack method where to receive notifications about incoming calls and other relevant events
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code:: python

    import time
    end_of_call = False # used as exit condition from the while loop at the end of this example
    
    # implement a method that will capture all the events triggered by the Voip Library
    def notify_events(voip_event_type,voip_event, params):
        print "Received Event Type:%s  Event:%s -> Params: %s" % (voip_event_type, voip_event, params)
        
        # event triggered when the account registration has been confirmed by the remote Sip Server 
        if (voip_event==VoipEvent.ACCOUNT_REGISTERED):
            print "Account %s registered: ready to accept call!" % myVoip.get_account().get_uri()
        
        # event triggered when a new call is incoming
        elif (voip_event==VoipEvent.CALL_INCOMING):
            print "INCOMING CALL From %s" % params["from"]
            time.sleep(2)
            print "Answering..."
            myVoip.answer_call()
        
        # event triggered when the call has been established    
        elif(voip_event==VoipEvent.CALL_ACTIVE):
            print "The call with %s has been established"  % myVoip.get_call().get_remote_uri()
            
            dur = 4
            print "Waiting %s seconds before hanging up..."  % dur
            time.sleep(dur)
            myVoip.hangup_call()
          
        
        # events triggered when the call ends for some reasons      
        elif (voip_event in [VoipEvent.CALL_REMOTE_DISCONNECTION_HANGUP, VoipEvent.CALL_REMOTE_HANGUP, VoipEvent.CALL_HANGUP]):
            print "End of call. Destroying lib..."
            myVoip.destroy_lib()
            
        # event triggered when the library was destroyed   
        elif (voip_event==VoipEvent.LIB_DEINITIALIZED):
            print "Call End. Exiting from the app."
            end_of_call = True
        
        # just print informations about other events triggered by the library
        else:
            print "Received unhandled event type:%s --> %s" % (voip_event_type,voip_event)
        
The method above detects the **VoipEvent.CALL\_INCOMING** state, that is
triggered when a remote user makes a call to the registered account (the
user 'ste' in this example). In this example, we answer the incoming
call and, in this way, the call is enstablished between the 2 users and
the event **VoipEvent.CALL\_CALLING** is triggered. At this point, we
decide to wait 4 seconds before hanging up the call, by calling the
**hangup\_call** method. This method will end the current active call
and will trigger the **VoipEvent.CALL\_HANGUP** method (or one of the
events **VoipEvent.CALL\_REMOTE\_DISCONNECTION\_HANGUP** and
**VoipEvent.CALL\_REMOTE\_HANGUP** if the remote user terminates the
call before us), so we destroy the voip lib and wait for the
**VoipEvent.LIB\_DEINITIALIZED** event to set the flag **end\_of\_call**
equals to True to notify the end of this example outside of this method.

Step 3: Initialize the Voip Library and register the account on the Sip Server
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Now we have to initialize the library (by passing the notification
method and the initialization params defined above) and register the
account.

.. code:: python

    # initialize the lib passing the dictionary and the callback method defined above:
    my_voip.init_lib(voip_params, notify_events)
    
    # register the account
    my_voip.register_account()

.. parsed-literal::

    Received Event Type:EVENT_TYPE__LIB_EVENT  Event:VOIP_EVENT__LIB_INITIALIZING -> Params: {'params': {u'username': u'ste', u'sip_server_transport': u'udp', u'log_level': 1, u'sip_server_user': u'ste', u'sip_server_pwd': u'ste', u'debug': False, u'sip_server_address': u'1192.168.1.100'}, 'success': True}
    Received unhandled event type:EVENT_TYPE__LIB_EVENT --> VOIP_EVENT__LIB_INITIALIZING
    Received Event Type:EVENT_TYPE__LIB_EVENT  Event:VOIP_EVENT__LIB_INITIALIZED -> Params: {'sip_server': '1192.168.1.100', 'success': True}
    Received unhandled event type:EVENT_TYPE__LIB_EVENT --> VOIP_EVENT__LIB_INITIALIZED
    Received Event Type:EVENT_TYPE__ACCOUNT_EVENT  Event:VOIP_EVENT__ACCOUNT_REGISTERING -> Params: {'account_info': u'ste', 'Success': True}
    Received unhandled event type:EVENT_TYPE__ACCOUNT_EVENT --> VOIP_EVENT__ACCOUNT_REGISTERING




.. parsed-literal::

    True



Step 4: Add a 'while' loop for waiting for incoming calls
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Now we are ready to wait for incoming call, so we can add a simple
'while loop' that doen't anything and exit when tha flag 'end\_of\_call'
assumes the **true** value.

.. code:: python

    while (end_of_call==False):
        time.sleep(2)

Step 5: Originate a call from the Sip Server for testing the example
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Open a CLI asterisk console and type the the following command for
making a call to the user registered at the **step 3**:

**originate SIP/ste extension**

This commands originate a call from the sip server to the user 'ste'
registered at the step 3. Obviously, it assumes that you have configured
the Asterisk Server so that the user 'ste' is a known sip user. To do it
, you have to configure the sip configuration file, called **sip.conf**
(in Linux platforms, it is generally located in the folder
/etc/asterisk).

; user section added at the end odf the configuration file sip.conf

[ste]

type=friend

secret=ste

host=dynamic

context=local\_test


