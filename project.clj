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
;    Carlo Sciolla - implemented "install" task

(defproject lein-amp "0.4.0-SNAPSHOT"
  :description       "Leiningen plugin for generating Alfresco Module Package (AMP) files."
  :url               "https://github.com/lambdalf/lein-amp"
  :license           {:name "Eclipse Public License"
                      :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version  "2.0.0"
  :repositories      [
                       ["alfresco" "https://artifacts.alfresco.com/nexus/content/groups/public/"]
                     ]
  :dependencies      [
                       [me.raynes/fs "1.4.6"]
                       [org.alfresco/alfresco-mmt "4.2.f"]
                     ]
  :eval-in-leiningen true)
