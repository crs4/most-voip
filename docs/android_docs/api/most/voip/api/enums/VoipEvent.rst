VoipEvent
=========

.. java:package:: most.voip.api.enums
   :noindex:

.. java:type:: public enum VoipEvent

   Contains all events triggered by the library

Enum Constants
--------------
ACCOUNT_REGISTERED
^^^^^^^^^^^^^^^^^^

.. java:field:: public static final VoipEvent ACCOUNT_REGISTERED
   :outertype: VoipEvent

   The sip user has been successfully registered to the remote Sip Server (this event is also triggered called for each registration renewal)

ACCOUNT_REGISTERING
^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final VoipEvent ACCOUNT_REGISTERING
   :outertype: VoipEvent

   The Sip user is under registration process (this event triggered only for explicit registration requests, so it is no called during automatic registration renewals)

ACCOUNT_REGISTRATION_FAILED
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final VoipEvent ACCOUNT_REGISTRATION_FAILED
   :outertype: VoipEvent

   The User Account Registration process failed for some reason (e.g authentication failed)

ACCOUNT_UNREGISTERED
^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final VoipEvent ACCOUNT_UNREGISTERED
   :outertype: VoipEvent

   The sip user has been successfully unregistered

ACCOUNT_UNREGISTERING
^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final VoipEvent ACCOUNT_UNREGISTERING
   :outertype: VoipEvent

   The Sip user is under unregistration process

ACCOUNT_UNREGISTRATION_FAILED
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final VoipEvent ACCOUNT_UNREGISTRATION_FAILED
   :outertype: VoipEvent

   The User Account Unregistration process failed for some reason (e.g the sip server is down)

BUDDY_CONNECTED
^^^^^^^^^^^^^^^

.. java:field:: public static final VoipEvent BUDDY_CONNECTED
   :outertype: VoipEvent

   The remote user is connected (i.e is in ON LINE status)

BUDDY_DISCONNECTED
^^^^^^^^^^^^^^^^^^

.. java:field:: public static final VoipEvent BUDDY_DISCONNECTED
   :outertype: VoipEvent

   The remote user is no longer connected (i.e is in OFF LINE status)

BUDDY_HOLDING
^^^^^^^^^^^^^

.. java:field:: public static final VoipEvent BUDDY_HOLDING
   :outertype: VoipEvent

   The remote user is still connected, but it is not available at the moment (it is in BUSY state)

BUDDY_SUBSCRIBED
^^^^^^^^^^^^^^^^

.. java:field:: public static final VoipEvent BUDDY_SUBSCRIBED
   :outertype: VoipEvent

   The remote user has been successfully subscribed (it is now possible to get status notifications about it)

BUDDY_SUBSCRIBING
^^^^^^^^^^^^^^^^^

.. java:field:: public static final VoipEvent BUDDY_SUBSCRIBING
   :outertype: VoipEvent

   a remote user is under subscrition process

BUDDY_SUBSCRIPTION_FAILED
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final VoipEvent BUDDY_SUBSCRIPTION_FAILED
   :outertype: VoipEvent

   The remote user subscription process failed for some reason

CALL_ACTIVE
^^^^^^^^^^^

.. java:field:: public static final VoipEvent CALL_ACTIVE
   :outertype: VoipEvent

   The call is active

CALL_DIALING
^^^^^^^^^^^^

.. java:field:: public static final VoipEvent CALL_DIALING
   :outertype: VoipEvent

   an outcoming call is ringing

CALL_HANGUP
^^^^^^^^^^^

.. java:field:: public static final VoipEvent CALL_HANGUP
   :outertype: VoipEvent

   The local user hangs up

CALL_HOLDING
^^^^^^^^^^^^

.. java:field:: public static final VoipEvent CALL_HOLDING
   :outertype: VoipEvent

   The local user puts on hold the call

CALL_INCOMING
^^^^^^^^^^^^^

.. java:field:: public static final VoipEvent CALL_INCOMING
   :outertype: VoipEvent

   an incoming call is ringing

CALL_READY
^^^^^^^^^^

.. java:field:: public static final VoipEvent CALL_READY
   :outertype: VoipEvent

   a new call is ready to become active or rejected

CALL_REMOTE_DISCONNECTION_HANGUP
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final VoipEvent CALL_REMOTE_DISCONNECTION_HANGUP
   :outertype: VoipEvent

   The remote server has been disconnected so the call was interrupted.

CALL_REMOTE_HANGUP
^^^^^^^^^^^^^^^^^^

.. java:field:: public static final VoipEvent CALL_REMOTE_HANGUP
   :outertype: VoipEvent

   The remote user hangs up

CALL_UNHOLDING
^^^^^^^^^^^^^^

.. java:field:: public static final VoipEvent CALL_UNHOLDING
   :outertype: VoipEvent

   The local user unholds the call

LIB_CONNECTION_FAILED
^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final VoipEvent LIB_CONNECTION_FAILED
   :outertype: VoipEvent

   The connection to the remote Sip Server failed (a Timeout occurred during account an registration request tothe remote Sip Server)

LIB_DEINITIALIZATION_FAILED
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final VoipEvent LIB_DEINITIALIZATION_FAILED
   :outertype: VoipEvent

   The library deinitialization process failed for some reason (e.g authentication failed)

LIB_DEINITIALIZED
^^^^^^^^^^^^^^^^^

.. java:field:: public static final VoipEvent LIB_DEINITIALIZED
   :outertype: VoipEvent

   The lib was successully deinitialied

LIB_DEINITIALIZING
^^^^^^^^^^^^^^^^^^

.. java:field:: public static final VoipEvent LIB_DEINITIALIZING
   :outertype: VoipEvent

   The library is under deinitilization process

LIB_INITIALIZATION_FAILED
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final VoipEvent LIB_INITIALIZATION_FAILED
   :outertype: VoipEvent

   The library initialization process failed for some reason (e.g authentication failed)

LIB_INITIALIZED
^^^^^^^^^^^^^^^

.. java:field:: public static final VoipEvent LIB_INITIALIZED
   :outertype: VoipEvent

   The lib was successully initialied

LIB_INITIALIZING
^^^^^^^^^^^^^^^^

.. java:field:: public static final VoipEvent LIB_INITIALIZING
   :outertype: VoipEvent

   The library is under initilization process

