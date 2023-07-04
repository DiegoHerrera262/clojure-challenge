(ns solutions.problem-1
  (:require [clojure.edn :as edn]))

(def invoice (edn/read-string (slurp "invoice.edn")))

(defn
  right-tax?
  [tax-item]
  (->> (= 19 (:tax/rate tax-item))
       (and (= :iva (:tax/category tax-item)))))

(defn
  right-retention?
  [retention-item]
  (->> (= 1 (:retention/rate retention-item))
       (and (= :ret_fuente (:retention/category retention-item)))))

(defn
  has-right-tax?
  [invoice-item]
  (->> (:taxable/taxes invoice-item)
       (filter right-tax?)
       (count)
       (<= 1)))

(defn
  has-right-retention?
  [invoice-item]
  (->> (:retentionable/retentions invoice-item)
       (filter right-retention?)
       (count)
       (<= 1)))

(defn
  is-correct-item?
  [invoice-item]
  (let [has-right-retention (has-right-retention? invoice-item)
        has-right-tax (has-right-tax? invoice-item)]
    (or (and has-right-retention (not has-right-tax)) (and has-right-tax (not has-right-retention)))))

(defn
  filter-invoice
  "Filters invoice items array"
  [invoice]
  (->> (:invoice/items invoice)
       (filter is-correct-item?)))

(println (->> (filter-invoice invoice) (map #(:invoice-item/id %))))

