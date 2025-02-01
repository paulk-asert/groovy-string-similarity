import ai.djl.Application
import ai.djl.ndarray.NDArrays
import ai.djl.ndarray.NDList
import ai.djl.repository.zoo.Criteria
import ai.djl.training.util.ProgressBar
import ai.djl.translate.NoBatchifyTranslator
import ai.djl.translate.TranslatorContext

String[] phrases = [
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

class UseTranslator implements NoBatchifyTranslator<String[], float[][]> {
    @Override
    NDList processInput(TranslatorContext ctx, String[] raw) {
        var factory = ctx.NDManager
        var inputs = new NDList(raw.collect(factory::create))
        new NDList(NDArrays.stack(inputs))
    }

    @Override
    float[][] processOutput(TranslatorContext ctx, NDList list) {
        long numOutputs = list.singletonOrThrow().shape.get(0)
        NDList result = []
        for (i in 0..<numOutputs) {
            result << list.singletonOrThrow().get(i)
        }
        result*.toFloatArray() //as double[][]
    }
}

System.setProperty('org.slf4j.simpleLogger.defaultLogLevel', 'info')
String modelUrl = "https://storage.googleapis.com/tfhub-modules/google/universal-sentence-encoder/4.tar.gz"

var criteria = Criteria.builder()
    .optApplication(Application.NLP.TEXT_EMBEDDING)
    .setTypes(String[], float[][])
    .optModelUrls(modelUrl)
    .optTranslator(new UseTranslator())
    .optEngine("TensorFlow")
    .optProgress(new ProgressBar())
    .build()

var model = criteria.loadModel()
var predictor = model.newPredictor()
var embeddings = predictor.predict(phrases)

String[] queries = ['cow', 'cat', 'dog', 'grass', 'Cows eat grass',
 'Poodles are cute', 'The water is turquoise']
var qEmbeddings = predictor.predict(queries)
qEmbeddings.eachWithIndex { s, i ->
    println "\n    ${queries[i]}"
    var bestMatches = embeddings.collect { MathUtil.cosineSimilarity(it, s) }.withIndex().sort { -it.v1 }.take(5)
    bestMatches.each { printf '%s (%4.2f)%n', phrases[it.v2], it.v1 }
}

String[] words = ['cow', 'bull', 'calf', 'bovine', 'cattle', 'livestock', 'cat', 'kitten', 'feline',
                  'hippo', 'bear', 'bare', 'milk', 'water', 'grass', 'green']
var wEmbeddings = predictor.predict(words)
wEmbeddings.eachWithIndex { s, i ->
    print "\n${words[i]}:"
    var bestMatches = wEmbeddings.collect { MathUtil.cosineSimilarity(it, s) }.withIndex().findAll{ it.v2 != i }.sort { -it.v1 }.take(5)
    bestMatches.each { printf ' %s (%4.2f)', words[it.v2], it.v1 }
}
