;   Copyright (c) Ryan Wilson. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns clj-lib.types
  "Useful utility fns for inspecting and coercing types."
  (:refer-clojure :exclude [float?])
  (:require [clj-lib.core :refer (safe)]))

(defn agent?
  "Returns true if `v` is an `agent`, false otherwise."
  [v] (instance? clojure.lang.Agent v))

(defn ref?
  "Returns true if `v` is a `ref`, false otherwise."
  [v] (instance? clojure.lang.Ref v))

(defn bool?
  "Returns true if `n` is a `bool`, false otherwise."
  [n] (instance? Boolean n))

(defn byte?
  "Returns true if `n` is a `byte`, false otherwise."
  [n] (instance? Byte n))

(defn short?
  "Returns true if `n` is a `short`, false otherwise."
  [n] (instance? Short n))

(defn int?
  "Returns true if `n` is an `int`, false otherwise. Differs from
  `clojure.core/integer?` in that `n` must be a java primitive `int`, whereas
  `clojure.core/integer?` returns true if `n` is any of a `byte`, `short`, `int`
  , `long` or even `BigInteger`."
  [n] (instance? Integer n))

(defn long?
  "Returns true if `n` is a `long`, false otherwise."
  [n] (instance? Long n))

(defn float?
  "Returns true if `n` is a `float`, false otherwise. Differs from
  `clojure.core/float?` in that `n` must be a java primitive `float`, whereas
  `clojure.core/float?` returns true if `n` is \"a floating point number,\"
  either single or a double precision."
  [n] (instance? Float n))

(defn double?
  "Returns true if `n` is a `double`, false otherwise."
  [n] (instance? Double n))

(def ^:const type-bool-array
  "Type const of a java primitive boolean array."
  (type (boolean-array 0)))

(def ^:const type-byte-array
  "Type const of a java primitive byte array."
  (type (byte-array 0)))

(def ^:const type-short-array
  "Type const of a java primitive short array."
  (type (short-array 0)))

(def ^:const type-int-array
  "Type const of a java primitive int array."
  (type (int-array 0)))

(def ^:const type-long-array
  "Type const of a java primitive long array."
  (type (long-array 0)))

(def ^:const type-float-array
  "Type const of a java primitive float array."
  (type (float-array 0)))

(def ^:const type-double-array
  "Type const of a java primitive double array."
  (type (double-array 0)))

(defn bool-array?
  "Returns true if `v` is a bool-array, false otherwise."
  [v] (= (type v) type-bool-array  ))

(defn byte-array?
  "Returns true if `v` is a byte-array, false otherwise."
  [v] (= (type v) type-byte-array  ))

(defn short-array?
  "Returns true if `v` is a short-array, false otherwise."
  [v] (= (type v) type-short-array ))

(defn int-array?
  "Returns true if `v` is a int-array, false otherwise."
  [v] (= (type v) type-int-array   ))

(defn long-array?
  "Returns true if `v` is a long-array, false otherwise."
  [v] (= (type v) type-long-array  ))

(defn float-array?
  "Returns true if `v` is a float-array, false otherwise."
  [v] (= (type v) type-float-array ))

(defn double-array?
  "Returns true if `v` is a double-array, false otherwise."
  [v] (= (type v) type-double-array))

(defn pattern?
  "Returns true if `v` is a `java.util.regex.Pattern`, as created by
  `#\"som[eE]thing\"` or `(re-pattern \"some[eE]thing\")`."
  [v] (= (type v) java.util.regex.Pattern))

(defn throwable?
  "Returns true if `v` is a `Throwable`, false otherwise."
  [v] (instance? Throwable v))

(defn empty?->nil
  "If `v` is `empty?`, returns nil. Otherwise, returns `v`."
  [v]
  (if (empty? v) nil v))

(defn ->int
  "Parses `v` as an int. Throws an exception if `v` is not parsable as an int."
  [v]
  (cond
   (nil? v) nil
   (integer? v) v
   (number? v) (int v)
   :else (Integer/parseInt v)))

(defn ->long
  "Parses `v` as a long. Throws an exception is `v` is not parsable as a long."
  [v]
  (cond
   (nil? v) nil
   (long? v) v
   (string? v) (Long/parseLong v)
   :else (long v)))

(defn ->float
  "Parses `v` as a float. Throws an exception if `v` is not parsable as a float."
  [v]
  (cond
   (nil? v) nil
   (float? v) v
   (double? v) (float v)
   (integer? v) (float v)
   :else (Float/parseFloat v)))

(defn ->double
  "Parses `v` as a double. Throws an exception if `v` is not parsable as a double."
  [v]
  (cond
   (nil? v) nil
   (double? v) v
   (float? v) (double v)
   (integer? v) (double v)
   :else (Double/parseDouble v)))

(defn ->bool
  "Parses `v` as a boolean. Throws an exception if `v` is not parsable as a boolean."
  [v]
  (cond
   (nil? v) false
   (bool? v) v
   (number? v) (not (zero? v))
   :else (Boolean/parseBoolean v)))

(defn safe->int
  "Parses `v` as an int. Returns `nil` if `v` is not parsable as an int."
  [v] (safe (->int v)))

(defn safe->long
  "Parses `v` as an int. Returns `nil` if `v` is not parsable as an int."
  [v] (safe (->long v)))

(defn safe->float
  "Parses `v` as a float. Returns `nil` if `v` is not parsable as a float."
  [v] (safe (->float v)))

(defn safe->double
  "Parses `v` as a double. Returns `nil` if `v` is not parsable as a double."
  [v] (safe (->double v)))

(defn ->string
  "Coerces `v` to a string."
  [v]
  (cond
    (nil? v) ""
    (string?  v) v
    (keyword? v) (str (name v))
    (symbol?  v) (str (name v))
    :else (str v)))

(defn ->keyword
  "Coerces `v` to a keyword, if possible."
  [v]
  {:pre [(not (clojure.core/float? v))]}
  (cond
    (nil? v) nil
    (keyword? v) v
    (integer? v) (keyword (str v))
    (symbol?  v) (keyword (name v))
    :else (keyword v)))

(defn ->symbol
  "Coerces `v` to a symbol, if possible."
  [v]
  {:pre [(not (clojure.core/float? v))]}
  (cond
    (nil? v) nil
    (symbol?  v) v
    (integer? v) (symbol (str v))
    (keyword? v) (symbol (name v))
    :else (symbol v)))
