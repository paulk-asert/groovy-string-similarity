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

Deep Learning
-------------

Uses the [Deep Java Library](https://djl.ai/) along with the [Huggingface Universal AnglE 📐 Embedding](https://huggingface.co/WhereIsAI/UAE-Large-V1) model for PyTorch as described in [1].

Steps for installing the model
------------------------------

Follow [instructions](https://djl.ai/extensions/tokenizers/):

    > git clone https://github.com/deepjavalibrary/djl.git
    > cd djl/extensions/tokenizers/src/main/python
    > python3 -m pip install -e .
    > cd djl_converter
    > djl-import -m WhereIsAI/UAE-Large-V1

References
----------

[1] [Deep Learning with Java/Kotlin | Semantic Text Similarity | NLP in Java | Deep Java Library](https://youtu.be/AHlnGId-Y-0?si=HMeGPg14wVJqIx6f)
