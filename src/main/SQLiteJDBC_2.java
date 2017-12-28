package main;

import java.io.IOException;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entries.AllEntry;
import entries.FinalEntry;
import entries.HTEntry;
import settings.Settings;
import utils.Lines;
import utils.Utils;
import xls.XlSUtils;

/**
 * PJDCC - Summary for class responsabilities.
 *
 * @author fourplus <fourplus1718@gmail.com>
 * @since 1.0
 * @version 11 Changes done
 */
public class SQLiteJDBC_2 {
	
	private final String NAME_CLASS = NAME_CLASS;
	private final String DRIVER = DRIVER;
	private final String SELECT_RESULTS = "select * from results";
	private final String DATE = "date";
	private final String HOME_TEAM_NAME = "hometeamname";
	private final String AWAY_TEAM_NAME = "awayteamname";
	private final String HOME_GOALS = "homegoals";
	private final String AWAY_GOALS = "awaygoals";
	private final String COMPETITION = "competition";
	private final String MATCHDAY = "matchday";
	private final String WRONG = "Something was wrong";
	private final String WHERE_COMPETITION = " where competition=";
	private final String AND_MATCHDAY = " and matchday<";
	private final String SEMICOLON = ";";
	
	// selects all fixtures for a given season from the database
	// without cl and wc and from 11 matchday up
	public static ArrayList<ExtendedFixture> select(int season) {
		ArrayList<ExtendedFixture> results = new ArrayList<>();

		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName(NAME_CLASS);
			c = DriverManager.getConnection(DRIVER);
			c.setAutoCommit(false);

			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(
					SELECT_RESULTS + season + " where matchday > 10 and competition not in ('CL' ,'WC');");
			while (rs.next()) {
				String date = rs.getString(DATE);
				String homeTeamName = rs.getString(HOME_TEAM_NAME);
				String awayTeamName = rs.getString(AWAY_TEAM_NAME);
				int homeGoals = rs.getInt(HOME_GOALS);
				int awayGoals = rs.getInt(AWAY_GOALS);
				String competition = rs.getString(COMPETITION);
				int matchday = rs.getInt(MATCHDAY);
				synchronized(format){
					ExtendedFixture ef = new ExtendedFixture(format.parse(date), homeTeamName, awayTeamName,
							new Result(homeGoals, awayGoals), competition).withMatchday(matchday);
				}
				results.add(ef);
			}
			rs.close();
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(WRONG);
			System.exit(0);
		}
		return results;
	}

	public static ArrayList<ExtendedFixture> selectLastAll(String team, int count, int season, int matchday,
			String competition) {
		ArrayList<ExtendedFixture> results = new ArrayList<>();

		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName(NAME_CLASS);
			c = DriverManager.getConnection(DRIVER);
			c.setAutoCommit(false);

			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(SELECT_RESULTS+ season + " where matchday < " + matchday
					+ " and competition='" + competition + "' and ((hometeamname = '" + team + "') or (awayteamname = '"
					+ team + "')) order by matchday" + " desc limit " + count + SEMICOLON);
			while (rs.next()) {
				String date = rs.getString(DATE);
				String homeTeamName = rs.getString(HOME_TEAM_NAME);
				String awayTeamName = rs.getString(AWAY_TEAM_NAME);
				int homeGoals = rs.getInt(HOME_GOALS);
				int awayGoals = rs.getInt(AWAY_GOALS);
				String competit = rs.getString(COMPETITION);
				int matchd = rs.getInt(MATCHDAY);
				synchronized(format){
					ExtendedFixture ef = new ExtendedFixture(format.parse(date), homeTeamName, awayTeamName,
							new Result(homeGoals, awayGoals), competit).withMatchday(matchd);
				}
				results.add(ef);
			}
			rs.close();
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(WRONG);
			System.exit(0);
		}
		return results;
	}

	public static ArrayList<ExtendedFixture> selectLastHome(String team, int count, int season, int matchday,
			String competition) {
		ArrayList<ExtendedFixture> results = new ArrayList<>();

		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName(NAME_CLASS);
			c = DriverManager.getConnection(DRIVER);
			c.setAutoCommit(false);

			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(SELECT_RESULTS+ season + " where matchday < " + matchday
					+ " and competition='" + competition + "' and (hometeamname = '" + team + "')  order by matchday"
					+ " desc limit " + count + SEMICOLON);
			while (rs.next()) {
				String date = rs.getString(DATE);
				String homeTeamName = rs.getString(HOME_TEAM_NAME);
				String awayTeamName = rs.getString(AWAY_TEAM_NAME);
				int homeGoals = rs.getInt(HOME_GOALS);
				int awayGoals = rs.getInt(AWAY_GOALS);
				String competit = rs.getString(COMPETITION);
				int matchd = rs.getInt(MATCHDAY);
				synchronized(format){
				ExtendedFixture ef = new ExtendedFixture(format.parse(date), homeTeamName, awayTeamName,
						new Result(homeGoals, awayGoals), competit).withMatchday(matchd);
				}
				results.add(ef);
			}
			rs.close();
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(WRONG);
			System.exit(0);
		}
		return results;
	}

	public static boolean checkExistense(String hometeam, String awayteam, String date, int season) {
		boolean flag = false;

		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName(NAME_CLASS);
			c = DriverManager.getConnection(DRIVER);
			c.setAutoCommit(false);
			stmt = c.createStatement();

			ResultSet rs = stmt
					.executeQuery(SELECT_RESULTS+ season + " where hometeamname = " + addQuotes(hometeam)
							+ " and awayteamname = " + addQuotes(awayteam) + " and date = " + addQuotes(date));
			flag = rs.next();

			rs.close();
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(WRONG);
			System.exit(0);
		}

		return flag;
	}

	public static ArrayList<String> getLeagues(int season) {
		ArrayList<String> leagues = new ArrayList<>();

		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName(NAME_CLASS);
			c = DriverManager.getConnection(DRIVER);
			c.setAutoCommit(false);
			stmt = c.createStatement();

			ResultSet rs = stmt.executeQuery("select distinct competition from results" + season);
			while (rs.next()) {
				leagues.add(rs.getString(COMPETITION));
			}

			rs.close();
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(WRONG);
			System.exit(0);
		}
		return leagues;
	}

	public static ArrayList<ExtendedFixture> selectLastAway(String team, int count, int season, int matchday,
			String competition) {
		ArrayList<ExtendedFixture> results = new ArrayList<>();

		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName(NAME_CLASS);
			c = DriverManager.getConnection(DRIVER);
			c.setAutoCommit(false);

			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(SELECT_RESULTS+ season + " where matchday < " + matchday
					+ " and competition='" + competition + "' and  (awayteamname = '" + team + "') order by matchday"
					+ " desc limit " + count + SEMICOLON);
			while (rs.next()) {
				String date = rs.getString(DATE);
				String homeTeamName = rs.getString(HOME_TEAM_NAME);
				String awayTeamName = rs.getString(AWAY_TEAM_NAME);
				int homeGoals = rs.getInt(HOME_GOALS);
				int awayGoals = rs.getInt(AWAY_GOALS);
				String competit = rs.getString(COMPETITION);
				int matchd = rs.getInt(MATCHDAY);
				synchronized(format){
					ExtendedFixture ef = new ExtendedFixture(format.parse(date), homeTeamName, awayTeamName,
						new Result(homeGoals, awayGoals), competit).withMatchday(matchd).withStatus("FINISHED");
				}
				results.add(ef);
			}
			rs.close();
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(WRONG);
			System.exit(0);
		}
		return results;
	}

	public static float selectAvgLeagueHome(String competition, int season, int matchday) {
		float average = -1.0f;
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName(NAME_CLASS);
			c = DriverManager.getConnection(DRIVER);
			c.setAutoCommit(false);

			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("select avg(homegoals) from results" + season + WHERE_COMPETITION
					+ addQuotes(competition) + AND_MATCHDAY + matchday);
			average = rs.getFloat("avg(homegoals)");

			rs.close();
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(WRONG);
			System.exit(0);
		}
		return average;
	}

	public static float selectAvgLeagueAway(String competition, int season, int matchday) {
		float average = -1.0f;
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName(NAME_CLASS);
			c = DriverManager.getConnection(DRIVER);
			c.setAutoCommit(false);

			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("select avg(awaygoals) from results" + season + WHERE_COMPETITION
					+ addQuotes(competition) + AND_MATCHDAY + matchday);
			average = rs.getFloat("avg(awaygoals)");

			rs.close();
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(WRONG);
			System.exit(0);
		}
		return average;
	}

	public static float selectAvgHomeTeamFor(String competition, String team, int season, int matchday) {
		float average = -1.0f;
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName(NAME_CLASS);
			c = DriverManager.getConnection(DRIVER);
			c.setAutoCommit(false);

			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("select avg(homegoals) from results" + season + WHERE_COMPETITION
					+ addQuotes(competition) + AND_MATCHDAY + matchday + " and hometeamname=" + addQuotes(team));
			average = rs.getFloat("avg(homegoals)");

			rs.close();
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(WRONG);
			System.exit(0);
		}
		return average;
	}

	public static float selectAvgHomeTeamAgainst(String competition, String team, int season, int matchday) {
		float average = -1.0f;
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName(NAME_CLASS);
			c = DriverManager.getConnection(DRIVER);
			c.setAutoCommit(false);

			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("select avg(awaygoals) from results" + season + WHERE_COMPETITION
					+ addQuotes(competition) + AND_MATCHDAY + matchday + " and hometeamname=" + addQuotes(team));
			average = rs.getFloat("avg(awaygoals)");

			rs.close();
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(WRONG);
			System.exit(0);
		}
		return average;
	}

	public static float selectAvgAwayTeamFor(String competition, String team, int season, int matchday) {
		float average = -1.0f;
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName(NAME_CLASS);
			c = DriverManager.getConnection(DRIVER);
			c.setAutoCommit(false);

			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("select avg(awaygoals) from results" + season + WHERE_COMPETITION
					+ addQuotes(competition) + AND_MATCHDAY + matchday + " and awayteamname=" + addQuotes(team));
			average = rs.getFloat("avg(awaygoals)");

			rs.close();
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(WRONG);
			System.exit(0);
		}
		return average;
	}

	public static float selectAvgAwayTeamAgainst(String competition, String team, int season, int matchday) {
		float average = -1.0f;
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName(NAME_CLASS);
			c = DriverManager.getConnection(DRIVER);
			c.setAutoCommit(false);

			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("select avg(homegoals) from results" + season + WHERE_COMPETITION
					+ addQuotes(competition) + AND_MATCHDAY + matchday + " and awayteamname=" + addQuotes(team));
			average = rs.getFloat("avg(homegoals)");

			rs.close();
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(WRONG);
			System.exit(0);
		}
		return average;
	}

	// update database with all results up to date for a season 30 days back
	public static void update(int season) throws ParseException {
		try {
			JSONArray arr = new JSONArray(
					Utils.query("http://api.football-data.org/alpha/soccerseasons/?season=" + season));
			for (int i = 0; i < arr.length(); i++) {
				String address = arr.getJSONObject(i).getJSONObject("_links").getJSONObject("fixtures")
						.getString("href") + "/?timeFrame=p30";
				String league = arr.getJSONObject(i).getString("league");
				JSONObject obj = createJSONObject(address);
				obj.getJSONArray("fixtures");
				JSONArray jsonFixtures = obj.getJSONArray("fixtures");

				ArrayList<ExtendedFixture> fixtures = Utils.createFixtureList(jsonFixtures);
				for (ExtendedFixture f : fixtures) {
					synchronized(format){
						if (f.status.equals("FINISHED") && !SQLiteJDBC.checkExistense(f.homeTeam, f.awayTeam, format.format(f.date), season))
							SQLiteJDBC.insert(f, league, "RESULTS" + season);
					}
				}
			}
		} catch (IOException | JSONException e) {
			System.out.println(WRONG);
		}
	}
	
}
