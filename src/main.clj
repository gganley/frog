(ns main
  (:require [clojure.java.io :as io]
            [clojure.pprint :refer [pprint]]
            [clojure.repl :refer [doc pst source]])
  (:import [javax.sound.sampled
            AudioInputStream
            AudioSystem]
           [java.nio
            ByteBuffer
            ByteOrder]))

;; Create a function that takes a 

(let [buffer (byte-array 2011136)
      short-buffer (short-array 1005568)]
  (with-open [in (AudioSystem/getAudioInputStream (io/file "/Users/gganley/Library/Mobile Documents/com~apple~CloudDocs/Classes/🐸/frog calls/out.wav"))]
    (while (not (neg? (.read in buffer))))
    (print (take 100 buffer) \newline)
    (.. (ByteBuffer/wrap buffer) (order ByteOrder/LITTLE_ENDIAN) asShortBuffer (get short-buffer))
    (print (take 100 short-buffer))))

