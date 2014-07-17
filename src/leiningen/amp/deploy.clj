;
; Copyright © 2014 Peter Monks (pmonks@gmail.com)
;
; All rights reserved. This program and the accompanying materials
; are made available under the terms of the Eclipse Public License v1.0
; which accompanies this distribution, and is available at
; http://www.eclipse.org/legal/epl-v10.html
;
; Contributors:
;    Peter Monks   - initial implementation

(ns leiningen.amp.deploy
  (:require [clojure.java.io             :as io]
            [cemerick.pomegranate.aether :as aether]
            [leiningen.amp.package       :as package]
            [leiningen.core.main         :as main])
  (:import [org.alfresco.repo.module.tool ModuleManagementTool]))

(defn- locate-amp
  "Finds the AMP file produced by the given project"
  [{:keys [target-path] :as project}]
  (if-let [amp (package/target-file project (io/file target-path))]
    amp
    (main/abort "The AMP file was not found. Did you remember to run `lein amp package` first?")))

(defn- repo-map
  "Builds a Pomegranate compatible map of repositories given the project configured ones.
   The repositories are found in the project to be a sequence of something like:
       [<id> {:url \"http://url.to.repo\" ...}]"
  [repos]
  (let [result
        (reduce (fn [m [id {:keys [url]}]]
                  (assoc m id url)) {} repos)]
    result))

(defn- depname
  "Stringifies the filename of a leiningen dependency"
  [dep]
  (-> dep first name))

(defn- version
  "Stringifies the version of a leiningen dependency"
  [dep]
  (str "-" (second dep)))

(defn- classifier
  "Provides an appendable string for the classifier"
  [dep]
  (if-let [classifier (:classifier dep)]
    (str "-" classifier)
    ""))

(defn- extension
  "Provides an appenavle string for the extension"
  [dep]
  (if-let [ext (:extension dep)]
    (str "." extension)
    ".war"))

(defn- dep-to-filename
  "Translates a dependency in a filename"
  [dep]
  (str (depname dep) (version dep) (classifier dep) (extension dep)))

(defn- dep-filter
  "Creates a function suitable to `filter` a seq of dependencies to find the requested one"
  [dep]
  (fn [^java.io.File file]
    (let [name (.getName file)
          dep-name (dep-to-filename dep)]
      (= dep-name name))))

(defn- find-one
  "Finds one and only one dependency that matches the given file"
  [file files]
  (let [filtered (filter (dep-filter file) files)
        file (first filtered)]
    (if file
      file
      (main/abort "The target WAR couldn't be found in the configured repository"))))

(defn- find-dependency
  "Finds the WAR from the project then returns its file"
  [{:keys [repositories amp-target-war]}]
  (let [files (aether/dependency-files
                   (aether/resolve-dependencies :repositories (repo-map repositories)
                                                :coordinates  [amp-target-war]))]
    (if (empty? files)
      (main/abort "No target WAR was found. Did you set :amp-target-war in your project.clj?")
      (find-one amp-target-war files))))

(defn- copy-war
  "Copies the WAR file where to install the AMP into a working location"
  [{:keys [amp-target-war] :as project}]
  (let [dependency (find-dependency project)
        temp-war (io/file (:target-path project) (dep-to-filename amp-target-war))]
    (io/copy dependency temp-war)
    temp-war))

(defn- install!
  "Uses the Alfresco MMT to install the AMP into the target WAR"
  [amp war]
  (ModuleManagementTool/main (into-array String ["install" (.toString amp) (.toString war)])))

(defn deploy-amp!
  "Installs the generated AMP into the specified WAR"
  [project args]
  (let [amp (locate-amp project)
        war (copy-war   project)]
    (install! amp war)))
