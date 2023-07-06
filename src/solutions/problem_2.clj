(ns solutions.problem-2
  (:require [clojure.data.json :as json]
            [clj-time.format :as f]
            [clj-time.core :as t]
            [invoice-spec :as s]))

;; GENERAL UTILS
(def date-formatter (f/formatter "dd/MM/yyyy"))

(defn now [] (f/unparse date-formatter (t/now)))

(defn load-json
  "Read invoice from json"
  [path]
  (json/read-str (slurp path) :key-fn keyword)
  )

;; PARSE DATE UTILS
(defn get-issue-date
  [invoice]
  (get-in invoice [:invoice, :issue_date]))

(defn parse-date [date-str]
  (f/parse (f/formatter "dd/MM/yyyy") (or date-str (now))))

;; PARSE CUSTOMER UTILS
(defn get-customer
  [invoice]
  (get-in invoice [:invoice, :customer]))

(defn parse-customer [customer]
  (-> {}
      (assoc :customer/name (or (:company_name customer) "N/N"))
      (assoc :customer/email (or (:email customer) "nomail@mail.com"))
      ))

;; PARSE TAX UTILES
(defn get-taxes
  [item]
  (:taxes item))
(defn parse-tax
  [tax]
  (-> {}
      (assoc :tax/category  (if (= "IVA" (:tax_category tax)) :iva "NA"))
      (assoc :tax/rate  (double (if (= "IVA" (:tax_category tax)) 19 0)))
      ))
(defn parse-taxes
  [item]
  (->> (get-taxes item)
      (map parse-tax)
       vec
   ))

;; PARSE ITEMS UTILS 
(defn get-items
  [invoice]
  (get-in invoice [:invoice :items]))

(defn parse-item
  [item]
  (-> {}
      (assoc :invoice-item/price (double (or (:price item) 0)))
      (assoc :invoice-item/quantity (double (or (:quantity item) 0)))
      (assoc :invoice-item/sku (or (:sku item) "NN"))
      (assoc :invoice-item/taxes (parse-taxes item))
      ))

(defn parse-items
  [invoice]
  (->> (get-items invoice)
       (map parse-item)
       vec
       ))

(defn parse-json-invoice
  [invoice]
  (-> {}
      (assoc :invoice/issue-date (parse-date (get-issue-date invoice)))
      (assoc :invoice/customer (parse-customer (get-customer invoice)))
      (assoc :invoice/items (parse-items invoice))
      ))

(def raw-invoice (load-json "invoice.json"))
(def json-invoice (parse-json-invoice raw-invoice))

(println json-invoice)
(println (s/validate-invoice json-invoice))