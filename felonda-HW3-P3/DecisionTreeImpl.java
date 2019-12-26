import java.util.List;
import java.util.ArrayList;

/**
 * Fill in the implementation details of the class DecisionTree using this file. Any methods or
 * secondary classes that you want are fine but we will only interact with those methods in the
 * DecisionTree framework.
 */
public class DecisionTreeImpl {
	public DecTreeNode root;
	public List<List<Integer>> trainData;
	public int maxPerLeaf;
	public int maxDepth;
	public int numAttr;

	class attributePair {
		public int attribute;
		public int threshold;

		public attributePair(int x, int y) {
			this.attribute = x;
			this.threshold = y;
		}
	}

	// Build a decision tree given a training set
	DecisionTreeImpl(List<List<Integer>> trainDataSet, int mPerLeaf, int mDepth) {
		this.trainData = trainDataSet;
		this.maxPerLeaf = mPerLeaf;
		this.maxDepth = mDepth;
		if (this.trainData.size() > 0) this.numAttr = trainDataSet.get(0).size() - 1;
		this.root = buildTree(this.trainData, 0);
	}

	private DecTreeNode buildTree(List<List<Integer>> data, int depth) {
		DecTreeNode node;
		int ones = 0;
		int zeros = 0;

		for (List<Integer> list : data) {
			if (list.get(list.size() - 1) == 0) {
				zeros += 1;
			}
			else {
				ones += 1;
			}
		}

		if ((zeros + ones) <= this.maxPerLeaf || depth >= this.maxDepth) {
			if (zeros > ones) {
				node = new DecTreeNode(0,-1,-1);
			}
			else {
				node = new DecTreeNode(1,-1,-1);
			}
		}
		else if (zeros == 0) {
			node = new DecTreeNode(1,-1,-1);
		}
		else if (ones == 0) {
			node = new DecTreeNode(0,-1,-1);
		}
		else {
			attributePair bestThreshold = findBestThreshold(data);
			if (bestThreshold == null) {
				if (zeros > ones) {
					node = new DecTreeNode(0,-1,-1);
				}
				else {
					node = new DecTreeNode(1,-1,-1);
				}
			}
			else {
				int attribute = bestThreshold.attribute;
				int threshold = bestThreshold.threshold;

				List<List<Integer>> zerosList = new ArrayList<>();
				List<List<Integer>> onesList = new ArrayList<>();

				for (List<Integer> point : data) {
					if (point.get(attribute) <= threshold) {
						zerosList.add(point);
					}
					else if (point.get(attribute) > threshold) {
						onesList.add(point);
					}
				}

				DecTreeNode zerosNode = buildTree(zerosList, depth + 1);
				DecTreeNode onesNode = buildTree(onesList, depth + 1);

				node = new DecTreeNode(-1, attribute, threshold);
				node.left = zerosNode;
				node.right = onesNode;
			}
		}
		return node;
	}

	public int classify(List<Integer> instance) {
		return traverseTree(this.root, instance);
	}

	private int traverseTree(DecTreeNode node, List<Integer> instance) {
		if (node.isLeaf()) {
			return node.classLabel;
		}
		else {
			if (instance.get(node.attribute) <= node.threshold) {
				return traverseTree(node.left, instance);
			}
			else if (instance.get(node.attribute) > node.threshold) {
				return traverseTree(node.right, instance);
			}
		}
		return -1;
	}

	private double calcEntropy(int zeros, int ones) {
		double zerosCalc = 0.0;
		double onesCalc = 0.0;
		double zerosLogged = 0.0;
		double onesLogged = 0.0;

		if (zeros != 0) {
			zerosCalc = (double)((double)(zeros)) / ((double)(zeros + ones));
			zerosLogged = Math.log(zerosCalc) / Math.log((double)2);
		}
		if (ones != 0) {
			onesCalc = (double)((double)(ones)) / ((double)(ones + zeros));
			onesLogged = Math.log(onesCalc) / Math.log((double)2);
		}
		return -(zerosCalc * zerosLogged + onesCalc * onesLogged);
	}

	private double initEntropy(List<List<Integer>> data) {
		int zeros = 0;
		int ones = 0;

		for (List<Integer> list : data) {
			if (list.get(list.size() - 1) == 0) {
				zeros += 1;
			}
			else if (list.get(list.size() - 1) == 1) {
				ones += 1;
			}
		}
		return calcEntropy(zeros, ones);
	}

	private double infoGain(double initEntropy, double entropyLeft, double entropyRight,
									int zeroZero, int zeroOne, int oneZero, int oneOne) {

		int total = zeroZero + zeroOne + oneZero + oneOne;
		double calcEntropy = ((double)((double)zeroZero + (double)zeroOne) / ((double)total)) * (entropyLeft) +
								((double)((double)oneZero + (double)oneOne) / ((double)total)) * (entropyRight);
		return initEntropy - calcEntropy;
	}

	private double infoGainAttr(List<List<Integer>> data, double initEntropy, attributePair attributePair) {
		int zeroZero = 0;
		int zeroOne = 0;
		int oneZero = 0;
		int oneOne = 0;
		int attribute = attributePair.attribute;
		int threshold = attributePair.threshold;

		for (List<Integer> point : data) {
			if (point.get(attribute) <= threshold) {
				if (point.get(point.size() - 1) == 0) {
					zeroZero++;
				}
				else if (point.get(point.size() - 1) == 1) {
					zeroOne++;
				}
			}
			else if (point.get(attribute) > threshold) {
				if (point.get(point.size() - 1) == 0) {
					oneZero++;
				}
				else if (point.get(point.size() - 1) == 1) {
					oneOne++;
				}
			}
		}
		double entropyLeft = calcEntropy(zeroZero, zeroOne);
		double entropyRight = calcEntropy(oneZero, oneOne);
		return infoGain(initEntropy, entropyLeft, entropyRight, zeroZero, zeroOne, oneZero, oneOne);
	}

	private attributePair findBestThreshold(List<List<Integer>> data) {
		attributePair bestThreshold = null;

		double initEntropy = initEntropy(data);
		double maxInfoGain = -1;

		for (int attribute = 0; attribute < this.numAttr; attribute++) {
			for (int threshold = 1; threshold <= 10; threshold++) {
				attributePair pair = new attributePair(attribute, threshold);
				double infoGain = infoGainAttr(data, initEntropy, pair);

				if (infoGain > maxInfoGain) {
					maxInfoGain = infoGain;
					bestThreshold = pair;
				}
			}
		}
		if (maxInfoGain == 0) {
			return null;
		}
		return bestThreshold;
	}

	// Print the decision tree in the specified format
	public void printTree() {
		printTreeNode("", this.root);
	}

	public void printTreeNode(String prefixStr, DecTreeNode node) {
		String printStr = prefixStr + "X_" + node.attribute;
		System.out.print(printStr + " <= " + String.format("%d", node.threshold));
		if(node.left.isLeaf()) {
			System.out.println(" : " + String.valueOf(node.left.classLabel));
		}
		else {
			System.out.println();
			printTreeNode(prefixStr + "|\t", node.left);
		}
		System.out.print(printStr + " > " + String.format("%d", node.threshold));
		if(node.right.isLeaf()) {
			System.out.println(" : " + String.valueOf(node.right.classLabel));
		}
		else {
			System.out.println();
			printTreeNode(prefixStr + "|\t", node.right);
		}
	}

	public double printTest(List<List<Integer>> testDataSet) {
		int numEqual = 0;
		int numTotal = 0;
		for (int i = 0; i < testDataSet.size(); i ++)
		{
			int prediction = classify(testDataSet.get(i));
			int groundTruth = testDataSet.get(i).get(testDataSet.get(i).size() - 1);
			System.out.println(prediction);
			if (groundTruth == prediction) {
				numEqual++;
			}
			numTotal++;
		}
		double accuracy = numEqual*100.0 / (double)numTotal;
		System.out.println(String.format("%.2f", accuracy) + "%");
		return accuracy;
	}
}