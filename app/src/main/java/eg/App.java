package eg;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class App {
	public static void main(String[] args) {

		JFrame frame = new JFrame("2D Grapher");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Popup p = new Popup();
		
		p.setBackground(Color.GRAY);
		p.setPreferredSize(new Dimension(200, 480));

		//frame.add(p, BorderLayout.LINE_END);

		JScrollPane scrollPane = new JScrollPane(p);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(0, 0, 200, 480);

		frame.add(scrollPane, BorderLayout.LINE_END);


		EquationWorld wld = new EquationWorld(640, 480, 10, -10, 10, -10, p);
		wld.setPreferredSize(new Dimension(640, 480));

		frame.add(wld, BorderLayout.CENTER);

		frame.pack();
		frame.setVisible(true);




		//p.add(scrollPane);


		frame.pack();
		frame.setVisible(true);


		//frame.setContentPane(wld);    
		//frame.setVisible(true); 
		//frame.add(wld);
		//frame.pack(); 

		//wld.setBounds(0, 0, 640, 480);


	}
}
