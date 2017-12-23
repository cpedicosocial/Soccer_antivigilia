package tables;

/**
 * PJDCC - Summary for class responsabilities.
 *
 * @author fourplus <fourplus1718@gmail.com>
 * @since 1.0
 * @version 11 Changes done
 */
public class Position implements Comparable<Position> {
    /**
     * This field sets the variable of class String
     */
	public String team;
    /**
     * This field sets the int variable
     */
	public int played;
    /**
     * This field sets the int variable
     */
	public int wins;
    /**
     * This field sets the int variable
     */
	public int draws;
    /**
     * This field sets the int variable
     */
	public int losses;
    /**
     * This field sets the int variable
     */
	public int scored;
    /**
     * This field sets the int variable
     */
	public int conceded;
    /**
     * This field sets the int variable
     */
	public int diff;
    /**
     * This field sets the int variable
     */
	public int points;
    /**
     * This field sets the int variable
     */
	public int homeplayed;
    /**
     * This field sets the int variable
     */
	public int homewins;
    /**
     * This field sets the int variable
     */
	public int homedraws;
    /**
     * This field sets the int variable
     */
	public int homelosses;
    /**
     * This field sets the int variable
     */
	public int homescored;
    /**
     * This field sets the int variable
     */
	public int homeconceded;
    /**
     * This field sets the int variable
     */
	public int homediff;
    /**
     * This field sets the int variable
     */
	public int homepoints;
    /**
     * This field sets the int variable
     */
	public int awayplayed;
    /**
     * This field sets the int variable
     */
	public int awaywins;
    /**
     * This field sets the int variable
     */
	public int awaydraws;
    /**
     * This field sets the int variable
     */
	public int awaylosses;
    /**
     * This field sets the int variable
     */
	public int awayscored;
    /**
     * This field sets the int variable
     */
	public int awayconceded;
    /**
     * This field sets the int variable
     */
	public int awaydiff;
    /**
     * This field sets the int variable
     */
	public int awaypoints;

	@Override
	public int compareTo(Position o) {
		if (points != o.points)
			return ((Integer) points).compareTo((Integer) o.points);
		else if (diff != o.diff)
			return ((Integer) diff).compareTo((Integer) o.diff);
		else if (scored != o.scored)
			return ((Integer) scored).compareTo((Integer) o.scored);
		else
			return team.compareTo(o.team);

	}
}
