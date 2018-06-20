package br.uefs.ecomp.ia.maze_robots;

import java.util.Arrays;
import br.uefs.ecomp.ia.maze_robots.core.Representation;

public class Robot extends Representation<Integer[][]> {

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
	public static final int COUNT_INPUTS = 0b11111111;

	/* OUTPUTS = [00-11]
	 * 00 - Subir
	 * 01 - Direita
	 * 10 - Descer
	 * 11 - Esquerda
	 */
	public static final int COUNT_OUTPUTS = 0b11;
	private static final int OUTPUT_BITS = 2;

	@Override
	public String toString() {
		String r = "Robot: id=" + id + ", fitness=" + fitness + ", value={";

		String output;
		for (int x = 0; x < value.length; x++) {
			r += '{';
			for (int y = 0; y < value[x].length; y++) {
				output = Integer.toBinaryString(value[x][y]);
				while (output.length() < (OUTPUT_BITS + value[x].length))
					output = '0' + output;
				r += output;
				if (y < value[x].length - 1)
					r += ',';
			}
			r += '}';
			if (x < value.length - 1)
				r += ',';
		}
		r += '}';
		return r;
	}

	@Override
	protected Robot clone() {
		Robot r = new Robot();
		r.value = Arrays.copyOf(this.value, this.value.length);
		return r;
	}
}