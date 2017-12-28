package utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * PJDCC - Summary for class responsabilities.
 *
 * @author fourplus <fourplus1718@gmail.com>
 * @since 1.0
 * @version 11 Changes done
 */
public class UtilsControls_3 {
	
	static int failtimesControls(boolean flag,int failtimes){
		if (flag)
			failtimes++;
		return failtimes;
	}
	static double totalControls(boolean flag,int total,float bankroll){
		if (!flag)
			total += bankroll;
		return total;
	}
	static int flagBankrollControls(boolean flag, int total, float bankroll, int losses){
		if (!flag && bankroll < 1000f)
			losses++;
		return losses;
	}
	
	static ArrayList<PlayerFixture> getPlayers(ExtendedFixture i, boolean home, ArrayList<PlayerFixture> pfs,
			HashMap<String, String> dictionary) {
		HashMap<String, String> reverseDictionary = (HashMap<String, String>) dictionary.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

		ArrayList<PlayerFixture> result = new ArrayList<>();
		for (PlayerFixture pf : pfs) {
			if (reverseDictionary.get(pf.fixture.homeTeam).equals(i.homeTeam)
					&& reverseDictionary.get(pf.fixture.awayTeam).equals(i.awayTeam)
					&& (pf.fixture.date.equals(i.date) || pf.fixture.date.equals(Utils.getYesterday(i.date))
							|| pf.fixture.date.equals(Utils.getTommorow(i.date)))
					&& reverseDictionary.get(pf.team).equals(home ? i.homeTeam : i.awayTeam)) {
				result.add(pf);
			}
		}
		result.sort(new Comparator<PlayerFixture>() {

			@Override
			public int compare(PlayerFixture o1, PlayerFixture o2) {
				return ((Integer) o2.minutesPlayed).compareTo((Integer) o1.minutesPlayed);
			}
		});
		return result;
	}
	
	static Settings createSettings(){
		return new Settings("", 0f, 0f, 0f, 0.55f, 0.55f, 0.55f, 0.5f, 0f).withShots(1f);
	}
	
	static float evaluateForBestSettingsWithParameters(int newStart, int newEnd, int newOffset,
			HashMap<String, HashMap<Integer, ArrayList<FinalEntry>>> byLeagueYear, float newLowerValue, float newUpperValue,
			float newStep) {
		float optimalValue = Float.NEGATIVE_INFINITY;
		float optimalResult = Float.NEGATIVE_INFINITY;

		for (float curr = newLowerValue; curr <= newUpperValue; curr += newStep) {
			ArrayList<FinalEntry> result = createFinalEntry();

			for (int i = newStart + newOffset; i <= newEnd; i++) {
				for (java.util.Map.Entry<String, HashMap<Integer, ArrayList<FinalEntry>>> league : byLeagueYear
						.entrySet()) {
					ArrayList<FinalEntry> current = Utils.deepCopy(league.getValue().get(i));

					ArrayList<FinalEntry> data = createFinalEntry();
					for (int j = i - newOffset; j < i; j++)
						if (league.getValue().containsKey(j))
							data.addAll(league.getValue().get(j));

					// analysys(data, -1, false);
					result.addAll(filterByPastResults(current, data, curr));

					// Settings initial = new Settings("", 0f, 0f, 0f, 0.55f,
					// 0.55f,
					// 0.55f, 0.5f, 0f).withShots(1f);
					// initial = XlSUtils.findThreshold(data, initial, maxBy);
					// ArrayList<FinalEntry> toAdd = XlSUtils.restrict(current,
					// initial);

					// if (maxBy.equals(MaximizingBy.UNDERS))
					// toAdd = onlyUnders(toAdd);
					// else if (maxBy.equals(MaximizingBy.OVERS))
					// toAdd = onlyOvers(toAdd);

					// withTH.addAll(toAdd);

				}
			}

			float currentEvaluation = evaluateRecord(result);
			if (currentEvaluation > optimalResult) {
				optimalResult = currentEvaluation;
				optimalValue = curr;
			}
		}

		return optimalValue;
	}

	// just all unders or overs for now
	static Collection<? extends FinalEntry> filterByPastResults(ArrayList<FinalEntry> current,
			ArrayList<FinalEntry> data, float onein) {
		ArrayList<FinalEntry> result = new ArrayList<>();
		boolean both = evaluateRecord(data) > onein;
		boolean onlyUnders = evaluateRecord(onlyUnders(data)) > onein;
		boolean onlyOvers = evaluateRecord(onlyOvers(data)) > onein;

		// if (both)
		// result.addAll(current);
		// if (onlyUnders)
		// result.addAll(onlyUnders(current));
		if (onlyOvers)
			result.addAll(onlyOvers(current));

		return result;
	}

	/**
	 * Deep copy list of finals
	 * 
	 * @param arrayList
	 * @return
	 */
	static ArrayList<FinalEntry> deepCopy(ArrayList<FinalEntry> finals) {
		return (ArrayList<FinalEntry>) finals.stream().map(i -> new FinalEntry(i)).collect(Collectors.toList());
	}
	
	static ArrayList<HTEntry> onlyOversHT(ArrayList<HTEntry> finals) {
		ArrayList<HTEntry> result = new ArrayList<>();
		for (HTEntry i : finals) {
			if (i.fe.prediction >= i.fe.threshold)
				result.add(i);
		}
		return result;
	}

	static ArrayList<HTEntry> onlyUndersHT(ArrayList<HTEntry> finals) {
		ArrayList<HTEntry> result = new ArrayList<>();
		for (HTEntry i : finals) {
			if (i.fe.prediction < i.fe.threshold)
				result.add(i);
		}
		return result;
	}

	/**
	 * Returns list of final entries from list of ht entries
	 * 
	 * @param all
	 * @return
	 */
	static ArrayList<FinalEntry> getFinals(ArrayList<HTEntry> all) {
		ArrayList<FinalEntry> result = new ArrayList<>();
		for (HTEntry i : all) {
			result.add(i.fe);
		}

		return result;
	}

	/**
	 * Returns profit from a list of half time entries
	 * 
	 * @param all
	 * @return
	 */
	static float getProfitHT(ArrayList<HTEntry> all) {
		float profit = 0f;
		for (HTEntry i : all) {
			profit += i.fe.getProfit();
		}
		return profit;
	}

	static ArrayList<HTEntry> createHTEntry(){
		return new ArrayList<HTEntry>(); 
	}
	
	public static float similarPoisson(ExtendedFixture f, HSSFSheet sheet, Table table) throws ParseException {
		ArrayList<String> filterHome = table.getSimilarTeams(f.awayTeam);
		ArrayList<String> filterAway = table.getSimilarTeams(f.homeTeam);

		ArrayList<ExtendedFixture> lastHomeTeam = filter(f.homeTeam,
				XlSUtils.selectLastAll(sheet, f.homeTeam, 50, f.date), filterHome);
		ArrayList<ExtendedFixture> lastAwayTeam = filter(f.awayTeam,
				XlSUtils.selectLastAll(sheet, f.awayTeam, 50, f.date), filterAway);

		float lambda = Utils.avgFor(f.homeTeam, lastHomeTeam);
		float mu = Utils.avgFor(f.awayTeam, lastAwayTeam);
		return Utils.poissonOver(lambda, mu);
	}
	
}
