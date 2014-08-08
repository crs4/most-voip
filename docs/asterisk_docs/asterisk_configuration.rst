
Asterisk Configuration Guide for Most Voip Examples
===================================================

All examples describing the Most Voip Library features require, to work
properly, a Sip Server running on a reachable PC. In this guide we show
how to configure the `Asterisk Sip Server <http://www.asterisk.org/>`__

Alternatively, if you prefer, you can install on your pc the Asterisk Virtual Machine (that contains an already configured Asterisk instance, as explained `here. <./vm_asterisk_installation.html>`_ 

How to add Sip Users to Asterisk
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Open the **sip.conf** configuration file (generally located in the
folder /etc/asterisk) set to **yes** the following options in the
**[general]** section:


.. code-block:: python

	[general]
	callevents=yes 
	notifyhold = yes
	callcounter=yes

Also, add these sections at the end of ** sip.conf **:

.. code-block:: python

	[ste]
	type=friend
	secret=ste
	host=dynamic
	context=local_test

	[steand]
	type=friend
	secret=steand
	host=dynamic
	context=local_test

How to add extensions to dial in Asterisk
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Open the **extensions.conf** configuration file (generally located in
the folder /etc/asterisk) and add these lines at the end:

.. code-block:: python

	[local_test]
	exten => 1234,1,Answer ; answer the call
	exten => 1234,2,Playback(tt-weasels) ; play an audio file that simulates the voice of the called user
	exten => 1234,3,Hangup ; hang up the call
	
	exten => ste,1,Set(VOLUME(RX)=10) ; set the RX volume 
	exten => ste,2,Set(VOLUME(TX)=10) ; set the RX volume 
	exten => ste,hint,SIP/ste; hint  'ste' used for presence notification 
	exten => ste,3,Dial(SIP/ste) ; call the user ste' 
	
	
	exten => steand,1,Set(VOLUME(RX)=10) ; set the RX volume  
	exten => steand,2,Set(VOLUME(TX)=10) ; set the RX volume  
	exten => steand,hint,SIP/ste; hint  'steand' used for presence notification 
	exten => steand,3,Dial(SIP/steand) call the user 'steand' used for presence notification

How to run Asterisk
~~~~~~~~~~~~~~~~~~~

Open a shell and type the following command:

.. code-block:: python

	sudo service asterisk restart

How to open the Asterisk Command Line Interface (CLI) Shell
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: python

	sudo asterisk -r

How to look for sip users current state:
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: python

	sip show peer 

How to reload the dialplan (useful when you add and/or modify a new extension):
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: python

	dialplan reload

How to originate a call:
~~~~~~~~~~~~~~~~~~~~~~~~

This following command originates a call from the sip server to the user
'ste'. Obviously, it assumes that you have configured the Asterisk
Server so that the user 'ste' is a known sip user. To do it , you have
to configure the sip configuration file, called **sip.conf** (in Linux
platforms, it is generally located in the folder /etc/asterisk).

.. code-block:: python

	originate SIP/ste extension
