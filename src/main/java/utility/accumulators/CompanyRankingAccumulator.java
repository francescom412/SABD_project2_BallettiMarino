package utility.accumulators;

import utility.delay_utility.DelayScorer;

import java.util.Date;
import java.util.HashMap;

/**
 * Scope: Global - Query 3
 * Accumulator used on time windows for company ranking on delay basis
 */
@SuppressWarnings("unused")
public class CompanyRankingAccumulator {

	// delay time threshold (in minutes) over which the score has a double value
	private static final int THRESHOLD_FOR_DOUBLE_SCORE = 30;
	// evaluation start date
	private Date startDate;
	// map of [company - score] couples
	private final HashMap<String, Double> companyRanking;

	/**
	 * No arguments constructor
	 */
	public CompanyRankingAccumulator() {
		// max possible start date, it will be then set to evaluation window start date
		this.startDate = new Date(Long.MAX_VALUE);
		// hash map initialization
		companyRanking = new HashMap<>();
	}

	/**
	 * Adds new info to the current company ranking map
	 * @param companyName name of the company whose delay must be added
	 * @param reason reason of the delay
	 * @param delay in minutes
	 */
	public void add(String companyName, String reason, Double delay) {
		boolean countTwice = false;
		// check if score must be doubled
		if (delay > THRESHOLD_FOR_DOUBLE_SCORE) {
			countTwice = true;
		}
		// add new evaluated score to the previous one
		this.companyRanking.merge(companyName, DelayScorer.computeAmount(reason, countTwice), Double::sum);
	}

	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Scope: Kafka Streams' serdes
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public HashMap<String, Double> getCompanyRanking() {
		return companyRanking;
	}
}
