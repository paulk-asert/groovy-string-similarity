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
    WordleLike: this::wordleSimilaritySliding
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
    ['pihsrentrap', 'pihsredael'],
    ['kitten', 'sitting'],
    ['elton john', 'john elton'],
    ['elton john', 'nhoj notle'],
    ['my name is Yoda', 'Yoda my name is'],
    ['the cat sat on the mat', 'the fox jumped over the dog'],
    ['poodles are cute', 'dachshunds are delightful']
]

pairs.each { one, two ->
    var results = simAlgs.collectValues { alg ->
        alg(one, two)
    }
    // display results ...
    println "      $one VS $two"
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
/**
 * Compute standard Wordle distance for equal-length words.
 */
int wordleDistanceEqual(String guess, String target) {
    int n = guess.size()
    def greens = (0..<n).findAll { guess[it] == target[it] }
    int G = greens.size()

    def guessCounts = [:].withDefault { 0 }
    def targetCounts = [:].withDefault { 0 }

    (0..<n).each { i ->
        if (!(i in greens)) {
            guessCounts[guess[i]]++
            targetCounts[target[i]]++
        }
    }

    int Y = guessCounts.collect { k, v -> Math.min(v, targetCounts[k]) }.sum() ?: 0

    return 2 * (n - G) - Y
}

/**
 * Compute Wordle-like distance using sliding window alignment.
 * Extra letters at start/end are treated as gray.
 */
int wordleDistanceSliding(String a, String b) {
    // Identify shorter and longer word
    def shorter = a.size() <= b.size() ? a : b
    def longer = a.size() <= b.size() ? b : a
    int lenShort = shorter.size()
    int lenLong = longer.size()

    int minDistance = Integer.MAX_VALUE

    // Try all possible alignments
    for (int offset = 0; offset <= lenLong - lenShort; offset++) {
        String window = longer[offset..<offset+lenShort]
        int distance = wordleDistanceEqual(shorter, window)

        // Padding cost: each extra letter outside window counts as gray (2)
        int padding = (lenLong - lenShort) * 2
        distance += padding

        minDistance = Math.min(minDistance, distance)
    }

    return minDistance
}

/**
 * Normalized Wordle-like similarity in [0,1].
 * Based on the maximum length of the two words.
 */
double wordleSimilaritySliding(String a, String b) {
    int n = Math.max(a.size(), b.size())
    int D = wordleDistanceSliding(a, b)
    return 1 - (D / (2.0 * n))
}

// Examples
//println "Distance: ${wordleDistanceSliding('CANDY', 'CRANE')}, " +
//    "Similarity: ${wordleSimilaritySliding('CANDY', 'CRANE')}"
//
//println "Distance: ${wordleDistanceSliding('CAN', 'CRANE')}, " +
//    "Similarity: ${wordleSimilaritySliding('CAN', 'CRANE')}"
//
//println "Distance: ${wordleDistanceSliding('CANDIES', 'CRANE')}, " +
//    "Similarity: ${wordleSimilaritySliding('CANDIES', 'CRANE')}"
