(ns veman.serial-com
  (:require [clojure [reflect :as cr]
           [pprint :as pp]])
  (:import [jssc
           SerialPort
           SerialPortException]))

(def port-id "COM3")
;; (pp/print-table (:members (cr/reflect jssc.SerialPort)))

(defn command [port msg cmd]
  (if cmd
    (.writeString port (str cmd "\r"))
    (.writeString port "\r"))
  (loop [port port, so-far ""]
    (let [res (str so-far (.readString port))]
      (if (.endsWith res "\r>")
        (println (str (if msg msg cmd) ": " res))
        (recur port res)))))


(let [port (SerialPort. port-id)]
  (try
    (if (.openPort port)
      (do
        (.setFlowControlMode port SerialPort/FLOWCONTROL_NONE)

        (command port "prompt" nil)
        (command port "version" "ver")
        (command port "id set" "id set 12345678")
        (command port nil "id get")
        (command port nil "gpio set A")
        (command port nil "gpio read A")

        (Thread/sleep 10000)
        (command port nil "gpio clear A")
        (command port nil "gpio read A")

        (Thread/sleep 10000)
        (.closePort port) )
      (println "not opened") )
    (catch SerialPortException ex
      (if (.isOpened port) (.closePort port))
      (println (str "caught exception: " (.getMessage ex)) ))))
