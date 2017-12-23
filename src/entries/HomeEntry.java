package entries;

import main.ExtendedFixture;

/**
 * PJDCC - Summary for class responsabilities.
 *
 * @author fourplus <fourplus1718@gmail.com>
 * @since 1.0
 * @version 11 Changes done
 */

public class HomeEntry {
    /**
     * This field sets the variable of class ExtendedFixture
     */
	public ExtendedFixture fixture;
    /**
     * This field sets the boolean variable
     */
	public boolean prediction;
    /**
     * This field sets the float variable
     */
	public float score;

	public HomeEntry(ExtendedFixture fixture, boolean prediction, float score) {
		super();
		this.fixture = fixture;
		this.prediction = prediction;
		this.score = score;
	}

	@Override
	public String toString() {
		String out = prediction ? "home" : "X2";
		float coeff = prediction ? fixture.homeOdds : 1f;
		return fixture.date + " " + fixture.homeTeam + " : " + fixture.awayTeam + " " + " " + out + " " + getProfit() + " "
				+ success() + "\n";
	}

	public boolean success() {
		return fixture.isHomeWin() && prediction;
	}

	public float getProfit() {
		if (success())
			return fixture.homeOdds - 1f;
		else
			return -1f;
	}

}
