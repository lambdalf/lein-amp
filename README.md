# lein-amp
A leiningen plugin for generating Alfresco Module Package (AMP) files.

What is it useful for?

AMPs are the standard for deploying extensions to the open source Alfresco content
management system.  Being able to develop such extensions in Clojure, therefore,
requires the ability for leiningen to emit AMP Files.

## Installation

lein-amp-plugin is (NOT YET!) available as a Maven artifact from
[Clojars](https://clojars.org/org.clojars.pmonks/lein-amp).
Plonk the following in your project.clj :plugins, `lein deps` and you should be good to go:

```clojure
[org.clojars.pmonks/lein-amp "#.#.#"]
```

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

