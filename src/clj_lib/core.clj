;   Copyright (c) Ryan Wilson. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns clj-lib.core
  "Useful fns, of all purposes, that might be considered language extensions.")

(defn maybe?
  "Returns true if either `val` is nil or `(pred? val)` returns truthful.

  Useful for situations where `nil` is acceptable, but when non-nil `val` is
  expected to meet some criterion.

  Examples:

      (maybe? map? m)
      (maybe? string? s)
  "
  [pred? val]
  {:pre [(ifn? pred?)]}
  (or (nil? val) (pred? val)))

(def not-neg?
  "As named. Equivalent to `(or (pos? v) (zero? v))`."
  (complement neg?))

(defn str=
  "Evaulates equality by converting all args to strings and then testing with
  string equality. Useful when comparing stringy values that may not all be
  strings, such as `1`, `\"1\"`, and `:1`."
  [& args]
  (apply = (map str args)))

(defn between
  "Returns true if `v` is between `min` and `max`, inclusive of both."
  [v min max]
  (and (>= v min) (<= v max)))

(defn round
  "Rounds a number to the specified precision. This is a modified version of the
  round fn used here: http://clojure-doc.org/articles/language/functions.html
  Compared to the reference, the argument order is reversed so it is more easily
  used in partially applied form as a map fn."
  [precision n]
  (let [factor (Math/pow 10 precision)]
    (/ (Math/round (* n factor)) factor)))

(defn avg
  "Returns the average of the nums; a la clj min/max"
  ([x] x)
  ([x y] (/ (+ x y) 2))
  ([x y & more]
    (/ (+ x y (apply + more))
       (+ 2 (count more)))))

(defn min-max
  "returns [(min coll) (max coll)] while iterating coll only once"
  [coll]
  (reduce (fn [result item]
            (if (nil? result)
              [item item]
              (cond-> result
                (< item (first result)) (assoc 0 item)
                (> item (last result)) (assoc 1 item))))
          nil
          coll))

(defn disj-in
  "Roughly equivalent to `(update-in m ks disj elem)`, except that empty sets
  are dissociated."
  [m ks elem]
  {:pre [(map? m) (vector? ks)]}
  (let [new-m (update-in m ks disj elem)]
    (if (empty? (get-in new-m ks))
      (if (= 1 (count ks))
        (dissoc new-m (first ks))
        (update-in new-m (vec (drop-last ks)) dissoc (last ks)))
      new-m)))

(defn map-kv
  "Maps `f` on the vals of `m`, producing a new map with the same keys."
  [f m]
  (reduce-kv #(assoc %1 %2 (f %3)) {} m))

(defn removev
  "Returns vector `v` with index `i` removed.  `v` is nil, returns nil. `i` must
  be a 0-indexed integer between 0 and the length of `v`."
  [v i]
  {:pre [(or (nil? v)
             (and (vector? v)
                  (integer? i)
                  (>= i 0)
                  (< i (count v))))]}
  (when v
    (vec (concat (subvec v 0 i) (subvec v (inc i))))))

(defmacro safe
  "Suppresses any exceptions that are thrown, returning nil instead."
  [& body]
  `(try ~@body (catch Throwable _# nil)))

(defmacro catch-and-return-throwable
  "Tries `f`. If `f` throws a `Throwable`, it is caught and returned."
  [& body] `(try ~@body (catch Throwable t# t#)))

(defn throw-if-throwable
  "Implementation detail.

  If `v` is a Throwable, throws `v`. Otherwise, returns `v`."
  [v] (if (instance? Throwable v) (throw v) v))

(defn retry*
  "Tries to executy `f` up to the number of times specified or until `pred?` returns
  truthful given the result of esecuting `f`. If `f` throws, the thrown value is caught
  and passed to `pred?`.

  This can be used to retry until a non-Exception response, like so:
  ```
  (retry* 5 (complement #(instance? Throwable %)) some-fn)
  ```

  Or, used to retry until a non-nil response, like so:
  ```
  (retry* 5 (complement nil?) some-fn)
  ```

  When `n` retries are reached, the result of the final `f` will be returned or thrown
  if it is throwable, regardless of `pred?`.
  "
  [n pred? f]
  (loop [index (dec n)]
    (let [result (catch-and-return-throwable (f))]
      (if (pred? result)
        result
        (if (zero? index)
          (throw-if-throwable result)
          (recur (dec index)))))))

(defmacro retry
  "Tries to executy `body` up to the number of times specified or until `pred?` returns
  truthful given the result of esecuting `body`."
  [count pred? & body]
  `(retry* ~count ~pred? (fn [] ~@body)))

(defmacro thread
  "Executes `body` in a new thread. Like `future`, except it returns the thread
  instead of the result of `body`."
  [& body]
  `(doto (Thread. (fn [] ~@body))
     (.start)))
