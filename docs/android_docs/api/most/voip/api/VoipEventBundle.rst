.. java:import:: most.voip.api.enums VoipEvent

.. java:import:: most.voip.api.enums VoipEventType

VoipEventBundle
===============

.. java:package:: most.voip.api
   :noindex:

.. java:type:: public class VoipEventBundle

Constructors
------------
VoipEventBundle
^^^^^^^^^^^^^^^

.. java:constructor:: public VoipEventBundle(VoipEventType eventType, VoipEvent event, String info, Object data)
   :outertype: VoipEventBundle

   This object contains all the informations of a Sip Event triggered by the Voip Library

   :param eventType: the type of this event
   :param event: the event
   :param info: a textual information describing this event
   :param data: a generic object containing event-specific informations (the object type depends on the type of the event)

Methods
-------
getData
^^^^^^^

.. java:method:: public Object getData()
   :outertype: VoipEventBundle

   Get a generic object containing event-specific informations (the object type depends on the type of the event)

   :return: a generic object containing event-specific informations

getEvent
^^^^^^^^

.. java:method:: public VoipEvent getEvent()
   :outertype: VoipEventBundle

getEventType
^^^^^^^^^^^^

.. java:method:: public VoipEventType getEventType()
   :outertype: VoipEventBundle

   Get the event type

   :return: the event type

getInfo
^^^^^^^

.. java:method:: public String getInfo()
   :outertype: VoipEventBundle

   Get a textual description of this event

   :return: a textual description of this event

