;
; Copyright © 2014 Peter Monks (pmonks@gmail.com)
;
; All rights reserved. This program and the accompanying materials
; are made available under the terms of the Eclipse Public License v1.0
; which accompanies this distribution, and is available at
; http://www.eclipse.org/legal/epl-v10.html
;
; Contributors:
;    Carlo Sciolla - initial implementation

(ns leiningen.amp.run-jetty
  (:require [leiningen.amp.install :as install]
            [clojure.string        :as s]
            [stencil.core          :refer [render-file]]
            [stencil.loader        :refer [set-cache]])
  (:import [org.eclipse.jetty.runner Runner]))

(defn- temp-jetty-xml
  "Creates a jetty.xml in a temporary location and returns the file handle"
  [project] ; TODO: read options from the project!
  (let [defaults {:jdbc-url    "jdbc:h2:target/h2_data/lambdalf;MVCC=true"
                  :username    "alfresco"
                  :password    "alfresco"
                  :realm-props "realm.properties"
                  :refresh     0
                  :users       [{:uid "alfresco"
                                 :pwd "alfresco"}]}
        _ (set-cache {}) ; needed as core.cache creates classpath hell
        jetty-xml (render-file "jetty" defaults)
        file (java.io.File/createTempFile "jetty" ".xml")]
    (spit file jetty-xml)
    file))

(defn- lib
  "Retrieves a file handle of the requested maven dependency"
  [dep]
  (str (System/getProperty "user.home")
       (s/replace dep "/" java.io.File/separator)))

(defn run-amp-jetty!
  "Start a Jetty webserver to serve the given handler according to the
  supplied options: (not yet implemented)

  :port           - the port to listen on (defaults to 80)
  :host           - the hostname to listen on
  :join?          - blocks the thread until server ends (defaults to true)
  :daemon?        - use daemon threads (defaults to false)
  :max-threads    - the maximum number of threads to use (default 50)
  :min-threads    - the minimum number of threads to use (default 8)
  :max-queued     - the maximum number of requests to queue (default unbounded)
  :max-idle-time  - the maximum idle time in milliseconds for a connection (default 200000)"
  [project args]
  (let [war (install/locate-war project args)
        xml (temp-jetty-xml project)]
    (Runner/main (into-array String
                             ["--lib" (lib "/.m2/repository/com/h2database/h2/1.4.181")
                              "--lib" (lib "/.m2/repository/tk/skuro/alfresco/h2-support/1.6")
                              "--config" (str xml)
                              (str war)]))))
