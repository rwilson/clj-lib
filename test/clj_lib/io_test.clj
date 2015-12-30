;   Copyright (c) Ryan Wilson. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns clj-lib.io-test
  (:require [clj-lib
             [io :refer :all]
             [types :refer (byte-array?)]]
            [clojure.test :refer :all]
            [clojure.java.io :as clj-io]))

(def serialization-test-values
  [["foo"  "\"foo\""]
   [1      "1"      ]
   [1.0    "1.0"    ]
   [{:a 1} "{:a 1}" ]
   ['(1)   "(1)"    ]
   [[1]    "[1]"    ]])

(deftest test-serialize-string
  (doall
   (map (fn [[in out]]
          (is (= out (serialize-string in))))
        serialization-test-values)))

(deftest test-deserialize-string
  (doall
   (map (fn [[in out]]
          (is (= in (deserialize-string out))))
        serialization-test-values)))

(deftest test-serialize-deserialize
  (doall
   (map (fn [[in out]]
          (is (= in (deserialize-string (serialize-string in)))))
        serialization-test-values)))

(def a-string-as-bytes (byte-array [0x61 0x73 0x74 0x72 0x69 0x6e 0x67]))
(def a-string-as-bytes-b64 (byte-array [89 88 78 48 99 109 108 117 90 119 61 61]))

(deftest test-str->from-bytes
  (let [s-in "astring"
        bytes (str->bytes s-in)
        s-out (bytes->str bytes)]
    (is (byte-array? bytes))
    (is (seq bytes))
    ; have to use seq to get byte-by-byte deep equality, since = for byte-array
    ; types is based on java's array reference equality
    (is (= (seq a-string-as-bytes) (seq bytes)))
    (is (= s-in s-out))))

(deftest test-base64-encode-decode
  (let [encoded (base64-encode a-string-as-bytes)
        decoded (base64-decode a-string-as-bytes-b64)]
    (is (= (seq a-string-as-bytes) (seq decoded)))
    (is (= (bytes->str a-string-as-bytes-b64)
           encoded))))

(defn mk-random-string [len]
  (let [chars (map char (concat (range 48 58)
                                (range 65 91)
                                (range 97 123)
                                (list \space \newline)))
        random-char (fn [] (rand-nth chars))]
    (apply str (take len (repeatedly random-char)))))

(deftest test-compress-decompress
  (let [orig (mk-random-string 10000)
        mod (-> orig compress decompress)]
    (is (= orig mod))))

(deftest test-ensure-dirs
  (let [p "/tmp/clj-lib/io/test/dir/file.txt"]
    (when (.exists (clj-io/as-file p))
      (.delete (clj-io/as-file "/tmp/clj-lib/io")))
    (ensure-dirs p)
    (is (.exists (.getParentFile (clj-io/as-file p))))))
