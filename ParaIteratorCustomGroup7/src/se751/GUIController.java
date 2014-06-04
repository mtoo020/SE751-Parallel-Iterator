package se751;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

/**
 * Creates a simple Window Frame with a LayeredPane.
 */
public class GUIController implements Runnable {

	private JLayeredPane _layeredPane = new JLayeredPane();
	
	private JFrame _frame;
	
	public GUIController(){
		_frame = new JFrame("New GUI");
	}
	
	public GUIController(String name){
		_frame = new JFrame(name);
	}
	
	public JLayeredPane getLayeredPane(){
		return _layeredPane;
	}
	
	public JFrame getFrame(){
		return _frame;
	}
	
	public void run() {
		_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		_layeredPane.setVisible(true);

		_layeredPane.setPreferredSize(new Dimension(700, 500));
		_frame.setSize(700, 700);

		JComponent newContentPane = new JPanel();
		newContentPane.add(_layeredPane);
		newContentPane.setVisible(true);
		newContentPane.setOpaque(true); // content panes must be opaque
		_frame.setContentPane(newContentPane);
		_frame.setVisible(true);
	}
	
}
