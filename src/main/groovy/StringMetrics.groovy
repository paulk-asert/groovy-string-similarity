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
    JaroWinkler: new JaroWinkler()::similarity,
    RatcliffObershelp: new RatcliffObershelp()::similarity,
    SorensenDice: new SorensenDice()::similarity,
    Cosine: new Cosine()::similarity,
    'JaccardSimilarity (commons text k=1)': new JaccardSimilarity()::apply,
    JaroWinklerSimilarity: new JaroWinklerSimilarity()::apply
]

var pairs = [
    ['cat', 'hat'],
    ['bear', 'bare'],
    ['pair', 'pear'],
    ['there', 'their'],
    ['sort', 'sought'],
    ['cow', 'bull'],
    ['winning', 'grinning'],
    ['knows', 'nose'],
    ['ground', 'aground'],
    ['peeler', 'repeal'],
    ['hippo', 'hippopotamus'],
    ['my name is Yoda', 'Yoda my name is'],
    ['the cat sat on the mat', 'the fox jumped over the dog'],
    ['poodles are cute', 'dachshunds are delightful']
]

pairs.each {
    showSimilarity(simAlgs, *it)
}

private void showSimilarity(Map algorithms, String... args) {
    println "      ${args.join(' VS ')}"
    var results = algorithms.collectEntries { k, method ->
        [k, method(*args)]
    }
    results.sort{ e -> -e.value }.each { k, v ->
        var color = v >= 0.8 ? GREEN_TEXT() : RED_TEXT()
        println "${k.padRight(40)} ${sprintf '%5.2f', v} ${colorize(bar((v * 20) as int, 0, 20, 20), color)}"
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
    LongestCommonSubsequence: new LongestCommonSubsequence()::distance,
    MetricLCS: new MetricLCS()::distance,
    'NGram(2)': new NGram(2)::distance,
    'NGram(4)': new NGram(4)::distance,
    QGram: new QGram(2)::distance,
    CosineDistance: new CosineDistance()::apply,
    HammingDistance: new HammingDistance()::apply,
    JaccardDistance: new JaccardDistance()::apply,
    JaroWinklerDistance: new JaroWinklerDistance()::apply,
//    LevenshteinDistance: LevenshteinDistance.defaultInstance::apply,
//    LevenshteinDetailedDistance: LevenshteinDetailedDistance.defaultInstance::apply,
    LongestCommonSubsequenceDistance: new LongestCommonSubsequenceDistance()::apply
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
    'Hippopotamus'
]

def sortByDist(distAlgs, words, search) {
    println "          $search"
    distAlgs.collectEntries { name, method ->
        var results = words.collectEntries{ w ->
            var result = Double.MAX_VALUE
            try {
                result = method(w, search)
            } catch(ignore) {
            }
            [w, result]
        }.toSorted{ e -> e.value }
        [name, results.take(3).collect{ k, v -> "$k (${sprintf v instanceof Double ? v < 100 ? '%.2f' : '%5.2e' : '%d', v})" }]
    }.each{ k, v -> println "$k: ${v.join(', ')}" }
    println()
}

sortByDist(distAlgs, phrases, 'The blue car')
sortByDist(distAlgs, phrases, 'The evening sky')
sortByDist(distAlgs, phrases, 'Red roses')
sortByDist(distAlgs, phrases, 'Hippo')
