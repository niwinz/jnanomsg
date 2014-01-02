#!/bin/sh
(cd docs; make)
cp -vr docs/index.html /tmp/index.html;
git checkout gh-pages;
rm -rf *
mv -fv /tmp/index.html .
git add .
git commit -a -m "Update doc"
