import java.io.*;
import java.awt.*;

public class FileHelper {
	String filepath;
	String filename;
	private InputStream textFileReader;
	private OutputStream textFileWriter;
	private final int bufferLength=1024;
	Frame parent;
	String title;

	/**
	 * Constructor. Open file by filename.
	 * @param filepath path of file
	 * @throws FileNotFoundException
	 */
	public FileHelper( String filepath ) {
		this.filepath = filepath;
		textFileReader = null;
		textFileWriter = null;
	}

	/**
	 * Constructor. Open file with file selector.
	 * @param filename name of file
	 * @throws FileNotFoundException
	 */
	public FileHelper( Frame parent, String title ) {
		this.parent = parent;
		this.title = title;
		textFileReader = null;
		textFileWriter = null;
	}
	
	/**
	 * Constructor for reading. Read file from stream.
	 * @param textFileStream stream to read from
	 */
	public FileHelper( InputStream textFileReader ) {
		this.textFileReader = textFileReader;
		textFileWriter = null;
		filepath = null;
	}

	/**
	 * Constructor for writing. Write file to stream.
	 * @param textFileStream stream to write to
	 */
	public FileHelper( OutputStream textFileWriter ) {
		this.textFileWriter = textFileWriter;
		textFileReader = null;
		filepath = null;
	}

	/**
	 * Reads a text file into a string.
	 * @return contents of file
	 */
	@SuppressWarnings("deprecation")
	public String readText() throws IOException {
		int bytesRead;

		if ( textFileReader == null ) {
			if ( filepath == null ) {
				FileDialog filedialog = new FileDialog( parent, title, FileDialog.LOAD );
				filedialog.setFile( filename );
				filedialog.show();
				if ( filedialog.getFile() != null ) {
					filename = filedialog.getFile();
					filepath = filedialog.getDirectory()+filename;
				} else
					return "";
			}
			textFileReader = new FileInputStream( filepath );
		}

		StringBuffer text = new StringBuffer();
		byte[] buffer = new byte[bufferLength];

		while ( ( bytesRead = textFileReader.read( buffer, 0, bufferLength ) ) != -1 )
			text.append( new String( buffer, 0, bytesRead ) );
		return text.toString();
	}

	/**
	 * Writes a string to a text file.
	 * @param text text to write
	 */
	@SuppressWarnings("deprecation")
	public void writeText( String text ) throws IOException {
		if ( textFileWriter == null ) {
			if ( filepath == null ) {
				FileDialog filedialog = new FileDialog( parent, title, FileDialog.SAVE );
				filedialog.setFile( filename );
				filedialog.show();
				if ( filedialog.getFile() != null ) {
					filename = filedialog.getFile();
					this.filepath = filedialog.getDirectory()+filename;
				} else
					return;
			}
			textFileWriter = new FileOutputStream( filepath );
		}

		textFileWriter.write( text.getBytes() );
	}
	
	/**
	 * Sets filename to use
	 * @param s filename
	 */
	public void setFileName( String s ) {
		filename = s;
	}

	/**
	 * Gets filename
	 * @return filename
	 */
	public String getFileName() {
		return filename;
	}
}

