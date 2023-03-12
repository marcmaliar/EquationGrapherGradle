package eg;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class DragMouseListener implements MouseMotionListener{

	private EquationWorld wld;
	private int mouseX;
	private int mouseY;


	public DragMouseListener(EquationWorld wld){
		this.wld = wld;

	}


	@Override
	public void mouseDragged(MouseEvent e) {
		double xShift = (wld.getxBoundaryRight() - wld.getxBoundaryLeft())* ((e.getX() - mouseX)/(double)wld.getCanvasWidth());
		wld.setxAxes(wld.getxBoundaryRight() - xShift, wld.getxBoundaryLeft() - xShift);
		//wld.setTickMarkXPosition(wld.getTickMarkXPosition() + (e.getX() - mouseX));

		double yShift = (wld.getyBoundaryUpper() - wld.getyBoundaryLower()) * ((e.getY() - mouseY)/(double)wld.getCanvasHeight());
		wld.setyAxes(wld.getyBoundaryUpper() + yShift, wld.getyBoundaryLower() + yShift);
		//wld.setTickMarkYPosition(wld.getTickMarkYPosition() + (e.getY() - mouseY));

		mouseX = e.getX();
		mouseY = e.getY();
		wld.repaint();

	}


	@Override
	public void mouseMoved(MouseEvent e) {
		
		
	}


	/**
	 * @return the mouseX
	 */
	public int getMouseX() {
		return mouseX;
	}


	/**
	 * @param mouseX the mouseX to set
	 */
	public void setMouseX(int mouseX) {
		this.mouseX = mouseX;
	}


	/**
	 * @return the mouseY
	 */
	public int getMouseY() {
		return mouseY;
	}


	/**
	 * @param mouseY the mouseY to set
	 */
	public void setMouseY(int mouseY) {
		this.mouseY = mouseY;
	}
	
	
	
	

}
