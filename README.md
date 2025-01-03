groovy-string-similarity
========================

String similarity helps answer questions like:
* Are two Jira/GitHub issues duplicates of the same issue?
* Are two (or more) customer records actually for the same customer?
* Is some social media topic trending because multiple posts are really about the same thing?
* Can I understand some natural language customer request even when it contains spelling mistakes?
* As a doctor, can I find a medical journal paper discussing a patient's medical diagnosis/symptoms/treatment?
* As a programmer, can I find a solution to my coding problem?

Simple comparisons
------------------

Examines three libraries for performing similarity matching:
* `info.debatty:java-string-similarity`
* `org.apache.commons:commons-text` Apache Commons Text
* `commons-codec:commons-codec` Apache Commons Codec for Soundex

Expected output from running the `Main.groovy` script:

    cat VS hat
    JaroWinkler                0.78 ███████████████▏
    JaroWinklerSimilarity      0.78 ███████████████▏
    NormalizedLevenshtein      0.67 █████████████▏
    RatcliffObershelp          0.67 █████████████▏
    Jaccard                    0.50 ██████████▏
    JaccardSimilarity          0.50 ██████████▏
    Jaccard(2)                 0.33 ██████▏
    SorensenDice               0.00 ▏
    Cosine                     0.00 ▏

          bear VS bare
    Jaccard                    1.00 ████████████████████▏
    JaccardSimilarity          1.00 ████████████████████▏
    JaroWinklerSimilarity      0.85 █████████████████▏
    JaroWinkler                0.85 ████████████████▏
    RatcliffObershelp          0.75 ███████████████▏
    NormalizedLevenshtein      0.50 ██████████▏
    Jaccard(2)                 0.20 ████▏
    SorensenDice               0.00 ▏
    Cosine                     0.00 ▏

          there VS their
    JaroWinklerSimilarity      0.91 ██████████████████▏
    JaroWinkler                0.91 ██████████████████▏
    Jaccard                    0.80 ████████████████▏
    RatcliffObershelp          0.80 ████████████████▏
    JaccardSimilarity          0.80 ████████████████▏
    NormalizedLevenshtein      0.60 ████████████▏
    Cosine                     0.33 ██████▏
    Jaccard(2)                 0.33 ██████▏
    SorensenDice               0.33 ██████▏

          cow VS bull
    NormalizedLevenshtein      0.00 ▏
    Jaccard                    0.00 ▏
    Jaccard(2)                 0.00 ▏
    JaroWinkler                0.00 ▏
    RatcliffObershelp          0.00 ▏
    SorensenDice               0.00 ▏
    Cosine                     0.00 ▏
    JaccardSimilarity          0.00 ▏
    JaroWinklerSimilarity      0.00 ▏

              The blue car
    NormalizedLevenshtein: The wind blew, The sky is blue, The sea is blue
    WeightedLevenshtein: The wind blew, The sky is blue, The sea is blue
    Damerau: The wind blew, The sky is blue, The sea is blue
    OptimalStringAlignment: The wind blew, The sky is blue, The sea is blue
    LongestCommonSubsequence: The sky is blue, The sea is blue, The wind blew
    MetricLCS: The wind blew, The sky is blue, The sea is blue
    NGram(2): The wind blew, The sky is blue, The sea is blue
    NGram(4): The wind blew, The sky is blue, The sea is blue
    QGram: The sky is blue, The sea is blue, The wind blew
    Soundex: I read a book, Numbers are odd or even, The sky is blue
    CosineDistance: The sky is blue, The sea is blue, The wind blew
    HammingDistance: The sky is blue, The sea is blue, Blue skies following me
    JaccardDistance: The sea is blue, The sky is blue, The wind blew
    JaroWinklerDistance: The wind blew, The sea is blue, The sky is blue
    LongestCommonSubsequenceDistance: The sky is blue, The sea is blue, The wind blew

              The evening sky
    NormalizedLevenshtein: The wind blew, The sky is blue, The sea is blue
    WeightedLevenshtein: The wind blew, The sky is blue, The sea is blue
    Damerau: The wind blew, The sky is blue, The sea is blue
    OptimalStringAlignment: The wind blew, The sky is blue, The sea is blue
    LongestCommonSubsequence: The wind blew, The sky is blue, The sea is blue
    MetricLCS: The sky is blue, The sea is blue, The wind blew
    NGram(2): The wind blew, The sky is blue, The sea is blue
    NGram(4): The wind blew, The sky is blue, The sea is blue
    QGram: The sky is blue, The wind blew, The sea is blue
    Soundex: Blue skies following me, I read a book, Numbers are odd or even
    CosineDistance: The sky is blue, The wind blew, The sea is blue
    HammingDistance: The sky is blue, The sea is blue, Blue skies following me
    JaccardDistance: The sky is blue, The sea is blue, The wind blew
    JaroWinklerDistance: The wind blew, The sea is blue, The sky is blue
    LongestCommonSubsequenceDistance: The wind blew, The sky is blue, The sea is blue

              Red roses
    NormalizedLevenshtein: Red noses, Apples are red, My ferrari is red
    WeightedLevenshtein: Red noses, Apples are red, I read a book
    Damerau: Red noses, Apples are red, I read a book
    OptimalStringAlignment: Red noses, Apples are red, I read a book
    LongestCommonSubsequence: Red noses, The sea is blue, I read a book
    MetricLCS: Red noses, The sea is blue, I read a book
    NGram(2): Red noses, Apples are red, My ferrari is red
    NGram(4): Red noses, Apples are red, I read a book
    QGram: Red noses, Apples are red, I read a book
    Soundex: The sky is blue, The sea is blue, I read a book
    CosineDistance: Red noses, The sky is blue, The sea is blue
    HammingDistance: Red noses, The sky is blue, The sea is blue
    JaccardDistance: Red noses, Apples are red, I read a book
    JaroWinklerDistance: Red noses, The sea is blue, The sky is blue
    LongestCommonSubsequenceDistance: Red noses, The sea is blue, I read a book

    cat       C300      hat       H300
    bear      B600      bare      B600
    there     T600      their     T600
    cow       C000      bull      B400

Deep Learning
-------------

Uses the [Deep Java Library](https://djl.ai/) along with the [Huggingface Universal AnglE 📐 Embedding](https://huggingface.co/WhereIsAI/UAE-Large-V1) model for PyTorch as described in [1].

Steps for installing the model:

Follow [these instructions](https://djl.ai/extensions/tokenizers/), roughly these steps:

    > git clone https://github.com/deepjavalibrary/djl.git
    > cd djl/extensions/tokenizers/src/main/python
    > python3 -m pip install -e .
    > cd djl_converter
    > djl-import -m WhereIsAI/UAE-Large-V1

Once built, create a `src/main/resources` folder and copy the `model/nlp/text_embedding/ai/djl/huggingface/pytorch/WhereIsAI/UAE-Large-V1/0.0.1/UAE-Large-V1.zip` file into the new folder.

Expected output from running the `DeepLearning.groovy` script:

    Loading:     100% |████████████████████████████████████████|
    [main] INFO ai.djl.pytorch.engine.PtEngine - PyTorch graph executor optimizer is enabled, this may impact your inference latency and throughput. See: https://docs.djl.ai/docs/development/inference_performance_optimization.html#graph-executor-optimization
    [main] INFO ai.djl.pytorch.engine.PtEngine - Number of inter-op threads is 12
    [main] INFO ai.djl.pytorch.engine.PtEngine - Number of intra-op threads is 12
    [main] INFO ai.djl.translate.DeferredTranslatorFactory - Using TranslatorFactory: ai.djl.huggingface.translator.TextEmbeddingTranslatorFactory
    [main] INFO ai.djl.util.Platform - Found matching platform from: jar:file:/Users/paulk/.gradle/caches/modules-2/files-2.1/ai.djl.huggingface/tokenizers/0.28.0/c66b1f8c7ef6fb541d0743362864c0751a47c4ed/tokenizers-0.28.0.jar!/native/lib/tokenizers.properties
    Bulls consume hay (0.69)
    The grass is green (0.62)
    The sky is blue (0.37)


References
----------

[1] [Deep Learning with Java/Kotlin | Semantic Text Similarity | NLP in Java | Deep Java Library](https://youtu.be/AHlnGId-Y-0?si=HMeGPg14wVJqIx6f)
