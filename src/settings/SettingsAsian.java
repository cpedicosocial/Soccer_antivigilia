package settings;

/**
 * PJDCC - Summary for class responsabilities.
 *
 * @author fourplus <fourplus1718@gmail.com>
 * @since 1.0
 * @version 11 Changes done
 */
public class SettingsAsian {
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
	public float expectancy;
    /**
     * This field sets the float variable
     */
	public float profit;

	public SettingsAsian(String league, int year, float basic, float poisson, float expectancy, float profit) {
		super();
		this.league = league;
		this.year = year;
		this.basic = basic;
		this.poisson = poisson;
		this.expectancy = expectancy;
		this.profit = profit;
	}

	private String format(float d) {
		return String.format("%.2f", d);
	}

	@Override
	public String toString() {
		return league + " bas*" + format(basic) + " poi*" + format(poisson) + format(expectancy) + format(profit);
	}

}
