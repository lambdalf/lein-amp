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

(ns leiningen.amp
  (:require [clojure.string        :as s]
            [leiningen.amp.package :refer [package-amp!]]
            [leiningen.amp.install :refer [install-amp!]]))

(def ^:private dispatch-table
  { "package" package-amp!
    "install"  install-amp! })

(defn amp
  "Generate an Alfresco Module Package (AMP) file from your project.
   See http://wiki.alfresco.com/wiki/AMP_Files for more details on the AMP
   file format. See http://wiki.alfresco.com/wiki/Module_Management_Tool
   for details on how to deploy AMP files to an Alfresco installation.

   To create an empty project compatible with this plugin, please use
   the 'amp' lein-new template (see https://github.com/lambdalf/amp-template).

   Usage:

     lein amp package [<options>]
     lein amp install [<options>] [<path-to-alfresco-war>]

   Tasks:

     package    Compile and package the AMP file.
     install    Install the AMP file into the specified Alfresco WAR file.  See
                'Project Options' below for an alternative way to specify this.

   Commandline Options:

     <none>

   Project Options:

     The 'install' task will optionally read the location of the Alfresco WAR
     file from a :amp-target-war property in project.clj.
  "
  [project & args]
  (let [^String t (when-let [^String t (first args)]
                    (when-not (.startsWith t ":") t))
        run-task! (get dispatch-table t)
        args      (if run-task! (rest args) args)]
    (if run-task!
      (run-task! project args)
      (println (if (nil? t) "No task specified" (str "Unknown task: " t))
               "\nValid tasks are:" (s/join ", " (keys dispatch-table))))))
