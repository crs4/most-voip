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

VoipLibBackend.MyAccount
========================

.. java:package:: most.voip.api
   :noindex:

.. java:type::  class MyAccount extends Account implements IAccount
   :outertype: VoipLibBackend

Fields
------
buddyList
^^^^^^^^^

.. java:field:: public HashMap<String, MyBuddy> buddyList
   :outertype: VoipLibBackend.MyAccount

cfg
^^^

.. java:field:: public AccountConfig cfg
   :outertype: VoipLibBackend.MyAccount

Constructors
------------
MyAccount
^^^^^^^^^

.. java:constructor::  MyAccount(AccountConfig config)
   :outertype: VoipLibBackend.MyAccount

Methods
-------
addBuddy
^^^^^^^^

.. java:method:: @Override public boolean addBuddy(String buddyUri)
   :outertype: VoipLibBackend.MyAccount

addBuddy
^^^^^^^^

.. java:method:: public MyBuddy addBuddy(BuddyConfig bud_cfg)
   :outertype: VoipLibBackend.MyAccount

   add a buddy to this account , if not already added

   :param bud_cfg:
   :return: the added buddy, null idf the buddy was previuosly added or an error occurred

delBuddy
^^^^^^^^

.. java:method:: public MyBuddy delBuddy(String uri)
   :outertype: VoipLibBackend.MyAccount

   delete the buddy with the given uri from the account

   :param uri:
   :return: the removed buddy, or null if the buddy to remove was not found.

getBuddies
^^^^^^^^^^

.. java:method:: @Override public IBuddy[] getBuddies()
   :outertype: VoipLibBackend.MyAccount

getBuddy
^^^^^^^^

.. java:method:: @Override public IBuddy getBuddy(String buddyUri)
   :outertype: VoipLibBackend.MyAccount

getState
^^^^^^^^

.. java:method:: @Override public AccountState getState()
   :outertype: VoipLibBackend.MyAccount

getUri
^^^^^^

.. java:method:: @Override public String getUri()
   :outertype: VoipLibBackend.MyAccount

hasBuddy
^^^^^^^^

.. java:method:: public boolean hasBuddy(String uri)
   :outertype: VoipLibBackend.MyAccount

onIncomingCall
^^^^^^^^^^^^^^

.. java:method:: @Override public void onIncomingCall(OnIncomingCallParam prm)
   :outertype: VoipLibBackend.MyAccount

onIncomingSubscribe
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void onIncomingSubscribe(OnIncomingSubscribeParam prm)
   :outertype: VoipLibBackend.MyAccount

onInstantMessage
^^^^^^^^^^^^^^^^

.. java:method:: @Override public void onInstantMessage(OnInstantMessageParam prm)
   :outertype: VoipLibBackend.MyAccount

onRegState
^^^^^^^^^^

.. java:method:: @Override public void onRegState(OnRegStateParam prm)
   :outertype: VoipLibBackend.MyAccount

removeBuddy
^^^^^^^^^^^

.. java:method:: @Override public boolean removeBuddy(String buddyUri)
   :outertype: VoipLibBackend.MyAccount

