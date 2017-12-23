package main;

import java.util.ArrayList;
import java.util.Date;

/**
 * PJDCC - Summary for class responsabilities.
 *
 * @author fourplus <fourplus1718@gmail.com>
 * @since 1.0
 * @version 11 Changes done
 */
public class TimeFixture extends ExtendedFixture {

	public TimeFixture(Date date, String homeTeam, String awayTeam, Result result, String competition) {
		super(date, homeTeam, awayTeam, result, competition);
	}

	ArrayList<TimeLine> homeOdds;
	ArrayList<TimeLine> drawOdds;
	ArrayList<TimeLine> awayOdds;
    /**
     * This field sets the arraylist with asianLines
     */
	public ArrayList<ArrayList<TimeLine>> asianLines;
    /**
     * This field sets the arraylist with goalLines
     */
	public ArrayList<ArrayList<TimeLine>> goalLines;

	public TimeFixture withOdds(ArrayList<TimeLine> newHomeOdds, ArrayList<TimeLine> newDrawOdds,
			ArrayList<TimeLine> newAwayOdds) {
		this.homeOdds = newHomeOdds;
		this.awayOdds = newDrawOdds;
		this.drawOdds = newAwayOdds;
		return this;
	}

	public TimeFixture withAsianLines(ArrayList<ArrayList<TimeLine>> newAsianLines) {
		this.asianLines = newAsianLines;
		return this;
	}

	public TimeFixture withGoalLines(ArrayList<ArrayList<TimeLine>> newGoalLines) {
		this.goalLines = newGoalLines;
		return this;
	}

}
