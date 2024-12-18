import info.debatty.java.stringsimilarity.*
import org.apache.commons.codec.language.Soundex
import org.apache.commons.text.similarity.*

import static com.diogonunes.jcolor.Ansi.colorize
import static com.diogonunes.jcolor.Attribute.GREEN_TEXT
import static com.diogonunes.jcolor.Attribute.RED_TEXT
import static org.codehaus.groovy.util.StringUtil.bar

var algorithms = [
    NormalizedLevenshtein: new NormalizedLevenshtein()::similarity,
    Jaccard: new Jaccard(1)::similarity,
    'Jaccard(2)': new Jaccard(2)::similarity,
    JaroWinkler: new JaroWinkler()::similarity,
    RatcliffObershelp: new RatcliffObershelp()::similarity,
    SorensenDice: new SorensenDice()::similarity,
    Cosine: new Cosine()::similarity,
    JaccardSimilarity: new JaccardSimilarity()::apply,
    JaroWinklerSimilarity: new JaroWinklerSimilarity()::apply
]

var pairs = [
    ['cat', 'hat'],
    ['bear', 'bare'],
    ['there', 'their'],
    ['cow', 'bull']
]

pairs.each {
    showSimilarity(algorithms, *it)
}

private void showSimilarity(Map algorithms, String... args) {
    println "      ${args.join(' VS ')}"
    var results = algorithms.collectEntries { k, method ->
        [k, method(*args)]
    }
    results.sort{ e -> -e.value }.each { k, v ->
        var color = v >= 0.8 ? GREEN_TEXT() : RED_TEXT()
        println "${k.padRight(25)} ${sprintf '%5.2f', v} ${colorize(bar((v * 20) as int, 0, 20, 20), color)}"
    }
    println()
}

var nl = new NormalizedLevenshtein()
println nl.distance('My string', 'My $tring')

// The cost for substituting 't' and 'r' is considered smaller
// as these 2 are located next to each other on a keyboard
var wl = new WeightedLevenshtein({ char c1, char c2 ->
    c1 == 't' && c2 == 'r' ? 0.5 : 1.0
})
println wl.distance("String1", "Srring2")

var d = new Damerau()
// 1 substitution
println d.distance("ABCDEF", "ABDCEF")
// 2 substitutions
println d.distance("ABCDEF", "BACDFE")
// 1 deletion
println d.distance("ABCDEF", "ABCDE")
println d.distance("ABCDEF", "BCDEF")
println d.distance("ABCDEF", "ABCGDEF")
// All different
println d.distance("ABCDEF", "POIU")

var osa = new OptimalStringAlignment()
println osa.distance("CA", "ABC")

var jw = new JaroWinkler()
// substitution of s and t
println '----\n' + jw.similarity("My string", "My tsring")
// substitution of s and n
println jw.similarity("My string", "My ntrisg")
jw = new JaroWinklerSimilarity()
println jw.apply("My string", "My tsring")
// substitution of s and n
println jw.apply("My string", "My ntrisg")
println '----'

var lcs = new LongestCommonSubsequence()
println lcs.distance("AGCAT", "GAC") // 4.0
println lcs.distance("AGCAT", "AGCT") // 1.0

var mlcs = new MetricLCS()
String s1 = "ABCDEFG"
String s2 = "ABCDEFHJKL"
// LCS: ABCDEF => length = 6
// longest = s2 => length = 10
// => 1 - 6/10 = 0.4
println mlcs.distance(s1, s2)
// LCS: ABDF => length = 4
// longest = ABDEF => length = 5
// => 1 - 4 / 5 = 0.2
println mlcs.distance("ABDEF", "ABDIF")

var twogram = new NGram(2)
println twogram.distance("ABCD", "ABTUIO") // 0.583333
String s10 = "Adobe CreativeSuite 5 Master Collection from cheap 4zp"
String s11 = "Adobe CreativeSuite 5 Master Collection from cheap d1x"
var ngram = new NGram(4)
println ngram.distance(s10, s11) // 0.97222

var dig = new QGram(2)
// AB BC CD CE
// 1  1  1  0
// 1  1  0  1
// Total: 2
println dig.distance("ABCD", "ABCE")

String s20 = "My first string"
String s21 = "My other string..."
var cosine = new Cosine(2) // sequences of 2 char
// Pre-compute the profile of strings
var profile1 = cosine.getProfile(s20)
var profile2 = cosine.getProfile(s21)
println cosine.similarity(profile1, profile2) // 0.516185

var sdx = new Soundex()
pairs.each { a, b ->
    var sa = sdx.soundex(a)
    var sb = sdx.soundex(b)
    var color = sa == sb ? GREEN_TEXT() : RED_TEXT()
    println a.padRight(10) +
        colorize(sa.padRight(10), color) +
        b.padRight(10) +
        colorize(sb.padRight(10), color)
}
