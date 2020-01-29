#!/bin/bash

java -cp build/jar/httpd.jar:lib/ini4j-0.5.4.jar ServerMain $@
