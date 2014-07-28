.. java:import:: java.util ArrayList

.. java:import:: java.util HashMap

.. java:import:: android.app Application

.. java:import:: android.content Context

.. java:import:: android.media MediaPlayer

.. java:import:: android.os Handler

.. java:import:: android.os Message

.. java:import:: android.util Log

.. java:import:: most.voip.api.enums AccountState

.. java:import:: most.voip.api.enums BuddyState

.. java:import:: most.voip.api.enums CallState

.. java:import:: most.voip.api.enums RegistrationState

.. java:import:: most.voip.api.enums ServerState

.. java:import:: most.voip.api.enums VoipEvent

.. java:import:: most.voip.api.enums VoipEventType

.. java:import:: most.voip.api.interfaces IAccount

.. java:import:: most.voip.api.interfaces IBuddy

.. java:import:: most.voip.api.interfaces ICall

.. java:import:: most.voip.api.interfaces IServer

VoipLibBackend
==============

.. java:package:: most.voip.api
   :noindex:

.. java:type:: public class VoipLibBackend extends Application implements VoipLib

Fields
------
accList
^^^^^^^

.. java:field:: public ArrayList<MyAccount> accList
   :outertype: VoipLibBackend

ep
^^

.. java:field:: public static Endpoint ep
   :outertype: VoipLibBackend

mediaPlayer
^^^^^^^^^^^

.. java:field::  MediaPlayer mediaPlayer
   :outertype: VoipLibBackend

Constructors
------------
VoipLibBackend
^^^^^^^^^^^^^^

.. java:constructor:: public VoipLibBackend()
   :outertype: VoipLibBackend

Methods
-------
answerCall
^^^^^^^^^^

.. java:method:: @Override public boolean answerCall()
   :outertype: VoipLibBackend

destroyLib
^^^^^^^^^^

.. java:method:: @Override public boolean destroyLib()
   :outertype: VoipLibBackend

getAccount
^^^^^^^^^^

.. java:method:: @Override public IAccount getAccount()
   :outertype: VoipLibBackend

getCall
^^^^^^^

.. java:method:: @Override public ICall getCall()
   :outertype: VoipLibBackend

getServer
^^^^^^^^^

.. java:method:: @Override public IServer getServer()
   :outertype: VoipLibBackend

getSipUriFromExtension
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public String getSipUriFromExtension(String extension)
   :outertype: VoipLibBackend

hangupCall
^^^^^^^^^^

.. java:method:: @Override public boolean hangupCall()
   :outertype: VoipLibBackend

holdCall
^^^^^^^^

.. java:method:: @Override public boolean holdCall()
   :outertype: VoipLibBackend

initLib
^^^^^^^

.. java:method:: @Override public boolean initLib(Context context, HashMap<String, String> configParams, Handler notificationHandler)
   :outertype: VoipLibBackend

makeCall
^^^^^^^^

.. java:method:: @Override public boolean makeCall(String extension)
   :outertype: VoipLibBackend

registerAccount
^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean registerAccount()
   :outertype: VoipLibBackend

unholdCall
^^^^^^^^^^

.. java:method:: @Override public boolean unholdCall()
   :outertype: VoipLibBackend

unregisterAccount
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean unregisterAccount()
   :outertype: VoipLibBackend

