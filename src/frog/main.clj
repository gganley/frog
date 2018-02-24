(ns frog.main
  (:require
   [clojure.java.io :as io]
   [frog.window :as window])
  (:import
   (java.nio ByteBuffer ByteOrder)
   (javax.sound.sampled
    AudioInputStream
    AudioSystem)
   (org.apache.commons.math3.complex.Complex)
   (org.apache.commons.math3.transform
    DftNormalization
    FastFourierTransformer
    TransformType)))

(defn get-audio-buffer [file-path]

(defn- log2 [val]
  (/ (Math/log val) (Math/log 2)))

(defn- pow2? [val]
  (let [log2-val (log2 val)]
    (== log2-val (int log2-val))))

(defn- pad-to-pow2 [coll]
  (let [big-n (count coll)
        nearest-pow2 (first (drop-while #(> big-n %) (iterate #(* 2 %) 1N)))]
    (take nearest-pow2 (concat coll (repeat 0)))))

  (with-open [in (AudioSystem/getAudioInputStream (io/file file-path))]
    (let [frame-width (.getSampleSizeInBits (.getFormat in))
          buffer (byte-array (* (/ frame-width 8) (.getFrameLength in)))
          short-buffer (short-array (.getFrameLength in))]
      (while (-> in (.read buffer) neg? not))
      (.. (ByteBuffer/wrap buffer) (order ByteOrder/LITTLE_ENDIAN) asShortBuffer (get short-buffer))
      short-buffer)))

(defn fft
  "Fourier transform on X. If X is not a power of 2 then it is padded."
  [x]
  (.transform (FastFourierTransformer. DftNormalization/UNITARY)
              (if (pow2? (count x))
                (double-array x)
                (double-array (pad-to-pow2 x)))
              TransformType/FORWARD))

(defn spectrogram-array
  ([coll n]
   (map fft (partition n coll)))
  ([coll n d]
   (map fft (partition n d coll)))
  ([coll n d omega]
   (pmap (fn [segment] (map #(.multiply %1 %2)
                           (fft segment)
                           (omega segment))) (partition n d coll))))
