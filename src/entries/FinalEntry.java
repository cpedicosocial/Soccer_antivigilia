package entries;

import main.ExtendedFixture;
import main.Result;

/**
 * PJDCC - Summary for class responsabilities.
 *
 * @author fourplus <fourplus1718@gmail.com>
 * @since 1.0
 * @version 11 Changes done
 */
public class FinalEntry implements Comparable<FinalEntry> {
	
	private final String LITERAL = "%.2f";
	
    /**
     * This field sets the variable of class ExtendedFixture
     */
	public ExtendedFixture fixture;
    /**
     * This field sets the variable of class Float
     */
	public Float prediction;
    /**
     * This field sets the variable of class Result
     */
	public Result result;
    /**
     * This field sets the float variable
     */
	public float threshold;
    /**
     * This field sets the float variable
     */
	public float upper;
    /**
     * This field sets the float variable
     */
	public float lower;
    /**
     * This field sets the float variable
     */
	public float value;

	public FinalEntry(ExtendedFixture fixture, float prediction, Result result, float threshold, float lower,
			float upper) {
		this.fixture = fixture;
		this.prediction = prediction;
		this.result = result;
		this.threshold = threshold;
		this.upper = upper;
		this.lower = lower;
		this.value = 0.9f;
	}

	/**
	 * Copy constructor (doesn't copy the fixture field, not necessary for now)
	 * 
	 * @param i
	 */
	public FinalEntry(FinalEntry i) {
		this.fixture = i.fixture;
		this.prediction = i.prediction;
		this.result = new Result(i.result);
		this.threshold = i.threshold;
		this.upper = i.upper;
		this.lower = i.lower;
		this.value = i.value;
	}

	public float getPrediction() {
		return prediction;
	}

	public float getCertainty() {
		return prediction > threshold ? prediction : (1f - prediction);
	}

	public float getCOT() {
		return prediction > threshold ? (prediction - threshold) : (threshold - prediction);
	}

	public float getValue() {
		float gain = prediction > threshold ? fixture.maxOver : fixture.maxUnder;
		return getCertainty() * gain;
	}

	@Override
	public String toString() {
		int totalGoals = result.goalsAwayTeam + result.goalsHomeTeam;
		String out = prediction >= upper ? "over" : "under";
		float coeff = prediction >= upper ? fixture.maxOver : fixture.maxUnder;
		if (fixture.result.goalsHomeTeam == -1)
			return String.format(LITERAL, prediction * 100) + " " + fixture.date + " " + fixture.homeTeam + " : "
					+ fixture.awayTeam + " " + out + " " + String.format(LITERAL, coeff) + "\n";
		else
			return String.format(LITERAL, prediction * 100) + " " + fixture.date + " " + fixture.homeTeam + " : "
					+ fixture.awayTeam + " " + totalGoals + " " + out + " " + success() + " "
					+ String.format(LITERAL, getProfit()) + "\n";
	}

	public boolean isOver() {
		return prediction >= upper;
	}

	public boolean isUnder() {
		return prediction < lower;
	}

	public boolean success() {
		int totalGoals = result.goalsAwayTeam + result.goalsHomeTeam;
		if (totalGoals > 2.5d) {
			return isOver();
		} else {
			return isUnder();
		}

	}

	public float getProfit() {
		if (fixture.getTotalGoals() < 0)
			return 0f;
		float coeff = prediction >= upper ? fixture.maxOver : fixture.maxUnder;
		if (success())
			return coeff - 1f;
		else
			return -1f;

	}

	public float getNormalizedProfit() {
		if (fixture.getTotalGoals() < 0)
			return 0f;
		float coeff = prediction >= upper ? fixture.maxOver : fixture.maxUnder;
		float betUnit = 1f / (coeff - 1);
		if (success())
			return 1f;
		else
			return -betUnit;
	}

	@Override
	public int compareTo(FinalEntry o) {
		return prediction.compareTo(o.prediction);
	}

}