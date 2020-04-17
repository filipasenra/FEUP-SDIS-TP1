#! /usr/bin/bash

# Basic compilation script
# To be executed in the root of the package (source code) hierarchy
# Assumes a package structure with only two directory levels
# Compiled code is placed under ./build/
# Modify it if needed to suite your purpose

export PATH_TO_FX=javajx-sdk-11.0.2/lib
javac --module-path $PATH_TO_FX --add-modules javafx.controls -d build com/assigment_1/Protocol/*.java
javac --module-path $PATH_TO_FX --add-modules javafx.controls -d build com/assigment_1/*.java


# If you are using jar files, and these must be in some particular
#  place under the build tree, you should copy/move those jar files.
