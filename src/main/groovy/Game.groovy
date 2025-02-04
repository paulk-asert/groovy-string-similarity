import ai.djl.Application
import ai.djl.repository.zoo.Criteria
import ai.djl.training.util.ProgressBar
import ai.djl.translate.DeferredTranslatorFactory
import info.debatty.java.stringsimilarity.JaroWinkler
import info.debatty.java.stringsimilarity.Levenshtein
import org.apache.commons.codec.language.Metaphone
import org.apache.commons.text.similarity.HammingDistance
import org.apache.commons.text.similarity.JaccardSimilarity
import org.apache.commons.text.similarity.LongestCommonSubsequence
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer

import java.nio.file.Paths

import static java.lang.Math.sqrt

//System.setProperty('org.slf4j.simpleLogger.defaultLogLevel', 'info')

var path = Paths.get(DjlPytorchAngle.classLoader.getResource('UAE-Large-V1.zip').toURI())
var criteria = Criteria.builder()
    .setTypes(String, float[])
    .optModelPath(path)
    .optTranslatorFactory(new DeferredTranslatorFactory())
    .optProgress(new ProgressBar())
    .build()

var angleModel = criteria.loadModel()
var anglePredictor = angleModel.newPredictor()
//var embeddings = sentences.collect(predictor::predict)

double cosineSimilarity(float[] a, float[] b) {
    var dotProduct = a.indices.sum{ a[it] * b[it] }
    var sumSqA = a.toList().sum(n -> n ** 2)
    var sumSqB = b.toList().sum(n -> n ** 2)
    dotProduct / (sqrt(sumSqA) * sqrt(sumSqB))
}

System.setProperty('org.slf4j.simpleLogger.defaultLogLevel', 'info')
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
    ConceptNet: 'conceptnet-numberbatch-17-06-300.bin',
    Glove: 'glove-wiki-gigaword-300.bin',
    FastText: 'fasttext-wiki-news-subwords-300.bin'
].collectValues {
    def p = Paths.get(ConceptNet.classLoader.getResource(it).toURI()).toFile()
    println "Loading model for $it ..."
    WordVectorSerializer.readWord2VecModel(p)
}

var distAlgs = [
    LongestCommonSubsequence: new LongestCommonSubsequence()::apply,
    Hamming: new HammingDistance()::apply,
    Levenshtein: { a, b -> new Levenshtein().distance(a, b).round() },
    Jaccard: { a, b -> "${(100 * new JaccardSimilarity().apply(a, b)).round()}%" },
    JaroWinkler: { a, b -> "${(100 * new JaroWinkler()::similarity(a, b)).round()}%" },
    Phonetic: { a, b ->
        var (sa, sb) = new Metaphone(maxCodeLen: 5).with{ [encode(a), encode(b)] }
        var max = [sa.size(), sb.size()].max()
        var m = new LongestCommonSubsequence().apply(sa, sb)
        "${(100 * m/max).round()}%" },
    Angle: { a, b -> "${(100 * cosineSimilarity(anglePredictor.predict(a), anglePredictor.predict(b))).round()}%" },
    Use: { a, b -> var ans = usePredictor.predict([a, b] as String[]); "${(100 * cosineSimilarity(ans[0], ans[1])).round()}%" }
]
word2vecModels.each { k, v ->
    distAlgs[k] = { String a, String b ->
        var (sa, sb) = k == 'ConceptNet' ? ["/c/en/$a", "/c/en/$b"] : [a, b]
        "${(100 * v.similarity(sa, sb)).round()}%"
    }
}

var hiddenWords = ['coffee', 'chocolate', 'glasses', 'overcoat', 'rhythm', 'onion', 'duck', 'beetroot', 'tricky', 'avalanche'].shuffled()

//println "Hidden word is $hidden"

var console = System.console() ?: System.in.newReader()
while (true) {
    int count = 1
    Set possible = 'a'..'z'
    var hidden = hiddenWords.pop()
    while (true) {
        print "Possible letters: ${possible.join(' ')}\nGuess the hidden word (turn $count): "
        var guess = console.readLine() ?: ''
        if (guess.toLowerCase() in ['quit', 'bye']) {
            println "Sorry, you quit! The hidden word was '$hidden'."
            break
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
                //println ignore.message
            }
            [k, result]
        }
        results.each { k, v ->
            //        var color = v >= 0.8 ? GREEN_TEXT() : RED_TEXT()
            //        println "${k.padRight(40)} ${sprintf '%5.2f', v} ${colorize(bar((v * 20) as int, 0, 40, 20), color)}"
            println "${k.padRight(40)} $v"
        }
        println()
        if (guess == hidden) {
            println "Congratulations, you guess correctly!"
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
