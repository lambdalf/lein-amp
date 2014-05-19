# lein-amp
Generates an [Alfresco Module Package (AMP) file](http://wiki.alfresco.com/wiki/AMP_Files) from your project, which
can be deployed to an Alfresco installation using the
[Module Management Tool](http://wiki.alfresco.com/wiki/Module_Management_Tool).

What is it useful for?

AMPs are the standard way to deploy extensions to the [open source Alfresco content management system](http://www.alfresco.org/).
Being able to develop such extensions in Clojure, therefore, requires the ability for leiningen to emit AMP Files - this plugin adds
that capability.

## Installation

It is highly recommended that you generate new AMP projects using the
[`lein new` AMP template](https://github.com/mstang/alfresco-amp-template). Amongst other things, this will automatically add
the lein-amp plugin to the new project's dependencies.

```shell
$ lein new amp <projectName>
$ cd <projectName>
```

## Usage:
```shell
$ lein amp package
$ lein amp deploy [<path-to-alfresco>]   # Not implemented yet!
```

The `package` task builds an AMP file from your project.
The `deploy` task (not yet implemented) will deploy that AMP to an Alfresco installation.

## Developer Information

[GitHub project](https://github.com/pmonks/lein-amp)

[Bug Tracker](https://github.com/pmonks/lein-amp/issues)

[![endorse](https://api.coderwall.com/pmonks/endorsecount.png)](https://coderwall.com/pmonks)

## License

Copyright © 2014 Peter Monks (pmonks@gmail.com)

Distributed under the [Eclipse Public License](http://www.eclipse.org/legal/epl-v10.html) either version 1.0 or (at your option) any later version.

