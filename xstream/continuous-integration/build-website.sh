#!/bin/bash

ant clean website
tar -C build/website -cf - . | ssh beaver tar -C /home/projects/xstream/public_html -xf -
