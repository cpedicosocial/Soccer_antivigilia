package odds;

import java.util.Date;

/**
 * PJDCC - Summary for class responsabilities.
 *
 * @author fourplus <fourplus1718@gmail.com>
 * @since 1.0
 * @version 11 Changes done
 */
public class MatchOdds extends Odds {
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

	public MatchOdds(String bookmaker, Date date, float homeOdds, float drawOdds, float awayOdds) {
		super();
		this.date = date;
		this.bookmaker = bookmaker;
		this.homeOdds = homeOdds;
		this.drawOdds = drawOdds;
		this.awayOdds = awayOdds;
	}

	@Override
	public float getMargin() {
		return 1f / homeOdds + 1f / drawOdds + 1f / awayOdds;
	}

	public String toString() {
		return bookmaker + "  " + homeOdds + "  " + drawOdds + "  " + awayOdds;
	}

	public float getHomeOdds() {
		return homeOdds;
	}

	public float getDrawOdds() {
		return drawOdds;
	}

	public void setDrawOdds(float drawOdds) {
		this.drawOdds = drawOdds;
	}

	public float getAwayOdds() {
		return awayOdds;
	}

	public void setAwayOdds(float awayOdds) {
		this.awayOdds = awayOdds;
	}

	public void setHomeOdds(float homeOdds) {
		this.homeOdds = homeOdds;
	}

	@Override
	public Odds getTrueOddsMarginal() {
		float margin = getMargin() - 1f;
		float truehomeOdds = 3 * homeOdds / (3f - margin * homeOdds);
		float truedrawOdds = 3 * drawOdds / (3f - margin * drawOdds);
		float trueawayOdds = 3 * awayOdds / (3f - margin * awayOdds);
		return new MatchOdds(bookmaker, date, truehomeOdds, truedrawOdds, trueawayOdds);
	}

}
