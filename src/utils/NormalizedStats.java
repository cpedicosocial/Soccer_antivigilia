package utils;

import java.util.ArrayList;

import entries.FinalEntry;

/**
 * PJDCC - Summary for class responsabilities.
 *
 * @author fourplus <fourplus1718@gmail.com>
 * @since 1.0
 * @version 11 Changes done
 */
public class NormalizedStats extends Stats{

	public NormalizedStats(ArrayList<FinalEntry> all, String description) {
		super(all, description);
	}
	
	public float getProfit() {
		return Utils.getNormalizedProfit(all);
	}


	public float getYield() {
		return Utils.getNormalizedYield(all);
	}

	public float getPvalueOdds() {
		return Utils.evaluateRecordNormalized(all);
	}
	
}
