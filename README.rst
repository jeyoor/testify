Introduction
============

Testify is a web applicaton framework designed for personal storying.

Installation
============

There are three dependencies of testify: java, redis, and lein

Your Java version needs to be at least 1.5. I bet you can handle that. Note
that we don't need the JDK: just the JRE is fine. (And if you don't know the
difference, you're fine ;-)

redis is in your distribution's repository. For Ubuntu, that is

  $ sudo apt-get install redis-server

You'll need to install lein, which can be retrieved here:

  https://raw.github.com/technomancy/leiningen/stable/bin/lein

Place the script in your path, and make it executable:

  $ mv lein ~/bin; chmod +x ~/bin/lein

Note: Make sure to add ~/bin to your PATH, perhaps in your .bashrc file:
PATH+=':~/bin'

The first time lein runs, it will gather all of its dependencies, so you don't
have to. 

Usage
=====

Run the following command to start the webserver:

  $ ./run.sh

This also starts redis-server, if that's not already running.

By default, the website is hosted at http://localhost:8000, but if you need to
change the port number, use the PORT environment variable. Note that PORT must
be either a number or unset.

  $ PORT=8000 ./run.sh

License
=======

Copyright (C) 2012 Jeyan Oorjitham 

Distributed under the Eclipse Public License 1.0, the same as Clojure.
