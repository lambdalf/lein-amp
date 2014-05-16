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
  (let [target       (io/file (:target-path project))
        amp-file     (io/file target
                              (or (get-in project [:amp :name])
                                  (str (:name project) "-" (:version project) ".amp")))
        _            (println "#### STEP 1: Construct uberjar...")
        uberjar-file (uj/uberjar project)
        _            (println "#### uberjar =" uberjar-file)]

    (println "#### STEP 2: Create AMP directory structure...")
    (io/make-parents (io/file target "amp/config/."))   ; Note: the final "." is needed as make-parents doesn't create the final path element as a directory
    (io/make-parents (io/file target "amp/lib/."))
    (io/make-parents (io/file target "amp/licenses/."))
    (io/make-parents (io/file target "amp/web/."))

    (println "#### STEP 3: Populate AMP directory structure...")
    ;####TODO!!!!

    (println "#### STEP 4: Create AMP..." amp-file)
    ;####TODO!!!!
  ))



(defn deploy-amp!
  [project args]
  (comment "####TODO: NOT YET IMPLEMENTED"))
