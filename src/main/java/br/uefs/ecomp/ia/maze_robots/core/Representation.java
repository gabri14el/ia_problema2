package br.uefs.ecomp.ia.maze_robots.core;

public abstract class Representation<R> implements Cloneable {

	private static int ID_GENERATION = 0;

	protected long id = ++ID_GENERATION;
	protected R value;
	protected Double fitness;
	protected Double normalizedFitness;
	protected Double fitnessProportional;
	protected Representation<R> parent;

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

	public Double getNormalizedFitness() {
		return normalizedFitness;
	}

	public void setNormalizedFitness(Double normalizedFitness) {
		this.normalizedFitness = normalizedFitness;
	}

	public Double getFitnessProportional() {
		return fitnessProportional;
	}

	public void setFitnessProportional(Double fitnessProportional) {
		this.fitnessProportional = fitnessProportional;
	}

	public R getValue() {
		return value;
	}

	public void setValue(R value) {
		this.value = value;
	}

	public Representation<R> getParent() {
		return parent;
	}

	public void setParent(Representation<R> parent) {
		this.parent = parent;
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
