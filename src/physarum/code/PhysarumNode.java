/**
 *    PhysarumNode.java
 *    
 *    @author Torsten Schoen
 *    
 *    @date 29. December 2013
 */
package physarum.code;

/**
 * A Physarum node, can be a normal node, a sink or a source and holds a
 * pressure value
 * 
 * @author Torsten
 * 
 */
public class PhysarumNode {
	/** The id of the node */
	private int myId;

	/** The name of the node */
	private String myName;

	/** The pressure of the node */
	private double myPressure = 0.0;

	/**
	 * The node's type Can either be NORMAL, SOURCE or SINK
	 */
	private PhysarumNodeType nyNodeType = PhysarumNodeType.NORMAL;

	/**
	 * Enum of different possible node types
	 * 
	 * @author Torsten
	 * 
	 */
	public enum PhysarumNodeType {
		SOURCE, NORMAL, SINK
	}

	/**
	 * Generates a new instance of type PhysarumNode with node type remains
	 * NORMAL
	 * 
	 * @param id_in
	 */
	public PhysarumNode(int id_in) {
		this.myId = id_in;
	}

	/**
	 * Generates a new instance of type PhysarumNode with node type nodeType
	 * 
	 * @param id_in
	 *            The id of the node
	 * @param nodeType
	 *            The type of the node
	 */
	public PhysarumNode(int id_in, PhysarumNodeType nodeType) {
		this.nyNodeType = nodeType;
		this.myId = id_in;
	}

	/** Set the pressure */
	public void setPressure(double p) {
		this.myPressure = p;
	}

	/**
	 * Get the pressure
	 * 
	 * @return pressure
	 */
	public double getPressure() {
		return this.myPressure;
	}

	/**
	 * Get the node id
	 * 
	 * @return
	 */
	public int getId() {
		return this.myId;
	}

	/**
	 * Set the node id
	 * 
	 * @param id
	 */
	public void setId(int id) {
		this.myId = id;
	}

	/**
	 * Set the node type
	 * 
	 * @param nodeType
	 *            the new node type
	 */
	public void setNodeType(PhysarumNodeType nodeType) {
		this.nyNodeType = nodeType;
	}

	/**
	 * Returns true if the node is of type SOURCE
	 * 
	 * @return true if SOURCE, false otherwise
	 */
	public boolean isSource() {
		return this.nyNodeType == PhysarumNodeType.SOURCE;
	}

	/**
	 * Returns true if the node is of type SINK
	 * 
	 * @return true if SINK, false otherwise
	 */
	public boolean isSink() {
		return this.nyNodeType == PhysarumNodeType.SINK;
	}

	/**
	 * Set or unset the node as SINK
	 * 
	 * @param setOrUnset
	 *            true for set, false for unset
	 */
	public void setSink(boolean setOrUnset) {
		if (setOrUnset) {
			this.nyNodeType = PhysarumNodeType.SINK;
		} else {
			this.nyNodeType = PhysarumNodeType.NORMAL;
		}
	}

	/**
	 * Set or unset the node as SOURCE
	 * 
	 * @param setOrUnset
	 *            true for set, false for unset
	 */
	public void setSource(boolean setOrUnset) {
		if (setOrUnset) {
			this.nyNodeType = PhysarumNodeType.SOURCE;
		} else {
			this.nyNodeType = PhysarumNodeType.NORMAL;
		}
	}

	/**
	 * Returns a string describing the node
	 * 
	 * @return a description string
	 */
	public String getDescription() {
		return "Node: id = " + this.myId + "\t" + "pressure = "
				+ this.myPressure + "\t" + this.nyNodeType.toString();
	}

	/**
	 * Set the name of the node
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.myName = name;
	}

	/**
	 * Get the nodes name
	 * 
	 * @return
	 */
	public String getName() {
		if (this.myName.isEmpty()) {
			return "Node" + this.myId;
		}
		return this.myName;
	}
}
