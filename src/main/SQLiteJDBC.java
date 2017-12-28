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
public class SQLiteJDBC {
	
	private final String NAME_CLASS = "org.sqlite.JDBC";
	private final String DRIVER = "jdbc:sqlite:test.db";
	private final String WRONG = "Something was wrong";
	private final String VALUES = "VALUES (";
	private final String COMMA = ",";
	private final String BRACKETS_SEMICOLON = " );";
	private final String TUKA = "tuka";
	private final String SEMICOLON = ";";
	private final String WHERE_YEAR = " where year=";
	private final String AND_COMPETITION = " AND competition=";
	private final String DATE = "date";
	private final String HOME_TEAM_NAME = "hometeamname";
	private final String AWAY_TEAM_NAME = AWAY_TEAM_NAME;
	
    /**
     * This field sets the variable of class DateFormat
     */
	public static final DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

	public static void createDB() {
		Connection c = null;
		try {
			Class.forName(NAME_CLASS);
			c = DriverManager.getConnection(DRIVER);
		} catch (Exception e) {
			System.err.println(WRONG);
			System.exit(0);
		}
	}

	public static void createTable(int year) {
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName(NAME_CLASS);
			c = DriverManager.getConnection(DRIVER);

			stmt = c.createStatement();
			String sql = "CREATE TABLE RESULTS" + year + " (DATE TEXT      NOT NULL,"
					+ " HOMETEAMNAME  TEXT     NOT NULL, " + " AWAYTEAMNAME  TEXT     NOT NULL, "
					+ " HOMEGOALS  INT   NOT NULL, " + " AWAYGOALS  INT   NOT NULL, " + " COMPETITION TEXT  NOT NULL, "
					+ " MATCHDAY INT       NOT NULL, " + " PRIMARY KEY (DATE, HOMETEAMNAME, AWAYTEAMNAME)) ";
			stmt.executeUpdate(sql);
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(WRONG);
			System.exit(0);
		}
	}

	// insert Fixture entry into DB
	public static void insert(ExtendedFixture f, String competition, String tableName) {
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName(NAME_CLASS);
			c = DriverManager.getConnection(DRIVER);
			c.setAutoCommit(false);
			
			stmt = c.createStatement();
			synchronized(format){
			String sql = "INSERT INTO " + tableName
					+ " (DATE,HOMETEAMNAME,AWAYTEAMNAME,HOMEGOALS,AWAYGOALS,COMPETITION,MATCHDAY)" + VALUES
					+ addQuotes(format.format(f.date)) + COMMA + addQuotes(f.homeTeam) + COMMA + addQuotes(f.awayTeam) + COMMA
					+ f.result.goalsHomeTeam + COMMA + f.result.goalsAwayTeam + COMMA + addQuotes(competition) + COMMA
					+ f.matchday + BRACKETS_SEMICOLON;
			}
			try {
				stmt.executeUpdate(sql);
			} catch (SQLException e) {
				System.out.println(TUKA);

			}

			stmt.close();
			c.commit();
			c.close();
		} catch (Exception e) {
			System.err.println(WRONG);
			try {
				c.close();
			} catch (SQLException e1) {
				System.out.println(WRONG);
			}
			System.exit(0);
		}
	}

	// populate database with all results up to date for a season
	public static void populateInitial(int season) throws ParseException {
		try {
			JSONArray arr = new JSONArray(
					Utils.query("http://api.football-data.org/alpha/soccerseasons/?season=" + season));
			for (int i = 0; i < arr.length(); i++) {
				String address = arr.getJSONObject(i).getJSONObject("_links").getJSONObject("fixtures")
						.getString("href");
				String league = arr.getJSONObject(i).getString("league");
				JSONObject obj = createJSONObject(address);
				obj.getJSONArray("fixtures");
				JSONArray jsonFixtures = obj.getJSONArray("fixtures");

				ArrayList<ExtendedFixture> fixtures = Utils.createFixtureList(jsonFixtures);
				for (ExtendedFixture f : fixtures) {
					if (f.status.equals("FINISHED")) {
						SQLiteJDBC.insert(f, league, "RESULTS" + season);
					}
				}
			}
		} catch (IOException | JSONException e) {
			System.out.println(WRONG);
		}
	}

	// insert Fixture entry into DB
	public static void storeSettings(Settings s, int year, int period) {
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName(NAME_CLASS);
			c = DriverManager.getConnection(DRIVER);
			c.setAutoCommit(false);

			stmt = c.createStatement();

			String sql = "INSERT INTO SETTINGS"
					+ " (LEAGUE,PERIOD,SEASON,BASIC,POISSON,WPOISSON,HTCOMBO,HTOVERONE,THRESHOLD,LOWER,UPPER,MINUNDER,MAXUNDER,MINOVER,MAXOVER,VALUE,SUCCESSRATE,PROFIT)"
					+ VALUES + addQuotes(s.league) + COMMA + period + COMMA + year + COMMA + s.basic + COMMA + s.poisson
					+ COMMA + s.weightedPoisson + COMMA + s.htCombo + COMMA + s.halfTimeOverOne + COMMA + s.threshold + COMMA
					+ s.upperBound + COMMA + s.lowerBound + COMMA + s.minUnder + COMMA + s.maxUnder + COMMA + s.minOver + COMMA
					+ s.maxOver + COMMA + s.value + COMMA + s.successRate + COMMA + s.profit + BRACKETS_SEMICOLON;
			try {
				stmt.executeUpdate(sql);
			} catch (SQLException e) {
				System.out.println(TUKA);
			}

			stmt.close();
			c.commit();
			c.close();
		} catch (Exception e) {
			System.err.println(WRONG);
			try {
				c.close();
			} catch (SQLException e1) {
				System.out.println(WRONG);
			}
			System.exit(0);
		}
	}

	// insert score
	public static void insertBasic(ExtendedFixture f, float score, int year, String tableName) {
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName(NAME_CLASS);
			c = DriverManager.getConnection(DRIVER);
			c.setAutoCommit(false);

			stmt = c.createStatement();
			synchronized(format){
			String sql = "INSERT INTO " + tableName + " (DATE,HOMETEAMNAME,AWAYTEAMNAME,YEAR,COMPETITION,SCORE)"
					+ VALUES + addQuotes(format.format(f.date)) + COMMA + addQuotes(f.homeTeam) + COMMA
					+ addQuotes(f.awayTeam) + COMMA + year + COMMA + addQuotes(f.competition) + COMMA + score + BRACKETS_SEMICOLON;
			}
			try {
				if (!Float.isNaN(score))
					stmt.executeUpdate(sql);
			} catch (SQLException e) {
				System.out.println(TUKA);
			}

			stmt.close();
			c.commit();
			c.close();
		} catch (Exception e) {
			System.err.println(WRONG);
			try {
				c.close();
			} catch (SQLException e1) {
				System.out.println(WRONG);
			}
			System.exit(0);
		}
	}

	// refavtor after min max odds change
	public static Settings getSettings(String league, int year, int period) {
		Settings sett = null;
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName(NAME_CLASS);
			c = DriverManager.getConnection(DRIVER);
			c.setAutoCommit(false);

			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("select * from settings where league=" + addQuotes(league) + " and SEASON="
					+ year + " and PERIOD=" + period + SEMICOLON);
			while (rs.next()) {
				float basic = rs.getFloat("basic");
				float poisson = rs.getFloat("poisson");
				float wpoisson = rs.getFloat("wpoisson");
				float htCombo = rs.getFloat("htcombo");
				float htOverOne = rs.getFloat("htoverone");
				float threshold = rs.getFloat("threshold");
				float lower = rs.getFloat("lower");
				float upper = rs.getFloat("upper");
				float minUnder = rs.getFloat("minunder");
				float maxUnder = rs.getFloat("maxunder");
				float minOver = rs.getFloat("minover");
				float maxOver = rs.getFloat("maxover");
				float value = rs.getFloat("value");
				float success = rs.getFloat("successrate");
				float profit = rs.getFloat("profit");
				sett = new Settings(league, basic, poisson, wpoisson, threshold, upper, lower, success, profit)
						.withYear(year).withValue(value).withMinMax(minUnder, maxUnder, minOver, maxOver)
						.withHT(htOverOne, htCombo);
			}
			rs.close();
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(WRONG);
			System.exit(0);
		}

		return sett;
	}

	public static String addQuotes(String s) {
		StringBuilder sb = new StringBuilder();
		for (char c : s.toCharArray()) {
			if (c == '\'')
				sb.append('\\');
			else
				sb.append(c);
		}
		String escaped = sb.toString();
		return "'" + escaped + "'";
	}

	public static void deleteSettings(String league, int year) {
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName(NAME_CLASS);
			c = DriverManager.getConnection(DRIVER);
			c.setAutoCommit(false);

			stmt = c.createStatement();
			stmt.executeUpdate("delete  from settings where league=" + addQuotes(league) + " and SEASON=" + year + SEMICOLON);

			stmt.close();
			c.commit();
			c.close();
		} catch (Exception e) {
			System.err.println(WRONG);
			System.exit(0);
		}

	}

	public static synchronized HashMap<ExtendedFixture, Float> selectScores(ArrayList<ExtendedFixture> all,
			String table, int year, String competition) throws InterruptedException {

		HashMap<ExtendedFixture, Float> result = new HashMap<>();

		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName(NAME_CLASS);
			c = DriverManager.getConnection(DRIVER);
			c.setAutoCommit(false);

			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("select * from " + table + WHERE_YEAR + year + AND_COMPETITION
					+ addQuotes(competition) + SEMICOLON);
			while (rs.next()) {
				String date = rs.getString(DATE);
				String homeTeamName = rs.getString(HOME_TEAM_NAME);
				String awayTeamName = rs.getString(AWAY_TEAM_NAME);
				Float score = rs.getFloat("score");
				synchronized(format){
					ExtendedFixture ef = new ExtendedFixture(format.parse(date), homeTeamName, awayTeamName,
							new Result(-1, -1), competition);
				}
				result.put(ef, score);
			}
			rs.close();
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(WRONG);
			System.exit(0);
		}

		return result;
	}

	private static void controlTableName(String tableName, ExtendedFixture f, HSSFSheet sheet, Statement stmt, int year)
	{
		float score = Float.NaN;
		if (tableName.equals("BASICS")) {
			score = XlSUtils.basic2(f, sheet, 0.6f, 0.3f, 0.1f);
		} else if (tableName.equals("POISSON")) {
			score = XlSUtils.poisson(f, sheet);
		} else if (tableName.equals("WEIGHTED")) {
			score = XlSUtils.poissonWeighted(f, sheet);
		} else if (tableName.equals("HALFTIME1")) {
			score = XlSUtils.halfTimeOnly(f, sheet, 1);
		} else if (tableName.equals("HALFTIME2")) {
			score = XlSUtils.halfTimeOnly(f, sheet, 2);
		} else if (tableName.equals("SHOTS")) {
			score = XlSUtils.shots(f, sheet);
		}
		synchronized(format){
		String sql = "INSERT INTO " + tableName + " (DATE,HOMETEAMNAME,AWAYTEAMNAME,YEAR,COMPETITION,SCORE)"
				+ VALUES + addQuotes(format.format(f.date)) + COMMA + addQuotes(f.homeTeam) + COMMA
				+ addQuotes(f.awayTeam) + COMMA + year + COMMA + addQuotes(f.competition) + COMMA + score + BRACKETS_SEMICOLON;
		}
		if (!Float.isNaN(score))
			stmt.executeUpdate(sql);
	}
	
	public static void insertBasic(HSSFSheet sheet, ArrayList<ExtendedFixture> all, int year, String tableName) {
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName(NAME_CLASS);
			c = DriverManager.getConnection(DRIVER);
			c.setAutoCommit(false);

			stmt = c.createStatement();
			try {
				for (ExtendedFixture f : all) {
					controlTableName(tableName, f, sheet, stmt, year);
				}
			} catch (SQLException e) {
				System.out.println(TUKA);
			}
			stmt.close();
			c.commit();
			c.close();
		} catch (Exception e) {
			System.err.println(WRONG);
			try {
				c.close();
			} catch (SQLException e1) {
				System.out.println(WRONG);
			}
			System.exit(0);
		}

	}

	public static synchronized void storeFinals(ArrayList<FinalEntry> finals, int year, String competition,
			String description) {
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName(NAME_CLASS);
			c = DriverManager.getConnection(DRIVER);
			c.setAutoCommit(false);

			stmt = c.createStatement();
			try {
				for (FinalEntry f : finals) {
	
					String sql = "INSERT INTO FINALS "
							+ "(DESCRIPTION,YEAR,DATE,COMPETITION,MATCHDAY,HOMETEAMNAME,AWAYTEAMNAME,HOMEGOALS,AWAYGOALS,OVER,UNDER,SCORE,THOLD,LOWER,UPPER,VALUE)"
							+ VALUES + addQuotes(description) + COMMA + year + COMMA
							+ addQuotes(format.format(f.fixture.date)) + COMMA + addQuotes(competition) + COMMA
							+ f.fixture.matchday + COMMA + addQuotes(f.fixture.homeTeam) + COMMA + addQuotes(f.fixture.awayTeam)
							+ COMMA + f.fixture.result.goalsHomeTeam + COMMA + f.fixture.result.goalsAwayTeam + COMMA
							+ f.fixture.maxOver + COMMA + f.fixture.maxUnder + COMMA
							+ (float) Math.round(f.prediction * 100000f) / 100000f + COMMA + f.threshold + COMMA + f.lower + COMMA
							+ f.upper + COMMA + f.value + BRACKETS_SEMICOLON;
					
					if (!Float.isNaN(f.prediction))
						stmt.executeUpdate(sql);
				}
			} catch (SQLException e) {
					System.out.println(WRONG);
					System.out.println(TUKA);
				}

			stmt.close();
			c.commit();
			c.close();
		} catch (Exception e) {
			System.err.println(WRONG);
			try {
				c.close();
			} catch (SQLException e1) {
				System.out.println(WRONG);
			}
			System.exit(0);
		}
	}

	public static synchronized void storePlayerFixtures(ArrayList<PlayerFixture> finals, int year, String competition) {
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName(NAME_CLASS);
			c = DriverManager.getConnection(DRIVER);
			c.setAutoCommit(false);

			int success = 0;
			int fails = 0;
			stmt = c.createStatement();
			try {
				for (PlayerFixture f : finals) {
					String sql = "INSERT INTO PLAYERFIXTURES "
							+ "(DATE,HOMETEAMNAME,AWAYTEAMNAME,HOMEGOALS,AWAYGOALS,YEAR,COMPETITION,TEAM,NAME,MINUTESPLAYED,LINEUP,SUBSTITUTE,GOALS,ASSISTS)"
							+ VALUES + addQuotes(format.format(f.fixture.date)) + COMMA + addQuotes(f.fixture.homeTeam)
							+ COMMA + addQuotes(f.fixture.awayTeam) + COMMA + f.fixture.result.goalsHomeTeam + COMMA
							+ f.fixture.result.goalsAwayTeam + COMMA + year + COMMA + addQuotes(competition) + COMMA
							+ addQuotes(f.team) + COMMA + addQuotes(f.name) + COMMA + f.minutesPlayed + COMMA + (f.lineup ? 1 : 0)
							+ COMMA + (f.substitute ? 1 : 0) + COMMA + f.goals + COMMA + f.assists + BRACKETS_SEMICOLON;
					
					stmt.executeUpdate(sql);
				}
			} catch (SQLException e) {
				fails++;
				System.out.println(WRONG);
			}

			stmt.close();
			c.commit();
			c.close();
		} catch (Exception e) {
			System.err.println(WRONG);
			try {
				c.close();
			} catch (SQLException e1) {
				System.out.println(WRONG);
			}
			System.exit(0);
		}
	}

	public static synchronized ArrayList<PlayerFixture> selectPlayerFixtures(String competition, int year)
			throws InterruptedException {

		ArrayList<PlayerFixture> result = new ArrayList<>();

		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName(NAME_CLASS);
			c = DriverManager.getConnection(DRIVER);
			c.setAutoCommit(false);

			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("select * from playerfixtures" + WHERE_YEAR + year
					+ AND_COMPETITION + addQuotes(competition) + SEMICOLON);
			while (rs.next()) {
				String date = rs.getString(DATE);
				String homeTeamName = rs.getString(HOME_TEAM_NAME);
				String awayTeamName = rs.getString(AWAY_TEAM_NAME);
				int homeGoals = rs.getInt("homegoals");
				int awayGoals = rs.getInt("awaygoals");
				String team = rs.getString("team");
				String name = rs.getString("name");
				int minutesPlayed = rs.getInt("minutesPlayed");
				int lineup = rs.getInt("lineup");
				int substitute = rs.getInt("substitute");
				int goals = rs.getInt("goals");
				int assists = rs.getInt("assists");
				synchronized(format){
					PlayerFixture pf = new PlayerFixture(new ExtendedFixture(format.parse(date), homeTeamName, awayTeamName,
										new Result(homeGoals, awayGoals), competition),
										team, name, minutesPlayed, lineup == 1, substitute == 1, goals, assists);
					}
				result.add(pf);
			}
			rs.close();
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(WRONG);
			System.exit(0);
		}
		return result;
	}
	
	private static ExtendedFixture createExtendedFixture(String date, String homeTeamName, String awayTeamName,
															int homeGoals, int awayGoals, String competition, int matchday,
															float over, float under, int year){
		synchronized(format){
			return new ExtendedFixture(format.parse(date), homeTeamName, awayTeamName,new Result(homeGoals, awayGoals), competition).withMatchday(matchday).withOdds(0f, 0f, over, under).withYear(year);
		}
	}

	public static synchronized ArrayList<FinalEntry> selectFinals(String competition, int year, String description)
			throws InterruptedException {

		ArrayList<FinalEntry> result = new ArrayList<>();

		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName(NAME_CLASS);
			c = DriverManager.getConnection(DRIVER);
			c.setAutoCommit(false);

			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("select * from finals" + WHERE_YEAR + year + AND_COMPETITION
					+ addQuotes(competition) + " AND description=" + addQuotes(description) + SEMICOLON);
			while (rs.next()) {
				String date = rs.getString(DATE);
				int matchday = rs.getInt("matchday");
				String homeTeamName = rs.getString(HOME_TEAM_NAME);
				String awayTeamName = rs.getString(AWAY_TEAM_NAME);
				int homeGoals = rs.getInt("homeGoals");
				int awayGoals = rs.getInt("awayGoals");
				float over = rs.getFloat("over");
				float under = rs.getFloat("under");
				Float score = rs.getFloat("score");
				float thold = rs.getFloat("thold");
				float lower = rs.getFloat("lower");
				float upper = rs.getFloat("upper");
				float value = rs.getFloat("value");
				ExtendedFixture ef = createExtendedFixture(date,homeTeamName,awayTeamName,homeGoals,awayGoals,competition,matchday,over,under,year);
				FinalEntry f = new FinalEntry(ef, score, new Result(homeGoals, awayGoals), thold, lower, upper);
				f.value = value;

				result.add(f);
			}
			rs.close();
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(WRONG);
			System.exit(0);
		}
		return result;
	}

	public static synchronized ArrayList<HTEntry> selectHTData(String competition, int year, String description)
			throws InterruptedException {

		ArrayList<HTEntry> result = new ArrayList<>();

		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName(NAME_CLASS);
			c = DriverManager.getConnection(DRIVER);
			c.setAutoCommit(false);

			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("select * from halftimedata" + WHERE_YEAR + year + AND_COMPETITION
					+ addQuotes(competition) + " AND description=" + addQuotes(description) + SEMICOLON);
			while (rs.next()) {
				String date = rs.getString(DATE);
				int matchday = rs.getInt("matchday");
				String homeTeamName = rs.getString(HOME_TEAM_NAME);
				String awayTeamName = rs.getString(AWAY_TEAM_NAME);
				int homeGoals = rs.getInt("homeGoals");
				int awayGoals = rs.getInt("awayGoals");
				float over = rs.getFloat("over");
				float under = rs.getFloat("under");
				Float score = rs.getFloat("score");
				float thold = rs.getFloat("thold");
				float lower = rs.getFloat("lower");
				float upper = rs.getFloat("upper");
				float value = rs.getFloat("value");
				float zero = rs.getFloat("zero");
				float one = rs.getFloat("one");
				float two = rs.getFloat("two");
				float more = rs.getFloat("more");

				ExtendedFixture ef = createExtendedFixture(date,homeTeamName,awayTeamName,homeGoals,awayGoals,competition,matchday,over,under,year);
				FinalEntry f = new FinalEntry(ef, score, new Result(homeGoals, awayGoals), thold, lower, upper);
				f.value = value;
				HTEntry hte = new HTEntry(f, zero, one, two, more);

				result.add(hte);
			}
			rs.close();
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(WRONG);
			System.exit(0);
		}

		return result;
	}

	public static Lines closestLine(ExtendedFixture f) {
		ArrayList<Lines> result = new ArrayList<>();
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName(NAME_CLASS);
			c = DriverManager.getConnection(DRIVER);
			c.setAutoCommit(false);

			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("select * from line" + " where home=" + f.asianHome + SEMICOLON);

			while (rs.next()) {
				String type = rs.getString("type");
				float line = rs.getFloat("line");
				float home = rs.getFloat("home");
				float away = rs.getFloat("away");
				float line1home = rs.getFloat("line1home");
				float line1away = rs.getFloat("line1away");
				float line2home = rs.getFloat("line2home");
				float line2away = rs.getFloat("line2away");
				float line3home = rs.getFloat("line3home");
				float line3away = rs.getFloat("line3away");
				float line4home = rs.getFloat("line4home");
				float line4away = rs.getFloat("line4away");

				Lines l = new Lines(type, line, home, away, line1home, line1away, line2home, line2away, line3home,
						line3away, line4home, line4away);
				result.add(l);
			}
			rs.close();
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(WRONG);
			System.exit(0);
		}

		if (result.isEmpty())
			System.out.println("NO LINE FOUND for " + f.asianHome);
		return result.get(0);
	}

	public static synchronized void storeHTData(ArrayList<HTEntry> halftimeData, int year, String competition,
			String description) {
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName(NAME_CLASS);
			c = DriverManager.getConnection(DRIVER);
			c.setAutoCommit(false);

			stmt = c.createStatement();
			try {
				for (HTEntry f : halftimeData) {

					String sql = "INSERT INTO HALFTIMEDATA "
							+ "(DESCRIPTION,YEAR,DATE,COMPETITION,MATCHDAY,HOMETEAMNAME,AWAYTEAMNAME,HOMEGOALS,AWAYGOALS,OVER,UNDER,SCORE,THOLD,LOWER,UPPER,VALUE,ZERO,ONE,TWO,MORE)"
							+ VALUES + addQuotes(description) + COMMA + year + COMMA
							+ addQuotes(format.format(f.fe.fixture.date)) + COMMA + addQuotes(competition) + COMMA
							+ f.fe.fixture.matchday + COMMA + addQuotes(f.fe.fixture.homeTeam) + COMMA
							+ addQuotes(f.fe.fixture.awayTeam) + COMMA + f.fe.fixture.result.goalsHomeTeam + COMMA
							+ f.fe.fixture.result.goalsAwayTeam + COMMA + f.fe.fixture.maxOver + COMMA + f.fe.fixture.maxUnder
							+ COMMA + (float) Math.round(f.fe.prediction * 100000f) / 100000f + COMMA + f.fe.threshold + COMMA
							+ f.fe.lower + COMMA + f.fe.upper + COMMA + f.fe.value + COMMA + f.zero + COMMA + f.one + COMMA + f.two
							+ COMMA + f.more + BRACKETS_SEMICOLON;
					
					if (!Float.isNaN(f.fe.prediction))
						stmt.executeUpdate(sql);
				}
			} catch (SQLException e) {
				System.out.println(WRONG);
				System.out.println(TUKA);
			}

			stmt.close();
			c.commit();
			c.close();
		} catch (Exception e) {
			System.err.println(WRONG);
			try {
				c.close();
			} catch (SQLException e1) {
				System.out.println(WRONG);
			}
			System.exit(0);
		}
	}

	public synchronized static void storeAllData(ArrayList<AllEntry> halftimeData, int year, String competition,
			String description) {
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName(NAME_CLASS);
			c = DriverManager.getConnection(DRIVER);
			c.setAutoCommit(false);

			stmt = c.createStatement();
			try {
				for (AllEntry f : halftimeData) {
					String sql = "INSERT INTO ALLDATA "
							+ "(DESCRIPTION,YEAR,DATE,COMPETITION,MATCHDAY,HOMETEAMNAME,AWAYTEAMNAME,HOMEGOALS,AWAYGOALS,OVER,UNDER,SCORE,THOLD,LOWER,UPPER,VALUE,ZERO,ONE,TWO,MORE,BASIC,POISSON,WEIGHTED,SHOTS)"
							+ VALUES + addQuotes(description) + COMMA + year + COMMA
							+ addQuotes(format.format(f.fe.fixture.date)) + COMMA + addQuotes(competition) + COMMA
							+ f.fe.fixture.matchday + COMMA + addQuotes(f.fe.fixture.homeTeam) + COMMA
							+ addQuotes(f.fe.fixture.awayTeam) + COMMA + f.fe.fixture.result.goalsHomeTeam + COMMA
							+ f.fe.fixture.result.goalsAwayTeam + COMMA + f.fe.fixture.maxOver + COMMA + f.fe.fixture.maxUnder
							+ COMMA + (float) Math.round(f.fe.prediction * 100000f) / 100000f + COMMA + f.fe.threshold + COMMA
							+ f.fe.lower + COMMA + f.fe.upper + COMMA + f.fe.value + COMMA + f.zero + COMMA + f.one + COMMA + f.two
							+ COMMA + f.more + COMMA+ f.basic + COMMA + f.poisson + COMMA + f.weighted + COMMA + f.shots + BRACKETS_SEMICOLON;
					
						if (!Float.isNaN(f.fe.prediction))
							stmt.executeUpdate(sql);
				
				}
			} catch (SQLException e) {
				System.out.println(WRONG);
				System.out.println(TUKA);
			}

			stmt.close();
			c.commit();
			c.close();
		} catch (Exception e) {
			System.err.println(WRONG);
			try {
				c.close();
			} catch (SQLException e1) {
				System.out.println(WRONG);
			}
			System.exit(0);
		}
	}
	
	private static JSONObject createJSONObject(String address){
		return new JSONObject(Utils.query(address));
	}

}