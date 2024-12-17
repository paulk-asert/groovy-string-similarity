import info.debatty.java.stringsimilarity.*

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
println jw.similarity("My string", "My tsring")
// substitution of s and n
println jw.similarity("My string", "My ntrisg")

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
