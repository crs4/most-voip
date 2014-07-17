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

VoipLibBackend.MyCall
=====================

.. java:package:: most.voip.api
   :noindex:

.. java:type::  class MyCall extends Call
   :outertype: VoipLibBackend

Constructors
------------
MyCall
^^^^^^

.. java:constructor::  MyCall(MyAccount acc, int call_id)
   :outertype: VoipLibBackend.MyCall

Methods
-------
onCallMediaState
^^^^^^^^^^^^^^^^

.. java:method:: @Override public void onCallMediaState(OnCallMediaStateParam prm)
   :outertype: VoipLibBackend.MyCall

onCallState
^^^^^^^^^^^

.. java:method:: @Override public void onCallState(OnCallStateParam prm)
   :outertype: VoipLibBackend.MyCall

