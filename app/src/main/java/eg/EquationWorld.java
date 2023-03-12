package eg;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;


/**
 * 
 * @author liliamaliar
 *
 */
public class EquationWorld extends JPanel implements Commandable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2469287503841658788L;

	/**
	 * The width of the canvas, in pixels.
	 */
	private double canvasWidth;

	/**
	 * The height of the canvas, in pixels.
	 */
	private double canvasHeight;
	/**
	 * The font size of the tick mark numbers.
	 */
	private final int fontSize = 20;
	/**
	 * The X-Coordinate of the Right Boundary (not in pixels). This number should be higher than xBoundaryRight.
	 */
	private double xBoundaryRight;
	/**
	 * The X-Coordinate of the Left Boundary (not in pixels). This number should be higher than xBoundaryLeft.
	 */
	private double xBoundaryLeft;
	/**
	 * The Y-Coordinate of the Upper Boundary (not in pixels). This number should be higher than the yBoundaryLower.
	 */
	private double yBoundaryUpper;
	/**
	 * The Y-Coordinate of the Lower Boundary (not in pixels). This number should be lower than the yBoundaryUpper.
	 */
	private double yBoundaryLower;
	/**
	 * The number of tick marks per axis.
	 */
	private int tickMarks = 12;
	/**
	 * Length of the tick marks.
	 */
	private final int TICK_MARK_LENGTH = 10;
	/**
	 * The number of digits to which the axis numbers are rounded to.
	 */
	private int roundDigits = 2;
	/**
	 * The graphics used in paint. 
	 */
	private Graphics g;
	/**
	 * The button that makes all boundaries two times bigger.
	 */
	private JButton zoomOut = new JButton();
	/**
	 * the button that makes all boundaries two times smaller.
	 */
	private JButton zoomIn = new JButton();
	private ArrayList<Equation> equations = new ArrayList<Equation>();
	private Popup p;
	
	private EquationWorld wld;
	
	
	private ArrayList<Component> commands;

	public EquationWorld(int frameWidth, int frameHeight, double x_Boundary_Right, double x_Boundary_Left, double y_Boundary_Upper, double y_Boundary_Lower, Popup p){

		//Equation World Variables
		setCanvasWidth(frameWidth);
		setCanvasHeight(frameHeight);
		
		this.xBoundaryRight = x_Boundary_Right;
		this.xBoundaryLeft = x_Boundary_Left;
		this.yBoundaryUpper = y_Boundary_Upper;
		this.yBoundaryLower = y_Boundary_Lower;

		Equation ex = new Equation("equation1", "y=1/x", this, p);
		equations.add(ex);
		
		this.p = p;
		wld = this;

		p.newPopupCommands(this);
		
		

		
		
		
		// Handling window resize
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				Component c = (Component)e.getSource();
				Dimension dim = c.getSize();
				setCanvasWidth(dim.width);
				setCanvasHeight(dim.height);
				setPreferredSize(dim);
			}
		});

		//createTextField();
		createMouseListeners();
		createZoomButtons();


	}
	public  void createMouseListeners(){
		//Creates DragMouseListener used to find coordinates of the mouse dragging the equationWorld around
		DragMouseListener dml = new DragMouseListener(this);
		addMouseMotionListener(dml);

		//Creates MouseListener used to track the position of the first click
		ClickMouseListener cml = new ClickMouseListener(this, dml);
		addMouseListener(cml);

		//Creates WheelListener used to change zoom when mouse wheel is rotated
		WheelListener wl = new WheelListener(this);
		addMouseWheelListener(wl);

	}

	
	public void newEquation(){
		Equation ex = new Equation("equation", "y=1/x", this, p);
		equations.add(ex);
	}
	


	public Equation findEquation(String name){
		for (int i = 0; i < equations.size(); i++){
			if (equations.get(i).getName().equalsIgnoreCase(name)){
				return equations.get(i);
			}
		}
		return null;

	}



	public  void createZoomButtons(){
		zoomIn.setText("Zoom in");
		zoomIn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {


				double xFactor = (getxBoundaryRight() - getxBoundaryLeft())/4;
				setxAxes(getxBoundaryRight() - xFactor, getxBoundaryLeft()+xFactor);
				double yFactor = (getyBoundaryUpper() - getyBoundaryLower())/4;
				setyAxes(getyBoundaryUpper() - yFactor, getyBoundaryLower()+yFactor);



				repaint();
			}
		});
		add(zoomIn);
		zoomIn.setVisible(true);



		zoomOut.setText("Zoom out");
		zoomOut.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				double xFactor = (getxBoundaryRight() - getxBoundaryLeft())/2;
				setxAxes(getxBoundaryRight() + xFactor, getxBoundaryLeft()- xFactor);
				double yFactor = (getyBoundaryUpper() - getyBoundaryLower())/2;
				setyAxes(getyBoundaryUpper() + yFactor, getyBoundaryLower() - yFactor);

				repaint();
			}
		});
		add(zoomOut);
		zoomOut.setVisible(true);

	}




	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);    // Paint background
		this.g = g;
		
		
		
		//updatePositionEquationBar();
		updatePositionZoomInButton();
		updatePositionZoomOutButton();
		drawBackground();
		drawFunctionNames();
		for (int i = 0; i < equations.size(); i++){
			equations.get(i).drawFunction();
		}

	}

	public void updatePositionZoomInButton(){
		zoomIn.setBounds((int) canvasWidth/2 - (85 + 5), 10, 85, 30);
	}

	public void updatePositionZoomOutButton(){
		zoomOut.setBounds((int)canvasWidth/2 + (5), 10, 85, 30);
	}



	public void drawFunctionNames(){
		for (int i = 0; i < equations.size(); i++){
			g.setColor(equations.get(i).getColor());
			g.drawString(equations.get(i).getName() + ": " + equations.get(i).getExpression(), 0, 20 + i * 30);
		}
	}


	public void drawBackground(){

		//Center of drawing canvas
		int centerX = toXPixel(0.0);
		int centerY = toYPixel(0.0);

		drawTickMarksAndGrid( centerX, centerY);
		drawAxes( centerX, centerY);


	}


	public void drawAxes(int centerX, int centerY){
		g.setColor(Color.BLACK);
		if (centerX < getCanvasWidth() && centerX > 0){
			
			g.drawLine(centerX, 0, centerX, (int) getCanvasHeight());
		}
		if (centerY < getCanvasHeight() & centerY > 0){
			g.drawLine(0, centerY, (int) getCanvasWidth(), centerY);
		}
		

	}




	public  void drawTickMarksAndGrid(double centerX, double centerY){
		
		double xCoordinatePosition = centerX;
		double yCoordinatePosition = centerY;

		if (centerX > getCanvasWidth() - 60){
			xCoordinatePosition = getCanvasWidth() - 60;
		}

		if (centerY < 20){
			yCoordinatePosition = 20;
		}
		if (centerX < 0){
			xCoordinatePosition = 0;
		}
		if (centerY > getCanvasHeight()){
			yCoordinatePosition = getCanvasHeight();
		}
		
		double xTICK_MARK_LENGTH = (double)getCanvasWidth() / getTickMarks();
		
		for (double i = centerX - xTICK_MARK_LENGTH; i > -40; i -= xTICK_MARK_LENGTH){
			if (i < getCanvasWidth() - 40){
				g.setColor(Color.BLACK);
				g.drawLine((int) i, (int)(centerY + (getTICK_MARK_LENGTH() / 2.0)), (int) i, (int)(centerY - (getTICK_MARK_LENGTH() / 2.0)));
				g.setFont(new Font("TimesRoman", Font.PLAIN, getFontSize()));
				g.drawString(roundNSigFigs(toXValue((int) i)), (int) i + 20, (int)(yCoordinatePosition - (getTICK_MARK_LENGTH() / 2.0)));
				g.setColor(Color.LIGHT_GRAY);
				g.drawLine((int) i, 0, (int) i, (int) getCanvasHeight());
			}
			
		}
		for (double i = centerX + xTICK_MARK_LENGTH; i<getCanvasWidth()-40; i += xTICK_MARK_LENGTH){
			g.setColor(Color.BLACK);
			g.drawLine((int) i, (int)(centerY + (getTICK_MARK_LENGTH() / 2.0)), (int) i, (int)(centerY - (getTICK_MARK_LENGTH() / 2.0)));
			g.setFont(new Font("TimesRoman", Font.PLAIN, getFontSize()));
			g.drawString(roundNSigFigs(toXValue((int) i)), (int) i + 20, (int)(yCoordinatePosition - (getTICK_MARK_LENGTH() / 2.0)));
			g.setColor(Color.LIGHT_GRAY);
			g.drawLine((int) i, 0, (int) i, (int) getCanvasHeight());
		}
		double yTICK_MARK_LENGTH = (double)getCanvasHeight() / getTickMarks();
		
		if (getCanvasWidth() > centerX + (getTICK_MARK_LENGTH() / 2.0)){
			
			for (double i = centerY - yTICK_MARK_LENGTH; i > 0; i-= yTICK_MARK_LENGTH){
				g.setColor(Color.BLACK);
				g.drawLine((int)(centerX + (getTICK_MARK_LENGTH() / 2.0)), (int)i, (int)(centerX - (getTICK_MARK_LENGTH() / 2.0)),(int) i);
				g.setFont(new Font("TimesRoman", Font.PLAIN, getFontSize()));
				g.drawString(roundNSigFigs(toYValue((int)i)), (int)(xCoordinatePosition + (getTICK_MARK_LENGTH() / 2.0)),(int) i + 10);
				g.setColor(Color.LIGHT_GRAY);
				g.drawLine(0, (int) i, (int) getCanvasWidth(), (int) i);
			}
			for (double i = centerY + yTICK_MARK_LENGTH; i < getCanvasHeight(); i+= yTICK_MARK_LENGTH){
				g.setColor(Color.BLACK);
				g.drawLine((int)(centerX + (getTICK_MARK_LENGTH() / 2.0)), (int) i, (int)(centerX - (getTICK_MARK_LENGTH() / 2.0)),(int) i);
				g.setFont(new Font("TimesRoman", Font.PLAIN, getFontSize()));
				g.drawString(roundNSigFigs(toYValue((int)i)), (int)(xCoordinatePosition + (getTICK_MARK_LENGTH() / 2.0)),(int) i + 10);
				g.setColor(Color.LIGHT_GRAY);
				g.drawLine(0, (int) i, (int) getCanvasWidth(), (int) i);
			}
		}
		else{
			for (double i = centerY - yTICK_MARK_LENGTH; i > 0; i-= yTICK_MARK_LENGTH){
				g.setColor(Color.BLACK);
				g.setFont(new Font("TimesRoman", Font.PLAIN, getFontSize()));
				g.drawString(roundNSigFigs(toYValue((int)i)), (int)(xCoordinatePosition + (getTICK_MARK_LENGTH() / 2.0)),(int) i + 10);
				g.setColor(Color.LIGHT_GRAY);
				g.drawLine(0, (int) i, (int) getCanvasWidth(), (int) i);
			}
			for (double i = centerY + yTICK_MARK_LENGTH; i < getCanvasHeight(); i+= yTICK_MARK_LENGTH){
				g.setColor(Color.BLACK);
				g.setFont(new Font("TimesRoman", Font.PLAIN, getFontSize()));
				g.drawString(roundNSigFigs(toYValue((int)i)), (int)(xCoordinatePosition + (getTICK_MARK_LENGTH() / 2.0)),(int) i + 10);
				g.setColor(Color.LIGHT_GRAY);
				g.drawLine(0, (int) i, (int) getCanvasWidth(), (int) i);
			}
		}
		
		
	}










	public double toXValue(double i){
		return (((double) i) / getCanvasWidth()) * (getxBoundaryRight() - getxBoundaryLeft()) + getxBoundaryLeft();
	}
	public double toYValue(int pixelInput){
		return (((double) pixelInput) / getCanvasHeight()) * (getyBoundaryLower() - getyBoundaryUpper()) + getyBoundaryUpper();
	}
	public int toXPixel(double xValue){
		return (int)((xValue - getxBoundaryLeft()) / ((getxBoundaryRight() - getxBoundaryLeft())) * getCanvasWidth());
	}
	public int toYPixel(double yValue){
		return (int)(((yValue - getyBoundaryUpper()) / ((getyBoundaryLower() - getyBoundaryUpper()))) * getCanvasHeight());
	}



	public void drawPoint(int x, int y){
		g.drawLine(x, y, x, y);

	}
	public void drawLine(int x1, int y1, int x2, int y2){
		g.drawLine(x1, y1, x2, y2);
	}
	public void drawCircle(int x, int y, int radius){
		g.fillOval(x - radius, y - radius, radius, radius);
	}

	public String roundNSigFigs(double input){
		if (input == 0){
			return "0";
		}


		boolean isNegative = input < 0;
		input = Math.abs(input);
		int scientificNotationFactor = 0;
		while (Math.abs(input) >= 10){
			input /= 10;
			scientificNotationFactor++;
		}
		while (Math.abs(input) < 1){
			input *= 10;
			scientificNotationFactor--;
		}
		return negativeSign(isNegative) + roundLastDigit(addZeroes(Double.toString(input)).substring(0, getRoundDigits() + 2)) + "E" + scientificNotationFactor;
	}

	public String roundLastDigit(String input){
		if (Integer.parseInt(input.charAt(getRoundDigits() + 1)+"") >= 5){
			return Double.toString(Math.ceil(Double.parseDouble(input) * Math.pow(10.0, getRoundDigits()-1)) / Math.pow(10.0, getRoundDigits()-1));
		}
		else{
			return Double.toString(Math.floor(Double.parseDouble(input) * Math.pow(10.0, getRoundDigits()-1)) / Math.pow(10.0, getRoundDigits()-1));
		}
	}


	public String addZeroes(String stringInput){
		for (int i = 0; i < getRoundDigits(); i++){
			stringInput += "0";
		}
		return stringInput;
	}

	public String negativeSign(boolean isNegative){
		if (isNegative){
			return "-";
		}
		else{
			return "";
		}
	}



	/**
	 * @return the canvasWidth
	 */
	public double getCanvasWidth() {
		return canvasWidth;
	}


	/**
	 * @param canvasWidth the canvasWidth to set
	 */
	public void setCanvasWidth(double canvasWidth) {
		if (canvasWidth <= 0){
			this.canvasWidth = 0.1;
		}
		else{
			this.canvasWidth = canvasWidth;
		}
		
	}


	/**
	 * @return the canvasHeight
	 */
	public double getCanvasHeight() {
		return canvasHeight;
	}


	/**
	 * @param canvasHeight the canvasHeight to set
	 */
	public void setCanvasHeight(double canvasHeight) {
		if (canvasHeight <= 0){
			this.canvasHeight = 0.1;
		}
		else{
			this.canvasHeight = canvasHeight;
		}
	}

	/**
	 * @return the fontSize
	 */
	public int getFontSize() {
		return fontSize;
	}



	/**
	 * @return the xBoundaryRight
	 */
	public double getxBoundaryRight() {
		return xBoundaryRight;
	}


	public void setxAxes(double xBoundaryRight, double xBoundaryLeft){
		if (xBoundaryRight > xBoundaryLeft){
			this.xBoundaryRight = xBoundaryRight;
			this.xBoundaryLeft = xBoundaryLeft;
		}
		else{

		}

	}


	/**
	 * @return the xBoundaryLeft
	 */
	public double getxBoundaryLeft() {
		return xBoundaryLeft;
	}



	/**
	 * @return the yBoundaryUpper
	 */
	public double getyBoundaryUpper() {
		return yBoundaryUpper;
	}


	public void setyAxes(double yBoundaryUpper, double yBoundaryLower){
		if (yBoundaryUpper > yBoundaryLower){
			this.yBoundaryUpper = yBoundaryUpper;
			this.yBoundaryLower = yBoundaryLower;
		}
		else{

		}
	}


	/**
	 * @return the yBoundaryLower
	 */
	public double getyBoundaryLower() {
		return yBoundaryLower;
	}



	/**
	 * @return the tickMarks
	 */
	public int getTickMarks() {
		return tickMarks;
	}



	/**
	 * @return the TICK_MARK_LENGTH
	 */
	public int getTICK_MARK_LENGTH() {
		return TICK_MARK_LENGTH;
	}





	/**
	 * @return the roundDigits
	 */
	public int getRoundDigits() {
		return roundDigits;
	}

	public void setRoundDigits(int roundDigits) {
		this.roundDigits = roundDigits;
	}
	/**
	 * @return the g
	 */
	public Graphics getG() {
		return g;
	}
	/**
	 * @param g the g to set
	 */
	public void setG(Graphics g) {
		this.g = g;
	}
	/**
	 * @param tickMarks the tickMarks to set
	 */
	public void setTickMarks(int tickMarks) {
		this.tickMarks = tickMarks;
	}
	
	
	@Override
	public ArrayList<Component> commands() {
		commands = new ArrayList<Component>();
		for (int i = 0; i < equations.size(); i++){
			Equation currentEquation = equations.get(i);
			JButton jb = new JButton(currentEquation.getName() + ": " + currentEquation.getExpression());
			
			jb.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					p.newPopupCommands(currentEquation);
				}
			});
			//jb.setPreferredSize(new Dimension(200, 100));
			
			commands.add(jb);
		}
		
		
		
		JButton newEquationButton = new JButton("New Equation");
		newEquationButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				newEquation();
				p.newPopupCommands(wld);
				repaint();
				
			}
		});
		//newEquationButton.setPreferredSize(new Dimension(200, 100));
		
		commands.add(newEquationButton);
		
		JButton redrawAll = new JButton("Redraw All");
		redrawAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				((JFrame)SwingUtilities.getWindowAncestor(wld)).setResizable(false);
				for (int i = 0; i < equations.size(); i++){
					if (equations.get(i).isComplex()){
						equations.get(i).generateComplexFunction();
					}
					if (equations.get(i).isComplexParametric()){
						equations.get(i).generateComplexParametric();
					}
				}
				((JFrame)SwingUtilities.getWindowAncestor(wld)).setResizable(true);
				repaint();
			}
		});
		
		commands.add(redrawAll);
		
		JTextField[] textComponents = new JTextField[5];
		String[] initialValues = new String[5]; 
		String[] prompts = new String[5];
		initialValues[0]=roundNSigFigsNoScientific(getxBoundaryRight())+"";
		prompts[0]="X Right: ";
		initialValues[1]=roundNSigFigsNoScientific(getxBoundaryLeft())+""; 
		prompts[1]="X Left: ";
		initialValues[2]=roundNSigFigsNoScientific(getyBoundaryUpper())+"";
		prompts[2]="Y Upper: ";
		initialValues[3]=roundNSigFigsNoScientific(getyBoundaryLower())+""; 
		prompts[3]="Y Lower: ";
		initialValues[4]=roundNSigFigsNoScientific(getRoundDigits())+""; 
		prompts[4]="Round Digits: ";


		for (int i = 0; i < textComponents.length; i++){
			textComponents[i]=setUpTextField(prompts[i], initialValues[i],  i);
		}




		JButton set = new JButton("Set");

		set.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				try{
					wld.setxAxes(Double.parseDouble(textComponents[0].getText().substring(prompts[0].length())), Double.parseDouble(textComponents[1].getText().substring(prompts[1].length())));
					wld.setyAxes(Double.parseDouble(textComponents[2].getText().substring(prompts[2].length())), Double.parseDouble(textComponents[3].getText().substring(prompts[3].length())));
					wld.setRoundDigits(Integer.parseInt(textComponents[4].getText().substring(prompts[4].length())));
					
				}
				catch (Exception ex){
					
				}
				wld.repaint();
			}
		});
		getCommands().add(set);
		
		
		return commands;
	}
	
	
	public JTextField setUpTextField(String prompt, String initialValue, int position){

		JTextField j = new JTextField(prompt + initialValue);

		//Adds a KeyListener to the TextField so that when return key is pressed equation is changed
		j.addKeyListener(new KeyListener(){

			@Override
			public void keyTyped(KeyEvent e) {
				JTextField j = ((JTextField) e.getSource());
				try{

					if (!(j.getText().substring(0,prompt.length()).equals(prompt))){
						j.setText(prompt);
					}
				}
				catch (Exception ex){
					j.setText(prompt);
					//System.out.println("Error: " + ex);
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {

			}
		});
		getCommands().add(j);
		return j;

	}
	/**
	 * @return the equations
	 */
	public ArrayList<Equation> getEquations() {
		return equations;
	}
	/**
	 * @param equations the equations to set
	 */
	public void setEquations(ArrayList<Equation> equations) {
		this.equations = equations;
	}
	/**
	 * @return the p
	 */
	public Popup getP() {
		return p;
	}
	/**
	 * @param p the p to set
	 */
	public void setP(Popup p) {
		this.p = p;
	}
	/**
	 * @return the commands
	 */
	public ArrayList<Component> getCommands() {
		return commands;
	}
	/**
	 * @param commands the commands to set
	 */
	public void setCommands(ArrayList<Component> commands) {
		this.commands = commands;
	}

	public double roundNSigFigsNoScientific(double input){
		if (input == 0){
			return 0;
		}
		boolean isNegative = input < 0;
		input = Math.abs(input);
		int scientificNotationFactor = 0;
		while (Math.abs(input) >= 10){
			input /= 10;
			scientificNotationFactor++;
		}
		while (Math.abs(input) < 1){
			input *= 10;
			scientificNotationFactor--;
		}
		return Double.parseDouble(negativeSign(isNegative) + roundLastDigit(addZeroes(Double.toString(input)).substring(0, wld.getRoundDigits() + 2))) * Math.pow(10, scientificNotationFactor) ;
	}

}

