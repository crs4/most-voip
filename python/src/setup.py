#!/usr/bin/env python
# -*- coding: utf-8 -*-
#
# Project MOST - Moving Outcomes to Standard Telemedicine Practice
# http://most.crs4.it/
#
# Copyright 2014, CRS4 srl. (http://www.crs4.it/)
# Dual licensed under the MIT or GPL Version 2 licenses.
# See license-GPLv2.txt or license-MIT.txt
#
# Permission is hereby granted, free of charge, to any person obtaining a copy of
# this software and associated documentation files (the "Software"), to deal in
# the Software without restriction, including without limitation the rights to
# use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
# the Software, and to permit persons to whom the Software is furnished to do so,
# subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in all
# copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
# FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
# COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
# IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
# CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.


from distutils.core import setup
from distutils.errors import DistutilsSetupError

desc = "MOST-Voip: a fast and lightweight library created for handling VOIP sessions."

long_desc = """
The MOST-Voip Library is a fast and lightweight library created for handling VOIP sessions.
----------------------------------------------------------------------------------

Main features:

 * Sip Account creation and registration on a remote Sip Server (e.g Asterisk)
 * Sip Call handling (making, holding, unholding, answering incoming calls)
 * Buddies Subscription and Real Time Presence Notification
"""


def _get_version():
    try:
        with open('VERSION') as f:
            return f.read().strip()
    except IOError:
        raise DistutilsSetupError("failed to read version info")


setup(
    name='most-voip',
    version=_get_version(),
    description=desc,
    long_description=long_desc,
    author='Stefano Monni, Francesco Cabras',
    author_email='<stefano.monni@crs4.it>, <paneb@crs4.it>',
    url='http://most-voip.readthedocs.org/en/latest/',
    download_url='https://github.com/crs4/most-voip/python/src',
    license='Dual License: (MIT, GPLv2), but please see http://most-voip.readthedocs.org/en/latest/#license',
    keywords=['most', 'voip', 'sip', 'python'],
    classifiers=[
        'License :: OSI Approved :: MIT License, GPLv2',
        'Operating System :: OS Independent',
        'Programming Language :: Python',
        'Intended Audience :: Healthcare Industry',
        'Topic :: Scientific/Engineering :: Medical Science Apps.'
    ],
    packages=['most', 'most.voip'],

)
