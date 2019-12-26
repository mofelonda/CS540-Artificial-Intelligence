import java.util.List;
import java.util.ArrayList;


public class CrossValidation {
    /*
     * Returns the k-fold cross validation score of classifier clf on training data.
     */
    public static double kFoldScore(Classifier clf, List<Instance> trainData, int k, int v) {
        if (k < 2 || k > trainData.size()) {
            return 0.0;
        }

        ArrayList<Double> results = new ArrayList<>();
        int setSize = trainData.size() / k;

        for (int i = 0; i < k; i++) {
            int start = setSize * i;
            int finish = start + setSize;
            List<Instance> trainSet = new ArrayList<>();
            List<Instance> testSet = new ArrayList<>();

            // Training set
            for (int j = 0; j < trainData.size(); j++) {
                if (!(j >= start && j < finish)) {
                    trainSet.add(trainData.get(j));
                }
            }
            clf.train(trainSet, v);

            // Testing set
            for (int j = start; j < (start + setSize); j++) {
                testSet.add(trainData.get(j));
            }
            if (testSet.size() == 0) {
                return 0.0;
            }

            // Determine accuracy of the classifier
            double correct = 0.0;
            for (Instance j : testSet) {
                ClassifyResult query = clf.classify(j.words);
                if (query.label == j.label) {
                    correct++;
                }
            }
            results.add(correct / testSet.size());
        }
        double sum = 0.0;
        for (Double result : results) {
            sum += result;
        }
        return sum / k;
    }
}