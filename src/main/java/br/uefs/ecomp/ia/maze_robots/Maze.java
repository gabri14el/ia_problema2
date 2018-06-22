package br.uefs.ecomp.ia.maze_robots;

public class Maze {

	public static final char EMPTY = ' ';
	public static final char WALL = '#';
	public static final char START = 'I';
	public static final char END = 'F';

	public static Maze[] mazes = new Maze[] {

			new Maze(new char[][] {
					/*
					###########
					#I       F#
					###########
					*/
					{ '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#' },
					{ '#', 'I', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'F', '#' },
					{ '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#' }
			}),
			new Maze(new char[][] {
					/*
					###########
					#I        #
					#         #
					#        F#
					###########
					*/
					{ '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#' },
					{ '#', 'I', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', '#' },
					{ '#', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', '#' },
					{ '#', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'F', '#' },
					{ '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#' }
			}),
			new Maze(new char[][] {
					/*
					###########
					#I        #
					#######   #
					      #   #
					      #   #
					      ##F##
					      #####
					*/
					{ '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#' },
					{ '#', 'I', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', '#' },
					{ '#', '#', '#', '#', '#', '#', '#', ' ', ' ', ' ', '#' },
					{ ' ', ' ', ' ', ' ', ' ', ' ', '#', ' ', ' ', ' ', '#' },
					{ ' ', ' ', ' ', ' ', ' ', ' ', '#', ' ', ' ', ' ', '#' },
					{ ' ', ' ', ' ', ' ', ' ', ' ', '#', '#', 'F', '#', '#' },
					{ ' ', ' ', ' ', ' ', ' ', ' ', '#', '#', '#', '#', '#' }
			}),
			new Maze(new char[][] {
					/*
					###########
					#F        #
					####   ####
					   #   #
					   #   #
					   ##I##
					   #####
					*/
					{ '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#' },
					{ '#', 'F', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', '#' },
					{ '#', '#', '#', '#', ' ', ' ', ' ', '#', '#', '#', '#' },
					{ ' ', ' ', ' ', '#', ' ', ' ', ' ', '#', ' ', ' ', ' ' },
					{ ' ', ' ', ' ', '#', ' ', ' ', ' ', '#', ' ', ' ', ' ' },
					{ ' ', ' ', ' ', '#', '#', 'I', '#', '#', ' ', ' ', ' ' },
					{ ' ', ' ', ' ', '#', '#', '#', '#', '#', ' ', ' ', ' ' }
			}),
			new Maze(new char[][] {
					/*
					###########
					#I        #
					####   ####
					#        F#
					###########
					*/
					{ '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#' },
					{ '#', 'I', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', '#' },
					{ '#', '#', '#', '#', ' ', ' ', ' ', '#', '#', '#', '#' },
					{ '#', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'F', '#' },
					{ '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#' }
			}),
			new Maze(new char[][] {
					/*
					##############
					#I           #
					# ####  #### #
					#            #
					# ## #### ## #
					# ##      ## #
					# ## #### ## #
					#            #
					# ####  #### #
					#           F#
					##############
					*/
					{ '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#' },
					{ '#', 'I', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', '#' },
					{ '#', ' ', '#', '#', '#', '#', ' ', ' ', '#', '#', '#', '#', ' ', '#' },
					{ '#', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', '#' },
					{ '#', ' ', '#', '#', ' ', '#', '#', '#', '#', ' ', '#', '#', ' ', '#' },
					{ '#', ' ', '#', '#', ' ', ' ', ' ', ' ', ' ', ' ', '#', '#', ' ', '#' },
					{ '#', ' ', '#', '#', ' ', '#', '#', '#', '#', ' ', '#', '#', ' ', '#' },
					{ '#', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', '#' },
					{ '#', ' ', '#', '#', '#', '#', ' ', ' ', '#', '#', '#', '#', ' ', '#' },
					{ '#', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'F', '#' },
					{ '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#' }
			})
	};

	public static Maze get(int index) {
		return mazes[index];
	}

	private final char maze[][];
	private int sY;
	private int sX;
	private int eY;
	private int eX;

	private Maze(char[][] maze) {
		this.maze = maze;
		sY = -1;
		sX = -1;
		eY = -1;
		eX = -1;

		for (int y = 0; y < getYLength(); y++) {
			for (int x = 0; x < getXLength(); x++) {
				if (maze[y][x] != EMPTY && maze[y][x] != WALL && maze[y][x] != START && maze[y][x] != END)
					throw new RuntimeException("Labirinto com posicao inválida: " + maze[y][x]);

				if (maze[y][x] == START) {
					sY = y;
					sX = x;
				}

				if (maze[y][x] == END) {
					eY = y;
					eX = x;
				}
			}
		}

		if (sY == -1)
			throw new RuntimeException("Labirinto sem inicio");
		if (eY == -1)
			throw new RuntimeException("Labirinto sem fim");
	}

	public char[][] getMaze() {
		return maze;
	}

	public int getYLength() {
		return maze.length;
	}

	public int getXLength() {
		return maze[0].length;
	}

	public int getSY() {
		return sY;
	}

	public int getSX() {
		return sX;
	}

	public int getEY() {
		return eY;
	}

	public int getEX() {
		return eX;
	}

	public char get(int y, int x) {
		return maze[y][x];
	}

	public boolean isEmpty(int y, int x) {
		return maze[y][x] == 'I' || maze[y][x] == ' ';
	}

	public boolean isWall(int y, int x) {
		return maze[y][x] == '#';
	}

	public boolean isEnd(int y, int x) {
		return maze[y][x] == 'F';
	}
}
