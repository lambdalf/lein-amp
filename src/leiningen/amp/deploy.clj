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
  (:require [leiningen.core.main :as main]))

(defn deploy-amp!
  [project args]
  (main/abort "AMP deployment is not yet implemented. Sorry!"))
