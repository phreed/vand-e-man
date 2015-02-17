(ns veman.serial-com
  (:require [clojure [reflect :as cr]
             [pprint :as pp]])
  (:import [jssc
            SerialPort
            SerialPortException]))


(defn command-er [port msg cmd]
  (if cmd
    (.writeString port (str cmd "\r"))
    (.writeString port "\r"))
  (loop [port port, so-far ""]
    (let [res (str so-far (.readString port))]
      (if (.endsWith res "\r>")
        (str (if msg msg cmd) ": " res)
        (recur port res)))))

(defn command [port msg cmd]
  (if cmd
    (.writeString port (str cmd "\r"))
    (.writeString port "\r"))
  (Thread/sleep 100)
  (let [res (.readString port)]
    (str (if msg msg cmd) ": " res)))

