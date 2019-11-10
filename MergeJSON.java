package jsontest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class MergeJSON {
	private static void mergeJson(String path, String inputPrefix, String outputPrefix, int maximumSize)
			throws ParseException {

		File dir = new File(path);
		File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.matches(inputPrefix + "[0-9]+.json");
			}
		});

		JSONArray finalArray = new JSONArray();
		JSONObject finalObject = new JSONObject(), safeObject = new JSONObject();
		int outputCounter = 1;
		for (int fileCounter = files.length - 1; fileCounter >= 0; fileCounter--) {
			JSONParser jsonparser = new JSONParser();

			try (FileReader reader = new FileReader(files[fileCounter])) {
				Object object = jsonparser.parse(reader);
				JSONObject list = (JSONObject) object;

				Set<String> arrayNames = list.keySet();
				System.out.println();
				for (String arrayName : arrayNames) {
					JSONArray array = (JSONArray) list.get(arrayName);
					finalArray.addAll(array);

				}
			} catch (FileNotFoundException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}
			finalObject.putIfAbsent("strikers", finalArray);
			int Length = finalObject.toJSONString().toCharArray().length;
			// System.out.println("Length"+Length);
			if (Length <= maximumSize) {
				if (fileCounter == 0) {
					try (FileWriter writer = new FileWriter(path + outputPrefix + outputCounter + ".json")) {
						writer.write(safeObject.toJSONString());
						writer.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				safeObject = finalObject;

			} else {
				try (FileWriter writer = new FileWriter(path + outputPrefix  + outputCounter + ".json")) {
					writer.write(safeObject.toJSONString());
					writer.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				finalObject.remove("strikers");

				finalObject.putIfAbsent("strikers", finalArray);
				// System.out.println("final"+finalObject);
				safeObject = finalObject;
				finalArray.clear();

				outputCounter++;

			}

		}
	}

	public static void main(String[] args) throws ParseException {
		Scanner input = new Scanner(System.in);
		System.out.println("Enter the Folder path: ");
		String path = input.nextLine();
		System.out.println("Enter the Prefix for Input File: ");
		String inputPrefix = input.next();
		System.out.println("Enter the Prefix for Output File: ");
		String outputPrefix = input.next();
		System.out.println("Maximum Size for Output file");
		int maximumSize = input.nextInt();
		mergeJson(path, inputPrefix, outputPrefix, maximumSize);
	}
}
