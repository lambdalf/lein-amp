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
  (:require [clojure.string    :as s]
            [clojure.java.io   :as io]
            [leiningen.uberjar :as uj]
            [me.raynes.fs      :as fs]))

(defn- map-function-on-map-vals
  "From http://stackoverflow.com/questions/1676891/mapping-a-function-on-the-values-of-a-map-in-clojure"
  [m f]
  (reduce (fn [altered-map [k v]] (assoc altered-map k (f v))) {} m))

(defn- replace-parameters
  [project value]
  (s/replace
    (s/replace
      (s/replace
        value
        "${project.name}" (s/replace (:name project) "/" "."))
      "${project.version}" (:version project))
    "${project.description}" (:description project)))

(defn- read-module-properties
  [project module-properties-file]
  (let [raw-props (java.util.Properties.)
        _         (with-open [input-stream (io/input-stream module-properties-file)]
                    (.load raw-props input-stream))]
    (map-function-on-map-vals raw-props #(replace-parameters project %))))

(defn- write-module-properties!
  [file module-properties]
  (let [props (java.util.Properties.)
        _     (.putAll props module-properties)
        _     (.store props (io/output-stream file) " !!!! DO NOT EDIT -- AUTO-GENERATED FILE -- DO NOT EDIT !!!!")]
    nil))

(defn- fexists
  [file]
  (.exists ^java.io.File file))

(defn- fname
  [file]
  (.getName ^java.io.File file))

(defn- mkdir-p
  [directory]
  (io/make-parents (io/file directory ".")))   ; make-parents won't create the last path element of the file

(defn- zip-directory!
  "Recursively compress all files in 'directory' into 'zip-file'."
  [zip-file directory]
  (let [files            (file-seq (io/file directory))
        directory-length (inc (.length (str directory)))]
    (if-not (empty? files)
      (with-open [zip-stream-out (java.util.zip.ZipOutputStream. (io/output-stream zip-file))]
        (doseq [file (rest files)]  ; Discard the first file - it's the directory itself
          (let [entry-name (str (.substring (.getPath ^java.io.File file)
                                            directory-length) (if (.isDirectory ^java.io.File file) "/"))]
            (.putNextEntry zip-stream-out (java.util.zip.ZipEntry. entry-name))
            (if (.isFile ^java.io.File file)
              (with-open [file-stream-in (io/input-stream file)]
                (io/copy file-stream-in zip-stream-out)))
            (.closeEntry zip-stream-out))))))
  nil)

(defn package-amp!
  [project args]
  (let [project-home           (io/file (:root project))
        src-amp                (io/file project-home "amp")
        module-properties-file (io/file src-amp "module.properties")
        _                      (if (not (fexists module-properties-file))
                                 (throw (RuntimeException. (str "Invalid AMP project - " module-properties-file " is missing."))))
        module-properties      (read-module-properties project module-properties-file)
        module-id              (get module-properties "module.id")
        module-version         (get module-properties "module.version")

        ; Source paths
        src-file-mapping       (io/file src-amp "file-mapping.properties")
        src-config             (io/file src-amp "config")
        src-licenses           (io/file src-amp "licenses")
        src-module             (io/file src-amp "module")
        src-web                (io/file src-amp "web")

        ; Intermediate build assets
        uberjar                (io/file (uj/uberjar project))

        ; Target paths (where the AMP gets constructs)
        target                 (io/file (:target-path project))
        tgt-amp                (io/file target     "amp")
        tgt-module-properties  (io/file tgt-amp    "module.properties")
        tgt-file-mapping       (io/file tgt-amp    "file-mapping.properties")
        tgt-config             (io/file tgt-amp    "config")
        tgt-lib                (io/file tgt-amp    "lib")
        tgt-licenses           (io/file tgt-amp    "licenses")
        tgt-module             (io/file tgt-config module-id)
        tgt-web                (io/file tgt-amp    "web")

        ; Output AMP file
        tgt-amp-file           (io/file target
                                        (or (get-in project [:amp-name])
                                            (str (:name project) "-" (:version project) ".amp")))]

    ; ${AMP}/
    (mkdir-p tgt-amp)
    (write-module-properties! tgt-module-properties module-properties)

    (if (fexists src-file-mapping)
      (io/copy src-file-mapping tgt-file-mapping))

    ; ${AMP}/config/
    (if (fexists src-config)
      (fs/copy-dir src-config tgt-amp))

    ; ${AMP}/lib/
    (if (fexists uberjar)
      (do
        (mkdir-p tgt-lib)
        (io/copy uberjar (io/file tgt-lib (fname uberjar)))))

    ; ${AMP}/licenses/
    (if (fexists src-licenses)
      (fs/copy-dir src-licenses tgt-amp))

    ; ${AMP}/module/ - note: this one is a bit unusual as it gets merged into ${AMP}/config...
    (if (fexists src-module)
      (do
        (fs/copy-dir src-module tgt-config)
        (.renameTo (io/file tgt-config (fname src-module)) tgt-module)))

    ; ${AMP}/web/
    (if (fexists src-web)
      (fs/copy-dir src-web tgt-amp))

    ; Now zip the AMP
    (zip-directory! tgt-amp-file tgt-amp)

    (println "Created module" module-id "version" module-version "in" (str tgt-amp-file))))


(defn deploy-amp!
  [project args]
  (comment "####TODO: NOT YET IMPLEMENTED"))
