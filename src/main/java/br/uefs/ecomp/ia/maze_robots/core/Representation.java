package br.uefs.ecomp.ia.maze_robots.core;

public abstract class Representation<R> implements Cloneable {

	public static int ID_GENERATION = 0;

	protected long id = ++ID_GENERATION;
	protected R value;
	protected Double fitness;

	public Representation() {
		fitness = null;
	}

	public long getId() {
		return id;
	}

	public Double getFitness() {
		return fitness;
	}

	public void setFitness(Double fitness) {
		this.fitness = fitness;
	}

	public R getValue() {
		return value;
	}

	public void setValue(R value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Representation<?> other = (Representation<?>) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	protected abstract Representation<R> clone();
}
