# Introduction
The aim of this project is for the measurement of similarity between web services.

### Algorithms
- Dice Coefficient: https://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Dice%27s_coefficient
- Jaccard Similarity: https://en.wikipedia.org/wiki/Jaccard_index
- Levenshtein Distance: https://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Levenshtein_distance#Java
- TF-IDF: https://gist.github.com/guenodz/d5add59b31114a3a3c66
- WordNet Similarity: https://code.google.com/archive/p/ws4j/
- snowball-stemmer: https://mvnrepository.com/artifact/com.github.rholder/snowball-stemmer

### dependencies
snowball-stemmer, commons-text, hibernate-core, mysql-connector-java, spring-orm, poi

# Getting Started
<img src="https://github.com/berkagmp/WebserviceSimilarity/blob/master/Picture2.png" alt="Overoll process" width="80%" height="80%" />

<img src="https://github.com/berkagmp/WebserviceSimilarity/blob/master/Picture1.png" alt="Overoll process" width="80%" height="80%" />

The main class is derek.project.App.java which have the main() method.

### Make a Document Set for TF-IDF Algorithm
DocumentGroup.java is for creating document groups for IDF of TF-IDF algorithm.
It works with or without parameters and stemming process.

### Make Two Vectors from Two Web Services
- This program adopts the method of "Question Similarity Calculation for FAQ Answering"(Link: https://ieeexplore.ieee.org/document/4438554/).
- It is possible to adjust Stemming, TF-IDF and the ratio of semantic similarity.
- Basically, DICE Coefficient for the syntax similarity is used.

### Calculate Cosine Similarity Between Two Vectors
Cosine similarity is well-known for measurement between two vectors. (Link: https://en.wikipedia.org/wiki/Cosine_similarity)

# Build and Test
- SyntaxBasedTest.java is for test of syntax-based similarity.
- The collecting data and information about database exist in another repository. (Link: https://github.com/berkagmp/ParsingAPIs)
- App.java has the main() method.
- RealMeasurement() is for measurement of whole data in a database, and the output is XLS file.
- SimilarityExperiment() is for experiment with various conditions (Stemming, TF-IDF and the ratio of semantic similarity), and the output is printed in the console area.
- RUN AS Java application in STS.

# Contribute
We can find following factors for the similarity measurement.
- The Effect of Parameters
- The Effect of Stemming
- The Effect of Stemming for TF-IDF
- The Effect of Semantic-based Similarity

