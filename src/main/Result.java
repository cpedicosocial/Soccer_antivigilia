package main;

/**
 * PJDCC - Summary for class responsabilities.
 *
 * @author fourplus <fourplus1718@gmail.com>
 * @since 1.0
 * @version 11 Changes done
 */
public class Result {
    /**
     * This field sets the int variable
     */
	public int goalsHomeTeam;
    /**
     * This field sets the int variable
     */
	public int goalsAwayTeam;

	public Result(int goalsHomeTeam, int goalsAwayTeam) {
		this.goalsHomeTeam = goalsHomeTeam;
		this.goalsAwayTeam = goalsAwayTeam;
	}

	/**
	 * Copy constructor
	 * 
	 * @param result
	 */
	public Result(Result result) {
		this.goalsHomeTeam = result.goalsHomeTeam;
		this.goalsAwayTeam = result.goalsAwayTeam;
	}

	public String toString() {
		return goalsHomeTeam + " : " + goalsAwayTeam;
	}

	public boolean equals(Result other) {
		return goalsAwayTeam == other.goalsAwayTeam && goalsHomeTeam == other.goalsHomeTeam;
	}
}
