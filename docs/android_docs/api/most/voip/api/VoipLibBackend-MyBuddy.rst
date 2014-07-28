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

VoipLibBackend.MyBuddy
======================

.. java:package:: most.voip.api
   :noindex:

.. java:type::  class MyBuddy extends Buddy implements IBuddy
   :outertype: VoipLibBackend

Fields
------
cfg
^^^

.. java:field:: public BuddyConfig cfg
   :outertype: VoipLibBackend.MyBuddy

Constructors
------------
MyBuddy
^^^^^^^

.. java:constructor::  MyBuddy(BuddyConfig config)
   :outertype: VoipLibBackend.MyBuddy

Methods
-------
getExtension
^^^^^^^^^^^^

.. java:method:: @Override public String getExtension()
   :outertype: VoipLibBackend.MyBuddy

getState
^^^^^^^^

.. java:method:: @Override public BuddyState getState()
   :outertype: VoipLibBackend.MyBuddy

getStatusText
^^^^^^^^^^^^^

.. java:method:: @Override public String getStatusText()
   :outertype: VoipLibBackend.MyBuddy

getUri
^^^^^^

.. java:method:: @Override public String getUri()
   :outertype: VoipLibBackend.MyBuddy

onBuddyState
^^^^^^^^^^^^

.. java:method:: @Override public void onBuddyState()
   :outertype: VoipLibBackend.MyBuddy

refreshStatus
^^^^^^^^^^^^^

.. java:method:: @Override public void refreshStatus()
   :outertype: VoipLibBackend.MyBuddy

updateBuddyStatus
^^^^^^^^^^^^^^^^^

.. java:method::  void updateBuddyStatus()
   :outertype: VoipLibBackend.MyBuddy

