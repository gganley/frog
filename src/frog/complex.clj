(ns frog.complex
  (:import org.apache.commons.math3.complex.Complex))

(defn c+ ^org.apache.commons.math3.complex.Complex [^org.apache.commons.math3.complex.Complex x ^org.apache.commons.math3.complex.Complex y]
  (.add x y))

(defn c* ^org.apache.commons.math3.complex.Complex [^org.apache.commons.math3.complex.Complex x ^org.apache.commons.math3.complex.Complex y]
  (.multiply x y))

(defn c-div ^org.apache.commons.math3.complex.Complex [^org.apache.commons.math3.complex.Complex x ^org.apache.commons.math3.complex.Complex y]
  (.divide x y))

(defn c- ^org.apache.commons.math3.complex.Complex [^org.apache.commons.math3.complex.Complex x ^org.apache.commons.math3.complex.Complex y]
  (.subtract x y))

(defn |c| ^double [^org.apache.commons.math3.complex.Complex x]
  (.abs x))
