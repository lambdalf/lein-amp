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
  (:require [leiningen.amp.install :as install])
  (:import [org.eclipse.jetty.server   Server]
           [org.eclipse.jetty.webapp   WebAppContext]
           [org.eclipse.jetty.security HashLoginService]
           [org.eclipse.jetty.util.security Password]
           [org.eclipse.jetty.plus.jndi Resource]
           [org.h2.jdbcx JdbcDataSource]))

(defn- configure-security
  "Sets up a login handler which is required by Alfresco to properly start up"
  [^Server server]
  (let [login-svc (doto (HashLoginService.) ; TODO make it configurable via project.clj
                        (.setName   "Repository")
                        (.setConfig "realm.properties")
                        (.setRefreshInterval 0)
                        (.putUser "alfresco" (Password. "alfresco") nil))]
    (-> server (.addBean login-svc))))

(defn- add-h2-datasource
  "Configures a data source that uses an in-memory H2 database"
  [server]
  (let [jndi-name "jdbc/dataSource"
        ds     (doto (JdbcDataSource.)
                     (.setURL "jdbc:h2:mem:alfresco?MVCC=true")
                     (.setUser "alfresco")
                     (.setPassword "alfresco"))
        ds-res (doto (Resource. nil jndi-name ds))]
    (-> server (.setAttribute "datasource" ds-res))))

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
        wac (doto (WebAppContext.)
                  (.setContextPath "/")
                  (.setWar (str war)))
        server (doto (Server. 8080)
                     (.setHandler wac)
                     configure-security
                     add-h2-datasource
                     (.start)
                     (.join))]))
