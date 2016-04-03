#! /usr/bin/bash

MACRAME_VERSION=$1
MACRAME_PLAY_VERSION=$2

sbt "macrame/doc"
sbt "macrame-play/doc"
rm -rf /tmp/macrame/$MACRAME_VERSION/
rm -rf /tmp/macrame-play/$MACRAME_PLAY_VERSION/
mkdir -p /tmp/macrame/$MACRAME_VERSION/
mkdir -p /tmp/macrame-play/$MACRAME_PLAY_VERSION/
cp -r macrame/target/scala-2.11/api/* /tmp/macrame/$MACRAME_VERSION/
cp -r macrame-play/target/scala-2.11/api/* /tmp/macrame-play/$MACRAME_PLAY_VERSION/

git checkout gh-pages

mkdir -p ./doc/macrame/$MACRAME_VERSION/
mkdir -p ./doc/macrame-play/$MACRAME_PLAY_VERSION/
cp -r /tmp/macrame/$MACRAME_VERSION/ ./doc/macrame/
cp -r /tmp/macrame-play/$MACRAME_PLAY_VERSION/ ./doc/macrame-play/

git add -A
git commit -m 'Update Scaladoc.'
git push

git checkout master
