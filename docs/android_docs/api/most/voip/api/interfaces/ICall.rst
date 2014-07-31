.. java:import:: most.voip.api.enums CallState

ICall
=====

.. java:package:: most.voip.api.interfaces
   :noindex:

.. java:type:: public interface ICall

   Contains informations about a call between 2 sip accounts.

Methods
-------
getLocalUri
^^^^^^^^^^^

.. java:method::  String getLocalUri()
   :outertype: ICall

   get the uri of the local sip account

   :return: the uri of the local sip account

getRemoteUri
^^^^^^^^^^^^

.. java:method::  String getRemoteUri()
   :outertype: ICall

   get the uri of the remote sip account

   :return: the uri of the remote sip account

getState
^^^^^^^^

.. java:method::  CallState getState()
   :outertype: ICall

   get the current state of this call

   :return: the current state of this call

