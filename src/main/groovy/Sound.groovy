import com.google.refine.clustering.binning.Metaphone3Keyer
import org.apache.commons.codec.language.Caverphone2
import org.apache.commons.codec.language.DoubleMetaphone
import org.apache.commons.codec.language.Metaphone
import org.apache.commons.codec.language.RefinedSoundex
import org.apache.commons.codec.language.Soundex
import org.apache.commons.text.similarity.LevenshteinDistance
import org.apache.commons.text.similarity.LongestCommonSubsequence

import static com.diogonunes.jcolor.Ansi.colorize
import static com.diogonunes.jcolor.Attribute.GREEN_TEXT

var pairs = [
    ['cat', 'hat'],
    ['bear', 'bare'],
    ['pair', 'pare'],
    ['there', 'their'],
    ['sort', 'sought'],
    ['cow', 'bull'],
    ['winning', 'grinning'],
    ['knows', 'nose'],
    ['ground', 'aground'],
    ['peeler', 'repeal'],
    ['hippo', 'hippopotamus'],
]

var pretty = { a, b ->
    var result = "$a|$b".padRight(18)
    a == b ? colorize(result, GREEN_TEXT()) : result
}

var algs = [Soundex: { a, b -> new Soundex().with{pretty(soundex(a), soundex(b)) }},
    RefinedSoundex: { a, b -> new RefinedSoundex().with{ pretty(encode(a), encode(b)) }},
    Metaphone: { a, b -> new Metaphone().with{ pretty(encode(a), encode(b)) }},
    'Metaphone(6)': { a, b -> new Metaphone(maxCodeLen: 6).with{ pretty(encode(a), encode(b)) }},
    Metaphone3: { a, b -> new Metaphone3Keyer().with{ pretty(key(a), key(b)) }},
    'DblMetaphone(6)': { a, b -> new DoubleMetaphone(maxCodeLen: 6).with{ pretty(doubleMetaphone(a), doubleMetaphone(b)) }},
    Caverphone2: { a, b -> new Caverphone2().with{ pretty(encode(a), encode(b)) }},
]

var gameAlgs = [SoundexDiff: { a, b -> "${25 * new Soundex().difference(a, b)}%" },
            Metaphone5LCS: { a, b ->
                var (sa, sb) = new Metaphone(maxCodeLen: 5).with{ [encode(a), encode(b)] }
                var max = [sa.size(), sb.size()].max()
                var m = new LongestCommonSubsequence().apply(sa, sb)
                "${(100 * m/max).round()}%" },
            Metaphone5Lev: { a, b ->
                var (sa, sb) = new Metaphone(maxCodeLen: 5).with{ [encode(a), encode(b)] }
                var max = [sa.size(), sb.size()].max()
                var m = max - new LevenshteinDistance(5).apply(sa, sb)
                "${(100 / max * m).round()}%" },
]

var results = [pairs, algs].combinations().collect { pair, namedAlg ->
    namedAlg.value(pair)
}

display(algs, pairs, results, 18, 22)

results = [pairs, gameAlgs].combinations().collect { pair, namedAlg ->
    namedAlg.value(pair)
}

display(gameAlgs, pairs, results, 14, 24

def display(algs, pairs, r, w1, w2) {
    for (i in 0..algs.size()) {
        if (i) print algs.entrySet()*.key[i - 1].padRight(w1)
        else print 'Pair'.padRight(w2)
    }
    println()

    for (row in 0..<pairs.size()) {
        for (i in 0..algs.size()) {
            if (i) print "${r[(i - 1) * pairs.size() + row]}".padRight(w1)
            else print pairs[row].join('|').padRight(w2)
        }
        println()
    }
}
