package utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * PJDCC - Summary for class responsabilities.
 *
 * @author fourplus <fourplus1718@gmail.com>
 * @since 1.0
 * @version 11 Changes done
 */
public class Utils_2 {
	public static float countDraws(ArrayList<ExtendedFixture> all) {
		int count = 0;
		for (ExtendedFixture i : all) {
			if (i.result.goalsHomeTeam == i.result.goalsAwayTeam)
				count++;
		}
		return all.size() == 0 ? 0 : ((float) count / all.size());
	}

	public static void byWeekDay(ArrayList<ExtendedFixture> all) {
		int[] days = new int[8];
		int[] overs = new int[8];
		String[] literals = { "SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT" };

		for (ExtendedFixture i : all) {
			Calendar c = Calendar.getInstance();
			c.setTime(i.date);
			int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
			days[dayOfWeek]++;
			if (i.getTotalGoals() > 2.5f)
				overs[dayOfWeek]++;
		}

		float[] raitios = new float[8];
		for (int i = 1; i < 8; i++) {
			raitios[i] = ((float) overs[i]) / days[i];
			System.out.println(literals[i - 1] + " " + raitios[i] + " from " + days[i]);
		}

	}

	public static ArrayList<FinalEntry> intersect(ArrayList<FinalEntry> finalsBasic,
			ArrayList<FinalEntry> finalsPoisson) {
		ArrayList<FinalEntry> result = new ArrayList<>();
		for (int i = 0; i < finalsBasic.size(); i++) {
			if (samePrediction(finalsBasic.get(i), finalsPoisson.get(i)))
				result.add(finalsBasic.get(i));
		}
		return result;
	}

	@SafeVarargs
	public static ArrayList<FinalEntry> intersectMany(ArrayList<FinalEntry>... lists) {
		ArrayList<FinalEntry> result = new ArrayList<>();
		for (int i = 0; i < lists[0].size(); i++) {
			if (samePrediction(lists, i))
				result.add(lists[0].get(i));
		}
		return result;
	}

	@SafeVarargs
	public static ArrayList<FinalEntry> intersectVotes(ArrayList<FinalEntry>... lists) {
		ArrayList<FinalEntry> result = new ArrayList<>();
		for (int i = 0; i < lists[0].size(); i++) {
			int overs = 0;
			int unders = 0;
			for (ArrayList<FinalEntry> list : lists) {
				if (list.get(i).prediction >= list.get(i).upper)
					overs++;
				else
					unders++;
			}

			FinalEntry curr = lists[0].get(i);
			curr.prediction = overs > unders ? 1f : 0f;
			result.add(curr);
		}
		return result;
	}

	public static boolean samePrediction(FinalEntry f1, FinalEntry f2) {
		return (f1.prediction >= f1.upper && f2.prediction >= f2.upper)
				|| (f1.prediction <= f1.lower && f2.prediction <= f2.lower);
	}

	public static ArrayList<FinalEntry> intersectDiff(ArrayList<FinalEntry> finals1, ArrayList<FinalEntry> finals2) {
		ArrayList<FinalEntry> result = new ArrayList<>();
		for (FinalEntry fe : finals1) {
			FinalEntry other = Utils.getFE(finals2, fe);
			if (other != null) {
				if (samePrediction(fe, other))
					result.add(fe);
			}
		}
		return result;
	}

	/**
	 * Combines two list of finals whit ratio weighted predictions
	 * 
	 * @param finals1
	 * @param finals2
	 * @param ratio
	 * @return
	 */
	public static ArrayList<FinalEntry> combineDiff(ArrayList<FinalEntry> finals1, ArrayList<FinalEntry> finals2,
			float ratio) {
		ArrayList<FinalEntry> result = new ArrayList<>();
		for (FinalEntry fe : finals1) {
			FinalEntry other = Utils.getFE(finals2, fe);
			if (other != null) {
				FinalEntry combined = new FinalEntry(fe);
				fe.prediction = ratio * fe.prediction + (1f - ratio) * other.prediction;
				result.add(new FinalEntry(combined));
			}
		}
		return result;
	}

	public static float bestNperWeek(ArrayList<FinalEntry> all, int n) {
		String[] literals = { "SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT" };
		ArrayList<FinalEntry> filtered = new ArrayList<>();
		for (FinalEntry fe : all) {
			Calendar c = Calendar.getInstance();
			c.setTime(fe.fixture.date);
			int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
			if (literals[dayOfWeek - 1].equals("SAT") || literals[dayOfWeek - 1].equals("SUN")) {
				filtered.add(fe);
			}
		}
		filtered.sort(new Comparator<FinalEntry>() {

			@Override
			public int compare(FinalEntry o1, FinalEntry o2) {
				return o1.fixture.date.compareTo(o2.fixture.date);
			}

		});

		float profit = 0f;
		int winBets = 0;
		int loseBets = 0;
		ArrayList<FinalEntry> curr = new ArrayList<>();
		Date currDate = filtered.get(0).fixture.date;
		for (int i = 0; i < filtered.size(); i++) {
			profit = methodTwoBestNperWeek(curr,filtered,profit,winBets,loseBets,currDate);
		}
		System.out.println("Total from " + n + "s: " + profit + " " + winBets + "W " + loseBets + "L");

		return profit;

	}

	public static void fullAnalysys(ArrayList<FinalEntry> all, String description) {
		analysys(all, description, true);

	}

	public static void analysys(ArrayList<FinalEntry> all, String description, boolean verbose) {
		trueOddsProportional(all);
		ArrayList<Stats> stats = new ArrayList<>();

		ArrayList<FinalEntry> noEquilibriums = Utils.noequilibriums(all);
		ArrayList<FinalEntry> equilibriums = Utils.equilibriums(all);

		Stats equilibriumsAsUnders = new Stats(allUnders(onlyFixtures(equilibriums)), "Equilibriums as unders");
		Stats equilibriumsAsOvers = new Stats(allOvers(onlyFixtures(equilibriums)), "Equilibriums as overs");
		stats.add(equilibriumsAsOvers);
		stats.add(equilibriumsAsUnders);
		if (verbose) {
			System.out.println(equilibriumsAsUnders);
			System.out.println(equilibriumsAsOvers);
			System.out.println("Avg return: " + avgReturn(onlyFixtures(noEquilibriums)));
		}
			
		Stats allStats = new Stats(noEquilibriums, "all");
		stats.add(allStats);
		if (verbose){
			System.out.println(allStats);
			System.out.println(thresholdsByLeague(all));
			LineChart.draw(Utils.createProfitMovementData(Utils.noequilibriums(all)), description);
		}

		ArrayList<FinalEntry> overs = Utils.onlyOvers(noEquilibriums);
		ArrayList<FinalEntry> unders = Utils.onlyUnders(noEquilibriums);

		isVerboseSystemOutPrint(verbose,description);
		isVerboseSystemOutPrint(verbose,null);
		isVerboseSystemOutPrint(verbose,null);
			
		ArrayList<Stats> byCertaintyandCOT = byCertaintyandCOT(noEquilibriums, "", verbose);
		stats.addAll(byCertaintyandCOT);

		isVerboseSystemOutPrint(verbose,null);
		ArrayList<Stats> byOdds = byOdds(noEquilibriums, "", verbose);
		stats.addAll(byOdds);

		isVerboseSystemOutPrint(verbose,null);
		ArrayList<Stats> byValue = byValue(noEquilibriums, "", verbose);
		stats.addAll(byValue);

		Stats underStats = new Stats(unders, "unders");
		isVerboseSystemOutPrint(verbose,null);
		stats.add(underStats);

		isVerboseSystemOutPrint(verbose,null);
		ArrayList<Stats> byCertaintyandCOTUnders = byCertaintyandCOT(unders, "unders", verbose);
		stats.addAll(byCertaintyandCOTUnders);

		isVerboseSystemOutPrint(verbose,null);
		ArrayList<Stats> byOddsUnders = byOdds(unders, "unders", verbose);
		stats.addAll(byOddsUnders);

		Stats overStats = new Stats(overs, "overs");
		isVerboseSystemOutPrint(verbose,overStats);
		stats.add(overStats);

		isVerboseSystemOutPrint(verbose,null);
		ArrayList<Stats> byCertaintyandCOTover = byCertaintyandCOT(overs, "overs", verbose);
		stats.addAll(byCertaintyandCOTover);

		System.out.println();
		ArrayList<Stats> byOddsOvers = byOdds(overs, "overs", verbose);
		stats.addAll(byOddsOvers);

		System.out.println();
		Utils.byYear(onlyUnders(noEquilibriums), "all");

		System.out.println();
		Utils.byCompetition(onlyUnders(noEquilibriums), "all");

		Stats allOvers = new Stats(allOvers(Utils.onlyFixtures(noEquilibriums)), "all Overs");
		stats.add(allOvers);
		isVerboseSystemOutPrint(verbose,null);
		isVerboseSystemOutPrint(verbose,allOvers);

		Stats allUnders = new Stats(allUnders(Utils.onlyFixtures(noEquilibriums)), "all Unders");
		isVerboseSystemOutPrint(verbose,allUnders);
		stats.add(allUnders);

		Stats higherOdds = new Stats(higherOdds(Utils.onlyFixtures(noEquilibriums)), "higher Odds");
		Stats lowerOdds = new Stats(lowerOdds(Utils.onlyFixtures(noEquilibriums)), "lower Odds");
		stats.add(higherOdds);
		stats.add(lowerOdds);
		isVerboseSystemOutPrint(verbose,null);
		isVerboseSystemOutPrint(verbose,higherOdds);
		isVerboseSystemOutPrint(verbose,lowerOdds);
	
		System.out.println();
		
		if (verbose)
			System.out.println("Soft lines wins: " + format((float) wins / certs) + " draws: "
					+ format((float) draws / certs) + " not losses: " + format((float) (wins + draws) / certs));

		ArrayList<Stats> normalizedStats = new ArrayList<>();
		for (Stats st : stats){
			statsControl(st,normalizedStats);
		}
			
		stats.addAll(normalizedStats);

		System.out.println();
		stats.sort(Comparator.comparing(Stats::getPvalueOdds).reversed());
		stats.stream().filter(v -> verbose ? true : (v.getPvalueOdds() > 4 && !v.all.isEmpty()))
				.forEach(System.out::println);
	}

	

	public static void printStats(ArrayList<FinalEntry> all, String name) {
		float profit = Utils.getProfit(all);
		System.out.println(all.size() + " " + name + " with rate: " + format(100 * Utils.getSuccessRate(all))
				+ " profit: " + format(profit) + " yield: " + String.format("%.2f%%", 100 * profit / all.size())
				+ ((profit >= 0f && !all.isEmpty()) ? (" 1 in " + format(evaluateRecord(all))) : ""));
	}

	
	public static String format(float d) {
		return String.format("%.2f", d);
	}
	
	public static void triples(ArrayList<FinalEntry> all, int year) {
		int failtimes = 0;
		int losses = 0;
		int testCount = 1_000_000;
		double total = 0D;

		for (int trials = 0; trials < testCount; trials++) {
			Collections.shuffle(all);

			float bankroll = 1000f;
			float unit = 6f;
			int yes = 0;
			boolean flag = false;
			for (int i = 0; i < all.size() - all.size() % 3; i += 3) {
				flag = flagControls(bankroll,flag);
				if(flag)
					break;
				bankroll = bankrollControls(all,bankroll,flag,i);
				yes = yesControls(all,i,yes);
			}

			failtimes = failtimesControls(flag,failtimes);
			total = totalControls(flag,total,bankroll);
			losses = flagBankrollControls(flag,total,bankroll,losses);
			}

		System.out.println(year + " Out of " + testCount + " fails: " + failtimes + " losses " + losses + " successes: "
				+ (testCount - failtimes - losses) + " with AVG: " + total / (testCount - failtimes));
	}

	public static void hyperReal(ArrayList<FinalEntry> all, int year, float bankroll, float percent) 
	{
		System.err.println(year);
		float bank = bankroll;
		float previous = bank;
		int succ = 0;
		int alls = 0;
		all.sort(new Comparator<FinalEntry>() {
			@Override
			public int compare(FinalEntry o1, FinalEntry o2) {
				return o1.fixture.date.compareTo(o2.fixture.date);
			}
		});
		float betSize = percent * bankroll;
		Calendar cal = Calendar.getInstance();
		cal.setTime(all.get(0).fixture.date);
		int month = cal.get(Calendar.MONTH);
		for (FinalEntry i : all) {
			cal.setTime(i.fixture.date);
			bank=UtilsControls.controlMonth(cal,month,bank,betSize,i,succ,alls);
			bank=UtilsControls.controlNotMonth(cal,month,bank,betSize,i,succ,alls,previous,percent);
		}
		System.out.println("Bank after month: " + (month + 1) + " is: " + bank + " unit: " + betSize + " profit: "
				+ (bank - previous) + " in units: " + (bank - previous) / betSize + " rate: " + (float) succ / alls
				+ "%");
	}

	public static float correlation(Integer[] arr1, Integer[] arr2) {
		if (arr1.length != arr2.length)
			return -1;
		float avg1 = 0f;
		float avg2 = 0f;
		for (int i = 0; i < arr1.length; i++) {
			avg1 += arr1[i];
			avg2 += arr2[i];
		}

		avg1 /= arr1.length;
		avg2 /= arr2.length;

		float sumXY = 0f;
		float sumX2 = 0f;
		float sumY2 = 0f;

		for (int i = 0; i < arr1.length; i++) {
			sumXY += (arr1[i] - avg1) * (arr2[i] - avg2);
			sumX2 += Math.pow(arr1[i] - avg1, 2.0f);
			sumY2 += Math.pow(arr2[i] - avg2, 2.0f);
		}
		return (float) (sumXY / (Math.sqrt(sumX2) * Math.sqrt(sumY2)));
	}

	public static boolean oddsInRange(float gain, float finalScore, Settings settings) {
		boolean over = finalScore > settings.threshold;

		if (over)
			return (gain >= settings.minOver && gain <= settings.maxOver);
		else
			return (gain >= settings.minUnder && gain <= settings.maxUnder);
	}

	public static ArrayList<FinalEntry> onlyUnders(ArrayList<FinalEntry> finals) {
		ArrayList<FinalEntry> result = new ArrayList<>();
		for (FinalEntry i : finals) {
			if (i.prediction < i.threshold)
				result.add(i);
		}
		return result;
	}

	public static ArrayList<FinalEntry> onlyOvers(ArrayList<FinalEntry> finals) {
		ArrayList<FinalEntry> result = new ArrayList<>();
		for (FinalEntry i : finals) {
			if (i.prediction >= i.threshold)
				result.add(i);
		}
		return result;
	}

	public static ArrayList<FinalEntry> ratioRestrict(ArrayList<FinalEntry> finals, Map<String, Integer> played,
			Map<String, Integer> success) {
		ArrayList<FinalEntry> result = new ArrayList<>();
		for (FinalEntry i : finals) {
			if (!played.containsKey(i.fixture.homeTeam) || !played.containsKey(i.fixture.awayTeam)
					|| played.get(i.fixture.homeTeam) + played.get(i.fixture.awayTeam) < 8)
				result.add(i);
			else {
				float homeRate = success.get(i.fixture.homeTeam) == 0 ? 0f
						: ((float) success.get(i.fixture.homeTeam) / played.get(i.fixture.homeTeam));
				float awayRate = success.get(i.fixture.awayTeam) == 0 ? 0f
						: ((float) success.get(i.fixture.awayTeam) / played.get(i.fixture.awayTeam));
				float avgRate = (homeRate + awayRate) / 2;
				if (avgRate >= 0.4)
					result.add(i);
			}
		}
		return result;
	}

	public static ArrayList<FinalEntry> certaintyRestrict(ArrayList<FinalEntry> finals, float cert) {
		ArrayList<FinalEntry> result = new ArrayList<>();
		for (FinalEntry i : finals) {
			float certainty = i.prediction > i.threshold ? i.prediction : (1f - i.prediction);
			if (certainty >= cert)
				result.add(i);
		}
		return result;
	}

	public static ArrayList<FinalEntry> cotRestrict(ArrayList<FinalEntry> finals, float f) {
		ArrayList<FinalEntry> result = new ArrayList<>();
		for (FinalEntry fe : finals) {
			float cot = fe.prediction > fe.threshold ? (fe.prediction - fe.threshold) : (fe.threshold - fe.prediction);
			if (cot >= f)
				result.add(fe);
		}

		return result;
	}

	public static ArrayList<FinalEntry> cotRestrictOU(ArrayList<FinalEntry> finals, Pair pair) {
		ArrayList<FinalEntry> result = new ArrayList<>();

		for (FinalEntry fe : Utils.onlyOvers(finals)) {
			float cot = fe.prediction > fe.threshold ? (fe.prediction - fe.threshold) : (fe.threshold - fe.prediction);
			if (cot >= pair.home)
				result.add(fe);
		}

		for (FinalEntry fe : Utils.onlyUnders(finals)) {
			float cot = fe.prediction > fe.threshold ? (fe.prediction - fe.threshold) : (fe.threshold - fe.prediction);
			if (cot >= pair.away)
				result.add(fe);
		}
		return result;
	}

	public static void drawAnalysis(ArrayList<FinalEntry> all) {
		int drawUnder = 0;
		int drawOver = 0;
		int under = 0;
		int over = 0;

		float profitOver = 0f;
		float profitUnder = 0f;

		for (FinalEntry i : all) {
			if (i.prediction <= i.lower) {
				under++;
				if (i.fixture.result.goalsHomeTeam == i.fixture.result.goalsAwayTeam) {
					drawUnder++;
					profitUnder += i.fixture.drawOdds;
				}

			} else if (i.prediction >= i.upper) {
				over++;
				if (i.fixture.result.goalsHomeTeam == i.fixture.result.goalsAwayTeam) {
					drawOver++;
					profitOver += i.fixture.drawOdds;
				}
			}
		}

		System.out.println("Draws when under pr: " + (profitUnder - under) + " from " + under + " "
				+ Results.format((float) (profitUnder - under) * 100 / under) + "%");
		System.out.println("Draws when over pr: " + (profitOver - over) + " from " + over + " "
				+ Results.format((float) (profitOver - over) * 100 / over) + "%");
	}

	public static Table createTable(ArrayList<ExtendedFixture> data, String sheetName, int year, int i) {
		HashMap<String, Position> teams = getTeams(data);

		Table table = new Table(sheetName, year, i);

		for (String team : teams.keySet()) {
			ExtendedFixture f = createExtendedFixture(team);
			ArrayList<ExtendedFixture> all = Utils.getHomeFixtures(f, data);
			all.addAll(Utils.getAwayFixtures(f, data));
			Position pos = createPosition(team, all);
			table.positions.add(pos);
		}
		table.sort();
		return table;
	}

	

	public static ArrayList<FinalEntry> shotsRestrict(ArrayList<FinalEntry> finals, HSSFSheet sheet)
			throws ParseException {
		ArrayList<FinalEntry> shotBased = new ArrayList<>();
		for (FinalEntry fe : finals) {
			float shotsScore = XlSUtils.shots(fe.fixture, sheet);
			if (fe.prediction >= fe.upper && Float.compare(shotsScore, 1f)==0) {
				shotBased.add(fe);
			} else if (fe.prediction <= fe.lower && Float.compare(shotsScore, 0f)==0) {
				shotBased.add(fe);
			}
		}
		return shotBased;
	}

	public static Pair positionLimits(ArrayList<FinalEntry> finals, Table table, String type) {

		float bestProfit = getProfit(finals);
		int bestLow = 0;
		int bestHigh = 23;

		for (int i = 1; i < 11; i++) {
			ArrayList<FinalEntry> diffPos = positionRestrict(finals, table, i, 23, type);

			float curr = Utils.getProfit(diffPos);
			if (curr > bestProfit) {
				bestProfit = curr;
				bestLow = i;
			}
		}

		for (int i = bestLow; i < 23; i++) {
			ArrayList<FinalEntry> diffPos = positionRestrict(finals, table, bestLow, i, type);

			float curr = Utils.getProfit(diffPos);
			if (curr > bestProfit) {
				bestProfit = curr;
				bestHigh = i;
			}
		}
		return Pair.of(bestLow, bestHigh);
	}

	public static ArrayList<FinalEntry> positionRestrict(ArrayList<FinalEntry> finals, Table table, int i, int j,
			String type) {
		ArrayList<FinalEntry> diffPos = new ArrayList<>();
		for (FinalEntry fe : finals) {
			int diff = Math.abs(table.getPositionDiff(fe.fixture));

			if (diff <= i && fe.prediction <= fe.lower)
				diffPos.add(fe);
			else if (diff >= j && fe.prediction >= fe.upper)
				diffPos.add(fe);
			else if (diff > i && diff < j)
				diffPos.add(fe);
		}
		return diffPos;

	}

	public static ArrayList<FinalEntry> similarityRestrict(HSSFSheet sheet, ArrayList<FinalEntry> finals, Table table)
			throws ParseException {
		ArrayList<FinalEntry> result = new ArrayList<>();
		for (FinalEntry i : finals) {
			float basicSimilar = Utils.basicSimilar(i.fixture, sheet, table);
			if (i.prediction >= i.upper && basicSimilar >= i.threshold)
				result.add(i);
			else if (i.prediction <= i.lower && basicSimilar <= i.threshold)
				result.add(i);
		}

		return result;
	}

	public static float basicSimilar(ExtendedFixture f, HSSFSheet sheet, Table table) throws ParseException {
		ArrayList<String> filterHome = table.getSimilarTeams(f.awayTeam);
		ArrayList<String> filterAway = table.getSimilarTeams(f.homeTeam);

		ArrayList<ExtendedFixture> lastHomeTeam = filter(f.homeTeam,
				XlSUtils.selectLastAll(sheet, f.homeTeam, 50, f.date), filterHome);
		ArrayList<ExtendedFixture> lastAwayTeam = filter(f.awayTeam,
				XlSUtils.selectLastAll(sheet, f.awayTeam, 50, f.date), filterAway);

		ArrayList<ExtendedFixture> lastHomeHomeTeam = filter(f.homeTeam,
				XlSUtils.selectLastHome(sheet, f.homeTeam, 25, f.date), filterHome);
		ArrayList<ExtendedFixture> lastAwayAwayTeam = filter(f.awayTeam,
				XlSUtils.selectLastAway(sheet, f.awayTeam, 25, f.date), filterAway);

		float allGamesAVG = (Utils.countOverGamesPercent(lastHomeTeam) + Utils.countOverGamesPercent(lastAwayTeam)) / 2;
		float homeAwayAVG = (Utils.countOverGamesPercent(lastHomeHomeTeam)
				+ Utils.countOverGamesPercent(lastAwayAwayTeam)) / 2;
		float BTSAVG = (Utils.countBTSPercent(lastHomeTeam) + Utils.countBTSPercent(lastAwayTeam)) / 2;

		return 0.6f * allGamesAVG + 0.3f * homeAwayAVG + 0.1f * BTSAVG;
	}
	
	
}
