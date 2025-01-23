import ai.djl.repository.zoo.Criteria
import ai.djl.training.util.ProgressBar
import ai.djl.translate.DeferredTranslatorFactory
import info.debatty.java.stringsimilarity.Damerau
import info.debatty.java.stringsimilarity.Levenshtein
import org.apache.commons.codec.language.Metaphone
import org.apache.commons.codec.language.Soundex
import org.apache.commons.text.similarity.HammingDistance
import org.apache.commons.text.similarity.JaccardDistance
import org.apache.commons.text.similarity.JaccardSimilarity
import org.apache.commons.text.similarity.JaroWinklerDistance
import org.apache.commons.text.similarity.LongestCommonSubsequence

import java.nio.file.Paths

import static java.lang.Math.sqrt

//System.setProperty('org.slf4j.simpleLogger.defaultLogLevel', 'info')

var path = Paths.get(DeepLearning.classLoader.getResource('UAE-Large-V1.zip').toURI())
var criteria = Criteria.builder()
    .setTypes(String, float[])
    .optModelPath(path)
    .optTranslatorFactory(new DeferredTranslatorFactory())
    .optProgress(new ProgressBar())
    .build()

var model = criteria.loadModel()
var predictor = model.newPredictor()
//var embeddings = sentences.collect(predictor::predict)

double cosineSimilarity(float[] a, float[] b) {
    var dotProduct = a.indices.sum{ a[it] * b[it] }
    var sumSqA = a.toList().sum(n -> n ** 2)
    var sumSqB = b.toList().sum(n -> n ** 2)
    dotProduct / (sqrt(sumSqA) * sqrt(sumSqB))
}

var distAlgs = [
    LongestCommonSubsequence: new LongestCommonSubsequence()::apply,
    Hamming: new HammingDistance()::apply,
    Levenshtein: { a, b -> new Levenshtein().distance(a, b).intValue() },
    Jaccard: { a, b -> "${(100 * new JaccardSimilarity().apply(a, b)).intValue()}%" },
    Sound: { a, b ->
        "${(12.5 * (new Soundex().difference(a, b) + 4 - new Metaphone().with{new Levenshtein().distance(encode(a), encode(b))})).intValue() }%" },
    Meaning: { a, b -> "${(100 * cosineSimilarity(predictor.predict(a), predictor.predict(b))).intValue() }%" }
]

var answer = 'peace'
var guesses = 'piece peas pizza calm place pecan'.split()

guesses.each {
    println "      $it $answer"
    var results = distAlgs.collectEntries { k, method ->
        var result = -1
        try {
            result = method(it, answer)
        } catch(ignore) { }
        [k, result]
    }
    results.each { k, v ->
//        var color = v >= 0.8 ? GREEN_TEXT() : RED_TEXT()
//        println "${k.padRight(40)} ${sprintf '%5.2f', v} ${colorize(bar((v * 20) as int, 0, 40, 20), color)}"
        println "${k.padRight(40)} $v"
    }
    println()
}

