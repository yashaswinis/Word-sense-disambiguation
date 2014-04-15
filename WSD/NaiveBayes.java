import java.util.Arrays;
import java.util.List;


public class NaiveBayes {

	public NaiveBayes(SupervisedWSD trained, SupervisedWSD test) {
		int correctSenses = 0;
		int totalSenses = 0;

		// each separate test entry
		for(String token : test.context_list.keySet()) {
			List<SenseContext> testContextSenses = test.context_list.get(token);
			// each sense context for test entry, should be only one per entry
			for(SenseContext testSenseContext : testContextSenses) {
				List<SenseContext> trainedContextSenses = trained.context_list.get(token);
				SenseProb[] senseProbabilities = new SenseProb[trainedContextSenses.size()];

				// each sense context for given target word, from training data
				for(int i = 0; i < trainedContextSenses.size(); i++) {
					SenseContext trainedSenseContext = trainedContextSenses.get(i);
					double prob = 0.0;

					// loop through keyset of trainedsensecontext and keyset of testSenseContext
					// compute prob lob value					
					for(String testToken : testSenseContext.word_list.keySet()) {
						if(trainedSenseContext.word_list.containsKey(testToken)) {
							prob += Math.log(trainedSenseContext.word_list.get(testToken).frequency 
									/ (double) trainedSenseContext.occurrences);
						}
					}				
					senseProbabilities[i] = new SenseProb(trainedSenseContext.senseNumber, prob);
					System.out.println(prob);
				}
				Arrays.sort(senseProbabilities);

				if (senseProbabilities[senseProbabilities.length - 1].senseNumber == testSenseContext.senseNumber) {
					correctSenses++;
				}
				totalSenses++;
			}
		}

		System.out.println("Results: " + correctSenses + " / " + totalSenses);
	}

	private class SenseProb implements Comparable<SenseProb>{
		double prob;
		int senseNumber;

		SenseProb(int sense, double pr) {
			prob = pr;
			senseNumber = sense;
		}

		@Override
		public int compareTo(SenseProb sb) {
			if(prob < sb.prob) {
				return -1;
			} else if (prob > sb.prob) {
				return 1;
			} else {
				return 0;
			}
		}
	}

}
