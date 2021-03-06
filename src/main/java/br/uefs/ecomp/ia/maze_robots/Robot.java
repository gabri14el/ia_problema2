package br.uefs.ecomp.ia.maze_robots;

import java.util.Arrays;
import java.util.Random;
import java.util.function.BiConsumer;
import br.uefs.ecomp.ia.maze_robots.core.Representation;

public class Robot extends Representation<String[][]> {
	/*
	 *STATE\INPUT
	 *				00000000		00000001		00000010		.....		11111111
	 * ...000		sseee...		sseee...		sseee...		.....		sseee...
	 * ...001		sseee...		sseee...		sseee...		.....		sseee...
	 * ...010		sseee...		sseee...		sseee...		.....		sseee...
	 * ......		sseee...		sseee...		sseee...		.....		sseee...
	 * ...111		sseee...		sseee...		sseee...		.....		sseee...
	 * 
	 * s - Saída
	 * e - Estado
	 */

	/* INPUTS = [00000000 - 11111111]
	 * 1º bit - Parede à esquerda
	 * 2º bit - Parede em cima
	 * 3º bit - Parede à direita
	 * 4º bit - Parede em baixo
	 * 
	 * 5º bit - Final ao oeste
	 * 6º bit - Final ao norte
	 * 7º bit - Final ao leste
	 * 8º bit - Final ao sul
	 */
	public static final int INPUT_SIZE = 0b11111111;
	public static final int INPUT_BITS = 8;

	/* OUTPUTS = [000-100]
	 * 000 - Nada
	 * 001 - Esquerda
	 * 010 - Subir
	 * 011 - Direita
	 * 100 - Descer
	 */
	public static final int OUTPUT_SIZE = 5;

	public Robot(int stateSize) {
		value = new String[stateSize][INPUT_SIZE];
	}

	public Robot(String[][] value) {
		this.value = value;
	}

	public int getStateSize() {
		return value.length;
	}

	public Integer getOutput(int s, int i) {
		return (value[s][i] != null) ? Integer.parseInt(value[s][i].substring(0, value[s][i].indexOf(":"))) : -1;
	}

	public void setOutput(int s, int i, int output) {
		set(s, i, output, getState(s, i));
	}

	public Integer getState(int s, int i) {
		return (value[s][i] != null) ? Integer.parseInt(value[s][i].substring(value[s][i].indexOf(":") + 1)) : -1;
	}

	public void setState(int s, int i, int state) {
		set(s, i, getOutput(s, i), state);
	}

	public void set(int s, int i, int output, int state) {
		value[s][i] = output + ":" + state;
	}

	public void forEach(BiConsumer<Integer, Integer> consumer) {
		forEach(0, 0, consumer);
	}

	public void forEach(int s, int ii, BiConsumer<Integer, Integer> consumer) {
		for (; s < getStateSize(); s++)
			for (int i = ii; i < INPUT_SIZE; i++)
				consumer.accept(s, i);
	}

	public String[][] copyValue(int newLength) {
		String[][] v = new String[newLength][value[0].length];
		if (newLength <= value.length) {
			for (int s = 0; s < newLength; s++)
				v[s] = value[s].clone();
		} else {
			for (int s = 0; s < newLength; s++)
				v[s] = value[s].clone();
			for (int s = value.length; s < newLength; s++)
				for (int i = 0; i < v[s].length; i++)
					v[s][i] = "-1:-1";
		}
		return v;
	}

	public void delState(Random random) {
		int state = getStateSize() - 1;
		value = copyValue(state);
		for (int s = 0; s < value.length; s++)
			for (int i = 0; i < value[s].length; i++)
				if (getState(s, i) == state)
					setState(s, i, random.nextInt(state));
	}

	public void addState(Random random) {
		int stateSize = getStateSize() + 1;
		value = Arrays.copyOf(value, stateSize);
		String[] newState = new String[INPUT_SIZE];
		for (int i = 0; i < INPUT_SIZE; i++)
			newState[i] = random.nextInt(OUTPUT_SIZE) + ":" + random.nextInt(stateSize);
		value[stateSize - 1] = newState;
	}

	public void changeLastState(Random random) {
		int stateSize = getStateSize();
		String[] newState = new String[INPUT_SIZE];
		for (int i = 0; i < INPUT_SIZE; i++)
			newState[i] = random.nextInt(OUTPUT_SIZE) + ":" + random.nextInt(stateSize);
		value[stateSize - 1] = newState;
	}

	// ==============================================================================================
	// ========================================== Sensores =============+============================
	// ==============================================================================================

	public int getLeftWallSensor(Maze maze, int ry, int rx) {
		String input = getInputBinary(maze, ry, rx);
		return Integer.parseInt("" + input.charAt(0));
	}

	public int getUpWallSensor(Maze maze, int ry, int rx) {
		String input = getInputBinary(maze, ry, rx);
		return Integer.parseInt("" + input.charAt(1));
	}

	public int getRightWallSensor(Maze maze, int ry, int rx) {
		String input = getInputBinary(maze, ry, rx);
		return Integer.parseInt("" + input.charAt(2));
	}

	public int getDownWallSensor(Maze maze, int ry, int rx) {
		String input = getInputBinary(maze, ry, rx);
		return Integer.parseInt("" + input.charAt(3));
	}

	public int getLeftEndSensor(Maze maze, int ry, int rx) {
		String input = getInputBinary(maze, ry, rx);
		return Integer.parseInt("" + input.charAt(4));
	}

	public int getUpEndSensor(Maze maze, int ry, int rx) {
		String input = getInputBinary(maze, ry, rx);
		return Integer.parseInt("" + input.charAt(5));
	}

	public int getRightEndSensor(Maze maze, int ry, int rx) {
		String input = getInputBinary(maze, ry, rx);
		return Integer.parseInt("" + input.charAt(6));
	}

	public int getDownEndSensor(Maze maze, int ry, int rx) {
		String input = getInputBinary(maze, ry, rx);
		return Integer.parseInt("" + input.charAt(7));
	}

	// ==============================================================================================
	// =================================== Navegação no Labirinto ===================================
	// ==============================================================================================

	public String getInputBinary(Maze maze, int ry, int rx) {
		String i = Integer.toBinaryString(getInput(maze, ry, rx));
		for (int x = i.length(); x < INPUT_BITS; x++)
			i = '0' + i;
		return i;
	}

	public int getInput(Maze maze, int ry, int rx) {
		int input = 0;

		// Não é possível ir mais à esquerda ou é uma parede
		input += (rx == 0 || maze.isWall(ry, rx - 1)) ? 1 : 0;
		input <<= 1;

		// Não é possível ir mais para cima ou é uma parede;
		input += (ry == 0 || maze.isWall(ry - 1, rx)) ? 1 : 0;
		input <<= 1;

		// Não é possível ir mais à direita ou é uma parede
		input += ((rx + 1) == maze.getXLength() || maze.isWall(ry, rx + 1)) ? 1 : 0;
		input <<= 1;

		// Não é possível ir mais para baixo ou é uma parede;
		input += ((ry + 1) == maze.getYLength() || maze.isWall(ry + 1, rx)) ? 1 : 0;
		input <<= 1;

		// Final está ao oeste
		input += (maze.getEX() < rx) ? 1 : 0;
		input <<= 1;

		// Final ao norte
		input += (maze.getEY() < ry) ? 1 : 0;
		input <<= 1;

		// Final ao leste
		input += (rx < maze.getEX()) ? 1 : 0;
		input <<= 1;

		// Final ao sul
		input += (ry < maze.getEY()) ? 1 : 0;

		return input;
	}

	public int sumY(int output) {
		if (output == 0b010)
			return -1;
		if (output == 0b100)
			return +1;
		return 0;
	}

	public int sumX(int output) {
		if (output == 0b001)
			return -1;
		if (output == 0b011)
			return +1;
		return 0;
	}

	// ==============================================================================================
	// ==============================================================================================

	@Override
	protected Robot clone() {
		Robot r = new Robot(getStateSize());
		r.setValue(copyValue(value.length));
		r.setParent(this);
		return r;
	}

	@Override
	public String toString() {
		String r = "Robot: id=" + id + ", fitness=" + fitness + ", value={";

		for (int s = 0; s < value.length; s++) {
			r += '{';
			for (int i = 0; i < value[s].length; i++) {
				r += String.format("{%d,%d}", getOutput(s, i), getState(s, i));
				if (i < value[s].length - 1)
					r += ',';
			}
			r += '}';
			if (s < value.length - 1)
				r += ',';
		}
		r += " parents={";
		Robot p = (Robot) getParent();
		if (p != null) {
			r += "\n" + p.toString();
			p = (Robot) p.getParent();
		}
		r += "}}";
		return r;
	}
}