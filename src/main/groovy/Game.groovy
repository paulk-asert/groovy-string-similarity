import ai.djl.Application
import ai.djl.repository.zoo.Criteria
import ai.djl.training.util.ProgressBar
import ai.djl.translate.DeferredTranslatorFactory
import info.debatty.java.stringsimilarity.JaroWinkler
import info.debatty.java.stringsimilarity.NormalizedLevenshtein
import org.apache.commons.codec.language.Metaphone
import org.apache.commons.codec.language.Soundex
import org.apache.commons.text.similarity.JaccardSimilarity
import org.apache.commons.text.similarity.LevenshteinDetailedDistance
import org.apache.commons.text.similarity.LongestCommonSubsequence
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer

import java.nio.file.Paths

import static java.lang.Math.sqrt

System.setProperty('org.slf4j.simpleLogger.defaultLogLevel', 'info')

var debug = true
var path = Paths.get(DjlPytorchAngle.classLoader.getResource('UAE-Large-V1.zip').toURI())
var criteria = Criteria.builder()
    .setTypes(String, float[])
    .optModelPath(path)
    .optTranslatorFactory(new DeferredTranslatorFactory())
    .optProgress(new ProgressBar())
    .build()

var angleModel = criteria.loadModel()
var anglePredictor = angleModel.newPredictor()

double cosineSimilarity(float[] a, float[] b) {
    var dotProduct = a.indices.sum{ a[it] * b[it] }
    var sumSqA = a.toList().sum(n -> n ** 2)
    var sumSqB = b.toList().sum(n -> n ** 2)
    dotProduct / (sqrt(sumSqA) * sqrt(sumSqB))
}

String modelUrl = "https://storage.googleapis.com/tfhub-modules/google/universal-sentence-encoder/4.tar.gz"

var useCriteria = Criteria.builder()
    .optApplication(Application.NLP.TEXT_EMBEDDING)
    .setTypes(String[], float[][])
    .optModelUrls(modelUrl)
    .optTranslator(new UseTranslator())
    .optEngine("TensorFlow")
    .optProgress(new ProgressBar())
    .build()

var useModel = useCriteria.loadModel()
var usePredictor = useModel.newPredictor()

var word2vecModels = [
    Glove: 'glove-wiki-gigaword-300.bin',
    FastText: 'fasttext-wiki-news-subwords-300.bin',
    ConceptNet: 'conceptnet-numberbatch-17-06-300.bin',
].collectValues {
    def p = Paths.get(ConceptNet.classLoader.getResource(it).toURI()).toFile()
    println "Loading model for $it ..."
    WordVectorSerializer.readWord2VecModel(p)
}

var soundex = new Soundex()
var jw = new JaroWinkler()
var distAlgs = [
    LongestCommonSubsequence: new LongestCommonSubsequence()::apply,
    Levenshtein: { a, b -> LevenshteinDetailedDistance.defaultInstance.apply(a, b).toString() },
    Jaccard: { a, b ->
        if (debug) {
            var sa = a.toSet(); var sb = b.toSet()
            var top = sa.intersect(sb).size(); var bottom = sa.plus(sb).size()
            "${(100 * new JaccardSimilarity().apply(a, b)).round()}%  ($top/$bottom)"
        } else {
            "${(100 * new JaccardSimilarity().apply(a, b)).round()}%"
        }
    },
    JaroWinkler: { a, b ->
        var fwd = (100 * jw.similarity(a, b)).round()
        var rev = (100 * jw.similarity(a.reverse(), b.reverse())).round()
        "PREFIX $fwd% / SUFFIX $rev%"
    },
    Phonetic: { a, b ->
        var sDiff = soundex.difference(a, b)
        var (ma, mb) = new Metaphone(maxCodeLen: 10).with{ [encode(a), encode(b)] }
        var max = [ma.size(), mb.size()].max()
        var lcs = new LongestCommonSubsequence().apply(ma, mb)
        var jws = jw.similarity(ma, mb)
        "Metaphone=$ma ${(50 * lcs/max + 50 * jws).round()}% / Soundex=${soundex.encode(a)} ${sDiff * 25}%" },
    Meaning: { a, b ->
        var ang = (100 * cosineSimilarity(anglePredictor.predict(a), anglePredictor.predict(b))).round()
        var uData = usePredictor.predict([a, b] as String[])
        var use = (100 * cosineSimilarity(uData[0], uData[1])).round()
        var cNet = (100 * word2vecModels.ConceptNet.similarity("/c/en/$a", "/c/en/$b")).round()
        var glv = (100 * word2vecModels.Glove.similarity(a, b)).round()
        var ft = (100 * word2vecModels.FastText.similarity(a, b)).round()
        "Angle $ang% / Use $use% / ConceptNet $cNet% / Glove $glv% / FastText $ft%"
    }
]

var hiddenWords = ['carrot','elevator', 'kangaroo', 'banana', 'kumquat', 'elephant', 'pudding']//['kumquat', 'pudding', 'elevator', 'book', 'elephant', 'rhythm', 'onion', 'telescope', 'beetroot', 'tricky', 'avalanche', 'submarine'].shuffled()

var console = System.console() ?: System.in.newReader()
var lev = new NormalizedLevenshtein()
while (true) {
    int count = 1
    Set possible = 'a'..'z'
    var hidden = hiddenWords.pop()
    if (debug) println "Hidden word is $hidden"
    var cClues = word2vecModels.ConceptNet.wordsNearest("/c/en/$hidden", 200)
        .findAll{ it.startsWith('/c/en/') }
        .collect{ it - '/c/en/' }
        .findAll{ lev.similarity(it, hidden) < 0.8 }
    var gClues = word2vecModels.Glove.wordsNearest(hidden, 20)
        .findAll{ lev.similarity(it, hidden) < 0.8 }
    var fClues = word2vecModels.FastText.wordsNearest(hidden, 20)
        .findAll{ lev.similarity(it, hidden) < 0.8 }
    def groupedClues = [:]
    groupedClues[1] = (cClues.drop(4).take(4) + gClues.drop(4).take(4) + fClues.drop(4).take(4))
        .findAll{ !it.contains(hidden) && lev.similarity(it, hidden) < 0.25 }
        .unique().sort{ lev.similarity(it, hidden) }.take(2).toSet()
    groupedClues[2] = ((cClues.drop(2).take(6) + gClues.drop(2).take(6) + fClues.drop(2).take(6))
        .findAll{ !it.contains(hidden) && lev.similarity(it, hidden) < 0.5 }
        .unique().sort{ lev.similarity(it, hidden) }.toSet() - groupedClues[1]).take(3)
    groupedClues[3] = ((cClues.take(6) + gClues.take(6) + fClues.take(6))
        .findAll{ !it.contains(hidden) }
        .unique().sort{ -lev.similarity(it, hidden) }.toSet() - groupedClues[1] - groupedClues[2]).take(4)
    while (true) {
        print "Possible letters: ${possible.join(' ')}\nGuess the hidden word (turn $count): "
        var guess = console.readLine() ?: ''
        if (guess.trim().toLowerCase() in ['quit', 'bye']) {
            println "Sorry, you quit! The hidden word was '$hidden'."
            break
        }
        if (guess.trim().toLowerCase() == 'restart') {
            println "Restarting."
            count = 1
            possible = ('a'..'z').toSet()
            continue
        }
        var results = distAlgs.collectEntries { k, method ->
            var result = '-'
            try {
                result = method(guess, hidden)
                if (k == 'Jaccard') {
                    if (result == '0%') possible -= guess.toSet()
                    else if (result == '100%') possible = guess.toSet()
                }
            } catch (ignore) {
                if (debug) println ignore.message
            }
            [k, result]
        }
        results.each { k, v ->
            //        var color = v >= 0.8 ? GREEN_TEXT() : RED_TEXT()
            //        println "${k.padRight(40)} ${sprintf '%5.2f', v} ${colorize(bar((v * 20) as int, 0, 40, 20), color)}"
            println "${k.padRight(30)} $v"
        }
        println()
        if (count % 8 == 0) {
            def clue = groupedClues[count.intdiv(8)]
            if (clue) println "You seem to be having trouble, here are one or more clues: ${clue.join(', ')}"
        }
        if (guess == hidden) {
            println "Congratulations, you guessed correctly!"
            break
        }
        if (count++ == 30) {
            println "Sorry, you took too many turns! The hidden word was '$hidden'."
            break
        }
    }
    if (!hiddenWords) break
    print '\nPlay again [Y/n]? '
    if (console.readLine().trim().toLowerCase().toList()[0] == 'n') break
}
