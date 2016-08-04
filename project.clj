;   Copyright (c) Ryan Wilson. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(defproject rwilson/clj-lib "0.8.0-SNAPSHOT"
  :description "Util libraries for common tasks"
  :url "http://github.com/rwilson/clj-lib/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"
            :comments "Same as clojure."}

  :aliases {"build" ^{:doc "Clean, compile all, test"}
            ["do" ["clean"] ["jar"] ["test"] ["codox"]]

            "build-install" ^{:doc "Clean, compile all, test, & install"}
            ["do" ["clean"] ["jar"] ["test"] ["install"] ["codox"]]}

  :dependencies [[org.clojure/clojure "1.7.0"]]
  :plugins [[lein-codox "0.9.5"]]

  ;; Codox
  :codox {:metadata {:doc "FIXME: write docs"}
          :output-path "doc/"
          :source-uri "http://github.com/rwilson/clj-lib/blob/master/{filepath}/#L{line}"}

  :test-selectors {:default (fn [m] (not (:network m)))
                   :network :network
                   :all (constantly true)}

  :profiles {:uberjar {:aot :all}}

  :repositories [["releases" {:creds :gpg}]]
  :deploy-repositories [["releases" :clojars]])
