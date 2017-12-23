package settings;

/**
 * PJDCC - Summary for class responsabilities.
 *
 * @author fourplus <fourplus1718@gmail.com>
 * @since 1.0
 * @version 11 Changes done
 */
public class SettingsDraws {
    /**
     * This field sets the variable of class String
     */
	public String league;
    /**
     * This field sets the int variable
     */
	public int year;
    /**
     * This field sets the float variable
     */
	public float basic;
    /**
     * This field sets the float variable
     */
	public float poisson;
    /**
     * This field sets the float variable
     */
	public float value;
    /**
     * This field sets the float variable
     */
	public float profit;

	public SettingsDraws(String league, int year, float basic, float poisson, float value, float profit) {
		this.league = league;
		this.year = year;
		this.basic = basic;
		this.poisson = poisson;
		this.value = value;
		this.profit = profit;
	}

	private String format(float d) {
		return String.format("%.2f", d);
	}

	@Override
	public String toString() {
		return league + " bas*" + format(basic) + " poi*" + format(poisson) + format(value) + format(profit);
	}

}
