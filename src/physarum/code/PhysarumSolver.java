/**
 *    PhysarumSolver.java
 *    
 *    @author Torsten Schoen
 *    
 *    @date 29. December 2013
 */
package physarum.code;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;

/**
 * The Physarum solver can find the shortest path between a source and a sink.
 * The solver needs to get a list of nodes and a list of connections between
 * these nodes It is implemented with respect to the TeroKobayashi2006 Paper
 * 
 * @author Torsten Schoen
 * 
 */
public class PhysarumSolver {
	/** A list holding the PhysarumConnections between the PhysarumNodes */
	protected ArrayList<PhysarumConnection> myConnections = new ArrayList<PhysarumConnection>();

	/** A list holding the PhysarumNodes */
	protected ArrayList<PhysarumNode> myNodes = new ArrayList<PhysarumNode>();

	/** Random instance to generate random numbers */
	protected Random myRandom = new Random();

	/**
	 * Greek mue, The flux is powered by mue to calculate the change in the
	 * conductivity D_new = Q^mue;
	 */
	protected double myMue = 1.2;

	/** The maximum iterations that the physarum solver is allowed to run */
	protected int myMaximumIterations = 50;

	/**
	 * Minimum conductivity a connection must have to be added to the survived
	 * connections
	 */
	protected double mySurvivalThreshold = 0.001;

	/** Initial conductivity minimum */
	protected double myConductivityMinimum = 0.5;

	/** Initial conductivity maximum */
	protected double myConductivityMaximum = 1.0;

	/**
	 * If a connection changes its conductivity no more than this threshold, it
	 * is treated as unchanged
	 */
	protected double myDeltaConductivityThreshold = 0.00001;

	/** String buffer holding the output string */
	protected StringBuffer myOutput = new StringBuffer();

	/** Turn logging on and off */
	protected boolean myLoggingEnabled = true;

	protected boolean firstRun = true;

	protected double I0 = 1.0;

	protected PhysarumConnection[][] connectionIndexTable;

	/**
	 * The constructor called with nodes and connections
	 * 
	 * @param nodes_in
	 *            A list of PhysarumNodes
	 * @param connections_in
	 *            A list of PhysarumConnections connection the PhysarumNodes of
	 *            nodes_in
	 */
	public PhysarumSolver(ArrayList<PhysarumNode> nodes_in,
			ArrayList<PhysarumConnection> connections_in) {
		// get nodes and connections
		this.myNodes = nodes_in;
		this.myConnections = connections_in;

		this.initConnectionIndexTable();

		this.logNodes();
		this.logConnections();
	}

	/**
	 * 2D Array holding the indexes of the connections to be easily available
	 */
	protected void initConnectionIndexTable() {
		this.connectionIndexTable = new PhysarumConnection[this.myNodes.size() + 1][this.myNodes
				.size() + 1];

		for (int i = 0; i < this.connectionIndexTable.length; i++) {
			for (int j = 0; j < this.connectionIndexTable[i].length; j++) {
				this.connectionIndexTable[i][j] = null;
			}
		}

		for (PhysarumConnection con : this.myConnections) {

			this.connectionIndexTable[this.myNodes.indexOf(con.getStartNode())][this.myNodes
					.indexOf(con.getEndNode())] = con;
			this.connectionIndexTable[this.myNodes.indexOf(con.getEndNode())][this.myNodes
					.indexOf(con.getStartNode())] = con;
		}
	}

	/**
	 * This methods runs the PhysarumSolver logic. It finds the shortest
	 * connection out of myConnections between a defined source and sink node
	 * 
	 * @throws Exception
	 */
	public void solve() throws Exception {
		// run maximal myMaximumIterations iterations
		for (int i = 0; i < this.myMaximumIterations; i++) {
			// create log string
			this.log("---------------------------------------------------\n");
			this.log("          iteration " + i + "\n");
			this.log("---------------------------------------------------\n");

			// build the lefthand side of the equation system holding the
			// different Flux definitions for the pressures
			// 0.7p1 + 0.1p2
			double[][] lefthandSide = this
					.buildLinearEquationSystemLefthandSide();

			// build the righthand side of the equation system holding the flux
			// sum
			// -1 if it is a source node
			// +1 of it is a sink node
			// 0 otherwise
			double[][] righthandSide = this
					.buildLinearEquationSystemRighthandSide();

			// create matrixes out of the 2D arrays
			RealMatrix m = new Array2DRowRealMatrix(lefthandSide);
			RealMatrix n = new Array2DRowRealMatrix(righthandSide);

			DecompositionSolver solver = new SingularValueDecomposition(m)
					.getSolver();

			RealMatrix c = solver.solve(n);

			try {
				RealMatrix pressures = MatrixUtils
						.createRealMatrix(c.getData());

				// set updated pressure values
				this.updatePressureForNodes(pressures.getData());

				// set updated conductivity values
				this.updateConductivities(this.myMue);

				// generate log output
				this.print2DArray(lefthandSide, "eq");
				this.log("\n");
				this.print2DArray(righthandSide, "eq");
				this.log("\n");
				this.print2DArray(pressures.getData(), "p");
				this.log("\n");
				this.logFlux();
				this.log("---------------------------------------------------\n");

				// counting how many connections haven't changed their
				// conductivity more that myDeltaConductivityThreshold
				int nrUnchangedConnections = 0;

				// count how many connections didn't change their conductivity
				// more than myDeltaConductivityThreshold in this iteration
				for (PhysarumConnection con : this.myConnections) {
					if (Math.abs(con.getConductivityChange()) < this.myDeltaConductivityThreshold) {
						nrUnchangedConnections++;
					}
				}

				// if no connection changed within this iteration, the physarum
				// solver converged and is stopped
				if (nrUnchangedConnections >= myConnections.size()) {
					this.log("PhysarumSolver stopped at iteration " + i + "\n");
					// stop solver
					break;
				}
			} catch (Exception e) {
				// log the exception message
				this.log("<><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><>\n");
				this.log(e.getMessage() + "\n");
				this.print2DArray(lefthandSide, "eq");
				this.log("\n");
				this.print2DArray(righthandSide, "eq");
				this.log("\n");
				this.log("<><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><>\n");
				throw e;
			}
		}
	}

	/**
	 * Log a 2D array
	 * 
	 * @param array_in
	 *            the array to be logged
	 * @param name_in
	 *            the name of each single line, it is printed as:
	 *            name+liennumber = []
	 */
	public void print2DArray(double[][] array_in, String name_in) {
		if (this.myLoggingEnabled) {
			for (int i = 0; i < array_in.length; i++) {
				this.myOutput.append(name_in + i + " = "
						+ Arrays.toString(array_in[i]) + "\n");
			}
		}
	}

	/**
	 * Returns the connections that survived after the PhysarumSolver has
	 * terminated e.g. The connections which's conductivity is greater than the
	 * survival threshold
	 * 
	 * @return ArrayList<PhysarumConnection> survived connections
	 */
	public ArrayList<PhysarumConnection> getSurvivedConnections() {
		// init an array list
		ArrayList<PhysarumConnection> survived = new ArrayList<PhysarumConnection>();

		for (PhysarumConnection con : this.myConnections) {
			// add connections where the conductivity is greater than the
			// survival threshold
			if (con.getConductivity_D() > this.mySurvivalThreshold) {
				survived.add(con);
			}
		}

		return survived;
	}

	/**
	 * Updates the conductivities of the nodes, based on the mue value given
	 * 
	 * @param mue_in
	 *            The mue of Q^mue
	 */
	protected void updateConductivities(double mue_in) {
		// for each connection
		for (PhysarumConnection con : this.myConnections) {
			// update the flux conductivity based on the new pressures
			con.updateFluxAndConductivity(mue_in);
		}
	}

	/**
	 * Set the calculated pressures to the nodes
	 * 
	 * @param calculatedPressures_in
	 *            the new pressure values
	 */
	protected void updatePressureForNodes(double[][] calculatedPressures_in) {
		// the position in the array is equal to the position in the node list
		for (int i = 0; i < calculatedPressures_in.length; i++) {
			// set new pressure value
			myNodes.get(i).setPressure(calculatedPressures_in[i][0]);

		}
	}

	/**
	 * Creates the right hand side of the linear equation system e.g. the
	 * results of the node equations: -1 if the node is a source +1 if the node
	 * is a sink 0 otherwise
	 * 
	 * @return the 2d matrix of the result values (which is only an array)
	 */
	protected double[][] buildLinearEquationSystemRighthandSide() {
		// init empty matrix of size myNodes.size()+1 and 1
		// myNodes.size()+1 => one equation for each node + one equation to set
		// sink pressure to 0
		double[][] matrix = new double[myNodes.size()][1];

		// for each node
		for (int i = 0; i < myNodes.size(); i++) {
			PhysarumNode node = myNodes.get(i);
			// if the node is a source, the result is -1
			if (node.isSource()) {
				double[] tmp = { this.I0 };
				matrix[i] = tmp;
			}
			// if the node is a sink, the result is +1
			else if (node.isSink()) {
				double[] tmp = { (-1) * this.I0 };
				matrix[i] = tmp;
			}
			// else, the result is 0
			else {
				double[] tmp = { 0 };
				matrix[i] = tmp;
			}
		}

		return matrix;
	}

	/**
	 * Creates the lefthand side of the linear equation system e.g. sums all Q
	 * for all connections and adds the pressures For Example: (3) --> (4) -->
	 * (5), for node (4): Q34 - Q45 == D34/L34 * p3 - D34/L34 * p4 - D45/L45 *
	 * p4 + D45/L45 *p5 Add all incoming nodes and subtract all outgoing nodes
	 * 
	 * @return
	 */
	protected double[][] buildLinearEquationSystemLefthandSide() {
		// build a matrix:
		// in each row: each column is for one nodes pressure value
		// the 2d array has one more row, for constants
		double[][] matrix = new double[myNodes.size()][myNodes.size()];

		// iterate through nodes and nodes
		for (int j = 0; j < myNodes.size(); j++) {
			// list of pressures plus 1 entry for the constant I0
			double[] pressures = new double[myNodes.size()];

			// init pressures with 0
			for (int k = 0; k < pressures.length; k++) {
				pressures[k] = 0.0;
			}

			for (int i = 0; i < myNodes.size(); i++) {
				double dlf = this.getDLFraction(myNodes.get(i), myNodes.get(j));
				pressures[i] += dlf;
				pressures[j] -= dlf;
			}

			for (int i = 0; i < myNodes.size(); i++) {
				if (myNodes.get(i).isSink() && firstRun) {
					pressures[i] = 0.0;
				}
			}

			// add the pressure array to the result matrix
			matrix[j] = pressures;
		}

		firstRun = false;
		return matrix;
	}

	/**
	 * Get the DLFraction between the nodes i and j, if there is no connection,
	 * return 0.0
	 * 
	 * @param i
	 *            node 1
	 * @param j
	 *            node 2
	 * @return the DLFraction of i and j or 0
	 */
	protected double getDLFraction(PhysarumNode i, PhysarumNode j) {
		// if the nodes are the same, the dlfraction is 0
		if (i == j) {
			return 0.0;
		}

		// if the is no connection, the fraction is 0
		double fraction = 0.0;

		PhysarumConnection con = this.connectionIndexTable[myNodes.indexOf(i)][myNodes
				.indexOf(j)];

		if (con != null) {
			fraction = con.getDLFraction();
		}

		return fraction;
	}

	// ============================================================================================
	// Getters and setters
	// ============================================================================================

	/**
	 * Get output string
	 * 
	 * @return myOutput
	 */
	public String getResultString() {
		return this.myOutput.toString();
	}

	/**
	 * Get ConductivityMinimum
	 * 
	 * @return myConductivityMinimum
	 */
	public double getConductivityMinimum() {
		return this.myConductivityMinimum;
	}

	/**
	 * Set ConductivityMinimum
	 * 
	 * @param new ConductivityMinimum
	 */
	public void setConductivityMinimum(double dMin) {
		this.myConductivityMinimum = dMin;
	}

	/**
	 * Get ConductivityMaximum
	 * 
	 * @return myConductivityMaximum
	 */
	public double getConductivityMaximum() {
		return this.myConductivityMaximum;
	}

	/**
	 * Set ConductivityMaximum
	 * 
	 * @param dMax
	 */
	public void setConductivityMaximum(double dMax) {
		this.myConductivityMaximum = dMax;
	}

	/**
	 * Get mue
	 * 
	 * @return myMue
	 */
	public double getMue() {
		return this.myMue;
	}

	/**
	 * Get Nr of maximum PhysarumSolver iterations
	 * 
	 * @return myConductivityMinimum
	 */
	public int getMaximumIterations() {
		return this.myMaximumIterations;
	}

	/**
	 * Get SurvivalThreshold
	 * 
	 * @return mySurvivalThreshold
	 */
	public double getSurvivalThreshold() {
		return mySurvivalThreshold;
	}

	/**
	 * Set mue value
	 * 
	 * @param mue
	 */
	public void setMue(double mue) {
		this.myMue = mue;
	}

	/**
	 * Set maximum iterations
	 * 
	 * @param iterations
	 */
	public void setMaximumIterations(int iterations) {
		this.myMaximumIterations = iterations;
	}

	/**
	 * Set survival threshold
	 * 
	 * @param threshold
	 */
	public void setSurvivalThreshold(double threshold) {
		this.mySurvivalThreshold = threshold;
	}

	/**
	 * Set if logging should be enabled or not
	 * 
	 * @param enabled
	 */
	public void enableLogging(boolean enabled) {
		this.myLoggingEnabled = enabled;
	}

	/**
	 * Get SurvivalThreshold
	 * 
	 * @return I0
	 */
	public double getI0() {
		return I0;
	}

	/**
	 * Set i0
	 * 
	 * @param i0
	 */
	public void setI0(double i0) {
		I0 = i0;
	}

	// ============================================================================================
	// Log methods
	// ============================================================================================

	/**
	 * Logs a message
	 * 
	 * @param message
	 *            The message to be logged
	 */
	protected void log(String message) {
		if (myLoggingEnabled) {
			myOutput.append(message);
		}
	}

	/**
	 * builds a log output of myConnections
	 */
	protected void logConnections() {
		this.log("<======================================================================>\n");
		this.log("Connections:\n");

		for (PhysarumConnection con : this.myConnections) {
			this.log(con.getDetailedDescription() + "\n");
		}

		this.log("<======================================================================>\n");
	}

	/**
	 * Builds a log output of myNodes
	 */
	protected void logNodes() {
		this.log("<======================================================================>\n");
		this.log("Nodes:\n");

		for (PhysarumNode node : this.myNodes) {
			this.log(node.getDescription() + "\n");
		}

		this.log("<======================================================================>\n");
	}

	/**
	 * Log the new Flux values
	 */
	protected void logFlux() {
		// define the format
		DecimalFormat f = new DecimalFormat("#0.00000");

		for (PhysarumConnection con : this.myConnections) {
			this.log(con.getDescription() + ", Q = "
					+ f.format(con.getFlux_Q()) + "\tD = "
					+ f.format(con.getConductivity_D()) + "\tdeltaD = "
					+ f.format(con.getConductivityChange()) + "\tL = "
					+ con.getLength_L() + "\n");
		}
	}
}
