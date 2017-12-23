package main;

/**
 * PJDCC - Summary for class responsabilities.
 *
 * @author fourplus <fourplus1718@gmail.com>
 * @since 1.0
 * @version 11 Changes done
 */
public class Player {
    /**
     * This field sets the variable of class String
     */
	public String team;
    /**
     * This field sets the variable of class String
     */
	public String name;
    /**
     * This field sets the int variable
     */
	public int minutesPlayed;
    /**
     * This field sets the int variable
     */
	public int lineups;
    /**
     * This field sets the int variable
     */
	public int substitutes;
    /**
     * This field sets the int variable
     */
	public int subsWOP;// substitute but didn't participate in the match
    /**
     * This field sets the int variable
     */
	public int goals;
    /**
     * This field sets the int variable
     */
	public int assists;
    /**
     * This field sets the int variable
     */
	public int homeMinutesPlayed;
    /**
     * This field sets the int variable
     */
	public int homeLineups;
    /**
     * This field sets the int variable
     */
	public int homeSubstitutes;
    /**
     * This field sets the int variable
     */
	public int homeSubsWOP;// substitute but didn't participate in the match at
							// home
    /**
     * This field sets the int variable
     */
	public int homeGoals;
    /**
     * This field sets the int variable
     */
	public int homeAssists;
    /**
     * This field sets the int variable
     */
	public int awayMinutesPlayed;
    /**
     * This field sets the int variable
     */
	public int awayLineups;
    /**
     * This field sets the int variable
     */
	public int awaySubstitutes;
    /**
     * This field sets the int variable
     */
	public int awaySubsWOP;// substitute but didn't participate in the match at
							// away
    /**
     * This field sets the int variable
     */
	public int awayGoals;
    /**
     * This field sets the int variable
     */
	public int awayAssists;

	public String getName() {
		return name;
	}

	public Player(String team, String name, int minutesPlayed, int lineups, int substitutes, int subsWOP, int goals,
			int assists, int homeMinutesPlayed, int homeLineups, int homeSubstitutes, int homeSubsWOP, int homeGoals,
			int homeAssists, int awayMinutesPlayed, int awayLineups, int awaySubstitutes, int awaySubsWOP,
			int awayGoals, int awayAssists) {
		super();
		this.team = team;
		this.name = name;
		this.minutesPlayed = minutesPlayed;
		this.lineups = lineups;
		this.substitutes = substitutes;
		this.subsWOP = subsWOP;
		this.goals = goals;
		this.assists = assists;
		this.homeMinutesPlayed = homeMinutesPlayed;
		this.homeLineups = homeLineups;
		this.homeSubstitutes = homeSubstitutes;
		this.homeSubsWOP = homeSubsWOP;
		this.homeGoals = homeGoals;
		this.homeAssists = homeAssists;
		this.awayMinutesPlayed = awayMinutesPlayed;
		this.awayLineups = awayLineups;
		this.awaySubstitutes = awaySubstitutes;
		this.awaySubsWOP = awaySubsWOP;
		this.awayGoals = awayGoals;
		this.awayAssists = awayAssists;
	}

	public float getGoalAvg() {
		return minutesPlayed == 0 ? 0 : ((float) goals) / minutesPlayed;
	}

	public float getAssistAvg() {
		return minutesPlayed == 0 ? 0 : ((float) assists) / minutesPlayed;
	}

	@Override
	public String toString() {
		return "Player [team=" + team + ", name=" + name + ", minutesPlayed=" + minutesPlayed + ", lineups=" + lineups
				+ ", substitutes=" + substitutes + ", subsWOP=" + subsWOP + ", goals=" + goals + ", assists=" + assists
				+ "]";
	}

	public float getGoalAvgHome() {
		return homeMinutesPlayed == 0 ? 0 : ((float) homeGoals) / homeMinutesPlayed;
	}

	public float getAssistAvgHome() {
		return homeMinutesPlayed == 0 ? 0 : ((float) homeAssists) / homeMinutesPlayed;
	}

	public float getGoalAvgAway() {
		return awayMinutesPlayed == 0 ? 0 : ((float) awayGoals) / awayMinutesPlayed;
	}

	public float getAssistAvgAway() {
		return awayMinutesPlayed == 0 ? 0 : ((float) awayAssists) / awayMinutesPlayed;
	}

}
