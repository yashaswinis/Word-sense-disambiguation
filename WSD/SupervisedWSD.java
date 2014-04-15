import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.tartarus.snowball.ext.PorterStemmer;

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

public class SupervisedWSD {

	HashMap<String, String> stop_words = new HashMap<String, String>();
	HashMap<String, List<SenseContext> > context_list = new HashMap<String, List<SenseContext> >();
	public static int testSense = 0;
	public static int graphLineNumber = 0;
	
	public static void main(String[] args) {
		SupervisedWSD trained = new SupervisedWSD();
		SupervisedWSD test = new SupervisedWSD();

		trained.createStopWords("stop_words.txt");
		test.createStopWords("stop_words.txt");

		trained.tokenizeData("sample.data");
		// trained.printWordList();
		System.out.println("Total lines in Training data " +  graphLineNumber);
		
		testSense = 1;
		graphLineNumber = 0;
		test.tokenizeData("sample.data");
		//test.tokenizeData("test_data.data");
		
		System.out.println("Total lines in Validation data " +  graphLineNumber);
		
		dcgCalculation(trained, test);
		new NaiveBayes(trained, test);

	}
	
	public static void dcgCalculation(SupervisedWSD trained, SupervisedWSD test) {
		int correctSenses = 0;
		int totalSenses = 0;

		for(String token : test.context_list.keySet()) {
			List<SenseContext> testContextSenses = test.context_list.get(token);

			for(SenseContext testSenseContext : testContextSenses) {
				List<SenseContext> trainedContextSenses = trained.context_list.get(token);
				DiscountedCumulativeGain dcg = new DiscountedCumulativeGain(testSenseContext.word_list);
				DCGSense[] senses = new DCGSense[trainedContextSenses.size()];

				for(int i = 0; i < trainedContextSenses.size(); i++) {
					double dcgValue = dcg.dcg(trainedContextSenses.get(i).word_list);
					senses[i] = new DCGSense(trainedContextSenses.get(i).senseNumber, dcgValue);
				}
				
				Arrays.sort(senses);
				
				if (senses[senses.length - 1].senseNumber == testSenseContext.senseNumber) {
					correctSenses++;
				}
				totalSenses++;
			}
		}

		System.out.println("Results: " + correctSenses + " / " + totalSenses);
	}

	public void createStopWords(String filename) {

		try {
			BufferedReader bbr = new BufferedReader(new FileReader(filename));
			String ln = "";

			while ((ln = bbr.readLine()) != null) {
				stop_words.put(ln, ln);
			}
			bbr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void tokenizeData(String filename) {

		try {
			InputStream is = new FileInputStream("en-token.bin");
			TokenizerModel model = new TokenizerModel(is);
			Tokenizer tokenizer = new TokenizerME(model);

			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line = "";

			List<SenseContext> myList = new ArrayList<SenseContext>();
			String curWord = "";
			String preWord = "";

			boolean scExists = false;
			int lineNumber = 0;

			while ((line = br.readLine()) != null) { 
				lineNumber ++;
				
				if(lineNumber % 1000 == 0) {
					System.out.println("1000 lines read");
				}

				SenseContext senseContext = null ;//= new SenseContext();
				scExists = false;

				String target[] = line.split(" | \\d+ | ");
				int targetLength = target[0].length() - 2;
				target[0] = target[0].substring(0,Math.max(targetLength, 0));

				curWord = target[0];


				if(!preWord.equalsIgnoreCase(target[0]) && !(lineNumber == 1)){
					context_list.put(preWord, myList);
					//System.out.println("Word put into list " +preWord );
					myList = new ArrayList<SenseContext>();
				}

				preWord = target[0];


				//System.out.println("Target Word :" + target[0] + "Target Sense" + prevSense);

				Iterator<SenseContext> iterator = myList.iterator();
				while (iterator.hasNext()) {
					SenseContext sC = iterator.next();
					//System.out.println("Sense Number" + sC.senseNumber);
					if(sC.senseNumber == Integer.valueOf(target[2])){
						senseContext = sC ;
						scExists = true;
						senseContext.occurrences++;
						//break;
					}
					if(sC.senseNumber == 0 || testSense == 1){
						scExists = false;
					}
				}
				if(!scExists){
					// System.out.println("Creatin new Sense Context for Target Word :" + target[0] + "Target Sense" + prevSense);

					senseContext = new SenseContext();
					senseContext.occurrences = 1;
				}


				//System.out.println(target[1]);
				//System.out.println(target[2]);-sense
				//System.out.println(target[3]);
				//System.out.println(target[4]);
				//System.out.println(target[5]);
				//System.out.println(target[6]);

				//System.out.println("Current Sense" + target[2]);

				if (senseContext == null){
					System.out.println("SC is null");
				}

				senseContext.senseNumber = Integer.valueOf(target[2]);

				//System.out.println(senseContext.senseNumber);

				line = line.substring(targetLength + 9);
				String tokens[] = tokenizer.tokenize(line);

				int findDistance = 0;
				String prevToken = "";
				int setForward = 1;

				for (String a : tokens) {
					findDistance++;

					if ( (a.equals("%")  ) && ( prevToken.equals("%") ) ) {
						setForward = 1;
						break;
					}
					prevToken = a;
				}
				findDistance--;

				for (String a : tokens) {
					// System.out.println("Token being parsed :" + a);

					if (setForward == 1 && findDistance > 0){
						findDistance--;
					} else if (findDistance == 0 && setForward == 1){
						findDistance =  findDistance - 3;
						setForward = 0;
					} else {
						findDistance++; 
					}

					if(findDistance < 0){
						continue;
					}

					//System.out.println(a + "," + prevToken);
					//System.out.println(distance);

					if(a.matches("\\d+") ||  a.matches("\\d+\\.\\d+")){
						continue;
					}


					/*					if(setTarget == 1 ){
						System.out.println(a);
						//setTarget = 0;
					}					
					 */
					/*					if ( (a.equals("%")  ) && ( prevToken.equals("%") ) ) {


						if (setTarget == 1){
							setTarget = 0;


							for (Map.Entry<String, Stats> entry : senseContext.word_list
									.entrySet()) {
								entry.getValue().setDistanceSum(distance);
							}
							distance = 0;
						}else{
						setTarget = 1;
						}
					}*/

					/*if ( a.equals("%")  ){
						continue;
					}*/

					prevToken = a;

					PorterStemmer stem = new PorterStemmer();
					stem.setCurrent(a);
					stem.stem();
					String result = stem.getCurrent();
					// System.out.println(result);
					result = result.toLowerCase();
					boolean value = stop_words.containsValue(result);

					if (!value) {
						if (senseContext.word_list.containsKey(result)) {
							//System.out.println("Value put into sc :" + result);
							senseContext.word_list.get(result).frequency++;
							senseContext.word_list.get(result).distance_sum += findDistance;
							//senseContext.word_list.get(result).distance = (double)senseContext.word_list.get(result).distance_sum / (double) senseContext.word_list.get(result).frequency;
						} else {
							//System.out.println("Value put into sc :" + result);
							Stats stats = new Stats(1, 0, 0.0, 0.0, 0);
							stats.distance_sum = findDistance ;
							//stats.distance = (double)stats.distance_sum / (double) stats.frequency;
							senseContext.word_list.put(result, stats);
						}
						// System.out.println(result);
					}
				}

				//	(myList.listIterator())

				/*				int exists=0;

				Iterator<SenseContext> iterator1 = myList.iterator();
					while (iterator1.hasNext()) {
						SenseContext sCC = iterator1.next();
						if(sCC.senseNumber == senseContext.senseNumber){
							exists = 1;
						}
						//System.out.println(iterator.next());
					}*/

				if(!scExists){
					myList.add(senseContext);	
				}

			}

			context_list.put(curWord, myList);
			//System.out.println("Word put into list " +preWord );

			//context_list.put("exchange", myList);

			is.close();
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return -1;
		}
		sizeOfContextList();
	}
	
	public void sizeOfContextList() {
		int count = 0;
		for(String token : context_list.keySet()) {
			count += context_list.get(token).size();
		}
		System.out.println(count);
	}

	public void printWordList() {
		for (Entry<String, List<SenseContext>> entry1 : context_list.entrySet()) {
			String key = entry1.getKey();
			System.out.println("==================================Context String " + key);
			List<SenseContext> value = entry1.getValue();
			// do something with key and/or tab
			for (SenseContext element : value) {
				// 1 - can call methods of element
				System.out.println("Key , Sense Number : " + key + ", " + element.senseNumber);

				for (Entry<String, Stats> entry : element.word_list.entrySet()) {
					String keyWL = entry.getKey();
					Stats valueWL = entry.getValue();
					System.out.println(keyWL + ", " + valueWL.frequency + ", " + valueWL.distance_sum + ", " + valueWL.distance );
					// ...
				}
				// ...
			}
		}
	}
	
	private static class DCGSense implements Comparable<DCGSense>{
		int senseNumber;
		double dcg;
		
		DCGSense(int sense, double dcgScore) {
			senseNumber = sense;
			dcg = dcgScore;
		}

		@Override
		public int compareTo(DCGSense dcgSense) {
			if (dcg < dcgSense.dcg) {
				return -1;
			} else if (dcg > dcgSense.dcg) {
				return 1;
			} else {
				return 0;
			}
		}
	}
}
