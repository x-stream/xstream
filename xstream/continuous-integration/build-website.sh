#!/bin/bash

ant clean dist
tar -C build/website -cf - . | ssh beaver tar -C /home/projects/xstream/public_html -xf -
scp build/xstream-SNAPSHOT.zip beaver:/home/projects/xstream/dist/distributions/
scp build/xstream-SNAPSHOT.jar beaver:/home/projects/xstream/dist/jars/

