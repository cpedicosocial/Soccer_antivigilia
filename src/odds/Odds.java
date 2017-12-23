package odds;

import java.util.Date;

/**
 * PJDCC - Summary for class responsabilities.
 *
 * @author fourplus <fourplus1718@gmail.com>
 * @since 1.0
 * @version 11 Changes done
 */
public abstract class Odds {
    /**
     * This field sets the variable of class String
     */
	public String bookmaker;
    /**
     * This field sets the variable of class Date
     */
	public Date date;
    /**
     * This field sets the variable of class boolean
     */
	public boolean isOpening;

	public abstract float getMargin();

	public abstract Odds getTrueOddsMarginal();

}
