public class Defect {
	private double x;
	private double y;
	private int ClusterNo = -1;
	
	
	/**
	 * 
	 */
	public Defect(int x, int y) {
		this.x = x;
		this.y = y;
	}	


	public static void main(String[] args) {
	}
	/**
	 * @return
	 */
	public int getClusterNo() {
		return ClusterNo;
	}

	/**
	 * @return
	 */
	public double getX() {
		return x;
	}

	/**
	 * @return
	 */
	public double getY() {
		return y;
	}

	/**
	 * @param i
	 */
	public void setClusterNo(int i) {
		ClusterNo = i;
	}

	/**
	 * @param d
	 */
	public void setX(double d) {
		x = d;
	}

	/**
	 * @param d
	 */
	public void setY(double d) {
		y = d;
	}

}
