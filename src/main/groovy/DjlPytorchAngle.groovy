import ai.djl.repository.zoo.Criteria
import ai.djl.training.util.ProgressBar
import ai.djl.translate.DeferredTranslatorFactory

import java.nio.file.Paths

import static MathUtil.cosineSimilarity

System.setProperty('org.slf4j.simpleLogger.defaultLogLevel', 'info')

var samplePhrases = [
    'bull', 'bovine', 'kitten', 'hay', 'The sky is blue',
    'The sea is blue', 'The grass is green', 'One two three',
    'Bulls consume hay', 'Bovines convert grass to milk',
    'Dogs play in the grass', 'Bulls trample grass',
    'Dachshunds are delightful', 'I like cats and dogs']

var queries = [
    'cow', 'cat', 'dog', 'grass', 'Cows eat grass',
    'Poodles are cute', 'The water is turquoise']

var modelName = 'UAE-Large-V1.zip'
var path = Paths.get(DjlPytorchAngle.classLoader.getResource(modelName).toURI())
var criteria = Criteria.builder()
    .setTypes(String, float[])
    .optModelPath(path)
    .optTranslatorFactory(new DeferredTranslatorFactory())
    .optProgress(new ProgressBar())
    .build()

var model = criteria.loadModel()
var predictor = model.newPredictor()
var sampleEmbeddings = samplePhrases.collect(predictor::predict)

queries.each { query ->
    println "\n    $query"
    var queryEmbedding = predictor.predict(query)
    sampleEmbeddings
        .collect { cosineSimilarity(it, queryEmbedding) }
        .withIndex()
        .sort { -it.v1 }
        .take(5)
        .each { printf '%s (%4.2f)%n', samplePhrases[it.v2], it.v1 }
}

String[] words = ['cow', 'bull', 'calf', 'bovine', 'cattle', 'livestock', 'cat', 'kitten', 'feline',
                  'hippo', 'bear', 'bare', 'milk', 'water', 'grass', 'green']
var wEmbeddings = words.collect{word -> predictor.predict(word) }
wEmbeddings.eachWithIndex { s, i ->
    print "\n${words[i]}:"
    var bestMatches = wEmbeddings.collect { cosineSimilarity(it, s) }.withIndex().findAll{ it.v2 != i }.sort { -it.v1 }.take(5)
    bestMatches.each { printf ' %s (%4.2f)', words[it.v2], it.v1 }
}
