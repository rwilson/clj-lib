;   Copyright (c) Ryan Wilson. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns clj-lib.types-test
  (:require [clj-lib.types :refer :all]
            [clojure.test :refer :all])
  (:refer-clojure :exclude [float?]))

(def a-not-dynamic-var nil)
(def ^:dynamic *a-dynamic-var* nil)

(deftest test-agent?
  (is (not (agent? nil)))
  (is (not (agent? 1)))
  (is (agent? (agent 1))))

(deftest test-ref?
  (is (not (ref? nil)))
  (is (not (ref? (atom 1))))
  (is (not (ref? 1)))
  (is (ref? (ref {}))))

(deftest test-bool?
  (is (not (bool? "foo")))
  (is (bool? true))
  (is (bool? false))
  (is (bool? (boolean 1))))

(deftest test-byte?
  (is (not (byte? 1)))
  (is (not (byte? "a")))
  (is (byte? (byte 0x1))))

(deftest test-short?
  (is (not (short? "a")))
  (is (not (short? 100)))
  (is (not (short? 5.0)))
  (is (short? (short 5))))

(deftest test-int?
  (is (not (int? "a")))
  (is (not (int? (double 100))))
  (is (not (int? 1.0)))
  (is (int? (int 1))))

(deftest test-long?
  (is (not (long? "a")))
  (is (not (long? (double 100))))
  (is (not (long? (int 1))))
  (is (long? (long 1))))

(deftest test-float?
  (is (not (float? "a")))
  (is (not (float? 1)))
  (is (not (float? (double 1.0))))
  (is (float? (float 1.0))))

(deftest test-double?
  (is (not (double? "a")))
  (is (not (double? 1)))
  (is (not (double? (float? 1.0))))
  (is (double? (double 1.0))))

(deftest test-bool-array?
  (is (not (bool-array? {:a 1})))
  (is (bool-array? (boolean-array (map even? (range 1 10))))))

(deftest test-byte-array?
  (is (not (byte-array? {:a 1})))
  (is (byte-array? (byte-array (.getBytes (String. "test-string") "UTF-8")))))

(deftest test-short-array?
  (is (not (short-array? {:a 1})))
  (is (short-array? (short-array (map short (range 1 10))))))

(deftest test-int-array?
  (is (not (int-array? {:a 1})))
  (is (int-array? (int-array (range 1 10)))))

(deftest test-long-array?
  (is (not (long-array? {:a 1})))
  (is (long-array? (long-array (range 1 10)))))

(deftest test-float-array?
  (is (not (float-array? {:a 1})))
  (is (float-array? (float-array (range 1 10)))))

(deftest test-double-array?
  (is (not (double-array? {:a 1})))
  (is (double-array? (double-array (range 1 10)))))

(deftest test-pattern?
  (is (not (pattern? "foo")))
  (is (not (pattern? nil)))
  (is (not (pattern? 1)))
  (is (pattern? #"[a-z]{3}"))
  (is (pattern? (re-pattern "[a-z]{3}"))))

(deftest test-throwable?
  (is (not (throwable? nil)))
  (is (not (throwable? 1)))
  (is (not (throwable? "foo")))
  (is (not (throwable? {})))
  (is (throwable? (Exception. "foo")))
  (is (throwable? (RuntimeException. "foo"))))

(deftest test->int
  (is (zero? (->int "0")))
  (is (= (int 1) (->int "1")))
  (is (zero? (->int 0)))
  (is (zero? (->int 0.0)))
  (is (= (int 1) (->int 1)))
  (is (= (int 1) (->int 1.0)))
  (is (= (int -1) (->int "-1")))
  (is (= (int -1) (->int -1)))
  (is (= (int -1) (->int -1.0)))
  (is (nil? (->int nil)))
  (is (thrown? Exception (->int "foo"))))

(deftest test->long
  (is (zero? (->long "0")))
  (is (= 1 (->long "1")))
  (is (zero? (->long 0)))
  (is (zero? (->long 0.0)))
  (is (= 1 (->long 1)))
  (is (= 1 (->long 1.0)))
  (is (= -1 (->long "-1")))
  (is (= -1 (->long -1)))
  (is (= -1 (->long -1.0)))
  (is (nil? (->long nil)))
  (is (thrown? Exception (->long "foo"))))

(deftest test->float
  (is (zero? (->float "0")))
  (is (zero? (->float "0.0")))
  (is (= 1.0 (->float "1.0")))
  (is (zero? (->float 0)))
  (is (zero? (->float 0.0)))
  (is (= 1.0 (->float 1)))
  (is (= 1.0 (->float 1.0)))
  (is (= -1.0 (->float "-1")))
  (is (= -1.0 (->float "-1.0")))
  (is (= -1.0 (->float -1)))
  (is (= -1.0 (->float -1.0)))
  (is (nil? (->float nil)))
  (is (thrown? Exception (->float "foo"))))

(deftest test->double
  (is (zero? (->double "0")))
  (is (zero? (->double "0.0")))
  (is (= 1.0 (->double "1.0")))
  (is (zero? (->double 0)))
  (is (zero? (->double 0.0)))
  (is (= 1.0 (->double 1)))
  (is (= 1.0 (->double 1.0)))
  (is (= -1.0 (->double "-1")))
  (is (= -1.0 (->double "-1.0")))
  (is (= -1.0 (->double -1)))
  (is (= -1.0 (->double -1.0)))
  (is (nil? (->double nil)))
  (is (thrown? Exception (->double "foo"))))

(deftest test->bool
  (is (false? (->bool 0)))
  (is (true? (->bool 1)))
  (is (false? (->bool 0.0)))
  (is (true? (->bool 1.1)))
  (is (false? (->bool false)))
  (is (false? (->bool "false")))
  (is (true? (->bool true)))
  (is (true? (->bool "true")))
  (is (false? (->bool nil)))
  (is (false? (->bool "foo"))))

(deftest test-safe->int
  (is (nil? (safe->int nil)))
  (is (nil? (safe->int "foo")))  )

(deftest test-safe->long
  (is (nil? (safe->long nil)))
  (is (nil? (safe->long "foo")))  )

(deftest test-safe->float
  (is (nil? (safe->float nil)))
  (is (nil? (safe->float "foo")))  )

(deftest test-safe->double
  (is (nil? (safe->double nil)))
  (is (nil? (safe->double "foo")))  )

(deftest test->string
  (let [s "foo"]
    (is (= s (->string s)))
    (is (= s (->string (keyword s))))
    (is (= s (->string (symbol s)))))
  (is (= "1" (->string 1)))
  (is (= "1.0" (->string 1.0))))

(deftest test->keyword
  (let [k :kw]
    (is (= k (->keyword k)))
    (is (= k (->keyword (str (name k)))))
    (is (= k (->keyword (symbol (name k))))))
  (is (= :1 (->keyword 1)))
  (is (nil? (->keyword nil)))
  (is (thrown? AssertionError (->keyword 1.0))))

(deftest test->symbol
  (let [s (symbol "foo")]
    (is (= s (->symbol s)))
    (is (= s (->symbol (keyword (name s)))))
    (is (= s (->symbol (str (name s))))))
  (is (= (symbol "1") (->symbol 1)))
  (is (nil? (->symbol nil)))
  (is (thrown? AssertionError (->symbol 1.0))))
