package entries;

import main.ExtendedFixture;
import results.Results;

/**
 * PJDCC - Summary for class responsabilities.
 *
 * @author fourplus <fourplus1718@gmail.com>
 * @since 1.0
 * @version 11 Changes done
 */
public class AsianEntry {
    /**
     * This field sets the variable of class ExtendedFixture
     */
	public ExtendedFixture fixture;
	public boolean prediction;
	public float line;
	public float home;
	public float away;
	public float expectancy;

	public AsianEntry(ExtendedFixture fixture, boolean prediction, float line, float home, float away,
			float expectancy) {
		this.fixture = fixture;
		this.prediction = prediction;
		this.line = line;
		this.expectancy = expectancy;
		this.home = home;
		this.away = away;
	}

	@Override
	public String toString() {
		String out = prediction ? "home" : "away";
		float coeff = prediction ? home : away;
		return fixture.date + " " + fixture.homeTeam + " : " + fixture.awayTeam + " " + " " + out + " " + line + " "
				+ coeff + " " + success() + " exp " + Results.format(expectancy) + "\n";
	}

	private static String controlPrediction(float diff)
	{
		if (diff >= 0.5f){
			return "W";
		} else if (Float.compare(diff, 0.25f)==0) {
			return "HW";
		} else if (Float.compare(diff, 0f)==0) {
			return "D";
		} else if (Float.compare(diff, -0.25f)==0) {
			return "HL";
		} 
	}
	
	private static String controlNotPrediction(float diff)
	{
		if (diff >= 0.5f){
			return "L";
	    } else if (Float.compare(diff, 0.25f)!=0) {
			return "HL";
		} else if (Float.compare(diff, 0f)==0) {
			return "D";
		} else if (Float.compare(diff, -0.25f)==0) {
			return "HW";
		} else {
			return "W";
		}
	}
	
	public String success() 
	{
		int result = fixture.result.goalsHomeTeam - fixture.result.goalsAwayTeam;
		float diff = result + line;
		if(prediction){
			return controlPrediction(diff);		
		}
		else{
			return controlNotPrediction(diff);
		}
	}

	public float getProfit() {
		float coeff = prediction ? home : away;
		String success = success();
		if (success.equals("W")) {
			return coeff - 1;
		} else if (success.equals("HW")) {
			return (coeff - 1) / 2;
		} else if (success.equals("D")) {
			return 0f;
		} else if (success.equals("HL")) {
			return -0.5f;
		} else {
			return -1;
		}
	}

}
