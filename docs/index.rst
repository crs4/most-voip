.. Most Voip API documentation master file, created by
   sphinx-quickstart on Tue Jul 15 15:50:41 2014.
   You can adapt this file completely to your liking, but it should at least
   contain the root `toctree` directive.

Most Voip Library
=================


The *MOST-Voip* Library is a fast and lightweight library created for handling VOIP sessions.


Main features:

  * Sip Account creation and registration on a remote Sip Server (e.g Asterisk)
  * Sip Call handling (making, holding, unholding, answering incoming calls)
  * Buddies Subscription and Real Time Presence Notification
 
Supported platforms:

 * Mobile: Android
 * Desktop: Linux Ubuntu

So far, MOST-Voip for desktop platforms has been tested only on Linux Ubuntu v.14.04 distribution. However, it is written in Python 2.7, 
so other platforms should be supported as well.

Table Of Contents
=================

.. toctree::
   :maxdepth: 3
   

   index
   python_docs/index
   android_docs/index
   authors
    

Installation
============

Most-Voip Library is based on `PJSIP 2.2.1 <http://www.pjsip.org/>`_ library.
So, first of all, you have to install PJSip, by performing the following steps:

1. Download the last svn revision from http://svn.pjsip.org/repos/pjproject/trunk/ (revision 4818 works well). (tar.gz and zip archives don't compile!)
2. ./configure CFLAGS='-fPIC'
3. make dep
4. make
5. sudo make install
6. cd pjsip-apps/src/python/
7. sudo python setup.py install

If you intend to use Most-Voip on the Android platform, you also have to build Pjsip for Android, as explained `here <https://trac.pjsip.org/repos/wiki/Getting-Started/Android#Requirements>`_

Get the latest release from GitHub: `https://github.com/crs4/most-voip <https://github.com/crs4/most-voip>`_


License
=======

::

     Project MOST - Moving Outcomes to Standard Telemedicine Practice
     http://most.crs4.it/
    
     Copyright 2014, CRS4 srl. (http://www.crs4.it/)
     
     Dual licensed under the MIT or GPL Version 2 licenses.
     See license-GPLv2.txt or license-MIT.txt

GPL2: https://www.gnu.org/licenses/gpl-2.0.txt

MIT: http://opensource.org/licenses/MIT


Detailed Dual Licensing Info
============================

The MOST-Voip API is licensed under both General Public License (GPL) version 2 and the MIT licence. In practical sense, this means:

    * if you are developing Open Source Software (OSS) based on MOST-Voip, chances are you will be able to use MOST-Voip under GPL. Note that the Most-Voip Library depends on the PJSIP API, so  please double check `here <http://www.pjsip.org/licensing.htm>`_ for OSS license compatibility with GPL.
    * alternatively, you can release your application under MIT licence, provided that you have followed the guidelines of the PJSIP licence explained `here <http://www.pjsip.org/licensing.htm>`_.


 
Indices and tables
==================

* :ref:`genindex`
* :ref:`modindex`
* :ref:`search`

