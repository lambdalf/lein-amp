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

(ns leiningen.amp.package
  (:require [clojure.string           :as s]
            [clojure.java.io          :as io]
            [clojure.pprint           :as pprint]
            [me.raynes.fs             :as fs]
            [leiningen.core.main      :as main]
            [leiningen.core.project   :as proj]
            [leiningen.core.classpath :as classpath]
            [leiningen.jar            :as jar]))

(defn- map-function-on-map-vals
  "From http://stackoverflow.com/questions/1676891/mapping-a-function-on-the-values-of-a-map-in-clojure"
  [m f]
  (into {} (for [[k v] m] [k (f v)])))

(defn- fexists
  [file]
  (.exists ^java.io.File file))

(defn- fname
  [file]
  (.getName ^java.io.File file))

(defn- flast-modified
  [file]
  (.lastModified ^java.io.File file))

(defn- dlast-modified
  "Gets the latest last modified date for all of the files in the given directory."
  [directory]
  (apply max (map flast-modified (file-seq directory))))

(defn- mkdir-p
  [directory]
  (io/make-parents (io/file directory ".")))   ; make-parents won't create the last path element of the file passed to it

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

(defn- get-amp-src
  "Returns the directory of the AMP source, as a java.io.File."
  [project]
  (let [project-home (io/file (:root project))
        src-amp-str  (:amp-source-path project)]
    (if (nil? src-amp-str)
      (io/file project-home "amp")
      (io/file src-amp-str))))

(defn- amp-is-stale?
  "Determines whether the given AMP file is stale. Note: limited to checking the following
  'well known' source paths:
  * :amp-source-path   (AMP source files)
  * :source-paths      (Clojure source files)
  * :java-source-paths (Java source files)
  * :resource-paths    (resource source files)"
  [tgt-amp-file project]
  (let [src-amp        (get-amp-src        project)
        srcs-clojure   (map io/file (:source-paths      project))
        srcs-java      (map io/file (:java-source-paths project))
        srcs-resources (map io/file (:resource-paths    project))]
    (if (not (fexists tgt-amp-file))
      true
      (< (flast-modified tgt-amp-file)
         (apply max (flatten [(dlast-modified src-amp)
                              (map dlast-modified srcs-clojure)
                              (map dlast-modified srcs-java)
                              (map dlast-modified srcs-resources)]))))))

(defn- amp-is-up-to-date?
  "Inverse of amp-is-stale?"
  [tgt-amp-file project]
  (not (amp-is-stale? tgt-amp-file project)))

(defn- fix-snapshot-version
  "This method converts SNAPSHOT version numbers into something MMT can support. It does this by
  decrementing the last non-zero version number element by 1 and appending a 999 version component on
  the end.  It also always returns a version number with at least 3 version components."
  [version]
  (if (.endsWith ^String version "-SNAPSHOT")
    (let [non-snapshot-version      (.substring ^String version 0 (- (.length ^String version) (.length "-SNAPSHOT")))
          version-number-components (map #(Integer/parseInt %) (s/split non-snapshot-version #"\."))
          version-number-components (drop-while zero? (reverse version-number-components))
          _                         (if (empty? version-number-components) (main/abort (str "Invalid project version number: " version)))
          version-number-components (reverse (concat [999 (dec (first version-number-components))] (rest version-number-components)))
          version-number-components (if (= (count version-number-components) 2)
                                      (concat version-number-components [999])
                                      version-number-components)]
      (s/join "." version-number-components))
    version))

(defn- replace-parameters
  [project value]
  (-> value
      (s/replace "${project.name}"        (if (= (:group project) (:name project))
                                            (:name project)
                                            (str (if (nil? (:group project))
                                                   ""
                                                   (str (:group project) "."))
                                                 (:name project))))
      (s/replace "${project.title}"       (:title project))
      (s/replace "${project.version}"     (fix-snapshot-version (:version project)))
      (s/replace "${project.description}" (:description project))))

(defn- read-module-properties
  [project module-properties-file]
  (let [raw-props (java.util.Properties.)
        _         (with-open [input-stream (io/input-stream module-properties-file)]
                    (.load raw-props input-stream))]
    (map-function-on-map-vals raw-props #(replace-parameters project %))))

(defn- write-module-properties!
  [file module-properties]
  (doto (java.util.Properties.)
    (.putAll module-properties)
    (.store (io/output-stream file) " !!!! DO NOT EDIT -- AUTO-GENERATED FILE -- DO NOT EDIT !!!!"))
  nil)

(defn- rewrite-module-context!
  [module-context-file module-id]
  (let [module-context-content (slurp module-context-file)
        rewritten-content      (s/replace module-context-content "${project.moduleId}" module-id)]
    (spit module-context-file rewritten-content))
  nil)

(defn- get-dependency-jars
  [project]
  (filter #(.endsWith (.getName ^java.io.File %) ".jar")
          (classpath/resolve-dependencies :dependencies project)))

(defn target-file
  "Returns a java.io.File for the target AMP file. Note: it doesn't necessarily exist."
  [project target]
  (io/file target
           (or (get-in project [:amp-name])
               (str (:name project) "-" (:version project) ".amp"))))

(defn package-amp!
  "Package the project and its dependencies into an AMP."
  [project args]
  (let [project                (proj/unmerge-profiles project [:base :provided])
        src-amp                (get-amp-src project)
        module-properties-file (io/file src-amp "module.properties")
        _                      (if (not (fexists module-properties-file))
                                 (main/abort (str "Invalid AMP project - " module-properties-file " is missing.")))
        module-properties      (read-module-properties project module-properties-file)
        module-id              (get module-properties "module.id")
        module-version         (get module-properties "module.version")

        ; Source paths
        src-file-mapping       (io/file src-amp "file-mapping.properties")

        ; Target paths (where the AMP gets constructed)
        target                 (io/file (:target-path project))
        tgt-amp                (io/file target              "amp")
        tgt-module-properties  (io/file tgt-amp             "module.properties")
        tgt-file-mapping       (io/file tgt-amp             "file-mapping.properties")

        tgt-lib                (io/file tgt-amp             "lib")

        ; Output AMP file
        tgt-amp-file           (target-file project target)]

    (if (amp-is-up-to-date? tgt-amp-file project)
      (main/info (str "AMP file " (str tgt-amp-file) " is up to date."))
      (do
        (if (fexists tgt-amp-file)
          (main/info (str "AMP file " (str tgt-amp-file) " is stale - rebuilding."))
          (main/info (str "AMP file doesn't exist - building.")))

        ; Cleanup anything left from a prior build
        (if (fexists tgt-amp)
          (fs/delete-dir tgt-amp))

        (if (fexists tgt-amp-file)
          (.delete ^java.io.File tgt-amp-file))

        ; module.properties & file-mappings.properties
        (mkdir-p tgt-amp)
        (write-module-properties! tgt-module-properties module-properties)

        (if (fexists src-file-mapping)
          (io/copy src-file-mapping tgt-file-mapping))

        ; lib
        (let [project-jar     (io/file (get (jar/jar project) [:extension "jar"]))
              dependency-jars (get-dependency-jars project)]
          (if (or (fexists project-jar)
                  (not (empty? dependency-jars)))
            (do
              (mkdir-p tgt-lib)
              (if (fexists project-jar)
                (io/copy project-jar (io/file tgt-lib (fname project-jar))))
              (if (not (empty? dependency-jars))
                (doall (map #(io/copy % (io/file tgt-lib (fname %))) dependency-jars))))))

        ; Now zip the AMP
        (zip-directory! tgt-amp-file tgt-amp)

        (main/info (str "Built AMP " module-id " v" module-version " in " (str tgt-amp-file)))))))
