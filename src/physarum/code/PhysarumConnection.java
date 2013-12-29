/**
 *    PhysarumConnection.java
 *    
 *    @author Torsten Schoen
 *    
 *    @date 29. December 2013
 */
package physarum.code;

/**
 * A Physarum connection between two PhysarumNodes, has a length, flux and
 * conductivity
 * 
 * @author Torsten
 * 
 */
public class PhysarumConnection implements Cloneable {
	public enum Q_Method {
		TypeOne, TypeTwo, TypeThree
	}

	/** The start node of the connection */
	private PhysarumNode myStartNode = null;

	/** The end node of the connection */
	private PhysarumNode myEndNode = null;

	/** The flux Q through the connection */
	private double myFlux_Q = 0.0;

	/** The length L of the connection */
	private double myLength_L = 0.0;

	/** The conductivity D of the connection */
	private double myConductivity_D = 0.0;

	/**
	 * The former conductivity of the connection, used to calculate the
	 * conductivity change for an iteration
	 */
	private double myFormerConductivity_D = 0.0;

	/** alpha */
	private double myAlpha = 1.0;

	private double myfQAlpha = 15.0;

	private double myWeigthAdaption = 1.0;

	private Q_Method myQMethod = Q_Method.TypeOne;

	/**
	 * Constructor
	 * 
	 * @param start
	 *            The start node
	 * @param end
	 *            The end node
	 */
	public PhysarumConnection(PhysarumNode start, PhysarumNode end) {
		this.myStartNode = start;
		this.myEndNode = end;
	}

	public double getMyAlpha() {
		return myAlpha;
	}

	public void setMyAlpha(double myAlpha) {
		this.myAlpha = myAlpha;
	}

	public double getMyfQAlpha() {
		return myfQAlpha;
	}

	public void setMyfQAlpha(double myfQAlpha) {
		this.myfQAlpha = myfQAlpha;
	}

	public Q_Method getMyQMethod() {
		return myQMethod;
	}

	public void setMyQMethod(Q_Method myQMethod) {
		this.myQMethod = myQMethod;
	}

	/**
	 * Get the start node
	 * 
	 * @return
	 */
	public PhysarumNode getStartNode() {
		return this.myStartNode;
	}

	/**
	 * Set the start node
	 * 
	 * @param startNode
	 */
	public void setStartNode(PhysarumNode startNode) {
		this.myStartNode = startNode;
	}

	/**
	 * Get the end node
	 * 
	 * @return
	 */
	public PhysarumNode getEndNode() {
		return this.myEndNode;
	}

	/**
	 * Set the end node
	 * 
	 * @param endNode
	 */
	public void setEndNode(PhysarumNode endNode) {
		this.myEndNode = endNode;
	}

	/**
	 * Get the flux
	 * 
	 * @return
	 */
	public double getFlux_Q() {
		return this.myFlux_Q;
	}

	/**
	 * Set the flux
	 * 
	 * @param flux_Q
	 */
	public void setFlux_Q(double flux_Q) {
		this.myFlux_Q = flux_Q;
	}

	/**
	 * Update flux, this has to be called when the pressure of either the start
	 * or end node changed
	 */
	private void updateFlux() {
		// calculate the new flux based on the pressures and the D/L fraction
		this.myFlux_Q = getDLFraction()
				* (this.myStartNode.getPressure() - this.myEndNode
						.getPressure());
	}

	/**
	 * Get the length
	 * 
	 * @return
	 */
	public double getLength_L() {
		return this.myLength_L;
	}

	/**
	 * Set the length
	 * 
	 * @param length_L
	 */
	public void setLength_L(double length_L) {
		this.myLength_L = length_L;
	}

	/**
	 * Get the conductivity
	 * 
	 * @return
	 */
	public double getConductivity_D() {
		return this.myConductivity_D;
	}

	/**
	 * Set the conductivity
	 * 
	 * @param conductivity_D
	 */
	public void setConductivity_D(double conductivity_D) {
		this.myConductivity_D = conductivity_D;
	}

	/**
	 * Get conductivity divided by length
	 * 
	 * @return
	 */
	public double getDLFraction() {
		return this.myConductivity_D / this.getLength_L();
	}

	/**
	 * Get a string with connection informations Example: Connection from 1 to 4
	 * 
	 * @return
	 */
	public String getDescription() {
		return "Connection from " + this.myStartNode.getId() + " to "
				+ this.myEndNode.getId();
	}

	/**
	 * Get a string with connection informations Example: Connection from 1 to 4
	 * 
	 * @return
	 */
	public String getDescriptionByName() {
		return "Connection from " + this.myStartNode.getName() + " to "
				+ this.myEndNode.getName();
	}

	/**
	 * Get a string with detailed connection information Example: Connection
	 * from 1 to 4 Q = 0.8 L = 200 D = 0.998
	 * 
	 * @return
	 */
	public String getDetailedDescription() {
		return this.getDescription() + "\tQ = " + myFlux_Q + "\tL = "
				+ this.getLength_L() + "\tD = " + myConductivity_D;
	}

	/**
	 * Updates the conductivity once the flux has been updated
	 * 
	 * @param mue
	 *            The mue value
	 */
	public void updateFluxAndConductivity(double mue) {
		// first, update the flux as the pressure values of the nodes might have
		// been changed
		this.updateFlux();

		// keep the actual conductivity
		this.myFormerConductivity_D = this.myConductivity_D;

		// update the conductivity:
		// deltaD = Q^mue - D
		double q = this.f_Q(mue);
		this.myConductivity_D += myWeigthAdaption
				* (q - (this.myAlpha * this.myConductivity_D));

	}

	private double f_Q(double mue) {
		if (this.myQMethod == Q_Method.TypeOne) {
			return Math.pow(Math.abs(this.myFlux_Q), mue);
		} else if (this.myQMethod == Q_Method.TypeTwo) {
			return ((1 + this.myfQAlpha) * Math.pow(Math.abs(this.myFlux_Q),
					mue))
					/ (1 + (this.myfQAlpha * Math.pow(Math.abs(this.myFlux_Q),
							mue)));
		} else {
			return (Math.pow(Math.abs(this.myFlux_Q), mue))
					/ (1 + Math.pow(Math.abs(this.myFlux_Q), mue));
		}
	}

	/**
	 * Get the change of the conductivity in the last update
	 * 
	 * @return
	 */
	public double getConductivityChange() {
		return this.myConductivity_D - this.myFormerConductivity_D;
	}

	public boolean scoreChangedThreshold(double conductivityParentThreshold) {
		// return true if D has changed and crossed the threshold
		return ((this.myFormerConductivity_D > conductivityParentThreshold && this.myConductivity_D < conductivityParentThreshold) || (this.myFormerConductivity_D < conductivityParentThreshold && this.myConductivity_D > conductivityParentThreshold));
	}
}
