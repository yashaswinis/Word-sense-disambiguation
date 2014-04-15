import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;


public class DiscountedCumulativeGain {

	HashMap<String, Stats> test;

	/*
	public static void main(String[] args) {
		String s1 = "hello";
		String s2 = "goodbye";
		String s3 = "name";
		
		Stats stat1 = new Stats(3,20);
		Stats stat2 = new Stats(1,5);
		Stats stat3 = new Stats(2,12);
		
		HashMap<String, Stats> senseContext = new HashMap<String, Stats>();
		HashMap<String, Stats> testContext = new HashMap<String, Stats>();
		
		senseContext.put(s1, stat1);
		senseContext.put(s2, stat2);
		senseContext.put(s3, stat3);
		
		testContext.put(s1, stat1);
		testContext.put(s2, stat2);
		testContext.put(s3, stat3);
		
		DiscountedCumulativeGain dcg = new DiscountedCumulativeGain(testContext);
		
		System.out.println(dcg.dcg(senseContext));

	}*/

	DiscountedCumulativeGain(HashMap<String, Stats> testContext) {
		test = testContext;
		distance(test);
		relevance(test);
		rank(test);
	}

	public double dcg(HashMap<String, Stats> senseContext) {
		double dcg = 0.0;
		distance(senseContext);
		relevance(senseContext);
		rank(senseContext);

		// main loop for DCG computation
		for(String token : senseContext.keySet()) {
			if (test.containsKey(token)) {
	/*			System.out.println("token: " + token);
				System.out.println("rel: " + test.get(token).relevance);
				System.out.println("rank: " + senseContext.get(token).rank);*/
				dcg += (Math.pow(2.0, test.get(token).relevance) - 1)
						/ (Math.log(senseContext.get(token).rank + 1)
								/ Math.log(2));
				//System.out.println(dcg);
			}
		}
		//System.out.println("DCG = " + dcg);
		return dcg;
	}
	
	public void relevance(HashMap<String, Stats> context) {
		for(Stats s : context.values()) {
			s.relevance = s.frequency * 1.5/ s.distance;
			//System.out.println(s.relevance);
		}
	}
	
	public void distance(HashMap<String, Stats> context) {
		for(Stats s : context.values()) {
			s.distance = ((double) s.distance_sum) / ((double) s.frequency); 
		}
	}

	public void rank(HashMap<String, Stats> context) {
		LinkedList<TokenRank> ranking = new LinkedList<TokenRank>();

		for(String token : context.keySet()) {
			ranking.add(new TokenRank(token, context.get(token).relevance));
		}

		Collections.sort(ranking);

		for(int i = 0; i < ranking.size(); i++) {
			context.get(ranking.get(i).token).rank = ranking.size() - i;
		}
	}

	private class TokenRank implements Comparable<TokenRank>{

		String token;
		double relevance;

		public TokenRank(String s, double rel) {
			token = s;
			relevance = rel;
		}

		@Override
		public int compareTo(TokenRank tr) {
			if (relevance - tr.relevance < 0) {
				return -1;
			} else if (relevance - tr.relevance > 0) {
				return 1;
			} else {
				return 0;
			}
		}

	}

}
