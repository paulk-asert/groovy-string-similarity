import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer

import java.nio.file.Paths

//var modelName = 'fasttext-wiki-news-subwords-300.bin'
//var modelName = 'conceptnet-numberbatch-17-06-300.bin'
var modelName = 'glove-wiki-gigaword-300.bin'

var path = Paths.get(Dl4jWord2Vec.classLoader.getResource(modelName).toURI()).toFile()
var model = WordVectorSerializer.readWord2VecModel(path)
println 'embedding size:\n' + model.getWordVector('cow').size()
println 'embedding for cow:\n' + model.getWordVector('cow')
println 'embedding for bull:\n' + model.getWordVector('bull')
