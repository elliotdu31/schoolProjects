
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

public class PCPcanvas extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private static String[] noms = {"c", "c#", "d", "d#", "e", "f", "f#", "g", "g#", "a", "a#", "b"};
	
	private double[] data;
	
	public void setData(double[] data) {
		this.data = data;
		this.repaint();
	}
	public void paintComponent(Graphics g) {
	    g.setColor(Color.white);
	    g.fillRect(0, 0, this.getWidth(), this.getHeight());
	    
	    if(data == null || data.length < 12)
	    	return;
		for(int i = 0; i < 12; i++) {
			g.setColor(Color.blue);
			g.fillRect(20 + i*40, 250 - (int)(data[i]*250), 20, (int)(data[i]*250));
			g.setColor(Color.black);
			g.drawRect(20 + i*40, 250 - (int)(data[i]*250), 20, (int)(data[i]*250));
			g.setColor(Color.red);
			g.drawString(noms[i], 25 + i*40, 280);
		}
		
	}
}