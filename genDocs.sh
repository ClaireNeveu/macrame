#! /usr/bin/bash

PROJECT_NAME=$1
PROJECT_VERSION=$2

if [[ ! $PROJECT_NAME ]] || [[ ! $PROJECT_VERSION ]]
then
    exit 1
fi

sbt "$PROJECT_NAME/doc"
rm -rf /tmp/$PROJECT_NAME/$PROJECT_VERSION/
mkdir -p /tmp/$PROJECT_NAME/$PROJECT_VERSION/
cp -r $PROJECT_NAME/target/scala-2.11/api/* /tmp/$PROJECT_NAME/$PROJECT_VERSION/

git checkout gh-pages

mkdir -p ./doc/$PROJECT_NAME/$PROJECT_VERSION/
cp -r /tmp/$PROJECT_NAME/$PROJECT_VERSION/ ./doc/$PROJECT_NAME/

git add -A
git commit -m "Add Scaladoc for $PROJECT_NAME $PROJECT_VERSION."
git push

git checkout master
