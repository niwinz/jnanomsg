#!/bin/sh
(cd docs; make html;)
lein javadoc;
cp -vr docs/_build /tmp/docs;
git checkout gh-pages;
rm -rf *
mv -fv /tmp/docs/_build/html/* ./
mv -fv /tmp/docs/_build/javadoc ./
git add .
git commit -a -m "Update doc"
