package eg;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class WheelListener implements MouseWheelListener{

	private EquationWorld wld;
	
	public WheelListener(EquationWorld wld){
		this.wld = wld;
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int rotations = e.getWheelRotation();
		double xFactor = rotations * (wld.getxBoundaryRight() - wld.getxBoundaryLeft())/4;
		wld.setxAxes(wld.getxBoundaryRight() - xFactor, wld.getxBoundaryLeft() + xFactor);
    	double yFactor = rotations * (wld.getyBoundaryUpper() - wld.getyBoundaryLower())/4;
    	wld.setyAxes(wld.getyBoundaryUpper() - yFactor, wld.getyBoundaryLower() + yFactor);
    	wld.repaint();
	}

}
