package net.mcnations.bb.game;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;

import net.mcnations.bb.BuildBattle;
import net.mcnations.bb.game.states.Lobby;

public class BuildObjects {

	private static String randomBuild = "none";
	public static String votedCategory = "none";

	public static int[] categoryVotes = new int[10];

	public static String getBuild() throws FileNotFoundException, IOException {
		votedCategory = getVotedCategory();
		
		List<String> list = (ArrayList<String>) BuildBattle.getCorePlugin().getConfig()
				.getList("Builds." + votedCategory);

		Random rnd = new Random();
		return list.get(rnd.nextInt(list.size()));
	}

	// Fetch a new random String
	public static void setRandomBuild() {
		try {
			randomBuild = getBuild();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Get the randomBuild
	public static String getRandomBuild() {
		return randomBuild;
	}

	public static String getVotedCategory() {
		return translateCategory(maxValue(categoryVotes));
	}

	static int maxValue(int array[]) {
		Random rnd = new Random();
		// Change
		int arraySize = (8 + 1);
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < array.length; i++) {
			list.add(array[i]);
		}

		if (moreThanOnce(list, Collections.max(list))) {

			List<Integer> tempList = new ArrayList<Integer>();

			for (int i = 0; i < arraySize; i++) {
				if (list.get(i) == Collections.max(list)) {
					tempList.add(i);
				}
			}

			return tempList.get(rnd.nextInt(tempList.size()));
		}

		if (Collections.max(list) == 0) {
			int index = rnd.nextInt(arraySize);
			return index;
		}

		return list.indexOf(Collections.max(list));
	}

	public static String translateCategory(int i) {
		// Change
		switch (i) {
		case 0:
			return "Fruits";
		case 1:
			return "Breakfast";
		case 2:
			return "Dinner";
		case 3:
			return "Animals";
		case 4:
			return "Landscape";
		case 5:
			return "Medieval";
		case 6:
			return "Modern";
		case 7:
			return "Fantasy";
		case 8:
			return "Gaming";

		default:
			return null;

		}
	}

	private static int translateCategory(String name) {
		// Change
		switch (name) {
		case "fruits":
			return 0;
		case "breakfast":
			return 1;
		case "dinner":
			return 2;
		case "animals":
			return 3;
		case "landscape":
			return 4;
		case "medieval":
			return 5;
		case "modern":
			return 6;
		case "fantasy":
			return 7;
		case "gaming":
			return 8;

		default:
			return -1;

		}
	}

	/*
	 * private static void addArrayList(List<Integer> plotRating, int i) { if (i
	 * >= plotRating.size()) { plotRating.add(i, 1); } else if (i <
	 * plotRating.size()) { plotRating.set(i, plotRating.get(i) + 1); }
	 * 
	 * }
	 */

	public static void removeCategoryVote(String str) {
		
		if(str.toLowerCase().contains("nothing"))
		{
			return;
		}
		
		int categoryNum = translateCategory(str);
		categoryVotes[categoryNum] = (categoryVotes[categoryNum] - 1);
	}

	public static void addCategoryVote(String itemName) {
		// Change
		switch (itemName) {
		case "fruits":
			categoryVotes[0] += 1;
			break;

		case "breakfast":
			categoryVotes[1] += 1;
			break;

		case "dinner":
			categoryVotes[2] += 1;
			break;

		case "animals":
			categoryVotes[3] += 1;
			break;

		case "landscape":
			categoryVotes[4] += 1;
			break;

		case "medieval":
			categoryVotes[5] += 1;
			break;
			
		case "modern":
			categoryVotes[6] += 1;
			break;
			
		case "fantasy":
			categoryVotes[7] += 1;
			break;
			
		case "gaming":
			categoryVotes[8] += 1;
			break;

		}

	}

	public static void clearCategoryVotes() {
		Arrays.fill(categoryVotes, 0);
	}

	static boolean moreThanOnce(List<Integer> list, int searched) {

		int numCount = 0;
		boolean more = false;

		for (int thisNum : list) {
			if (thisNum == searched) {
				numCount++;
			}
		}

		if (numCount > 1) {
			more = true;
		}

		return more;
	}

}
