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

(defproject lein-amp "0.1.0-SNAPSHOT"
  :description      "Leiningen plugin for generating Alfresco Module Package (AMP) files."
  :url              "https://github.com/pmonks/lein-amp"
  :license          {:name "Eclipse Public License"
                     :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.0.0"
  :dependencies [
                  [me.raynes/fs "1.4.5"]
                ]
  :eval-in-leiningen true)
