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

VoipLibBackend.MyAccountConfig
==============================

.. java:package:: most.voip.api
   :noindex:

.. java:type::  class MyAccountConfig
   :outertype: VoipLibBackend

Fields
------
accCfg
^^^^^^

.. java:field:: public AccountConfig accCfg
   :outertype: VoipLibBackend.MyAccountConfig

buddyCfgs
^^^^^^^^^

.. java:field:: public ArrayList<BuddyConfig> buddyCfgs
   :outertype: VoipLibBackend.MyAccountConfig

Methods
-------
readObject
^^^^^^^^^^

.. java:method:: public void readObject(ContainerNode node)
   :outertype: VoipLibBackend.MyAccountConfig

writeObject
^^^^^^^^^^^

.. java:method:: public void writeObject(ContainerNode node)
   :outertype: VoipLibBackend.MyAccountConfig

