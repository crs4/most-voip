.. java:import:: most.voip.api.enums BuddyState

IBuddy
======

.. java:package:: most.voip.api.interfaces
   :noindex:

.. java:type:: public interface IBuddy

   An IBuddy is a remote Sip user that notify its presence status to sip accounts (\ :java:ref:`IAccount`\  objects) that are interested to get informations by them.

Methods
-------
getExtension
^^^^^^^^^^^^

.. java:method::  String getExtension()
   :outertype: IBuddy

   get the sip extension of this buddy

   :return: the sip extension of this buddy

getState
^^^^^^^^

.. java:method::  BuddyState getState()
   :outertype: IBuddy

   get the current state of this buddy

   :return: the current state of this buddy

   **See also:** :java:ref:`IBuddy.refreshStatus()`

getStatusText
^^^^^^^^^^^^^

.. java:method::  String getStatusText()
   :outertype: IBuddy

   get a textual description of the current status of this buddy

   :return: a textual description of the current status of this buddy

getUri
^^^^^^

.. java:method::  String getUri()
   :outertype: IBuddy

   get the sip uri of this buddy

   :return: the sip uri of this buddy

refreshStatus
^^^^^^^^^^^^^

.. java:method::  void refreshStatus()
   :outertype: IBuddy

   Refreshes the current status of this buddy

