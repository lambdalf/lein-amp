;
; Copyright © 2014 Peter Monks (pmonks@gmail.com)
;
; All rights reserved. This program and the accompanying materials
; are made available under the terms of the Eclipse Public License v1.0
; which accompanies this distribution, and is available at
; http://www.eclipse.org/legal/epl-v10.html
;
; Contributors:
;    Peter Monks - initial implementation

(ns leiningen.amp.impl
  (:require [clojure.java.io   :as io]
            [leiningen.uberjar :as uj]))

(def ^:private default-web-resources "web-resources")
(def ^:private default-amp-resources "amp-resources")

(defn package-amp!
  [project args]
  (let [project-home       (io/file (:root project))
        web-resources      (io/file project-home (:web-resource-path project default-web-resources))
        amp-resources-name (:amp-resource-path project default-amp-resources)
        module-properties  (io/file project-home (str amp-resources-name "/module.properties"))
        file-mappings      (io/file project-home (str amp-resources-name "/file-mapping.properties"))
        target             (io/file (:target-path project))
        amp-file           (io/file target
                                    (or (get-in project [:amp :name])
                                        (str (:name project) "-" (:version project) ".amp")))
        _                  (println "#### STEP 1: Construct uberjar...")
        uberjar-file       (io/file (uj/uberjar project))
        _                  (println "#### uberjar =" uberjar-file)
        amp                (io/file target "amp/.")
        amp-config         (io/file target "amp/config/.")
        amp-lib            (io/file target "amp/lib/.")
        amp-licenses       (io/file target "amp/licenses/.")
        amp-web            (io/file target "amp/web/.")]

    (println "#### STEP 2: Create AMP directory structure...")
    (io/make-parents amp)
    (io/make-parents amp-config)
    (io/make-parents amp-lib)
    (io/make-parents amp-licenses)
    (io/make-parents amp-web)

    (println "#### STEP 3: Populate AMP directory structure...")
    (if (.exists ^java.io.File module-properties)
      (io/copy module-properties (io/file amp (.getName ^java.io.File module-properties)))
      (throw (RuntimeException. "Module doesn't have a module.properties file.")))    ;####TODO: Make this more pleasant

    (if (.exists ^java.io.File file-mappings)
      (io/copy file-mappings (io/file amp (.getName ^java.io.File file-mappings))))

    (if (.exists ^java.io.File uberjar-file)
      (io/copy uberjar-file (io/file amp-lib (.getName ^java.io.File uberjar-file))))

    ;####TODO!!!!

    (println "#### STEP 4: Create AMP..." amp-file)
    ;####TODO!!!!
  ))



(defn deploy-amp!
  [project args]
  (comment "####TODO: NOT YET IMPLEMENTED"))
