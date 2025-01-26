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
    ['there', 'their'],
    ['cow', 'bull'],
    ['winning', 'grinning'],
    ['knows', 'nose'],
    ['ground', 'aground'],
    ['hippo', 'hippopotamus'],
]

var pretty = { a, b ->
    var result = "$a|$b".padRight(22)
    a == b ? colorize(result, GREEN_TEXT()) : result
}

var algs = [Soundex: { a, b -> new Soundex().with{pretty(soundex(a), soundex(b)) }},
    RefinedSoundex: { a, b -> new RefinedSoundex().with{ pretty(encode(a), encode(b)) }},
    SoundexDiff: { a, b -> 4 - new Soundex().difference(a, b) },
    Caverphone2: { a, b -> new Caverphone2().with{ pretty(encode(a), encode(b)) }},
    Metaphone: { a, b -> new Metaphone().with{ pretty(encode(a), encode(b)) }},
    Metaphone8: { a, b -> new Metaphone(maxCodeLen: 8).with{ pretty(encode(a), encode(b)) }},
    Metaphone8LCS: { a, b -> new Metaphone(maxCodeLen: 8).with{ new LongestCommonSubsequence().apply(encode(a), encode(b)) }},
    Metaphone8Lev: { a, b -> new Metaphone(maxCodeLen: 8).with{ new LevenshteinDistance(8).apply(encode(a), encode(b)) }},
    DoubleMetaphone: { a, b -> new DoubleMetaphone().with{ pretty(doubleMetaphone(a), doubleMetaphone(b)) }},
]

var results = [pairs, algs].combinations().collect { pair, namedAlg ->
    namedAlg.value(pair)
}

for (i in 0..algs.size()) {
    if (i) print algs.entrySet()*.key[i-1].padRight(22)
    else print 'Pair'.padRight(22)
}
println()

for (row in 0..<pairs.size()) {
    for (i in 0..algs.size()) {
        if (i) print "${results[(i-1) * pairs.size() + row]}".padRight(22)
        else print pairs[row].join('|').padRight(22)
    }
    println()
}
