package br.uefs.ecomp.ia.maze_robots;

import java.util.Arrays;
import java.util.function.BiConsumer;
import br.uefs.ecomp.ia.maze_robots.core.Representation;

public class Robot extends Representation<Integer[][][]> {
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
	private static final int INPUT_SIZE = 0b11111111;

	/* OUTPUTS = [000-100]
	 * 000 - Nada
	 * 001 - Esquerda
	 * 010 - Subir
	 * 011 - Direita
	 * 100 - Descer
	 */
	public static final int OUTPUT_SIZE = 0b100;

	public Robot(int stateSize) {
		value = new Integer[stateSize][INPUT_SIZE][2];
		for (Integer[][] state : value) {
			for (Integer[] v : state) {
				v[0] = -1;
				v[1] = -1;
			}
		}
	}

	public int getStateSize() {
		return value.length;
	}

	public Integer getOutput(int s, int i) {
		return value[s][i][0];
	}

	public void setOutput(int s, int i, int output) {
		value[s][i][0] = output;
	}

	public Integer getState(int s, int i) {
		return value[s][i][1];
	}

	public void setState(int s, int i, int state) {
		value[s][i][1] = state;
	}

	public void forEach(BiConsumer<Integer, Integer> consumer) {
		forEach(0, 0, consumer);
	}

	public void forEach(int s, int ii, BiConsumer<Integer, Integer> consumer) {
		for (; s < getStateSize(); s++)
			for (int i = ii; i < INPUT_SIZE; i++)
				consumer.accept(s, i);
	}

	public void delState() {
		int state = getStateSize() - 1;
		value = Arrays.copyOf(value, state);
		for (Integer[] v : value[value.length - 1]) {
			if (v[1] == state)
				v[1] = -1;
		}
	}

	public void addState() {
		value = Arrays.copyOf(value, getStateSize() + 1);
		value[value.length - 1] = new Integer[INPUT_SIZE][2];
		for (Integer[] v : value[value.length - 1]) {
			v[0] = -1;
			v[1] = -1;
		}
	}

	// ==============================================================================================
	// =================================== Navegação no Labirinto ===================================
	// ==============================================================================================

	public int getInput(Maze maze, int rx, int ry) {
		int input = 0;

		// Não é possível ir mais à esquerda ou é uma parede
		input += (ry == 0 || maze.getMaze()[rx][ry - 1] == Maze.WALL) ? 1 : 0;
		input <<= 1;

		// Não é possível ir mais para cima ou é uma parede;
		input += (rx == 0 || maze.getMaze()[rx - 1][ry] == Maze.WALL) ? 1 : 0;
		input <<= 1;

		// Não é possível ir mais à direita ou é uma parede
		input += ((ry + 1) == maze.getMaze()[0].length || maze.getMaze()[rx][ry + 1] == Maze.WALL) ? 1 : 0;
		input <<= 1;

		// Não é possível ir mais para baixo ou é uma parede;
		input += ((rx + 1) == maze.getMaze().length || maze.getMaze()[rx + 1][ry] == Maze.WALL) ? 1 : 0;
		input <<= 1;

		// Final está ao oeste
		input += (maze.getEY() < ry) ? 1 : 0;
		input <<= 1;

		// Final ao norte
		input += (maze.getEX() < rx) ? 1 : 0;
		input <<= 1;

		// Final ao leste
		input += (ry < maze.getEY()) ? 1 : 0;
		input <<= 1;

		// Final ao sul
		input += (rx < maze.getEX()) ? 1 : 0;

		return input;
	}

	public int sumX(int output) {
		if (output == 0b010)
			return -1;
		if (output == 0b100)
			return +1;
		return 0;
	}

	public int sumY(int output) {
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
		r.value = Arrays.copyOf(this.value, this.value.length);
		return r;
	}

	@Override
	public String toString() {
		String r = "Robot: id=" + id + ", fitness=" + fitness + ", value={";

		for (int s = 0; s < value.length; s++) {
			r += '{';
			for (int i = 0; i < value[s].length; i++) {
				r += String.format("%d|%04d", getOutput(s, i), getState(s, i));
				if (i < value[s].length - 1)
					r += ',';
			}
			r += '}';
			if (s < value.length - 1)
				r += ',';
		}
		r += '}';
		return r;
	}
}