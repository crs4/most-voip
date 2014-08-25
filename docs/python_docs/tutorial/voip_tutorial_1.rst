

Tutorial 1: Making a Call
========================

This first tutorial shows how to make a call to an arbitrary destination
using the Voip Library. To make a call, you have to perform the
following steps, each of them explained in the next sections.

.. toctree::
   :maxdepth: 3
 
   voip_tutorial_1
   blank

Note that this example, to work, requires a Sip Server (e.g Asterisk)
installed and running on a reachable PC. For getting instructions about
the Asterisk configuration, click `here <asterisk_configuration.html>`_

Step 1: Initialize the Library
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

First of all, you have to import and instance the class *VoipLib*

.. code:: python

    # add the most.voip library root dir to the current python path...
    import sys
    sys.path.append("../src/")
    
    # import the Voip Library
    from most.voip.api import VoipLib
    
    # instanziate the lib
    my_voip = VoipLib()
Now, you have to build a dictionary containing all parameters needed for
the Lib initialization

.. code:: python

    # build a dictionary containing all parameters needed for the Lib initialization
    
    voip_params = {  u'username': u'ste',  # a name describing the user
                     u'sip_server_address': u'192.168.1.100',  # the ip of the remote sip server (default port: 5060)
                     u'sip_server_user': u'ste', # the username of the sip account
                     u'sip_server_pwd': u'ste',  #  the password of the sip account
                     u'sip_server_transport' :u'udp', # the transport type (default: tcp) 
                     u'log_level' : 1,  # the log level (greater values provide more informations)
                     u'debug' : False  # enable/disable debugging messages
                     }
| At this point, you have to implement a callback method that will be
called by the voip library to notify any relevant voip event. You can
choose an arbitrary name for this method, but it must contain the
following 3 arguments: 1. *voip\_event\_type* argument indicating the
type of the triggered event (VoipEventType.LIB\_EVENT,
VoipEventType.ACCOUNT\_EVENT, VoipEventType.BUDDY\_EVENT or
VoipEventType.CALL\_EVENT)
| 2. *voip\_event* reporting the specific event (e.g
VoipEvent.ACCOUNT\_REGISTERED to notify an account registration) 3.
*params* a dictionary containing additional informations, depending on
the specific triggered event call the *initialize* method passing the 2
parameters defined above

.. code:: python

    
    # define a method used for receive event notifications from the lib:
    
    def notify_events(voip_event_type, voip_event, params):
        print "Received Event Type:%s -> Event: %s Params: %s" % (voip_event_type, voip_event, params)
        
At this point, you are ready to initialize the library passing the
dictionary and the callback method defined above:

.. code:: python

    # initialize the lib passing the dictionary and the callback method defined above:
    my_voip.init_lib(voip_params, notify_events)

.. parsed-literal::

    Received Event Type:EVENT_TYPE__LIB_EVENT -> Event: VOIP_EVENT__LIB_INITIALIZING Params: {'params': {u'username': u'ste', u'sip_server_transport': u'udp', u'log_level': 1, u'sip_server_user': u'ste', u'sip_server_pwd': u'ste', u'debug': False, u'sip_server_address': u'192.168.1.100'}, 'success': True}
    Received Event Type:EVENT_TYPE__LIB_EVENT -> Event: VOIP_EVENT__LIB_INITIALIZED Params: {'sip_server': '192.168.1.100', 'success': True}




.. parsed-literal::

    True



The example above assumes that you have a Sip Server (e.g, Asterisk)
running on a pc reachable at the address 192.168.1.100.

Note that, so far, no connection to the Sip Server has been established
yet. The *init\_lib* method returns a *True* value if the initialization
request completes without errors, *False* otherwise.

Finally, note that at the end of the inititialization process the method
**notify\_events** is called, containing all informations related to the
outcome of the initialization process.

Step 2: Registering the account on the Sip Server
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Now, you are ready to register the user to the sip server (in this
example, we are registering a user called *ste* with the password *ste*.
We assume that the Sip Server knows this user and is able to accept the
registration request from it).

.. code:: python

    my_voip.register_account()

.. parsed-literal::

    Received Event Type:EVENT_TYPE__ACCOUNT_EVENT -> Event: VOIP_EVENT__ACCOUNT_REGISTERING Params: {'account_info': u'ste', 'Success': True}




.. parsed-literal::

    True



Also in this case, the library calls the method *notify\_events* to
notify the outcome of the registration process. In particular, this
method is called as soon as a registration request is sent (with a
VoipEvent.\_ACCOUNT\_REGISTERING event) and later, as soon as the
registration is accepted by the remote Sip server (with a
VoipEvent.\_ACCOUNT\_REGISTERED state) or refused (with a
VoipEvent.\_ACCOUNT\_REGISTRATION\_FAILED event)

Step 3: Making a call to an arbitrary extension
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

In case of successfull registration, you can dial an extension (or call
an arbitrary Sip User) in the following way:

.. code:: python

    my_extension = "1234"
    my_voip.make_call(my_extension)
    
    import time
    # wait until the call is active
    while(True):
        time.sleep(1)
    

Note that the notify\_events method is called when the call is
established (with the event VoipEvent.CALL\_ACTIVE)

Step 4: Hangup the active call
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

To hangup the call you have just to call the method *hangup\_call*:

.. code:: python

    # ends the current call
    my_voip.hangup_call()



.. parsed-literal::

    True



Note that, when the user hangs up the call , the callback method is
called again with the event VoipEvent.CALL\_HANGUP)

.. code:: python

    