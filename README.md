# lein-amp
Generates an Alfresco Module Package (AMP) file from your project.
See http://wiki.alfresco.com/wiki/AMP_Files for more details on the AMP file format.
See http://wiki.alfresco.com/wiki/Module_Management_Tool for details on how to deploy AMP files to an Alfresco installation.

Also see https://github.com/mstang/alfresco-amp-template for a `lein new` template that will create a skeleton project of this type.

What is it useful for?

AMPs are the standard for deploying extensions to the [open source Alfresco content management system](http://www.alfresco.org/).
Being able to develop such extensions in Clojure, therefore, requires the ability for leiningen to emit AMP Files.  This plugin adds
that capability.

## Installation

lein-amp is available as a Maven artifact from [Clojars](https://clojars.org/org.clojars.pmonks/lein-amp).
Plonk the following in your project.clj, `lein deps` and you should be good to go:

```clojure
  :profiles {:dev      { :plugins [[lein-amp "0.1.0-SNAPSHOT"]] }
             :provided { :dependencies [
                                         [org.clojure/clojure         "1.6.0"          :scope "runtime"]
;                                         [org.clojars.pmonks/lambdalf "2.0.0-SNAPSHOT" :scope "runtime"]  ; SOON - NOT YET DEPLOYED TO CLOJARS!
                                       ]}
```

## Usage:
```shell
$ lein amp package [<options>]
$ lein amp deploy [<options>] [<path-to-alfresco>]   # Not implemented yet!
```

####TODO
####TODO
####TODO: FINISH THIS OFF!!!!!!!!!!
####TODO
####TODO

   Tasks:

     package    Compile and package the AMP file.
     deploy     Deploy the AMP file to the specified Alfresco installation (NOT YET IMPLEMENTED).

   Commandline Options:

     <none>


The latest version is:

[![version](https://clojars.org/org.clojars.pmonks/lein-amp/latest-version.svg)](https://clojars.org/org.clojars.pmonks/lein-amp)

## Usage

```shell
$ lein amp
```

## Developer Information

[GitHub project](https://github.com/pmonks/lein-amp)

[Bug Tracker](https://github.com/pmonks/lein-amp/issues)

[![endorse](https://api.coderwall.com/pmonks/endorsecount.png)](https://coderwall.com/pmonks)

## License

Copyright © 2014 Peter Monks (pmonks@gmail.com)

Distributed under the [Eclipse Public License](http://www.eclipse.org/legal/epl-v10.html) either version 1.0 or (at your option) any later version.

