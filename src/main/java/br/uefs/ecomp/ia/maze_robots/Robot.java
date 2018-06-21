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
	 * 1º bit - Parede em cima
	 * 2º bit - Parede à direita
	 * 3º bit - Parede em baixo
	 * 4º bit - Parede à esquerda
	 * 
	 * 5º bit - Final ao norte
	 * 6º bit - Final ao leste
	 * 7º bit - Final ao sul
	 * 8º bit - Final ao oeste
	 */
	private static final int INPUT_SIZE = 0b11111111;

	/* OUTPUTS = [000-100]
	 * 000 - Nada
	 * 001 - Subir
	 * 010 - Direita
	 * 011 - Descer
	 * 100 - Esquerda
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

	public void forEach(int s, int i, BiConsumer<Integer, Integer> consumer) {
		for (; s < getStateSize(); s++)
			for (; i < INPUT_SIZE; i++)
				consumer.accept(s, i);
	}

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

	public void delState() {
		value = Arrays.copyOf(value, getStateSize() - 1);
	}

	public void addState() {
		value = Arrays.copyOf(value, getStateSize() + 1);
		value[value.length - 1] = new Integer[INPUT_SIZE][2];
		for (Integer[] v : value[value.length - 1]) {
			v[0] = -1;
			v[1] = -1;
		}
	}
}