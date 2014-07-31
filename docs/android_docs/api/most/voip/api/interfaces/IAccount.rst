.. java:import:: most.voip.api.enums AccountState

IAccount
========

.. java:package:: most.voip.api.interfaces
   :noindex:

.. java:type:: public interface IAccount

   Represents a local sip account

Methods
-------
addBuddy
^^^^^^^^

.. java:method:: public boolean addBuddy(String uri)
   :outertype: IAccount

   Add a buddy to this account.

   :param uri: the buddy sip uri
   :return: True if the buddy was added to the buddy list, False otherwise

getBuddies
^^^^^^^^^^

.. java:method:: public IBuddy[] getBuddies()
   :outertype: IAccount

   Get the list of buddies of the current registered account

   :return: the list of the buddies of the currently registered account

getBuddy
^^^^^^^^

.. java:method:: public IBuddy getBuddy(String uri)
   :outertype: IAccount

   Get the buddy with the given extension, or null if it is not found

   :param uri: the buddy uri
   :return: the buddy with the provided uri, or null if it is not found

getState
^^^^^^^^

.. java:method:: public AccountState getState()
   :outertype: IAccount

   Get the current state of this account

   :return: the current state of this account

getUri
^^^^^^

.. java:method:: public String getUri()
   :outertype: IAccount

   Get the uri of this sip account

   :return: the sip uri of this account

removeBuddy
^^^^^^^^^^^

.. java:method:: public boolean removeBuddy(String uri)
   :outertype: IAccount

   Remove the buddy from this account

   :param uri: The sip uri of the buddy to remove
   :return: True if the buddy was found and it was successfully removed, False otherwise

