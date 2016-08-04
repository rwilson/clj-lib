**[API docs](http://rwilson.github.io/clj-lib/)**

[![Clojars Project](http://clojars.org/rwilson/clj-lib/latest-version.svg)](http://clojars.org/rwilson/clj-lib)

# clj-lib

A library of useful clojure utilities.

## Usage

To generate documentation, using codox:
```
$ lein codox
```

To install locally:
```
$ lein build-install
```

## Changelog

**0.8.0**
 * Added core/deep-merge and core/deep-merge-with like so many other libs

**0.7.0**
* Added core/clamp
* Added transducer producing arity of core/round

**0.6.0**
* Added io/last-modified 

**0.5.0**
* Added core/defdata macro

**0.4.0**
* Added core/quote-string
* Added core/escape-quotes

**0.3.0**
* Added core/maybe?
* Added core/not-neg?
* Added core/str=

**0.2.1**
* Bug fix: core/thread macro did not return thread, as docs stated 

**0.2.0** 
* Added core/thread macro 
* Added io/with-in and io/with-out macros

## License

Copyright © 2015 Ryan Wilson

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
