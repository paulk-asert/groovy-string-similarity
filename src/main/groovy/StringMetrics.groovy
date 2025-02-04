import info.debatty.java.stringsimilarity.*
import org.apache.commons.text.similarity.*

import static com.diogonunes.jcolor.Ansi.colorize
import static com.diogonunes.jcolor.Attribute.GREEN_TEXT
import static com.diogonunes.jcolor.Attribute.RED_TEXT
import static org.codehaus.groovy.util.StringUtil.bar

var simAlgs = [
    NormalizedLevenshtein: new NormalizedLevenshtein()::similarity,
    'Jaccard (debatty k=1)': new Jaccard(1)::similarity,
    'Jaccard (debatty k=2)': new Jaccard(2)::similarity,
    'Jaccard (debatty k=3)': new Jaccard()::similarity,
    'Jaccard (commons text k=1)': new JaccardSimilarity()::apply,
    'JaroWinkler (debatty)': new JaroWinkler()::similarity,
    'JaroWinkler (commons text)': new JaroWinklerSimilarity()::apply,
    RatcliffObershelp: new RatcliffObershelp()::similarity,
    SorensenDice: new SorensenDice()::similarity,
    Cosine: new Cosine()::similarity,
]

var pairs = [
    ['cat', 'hat'],
    ['cat', 'kitten'],
    ['cat', 'dog'],
    ['bear', 'bare'],
    ['bear', 'bean'],
    ['pair', 'pear'],
    ['there', 'their'],
    ['sort', 'sought'],
    ['cow', 'bull'],
    ['cow', 'cowbell'],
    ['winners', 'grinners'],
    ['knows', 'nose'],
    ['ground', 'aground'],
    ['grounds', 'aground'],
    ['peeler', 'repeal'],
    ['hippo', 'hippopotamus'],
    ['superstar', 'supersonic'],
    ['partnership', 'leadership'],
    ['elton john', 'john elton'],
    ['elton john', 'nhoj notle'],
    ['my name is Yoda', 'Yoda my name is'],
    ['the cat sat on the mat', 'the fox jumped over the dog'],
    ['poodles are cute', 'dachshunds are delightful']
]

pairs.each {wordPair ->
    var results = simAlgs.collectValues { method ->
        method(*wordPair)
    }
    // display results ,,,
    println "      ${wordPair.join(' VS ')}"
    results.sort{ e -> -e.value }.each { k, v ->
        var color = v >= 0.8 ? GREEN_TEXT() : RED_TEXT()
        println "${k.padRight(30)} ${sprintf '%5.2f', v} ${colorize(bar((v * 20) as int, 0, 20, 20), color)}"
    }
    println()
}

var distAlgs = [
    NormalizedLevenshtein: new NormalizedLevenshtein()::distance,
    'WeightedLevenshtein (t is near r)': new WeightedLevenshtein({ char c1, char c2 ->
        c1 == 't' && c2 == 'r' ? 0.5 : 1.0
    })::distance,
    Damerau: new Damerau()::distance,
    OptimalStringAlignment: new OptimalStringAlignment()::distance,
//    'LongestCommonSubsequence (debatty)': new LongestCommonSubsequence()::distance,
    MetricLCS: new MetricLCS()::distance,
    'NGram(2)': new NGram(2)::distance,
    'NGram(4)': new NGram(4)::distance,
    QGram: new QGram(2)::distance,
    CosineDistance: new CosineDistance()::apply,
    HammingDistance: new HammingDistance()::apply,
    JaccardDistance: new JaccardDistance()::apply,
    JaroWinklerDistance: new JaroWinklerDistance()::apply,
    LevenshteinDetailedDistance: { a, b -> LevenshteinDetailedDistance.defaultInstance.apply(a, b).toString() },
    LevenshteinDistance: LevenshteinDistance.defaultInstance::apply,
    'LongestCommonSubsequenceDistance  (commons text)': new LongestCommonSubsequenceDistance()::apply,
    'LongestCommonSubsequence (commons text)': new org.apache.commons.text.similarity.LongestCommonSubsequence()::apply,
]

var phrases = [
    'The sky is blue',
    'The blue sky',
    'The blue cat',
    'The sea is blue',
    'Blue skies following me',
    'My ferrari is red',
    'Apples are red',
    'I read a book',
    'The wind blew',
    'Numbers are odd or even',
    'Red noses',
    'Read knows',
    'Hippopotamus',
//    'grounds',
//    'grinder',
//    'grounded',
//    'aground'
]

var wordDistAlgs = [
    LongestCommonSubsequence: new org.apache.commons.text.similarity.LongestCommonSubsequence()::apply,
    Hamming: new HammingDistance()::apply,
    LevenshteinDetails: { a, b -> LevenshteinDetailedDistance.defaultInstance.apply(a, b).toString() },
    Levenshtein: LevenshteinDistance.defaultInstance::apply,
]

def sortByDist(distAlgs, words, search) {
    println "          $search"
    distAlgs.collectEntries { name, method ->
        var results = words.collectEntries{ w ->
            var result = '-'
            try {
                result = method(w, search)
            } catch(ignore) {
            }
            [w, result]
        }.toSorted{ e -> e.value }
        [name, results.take(3).collect{ k, v -> "$k (${sprintf v instanceof String ? '%s' : v instanceof Double ? v < 100 ? '%.2f' : '%5.2e' : '%d', v})" }]
    }.each{ k, v -> println "$k: ${v.join(', ')}" }
    println()
}

sortByDist(distAlgs, phrases, 'The blue car')
sortByDist(distAlgs, phrases, 'The evening sky')
sortByDist(distAlgs, phrases, 'Red roses')
sortByDist(distAlgs, phrases, 'Hippo')
sortByDist(distAlgs, phrases, 'aground')

displayAll(wordDistAlgs, pairs)

def displayAll(algs, pairs) {
    pairs.each { pair ->
        println "${pair.join(' vs ')}: "
        algs.collectEntries { name, method ->
            var results = pair.collectEntries { w ->
                var result = '-'
                try {
                    result = method(*pair)
                } catch (ignore) {
                }
                [name, result]
            }
        }.each { k, v -> print "$k ($v) " }
        println()
    }
    println()
}
