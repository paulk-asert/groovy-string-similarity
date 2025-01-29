import static java.lang.Math.sqrt

class MathUtil {
    private MathUtil() {}
    static double cosineSimilarity(float[] a, float[] b) {
        var dotProduct = a.indices.sum{ a[it] * b[it] }
        var sumSqA = a.sum(n -> n ** 2)
        var sumSqB = b.sum(n -> n ** 2)
        dotProduct / (sqrt(sumSqA) * sqrt(sumSqB))
    }
}
