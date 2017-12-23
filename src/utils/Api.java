package utils;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import main.ExtendedFixture;

/**
 * PJDCC - Summary for class responsabilities.
 *
 * @author fourplus <fourplus1718@gmail.com>
 * @since 1.0
 * @version 11 Changes done
 */

public class Api {

    /**
     * This field sets the timeframe
     */
	public static final String TIMEFRAME = "?timeFrame=";
    /**
     * This field sets the web site
     */
	public static final String BASE = "http://api.football-data.org/alpha/";
    /**
     * This field sets the array for fixtures
     */
	public static JSONArray getFixtures(int period) throws JSONException, IOException {
		JSONObject obj = new JSONObject(Utils.query(BASE + "fixtures/" + TIMEFRAME + "n" + period));
		return obj.getJSONArray("fixtures");
	}

	// currently returns all finished fixtures in the given competition for the
	// home/away team for the current season only
	// public static ArrayList<ExtendedFixture>
	// getTeamRelevantFixtures(ExtendedFixture fixture, String side)
	// throws JSONException, IOException {
	// String queryResult = Utils.query(
	// side.equals("home") ? (fixture.links_homeTeam + "/fixtures") :
	// (fixture.links_awayTeam + "/fixtures"));
	//
	// JSONArray jsonAll = new JSONObject(queryResult).getJSONArray("fixtures");
	// ArrayList<ExtendedFixture> all = Utils.createFixtureList(jsonAll);
	// ArrayList<ExtendedFixture> finished = new ArrayList<>();
	// for (ExtendedFixture f : all) {
	// if (f.status.equals("FINISHED") &&
	// f.competition.equals(fixture.competition))
	// finished.add(f);
	// }
	// return finished;
	// }

	// finds fixtures that will be played in the next 7 days
    /**
     * This field sets the ArrayList for find fixtures
     */
	public static ArrayList<ExtendedFixture> findFixtures(int period)
			throws JSONException, IOException, ParseException {
		JSONArray arr = getFixtures(period);
		return Utils.createFixtureList(arr);
	}

}
