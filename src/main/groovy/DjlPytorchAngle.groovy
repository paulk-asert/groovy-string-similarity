import ai.djl.repository.zoo.Criteria
import ai.djl.training.util.ProgressBar
import ai.djl.translate.DeferredTranslatorFactory

import java.nio.file.Paths

var phrases = [
    'bull',
    'bovine',
    'kitten',
    'hay',
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

var path = Paths.get(DjlPytorchAngle.classLoader.getResource('UAE-Large-V1.zip').toURI())
var criteria = Criteria.builder()
    .setTypes(String, float[])
    .optModelPath(path)
    .optTranslatorFactory(new DeferredTranslatorFactory())
    .optProgress(new ProgressBar())
    .build()

var model = criteria.loadModel()
var predictor = model.newPredictor()
var embeddings = phrases.collect(predictor::predict)

['cow', 'cat', 'dog', 'grass', 'Cows eat grass',
 'Poodles are cute', 'The water is turquoise'].each { query ->
    println "\n    $query"
    var qe = predictor.predict(query)
    var bestMatches = embeddings.collect { MathUtil.cosineSimilarity(it, qe) }.withIndex().sort { -it.v1 }.take(5)
    bestMatches.each { printf '%s (%4.2f)%n', phrases[it.v2], it.v1 }
}
