package entries;

/**
 * PJDCC - Summary for class responsabilities.
 *
 * @author fourplus <fourplus1718@gmail.com>
 * @since 1.0
 * @version 11 Changes done
 */

public class AllEntry {
    /**
     * This field sets the variable of class final entry
     */
	public FinalEntry fe;
	public float zero;
	public float one;
	public float two;
	public float more;
	public float basic;
	public float poisson;
	public float weighted;
	public float shots;

	public AllEntry(FinalEntry fe, float zero, float one, float two, float more, float basic, float poisson,
			float weighted, float shots) {
		super();
		this.fe = fe;
		this.zero = zero;
		this.one = one;
		this.two = two;
		this.more = more;
		this.basic = basic;
		this.poisson = poisson;
		this.weighted = weighted;
		this.shots = shots;
	}


}
