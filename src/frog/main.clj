(ns frog.main
  (:require [clojure.java.io :as io])
  (:import (java.nio ByteBuffer ByteOrder)
           (javax.sound.sampled AudioInputStream AudioSystem)
           org.apache.commons.math3.complex.Complex
           (org.apache.commons.math3.transform DftNormalization FastFourierTransformer TransformType)))

(defn get-audio-buffer [file-path]
  (with-open [in (AudioSystem/getAudioInputStream (io/file file-path))]
    (let [frame-width (.getSampleSizeInBits (.getFormat in))
          buffer (byte-array (* (/ frame-width 8) (.getFrameLength in)))
          short-buffer (short-array (.getFrameLength in))]
      (while (-> in (.read buffer) neg? not))
      (.. (ByteBuffer/wrap buffer) (order ByteOrder/LITTLE_ENDIAN) asShortBuffer (get short-buffer))
      short-buffer)))

(defn fft [x]
  (.transform (FastFourierTransformer. DftNormalization/STANDARD)
              (double-array x)
              TransformType/FORWARD))

(defn hann-window [alpha beta big-n]
  (for [little-n (range big-n)]
    (- alpha (* beta (Math/cos (/ (* 2 Math/PI little-n)
                                  big-n))))))

(defn hamming-window [coll]
  (hann-window 0.54 0.46 (count coll)))

(defn spectrogram-array
  ([coll n]
   (map fft (partition n coll)))
  ([coll n d]
   (map fft (partition n d coll)))
  ([coll n d omega]
   (pmap (fn [segment] (map #(.multiply %1 %2)
                           (fft segment)
                           (omega segment))) (partition n d coll))))
