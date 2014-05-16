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
            [leiningen.uberjar :as uj]
            [me.raynes.fs      :as fs]))

(defn- zip-directory!
  "Recursively compress all files in 'directory' into 'zip-file'."
  [zip-file directory]
  (let [files            (file-seq (io/file directory))
        directory-length (inc (.length (str directory)))]
    (if-not (empty? files)
      (with-open [zip-stream-out (java.util.zip.ZipOutputStream. (io/output-stream zip-file))]
        (doseq [file (rest files)]  ; Discard the first file - it's the directory itself
          (.putNextEntry zip-stream-out
                         (java.util.zip.ZipEntry. (.substring (.getPath ^java.io.File file)
                                                              directory-length)))
          (if (.isFile ^java.io.File file)
            (with-open [file-stream-in (io/input-stream file)]
              (io/copy file-stream-in zip-stream-out)))
          (.closeEntry zip-stream-out)))))
  nil)


(defn package-amp!
  [project args]
  (let [project-home       (io/file (:root project))
        web-resources      (io/file project-home "web-resources")
        module-properties  (io/file project-home "amp-resources/module.properties")
        file-mappings      (io/file project-home "amp-resources/file-mapping.properties")
        config             (io/file project-home "config")
        licenses           (io/file project-home "licenses")
        target             (io/file (:target-path project))
        _                  (println "#### STEP 1: Construct uberjar...")
        uberjar-file       (io/file (uj/uberjar project))
        _                  (println "#### uberjar =" uberjar-file)
        amp                (io/file target "amp/.")
        amp-lib            (io/file target "amp/lib/.")
        amp-config         (io/file target "amp/config/.")
        amp-licenses       (io/file target "amp/licenses/.")
        amp-web            (io/file target "amp/web/.")
        amp-file           (io/file target
                                    (or (get-in project [:amp-name])
                                        (str (:name project) "-" (:version project) ".amp")))]

    (println "#### STEP 2: Create AMP directory structure...")
    (io/make-parents amp)
    (io/make-parents amp-lib)

    (println "#### STEP 3: Populate AMP directory structure...")
    (if (.exists ^java.io.File module-properties)
      (io/copy module-properties (io/file amp (.getName ^java.io.File module-properties)))
      (println "#### RUH ROH RAGGY : no module.properties!!!!1"))
;      (throw (RuntimeException. "Module doesn't have a module.properties file, so it's invalid.")))    ;####TODO: Make this more pleasant

    (if (.exists ^java.io.File file-mappings)
      (io/copy file-mappings (io/file amp (.getName ^java.io.File file-mappings))))

    (if (.exists ^java.io.File uberjar-file)
      (io/copy uberjar-file (io/file amp-lib (.getName ^java.io.File uberjar-file))))

    (if (.exists ^java.io.File web-resources)
      (do
        (fs/copy-dir web-resources amp)
        (.renameTo ^java.io.File (io/file target "amp/web-resources") amp-web)))

    (if (.exists ^java.io.File config)
      (do
        (fs/copy-dir config amp)
        (.renameTo ^java.io.File (io/file target "amp/config") amp-config)))

    (if (.exists ^java.io.File config)
      (do
        (fs/copy-dir config amp)))

    (if (.exists ^java.io.File licenses)
      (do
        (fs/copy-dir licenses amp)))

    (println "#### STEP 4: Create AMP..." amp-file)
    (zip-directory! amp-file amp)
  ))


(defn deploy-amp!
  [project args]
  (comment "####TODO: NOT YET IMPLEMENTED"))
