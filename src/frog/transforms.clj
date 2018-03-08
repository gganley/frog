(ns frog.transforms
  (:require [frog.complex :refer [c* |c|]]
            [frog.util :refer [overlap pad-to-pow2 pow2?]])
  (:import [org.apache.commons.math3.transform DftNormalization FastFourierTransformer TransformType]))

(defn fft
  "Fourier transform on X. If X is not a power of 2 then it is padded."
  [x]
  (.transform (FastFourierTransformer. DftNormalization/UNITARY)
              (if (pow2? (count x))
                (double-array x)
                (double-array (pad-to-pow2 x)))
              TransformType/FORWARD))

(defn ifft
  [x]
  (.transform (FastFourierTransformer. DftNormalization/UNITARY)
              (if (pow2? (count x))
                (into-array x)
                (into-array (pad-to-pow2 x)))
              TransformType/INVERSE))

(defn- convolution [xs ys]
  (map |c| (ifft (map c* (fft xs) (fft ys)))))


(defn histogram
  "Creates a histogram of COLL.
  Creates a seq where empty pockets are filled with 0.
  Does not attempt to make values of COLL integers"
  [coll]
  (map second (merge (zipmap (range (count coll)) (repeat 0))
                     (frequencies coll))))

(defn spectrogram-array
  ([coll window-size]
   (map fft (partition window-size coll)))
  ([coll window-size overlap-rate]
   (map fft (partition window-size (overlap coll overlap-rate) coll)))
  ([coll window-size overlap-rate wfun]
   (map (fn [segment] (map #(.multiply %1 %2)
                          (fft segment)
                          (wfun segment)))
        (partition window-size (overlap coll overlap-rate) coll))))
