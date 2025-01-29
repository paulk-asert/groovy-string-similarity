import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import org.deeplearning4j.models.word2vec.Word2Vec

var queries = ['bull', 'bovine', 'kitten',
               'hay', 'cow', 'cat', 'dog', 'grass']

Word2Vec w2vModel = WordVectorSerializer.readWord2VecModel("/Users/james/OneDrive/Desktop/groovy-string-similarity/w2v_model.bin")
queries.each { a ->
    var results = (queries - a).collectEntries{ b -> [b, w2vModel.similarity("/c/en/$a", "/c/en/$b")] }.sort{-it.value }.take(3)
    println "$a: ${results.collect{ k, v -> k + sprintf(' (%.2f) ', v) }.join()}"
    println "$a: ${w2vModel.wordsNearest("/c/en/$a", 3)}"
}
