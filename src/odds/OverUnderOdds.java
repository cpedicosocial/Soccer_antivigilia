package odds;

import java.util.Date;

/**
 * PJDCC - Summary for class responsabilities.
 *
 * @author fourplus <fourplus1718@gmail.com>
 * @since 1.0
 * @version 11 Changes done
 */
public class OverUnderOdds extends Odds {
    /**
     * This field sets the float variable
     */
	public float line;
    /**
     * This field sets the float variable
     */
	public float overOdds;
    /**
     * This field sets the float variable
     */
	public float underOdds;

	public OverUnderOdds(String bookmaker, Date date, float line, float overOdds, float underOdds) {
		super();
		this.date = date;
		this.bookmaker = bookmaker;
		this.line = line;
		this.overOdds = overOdds;
		this.underOdds = underOdds;
	}

	@Override
	public float getMargin() {
		return 1f / overOdds + 1f / underOdds;
	}

	public String toString() {
		return bookmaker + "  " + line + "  " + overOdds + "  " + underOdds;
	}

	@Override
	public Odds getTrueOddsMarginal() {
		float margin = 1f / overOdds + 1f / underOdds - 1f;
		float trueOverOdds = 2 * overOdds / (2f - margin * overOdds);
		float trueUnderOdds = 2 * underOdds / (2f - margin * underOdds);
		return new OverUnderOdds(bookmaker, date, line, trueOverOdds, trueUnderOdds);
	}
}
