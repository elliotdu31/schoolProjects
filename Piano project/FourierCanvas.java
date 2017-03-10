
import java.awt.Color;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;

public class FourierCanvas extends JPanel implements MouseWheelListener, MouseListener {
	private static final long serialVersionUID = 1L;

	private double scaleY;
	private int decalage;
	private double zoom;

	private FourrierSample fourrierSample;
	
	FourierCanvas() {
		addMouseWheelListener(this);
		addMouseListener(this);
		this.zoom = 1.0;
		this.scaleY = 0.001;
	}

	public void setData(FourrierSample fourrierSample) {
		this.fourrierSample = fourrierSample;
		this.repaint();
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (e.getWheelRotation() < 0) {
			zoom *= 0.8;
			this.decalage = (int)(this.decalage *0.8);
		}
		else {
			zoom /= 0.8;
			this.decalage = (int)(this.decalage /0.8);
		}
		if (zoom < 1.0) 
			zoom = 1.0;
		this.repaint();
	}
	private int mouseLastX = 0;
	public void mousePressed(MouseEvent e) {
		this.mouseLastX = e.getX();
	}

	public void mouseReleased(MouseEvent e) {
		this.decalage += e.getX() - this.mouseLastX;
		if(decalage > 0)
			decalage = 0;
	}

	public void mouseEntered(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {

	}

	public void mouseClicked(MouseEvent e) {

	}

	public void paintComponent(Graphics g) {

		g.setColor(Color.white);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		if (this.fourrierSample == null || this.fourrierSample.data == null)
			return;
		
		double scaleX = (double) this.getWidth() / (double) this.fourrierSample.data.length * zoom;
		g.setColor(Color.blue);
		for (int i = 0; i < fourrierSample.data.length; i++)
			g.drawLine((int) (i * scaleX) + decalage, this.getHeight() - 10 - (int) (this.fourrierSample.data[i] * scaleY),
					(int) (i * scaleX) + decalage, this.getHeight() - 10);

		g.setColor(Color.black);
		g.drawLine(0, this.getHeight() - 10, this.getWidth(), this.getHeight() - 10);
		
		
		
		g.drawString("f : "
				+ (int) ((double) (MouseInfo.getPointerInfo().getLocation().getX() - this.getLocationOnScreen().getX() - decalage)
						/ this.getWidth() * this.fourrierSample.sampleRate / this.zoom),
				10, 10);
		g.drawString("y : " + (int)(this.getHeight() - 10  -MouseInfo.getPointerInfo().getLocation().getY() + this.getLocationOnScreen().getY()), 10, 30);
		g.drawString("zoom : " + zoom, 10, 50);
		
		g.drawLine((int)(MouseInfo.getPointerInfo().getLocation().getX()  - this.getLocationOnScreen().getX()), 0, (int)(MouseInfo.getPointerInfo().getLocation().getX()  - this.getLocationOnScreen().getX()), this.getHeight());
		g.drawLine(0, (int)(MouseInfo.getPointerInfo().getLocation().getY() - this.getLocationOnScreen().getY()), this.getWidth(), (int)(MouseInfo.getPointerInfo().getLocation().getY() - this.getLocationOnScreen().getY()));
	}
}