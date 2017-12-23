package odds;

import java.util.Date;

/**
 * PJDCC - Summary for class responsabilities.
 *
 * @author fourplus <fourplus1718@gmail.com>
 * @since 1.0
 * @version 11 Changes done
 */

public class AsianOdds extends Odds {
    /**
     * This field sets the float variable
     */
	public float line;
    /**
     * This field sets the float variable
     */
	public float homeOdds;
    /**
     * This field sets the float variable
     */
	public float awayOdds;

	public AsianOdds(String bookmaker, Date date, float line, float homeOdds, float awayOdds) {
		super();
		this.date = date;
		this.bookmaker = bookmaker;
		this.line = line;
		this.homeOdds = homeOdds;
		this.awayOdds = awayOdds;
	}

	@Override
	public float getMargin() {
		return 1f / homeOdds + 1f / awayOdds;
	}

	public String toString() {
		return bookmaker + "  " + line + "  " + homeOdds + "  " + awayOdds;
	}

	@Override
	public Odds getTrueOddsMarginal() {
		float margin = 1f / homeOdds + 1f / awayOdds - 1f;
		float truehomeOdds = 2 * homeOdds / (2f - margin * homeOdds);
		float trueawayOdds = 2 * awayOdds / (2f - margin * awayOdds);
		return new AsianOdds(bookmaker, date, line, truehomeOdds, trueawayOdds);
	}

}
