package settings;

/**
 * PJDCC - Summary for class responsabilities.
 *
 * @author fourplus <fourplus1718@gmail.com>
 * @since 1.0
 * @version 11 Changes done
 */
public class ShotsSettings {
	float thold;
	float cotOvers;
	float cotUnders;
    /**
     * This field sets the boolean variable
     */
	public boolean onlyOvers;
    /**
     * This field sets the boolean variable
     */
	public boolean onlyUnders;
    /**
     * This field sets the boolean variable
     */
	public boolean doNotPlay;

	public ShotsSettings() {
	}

	public ShotsSettings withTH(float th) {
		thold = th;
		return this;
	}

	public ShotsSettings withCot(float newCotOvers, float newCotUnders) {
		this.cotOvers = newCotOvers;
		this.cotUnders = newCotUnders;
		return this;
	}

	public void onlyUnders() {
		onlyUnders = true;
	}

	public void onlyOvers() {
		onlyOvers = true;
	}

	public void doNotPlay() {
		doNotPlay = true;
	}

}
