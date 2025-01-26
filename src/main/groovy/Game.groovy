import ai.djl.repository.zoo.Criteria
import ai.djl.training.util.ProgressBar
import ai.djl.translate.DeferredTranslatorFactory
import info.debatty.java.stringsimilarity.Levenshtein
import org.apache.commons.codec.language.Metaphone
import org.apache.commons.codec.language.Soundex
import org.apache.commons.text.similarity.HammingDistance
import org.apache.commons.text.similarity.JaccardSimilarity
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
    Levenshtein: { a, b -> new Levenshtein().distance(a, b).round() },
    Jaccard: { a, b -> "${(100 * new JaccardSimilarity().apply(a, b)).round()}%" },
    Sound: { a, b -> def (sa, sb) = new Metaphone(maxCodeLen: 8).with{[encode(a), encode(b)] }
        def soundex = 10 * new Soundex().difference(sa, sb)
        def jaccard = 20 * new JaccardSimilarity().apply(sa, sb)
        def lev = 5 * (4 - new Levenshtein().distance(sa, sb))
        def lcs = 40 * (new LongestCommonSubsequence().apply(sa, sb)/(sa.size() + sb.size()))
        "${(soundex + jaccard + lev + lcs).round() }% ${new Soundex().encode(a)} ${new Soundex().encode(b)} $sa $sb $soundex $jaccard $lev $lcs" },
    Angle: { a, b -> "${(100 * cosineSimilarity(predictor.predict(a), predictor.predict(b))).round() }%" }
]

//println new Soundex().difference('navy', 'envy')
//println 4 - new Metaphone().with{new Levenshtein().distance(encode('navy'), encode('envy')) }
//println new Metaphone().encode('navy')
//println new Metaphone().encode('envy')
//var answer = 'peace'
//var guesses = 'piece peas pizza calm place pecan'.split()
//var answer = 'upper'
//var guesses = 'lower udder purer touch higher above capital peruse'.split()
//var answer = 'envy'
//var guesses = 'greed navy green environment envious enviable jealous envy'.split()
//var answer = 'green'
//var guesses = 'rainbow stow braid anger groan grass green'.split()
//var answer = 'steak'
//var guesses = 'aftershock fish meat trace break stake'.split()
var answer = 'pudding'
//var guesses = 'aftershock egg pig pruning pudding'.split() // juice sushi pulling pudding mulling
var guesses = 'aftershock fruit budging bugling buzzing
//var guesses = 'egg aftershock pig pruning pudding'.split()
Set possible = 'a'..'z'

guesses.each {guess ->
    println "      $guess $answer (possible: ${possible.join(' ')})"
    var results = distAlgs.collectEntries { k, method ->
        var result = '-'
        try {
            result = method(guess, answer)
            if (k == 'Jaccard') {
                if (result == '0%') possible -= guess.toSet()
                else if (result == '100%') possible = guess.toSet()
            }
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

