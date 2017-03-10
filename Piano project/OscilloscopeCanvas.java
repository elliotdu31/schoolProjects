import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;

public class OscilloscopeCanvas extends JPanel implements MouseWheelListener {
	private static final long serialVersionUID = 1L;

	public boolean scaled = true;
	private double zoom;
	private double scaleY = 1.0;

	private SoundSample soundSample;

	OscilloscopeCanvas() {
		this.zoom = 1.0;
		addMouseWheelListener(this);
	}

	OscilloscopeCanvas(boolean scaled, double zoom) {
		this.scaleY = 0.5;
		this.zoom = 1.0;
		this.scaled = scaled;
		addMouseWheelListener(this);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (e.getWheelRotation() < 0)
			zoom *= 0.9;
		else
			zoom /= 0.9;
	}

	public void setData(SoundSample soundSample) {
		this.soundSample = soundSample;
		this.repaint();
	}

	public void paintComponent(Graphics g) {
		g.setColor(Color.white);

		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		if (this.soundSample == null || this.soundSample.data == null)
			return;

		double max = 0.0;
		double somme = 0.0;
		for (int i = 0; i < soundSample.data.length; i++) {
			somme += Math.abs(soundSample.data[i]);
			if (Math.abs(soundSample.data[i]) > max)
				max = Math.abs(soundSample.data[i]);
		}
		somme /= soundSample.data.length;
		this.scaled = false;
		if (this.scaled)
			scaleY = (double) this.getHeight() / max * 0.5;

		/*// ligne du level 
		g.setColor(Color.red);
		int level = (int) (somme / 128.0 * this.getHeight());
		g.fillRect(0, this.getHeight()-level, this.getWidth(), level);
		g.drawString("level : " + level, 10, 20);*/

		/*/lignes horizontales
		g.setColor(Color.gray);
		for (double i = 0; i < this.getHeight(); i += 5) 
			g.drawLine(0, (int) i, this.getWidth(), (int) i);
		/*/

		//le signal
		g.setColor(Color.blue);
		for (int i = 0; i < soundSample.data.length - 1; i++) {
			g.drawLine((int) (i * zoom), this.getHeight() / 2 + (int) (this.soundSample.data[i] * scaleY),
					(int) ((i + 1) * zoom), this.getHeight() / 2 + (int) (this.soundSample.data[i + 1] * scaleY));
		}

		//ligne du milieu
		g.setColor(Color.black);
		g.drawLine(0, this.getHeight() / 2, this.getWidth(), this.getHeight() / 2);
		g.drawString("Zoom : " + this.zoom, 10, 10);
	}
}