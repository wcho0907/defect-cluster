import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class DefectClusterMain extends DefectCluster {

	private static final long serialVersionUID = 6239029401279604891L;

	public static void main(String args[]) {
		DefectClusterMain gameOfLife = new DefectClusterMain();
		new AppletFrame( "Cluster Analysis", gameOfLife );
    }

	public void init( Frame parent ) {
		getParams();
		setBackground(new Color(0x999999));
		defectClusterGrid = new StandAloneDefectClusterGrid(parent, cellCols, cellRows);
		defectClusterCanvas = new DefectClusterCanvas(defectClusterGrid, cellSize);
		controls = new DefectClusterPanel( this , defectClusterCanvas);

		setLayout(new BorderLayout());
		add(BorderLayout.SOUTH, controls);
		add(BorderLayout.NORTH, defectClusterCanvas);
		setVisible(true);
		validate();
	}

	public String getParameter( String parm ) {
        return System.getProperty( parm );
    }

	public StandAloneDefectClusterGrid getDefectClusterGridStandAlone() {
		return (StandAloneDefectClusterGrid) defectClusterGrid;
	}
}

class AppletFrame extends Frame {

	private static final long serialVersionUID = -2810368718460332769L;
	private DefectCluster applet;
 
	@SuppressWarnings("deprecation")
	public AppletFrame(String title, DefectClusterMain applet) {
        super( title );
		this.applet = applet;

		this.enableEvents(Event.WINDOW_DESTROY);

        MenuBar menubar = new MenuBar();
        Menu fileMenu = new Menu("File", true);
		MenuItem readMenuItem = new MenuItem("Open...");
		readMenuItem.addActionListener(
			new ActionListener() {
				public synchronized void actionPerformed(ActionEvent e) {
					StandAloneDefectClusterGrid grid = getStandaloneDefectCluster().getDefectClusterGridStandAlone();
					grid.openShape();
					getStandaloneDefectCluster().reset();
				}
			}
		);
		MenuItem writeMenuItem = new MenuItem("Save...");
		writeMenuItem.addActionListener(
			new ActionListener() {
				public synchronized void actionPerformed(ActionEvent e) {
					StandAloneDefectClusterGrid grid = getStandaloneDefectCluster().getDefectClusterGridStandAlone();
					grid.saveShape();
				}
			}
		);
		MenuItem quitMenuItem = new MenuItem("Exit");
		quitMenuItem.addActionListener(
			new ActionListener() {
				public synchronized void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			}
		);

        fileMenu.add(readMenuItem);
		fileMenu.add(writeMenuItem);
		fileMenu.addSeparator();
		fileMenu.add(quitMenuItem);

        menubar.add(fileMenu);

        this.setMenuBar(menubar);
        add("Center", applet);
        setResizable(false);
        Toolkit screen = getToolkit();
        Dimension screenSize = screen.getScreenSize();
        
        if ( screenSize.width >= 640 && screenSize.height >= 480 )
	        //setLocation((screenSize.width-550)/2, (screenSize.height-400)/2);
        	setLocation(100,100);
        show();
		applet.init( this );
		applet.start();
		pack();
    }


	public void processEvent( AWTEvent e ) {
		if ( e.getID() == Event.WINDOW_DESTROY )
			System.exit(0);
	}

	public DefectClusterMain getStandaloneDefectCluster() {
		return (DefectClusterMain) applet;
	}
}


class StandAloneDefectClusterGrid extends DefectClusterGrid {
	private Frame parent;
	private String filename = null;

	public StandAloneDefectClusterGrid( Frame parent, int cellCols, int cellRows) {
		super( cellCols, cellRows );
		this.parent = parent;
	}

	/**
	 * Load shape from disk
	 */
	public void openShape() {
		int col = 0;
		int row = 0;
		boolean cell;
		// Cope with different line endings ("\r\n", "\r", "\n")
		boolean nextLine = false;
		try {
			FileHelper file = new FileHelper( parent, "Open Game of Life file" );
			String text = file.readText();
			if ( text.length() == 0 )
				return;
			filename = file.getFileName();
			clear();
			char [] ca = text.toCharArray();
			for ( int i=0; i < ca.length; i++ ) {
				if ( !nextLine && ( ca[i] == '\n' || ca[i] == '\r' ) ) {
					row++;
					col = 0;
					nextLine = true;
					continue; 
				}
				nextLine = false;
				switch( ca[i] ) {
					case '*':
					case 'O':
					case 'X':
					case '1':
						cell = true;
						break;
					default:
						cell = false;
						break;
				}
				setCell( col, row, cell );
				col++;
			}
			notEmpty();
		} catch (FileNotFoundException e) {
			System.out.println("Couldn't open this file.\n"+e.getMessage());
		} catch (IOException e) {
			System.out.println("Couldn't read this file.\n"+e.getMessage());
		}
	}

	/**
	 * Write shape to disk.
	 */
	public void saveShape() {
		String lineSeperator = System.getProperty( "line.separator" );
		StringBuffer text = new StringBuffer();
		for ( int row = 0; row < cellRows; row++ ) {
			for ( int col = 0; col < cellCols; col++ ) {
				text.append( getCell( col, row ) ? 'O' : '-' );
			}
			text.append( lineSeperator );
		}
		FileHelper file;
		try {
			file = new FileHelper( parent, "Save Game of Life file" );
			file.setFileName( filename );
			file.writeText( text.toString() );
		} catch (FileNotFoundException e) {
			System.out.println("Couldn't open this file.\n"+e.getMessage());
		} catch (IOException e) {
			System.out.println("Couldn't write to this file.\n"+e.getMessage());
		}
	}
}





