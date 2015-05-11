.. java:import:: java.util HashMap

.. java:import:: most.voip.api.interfaces IAccount

.. java:import:: most.voip.api.interfaces IBuddy

.. java:import:: most.voip.api.interfaces ICall

.. java:import:: most.voip.api.interfaces IServer

.. java:import:: android.content Context

.. java:import:: android.os Handler

VoipLib
=======

.. java:package:: most.voip.api
   :noindex:

.. java:type:: public interface VoipLib

   It is the core class of the Library, that allows you to:

   ..

   * initialize the Voip Library
   * create an account and register it on a remote Sip Server
   * make a call
   * listen for incoming calls and answer
   * get instances of IAccount, ICall and IServer objects

Methods
-------
answerCall
^^^^^^^^^^

.. java:method:: public boolean answerCall()
   :outertype: VoipLib

   Answer a call

   :return: false if this command was ignored for some reasons (e.g there is already an active call), true otherwise

destroyLib
^^^^^^^^^^

.. java:method:: public boolean destroyLib()
   :outertype: VoipLib

   Destroy the Voip Lib

   :return: \ ``true``\  if no error occurred in the deinitialization process

getAccount
^^^^^^^^^^

.. java:method:: public IAccount getAccount()
   :outertype: VoipLib

   Get informations about the local sip account

   :return: informations about the local sip account , like its current state

getCall
^^^^^^^

.. java:method:: public ICall getCall()
   :outertype: VoipLib

   Get the current call info (if any)

   :return: informations about the current call (if any), like the current Call State

getServer
^^^^^^^^^

.. java:method:: public IServer getServer()
   :outertype: VoipLib

   Get informations about the remote Sip Server

   :return: informations about the current sip server, like the current Server State

hangupCall
^^^^^^^^^^

.. java:method:: public boolean hangupCall()
   :outertype: VoipLib

   Close the current active call

   :return: true if no error occurred during this operation, false otherwise

holdCall
^^^^^^^^

.. java:method:: public boolean holdCall()
   :outertype: VoipLib

   Put the active call on hold status

   :return: true if no error occurred during this operation, false otherwise

initLib
^^^^^^^

.. java:method:: public boolean initLib(Context context, HashMap<String, String> configParams, Handler notificationHandler)
   :outertype: VoipLib

   Initialize the Voip Lib

   :param context: application context of the activity that uses this library
   :param configParams: All needed configuration string parameters. All the supported parameters are the following (turn server params are needed only if you intend to use a turn server):

   ..

   * sipServerIp: the ip address of the Sip Server (e.g Asterisk)
   * sipServerPort: the port of the Sip Server (default: 5060)
   * sipServerTransport: the sip transport: it can be "udp" or "tcp" (default: "udp")
   * sipUserName: the account name of the peer to register to the sip server
   * sipUserPwd: the account password of the peer to register to the sip server
   * turnServerIp: the ip address of the Turn Server
   * turnServerPort: the port of the Turn Server (default: 3478)
   * turnServerUser: the username used for TurnServer Authentication
   * turnServerPwd: the password of the user used for TurnServer Authentication
   * turnAuthRealm: the realm for the authentication (default:"most.crs4.it")
   * onHoldSound: the path of the sound file played when the call is put on hold status
   * onIncomingCallSound: the path of the sound file played for outcoming calls
   * onOutcomingCallSound: the path of the sound file played for outcoming calls
   :param notificationHandler: the handller that will receive all sip notifications
   :return: true if the initialization request completes without errors, false otherwise

makeCall
^^^^^^^^

.. java:method:: public boolean makeCall(String extension)
   :outertype: VoipLib

   Make a call to the specific extension

   :param extension: The extension to dial
   :return: true if no error occurred during this operation, false otherwise

registerAccount
^^^^^^^^^^^^^^^

.. java:method:: public boolean registerAccount()
   :outertype: VoipLib

   Register the account according to the configuration params provided in the \ :java:ref:`initLib(HashMap,Handler)`\  method

   :return: \ ``true``\  if the registration request was sent to the sip server, \ ``false``\  otherwise

unholdCall
^^^^^^^^^^

.. java:method:: public boolean unholdCall()
   :outertype: VoipLib

   Put the active call on active status

   :return: true if no error occurred during this operation, false otherwise

unregisterAccount
^^^^^^^^^^^^^^^^^

.. java:method:: public boolean unregisterAccount()
   :outertype: VoipLib

   Unregister the currently registered account

   :return: \ ``true``\  if the unregistration request was sent to the sip server, \ ``false``\  otherwise

