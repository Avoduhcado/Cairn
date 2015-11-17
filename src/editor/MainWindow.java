package editor;

import java.awt.EventQueue;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.newdawn.slick.util.ResourceLoader;

import java.awt.Toolkit;
import java.io.IOException;

public class MainWindow {

	private JFrame frmCairnEditor;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		System.setProperty("resources", System.getProperty("user.dir") + "/resources");

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frmCairnEditor.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmCairnEditor = new JFrame();
		try {
			frmCairnEditor.setIconImage(ImageIO.read(
					ResourceLoader.getResourceAsStream(System.getProperty("resources") + "/sprites/ui/Icon.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		frmCairnEditor.setTitle("Cairn Editor");
		frmCairnEditor.setBounds(100, 100, 450, 300);
		frmCairnEditor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
