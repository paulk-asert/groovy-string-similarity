import ai.djl.repository.zoo.Criteria
import ai.djl.training.util.ProgressBar
import ai.djl.translate.DeferredTranslatorFactory

import java.nio.file.Paths

import static java.lang.Math.sqrt

var sentences = [
    'The sky is blue',
    'The sea is blue',
    'The grass is green',
    'One two three',
    'Bulls consume hay',
    'Bovines convert grass to milk',
    'Dogs play in the grass',
    'Bulls trample grass',
    'Dachshunds are delightful'
]

System.setProperty('org.slf4j.simpleLogger.defaultLogLevel', 'info')

var path = Paths.get(AngleDLJ.classLoader.getResource('UAE-Large-V1.zip').toURI())
var criteria = Criteria.builder()
    .setTypes(String, float[])
    .optModelPath(path)
    .optTranslatorFactory(new DeferredTranslatorFactory())
    .optProgress(new ProgressBar())
    .build()

var model = criteria.loadModel()
var predictor = model.newPredictor()
var embeddings = sentences.collect(predictor::predict)

['Cows eat grass',
 'Poodles are cute',
 'The water is turquoise'].each { query ->
    println "\n    $query"
    var qe = predictor.predict(query)
    var bestMatches = embeddings.collect { cosineSimilarity(it, qe) }.withIndex().sort { -it.v1 }.take(5)
    bestMatches.each { printf '%s (%4.2f)%n', sentences[it.v2], it.v1 }
}

double cosineSimilarity(float[] a, float[] b) {
    var dotProduct = a.indices.sum{ a[it] * b[it] }
    var sumSqA = a.toList().sum(n -> n ** 2)
    var sumSqB = b.toList().sum(n -> n ** 2)
    dotProduct / (sqrt(sumSqA) * sqrt(sumSqB))
}
