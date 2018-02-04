(defproject panic "0.1.0-SNAPSHOT"
  :description "PANic. Tool for bruteforcing of primary account number (PAN) SHA1 hashes."
  :url "https://github.com/gunnarhelgason/panic"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/tools.cli "0.3.5"]
                 [org.clojure/core.async "0.4.474"]]
  :aot :all
  :main panic.core)

