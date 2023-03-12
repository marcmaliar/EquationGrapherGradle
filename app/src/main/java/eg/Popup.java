package eg;

import java.awt.Component;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Popup extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = -257445050084749913L;

	
	private ArrayList<Component> listOfCommands;
	
	
	public Popup(){
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		listOfCommands = new ArrayList<Component>();
	}
	
	public void newPopupCommands(Commandable c){
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		removeAll();
		((JFrame) SwingUtilities.getWindowAncestor(this)).repaint();
		
	
		listOfCommands = c.commands();
		
		for (int i = 0; i < listOfCommands.size(); i++){
			this.add(listOfCommands.get(i));
			listOfCommands.get(i).setVisible(true);
		}
		((JFrame) SwingUtilities.getWindowAncestor(this)).pack();
		((JFrame) SwingUtilities.getWindowAncestor(this)).repaint();
	}
	
	
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);    // Paint background
		
	}
	
	public void showCommands(){
		
	}

}
