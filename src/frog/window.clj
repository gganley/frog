(ns frog.window)

(defn raised-cos [alpha beta]
  (fn ^doubles [coll]
    (let [big-n (count coll)]
      (for [little-n (range big-n)]
        (- alpha (* beta (Math/cos (/ (* 2 Math/PI little-n) (- big-n 1)))))))))

(def hamming
  (raised-cos 0.54 0.46))

(def hann
  (raised-cos 0.5 0.5))
