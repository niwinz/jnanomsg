#!/bin/sh
VERSION="latest"

(cd doc; make)
lein doc
lein javadoc

rm -rf /tmp/nanomsg-doc/
mkdir -p /tmp/nanomsg-doc/

mv doc/index.html /tmp/nanomsg-doc/
mv doc/api /tmp/nanomsg-doc/

git checkout gh-pages;

rm -rf ./$VERSION
mv /tmp/nanomsg-doc/ ./$VERSION

git add --all ./$VERSION
git commit -a -m "Update ${VERSION} doc"
