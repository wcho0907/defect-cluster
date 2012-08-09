import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.util.*;

public class DefectCluster extends Applet implements Runnable {

	private static final long serialVersionUID = 1517414147958850655L;
	protected DefectClusterCanvas defectClusterCanvas;
	protected DefectClusterGrid defectClusterGrid;
	protected int cellSize;
	protected int cellCols;
	protected int cellRows;
	protected int genTime;
	protected DefectClusterPanel controls;
	private static Thread workThread = null;
	private final String VIRGIN = "First draw a shape or select a shape from\nthe pull-down menu.";

	public void init() {
		getParams();
		setBackground(new Color(0x999999));
		defectClusterGrid = new DefectClusterGrid(cellCols, cellRows);
		defectClusterCanvas = new DefectClusterCanvas(defectClusterGrid, cellSize);
		controls = new DefectClusterPanel( this, defectClusterCanvas);
		setLayout(new BorderLayout());
		add(BorderLayout.SOUTH, controls);
		add(BorderLayout.NORTH, defectClusterCanvas);
		setVisible(true);
		validate();
	}
	
	protected void getParams() {
		cellSize = getParamInteger("cellsize", 2 );
		cellCols = getParamInteger("cellcols", 400 );
		cellRows = getParamInteger("cellrows", 250 );
	}
	
	protected int getParamInteger( String name, int defaultParam ) {
		String param;
		int paramInt;

		param = getParameter( name );
		if ( param == null )
			paramInt = defaultParam;
		else
			paramInt = Integer.valueOf(param).intValue();
		return paramInt;
	}

	public synchronized void start2() {
		if ( defectClusterGrid.isEmpty() ) {
			alert( VIRGIN );
		} else {
			controls.start();
			if (workThread == null) {
				workThread = new Thread(this);
				workThread.start();
			}
		}
	}

	public void stop() {
		controls.stop();
		workThread = null;
	}

	public synchronized void run() {
		while (workThread != null) {
			nextGeneration();
			try {
				Thread.sleep(genTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean isRunning() {
		return workThread != null;
	}

	public void nextGeneration() {
		if ( defectClusterGrid.isEmpty() ) {
			alert( VIRGIN );
		} else {
			defectClusterCanvas.repaint();
		}
	}

	public void cluster(){
		double tolerance = controls.getTorelance();
		int counts = controls.getCounts();
		
		ClusterHelper myCH = new ClusterHelper(tolerance , counts);
		ArrayList<Defect> defects = new ArrayList<Defect>();
		
		for (int x = 0; x < cellCols; x++) {
			for (int y = 0; y < cellRows; y++) {
				if(defectClusterGrid.getCell(x,y)){
					defects.add(new Defect(x,y));
				}
			}
		}
		myCH.setDefects(defects);
		myCH.cluster();
		ArrayList<Defect> showList;
		showList = myCH.getDefects();
		Defect theDefect;
		for(int i = 0; i < showList.size();i++){
			theDefect = (Defect)showList.get(i);
			defectClusterCanvas.drawCell((int)theDefect.getX(),(int)theDefect.getY(),theDefect.getClusterNo());			
		}	
		//System.out.println(counts);

	}
	
	public void setShape( String shapeName ) {
		try {
			defectClusterGrid.setShape( shapeName );
			reset();
		} catch (ShapeException e) {
			alert( e.getMessage() );
		}
	}
	
	public void reset() {
		stop(); // might otherwise confuse user
		defectClusterCanvas.repaint();
		showGenerations();
		//showStatus( "" );
	}

	public String getAppletInfo() {
		return "Defect Cluster";
	}

	private void showGenerations() {
	}
	
	public void setSpeed( int fps ) {
		genTime = fps;
	}
	
	public void alert( String s ) {
		showStatus( s );
	}
}

class DefectClusterPanel extends Panel{
	private static final long serialVersionUID = -1009540911149095190L;
	private Label toleranceLabel;
	private Label minAmountLabel;	
	private Label posotionLabel;
	private Label cellNumsLabel;
	private final String positionLabelText = "Position[";
	private final String cellNums = "Cell(s)[";
	private TextField tolerance;
	private TextField minAmount;
	private final String startLabelText = "Cluster";
	private final String stopLabelText = "Set";
	private Button clusterButton;
	private DefectCluster defectCluster;
	private DefectClusterCanvas defectClusterCanvas;
	private MouseMotionAdapter mouseMotionAdapter;
	private MouseAdapter mouseAdapter;

	public DefectClusterPanel( DefectCluster defectCluster,  final DefectClusterCanvas defectClusterCavas) {
		this.defectCluster = defectCluster;
		this.defectClusterCanvas = defectClusterCavas;
		
		// pulldown menu with shapes
		Choice shapesChoice = new Choice();
	
		// Put names of shapes in menu
		Enumeration<Object> shapes = DefectClusterGrid.getShapes();
		while (shapes.hasMoreElements()) {
			shapesChoice.addItem((String)shapes.nextElement());
		}
	
		// when shape is selected
		shapesChoice.addItemListener(
			new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					String shapeName = (String) e.getItem();
					getDefectCluster().setShape(shapeName );
					showCellNums();
				}
			}
		);
	
		toleranceLabel = new Label("Tolerance");
		minAmountLabel = new Label("Min. Amount");
		
		tolerance = new TextField("10");
		minAmount = new TextField("1");
		
		// number of generations
		posotionLabel = new Label(positionLabelText+"x:y]          ");
		cellNumsLabel = new Label(cellNums+"0]      ");
	
		// start and stop buttom
		clusterButton = new Button(startLabelText);
			
		// when start/stop button is clicked
		clusterButton.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					//startStopButtonClicked();
					getDefectCluster().cluster();
				}
			}
		);	

		// create panel with controls
		this.add(toleranceLabel);
		this.add(tolerance);
		this.add(minAmountLabel);
		this.add(minAmount);
		this.add(shapesChoice);

		this.add(clusterButton);
		//this.add(speedChoice);
		this.add(posotionLabel);
		this.add(cellNumsLabel);
		this.validate();
		mouseMotionAdapter = new MouseMotionAdapter() {
			   public void mouseDragged(MouseEvent e) {				   
				   draw(e.getX(), e.getY());
				showCellNums();
					showPosition(e.getX()/defectClusterCavas.cellSize, e.getY()/defectClusterCavas.cellSize);

			   }
			   public void mouseMoved(MouseEvent e) {
				   showPosition(e.getX()/defectClusterCavas.cellSize, e.getY()/defectClusterCavas.cellSize);

			   }
		   };

		//addMouseListener(
		mouseAdapter = new MouseAdapter() {
				public void mouseReleased(MouseEvent e) {
					draw(e.getX(), e.getY());
					showCellNums();
				}
				public void mousePressed(MouseEvent e) {
					defectClusterCavas.saveCellUnderMouse(e.getX(), e.getY());					
				}
					
		};
			
		defectClusterCavas.addMouseMotionListener(mouseMotionAdapter);
		defectClusterCavas.addMouseListener(mouseAdapter);
	}

	public void draw(int x, int y) {
		defectClusterCanvas.draw(x, y);
	}		

	public void showPosition( int x, int y) {
		posotionLabel.setText(positionLabelText + x + " : " + y +"]");
	}
	
	public void showCellNums(){
		cellNumsLabel.setText(cellNums + defectClusterCanvas.getCellNums()+"]");
	}

	public void start() {
		clusterButton.setLabel(stopLabelText);
	}

	public void stop() {
		clusterButton.setLabel(startLabelText);
	}

	public void startStopButtonClicked() {
		if ( defectCluster.isRunning() ) {
			defectCluster.stop();
		} else {
			defectCluster.start2();
		}
	}

	public DefectCluster getDefectCluster() {
		return defectCluster;
	}
	
	public double getTorelance(){
		return Double.parseDouble(tolerance.getText());
	}
	
	public int getCounts(){
		return Integer.parseInt(minAmount.getText());
	}
}

class DefectClusterCanvas extends Canvas{

	private static final long serialVersionUID = -8361275815540073627L;
	private boolean cellUnderMouse;
	private Image offScreenImage = null;
	private Graphics offScreenGraphics;
	public int cellSize;
	final private DefectClusterGrid defectClusterGrid;

	public DefectClusterCanvas(final DefectClusterGrid defectClusterGrid, final int cellSize) {
		this.defectClusterGrid = defectClusterGrid;
		this.cellSize = cellSize;
		defectClusterGrid.clear();
	}

	public void saveCellUnderMouse(int x, int y) {
		try {
			cellUnderMouse = defectClusterGrid.getCell(x / cellSize, y / cellSize);
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			// ignore
		}
	}

	public void draw(int x, int y) {
		try {			
			defectClusterGrid.setCell(x / cellSize, y / cellSize, !cellUnderMouse );
			repaint();
			defectClusterGrid.notEmpty();
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			// ignore
		}
	}

	public void update(Graphics theG) {
		Dimension d = getSize();
		if ((offScreenImage == null)) {
			offScreenImage = createImage(d.width, d.height);
			offScreenGraphics = offScreenImage.getGraphics();
		}
		paint(offScreenGraphics);
		theG.drawImage(offScreenImage, 0, 0, null);
	}

	public void paint(Graphics g) {
		// draw background (MSIE doesn't do that)
		Dimension dim = defectClusterGrid.getDimension();
		g.setColor(Color.gray);
		g.fillRect(0, 0, cellSize * dim.width - 1, cellSize * dim.height - 1);
		// draw grid
		g.setColor(getBackground());
		for (int x = 1; x < dim.width; x++) {
			g.drawLine(x * cellSize - 1, 0, x * cellSize - 1, cellSize * dim.height - 1);
		}
		for (int y = 1; y < dim.height; y++) {
			g.drawLine( 0, y * cellSize - 1, cellSize * dim.width - 1, y * cellSize - 1);
		}
		// draw populated cells
		g.setColor(Color.yellow);
		for (int y = 0; y < dim.height; y++) {
			for (int x = 0; x < dim.width; x++) {
				if (defectClusterGrid.getCell(x, y)) {					
					g.setColor(Color.black);
					g.fillRect(x * cellSize, y * cellSize, cellSize - 1, cellSize - 1);
					//g.setColor(Color.yellow);
					//g.drawRect(x * cellSize, y * cellSize, cellSize - 1, cellSize - 1);
				}
			}
		}
	}
	
	public void drawCell(int x, int y, int col){
		Graphics g = this.getGraphics();
		g.setColor(Color.black);
		g.fillRect(x * cellSize, y * cellSize, cellSize - 1, cellSize - 1);
		col = (int)col%7;
		switch(col){
			case -1:
				g.setColor(Color.BLACK);
				break;			
			case 0:
				g.setColor(Color.YELLOW);
				break;
			case 1:
				g.setColor(Color.BLUE);
				break;
			case 2:
				g.setColor(Color.RED);
				break;
			case 3:
				g.setColor(Color.GREEN);
				break;
			case 4:
				g.setColor(Color.MAGENTA);
				break;
			case 5:
				g.setColor(Color.ORANGE);
				break;
			case 6:
				g.setColor(Color.PINK);
				break;       		
		}
		g.drawRect(x * cellSize, y * cellSize, cellSize - 1, cellSize - 1);
	}
	/**
	 * This is the preferred size.
	 * @see java.awt.Component#getPreferredSize()
	 */
	public Dimension getPreferredSize() {
		Dimension dim = defectClusterGrid.getDimension();
		return new Dimension( cellSize * dim.width,	cellSize * dim.height );
	}
	
	public int getCellNums() {
		return defectClusterGrid.getCellNums();
	}
}

class DefectClusterGrid {
	protected boolean cells[][];
	protected int cellRows;
	protected int cellCols;
	protected int cellsBuffer[][];
	private int generations;
	private static Shape[] shapes;
	private boolean empty;
	private int cellNums = 0;
	
	static {
		// define all available shapes
		shapes = new Shape[1];
		shapes[0] = new Shape("Clear", new int[][] {} );
	}

	public DefectClusterGrid(int cellCols, int cellRows) {
		this.cellCols = cellCols;
		this.cellRows = cellRows;
		cellsBuffer = new int[cellCols][cellRows];
		cells = new boolean[cellCols][cellRows];
		empty = true;
	}


	public static ShapeEnumeration getShapes() {
		return new ShapeEnumeration( shapes );
	}


	public void clear() {
		generations = 0;
		//virgin = true;
		for (int x = 0; x < cellCols; x++) {
			for (int y = 0; y < cellRows; y++) {
				cells[x][y] = false;
			}
		}
		setCellNums(0);
	}

	public synchronized void setShape(String shapeName) throws ShapeException {
		int xOffset;
		int yOffset;
		int[][] shape = null;
		Dimension dim = null;
		int i;

		notEmpty();
		for ( i = 0; i < shapes.length; i++ ) {
			if ( shapes[i].getName().equals( shapeName ) )
				break;
		}
		
		// not found
		if ( i == shapes.length )
			throw new ShapeException( "Unknown shape" ); // shape doesn't fit on canvas

		// get shape properties
		shape = shapes[i].getShape();
		dim =  shapes[i].getDimension();

		if (dim.width > cellCols || dim.height > cellRows)
			throw new ShapeException( "Shape doesn't fit on canvas" ); // shape doesn't fit on canvas

		// center the shape
		xOffset = (cellCols - dim.width) / 2;
		yOffset = (cellRows - dim.height) / 2;
		clear();

		// draw shape
		for ( i = 0; i < shape.length; i++ ){
			cells[xOffset + shape[i][0]][yOffset + shape[i][1]] = true;			
		}
		setCellNums(i);
	}
	
	public boolean getCell( int x, int y ) {
		try {
			return cells[x][y];
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
	}

	public void setCell( int x, int y, boolean c ) {
		try {
			if(cells[x][y]==false){
				if(c==true){
					setCellNums(getCellNums()+1);
				}				
			}
			else{
				if(c==false){
					setCellNums(getCellNums()-1);
				}
			}
			cells[x][y] = c;
		} catch (ArrayIndexOutOfBoundsException e) {
			// ignore
		}
	}

	public int getGenerations() {
		return generations;
	}
	
	public Dimension getDimension() {
		return new Dimension( cellCols, cellRows );
	}

	public void notEmpty() {
		empty = false;
	}
	
	public boolean isEmpty() {
		return empty;
	}

	public int getCellNums() {
		return cellNums;
	}

	public void setCellNums(int i) {
		cellNums = i;
	}

}

class Shape {
	private String name;
	private int[][] shape;
	
	public Shape( String name, int[][] shape ) {
		this.name = name;
		this.shape = shape;
	}
	
	public Dimension getDimension() {
		int shapeWidth = 0;
		int shapeHeight = 0;
		for (int cell = 0; cell < shape.length; cell++) {
			if (shape[cell][0] > shapeWidth)
				shapeWidth = shape[cell][0];
			if (shape[cell][1] > shapeHeight)
				shapeHeight = shape[cell][1];
		}
		shapeWidth++;
		shapeHeight++;
		return new Dimension( shapeWidth, shapeHeight );
	}

	public String getName() {
		return name;
	}

	public int[][] getShape() {
		return shape;
	}
}

class ShapeEnumeration implements Enumeration<Object> {
	private int index;
	private Shape[] shapes;

	public ShapeEnumeration( Shape[] shapes ) {
		index = 0;
		this.shapes = shapes;
	}

	public boolean hasMoreElements() {
		return index < shapes.length;
	}

	public Object nextElement() {
		index++;
		return shapes[index-1].getName();
	}
}

class ShapeException extends Exception {
	private static final long serialVersionUID = 8833495232858994431L;

	public ShapeException() {
		super();
	}

	public ShapeException( String s ) {
		super( s );
	}
}

