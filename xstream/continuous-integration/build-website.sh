#!/bin/bash

ant clean website
tar -xvz -C /home/projects/xstream/public_html -f build/xstream-website.tgz
