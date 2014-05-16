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
  (:require [clojure.java.io :as io]))

(defn package-amp!
  [project args]
  (let [target   (io/file (:target-path project))
        amp-file (io/file target
                          (or (get-in project [:amp :name])
                              (str (:name project) "-" (:version project) ".amp")))]
    (println "#### packaging AMP" (.getName ^java.io.File amp-file))

  ))



(defn deploy-amp!
  [project args]
  (comment "####TODO: NOT YET IMPLEMENTED"))
