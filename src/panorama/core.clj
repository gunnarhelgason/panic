(ns panorama.core
  (:require [clojure.java.io :refer [file output-stream]]
            [clojure.tools.cli :refer [parse-opts]])
  (:import (java.security MessageDigest))
  (:gen-class))

(set! *warn-on-reflection* true)
(set! *unchecked-math* true)

(defn calculate-luhn
  [^bytes arr]
  (let [len (alength arr)]
    (loop [i 1 acc 0]
      (if (> i len)
        (unchecked-remainder-int (* acc 9) 10)
        (let [multiplier (if (= (unchecked-remainder-int i 2) 0) 1 2)
              product (let [product (* multiplier (aget arr (- len i)))]
                        (if (>= product 10) (- product 9) product))]
          (recur (+ i 1) (+ acc product)))))))

(defn sha1
  (^bytes
   [^bytes pan]
   (let [md (MessageDigest/getInstance "SHA-1")]
     (.update md pan)
     (.digest md))))

(defn insert
  [^bytes arr n]
  (let [nlen (alength arr)]
    (aset-byte arr 15 0)
    (loop [i (- nlen 2) d n]
      (if (= d 0)
        arr
        (do
          (aset-byte arr i (unchecked-remainder-int d 10))
          (recur (- i 1) (unchecked-divide-int d 10)))))))

(defn make-lookup-entries
  [iin start stop]
  (let [panbytes (byte-array 16 (map #(Character/digit ^Character % 10) iin))
        output-file (str "./" iin "_" start "-"(dec stop))]
    (with-open [w (output-stream (file  output-file))]
      (dotimes [i (- stop start)]
        (insert panbytes (+ start i))
        (aset-byte panbytes 15 (calculate-luhn panbytes))
        (.write w (sha1 panbytes))
        (.write w panbytes)))))

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
                              (future (make-lookup-entries iin start stop))) ranges))]
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
