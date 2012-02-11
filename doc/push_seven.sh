#!/bin/sh
#Run this script from a git working directory to create a clone on an ssh host.
REMOTE_NAME="l7"
BARE_DIR="/mnt/home/jeyan/Documents/praj/gitmount/testify.git/"
REMOTE_DIR="jeyv0rz@lxsrv7.oru.edu:/home/jeyv0rz/praj/gitmount/"

#if the bare clone does not exist, create it
if [ ! -d "$BARE_DIR" ]; then
    git clone --local --bare . $BARE_DIR
    git remote add $REMOTE_NAME $BARE_DIR
fi

#push to the bare clone
git push $REMOTE_NAME master
#give overwrite permissions to self
chmod -R u+rw $BARE_DIR
cd $BARE_DIR
#update the bare dir with latest info
git update-server-info
#czav = compressed, archive, verbose, -e execute (ssh)
rsync -czav -e ssh $REMOTE_DIR $BARE_DIR