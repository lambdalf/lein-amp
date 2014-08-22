# lein-amp
Generates an [Alfresco Module Package (AMP) file](http://wiki.alfresco.com/wiki/AMP_Files) from your project, which
can be deployed to an Alfresco installation using the
[Module Management Tool](http://wiki.alfresco.com/wiki/Module_Management_Tool).

What is it useful for?

AMPs are the standard way to package extensions to the [open source Alfresco content management system](http://www.alfresco.org/).
Being able to develop such extensions in Clojure, therefore, requires the ability for leiningen to emit AMP Files - this plugin adds
that capability.

## Installation

It is highly recommended that you generate new AMP projects using the
[`lein new` AMP template](https://github.com/lambdalf/amp-template). Amongst other things, this will automatically add
the lein-amp plugin to the new project's dependencies.

```shell
$ lein new amp <projectName>
$ cd <projectName>
$ lein deps
```

## Usage
```shell
$ lein amp package
$ lein amp install [<path-to-alfresco-war>]
```

The `package` task builds an AMP file from your project.
The `install` task will install that AMP into an Alfresco WAR file.

## Configuration

The following configuration options are read from your `project.clj`:

```clojure
; where to look for AMP resources
:amp-source-path "src/amp"

; specify the location to the Alfresco WAR file
:amp-target-war "path/to/alfresco.war"
; or a dependency to the WAR (remember to configure the proper Maven repository)
:amp-target-war [org.alfresco/alfresco "5.0.a" :extension "war"]
```

## Developer Information

[GitHub project](https://github.com/lambdalf/lein-amp)

[Bug Tracker](https://github.com/lambdalf/lein-amp/issues)

[![endorse](https://api.coderwall.com/pmonks/endorsecount.png)](https://coderwall.com/pmonks)

## License

Copyright © 2014 Peter Monks (pmonks@gmail.com)

Distributed under the [Eclipse Public License](http://www.eclipse.org/legal/epl-v10.html) either version 1.0 or (at your option) any later version.
