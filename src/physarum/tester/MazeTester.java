/**
 *    MazeTester.java
 *    
 *    @author Torsten Schoen
 *    
 *    @date 29. December 2013
 */
package physarum.tester;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import physarum.code.PhysarumConnection;
import physarum.code.PhysarumNode;
import physarum.code.PhysarumSolver;
import physarum.code.PhysarumNode.PhysarumNodeType;

/***
 * A class to test the Physarum Solver on different mazes
 * 
 * @author Torsten
 * 
 */
public class MazeTester {

	/** A list holding the PhysarumConnections between the PhysarumNodes */
	protected ArrayList<PhysarumConnection> myConnections = new ArrayList<PhysarumConnection>();

	/** A list holding the PhysarumNodes */
	protected ArrayList<PhysarumNode> myNodes = new ArrayList<PhysarumNode>();

	/** Initial conductivity minimum */
	protected double myConductivityMinimum = 0.5;

	/** Initial conductivity maximum */
	protected double myConductivityMaximum = 1.0;

	/** Random instance to generate random numbers */
	protected Random myRandom = new Random();

	public String solveTestMaze(int maze_in) {
		// switch by the maze id
		switch (maze_in) {
		case 0:
			// a simple self defined maze
			initMyMaze();
			break;
		case 1:
			// the maze defined in the Tero paper
			initTeroPaperMaze();
			break;
		case 2:
			// a simple T-shaped maze
			initTShape();
			break;
		case 3:
			// a ring shaped maze
			initRingShape();
			// Not working, fluxes are calculated differently in ring-shape
			break;
		case 4:
			// a ring shaped maze
			initASimpleMaze();
			break;
		}

		try {
			PhysarumSolver solver = new PhysarumSolver(myNodes, myConnections);
			solver.solve();
			return solver.getResultString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "Error";
	}

	/**
	 * Method to test the PhysarumSolver with different predefined mazes
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		MazeTester tester = new MazeTester();
		String result = tester.solveTestMaze(1);

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(
					"PhysarumSolver.log"));
			writer.write(result);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println(result);
	}

	/**
	 * Initialize a simple maze defined by me
	 */
	private void initMyMaze() {
		PhysarumNode node0 = new PhysarumNode(0, PhysarumNodeType.SOURCE);
		PhysarumNode node1 = new PhysarumNode(1, PhysarumNodeType.SINK);
		node1.setPressure(0);
		PhysarumNode node2 = new PhysarumNode(2);
		PhysarumNode node3 = new PhysarumNode(3);
		PhysarumNode node4 = new PhysarumNode(4);
		PhysarumNode node5 = new PhysarumNode(5);

		myNodes.add(node0);
		myNodes.add(node1);
		myNodes.add(node2);
		myNodes.add(node3);
		myNodes.add(node4);
		myNodes.add(node5);

		PhysarumConnection c1 = new PhysarumConnection(node0, node2);
		c1.setLength_L(1.0);
		c1.setConductivity_D(0.8);

		PhysarumConnection c2 = new PhysarumConnection(node2, node3);
		c2.setLength_L(3.0);
		c2.setConductivity_D(0.9);

		PhysarumConnection c3 = new PhysarumConnection(node2, node4);
		c3.setLength_L(7.0);
		c3.setConductivity_D(0.5);

		PhysarumConnection c4 = new PhysarumConnection(node3, node5);
		c4.setLength_L(3.0);
		c4.setConductivity_D(0.8);

		PhysarumConnection c5 = new PhysarumConnection(node4, node5);
		c5.setLength_L(7.0);
		c5.setConductivity_D(0.6);

		PhysarumConnection c6 = new PhysarumConnection(node5, node1);
		c6.setLength_L(1.0);
		c6.setConductivity_D(0.9);

		myConnections.add(c1);
		myConnections.add(c2);
		myConnections.add(c3);
		myConnections.add(c4);
		myConnections.add(c5);
		myConnections.add(c6);
	}

	/**
	 * A very simple self defined maze
	 */
	private void initASimpleMaze() {
		PhysarumNode node0 = new PhysarumNode(0, PhysarumNodeType.SOURCE);
		PhysarumNode node1 = new PhysarumNode(1, PhysarumNodeType.SINK);
		node1.setPressure(0);
		PhysarumNode node2 = new PhysarumNode(2);
		PhysarumNode node3 = new PhysarumNode(3);

		myNodes.add(node0);
		myNodes.add(node1);
		myNodes.add(node2);
		myNodes.add(node3);

		PhysarumConnection c1 = new PhysarumConnection(node0, node2);
		c1.setLength_L(1.0);
		c1.setConductivity_D(0.8);

		PhysarumConnection c2 = new PhysarumConnection(node2, node3);
		c2.setLength_L(2.0);
		c2.setConductivity_D(0.8);

		PhysarumConnection c3 = new PhysarumConnection(node3, node1);
		c3.setLength_L(1.0);
		c3.setConductivity_D(0.8);

		myConnections.add(c1);
		myConnections.add(c2);
		myConnections.add(c3);
	}

	/**
	 * Initialize the maze presented in the Tero paper
	 */
	protected void initTeroPaperMaze() {
		// Create nodes
		for (int i = 0; i < 23; i++) {
			PhysarumNode node;

			if (i == 0) {
				node = new PhysarumNode(i, PhysarumNodeType.SOURCE);
			} else if (i == 1) {
				node = new PhysarumNode(i, PhysarumNodeType.SINK);
				node.setPressure(0);
			} else {
				node = new PhysarumNode(i);
			}

			myNodes.add(node);
		}

		PhysarumConnection c1 = new PhysarumConnection(myNodes.get(0),
				myNodes.get(2));
		c1.setLength_L(1.5);
		c1.setConductivity_D(getRand(myConductivityMinimum,
				myConductivityMaximum));
		myConnections.add(c1);

		PhysarumConnection c2 = new PhysarumConnection(myNodes.get(2),
				myNodes.get(3));
		c2.setLength_L(0.1);
		c2.setConductivity_D(getRand(myConductivityMinimum,
				myConductivityMaximum));
		myConnections.add(c2);

		PhysarumConnection c3 = new PhysarumConnection(myNodes.get(2),
				myNodes.get(4));
		c3.setLength_L(4.0);
		c3.setConductivity_D(getRand(myConductivityMinimum,
				myConductivityMaximum));
		myConnections.add(c3);

		PhysarumConnection c4 = new PhysarumConnection(myNodes.get(3),
				myNodes.get(4));
		c4.setLength_L(3.3);
		c4.setConductivity_D(getRand(myConductivityMinimum,
				myConductivityMaximum));
		myConnections.add(c4);

		PhysarumConnection c5 = new PhysarumConnection(myNodes.get(3),
				myNodes.get(22));
		c5.setLength_L(1.6);
		c5.setConductivity_D(getRand(myConductivityMinimum,
				myConductivityMaximum));
		myConnections.add(c5);

		PhysarumConnection c6 = new PhysarumConnection(myNodes.get(4),
				myNodes.get(5));
		c6.setLength_L(0.1);
		c6.setConductivity_D(getRand(myConductivityMinimum,
				myConductivityMaximum));
		myConnections.add(c6);

		PhysarumConnection c7 = new PhysarumConnection(myNodes.get(5),
				myNodes.get(6));
		c7.setLength_L(0.1);
		c7.setConductivity_D(getRand(myConductivityMinimum,
				myConductivityMaximum));
		myConnections.add(c7);

		PhysarumConnection c8 = new PhysarumConnection(myNodes.get(5),
				myNodes.get(7));
		c8.setLength_L(0.7);
		c8.setConductivity_D(getRand(myConductivityMinimum,
				myConductivityMaximum));
		myConnections.add(c8);

		PhysarumConnection c9 = new PhysarumConnection(myNodes.get(7),
				myNodes.get(8));
		c9.setLength_L(5.0);
		c9.setConductivity_D(getRand(myConductivityMinimum,
				myConductivityMaximum));
		myConnections.add(c9);

		PhysarumConnection c10 = new PhysarumConnection(myNodes.get(8),
				myNodes.get(9));
		c10.setLength_L(0.6);
		c10.setConductivity_D(getRand(myConductivityMinimum,
				myConductivityMaximum));
		myConnections.add(c10);

		PhysarumConnection c11 = new PhysarumConnection(myNodes.get(8),
				myNodes.get(10));
		c11.setLength_L(0.1);
		c11.setConductivity_D(getRand(myConductivityMinimum,
				myConductivityMaximum));
		myConnections.add(c11);

		PhysarumConnection c12 = new PhysarumConnection(myNodes.get(10),
				myNodes.get(11));
		c12.setLength_L(0.1);
		c12.setConductivity_D(getRand(myConductivityMinimum,
				myConductivityMaximum));
		myConnections.add(c12);

		PhysarumConnection c13 = new PhysarumConnection(myNodes.get(10),
				myNodes.get(12));
		c13.setLength_L(0.2);
		c13.setConductivity_D(getRand(myConductivityMinimum,
				myConductivityMaximum));
		myConnections.add(c13);

		PhysarumConnection c14 = new PhysarumConnection(myNodes.get(12),
				myNodes.get(13));
		c14.setLength_L(0.1);
		c14.setConductivity_D(getRand(myConductivityMinimum,
				myConductivityMaximum));
		myConnections.add(c14);

		PhysarumConnection c15 = new PhysarumConnection(myNodes.get(12),
				myNodes.get(14));
		c15.setLength_L(1.0);
		c15.setConductivity_D(getRand(myConductivityMinimum,
				myConductivityMaximum));
		myConnections.add(c15);

		PhysarumConnection c16 = new PhysarumConnection(myNodes.get(7),
				myNodes.get(19));
		c16.setLength_L(3.5);
		c16.setConductivity_D(getRand(myConductivityMinimum,
				myConductivityMaximum));
		myConnections.add(c16);

		PhysarumConnection c17 = new PhysarumConnection(myNodes.get(6),
				myNodes.get(15));
		c17.setLength_L(1.1);
		c17.setConductivity_D(getRand(myConductivityMinimum,
				myConductivityMaximum));
		myConnections.add(c17);

		PhysarumConnection c19 = new PhysarumConnection(myNodes.get(6),
				myNodes.get(16));
		c19.setLength_L(2.7);
		c19.setConductivity_D(getRand(myConductivityMinimum,
				myConductivityMaximum));
		myConnections.add(c19);

		PhysarumConnection c18 = new PhysarumConnection(myNodes.get(16),
				myNodes.get(17));
		c18.setLength_L(0.2);
		c18.setConductivity_D(getRand(myConductivityMinimum,
				myConductivityMaximum));
		myConnections.add(c18);

		PhysarumConnection c20 = new PhysarumConnection(myNodes.get(17),
				myNodes.get(18));
		c20.setLength_L(0.2);
		c20.setConductivity_D(getRand(myConductivityMinimum,
				myConductivityMaximum));
		myConnections.add(c20);

		PhysarumConnection c21 = new PhysarumConnection(myNodes.get(17),
				myNodes.get(19));
		c21.setLength_L(0.2);
		c21.setConductivity_D(getRand(myConductivityMinimum,
				myConductivityMaximum));
		myConnections.add(c21);

		PhysarumConnection c22 = new PhysarumConnection(myNodes.get(19),
				myNodes.get(20));
		c22.setLength_L(0.2);
		c22.setConductivity_D(getRand(myConductivityMinimum,
				myConductivityMaximum));
		myConnections.add(c22);

		PhysarumConnection c23 = new PhysarumConnection(myNodes.get(16),
				myNodes.get(21));
		c23.setLength_L(0.7);
		c23.setConductivity_D(getRand(myConductivityMinimum,
				myConductivityMaximum));
		myConnections.add(c23);

		PhysarumConnection c24 = new PhysarumConnection(myNodes.get(20),
				myNodes.get(1));
		c24.setLength_L(0.7);
		c24.setConductivity_D(getRand(myConductivityMinimum,
				myConductivityMaximum));
		myConnections.add(c24);
	}

	/**
	 * Init a simple T shaped maze as in the Tero paper
	 */
	private void initTShape() {
		PhysarumNode node1 = new PhysarumNode(1, PhysarumNodeType.SOURCE);
		PhysarumNode node2 = new PhysarumNode(2, PhysarumNodeType.SINK);
		node2.setPressure(0);
		PhysarumNode node3 = new PhysarumNode(3);
		PhysarumNode node4 = new PhysarumNode(4);

		myNodes.add(node1);
		myNodes.add(node2);
		myNodes.add(node3);
		myNodes.add(node4);

		PhysarumConnection c1 = new PhysarumConnection(node1, node3);
		c1.setLength_L(1.5);
		c1.setConductivity_D(getRand(myConductivityMinimum,
				myConductivityMaximum));

		PhysarumConnection c2 = new PhysarumConnection(node3, node4);
		c2.setLength_L(1.5);
		c2.setConductivity_D(getRand(myConductivityMinimum,
				myConductivityMaximum));

		PhysarumConnection c3 = new PhysarumConnection(node3, node2);
		c3.setLength_L(1.5);
		c3.setConductivity_D(getRand(myConductivityMinimum,
				myConductivityMaximum));

		myConnections.add(c1);
		myConnections.add(c2);
		myConnections.add(c3);
	}

	/**
	 * Init a simple Ring shaped maze as in the Tero paper
	 */
	private void initRingShape() {
		PhysarumNode node1 = new PhysarumNode(1, PhysarumNodeType.SOURCE);
		PhysarumNode node2 = new PhysarumNode(2, PhysarumNodeType.SINK);
		node2.setPressure(0);

		PhysarumNode node3 = new PhysarumNode(3);
		PhysarumNode node4 = new PhysarumNode(4);

		myNodes.add(node1);
		myNodes.add(node2);
		myNodes.add(node3);
		myNodes.add(node4);

		PhysarumConnection c1 = new PhysarumConnection(node1, node3);
		c1.setLength_L(1.0);
		c1.setConductivity_D(getRand(myConductivityMinimum,
				myConductivityMaximum));

		PhysarumConnection c2 = new PhysarumConnection(node4, node2);
		c2.setLength_L(1.0);
		c2.setConductivity_D(getRand(myConductivityMinimum,
				myConductivityMaximum));

		PhysarumConnection c3 = new PhysarumConnection(node3, node4);
		c3.setLength_L(13.0);
		c3.setConductivity_D(getRand(myConductivityMinimum,
				myConductivityMaximum));

		PhysarumConnection c4 = new PhysarumConnection(node3, node4);
		c4.setLength_L(42.0);
		c4.setConductivity_D(getRand(myConductivityMinimum,
				myConductivityMaximum));

		myConnections.add(c1);
		myConnections.add(c2);
		myConnections.add(c3);
		myConnections.add(c4);
	}

	/**
	 * Generates and returns a random value in the range between Min and Max
	 * 
	 * @param min_in
	 *            lower bound
	 * @param max_in
	 *            ipper bound
	 * @return the random number
	 */
	protected double getRand(double min_in, double max_in) {
		return (min_in + (double) (this.myRandom.nextDouble() % (max_in - min_in)));
	}
}
