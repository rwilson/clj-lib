;   Copyright (c) Ryan Wilson. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns clj-lib.io
  "Useful utility fns for local & network IO"
  (:require [clojure.java.io :as clj-io]
            [clojure.string :as clj-str]
            [clj-lib.types :refer (byte-array? ->string)])
  (:import [java.io ByteArrayOutputStream]
           [java.util.zip Deflater Inflater DeflaterOutputStream InflaterOutputStream]
           [java.util Base64]))

(def ^{:dynamic true
       :doc "Defines the default character encoding for fns in this namespace that
             require one. Specifically, string to/from bytes and URL encode/decode."}
  *char-encoding* "UTF-8")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; Serializing data to / from strings
;;

(defn serialize-string
  "Serializes expressions `s` with `pr`, and returns the value as a string."
  [s] (with-out-str (pr s)))

(defn deserialize-string
  "Inverse of [[serialize-string]]. An alias for `read-string`."
  [s] (read-string s))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; string to/from byte[]
;;

(defn str->bytes
  "Returns string `s` as a byte array, using [[*char-encoding*]]."
  [s]
  {:pre [(string? s)]}
  (.getBytes (->string s) *char-encoding*))

(defn bytes->str
  "Constructs a string from byte-array `bytes`, using [[*char-encoding*]]."
  [bytes]
  {:pre [(byte-array? bytes)]}
  (String. bytes *char-encoding*))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; byte[] to/from base64 encoding
;;

(def ^{:private true
       :doc "Base64.Encoder is thread-safe; this Var caches the instance so we
             only ever create one. Using delay defers instantiation until the
             instance is actually needed."}
  base64-encoder (delay (Base64/getEncoder)))

(def ^{:private true
       :doc "Base64.Decoder is thread-safe; this Var caches the instance so we
             only ever create one. Using delay defers instantiation until the
             instance is actually needed."}
  base64-decoder (delay (Base64/getDecoder)))

(defn base64-encode [b]
  {:pre [(byte-array? b)]}
  (.encodeToString @base64-encoder ^bytes b))

(defn base64-decode [v]
  "accepts either a string or a byte-array, base64 decodes the value,
   and returns a byte-array."
  (cond
    (string? v) (.decode @base64-decoder ^String v)
    (byte-array? v) (.decode @base64-decoder ^bytes v)
    :else (throw (IllegalArgumentException. (str "Cannot base64-decode type: "
                                                 (type v))))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; Compression & Decompression using zlib
;;

(def ^{:dynamic true
       :doc "Defines the size of the compress/decompress stream buffers, in bytes"}
  *zip-buff-size* 1024)

(defn compress
  "Compresses the input string and returns it as a byte array, using zlib compression"
  [s]
  (let [bytes (str->bytes s)
        out (ByteArrayOutputStream.)]
    (with-open [compresser (DeflaterOutputStream. out (Deflater.) *zip-buff-size*)]
      (.write compresser ^bytes bytes))
    (.toByteArray out)))

(defn decompress
  "Decompresses a zlib compressed byte-array, returns the value as a string"
  [b]
  {:pre [(byte-array? b)]}
  (let [out (ByteArrayOutputStream.)]
    (with-open [decompresser (InflaterOutputStream. out (Inflater.) *zip-buff-size*)]
      (.write decompresser ^bytes b))
    (bytes->str (.toByteArray out))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; File Utils
;;

(defn ensure-dirs
  "Ensures that all directories in the specified `path` exist, creating them if
  necessary."
  [path]
  (let [parent-dir (.getParentFile (clj-io/as-file path))]
    (when-not (.exists parent-dir)
      (.mkdirs parent-dir))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; Std i/o macros
;;

(defmacro with-in
  "Executes `body` in a context in which `*in*` is bound to a reader of `s`. See
  See `clojure.java.io/reader` for supported argument types."
  [s & body]
  `(binding [*in* (java.io/reader ~s)]
     ~@body))

(defmacro with-out
  "Executes `body` in a context in which `*out*` is bound to a writer to `s`.
  See `clojure.java.io/writer` for supported argument types."
  [s & body]
  `(binding [*out* (java.io/writer ~s)]
     ~@body))
