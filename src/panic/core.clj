(ns panic.core
  (:require [clojure.java.io :refer [reader]]
            [clojure.tools.cli :refer [parse-opts]]
            [clojure.core.async :refer [thread chan >!! <!!]])
  (:import (java.security MessageDigest))
  (:gen-class))

(set! *warn-on-reflection* true)
(set! *unchecked-math* true)

(defn hex->byte
  [hex]
  (unchecked-byte (Integer/parseInt hex 16)))

(defn hex->bytes
  [hex]
  (seq (byte-array
        (map
         (fn [[x y]] (hex->byte (str x y)))
         (partition 2 hex)))))

(defn bytes->hex
  [bytes]
  (apply str (map (partial format "%02x") bytes)))

(defn bytes->string
  [bytes]
  (apply str (map char bytes)))

(defn load-hashes
  [file]
  (set (with-open [rdr (reader file)]
         (doall (map hex->bytes (line-seq rdr))))))

(defn calculate-luhn
  [^bytes arr]
  (let [len (dec (alength arr))]
    (loop [i 1 acc 0]
      (if (> i len)
        (+ (unchecked-remainder-int (* acc 9) 10) 48)
        (let [multiplier (if (= (unchecked-remainder-int i 2) 0) 1 2)
              product (let [product (* multiplier (- (aget arr (- len i)) 48))]
                        (if (>= product 10) (- product 9) product))]
          (recur (+ i 1) (+ acc product)))))))

(defn sha1
  (^bytes
   [^bytes pan]
   (let [md (MessageDigest/getInstance "SHA-1")]
     (.update md pan)
     (.digest md))))

(defn build-pan
  [^bytes arr ^long n]
  (let [nlen (alength arr)]
    (loop [i (- nlen 2) d n]
      (if (= d 0)
        arr
        (do
          (aset-byte arr i (+ (unchecked-remainder-int d 10) 48))
          (recur (- i 1) (unchecked-divide-int d 10)))))))

(defn search
  [iin start stop hashes channel]
  (let [panbytes (byte-array 16
                             (concat
                              (map byte (map char iin))
                              (repeat (- 16 (count iin)) 48)))]
    (dotimes [i (- stop start)]
      (build-pan panbytes (+ start i))
      (aset-byte panbytes 15 (calculate-luhn panbytes))
      (when-let [h (get hashes (seq (sha1 panbytes)))]
        (>!! channel {:hash h :panbytes (byte-array panbytes)})))))

(defn split-range
  [total-elems nthreads]
  (let [per-thread (int (/ total-elems nthreads))]
    (partition 2 1 [total-elems] (range 0 total-elems per-thread))))

(defn run
  [options]
  (let [iin (:iin options)
        nthreads (:nthreads options)
        hashes (load-hashes (:file options))
        range-max (Math/pow 10 (- 15 (count iin)))
        ranges (split-range range-max nthreads)
        channel (chan)
        workers (doall (map (fn [[start stop]]
                              (thread (search iin start stop hashes channel))) ranges))]
    (thread
     (let [hashlen (count hashes)
           padlen (int (+ 1 (Math/log10 hashlen)))
           fmt (str "%0" padlen "d")
           cnt (atom 0)]
       (while true
         (swap! cnt inc)
         (let [pan (<!! channel)
               msg (->> ["["
                         (format fmt @cnt)
                         "/"
                         hashlen
                         "] "
                         (bytes->hex (:hash pan))
                         " -> "
                         (bytes->string (:panbytes pan))]
                        (apply str))]
           (println msg)))))
    (doall (map <!! workers))))

(def cli-options
  [["-i" "--iin IIN" "IIN range"]
   ["-t" "--nthreads THREADS" "Number of threads"
    :default 4
    :parse-fn #(Integer/parseInt %)]
   ["-f" "--file FILE" "Input file containing SHA1 hashes"]
   ["-h" "--help"]])

(defn usage
  [options-summary]
  (->> ["PANic. Tool for bruteforcing of primary account number (PAN) SHA1 hashes.\n\n"
        "Usage: java <panic jar file> [options]\n\n"
        "Options:\n"
        options-summary
        "\n"]
       (apply str)))

(defn exit
  [status msg]
  (println msg)
  (System/exit status))

(defn -main
  [& args]
  (let [{:keys [options _ _ summary]} (parse-opts args cli-options)]
    (cond
     (:help options) (exit 0 (usage summary))
     (or (not (:iin options))
         (not (:file options))) (exit 1 (usage summary)))
    (run options)))
