package main;

import java.util.Date;

/**
 * PJDCC - Summary for class responsabilities.
 *
 * @author fourplus <fourplus1718@gmail.com>
 * @since 1.0
 * @version 11 Changes done
 */
public class ExtendedFixture implements Comparable<ExtendedFixture> {
    /**
     * This field sets the date variable
     */
	public Date date;
    /**
     * This field sets the int variable
     */
	public int year;
    /**
     * This field sets the String variable
     */
	public String homeTeam;
    /**
     * This field sets the String variable
     */
	public String awayTeam;
    /**
     * This field sets the Result variable
     */
	public Result result;
    /**
     * This field sets the Result variable
     */
	public Result HTresult;
    /**
     * This field sets the String variable
     */
	public String status;
    /**
     * This field sets the String variable
     */
	public String competition;
    /**
     * This field sets the int variable
     */
	public int matchday;
    /**
     * This field sets the float variable
     */
	public float overOdds;
    /**
     * This field sets the float variable
     */
	public float underOdds;
    /**
     * This field sets the float variable
     */
	public float maxOver;
    /**
     * This field sets the float variable
     */
	public float maxUnder;
    /**
     * This field sets the float variable
     */
	public float homeOdds;
    /**
     * This field sets the float variable
     */
	public float drawOdds;
    /**
     * This field sets the float variable
     */
	public float awayOdds;
    /**
     * This field sets the int variable
     */
	public int shotsHome;
    /**
     * This field sets the int variable
     */
	public int shotsAway;
    /**
     * This field sets the float variable
     */
	public float line;
    /**
     * This field sets the float variable
     */
	public float asianHome;
    /**
     * This field sets the float variable
     */
	public float asianAway;
    /**
     * This field sets the int variable
     */
	public int redHome;
    /**
     * This field sets the int variable
     */
	public int redAway;
	

	public ExtendedFixture(Date newDate, String newHomeTeam, String newAwayTeam, Result newResult, String newCompetition) {
		this.date = date;
		this.homeTeam = homeTeam;
		this.awayTeam = awayTeam;
		this.result = result;
		this.competition = competition;
	}

//	/**
//	 * Copy constructor
//	 * 
//	 * @param fixture
//	 */
//	public ExtendedFixture(ExtendedFixture fixture) {
//		// TODO Auto-generated constructor stub
//	}

	public ExtendedFixture withOdds(float newOverOdds, float newUnderOdds, float newMaxOver, float newMaxUnder) {
		this.overOdds = newOverOdds;
		this.underOdds = newUnderOdds;
		this.maxOver = newMaxOver;
		this.maxUnder = newMaxUnder;
		return this;
	}

	public ExtendedFixture with1X2Odds(float newHomeOdds, float newDrawOdds, float newAwayOdds) {
		this.homeOdds = newHomeOdds;
		this.drawOdds = newDrawOdds;
		this.awayOdds = newAwayOdds;
		return this;
	}

	public ExtendedFixture withStatus(String newStatus) {
		this.status = newStatus;
		return this;
	}
	
	public ExtendedFixture withYear(int newYear) {
		this.year = newYear;
		return this;
	}

	public ExtendedFixture withHTResult(Result ht) {
		this.HTresult = ht;
		return this;
	}

	public ExtendedFixture withMatchday(int newMatchday) {
		this.matchday = newMatchday;
		return this;
	}

	public ExtendedFixture withShots(int newShotsHome, int newShotsAway) {
		this.shotsHome = newShotsHome;
		this.shotsAway = newShotsAway;
		return this;
	}

	public ExtendedFixture withAsian(float newLine, float newAsianHome, float newAsianAway) {
		this.line = newLine;
		this.asianHome = newAsianHome;
		this.asianAway = newAsianAway;
		return this;
	}

	public ExtendedFixture withCards(int newRedHome, int newRedAway) {
		this.redHome = newRedHome;
		this.redAway = newRedAway;
		return this;
	}

	public int getShotsTotal() {
		return shotsHome + shotsAway;
	}

	public int getTotalGoals() {
		return result.goalsHomeTeam + result.goalsAwayTeam;
	}

	public double getHalfTimeGoals() {

		return HTresult.goalsHomeTeam + HTresult.goalsAwayTeam;
	}

	public boolean bothTeamScore() {
		return ((result.goalsAwayTeam > 0) && (result.goalsHomeTeam > 0));
	}

	@Override
	public String toString() {
		return date + " " + status + "\n" + homeTeam + " " + result.goalsHomeTeam + " : " + result.goalsAwayTeam + " "
				+ awayTeam + "\n";
	}

	@Override
	public int compareTo(ExtendedFixture o) {
		return date.compareTo(o.date);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((awayTeam == null) ? 0 : awayTeam.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((homeTeam == null) ? 0 : homeTeam.hashCode());
		return result;
	}

	private static boolean controlAwayTeam(ExtendedFixture other)
	{
		if (awayTeam == null && other.awayTeam != null)
			return false;
		if (awayTeam != null && !awayTeam.equals(other.awayTeam))
			return false;
	}
	
	private static boolean controlDate(ExtendedFixture other)
	{
		if (date == null && other.date != null)
			return false;
		if (date != null && !date.equals(other.date))
			return false;
	}
	
	private static boolean controlHomeTeam(ExtendedFixture other)
	{
		if (homeTeam == null && other.homeTeam != null)
			return false;
		if (homeTeam != null && !homeTeam.equals(other.homeTeam))
			return false;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExtendedFixture other = (ExtendedFixture) obj;
		controlAwayTeam(other);
		controlDate(other);
		controlHomeTeam(other);
		return true;
	}

	public boolean isHomeWin() {
		return result.goalsHomeTeam > result.goalsAwayTeam;
	}

	public boolean isAwayWin() {
		return result.goalsHomeTeam < result.goalsAwayTeam;
	}

	public boolean isDraw() {
		return result.goalsHomeTeam == result.goalsAwayTeam;
	}

}
