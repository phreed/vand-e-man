(ns veman.core-test
  (:require [midje.sweet :as t]
            [veman.core :refer :all]
            [veman.serial-com :as sc])
  (:import [jssc
            SerialPort SerialPortList
            SerialPortException]))


(t/fact
 "I succeed."
 1 => 1)


(def port-id "/dev/ttyACM0")
;; (def port-id "COM3")
;; (pp/print-table (:members (cr/reflect jssc.SerialPort)))

(t/fact "port names"
        (seq (SerialPortList/getPortNames))
        => (list port-id))


(let [port (SerialPort. port-id)]
  (try
    (if (.openPort port)
      (do
        (t/fact "flow control"
                (.setFlowControlMode port SerialPort/FLOWCONTROL_NONE)
                => true)
        (t/fact "parameters"
                (.setParams port SerialPort/BAUDRATE_9600
                            SerialPort/DATABITS_8
                            SerialPort/STOPBITS_1
                            SerialPort/PARITY_NONE)
                => true)

        (t/fact "prompt"
                (sc/command port "prompt" nil)
                => "prompt: \n\r>")

        (t/fact "version"
                (sc/command port "version" "ver")
                => "version: ver\n\r00000008\n\r>")

        (t/fact "id set"
                (sc/command port "id set" "id set 12345678")
                => "id set: id set 12345678\n\r>")

        (t/fact "id get"
                (sc/command port nil "id get")
                => "id get: id get\n\r12345678\n\r>")

        (t/fact "gpio clear 0"
                (sc/command port nil "gpio clear 0")
                => "gpio clear 0: gpio clear 0\n\r>")
        (Thread/sleep 10000)
        ;; at this point the voltage is low ~0.001

        (t/fact "gpio read 0"
                (sc/command port nil "gpio read 0")
                => "gpio read 0: gpio read 0\n\r0\n\r>")

        (t/fact "gpio set 0"
                (sc/command port nil "gpio set 0")
                => "gpio set 0: gpio set 0\n\r>")
        (Thread/sleep 10000)
        ;; at this point the voltage is high ~3.130

        (t/fact "gpio read 0"
                (sc/command port nil "gpio read 0")
                => "gpio read 0: gpio read 0\n\r1\n\r>")

        ;; at this point the voltage is floating ~0.142


        (.closePort port) )
      (println "not opened") )
    (catch SerialPortException ex
      (if (.isOpened port) (.closePort port))
      (println (str "caught exception: " (.getMessage ex)) )))
  (println "completed"))
