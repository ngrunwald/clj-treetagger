(ns clj-treetagger
    (:import [org.annolab.tt4j TreeTaggerWrapper TokenHandler]))

(defn make-pos-tagger
  [home-path model-path]
  (System/setProperty "treetagger.home" home-path)
  (let [tt (TreeTaggerWrapper.)]
    (doto tt
      (.setModel model-path))))

(defn tag-tokens
  [tagger tokens]
  (let [collect (atom [])]
    (doto tagger
      (.setHandler
       (proxy [TokenHandler] []
         (token [token pos lemma]
           (swap! collect (fn [arr] (conj arr {:token token :pos pos :lemma lemma})))))))
    (.process tagger tokens)
    @collect))

(defn make-pos-tagger-fn
  [home-path model-path]
  (let [tagger (make-pos-tagger home-path model-path)]
    (fn [tokens] (tag-tokens tagger tokens))))
