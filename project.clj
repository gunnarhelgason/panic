(defproject panic "0.1.0-SNAPSHOT"
  :description "PANic. Tool for bruteforcing of primary account number (PAN) SHA1 hashes."
  :url "https://github.com/gunnarhelgason/panic"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.cli "0.3.1"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]]
  :aot :all
  :main panic.core)

