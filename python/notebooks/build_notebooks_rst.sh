#!/bin/bash
echo Creating rst version of notebook examples ...
ipython nbconvert --to rst voip_example_1.ipynb
ipython nbconvert --to rst voip_example_2.ipynb
ipython nbconvert --to asterisk_configuration.ipynb
echo Done.