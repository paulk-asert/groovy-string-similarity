import com.google.refine.clustering.binning.Metaphone3Keyer
import org.apache.commons.codec.language.Caverphone2
import org.apache.commons.codec.language.DaitchMokotoffSoundex
import org.apache.commons.codec.language.DoubleMetaphone
import org.apache.commons.codec.language.Metaphone
import org.apache.commons.codec.language.Nysiis
import org.apache.commons.codec.language.RefinedSoundex
import org.apache.commons.codec.language.Soundex
import org.apache.commons.text.similarity.LevenshteinDistance
import org.apache.commons.text.similarity.LongestCommonSubsequence

import static com.diogonunes.jcolor.Ansi.colorize
import static com.diogonunes.jcolor.Attribute.GREEN_TEXT
import static java.lang.Math.max

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
var labelWidth = pairs.collect{ a, b -> a.size() + b.size() }.max() + 3

var pretty = { s, b ->
    b ? colorize(s, GREEN_TEXT()) : s
}

var result = { a, b -> ["$a|$b", a == b] }
var gameResult = { a -> ["$a%", a >= 80] }

var soundex = [
    Soundex: { a, b -> new Soundex().with{result(soundex(a), soundex(b)) }},
    RefinedSoundex: { a, b -> new RefinedSoundex().with{ result(encode(a), encode(b)) }},
    DaitchMokotoffSoundex: { a, b -> new DaitchMokotoffSoundex().with{ result(encode(a), encode(b)) }},
]

var metaphone = [
    Metaphone: { a, b -> new Metaphone().with{ result(encode(a), encode(b)) }},
    'Metaphone(8)': { a, b -> new Metaphone(maxCodeLen: 8).with{ result(encode(a), encode(b)) }},
    'DblMetaphone(8)': { a, b -> new DoubleMetaphone(maxCodeLen: 8).with{ result(doubleMetaphone(a), doubleMetaphone(b)) }},
    Metaphone3: { a, b -> new Metaphone3Keyer().with{ result(key(a), key(b)) }},
]

var other = [
    Nysiis: { a, b -> new Nysiis().with{ result(encode(a), encode(b)) }},
    Caverphone2: { a, b -> new Caverphone2().with{ result(encode(a), encode(b)) }},
]

var gameAlgs = [
    SoundexDiff: { a, b -> gameResult(25 * new Soundex().difference(a, b)) },
    Metaphone5LCS: { a, b ->
        var (sa, sb) = new Metaphone(maxCodeLen: 5).with{ [encode(a), encode(b)] }
        var max = [sa.size(), sb.size()].max()
        var m = new LongestCommonSubsequence().apply(sa, sb)
        gameResult((100 * m/max).round()) },
    Metaphone5Lev: { a, b ->
        var (sa, sb) = new Metaphone(maxCodeLen: 5).with{ [encode(a), encode(b)] }
        var max = [sa.size(), sb.size()].max()
        var m = max - new LevenshteinDistance(5).apply(sa, sb)
        gameResult((100 / max * m).round()) },
]

def run() {
    runAndDisplay(soundex, 22)
    runAndDisplay(metaphone, 22)
    runAndDisplay(other, 22)
    runAndDisplay(gameAlgs, 14)
}

def runAndDisplay(a, w1) {
    var results = [pairs, a].combinations().collect { pair, namedAlg -> namedAlg.value(pair) }
    var colWidth = max(a.keySet()*.size().max(), results*.get(0)*.size().max()) + 2
    display(a, pairs, results, colWidth)
    println()
}

def display(algs, pairs, r, w1) {
    for (i in 0..algs.size()) {
        if (i) print algs.entrySet()*.key[i - 1].padRight(w1)
        else print 'Pair'.padRight(labelWidth)
    }
    println()

    for (row in 0..<pairs.size()) {
        for (i in 0..algs.size()) {
            def (s, b) = r[(i - 1) * pairs.size() + row]
            if (i) print pretty(s.padRight(w1), b)
            else print pairs[row].join('|').padRight(labelWidth)
        }
        println()
    }
}
