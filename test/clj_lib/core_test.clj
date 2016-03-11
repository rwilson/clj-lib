;   Copyright (c) Ryan Wilson. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns clj-lib.core-test
  (:require [clj-lib.core :refer :all]
            [clojure.test :refer :all]))

(deftest test-maybe?
  (is (true? (maybe? map? nil)))
  (is (true? (maybe? map? {})))
  (is (false? (maybe? map? []))))

(deftest test-not-neg?
  (is (true? (not-neg? 1)))
  (is (true? (not-neg? 0)))
  (is (false? (not-neg? -1))))

(deftest test-str=
  (is (false? (= "1000" 1000)))
  (is (true? (str= "1000" 1000)))
  (is (true? (str= "" nil))))

(deftest test-quote-string
  (is (= "" (quote-string nil)))
  (is (= "" (quote-string "")))
  (is (= "\"foo\"" (quote-string "foo")))
  (is (= "\"1\"" (quote-string "1"))))

(deftest test-escape-quotes
  (is (= "quoted \\\"foo bar\\\" baz"
         (escape-quotes "quoted \"foo bar\" baz"))))

(deftest test-between
  (is (between 1 1 1))
  (is (between 2 1 2))
  (is (between 2 1 3))
  (is (not (between 0 1 1)))
  (is (not (between 2 1 1)))
  (is (not (between 1 2 3)))
  (is (not (between 3 1 2))))

(deftest test-round
  (is (= 1.0 (round 1 1)))
  (is (= 1.0 (round 1 1.0)))
  (is (= 1.0 (round 1 1.000001)))
  (is (= 1.2 (round 1 (+ 1.1 0.1)))))

(deftest test-avg
  (is (= 1 (avg 1)))
  (is (= 3 (avg 2 4)))
  (is (= 3 (avg 1 3 5))))

(deftest test-min-max
  (is (= [ 1 10] (min-max (range  1 11))))
  (is (= [-9  9] (min-max (range -9 10))))
  (is (= [1.0 9.8] (min-max (map (partial round 1) (range 1.0 9.9 0.2)))))
  (is (= [1.0 9.5] (min-max (range 1.0 10.0 0.5)))))

(deftest test-disj-in
  ;; test removing element
  (is (= {:a #{1 3}} (disj-in {:a #{1 2 3}} [:a] 2)))
  ;; test removing last element
  (is (= {} (disj-in {:a #{2}} [:a] 2)))
  ;; test removing element in nested map
  (is (= {:a {:b #{1 3}}} (disj-in {:a {:b #{1 2 3}}} [:a :b] 2)))
  ;; test removing last element in nested map
  (is (= {:a {}} (disj-in {:a {:b #{2}}} [:a :b] 2))))

(deftest test-map-kv
  (is (= (map-kv inc {:one 1 :two 2 :three 3})
         {:one 2 :two 3 :three 4})))

(deftest test-removev
  (let [v (vec (range 5))]
    (is (= (removev v 0) [1 2 3 4]))
    (is (= (removev v 2) [0 1 3 4]))
    (is (= (removev v 4) [0 1 2 3]))
    (is (nil? (removev nil 2)))
    (is (thrown? AssertionError (removev v -1)))
    (is (thrown? AssertionError (removev v 5)))
    (is (thrown? AssertionError (removev v :a)))
    (is (thrown? AssertionError (removev v "0")))))

(deftest test-safe
  (is (thrown? Exception (Integer/parseInt nil)))
  (is (nil? (safe (Integer/parseInt nil)))))

(deftest test-catch-and-return-throwable
  (is (nil? (catch-and-return-throwable nil)))
  (is (= 1 (catch-and-return-throwable 1)))
  (is (= "foo" (catch-and-return-throwable "foo")))
  (is (= java.lang.Exception
         (type (catch-and-return-throwable (throw (Exception. "foo")))))))

(deftest test-throw-if-throwable
  (is (nil? (throw-if-throwable nil)))
  (is (= 1 (throw-if-throwable 1)))
  (is (= "foo" (throw-if-throwable "foo")))
  (is (thrown? RuntimeException (throw-if-throwable (RuntimeException. "foo")))))

(deftest test-retry*
  (let [v (retry* Integer/MAX_VALUE #(> % 0.5) rand)]
    (is (> v 0.5)))

  (let [;; define a counter that we will use to track the number of times our
        ;; retriable fn has been called
        counter (atom 0)]

    (let [v (retry* 2
                    (fn [r]
                      (swap! counter inc)
                      false)
                    (constantly nil))]
      (is (nil? v))
      (is (= 2 @counter)))

    ;; reset!
    (reset! counter 0)

    ;; define a retriable test fn that will return successfully after being
    ;; called a set number of times
    (let [retry-count 5
          not-throwable? (complement #(instance? Throwable %))
          throw-ex (fn [] (throw (Exception. "Not at target")))
          retry-fn (fn [target]
                     (if (= target @counter)
                       target
                       (do
                         (swap! counter inc)
                         (throw-ex))))]
      ;; Test that the retry* fn can return successfully at each valid retry count
      (dotimes [n retry-count]
        (reset! counter 0)
        (is (= n (retry* retry-count
                         not-throwable?
                         #(retry-fn n)))
            (str "Failed retry* index: " n)))

      ;; Test that the retry* fn will re-throw the last thrown Exception if every
      ;; retry throws an Exception
      (reset! counter 0)
      (is (thrown-with-msg? Exception #"Not at target"
                            (retry* retry-count
                                    (complement #(instance? Throwable %))
                                    throw-ex))))))

(deftest test-thread
  (let [a (atom false)
        t (thread (reset! a true))]
    (is (instance? java.lang.Thread t))
    (while (.isAlive t)
      (Thread/sleep 1))
    (is (true? @a))))

(defdata data "test-data/data.edn")

(deftest test-defdata
  (is (= (:foo data) "bar"))
  (is (= (:baz data) 1)))
