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
  (:require [leiningen.amp.impl :refer [package-amp! deploy-amp!]]))

(def ^:private dispatch-table
  { "package" package-amp!
    "deploy"  deploy-amp! })

(defn amp
  "Generate an Alfresco Module Package (AMP) file from your project.

   Usage:

     lein amp package [<options>] [<path>]
     lein amp deploy [<options>]

   Tasks:

     package              Compile and package the AMP file.
     deploy               Deploy the AMP file to an Alfresco server.

   Commandline Options:

     ####TODO - FIX THIS

     :aggressive          Check all available repositories (= Do not stop after first artifact match).
     :all                 Check Dependencies and Plugins.
     :allow-all           Allow SNAPSHOT and qualified versions to be reported as new.
     :allow-qualified     Allow '*-alpha*' versions & co. to be reported as new.
     :allow-snapshots     Allow '*-SNAPSHOT' versions to be reported as new.
     :check-clojure       Include Clojure (org.clojure/clojure) in checks.
     :dependencies        Check Dependencies. (default)
     :interactive         Run 'upgrade' in interactive mode, prompting whether to apply changes.
     :no-colors           Disable colorized output.
     :no-profiles         Do not check Dependencies/Plugins in Profiles.
     :no-tests            Do not run tests after upgrading a project.
     :overwrite-backup    Do not prompt if a backup file exists when upgrading a project.
     :plugins             Check Plugins.
     :print               Print result of 'upgrade' task instead of writing it to 'project.clj'.
     :recursive           Perform recursive 'check' or 'upgrade'.
  "
  [project & args]
  (let [^String t (when-let [^String t (first args)]
                    (when-not (.startsWith t ":") t))
        run-task! (get dispatch-table t)
        args      (if run-task! (rest args) args)]
    (if run-task!
      (run-task! project args)
      (println "Unknown task" t))))
