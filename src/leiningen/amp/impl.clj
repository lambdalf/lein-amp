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

(defn package-amp!
  [project args]
  (let [target   (io/file (:target-path project))
        amp-file (io/file target
                          (or (get-in project [:amp :name])
                              (str (:name project) "-" (:version project) ".amp")))]
    ; Step 1: construct a standard uberjar
    (println "Constructing uberjar...")
    (uj/uberjar project)

    ; Step 2: construct AMP structure on disk
    (println "#### creating AMP directory structure")

    ; Step 3: populate AMP structure
    (println "#### populating AMP directory structure")

    ; Step 4: zip AMP structure up into an AMP file
    (println "#### creating AMP" (.getName ^java.io.File amp-file))
  ))



(defn deploy-amp!
  [project args]
  (comment "####TODO: NOT YET IMPLEMENTED"))
