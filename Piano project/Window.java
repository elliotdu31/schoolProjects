import javax.swing.JFrame;
import javax.swing.JPanel;

public class Window extends JFrame {

	private static final long serialVersionUID = 1L;

	public Window(JPanel canvas, String name, int w, int h, int x, int y) {
		this.setTitle(name);
		this.setSize(w, h);
		this.setLocation(x, y);
		this.setContentPane(canvas);
		this.setVisible(true);
	}

	public Window(JPanel canvas, String name, int w, int h, int x, int y, boolean exitOnClose, boolean resizable) {
		this.setTitle(name);
		this.setSize(w, h);
		this.setLocation(x, y);
		if (exitOnClose)
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setContentPane(canvas);
		this.setVisible(true);
		this.setResizable(resizable);
	}
}