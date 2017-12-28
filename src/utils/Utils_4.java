package utils;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * PJDCC - Summary for class responsabilities.
 *
 * @author fourplus <fourplus1718@gmail.com>
 * @since 1.0
 * @version 11 Changes done
 */
public class Utils_4 {
	/**
	 * Method for verifying two list of fixtures are the same (only difference
	 * in naming the clubs)
	 * 
	 * @param teamFor
	 * 
	 * @param fixtures
	 * @param fwa
	 * @return
	 */
	public static boolean matchesFixtureLists(String teamFor, ArrayList<ExtendedFixture> fixtures,
			ArrayList<ExtendedFixture> fwa) {
		for (ExtendedFixture i : fixtures) {
			boolean foundMatch = false;
			boolean isHomeSide = teamFor.equals(i.homeTeam);
			for (ExtendedFixture j : fwa) {
				if (Math.abs(i.date.getTime() - j.date.getTime()) <= 24 * 60 * 60 * 1000 && i.result.equals(j.result)) {
					foundMatch = true;
					break;
				}
			}
			if (!foundMatch)
				return false;

		}

		return true;
	}

	public static ArrayList<FinalEntry> specificLine(ArrayList<FinalEntry> finals,
			HashMap<ExtendedFixture, FullFixture> map, float line) {
		ArrayList<FinalEntry> fulls = new ArrayList<>();

		for (FinalEntry f : finals) {
			FullEntry full = new FullEntry(f.fixture, f.prediction, f.result, f.threshold, f.lower, f.upper, null);
			for (Line l : map.get(f.fixture).goalLines.getArrayLines()) {
				if (Float.compare(l.line, line)==0) {
					full.line = l;
					fulls.add(full);
					continue;
				}
			}

		}

		return fulls;
	}

	/**
	 * Calculates the similarity (a number within 0 and 1) between two strings.
	 */
	public static double similarity(String s1, String s2) {
		String longer = s1, shorter = s2;
		if (s1.length() < s2.length()) { // longer should always have greater
											// length
			longer = s2;
			shorter = s1;
		}
		int longerLength = longer.length();
		if (longerLength == 0) {
			return 1.0;
			/* both strings are zero length */ }
		/*
		 * // If you have StringUtils, you can use it to calculate the edit
		 * distance: return (longerLength -
		 * StringUtils.getLevenshteinDistance(longer, shorter)) / (double)
		 * longerLength;
		 */
		return (longerLength - editDistance(longer, shorter)) / (double) longerLength;

	}

	// Example implementation of the Levenshtein Edit Distance
	// See http://rosettacode.org/wiki/Levenshtein_distance#Java
	public static int editDistance(String s1, String s2) {
		s1 = s1.toLowerCase();
		s2 = s2.toLowerCase();

		int[] costs = new int[s2.length() + 1];
		for (int i = 0; i <= s1.length(); i++) {
			int lastValue = i;
			for (int j = 0; j <= s2.length(); j++) {
				if (i == 0)
					costs[j] = j;
				else {
					if (j > 0) {
						int newValue = costs[j - 1];
						if (s1.charAt(i - 1) != s2.charAt(j - 1))
							newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
						costs[j - 1] = lastValue;
						lastValue = newValue;
					}
				}
			}
			if (i > 0)
				costs[s2.length()] = lastValue;
		}
		return costs[s2.length()];
	}

	public static ArrayList<FinalEntry> estimateOposite(ArrayList<ExtendedFixture> current,
			HashMap<ExtendedFixture, FullFixture> map, HSSFSheet sheet) throws ParseException {
		ArrayList<FinalEntry> fulls = new ArrayList<>();
		for (ExtendedFixture f : current) {
			FullEntry fe = worse(f, estimateBoth(f, map, sheet), f.line, f.asianHome, f.asianAway, map);
			fulls.add(fe);
		}

		return fulls;
	}

	/**
	 * The oposite of the main goal line with advantage
	 * 
	 * @param f
	 * @param map
	 * @param sheet
	 * @return
	 * @throws ParseException
	 */
	
	public static float outcomes(ArrayList<String> results, float coeff) {
		int wins = 0;
		int halfwins = 0;
		int draws = 0;
		int halflosses = 0;
		int losses = 0;
		for (String i : results) {
			if (i.equals("W"))
				wins++;
			else if (i.equals("HW")) {
				halfwins++;
			} else if (i.equals("D")) {
				draws++;
			} else if (i.equals("HL")) {
				halflosses++;
			} else {
				losses++;
			}

		}

		return ((float) wins / results.size()) * coeff + ((float) halfwins / results.size()) * (1 + (coeff - 1) / 2)
		/* + ((float) draws / results.size()) */ - ((float) halflosses / results.size()) / 2
				- ((float) losses / results.size());
	}

	public static LinearRegression getRegression(String homeTeam, ArrayList<ExtendedFixture> lastHome) {
		ArrayList<Double> homeGoals = new ArrayList<>();
		ArrayList<Double> homeShots = new ArrayList<>();
		for (ExtendedFixture i : lastHome) {
			if (i.homeTeam.equals(homeTeam)) {
				homeGoals.add((double) i.result.goalsHomeTeam);
				homeShots.add((double) i.shotsHome);
			} else {
				homeGoals.add((double) i.result.goalsAwayTeam);
				homeShots.add((double) i.shotsAway);
			}
		}

		double[] xhome = new double[homeGoals.size()];
		double[] yhome = new double[homeGoals.size()];

		for (int i = 0; i < homeGoals.size(); i++) {
			xhome[i] = homeShots.get(i);
			yhome[i] = homeGoals.get(i);
		}

		return new LinearRegression(xhome, yhome);
	}

	public static ArrayList<FinalEntry> removePending(ArrayList<FinalEntry> finals) {
		ArrayList<FinalEntry> result = new ArrayList<>();
		for (FinalEntry i : finals) {
			if (i.fixture.getTotalGoals() >= 0)
				result.add(i);
		}
		return result;
	}

	public static float predictionCorrelation(ArrayList<FinalEntry> all) {
		Integer[] totalGoals = new Integer[all.size()];
		Integer[] predictions = new Integer[all.size()];
		for (int i = 0; i < all.size(); i++) {
			totalGoals[i] = all.get(i).fixture.getTotalGoals();
			predictions[i] = (int) (all.get(i).prediction * 1000);
		}

		System.out.println("Correlation is: " + Utils.correlation(totalGoals, predictions));
		return Utils.correlation(totalGoals, predictions);
	}

	/**
	 * zero based months
	 * 
	 * @param current
	 */

	public static ArrayList<ExtendedFixture> inMonth(ArrayList<ExtendedFixture> current, int i, int j) {
		ArrayList<ExtendedFixture> result = new ArrayList<>();
		for (ExtendedFixture c : current) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(c.date);
			int month = cal.get(Calendar.MONTH);
			if (month >= i && month <= j)
				result.add(c);
		}
		return result;
	}

	public static ArrayList<FinalEntry> similarRanking(ArrayList<FinalEntry> finals, Table table) {
		ArrayList<FinalEntry> result = new ArrayList<>();
		for (FinalEntry c : finals) {
			if ((table.getMiddleTeams().contains((c.fixture.homeTeam))
					&& table.getMiddleTeams().contains((c.fixture.awayTeam)))
			)
				result.add(c);
		}
		return result;
	}

	public static ArrayList<FinalEntry> runWithPlayersData(ArrayList<ExtendedFixture> current,
			ArrayList<PlayerFixture> pfs, HashMap<String, String> dictionary, ArrayList<ExtendedFixture> all, float th)
					throws ParseException {
		ArrayList<FinalEntry> result = new ArrayList<>();
		for (ExtendedFixture i : current) {
			float eval = evaluatePlayers(i, pfs, dictionary, all);
			FinalEntry fe = new FinalEntry(i, eval/* >=0.55f ? 0f : 1f */, i.result, th, th, th);
			// fe.prediction = fe.isOver() ? 0f : 1f;
			if (fe.getValue() > 1.05f)
				result.add(fe);
		}
		return result;
	}

	public static float evaluatePlayers(ExtendedFixture ef, ArrayList<PlayerFixture> pfs,
			HashMap<String, String> dictionary, ArrayList<ExtendedFixture> all) throws ParseException 
	{
		// The shots data from soccerway(opta) does not add the goals as shots,
		// must be added for more accurate predictions and equivalancy with
		// alleurodata
		boolean manual = Arrays.asList(MinMaxOdds.MANUAL).contains(ef.competition);
		float goalsWeight = 1f;

		float homeEstimate = estimateGoalFromPlayerStats(ef, pfs, dictionary, true, all);
		float awayEstimate = estimateGoalFromPlayerStats(ef, pfs, dictionary, false, all);

		// -----------------------------------------------
		// shots adjusted with pfs team expectancy)
		Pair avgShotsGeneral = FixtureUtils.selectAvgShots(all, ef.date, manual, goalsWeight);
		float avgHome = avgShotsGeneral.home;
		float avgAway = avgShotsGeneral.away;
		Pair avgShotsHomeTeam = FixtureUtils.selectAvgShotsHome(all, ef.homeTeam, ef.date, manual, goalsWeight);
		float homeShotsFor = avgShotsHomeTeam.home;
		float homeShotsAgainst = avgShotsHomeTeam.away;
		Pair avgShotsAwayTeam = FixtureUtils.selectAvgShotsAway(all, ef.awayTeam, ef.date, manual, goalsWeight);
		float awayShotsFor = avgShotsAwayTeam.home;
		float awayShotsAgainst = avgShotsAwayTeam.away;

		float lambda = Float.compare(avgAway, 0f)==0 ? 0 : homeShotsFor * awayShotsAgainst / avgAway;
		float mu = Float.compare(avgHome, 0f)==0 ? 0 : awayShotsFor * homeShotsAgainst / avgHome;

		Pair avgShotsByType = FixtureUtils.selectAvgShotsByType(all, ef.date, manual, goalsWeight);
		float avgShotsUnder = avgShotsByType.home;
		float avgShotsOver = avgShotsByType.away;
		float expected = homeEstimate * lambda + awayEstimate * mu;

		float dist = avgShotsOver - avgShotsUnder;

		float returned=0f;
		returned=UtilsControls.controlAvg(avgShotsUnder,avgShotsOver);
		returned=UtilsControls.controlExpectedGreater(expected,avgShotsUnder,avgShotsOver,dist);
		returned=UtilsControls.controlExpectedSmaller(expected,avgShotsUnder,avgShotsOver,dist);
		return returned;

	}

	// TODO remove later
	public static void printPlayers(ArrayList<PlayerFixture> homePlayers) {
		if (homePlayers.size() > 0)
			System.out.println(homePlayers.get(0).team);
		for (PlayerFixture i : homePlayers) {
			System.out.println(i.name + " " + i.lineup + " " + i.minutesPlayed + "' " + i.goals + " " + i.assists);
		}
	}

	public static ArrayList<ExtendedFixture> getFixtures(ArrayList<PlayerFixture> pfs) {
		ArrayList<ExtendedFixture> result = new ArrayList<>();
		for (PlayerFixture i : pfs) {
			if (!result.contains(i.fixture))
				result.add(i.fixture);
		}
		return result;
	}

	public static ArrayList<ExtendedFixture> notPending(ArrayList<ExtendedFixture> all) {
		ArrayList<ExtendedFixture> result = new ArrayList<>();
		for (ExtendedFixture i : all) {
			if (i.getTotalGoals() >= 0)
				result.add(i);
		}
		return result;
	}

	public static ArrayList<ExtendedFixture> pending(ArrayList<ExtendedFixture> all) {
		ArrayList<ExtendedFixture> result = new ArrayList<>();
		for (ExtendedFixture i : all) {
			if (i.getTotalGoals() < 0)
				result.add(i);
		}
		return result;
	}

	public static ArrayList<PlayerFixture> removeRepeats(ArrayList<PlayerFixture> all) {
		ArrayList<PlayerFixture> result = new ArrayList<>();

		for (PlayerFixture i : all) {
			boolean repeat = false;
			for (PlayerFixture j : result) {
				if (i.fixture.equals(j.fixture) && i.team.equals(j.team) && i.name.equals(j.name))
					repeat = true;
				break;
			}
			if (!repeat)
				result.add(i);
		}
		return result;
	}

	public static HashMap<String, ArrayList<FinalEntry>> byLeague(ArrayList<FinalEntry> all) {
		HashMap<String, ArrayList<FinalEntry>> leagues = new HashMap<>();
		for (FinalEntry i : all) {
			if (!leagues.containsKey(i.fixture.competition))
				leagues.put(i.fixture.competition, new ArrayList<>());

			leagues.get(i.fixture.competition).add(i);
		}
		return leagues;
	}

	/**
	 * Running precomputed finals with best TH from previous offset seasons
	 * 
	 * @param byLeagueYear
	 *            - hash map of finals by competition and year
	 * @param offset
	 *            - number of previous seasons based on which data the best th
	 *            will be computed
	 * @return
	 */
	public static ArrayList<FinalEntry> withBestThreshold(
			HashMap<String, HashMap<Integer, ArrayList<FinalEntry>>> byLeagueYear, int offset, MaximizingBy maxBy) {
		ArrayList<FinalEntry> withTH = new ArrayList<>();

		int start = Integer.MAX_VALUE;
		int end = Integer.MIN_VALUE;

		for (java.util.Map.Entry<String, HashMap<Integer, ArrayList<FinalEntry>>> league : byLeagueYear.entrySet()) {
			start = Math.min(start, league.getValue().keySet().stream().min(Integer::compareTo).get());
			end = Math.max(start, league.getValue().keySet().stream().max(Integer::compareTo).get());
		}

		for (int i = start + offset; i <= end; i++) {
			for (java.util.Map.Entry<String, HashMap<Integer, ArrayList<FinalEntry>>> league : byLeagueYear
					.entrySet()) {
				ArrayList<FinalEntry> current = Utils.deepCopy(league.getValue().get(i));

				ArrayList<FinalEntry> data = createFinalEntry();
				for (int j = i - offset; j < i; j++)
					if (league.getValue().containsKey(j))
						data.addAll(league.getValue().get(j));

				Settings initial = createSettings();
				initial = XlSUtils.findThreshold(data, initial, maxBy);
				ArrayList<FinalEntry> toAdd = XlSUtils.restrict(current, initial);

				if (maxBy.equals(MaximizingBy.UNDERS))
					toAdd = onlyUnders(toAdd);
				else if (maxBy.equals(MaximizingBy.OVERS))
					toAdd = onlyOvers(toAdd);

				withTH.addAll(toAdd);

			}
		}
		return withTH;
	}

	public static ArrayList<FinalEntry> withBestSettings(
			HashMap<String, HashMap<Integer, ArrayList<FinalEntry>>> byLeagueYear, int offset) {
		ArrayList<FinalEntry> result = new ArrayList<>();
		int start = Integer.MAX_VALUE;
		int end = Integer.MIN_VALUE;

		for (java.util.Map.Entry<String, HashMap<Integer, ArrayList<FinalEntry>>> league : byLeagueYear.entrySet()) {
			start = Math.min(start, league.getValue().keySet().stream().min(Integer::compareTo).get());
			end = Math.max(start, league.getValue().keySet().stream().max(Integer::compareTo).get());
		}

		float optimalOneIn = evaluateForBestSettingsWithParameters(start, end, offset, byLeagueYear, 2f, 200f, 0.5f);
		System.out.println("Optimal 1 in is: " + optimalOneIn);
		for (int i = start + offset; i <= end; i++) {
			for (java.util.Map.Entry<String, HashMap<Integer, ArrayList<FinalEntry>>> league : byLeagueYear
					.entrySet()) {
				ArrayList<FinalEntry> current = Utils.deepCopy(league.getValue().get(i));

				ArrayList<FinalEntry> data = createFinalEntry();
				for (int j = i - offset; j < i; j++)
					if (league.getValue().containsKey(j))
						data.addAll(league.getValue().get(j));

				result.addAll(filterByPastResults(current, data, optimalOneIn));

			}
		}

		return result;
	}

	/**
	 * Helper function for finding optimal value for withBestSettings method
	 * 
	 * @param start
	 * @param end
	 * @param offset
	 * @param byLeagueYear
	 * @param lowerValue
	 * @param upperValue
	 */
	

	public static ArrayList<FinalEntry> notPendingFinals(ArrayList<FinalEntry> all) {
		ArrayList<FinalEntry> result = new ArrayList<>();
		for (FinalEntry i : all) {
			if (i.fixture.getTotalGoals() >= 0)
				result.add(i);
		}
		return result;
	}

	public static ArrayList<FinalEntry> pendingFinals(ArrayList<FinalEntry> all) {
		ArrayList<FinalEntry> result = new ArrayList<>();
		for (FinalEntry i : all) {
			if (i.fixture.getTotalGoals() < 0)
				result.add(i);
		}
		return result;
	}

	public static ArrayList<FinalEntry> todayGames(ArrayList<FinalEntry> value) {
		return (ArrayList<FinalEntry>) value.stream().filter(i -> isToday(i.fixture.date)).collect(Collectors.toList());
	}

	public static void byYear(ArrayList<FinalEntry> all, String description) {
		System.out.println(description);
		Map<Object, List<FinalEntry>> map = all.stream().collect(Collectors.groupingBy(p -> (Integer) p.fixture.year,
				Collectors.mapping(Function.identity(), Collectors.toList())));

		for (Entry<Object, List<FinalEntry>> i : map.entrySet()) {
			System.out.println(new Stats((ArrayList<FinalEntry>) i.getValue(), ((Integer) i.getKey()).toString()));
		}

		System.out.println(new Stats(all, description));
	}

	public static void byCompetition(ArrayList<FinalEntry> all, String description) {
		System.out.println(description);
		Map<Object, List<FinalEntry>> map = all.stream().collect(Collectors.groupingBy(p -> p.fixture.competition,
				Collectors.mapping(Function.identity(), Collectors.toList())));

		for (Entry<Object, List<FinalEntry>> i : map.entrySet()) {
			System.out.println(new Stats((ArrayList<FinalEntry>) i.getValue(), (i.getKey()).toString()));
		}

		System.out.println(new Stats(all, description));
	}

	/**
	 * Mutably changes the predictions of a list of finals to (prediction +
	 * x*impliedProb)/(x+1) where x is the weight of the implied probability of
	 * the odds
	 * 
	 * @param all
	 * @param oddsImpliedProbabilityWeight
	 */
	public static void weightedPredictions(ArrayList<FinalEntry> all, float oddsImpliedProbabilityWeight) {
		for (FinalEntry i : all) {
			float gain = i.prediction > i.threshold ? i.fixture.maxOver : i.fixture.maxUnder;
			i.prediction = (i.prediction + oddsImpliedProbabilityWeight / gain) / (oddsImpliedProbabilityWeight + 1f);
		}
	}

	/**
	 * Finds the best half time evaluatuan representing linear combination of
	 * the average frequencies of 0,1,2 and more half time goals averages for
	 * both teams The data is selected from database
	 * 
	 * @param start
	 * @param end
	 * @param dataType
	 * @throws InterruptedException
	 */
	public static void optimalHTSettings(int newStart, int newEnd, DataType dataType, MaximizingBy newMaxBy)
			throws InterruptedException 
	{
		ArrayList<HTEntry> all = new ArrayList<>();
		for (int i = newStart; i <= newEnd; i++) {
			ArrayList<HTEntry> finals = createHTEntry();
			for (String comp : Arrays.asList(MinMaxOdds.SHOTS)) {
				finals.addAll(SQLiteJDBC.selectHTData(comp, i, "ht"));
			}
			all.addAll(finals);
		}
		float step = 0.1f;
		float bestProfit = Float.NEGATIVE_INFINITY;
		float bestWinRatio = 0f;
		String bestDescription = null;
		float bestx, besty, bestz, bestw;
		bestx = besty = bestz = bestw = 0f;
		float bestTH = 0.3f;
		float bestEval = 1f;
		for (int i = 0; i <= 0; i++) {
			float currentTH = 0.22f + i * 0.01f;
			System.out.println(currentTH);
			for (HTEntry hte : all) {
				hte.fe.threshold = currentTH;
				hte.fe.lower = currentTH;
				hte.fe.upper = currentTH;
			}
			int xmax = (int) (1f / step);
			for (int x = 0; x <= xmax; x++) {
				int ymax = xmax - x;
				for (int y = 0; y <= ymax; y++) {
					float currEval = 1f;
					currEval=UtilsControls.controlLoop(x,y,step,all,ymax,newMaxBy,currentTH,
							bestx,besty,bestz,bestw,bestTH,bestEval,bestDescription,bestProfit,bestWinRatio);
				}
			}
		}

		for (HTEntry hte : all) {
			hte.fe.prediction = bestx * hte.zero + besty * hte.one + bestz * hte.two + bestw * hte.more;
			hte.fe.threshold = bestTH;
			hte.fe.lower = bestTH;
			hte.fe.upper = bestTH;
		}
		all=UtilsControls.controlNewMaxBy5(newMaxBy,all); 
		System.out.println(bestProfit);
		System.out.println(bestTH);
		System.out.println("1 in " + bestEval);
		System.out.println(new Stats(getFinals(all), bestDescription));

	}
	
	public static void fastSearch(int newStart, int newEnd, DataType dataType, MaximizingBy newMaxBy)
			throws InterruptedException {
		ArrayList<HTEntry> all = new ArrayList<>();

		for (int i = newStart; i <= newEnd; i++) {
			ArrayList<HTEntry> finals = createHTEntry();
			for (String comp : Arrays.asList(MinMaxOdds.SHOTS)) {
				finals.addAll(SQLiteJDBC.selectHTData(comp, i, "ht"));
			}
			all.addAll(finals);
		}

		float step = 0.0005f;

		float bestProfit = Float.NEGATIVE_INFINITY;
		String bestDescription = null;
		float bestx, besty = 0, bestz, bestw;
		bestx = 0f;
		float bestTH = 0.3f;
		float bestEval = 1f;

		int xmax = (int) (1f / step);
		for (int x = 0; x <= xmax; x++) {
			int y = xmax - x;

			for (HTEntry hte : all) {
				hte.fe.prediction = x * step * hte.zero + y * step * hte.one;
			}

			float currentProfit, currentWinRate = 0f;
			float currEval = 1f;
			
			currEval=UtilsControls.controlNewMaxByAll(newMaxBy,all,currentProfit,currEval);
			currEval=UtilsControls.controlNewMaxByUnders(newMaxBy,all,currentProfit,currEval);
			currEval=UtilsControls.controlNewMaxByOvers(newMaxBy,all,currentProfit,currEval);
			
			System.out.println(x * step + " " + y * step);
			System.out.println(currentProfit);
			System.out.println("1 in " + currEval);

			bestDescription=UtilsControls.controlNewMaxByFinal(currEval,bestEval,currentProfit,bestProfit,step,bestx,x,besty,y,bestDescription);
		}

		for (HTEntry hte : all) {
			hte.fe.prediction = bestx * hte.zero + besty * hte.one;
		}

		all=UtilsControls.controlNewMaxByEquals(newMaxBy,all);
		System.out.println(bestProfit);
		System.out.println(bestTH);
		System.out.println("1 in " + bestEval);
		System.out.println(new Stats(getFinals(all), bestDescription));

	}

	/**
	 * Return exactly the oposite prediction for all finals (For testing of
	 * significantly bad results) Mutator
	 * 
	 * @param finals
	 * @return
	 */
	public static void theOposite(ArrayList<FinalEntry> finals) {
		for (FinalEntry i : finals) {
			i.prediction = i.prediction >= i.threshold ? 0f : 1f;
		}
	}

}
