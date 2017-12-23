package entries;

import main.ExtendedFixture;

/**
 * PJDCC - Summary for class responsabilities.
 *
 * @author fourplus <fourplus1718@gmail.com>
 * @since 1.0
 * @version 11 Changes done
 */

public class DrawEntry {
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
	public float expectancy;

	public DrawEntry(ExtendedFixture fixture, boolean prediction, float expectancy) {
		super();
		this.fixture = fixture;
		this.prediction = prediction;
		this.expectancy = expectancy;
	}

	@Override
	public String toString() {
		String out = prediction ? "draw" : "12";
		float coeff = prediction ? fixture.drawOdds : 1f;
		return fixture.date + " " + fixture.homeTeam + " : " + fixture.awayTeam + " " + " " + out + " " + coeff + " "
				+ success() + "\n";
	}

	public boolean success() {
		return fixture.isDraw() && prediction;
	}

	public float getProfit() {
		if (success())
			return fixture.drawOdds - 1f;
		else
			return -1f;
	}

}
