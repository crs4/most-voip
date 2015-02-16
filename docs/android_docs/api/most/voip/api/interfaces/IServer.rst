.. java:import:: most.voip.api.enums ServerState

IServer
=======

.. java:package:: most.voip.api.interfaces
   :noindex:

.. java:type:: public interface IServer

   Contains informations about the remote Sip Server (e.g Asterisk)

Methods
-------
getIp
^^^^^

.. java:method::  String getIp()
   :outertype: IServer

   get the ip address of the remote sip server

   :return: the ip address of the remote sip server

getPort
^^^^^^^

.. java:method::  String getPort()
   :outertype: IServer

   get the port of the remote sip server

   :return: the ip address of the remote sip server

getState
^^^^^^^^

.. java:method::  ServerState getState()
   :outertype: IServer

   get the current status of the sip server (see :class:`most.voip.constants.ServerState`)

   :return: the current status of the sip server

