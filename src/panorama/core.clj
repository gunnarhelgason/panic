(ns panorama.core
  (:require [clojure.java.io :refer [writer]]
            [clojure.tools.cli :refer [parse-opts]])
  (:import (java.security MessageDigest))
  (:gen-class))

(set! *warn-on-reflection* true)

(defn divmod
  [m n]
  [(quot m n) (rem m n)])

(defn calculate-luhn
  [pan]
  (let [factors (cycle [2 1])
        digits (map #(Character/digit ^Character % 10) (reverse pan))
        products (flatten (map #(divmod (* % %2) 10) digits factors))]
    (mod (- 10 (reduce + products)) 10)))

(defn string->bytes
  (^bytes
   [^String string]
   (.getBytes string "utf-8")))

(defn bytes->hex
  [bytes]
  (apply str (map (partial format "%02x") bytes)))

(defn sha1
  [^String s]
  (let [md (MessageDigest/getInstance "SHA-1")]
    (.update md (string->bytes s))
    (bytes->hex (.digest md))))

(defn build-pan
  [iin n]
  (let [pan (str iin n)]
    (str pan (calculate-luhn pan))))

(defn zero-pad
  [iin n]
  (let [nlen (count (str n))]
    (str (apply str (repeat (- (- 15 (count iin)) nlen) "0")) n)))

(defn num-range
  [iin start stop]
  (map #(zero-pad iin %) (range start stop)))

(defn pan-range
  [iin start stop]
  (map #(build-pan iin %) (num-range iin start stop)))

(defn generate-sha1
  [iin start stop]
  (let [output-file (str "./" iin "_" start "-"(dec stop))]
    (with-open [w (writer output-file)]
      (doseq [pan (pan-range iin start stop)]
        (.write w (str pan ":" (sha1 pan) "\n"))))))

(defn split-range [total-elems nthreads]
  (let [per-thread (int (/ total-elems nthreads))]
    (partition 2 1 [total-elems] (range 0 total-elems per-thread))))

(defn run
  [options]
  (let [iin (:iin options)
        nthreads (:nthreads options)
        range-max (Math/pow 10 (- 15 (count iin)))
        ranges (split-range range-max nthreads)
        workers (doall (map (fn [[start stop]]
                              (future (generate-sha1 iin start stop))) ranges))]
    (doall (map deref workers))))

(def cli-options
  [["-i" "--iin IIN" "IIN range"]
   ["-t" "--nthreads THREADS" "Number of threads"
    :default 4
    :parse-fn #(Integer/parseInt %)]
   ["-h" "--help"]])

(defn -main [& args]
  (let [{:keys [options _ _ summary]} (parse-opts args cli-options)]
    (if (:help options)
      (println summary)
      (run options))))
