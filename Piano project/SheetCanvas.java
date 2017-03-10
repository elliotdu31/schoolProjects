
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

public class SheetCanvas extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private boolean[][] sheet;
	private int size;
	SheetCanvas(int size) {
		this.size = size;
		sheet = new boolean[size][];
		for(int i = 0; i < size; i++)
			sheet[i] = new boolean[12];
		this.setSize(size*20, 12*20);
		
	}
	public void setData(boolean[] data) {
		for(int i = 0; i < size-1; i++)
			for(int j = 0; j < 12; j++)
				sheet[i][j] = sheet[i+1][j];
		for(int j = 0; j < 12; j++)
			sheet[size-1][j] = data[j];
		
		this.repaint();
	}
	public void paintComponent(Graphics g) {
	    g.setColor(Color.black);
	    g.fillRect(0, 0, size*20, 12*20);
	    g.setColor(Color.white);
	    for(int i = 0; i < size; i++) {
	    	for(int j = 0; j < 12; j++) {
	    		if(sheet[i][j])
	    			g.fillRect(i*20, j*20, 20, 20);
	    	}
	    }
		
	}
}