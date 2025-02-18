import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import org.deeplearning4j.models.word2vec.Word2Vec

import java.nio.file.Paths

var queries = ['bull', 'bovine', 'kitten',
               'hay', 'cow', 'cat', 'dog', 'grass']

//var modelName = 'fasttext-wiki-news-subwords-300.bin'
var modelName = 'conceptnet-numberbatch-17-06-300.bin'
//var modelName = 'glove-wiki-gigaword-300.bin'
var path = Paths.get(ConceptNet.classLoader.getResource(modelName).toURI()).toFile()
Word2Vec model = WordVectorSerializer.readWord2VecModel(path)
/*queries.each { a ->
    var results = (queries - a).collectEntries{ b -> [b, w2vModel.similarity("/c/en/$a", "/c/en/$b")] }.sort{-it.value }.take(3)
    println "$a: ${results.collect{ k, v -> k + sprintf(' (%.2f) ', v) }.join()}"
    println "$a: ${w2vModel.wordsNearest("/c/en/$a", 3)}"
}*/

//String[] words = ['cow', 'bull', 'calf', 'bovine', 'cattle', 'livestock', 'cat', 'kitten', 'feline',
//                  'hippo', 'bear', 'bare', 'milk', 'water', 'grass', 'green']
String[] words = ['/c/en/cow', '/c/en/bull', '/c/en/calf', '/c/en/bovine', '/c/fr/bovin', '/c/fr/vache', '/c/fr/taureau', '/c/de/kuh', '/c/en/kitten', '/c/en/cat', '/c/de/katze']

//words.eachWithIndex { w, i ->
println """ConceptNet similarity to /c/en/cow: ${
    words
        .collectEntries { ["/c/en/$it", model.similarity('/c/en/cow', "/c/en/$it")] }
        .sort { -it.value }
        .collectValues('%4.2f'::formatted)
}
Nearest words in vocab: ${model.wordsNearest('/c/en/cow', 4)}
"""

/*
conceptnet
cow: bovine (0.77) cattle (0.77) livestock (0.63) bull (0.54) calf (0.53)
bull: cow (0.54) cattle (0.50) bovine (0.46) livestock (0.37) calf (0.35)
calf: cow (0.53) bovine (0.50) cattle (0.48) livestock (0.39) bull (0.35)
bovine: cow (0.77) cattle (0.75) livestock (0.62) calf (0.50) bull (0.46)
cattle: livestock (0.89) cow (0.77) bovine (0.75) bull (0.50) calf (0.48)
livestock: cattle (0.89) cow (0.63) bovine (0.62) calf (0.39) bull (0.37)
cat: kitten (0.84) feline (0.84) bull (0.25) bear (0.21) cow (0.20)
kitten: cat (0.84) feline (0.71) bear (0.24) bull (0.20) hippo (0.20)
feline: cat (0.84) kitten (0.71) bear (0.24) hippo (0.24) livestock (0.24)
hippo: cow (0.32) bovine (0.31) bear (0.28) calf (0.25) bull (0.25)
bear: bull (0.32) hippo (0.28) feline (0.24) kitten (0.24) cat (0.21)
bare: grass (0.11) bear (0.10) kitten (0.07) cat (0.03) cattle (0.03)
milk: cow (0.50) bovine (0.38) cattle (0.36) livestock (0.32) water (0.29)
water: milk (0.29) hippo (0.20) grass (0.19) green (0.14) livestock (0.09)
grass: green (0.34) livestock (0.23) water (0.19) cattle (0.18) calf (0.12)
green: grass (0.34) water (0.14) hippo (0.13) milk (0.05) bear (0.04)

glove
cow: bovine (0.67) cattle (0.62) milk (0.48) livestock (0.47) calf (0.44)
bull: cow (0.38) bear (0.38) calf (0.32) cattle (0.32) cat (0.30)
calf: cow (0.44) bovine (0.34) bull (0.32) cattle (0.30) hippo (0.28)
bovine: cow (0.67) cattle (0.42) feline (0.34) calf (0.34) milk (0.28)
cattle: livestock (0.78) cow (0.62) bovine (0.42) milk (0.38) bull (0.32)
livestock: cattle (0.78) cow (0.47) milk (0.33) water (0.29) bovine (0.28)
cat: feline (0.49) kitten (0.43) cow (0.34) bear (0.33) bull (0.30)
kitten: cat (0.43) feline (0.38) hippo (0.25) cow (0.22) calf (0.20)
feline: cat (0.49) kitten (0.38) bovine (0.34) hippo (0.26) cow (0.16)
hippo: calf (0.28) feline (0.26) kitten (0.25) bull (0.21) cow (0.20)
bear: bull (0.38) cat (0.33) cow (0.27) livestock (0.21) cattle (0.21)
bare: grass (0.27) water (0.22) green (0.16) calf (0.13) cat (0.13)
milk: cow (0.48) water (0.42) cattle (0.38) livestock (0.33) bovine (0.28)
water: milk (0.42) green (0.35) grass (0.29) livestock (0.29) bare (0.22)
grass: green (0.36) water (0.29) bare (0.27) livestock (0.25) cattle (0.24)
green: grass (0.36) water (0.35) milk (0.21) bear (0.17) cow (0.16)

fasttext
cow: bovine (0.72) cattle (0.70) calf (0.67) bull (0.67) milk (0.66)
bull: cow (0.67) cattle (0.59) bovine (0.57) calf (0.56) bear (0.50)
calf: cow (0.67) cattle (0.59) bull (0.56) bovine (0.54) livestock (0.52)
bovine: cow (0.72) cattle (0.61) bull (0.57) calf (0.54) livestock (0.52)
cattle: livestock (0.85) cow (0.70) bovine (0.61) calf (0.59) bull (0.59)
livestock: cattle (0.85) cow (0.61) water (0.52) bovine (0.52) calf (0.52)
cat: kitten (0.75) feline (0.74) cow (0.45) hippo (0.44) bull (0.40)
kitten: cat (0.75) feline (0.67) hippo (0.48) cow (0.48) calf (0.46)
feline: cat (0.74) kitten (0.67) bovine (0.48) hippo (0.46) cow (0.41)
hippo: cow (0.49) calf (0.49) kitten (0.48) feline (0.46) cat (0.44)
bear: bull (0.50) bare (0.49) kitten (0.41) calf (0.41) livestock (0.41)
bare: bear (0.49) grass (0.46) green (0.41) bull (0.37) water (0.35)
milk: cow (0.66) water (0.57) cattle (0.53) livestock (0.51) calf (0.50)
water: milk (0.57) grass (0.52) livestock (0.52) green (0.48) cattle (0.43)
grass: green (0.57) water (0.52) livestock (0.48) cow (0.48) cattle (0.47)
green: grass (0.57) water (0.48) bare (0.41) cow (0.39) bear (0.36)

/c/en/cow: /c/fr/vache (0.95) /c/de/kuh (0.95) /c/en/bovine (0.77) /c/fr/bovin (0.75) /c/en/bull (0.54)
/c/en/bull: /c/fr/taureau (0.91) /c/en/cow (0.54) /c/fr/vache (0.52) /c/de/kuh (0.51) /c/fr/bovin (0.50)
/c/en/calf: /c/de/kuh (0.53) /c/en/cow (0.53) /c/fr/vache (0.52) /c/en/bovine (0.50) /c/fr/bovin (0.50)
/c/en/bovine: /c/fr/bovin (0.92) /c/en/cow (0.77) /c/de/kuh (0.75) /c/fr/vache (0.74) /c/en/calf (0.50)
/c/fr/bovin: /c/en/bovine (0.92) /c/fr/vache (0.79) /c/de/kuh (0.76) /c/en/cow (0.75) /c/fr/taureau (0.58)
/c/fr/vache: /c/en/cow (0.95) /c/de/kuh (0.93) /c/fr/bovin (0.79) /c/en/bovine (0.74) /c/fr/taureau (0.56)
/c/fr/taureau: /c/en/bull (0.91) /c/fr/bovin (0.58) /c/fr/vache (0.56) /c/en/cow (0.53) /c/de/kuh (0.53)
/c/de/kuh: /c/en/cow (0.95) /c/fr/vache (0.93) /c/fr/bovin (0.76) /c/en/bovine (0.75) /c/en/calf (0.53)
/c/en/kitten: /c/en/cat (0.84) /c/de/katze (0.80) /c/en/bull (0.20) /c/en/cow (0.19) /c/de/kuh (0.18)
/c/en/cat: /c/de/katze (0.94) /c/en/kitten (0.84) /c/en/bull (0.25) /c/en/cow (0.20) /c/fr/taureau (0.19)
/c/de/katze: /c/en/cat (0.94) /c/en/kitten (0.80) /c/en/bull (0.24) /c/de/kuh (0.24) /c/fr/taureau (0.21)
 */
