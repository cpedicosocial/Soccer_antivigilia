package utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
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
public class UtilsControls_2 {
	static ArrayList<Stats> byOdds(ArrayList<FinalEntry> all, String prefix, boolean verbose) {
		ArrayList<Stats> result = new ArrayList<>();
		ArrayList<FinalEntry> under14 = new ArrayList<>();
		ArrayList<FinalEntry> under18 = new ArrayList<>();
		ArrayList<FinalEntry> under22 = new ArrayList<>();
		ArrayList<FinalEntry> over22 = new ArrayList<>();

		for (FinalEntry i : all) {
			float odds = i.isOver() ? i.fixture.maxOver : i.fixture.maxUnder;
			if (odds <= 1.4f) {
				under14.add(i);
			} else if (odds <= 1.8f) {
				under18.add(i);
			} else if (odds <= 2.2f) {
				under22.add(i);
			} else {
				over22.add(i);
			}
		}

		if (verbose) {
			System.out.println(new Stats(under14, prefix + " " + "1.00 - 1.40"));
			System.out.println(new Stats(under18, prefix + " " + "1.41 - 1.80"));
			System.out.println(new Stats(under22, prefix + " " + "1.81 - 2.20"));
			System.out.println(new Stats(over22, prefix + " " + " > 2.21"));
		}
		result.add(new Stats(under14, prefix + " " + "1.00 - 1.40"));
		result.add(new Stats(under18, prefix + " " + "1.41 - 1.80"));
		result.add(new Stats(under22, prefix + " " + "1.81 - 2.20"));
		result.add(new Stats(over22, prefix + " " + " > 2.21"));

		return result;

	}
	
	private static void underControlsOne(ArrayList<FinalEntry> under09,ArrayList<FinalEntry> under1,ArrayList<FinalEntry> under110,
						              ArrayList<FinalEntry> under120,ArrayList<FinalEntry> under130,ArrayList<FinalEntry> under140,
						              ArrayList<FinalEntry> under150,ArrayList<FinalEntry> under160,ArrayList<FinalEntry> over160,FinalEntry i) {
		float value = i.getValue();
		
		if (value <= 0.9f)
			under09.add(i);
		else if (value <= 1f)
			under1.add(i);
		else if (value <= 1.10f) 
			under110.add(i);
		else if (value <= 1.20f) 
			under120.add(i);
		else if (value <= 1.30f && value > 1.40f) 
			under130.add(i);		
	}
	
	private static void underControlsTwo(ArrayList<FinalEntry> under09,ArrayList<FinalEntry> under1,ArrayList<FinalEntry> under110,
            							ArrayList<FinalEntry> under120,ArrayList<FinalEntry> under130,ArrayList<FinalEntry> under140,
            							ArrayList<FinalEntry> under150,ArrayList<FinalEntry> under160,ArrayList<FinalEntry> over160,FinalEntry i) {
		float value = i.getValue();

		if (value <= 1.40f) 
			under140.add(i);
		else if (value <= 1.50f)
			under150.add(i);
		else if (value <= 1.60f){
			under160.add(i);
		} else {
			over160.add(i);
		}

}
	
	static ArrayList<Stats> byValue(ArrayList<FinalEntry> all, String prefix, boolean verbose) {
		ArrayList<Stats> result = new ArrayList<>();
		ArrayList<FinalEntry> under09 = new ArrayList<>();
		ArrayList<FinalEntry> under1 = new ArrayList<>();
		ArrayList<FinalEntry> under110 = new ArrayList<>();
		ArrayList<FinalEntry> under120 = new ArrayList<>();
		ArrayList<FinalEntry> under130 = new ArrayList<>();
		ArrayList<FinalEntry> under140 = new ArrayList<>();
		ArrayList<FinalEntry> under150 = new ArrayList<>();
		ArrayList<FinalEntry> under160 = new ArrayList<>();
		ArrayList<FinalEntry> over160 = new ArrayList<>();

		for (FinalEntry i : all) {
			underControlsOne(under09,under1,under110,under120,under130,under140,under150,under160,over160,i);
			underControlsTwo(under09,under1,under110,under120,under130,under140,under150,under160,over160,i);
		}
		
		isVerboseSystemOutPrint(verbose,new Stats(under09, prefix + " " + "< 0.9"));
		isVerboseSystemOutPrint(verbose,new Stats(under1, prefix + " " + "0.9 - 1.0"));
		isVerboseSystemOutPrint(verbose,new Stats(under110, prefix + " " + "1.00 - 1.10"));
		isVerboseSystemOutPrint(verbose,new Stats(under120, prefix + " " + "1.10 - 1.2"));
		isVerboseSystemOutPrint(verbose,new Stats(under130, prefix + " " + "1.2 - 1.3"));
		isVerboseSystemOutPrint(verbose,new Stats(under140, prefix + " " + "1.3 - 1.4"));
		isVerboseSystemOutPrint(verbose,new Stats(under150, prefix + " " + "1.4 - 1.5"));
		isVerboseSystemOutPrint(verbose,new Stats(under160, prefix + " " + "1.5 - 1.6"));
		isVerboseSystemOutPrint(verbose,new Stats(over160, prefix + " " + " > 1.6"));

		result.add(new Stats(under09, prefix + " " + "< 0.9"));
		result.add(new Stats(under1, prefix + " " + "0.9 - 1.0"));
		result.add(new Stats(under110, prefix + " " + "1.0 - 1.10"));
		result.add(new Stats(under120, prefix + " " + "1.1 - 1.2"));
		result.add(new Stats(under130, prefix + " " + "1.2 - 1.3"));
		result.add(new Stats(under140, prefix + " " + "1.3 - 1.4"));
		result.add(new Stats(under150, prefix + " " + "1.4 - 1.5"));
		result.add(new Stats(under160, prefix + " " + "1.5 - 1.6"));
		result.add(new Stats(over160, prefix + " " + "> 1.6"));

		return result;

	}
	
	static ExtendedFixture createExtendedFixture(String team){
		return new ExtendedFixture(null, team, team, null, null);
	}
	static Position createPosition(String team, ArrayList<ExtendedFixture> all) {
		Position pos = new Position();
		pos.team = team;

		for (ExtendedFixture i : all) {
			if (i.homeTeam.equals(team)) {
				pos.played++;
				pos.homeplayed++;

				if (i.isHomeWin()) {
					pos.wins++;
					pos.homewins++;
					pos.points += 3;
					pos.homepoints += 3;
				} else if (i.isAwayWin()) {
					pos.losses++;
					pos.homelosses++;
				} else {
					pos.draws++;
					pos.homedraws++;
					pos.points++;
					pos.homepoints++;
				}

				pos.scored += i.result.goalsHomeTeam;
				pos.conceded += i.result.goalsAwayTeam;

				pos.homescored += i.result.goalsHomeTeam;
				pos.homeconceded += i.result.goalsAwayTeam;
			} else {
				pos.played++;
				pos.awayplayed++;

				if (i.isHomeWin()) {
					pos.losses++;
					pos.awaylosses++;
				} else if (i.isAwayWin()) {
					pos.wins++;
					pos.awaywins++;
					pos.points += 3;
					pos.awaypoints += 3;
				} else {
					pos.draws++;
					pos.awaydraws++;
					pos.points++;
					pos.awaypoints++;
				}

				pos.scored += i.result.goalsAwayTeam;
				pos.conceded += i.result.goalsHomeTeam;

				pos.awayscored += i.result.goalsAwayTeam;
				pos.awayconceded += i.result.goalsHomeTeam;
			}
		}

		pos.diff = pos.scored - pos.conceded;
		pos.homediff = pos.homescored - pos.homeconceded;
		pos.awaydiff = pos.awayscored - pos.awayconceded;
		pos.team = team;
		return pos;
	}

	static HashMap<String, Position> getTeams(ArrayList<ExtendedFixture> data) {
		HashMap<String, Position> teams = new HashMap<>();

		for (ExtendedFixture i : data) {
			if (!teams.containsKey(i.homeTeam)) {
				Position pos = new Position();
				pos.team = i.homeTeam;
				teams.put(i.homeTeam, pos);
			}
			if (!teams.containsKey(i.awayTeam)) {
				Position pos = new Position();
				pos.team = i.awayTeam;
				teams.put(i.awayTeam, pos);
			}
		}

		return teams;
	}
	
	static ArrayList<ExtendedFixture> filter(String team, ArrayList<ExtendedFixture> selectLastAll,
			ArrayList<String> filterHome) {
		ArrayList<ExtendedFixture> result = new ArrayList<>();
		for (ExtendedFixture i : selectLastAll) {
			if (i.homeTeam.equals(team) && filterHome.contains(i.awayTeam))
				result.add(i);
			if (i.awayTeam.equals(team) && filterHome.contains(i.homeTeam))
				result.add(i);

		}
		return result;
	}
	
	static ArrayList<FinalEntry> createFinalEntry(){
		return new ArrayList<FinalEntry>();
	}
	
	static float getNormalizedStakeSum(ArrayList<FinalEntry> all) {
		float stakeSum = 0f;
		for (FinalEntry i : all) {
			float coeff = i.prediction >= i.upper ? i.fixture.maxOver : i.fixture.maxUnder;
			float betUnit = 1f / (coeff - 1);
			stakeSum += betUnit;
		}
		return stakeSum;
	}
	
	static FullEntry findBestLineFullEntry(FinalEntry f, int[] distributionHome, int[] distributionAway,
			HashMap<ExtendedFixture, FullFixture> map) {
		FullEntry best = new FullEntry(f.fixture, f.prediction, f.result, f.threshold, f.lower, f.upper,
				map.get(f.fixture).goalLines.main);
		FullFixture full = map.get(f.fixture);
		Result originalResult = new Result(full.result.goalsHomeTeam, full.result.goalsAwayTeam);
		float bestValue = Float.NEGATIVE_INFINITY;

		for (Line i : map.get(f.fixture).goalLines.getArrayLines()) {
			float valueHome = 0;
			int homeSize = 0;
			for (int s : distributionHome)
				homeSize += s;

			for (int goals = 0; goals < distributionHome.length; goals++) {
				full.result = new Result(goals, 0);
				valueHome += distributionHome[goals]
						* new FullEntry(full, f.prediction, new Result(goals, 0), f.threshold, f.lower, f.upper, i)
								.getProfit();
			}

			valueHome /= homeSize;

			float valueAway = 0;
			int awaySize = 0;
			for (int s : distributionAway)
				awaySize += s;

			for (int goals = 0; goals < distributionAway.length; goals++) {
				full.result = new Result(goals, 0);
				valueAway += distributionAway[goals]
						* new FullEntry(full, f.prediction, new Result(goals, 0), f.threshold, f.lower, f.upper, i)
								.getProfit();
			}

			valueAway /= awaySize;

			float finalValue = (valueAway + valueHome) / 2;

			if (finalValue > bestValue) {
				bestValue = finalValue;
				best.line = i;
			}
		}

		best.result = originalResult;
		best.fixture.result = originalResult;
		return best;

	}

	static int[] getGoalDistribution(ExtendedFixture fixture, ArrayList<ExtendedFixture> all, String team) {
		int[] distribution = new int[20];
		for (ExtendedFixture i : all) {
			if ((i.homeTeam.equals(team) || i.awayTeam.equals(team)) && i.date.before(fixture.date)) {
				int totalGoals = i.getTotalGoals();
				distribution[totalGoals]++;
			}
		}
		return distribution;

	}
	
	static Pair estimateBoth(ExtendedFixture f, HashMap<ExtendedFixture, FullFixture> map, HSSFSheet sheet)
			throws ParseException {
		ArrayList<ExtendedFixture> lastHomeHomeTeam = XlSUtils.selectLastAll(sheet, f.homeTeam, 50, f.date);
		ArrayList<ExtendedFixture> lastAwayAwayTeam = XlSUtils.selectLastAll(sheet, f.awayTeam, 50, f.date);

		float over = Utils.estimateTheLineFull(f, f.homeTeam, lastHomeHomeTeam, f.line, true, map);
		float under = Utils.estimateTheLineFull(f, f.awayTeam, lastAwayAwayTeam, f.line, false, map);

		return Pair.of(over, under);
	}

	static FullEntry createFullEntry(ExtendedFixture i, float prediction, HashMap<ExtendedFixture, FullFixture> map){
		return new FullEntry(i, prediction, i.result, 0.55f, 0.55f, 0.55f, mapget(map, i).goalLines.main);
	}
	
	static float estimateTheLineFull(ExtendedFixture f, String homeTeam,
			ArrayList<ExtendedFixture> lastHomeTeam, float line, boolean b, HashMap<ExtendedFixture, FullFixture> map) {
		if (lastHomeTeam.size() == 0)
			return 0;
		ArrayList<String> results = new ArrayList<>();
		for (ExtendedFixture i : lastHomeTeam) {
			float prediction = b ? 1f : 0f;
			FullEntry ae = createFullEntry(i,prediction,map);
			results.add(ae.successFull());
		}

		float coeff = b ? map.get(f).goalLines.main.home : map.get(f).goalLines.main.away;
		return outcomes(results, coeff);
	}

	static FullFixture mapget(HashMap<ExtendedFixture, FullFixture> map, ExtendedFixture i) {
		for (Entry<ExtendedFixture, FullFixture> entry : map.entrySet())
			if (entry.getKey().homeTeam.equals(i.homeTeam) && entry.getKey().awayTeam.equals(i.awayTeam)
					&& entry.getKey().date.equals(i.date))
				return entry.getValue();

		return null;
	}

	static FullEntry worse(ExtendedFixture f, Pair pair, float line, float home2, float away2,
			HashMap<ExtendedFixture, FullFixture> map) {
		FullEntry home = new FullEntry(f, pair.home, f.result, f.matchday, 0.55f, 0.55f, map.get(f).goalLines.main);
		FullEntry away = new FullEntry(f, pair.away, f.result, f.matchday, 0.55f, 0.55f, map.get(f).goalLines.main);
		if (home.prediction < away.prediction) {
			home.prediction = 1f;
			return home;
		} else {
			away.prediction = 0f;
			return away;
		}
	}
	
	static float estimateGoalFromPlayerStats(ExtendedFixture ef, ArrayList<PlayerFixture> pfs,
			HashMap<String, String> dictionary, boolean home, ArrayList<ExtendedFixture> all) throws ParseException {
		ArrayList<PlayerFixture> homePlayers = getPlayers(ef, home, pfs, dictionary);
		if (!Utils.validatePlayers(homePlayers))
			System.out.println("Not a valid squad for " + ef);
		// printPlayers(homePlayers);
		ArrayList<Player> playerStatsHome = createStatistics(ef, home, pfs, dictionary);
		HashMap<String, Player> homeHash = (HashMap<String, Player>) playerStatsHome.stream()
				.collect(Collectors.toMap(Player::getName, Function.identity()));

		float avgGoalsHome = FixtureUtils.selectAvgHomeTeam(all, ef.homeTeam, ef.date).home;
		float avgGoalsAway = FixtureUtils.selectAvgAwayTeam(all, ef.awayTeam, ef.date).home;

		float homeAvgFor = home ? avgGoalsHome : avgGoalsAway;
		// float homeAvgFor = XlSUtils.selectAvgFor(sheet, home ? ef.homeTeam :
		// ef.awayTeam, ef.date);
		ArrayList<Player> keyAttackingPlayers = new ArrayList<>();
		// int totalGoals = 0, totalAssists = 0;
		// for (Player i : playerStatsHome) {
		// totalGoals += i.goals;
		// totalAssists += i.assists;
		// }

		// for (Player i : playerStatsHome) {
		// if (((float) i.goals / totalGoals) > 0.4f)
		// keyAttackingPlayers.add(i);
		// }

		// System.out.println(playerStatsHome);

		float goalRatio = 1f;
		float assistRatio = 1f - goalRatio;
		float homeEstimate = 0f;
		for (PlayerFixture i : homePlayers) {
			if (i.lineup) {
				if (homeHash.containsKey(i.name)) {
					homeEstimate += goalRatio * homeHash.get(i.name).getGoalAvg() * 90
							+ assistRatio * homeHash.get(i.name).getAssistAvg() * 90;
				}
			}
		}

		// for (Player i : keyAttackingPlayers) {
		// if (!homePlayers.contains(i))
		// return 0f;
		// }

		return homeEstimate / homeAvgFor;
	}

	static boolean validatePlayers(ArrayList<PlayerFixture> players) {
		int lineups = 0;
		int subs = 0;

		for (PlayerFixture i : players) {
			if (i.lineup)
				lineups++;
			if (i.substitute)
				subs++;
		}

		boolean result = lineups == 11 && subs <= 3;
		if (!result)
			System.out.println("Not a valid squad for " + (players.isEmpty() ? " " : players.get(0).team));

		return result;
	}
	
	private static void fullControlsOne(FullEntry full, HashMap<ExtendedFixture, FullFixture> map){
		if (Float.compare(offset, 0.25f)==0 && isOver)
			full.line = map.get(f.fixture).goalLines.line2;
		else
			full.line = map.get(f.fixture).goalLines.line3;
		
		if (Float.compare(offset, 0.5f)==0 && isOver) 
			full.line = map.get(f.fixture).goalLines.line1;
		else
			full.line = map.get(f.fixture).goalLines.line4;
	}
	
	private static void fullControlsTwo(FullEntry full, HashMap<ExtendedFixture, FullFixture> map){
		if (Float.compare(offset, -0.25f)==0 && isOver) 
			full.line = map.get(f.fixture).goalLines.line3;
		else
			full.line = map.get(f.fixture).goalLines.line2;
		if (Float.compare(offset, -0.5f)==0 && isOver)
			full.line = map.get(f.fixture).goalLines.line4;
		else{
			full.line = map.get(f.fixture).goalLines.line1;
		}
	}
	
	static FullEntry fullControls(FullEntry full, HashMap<ExtendedFixture, FullFixture> map) {
		fullControlsOne(full,map);		
		fullControlsTwo(full,map);
		return full;
	}

	private static void playersControl(HashMap<String, String> reverseDictionary,PlayerFixture pf,String team,ExtendedFixture i,HashMap<String, ArrayList<PlayerFixture>> players)
	{
		if ((reverseDictionary.get(pf.team).equals(team) || reverseDictionary.get(pf.team).equals(team)) && pf.fixture.date.before(i.date))
			players.get(pf.name).add(pf);
	}
	
	private static void playersControlKey(HashMap<String, String> reverseDictionary,PlayerFixture pf,String team,ExtendedFixture i,HashMap<String, ArrayList<PlayerFixture>> players)
	{
		if ( ((reverseDictionary.get(pf.team).equals(team) || reverseDictionary.get(pf.team).equals(team)) && pf.fixture.date.before(i.date)) && !players.containsKey(pf.name))
			players.put(pf.name, new ArrayList<>());
	}
	
	private static void playerControl(PlayerFixture pf, Player player)
	{
		if (pf.lineup)
			player.lineups++;
		else if (pf.substitute)
			player.substitutes++;
		else
			player.subsWOP++;
	}
	
	private static void teamControl(String team,PlayerFixture pf,Player player)
	{
		if (team.equals(pf.fixture.homeTeam)) {
			player.homeMinutesPlayed += pf.minutesPlayed;
			player.homeGoals += pf.goals;
			player.homeAssists += pf.assists;
			if (pf.lineup)
				player.homeLineups++;
			else if (pf.substitute)
				player.homeSubstitutes++;
			else
				player.homeSubsWOP++;
		}
	}
	
	private static void teamControlNot(String team,PlayerFixture pf,Player player)
	{
		if (team.equals(pf.fixture.homeTeam)==false) {
			player.awayMinutesPlayed += pf.minutesPlayed;
			player.awayGoals += pf.goals;
			player.awayAssists += pf.assists;
			if (pf.lineup)
				player.awayLineups++;
			else if (pf.substitute)
				player.awaySubstitutes++;
			else
				player.awaySubsWOP++;
		}
	}
	
	static ArrayList<Player> createStatistics(ExtendedFixture i, boolean home, ArrayList<PlayerFixture> pfs,
			HashMap<String, String> dictionary) {
		
		HashMap<String, String> reverseDictionary = (HashMap<String, String>) dictionary.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
		String team = home ? i.homeTeam : i.awayTeam;
		HashMap<String, ArrayList<PlayerFixture>> players = new HashMap<>();
		
		for (PlayerFixture pf : pfs) {
			playersControl(reverseDictionary,pf,team,i,players);
			playersControlKey(reverseDictionary,pf,team,i,players);
		}

		ArrayList<Player> result = new ArrayList<>();
		for (Entry<String, ArrayList<PlayerFixture>> entry : players.entrySet()) {
			Player player = new Player(team, entry.getKey(), 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
			for (PlayerFixture pf : entry.getValue()) {
				player.minutesPlayed += pf.minutesPlayed;
				player.goals += pf.goals;
				player.assists += pf.assists;
				playerControl(pf,player);
				teamControl(team,pf,player);
				teamControlNot(team,pf,player);
			}
			result.add(player);
		}

		// Sort - goalscorers first
		result.sort(new Comparator<Player>() {

			@Override
			public int compare(Player o1, Player o2) {
				return ((Integer) o2.goals).compareTo((Integer) o1.goals);
			}
		});

		return result;
	}
	
	static float bankrollControls(ArrayList<FinalEntry> all, float bankroll, boolean flag, int i){
		if (all.get(i).success() && all.get(i + 1).success() && all.get(i + 2).success()) {
			float c1 = all.get(i).prediction > all.get(i).upper ? all.get(i).fixture.maxOver
					: all.get(i).fixture.maxUnder;
			float c2 = all.get(i + 1).prediction > all.get(i + 1).upper ? all.get(i + 1).fixture.maxOver
					: all.get(i + 1).fixture.maxUnder;
			float c3 = all.get(i + 2).prediction > all.get(i + 2).upper ? all.get(i + 2).fixture.maxOver
					: all.get(i + 2).fixture.maxUnder;
			bankroll += unit * (c1 * c2 * c3 - 1f);
		} else {
			bankroll -= unit;
		}
		return bankroll;
	}
	
	static int yesControls(ArrayList<FinalEntry> all, int i, int yes){
		if (all.get(i).success() && all.get(i + 1).success() && all.get(i + 2).success()) {
			yes++;
		}
		return yes;
	}
	

}
