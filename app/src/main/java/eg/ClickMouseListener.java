package eg;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ClickMouseListener implements MouseListener{
	private EquationWorld wld;
	private DragMouseListener dml;
	private long startTime = 1000;
	
	public ClickMouseListener(EquationWorld wld, DragMouseListener dml){
		this.wld = wld;
		this.dml = dml;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		long currentTime = System.currentTimeMillis();
		if ((currentTime - startTime) < 200){
			double x = wld.toXValue(e.getX());
			double y = wld.toYValue(e.getY());
			wld.setxAxes(x + (wld.getxBoundaryRight()-x)/2.0, x - (x - wld.getxBoundaryLeft())/2.0);
			wld.setyAxes(y + (wld.getyBoundaryUpper()-y)/2.0, y - (y - wld.getyBoundaryLower())/2.0);
			wld.repaint();
		}
		else{
			startTime = currentTime;
		}
		
		dml.setMouseX(e.getX());
		dml.setMouseY(e.getY());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
