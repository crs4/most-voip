.. Most Voip API documentation master file, created by
   sphinx-quickstart on Tue Jul 15 15:50:41 2014.
   You can adapt this file completely to your liking, but it should at least
   contain the root `toctree` directive.

Examples
========

A tutorial can be found in the `getting started page  <../tutorial/index.html>`_ .
Basic and advanced examples can be found in the `android/examples/` subdirectory of
the MOST-Voip sources. The available examples are the following:

 * *MostVoipActivityFirstExample*: shows how to initialize the Voip Lib and register a Sip Account on a remote Sip Server
 * *MostVoipActivitySecondExample*: shows how to make a call to a remote Sip account
 * *MostVoipActivityAnswerCallExample*: shows how to answer a call incoming from a remote Sip account
 * *MostVoipActivityCallStateExample*: shows how to monitor the state of the remote Sip Server, of the current call and of the remote buddies
 * *MostVoipActivityDemo*: show how to make a call to a remote buddy, answer a call, and monitor the state of the remote Sip Server, of the current call and of the remote buddies
 * *MostVoipActivityRemoteConfigurationExample*: like the previous example, but it also shows how to load the Sip Account Configuration from a remote Web Server



How to build and run the examples
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

First of all, download the Most-voip Asterisk VM,  containing a running Asterisk Server instance  already configured for running the proposed android examples, as explained `here <../../asterisk_docs/vm_asterisk_installation.html>`_ .

Then, do the following:

 * Open your preferred IDE and import the Android Most Voip library project from the *android/src/AndroidVoipLib* folder (if you are using Eclipse, select *File/Import.../Android/Existing Android Code Into Workspace* to import the project)
 * Add to the project the dependence *android-support-v4.jar* . Please, visit `this site <https://developer.android.com/tools/support-library/setup.html>`_ to get detailed instructions about how to do it.
 * Import your preferred example project (e.g MostVoipActivityFirstExample) located in the *android/examples* folder in the same way you have imported the Android Most Voip library project
 * Set the AndroidVoipLib library project (previously added to the workspace) as a Project Reference of the example project imported at the previous step
 * Add to the example project the dependence 'android-support-v4.jar' , in the same way you have done for the AndroidVoipLib library project
 * Build the example project and deploy the generated .apk on your android emulator or mobile phone

