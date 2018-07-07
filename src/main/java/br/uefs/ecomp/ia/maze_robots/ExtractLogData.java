package br.uefs.ecomp.ia.maze_robots;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ExtractLogData {

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		File dir = new File("execucoes");
		run(dir);
	}

	private static void run(File file) throws FileNotFoundException, UnsupportedEncodingException {
		if (file.isDirectory()) {
			for (File f : file.listFiles())
				run(f);
			return;
		}

		if (!file.getName().equals("output.txt"))
			return;

		try (Scanner in = new Scanner(file, "UTF-8")) {
			List<Integer[]> values = new ArrayList<>(2000);
			String line;
			Integer[] value = null;

			while (in.hasNext()) {
				line = in.nextLine();
				if (line.startsWith("F max")) {
					value = new Integer[3];
					value[0] = Integer.parseInt(line.substring(line.indexOf("f:") + 2).trim());
				} else if (line.startsWith("F min")) {
					value[1] = Integer.parseInt(line.substring(line.indexOf("f:") + 2).trim());
				} else if (line.startsWith("F avg")) {
					value[2] = Integer.parseInt(line.substring(line.indexOf("g:") + 2).trim());
					values.add(value);
				}
			}

			try (PrintStream out = new PrintStream(new FileOutputStream(new File(file.getParentFile(), "values.txt"), false), true, "UTF-8")) {
				Integer[] v;
				for (int x = 0; x < values.size(); x++) {
					v = values.get(x);
					out.format("%d\t%d\t%d\n", v[0], v[1], v[2]);
				}
			}
		}
	}
}
