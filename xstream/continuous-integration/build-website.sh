#!/bin/bash

ant clean website
tar cf - build/website | ssh beaver tar -C /home/projects/xstream/public_html -xf -
