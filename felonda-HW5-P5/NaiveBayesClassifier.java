import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Your implementation of a naive bayes classifier. Please implement all four methods.
 */

public class NaiveBayesClassifier implements Classifier {
    private Map<Label,Integer> wordsPerLabel;
    private Map<String,Integer> posWords;
    private Map<String,Integer> negWords;
    private double posPrior;
    private double negPrior;
    private int vocabSize;

    /**
     * Trains the classifier with the provided training data and vocabulary size
     */
    @Override
    public void train(List<Instance> trainData, int v) {
        Map<Label, Integer> docsPerLabel = getDocumentsCountPerLabel(trainData);
        wordsPerLabel = getWordsCountPerLabel(trainData);
        posWords = new HashMap<>();
        negWords = new HashMap<>();
        vocabSize = v;
        for (Instance i : trainData) {
            if (i.label == Label.POSITIVE) {
                for(String word : i.words) {
                    posWords.put(word, posWords.getOrDefault(word, 0) + 1);
                }
            }
            else {
                for (String word : i.words) {
                    negWords.put(word, negWords.getOrDefault(word, 0) + 1);
                }
            }
        }

        double totalDocs = trainData.size();
        if (totalDocs == 0.0) {
            posPrior = 0.0;
            negPrior = 0.0;
        }
        else {
            posPrior = (double) docsPerLabel.getOrDefault(Label.POSITIVE, 0) / totalDocs;
            negPrior = (double) docsPerLabel.getOrDefault(Label.NEGATIVE, 0) / totalDocs;
        }
    }

    /*
     * Counts the number of words for each label
     */
    @Override
    public Map<Label, Integer> getWordsCountPerLabel(List<Instance> trainData) {
        Map<Label,Integer> map = new HashMap<>();
        int posCount = 0;
        int negCount = 0;

        for (Instance i : trainData) {
            if (i.label == Label.POSITIVE) {
                posCount += i.words.size();
            }
            else {
                negCount += i.words.size();
            }
        }
        map.put(Label.POSITIVE, posCount);
        map.put(Label.NEGATIVE, negCount);
        return map;
    }

    /*
     * Counts the total number of documents for each label
     */
    @Override
    public Map<Label, Integer> getDocumentsCountPerLabel(List<Instance> trainData) {
        Map<Label, Integer> map = new HashMap<>();
        int posCount = 0;
        int negCount = 0;

        for (Instance i : trainData) {
            if (i.label == Label.POSITIVE) {
                posCount++;
            }
            else {
                negCount++;
            }
        }
        map.put(Label.POSITIVE, posCount);
        map.put(Label.NEGATIVE, negCount);
        return map;
    }

    /**
     * Returns the smoothed conditional probability of the word given the label, i.e. P(word|POSITIVE) or
     * P(word|NEGATIVE)
     */
    private double p_w_given_l(String word, Label label) {
        double denominator = this.wordsPerLabel.get(label) + this.vocabSize;
        if (denominator == 0.0) {
            return 0.0;
        }

        double numerator;
        if (label == Label.POSITIVE) {
            numerator = this.posWords.getOrDefault(word, 0) + 1;
        }
        else {
            numerator = this.negWords.getOrDefault(word, 0) + 1;
        }

        return numerator / denominator;
    }

    /**
     * Classifies an array of words as either POSITIVE or NEGATIVE.
     */
    @Override
    public ClassifyResult classify(List<String> words) {
        double posLog = posPrior == 0.0 ? 0.0 : Math.log(posPrior);
        double negLog = negPrior == 0.0 ? 0.0 : Math.log(negPrior);

        for (String word : words) {
            double posCond = p_w_given_l(word, Label.POSITIVE);
            double negCond = p_w_given_l(word, Label.NEGATIVE);
            posLog += posCond == 0 ? 0.0 : Math.log(posCond);
            negLog += negCond == 0 ? 0.0 : Math.log(negCond);
        }

        ClassifyResult result = new ClassifyResult();
        if (posLog > negLog) {
            result.label = Label.POSITIVE;
        }
        else {
            result.label = Label.NEGATIVE;
        }

        Map<Label, Double> logMap = new HashMap<>();
        logMap.put(Label.POSITIVE, posLog);
        logMap.put(Label.NEGATIVE, negLog);

        result.logProbPerLabel = logMap;
        return result;
    }
}