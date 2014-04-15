
public class Stats {
	int frequency;
	int distance_sum;
	double distance;
	double relevance;
	int rank;
	
	public Stats() {
		frequency = 0;
		distance_sum = 0;
		distance = 0;
		relevance = 0;
		rank = 0;
	}
	
	public Stats(int frequency,int distance_sum,double distance,double relevance,int rank) {
		this.frequency = frequency;
		this.distance_sum = distance_sum;
		this.distance = distance;
		this.relevance = relevance;
		this.rank = rank;
	}
	
/*	public void incrementFrequency() {
		this.frequency++;
	}
	
	public void decrement() {
		this.frequency--;
	}
	
	public void setCount(int value) {
		this.frequency = value;
	}
	
	public int getFrequency() {
		return this.frequency;
	}	
	
	
	public void incrementDistanceSum() {
		this.distance_sum++;
	}
	
	public void incrementDistance(int value) {
		this.distance_sum += value;
	}
	
	public void decrementDistanceSum() {
		this.distance_sum--;
		
	}
	
	public void setDistanceSum(int value) {
		this.distance_sum = value - this.distance_sum ;
	}
	
	public void setDistance(int value) {
		this.distance_sum = value ;
	}
	
	public int getDistanceSum() {
		return this.distance_sum;
	}	
	
	public double getDistanceCalc() {
		return this.distance;
	}
	
	public void setDistanceCalc() {
		this.distance = (double)this.distance_sum / (double) this.frequency;
	}*/
}
