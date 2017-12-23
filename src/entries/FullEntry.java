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
public class FullEntry extends FinalEntry {
    /**
     * This field sets the variable of class main.Line
     */
	public main.Line line;

	public FullEntry(ExtendedFixture fixture, float prediction, Result result, float threshold, float lower,
			float upper, main.Line line) {
		super(fixture, prediction, result, threshold, lower, upper);
		this.line = line;
	}
	
	private static String predictionGreater(float diff)
	{
		if (diff >= 0.5f){
			return "W";
		} else if (Float.compare(diff, 0.25f)==0) {
			return "HW";
		} else if (Float.compare(diff, 0f)==0) {
			return "D";
		} else if (Float.compare(diff, -0.25f)==0) {
			return "HL";
		} else {
			return "L";
		}
	}

	private static String predictionSmaller(float diff)
	{
		if (diff >= 0.5f){
			return "L";
		} else if (Float.compare(diff, 0.25f)==0) {
			return "HL";
		} else if (Float.compare(diff, 0f)==0) {
			return "D";
		} else if (Float.compare(diff, -0.25f)==0) {
			return "HW";
		} else {
			return "W";
		}
	}
	
	public String successFull() {
		int result = fixture.result.goalsHomeTeam + fixture.result.goalsAwayTeam;
		float diff = result - line.line;
		if (line.line == -1f) {
			return "missing  data";
		}
		
		if(prediction>=upper){
			predictionGreater(diff);
		}
		else{
			predictionSmaller(diff);
		}		
	}


	public float getProfit() {
		if (line.line == -1f)
			return 0;
		float coeff = prediction >= upper ? line.home : line.away;
		String success = successFull();
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

	public String toString() {
		int totalGoals = result.goalsAwayTeam + result.goalsHomeTeam;
		String out = prediction >= upper ? "over" : "under";
		return String.format("%.2f", prediction * 100) + " " + fixture.date + " " + fixture.homeTeam + " : "
				+ fixture.awayTeam + " " + totalGoals + " " + out + " " + line.line + " " + successFull() + " "
				+ String.format("%.2f", getProfit()) + "\n";
	}

}
