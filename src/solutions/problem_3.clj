(ns solutions.problem-3
  (:require [clojure.test :refer [deftest is run-tests testing]]
            [invoice-item :as ii]))

((deftest base-case
   (testing "Test working in normal conditions"
     (let [item {:invoice-item/precise-quantity 5
                 :invoice-item/precise-price 10
                 :invoice-item/discount-rate 1}
           subtotal (ii/subtotal item)] (is (= 49.5  subtotal))))))

((deftest missing-discount
   (testing "Missing discount"
     (let [item {:invoice-item/precise-quantity 5
                 :invoice-item/precise-price 10}
           subtotal (ii/subtotal item)] (is (= 50.0  subtotal))))))

((deftest missing-precise-quantities
   (testing "Missing precise quantities"
     (let [item-q {:invoice-item/precise-price 10}
           subtotal-q (ii/subtotal item-q)
           item-p {:invoice-item/precise-quantity 10}
           subtotal-p (ii/subtotal item-p)]
       (is (= 0.0  subtotal-p))
       (is (= 0.0  subtotal-q))))))

((deftest bad-type-precise-quantities
   (testing "Bad type of precise quantities"
     (let [item-q {:invoice-item/precise-price 10
                   :invoice-item/precise-quantity "5"}
           subtotal-q (ii/subtotal item-q)
           item-p {:invoice-item/precise-quantity 10
                   :invoice-item/precise-price "5"}
           subtotal-p (ii/subtotal item-p)]
       (is (= 50.0  subtotal-p))
       (is (= 50.0  subtotal-q))))))

((deftest bad-type-discount
   (testing "Bad type of precise quantities"
     (let [item {:invoice-item/precise-price 10
                 :invoice-item/precise-quantity 5
                 :invoice-item/discount-rate "1"}
           subtotal (ii/subtotal item)]
       (is (= 49.5  subtotal))))))

((deftest negative-discount
   (testing "Ignores negative discounts"
     (let [item {:invoice-item/precise-price 10
                 :invoice-item/precise-quantity 5
                 :invoice-item/discount-rate -1}
           subtotal (ii/subtotal item)]
       (is (= 50 subtotal))))))

((deftest negative-precise-quantities
   (testing "Truncates negative precise quantities"
     (let [item-q {:invoice-item/precise-price 10
                   :invoice-item/precise-quantity -5
                   :invoice-item/discount-rate 1}
           subtotal-q (ii/subtotal item-q)
           item-p {:invoice-item/precise-quantity -10
                   :invoice-item/precise-price 5
                   :invoice-item/discount-rate 1}
           subtotal-p (ii/subtotal item-p)]
       (is (= 0.0  subtotal-p))
       (is (= 0.0  subtotal-q))))))

(run-tests)