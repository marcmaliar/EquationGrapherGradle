package eg;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Equation implements Commandable{

	private String name;
	private String expression;
	private Color color;
	private ArrayList<Double> xCoordinates = new ArrayList<Double>();
	private ArrayList<Double> yCoordinates = new ArrayList<Double>();
	private Equation eq;
	private EquationWorld wld;
	private int roundDigits;
	private int testNumbers = 1000;
	private boolean isComplex = false;
	private boolean isComplexParametric = false;
	private String xParametric = "";
	private String yParametric = "";
	private double leftParametricBoundary = 0;
	private double rightParametricBoundary = 2 * Math.PI;
	private Popup p;

	ArrayList<Component> commands = new ArrayList<Component>();
	private JTextField[] textComponents;





	public Equation(String name, String expression, EquationWorld wld, Popup p){
		setName(name);
		setExpression(expression);
		setWld(wld);
		setRoundDigits(getWld().getRoundDigits());
		newRandomColor();
		setP(p);
		setEq(this);
		setUpComponents();
	}


	public void newRandomColor(){
		Random rand = new Random();
		float r = rand.nextFloat();
		float g = rand.nextFloat();
		float b = rand.nextFloat();
		setColor(new Color(r,g,b).darker());
	}


	public  void drawFunction(){
		wld.getG().setColor(getColor());


		Graphics2D g2 = (Graphics2D) wld.getG();
		g2.setStroke(new BasicStroke(5));
		
		setComplexParametric(false);
		setComplex(false);

		if (!xParametric.isEmpty()  && !yParametric.isEmpty()){

			String leftSideX = getxParametric().substring(0, getxParametric().indexOf('='));
			String rightSideX = getxParametric().substring(getxParametric().indexOf('=') + 1, getxParametric().length());
			String leftSideY = getyParametric().substring(0, getyParametric().indexOf('='));
			String rightSideY = getyParametric().substring(getyParametric().indexOf('=') + 1, getyParametric().length());

			String xExpression = "";
			String yExpression = "";

			if (leftSideX.equals("x") && !rightSideX.contains("x")){
				xExpression = rightSideX;
			}
			else if (rightSideX.equals("x") && !leftSideX.contains("x")){
				xExpression = leftSideX;
			}
			if (leftSideY.equals("y") && !rightSideY.contains("y")){
				yExpression = rightSideY;
			}
			else if (rightSideY.equals("y") && !leftSideY.contains("y")){
				yExpression = leftSideY;
			}

			if (xExpression != "" || yExpression != ""){
				drawParametric(xExpression, yExpression);
			}
			else{
				setComplexParametric(true);
				for (int i = 0; i < getxCoordinates().size(); i++){
					wld.drawCircle(wld.toXPixel(getxCoordinates().get(i)), wld.toYPixel(getyCoordinates().get(i)), 3);
				}
			}

		}
		else{

			String leftSide = getExpression().substring(0, getExpression().indexOf('='));
			String rightSide = getExpression().substring(getExpression().indexOf('=') + 1, getExpression().length());

			if (leftSide.equals("y") && !rightSide.contains("y")){
				drawSimpleFunction(rightSide);
			}
			else if (rightSide.equals("y") && !leftSide.contains("y")){
				drawSimpleFunction(leftSide);
			}
			else if (leftSide.equals("r") && !rightSide.contains("r") && !rightSide.contains("x") && !rightSide.contains("y")){
				drawParametric("(" + rightSide.replace("theta", "t") + ")*COS(t*180/Pi)","(" + rightSide.replace("theta", "t") + ")*SIN(t*180/Pi)");
			}
			else if (rightSide.equals("r") && !leftSide.contains("r") && !leftSide.contains("x") && !leftSide.contains("y")){
				drawParametric("(" + leftSide.replace("theta", "t") + ")*COS(t*180/Pi)" , "(" + leftSide.replace("theta", "t") + ")*SIN(t*180/Pi)");
			}
			else{
				setComplex(true);
				for (int i = 0; i < getxCoordinates().size(); i++){
					wld.drawCircle(wld.toXPixel(getxCoordinates().get(i)), wld.toYPixel(getyCoordinates().get(i)), 3);
				}
			}
		}
	}

	public void drawSimpleFunction(String expression){
		double previousXValue, previousYValue, currentXValue, currentYValue, slope, loopStep = 1;
		try{
			previousXValue = wld.toXValue(-1);
			previousYValue = evaluate(expression, "x", previousXValue);
			currentXValue = wld.getxBoundaryLeft();
			currentYValue = evaluate(expression, "x", currentXValue);
		}
		catch(Exception ex){
			previousXValue = (Math.random() - 0.5)*2;
			previousYValue = (Math.random() - 0.5)*2;

			currentXValue = (Math.random() - 0.5)*2;
			currentYValue = (Math.random() - 0.5)*2;
		}
		


		for (double i = 0; i < wld.getCanvasWidth(); i += Math.abs(loopStep)){
			try{
				currentXValue = wld.toXValue(i);
				currentYValue = evaluate(expression, "x", currentXValue);

				slope = Math.abs((currentYValue - previousYValue) / (currentXValue - previousXValue));


				loopStep = ((1/(slope+1))+0.01);


				if (( currentYValue > wld.getyBoundaryLower() && currentYValue < wld.getyBoundaryUpper())){
					wld.drawLine((int)i, wld.toYPixel(currentYValue), (int)(i-loopStep), wld.toYPixel(previousYValue));
					//wld.drawCircle((int)i, wld.toYPixel(currentYValue), 5);
				}
				else if (currentYValue > wld.getyBoundaryUpper() && i - loopStep == wld.toXPixel(previousXValue)){
					wld.drawLine((int)i, (int)wld.getyBoundaryUpper(), (int)(i-loopStep), wld.toYPixel(previousYValue));
				}
				else if (currentYValue < wld.getyBoundaryLower() && i - loopStep == wld.toXPixel(previousXValue)){
					wld.drawLine((int)i, (int)wld.getyBoundaryLower(), (int)(i-loopStep), wld.toYPixel(previousYValue));
				}
				
				previousXValue = currentXValue;
				previousYValue = currentYValue;
				


			}
			catch(Exception ex){
				//System.out.println("Error: " + ex);
			}
		}
	}

	public void generateComplexParametric(){

		xCoordinates = new ArrayList<Double>();
		yCoordinates = new ArrayList<Double>();



		String leftSideX = getxParametric().substring(0, getxParametric().indexOf('='));
		String rightSideX = getxParametric().substring(getxParametric().indexOf('=') + 1, getxParametric().length());
		String leftSideY = getyParametric().substring(0, getyParametric().indexOf('='));
		String rightSideY = getyParametric().substring(getyParametric().indexOf('=') + 1, getyParametric().length());

		double xBoundaryRight = wld.getxBoundaryRight();
		double xBoundaryLeft = wld.getxBoundaryLeft();



		double tFactor = 0;
		double totalT = getRightParametricBoundary() - getLeftParametricBoundary();

		if (xBoundaryRight > 0 && xBoundaryLeft < 0){
			tFactor = getRightParametricBoundary() - getLeftParametricBoundary();
			totalT = (getRightParametricBoundary() - getLeftParametricBoundary()) * 2;
			drawXBoundaryRight(leftSideX, rightSideX, leftSideY, rightSideY, totalT, 0);
			drawXBoundaryLeft(leftSideX, rightSideX, leftSideY, rightSideY, totalT, tFactor);
		}
		else if (xBoundaryRight > 0){
			drawXBoundaryRight(leftSideX, rightSideX, leftSideY, rightSideY, totalT, 0);
		}
		else if (xBoundaryLeft < 0){
			drawXBoundaryLeft(leftSideX, rightSideX, leftSideY, rightSideY, totalT, 0);
		}



	}

	public void drawXBoundaryRight(String leftSideX, String rightSideX, String leftSideY, String rightSideY,  double totalT, double tFactor){
		JFrame parent = ((JFrame) SwingUtilities.getWindowAncestor(wld));  

		double xBoundaryRight = wld.getxBoundaryRight();
		double yTestBoundaryLower = Math.pow(10.0, Math.floor(Math.log10(xBoundaryRight)));
		if (yTestBoundaryLower == xBoundaryRight){
			yTestBoundaryLower /= 10;
		}
		double testNumbers = ((xBoundaryRight - yTestBoundaryLower)/(yTestBoundaryLower * 10.0)) * getTestNumbers();

		for (double i = getLeftParametricBoundary(); i < getRightParametricBoundary(); i += (getRightParametricBoundary()-getLeftParametricBoundary())/getTestNumbers()){
			parent.setTitle(roundNSigFigs(100 * (i + tFactor) / (totalT)) + "% of the way done.");
			recursiveTestNumbersXParametric(i, leftSideX, rightSideX, leftSideY, rightSideY, yTestBoundaryLower, xBoundaryRight, testNumbers);
		}

		parent.setTitle("2D Grapher");
	}

	public void drawXBoundaryLeft(String leftSideX, String rightSideX, String leftSideY, String rightSideY, double totalT, double tFactor){
		JFrame parent = ((JFrame) SwingUtilities.getWindowAncestor(wld));  

		double xBoundaryLeft = wld.getxBoundaryLeft();
		double rightXTestBoundary = -Math.pow(10.0, Math.floor(Math.log10(-xBoundaryLeft)));
		if (rightXTestBoundary == xBoundaryLeft){
			rightXTestBoundary /= 10;
		}
		double testNumbers = ((xBoundaryLeft - rightXTestBoundary)/(rightXTestBoundary * 10.0)) * getTestNumbers();
		for (double i = getLeftParametricBoundary(); i < getRightParametricBoundary(); i += (getRightParametricBoundary()-getLeftParametricBoundary())/getTestNumbers()){
			parent.setTitle(roundNSigFigs(100 * (i + tFactor) / (totalT)) + "% of the way done.");
			recursiveTestNumbersXParametric(i, leftSideX, rightSideX, leftSideY, rightSideY, rightXTestBoundary, xBoundaryLeft, testNumbers);
		}
		parent.setTitle("2D Grapher");
	}


	public void recursiveTestNumbersXParametric(double tValue, String leftSideX, String rightSideX, String leftSideY, String rightSideY, double bound1, double bound2, double testNumbers){


		if (!(wld.toXPixel(bound1) == wld.toXPixel(bound2))){

			for (double xCoor = bound1; Math.abs(xCoor) < Math.abs(bound2); xCoor+=(Math.abs(bound2)-Math.abs(bound1)) * ((bound2 - bound1)/Math.abs(bound1 - bound2)) / testNumbers){

				try{
					double leftXDouble = evaluate(leftSideX, "t", tValue, "x", xCoor);
					double rightXDouble = evaluate(rightSideX, "t", tValue, "x", xCoor);
					if (roundNSigFigs(leftXDouble).equals(roundNSigFigs(rightXDouble))){

						double yBoundaryUpper = wld.getyBoundaryUpper();
						double yBoundaryLower = wld.getyBoundaryLower();

						double lowerYTestBoundary = Math.pow(10.0, Math.floor(Math.log10(yBoundaryUpper)));
						if (lowerYTestBoundary == yBoundaryUpper){
							lowerYTestBoundary /= 10;
						}
						double testNumbersYUpper = ((yBoundaryUpper - lowerYTestBoundary)/(lowerYTestBoundary * 10.0)) * getTestNumbers();

						double upperYTestBoundary = -Math.pow(10.0, Math.floor(Math.log10(-yBoundaryLower)));
						if (upperYTestBoundary == yBoundaryLower){
							upperYTestBoundary /= 10;
						}
						double testNumbersYLower = ((yBoundaryLower - upperYTestBoundary)/(upperYTestBoundary * 10.0)) * getTestNumbers();

						recursiveTestNumbersYParametric(tValue, xCoor, leftSideY, rightSideY, upperYTestBoundary, yBoundaryLower, testNumbersYLower);
						recursiveTestNumbersYParametric(tValue, xCoor, leftSideY, rightSideY, lowerYTestBoundary, yBoundaryUpper, testNumbersYUpper);
					}
				}
				catch (Exception ex){
					//System.out.println("Error: " + ex);
				}
			}
			recursiveTestNumbersXParametric(tValue, leftSideX, rightSideX, leftSideY, rightSideY, bound1 / 10.0, bound1, getTestNumbers());
		}
	}
	public void recursiveTestNumbersYParametric(double tValue, double xCoor, String leftSide, String rightSide, double bound1, double bound2, double testNumbers){

		if (!(wld.toYPixel(bound1) == wld.toYPixel(bound2))){

			for (double yCoor = bound1; Math.abs(yCoor) < Math.abs(bound2); yCoor+=(Math.abs(bound2)-Math.abs(bound1)) * ((bound2 - bound1)/Math.abs(bound1 - bound2)) / testNumbers){



				try{
					double leftDouble = evaluate(leftSide, "t", tValue, "y", yCoor);
					double rightDouble = evaluate(rightSide, "t", tValue, "y", yCoor);
					if (roundNSigFigs(leftDouble).equals(roundNSigFigs(rightDouble))){
						getxCoordinates().add(xCoor);
						getyCoordinates().add(yCoor);
					}
				}
				catch (Exception ex){
					//System.out.println("Error: " + ex);
				}
			}
			recursiveTestNumbersYParametric(tValue, xCoor, leftSide, rightSide, bound1 / 10.0, bound1, getTestNumbers());
		}

	}




	public void drawParametric(String xExpression, String yExpression){
		double previousXValue, previousYValue, currentXValue, currentYValue, slope, loopStep = 1;
		boolean doIt = true;
		try{
			previousXValue = evaluate(xExpression, "t", getLeftParametricBoundary()-0.01);
			previousYValue = evaluate(yExpression, "t", getLeftParametricBoundary()-0.01);

			currentXValue = evaluate(xExpression, "t", getLeftParametricBoundary());
			currentYValue = evaluate(yExpression, "t", getLeftParametricBoundary());
		}
		catch(Exception ex){
			previousXValue = (Math.random() - 0.5)*2;
			previousYValue = (Math.random() - 0.5)*2;

			currentXValue = (Math.random() - 0.5)*2;
			currentYValue = (Math.random() - 0.5)*2;

			doIt = false;
		}
		
		boolean continuous = true;
		for (double i = getLeftParametricBoundary(); i < getRightParametricBoundary(); i += Math.abs(loopStep)){
			if (!continuous) System.out.println(continuous);
			try{
				currentXValue = evaluate(xExpression, "t", i);
				currentYValue = evaluate(yExpression, "t", i);

				slope = Math.abs((currentYValue - previousYValue) / (currentXValue - previousXValue));


				loopStep = ((1/(slope+1))+0.01);


				if ((currentYValue > wld.getyBoundaryLower() && currentYValue < wld.getyBoundaryUpper()) && doIt){
					wld.drawLine((int) wld.toXPixel(currentXValue), wld.toYPixel(currentYValue), (int) wld.toXPixel(previousXValue), wld.toYPixel(previousYValue));
					//wld.drawCircle((int)i, wld.toYPixel(currentYValue), 5);
				}
				else if (currentYValue > wld.getyBoundaryUpper() && continuous){
					wld.drawLine((int)i, (int)wld.getyBoundaryUpper(), (int)(i-loopStep), wld.toYPixel(previousYValue));
				}
				else if (currentYValue < wld.getyBoundaryLower() && continuous){
					wld.drawLine((int)i, (int)wld.getyBoundaryLower(), (int)(i-loopStep), wld.toYPixel(previousYValue));
				}
				else if (currentXValue > wld.getxBoundaryRight() && continuous){
					wld.drawLine((int)i, (int)wld.getxBoundaryRight(), (int)(i-loopStep), wld.toYPixel(previousYValue));
				}
				else if (currentXValue < wld.getxBoundaryLeft() && continuous){
					wld.drawLine((int)i, (int)wld.getxBoundaryLeft(), (int)(i-loopStep), wld.toYPixel(previousYValue));
				}
				previousXValue = currentXValue;
				previousYValue = currentYValue;
				doIt = true;
				continuous = true;

			}
			catch(Exception ex){
				continuous = false;
				//System.out.println("Error: " + ex);
			}
		}
	}

	public double evaluate(String expression, String variable, double value){
		return new Expression(expression).with(variable, Double.toString(value)).eval().doubleValue();
	}
	public double evaluate(String expression, String variable1, double value1, String variable2, double value2){
		return new Expression(expression).with(variable1, Double.toString(value1)).with(variable2, Double.toString(value2)).eval().doubleValue();
	}




	public void generateComplexFunction(){

		xCoordinates = new ArrayList<Double>();
		yCoordinates = new ArrayList<Double>();

		String leftSide = getExpression().substring(0, getExpression().indexOf('='));
		String rightSide = getExpression().substring(getExpression().indexOf('=') + 1, getExpression().length());

		JFrame parent = ((JFrame) SwingUtilities.getWindowAncestor(getWld()));  



		double yBoundaryUpper = getWld().getyBoundaryUpper();
		double yBoundaryLower = getWld().getyBoundaryLower();

		if (yBoundaryUpper > 0 && yBoundaryLower < 0){
			double yTestBoundaryLower = Math.pow(10.0, Math.floor(Math.log10(yBoundaryUpper)));

			if (yTestBoundaryLower == yBoundaryUpper){
				yTestBoundaryLower /= 10;
			}
			double testNumbersUpper = ((yBoundaryUpper - yTestBoundaryLower)/(yTestBoundaryLower * 10.0)) * getTestNumbers();


			double yTestBoundaryUpper = -Math.pow(10.0, Math.floor(Math.log10(-yBoundaryLower)));

			if (yTestBoundaryUpper == yBoundaryLower){
				yTestBoundaryUpper /= 10;
			}
			double testNumbersLower = ((yBoundaryLower - yTestBoundaryUpper)/(yTestBoundaryUpper * 10.0)) * getTestNumbers();

			for (double i = wld.getxBoundaryLeft(); i < wld.getxBoundaryRight(); i+= (wld.getxBoundaryRight()-wld.getxBoundaryLeft())/getTestNumbers()){
				parent.setTitle(getName() + ": " + wld.roundNSigFigs(100.0*wld.toXPixel(i)/wld.getCanvasWidth()) + "% of the way done.");
				//System.out.println(i);
				recursiveTestNumbers(i, leftSide, rightSide, yTestBoundaryLower, yBoundaryUpper, (int)testNumbersUpper);
				recursiveTestNumbers(i, leftSide, rightSide, yTestBoundaryUpper, yBoundaryLower, (int)testNumbersLower);
			}

		}
		else if (yBoundaryUpper> 0){
			double yTestBoundaryLower = Math.pow(10.0, Math.floor(Math.log10(yBoundaryUpper)));

			if (yTestBoundaryLower == yBoundaryUpper){
				yTestBoundaryLower /= 10;
			}
			double testNumbersUpper = ((yBoundaryUpper - yTestBoundaryLower)/(yTestBoundaryLower * 10.0)) * getTestNumbers();


			for (double i = wld.getxBoundaryLeft(); i < wld.getxBoundaryRight(); i+= (wld.getxBoundaryRight()-wld.getxBoundaryLeft())/getTestNumbers()){
				parent.setTitle(getName() + ": " + wld.roundNSigFigs(100.0*wld.toXPixel(i)/wld.getCanvasWidth()) + "% of the way done.");
				recursiveTestNumbers(i, leftSide, rightSide, yTestBoundaryLower, yBoundaryUpper, (int)testNumbersUpper);
			}

		}
		else{
			double yTestBoundaryUpper = -Math.pow(10.0, Math.floor(Math.log10(-yBoundaryLower)));

			if (yTestBoundaryUpper == yBoundaryLower){
				yTestBoundaryUpper /= 10;
			}
			double testNumbersLower = ((yBoundaryLower - yTestBoundaryUpper)/(yTestBoundaryUpper * 10.0)) * getTestNumbers();
			for (double i = wld.getxBoundaryLeft(); i < wld.getxBoundaryRight(); i+= (wld.getxBoundaryRight()-wld.getxBoundaryLeft())/getTestNumbers()){
				parent.setTitle(getName() + ": " + wld.roundNSigFigs(100.0*wld.toXPixel(i)/wld.getCanvasWidth()) + "% of the way done.");
				recursiveTestNumbers(i, leftSide, rightSide, yTestBoundaryUpper, yBoundaryLower, (int)testNumbersLower);
			}
		}

		parent.setTitle("2D Grapher");


		wld.repaint();

	}





	public void recursiveTestNumbers(double xValue, String leftSide, String rightSide, double bound1, double bound2, int testNumbers){


		if (!(wld.toYPixel(bound1) == wld.toYPixel(bound2))){
			for (double i = bound1; Math.abs(i) < Math.abs(bound2); i+=(Math.abs(bound2)-Math.abs(bound1)) * ((bound2 - bound1)/Math.abs(bound1 - bound2)) / testNumbers){
				try{

					double leftDouble = evaluate(leftSide, "x", xValue, "y", i);
					double rightDouble = evaluate(rightSide, "x", xValue, "y", i);
					if (roundNSigFigs(leftDouble).equals(roundNSigFigs(rightDouble))){
						xCoordinates.add(xValue);
						yCoordinates.add(i);
					}
				}
				catch (Exception ex){
					//System.out.println("Error: " + ex);
				}
			}

			recursiveTestNumbers(xValue, leftSide, rightSide, bound1/10.0, bound1, getTestNumbers());
		}
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
		return negativeSign(isNegative) + roundLastDigit(addZeroes(Double.toString(input)).substring(0, wld.getRoundDigits() + 2)) + "E" + scientificNotationFactor;
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

	public String roundLastDigit(String input){
		if (Integer.parseInt(input.charAt(getRoundDigits() + 1)+"") >= 5){
			return Double.toString(Math.ceil(Double.parseDouble(input) * Math.pow(10.0, getRoundDigits()-1)) / Math.pow(10.0, getRoundDigits()-1));
		}
		else{
			return Double.toString(Math.floor(Double.parseDouble(input) * Math.pow(10.0, getRoundDigits()-1)) / Math.pow(10.0, getRoundDigits()-1));
		}
	}


	public String addZeroes(String stringInput){
		for (int i = 0; i < wld.getRoundDigits(); i++){
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
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the expression
	 */
	public String getExpression() {
		return expression;
	}

	/**
	 * @param expression the expression to set
	 */
	public void setExpression(String expression) {
		this.expression = expression;
	}

	/**
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * @return the xCoordinates
	 */
	public ArrayList<Double> getxCoordinates() {
		return xCoordinates;
	}

	/**
	 * @param xCoordinates the xCoordinates to set
	 */
	public void setxCoordinates(ArrayList<Double> xCoordinates) {
		this.xCoordinates = xCoordinates;
	}

	/**
	 * @return the yCoordinates
	 */
	public ArrayList<Double> getyCoordinates() {
		return yCoordinates;
	}

	/**
	 * @param yCoordinates the yCoordinates to set
	 */
	public void setyCoordinates(ArrayList<Double> yCoordinates) {
		this.yCoordinates = yCoordinates;
	}




	/**
	 * @return the roundDigits
	 */
	public int getRoundDigits() {
		return roundDigits;
	}




	/**
	 * @param roundDigits the roundDigits to set
	 */
	public void setRoundDigits(int roundDigits) {
		this.roundDigits = roundDigits;
	}




	/**
	 * @return the testNumbers
	 */
	public int getTestNumbers() {
		return testNumbers;
	}




	/**
	 * @return the isComplex
	 */
	public boolean isComplex() {
		return isComplex;
	}




	/**
	 * @param isComplex the isComplex to set
	 */
	public void setComplex(boolean isComplex) {
		this.isComplex = isComplex;
	}




	/**
	 * @param testNumbers the testNumbers to set
	 */
	public void setTestNumbers(int testNumbers) {
		this.testNumbers = testNumbers;
	}


	/**
	 * @return the xParametric
	 */
	public String getxParametric() {
		return xParametric;
	}


	/**
	 * @param xParametric the xParametric to set
	 */
	public void setxParametric(String xParametric) {
		this.xParametric = xParametric;
	}


	/**
	 * @return the yParametric
	 */
	public String getyParametric() {
		return yParametric;
	}


	/**
	 * @param yParametric the yParametric to set
	 */
	public void setyParametric(String yParametric) {
		this.yParametric = yParametric;
	}


	/**
	 * @return the leftParametricBoundary
	 */
	public double getLeftParametricBoundary() {
		return leftParametricBoundary;
	}


	/**
	 * @param leftParametricBoundary the leftParametricBoundary to set
	 */
	public void setLeftParametricBoundary(double leftParametricBoundary) {
		this.leftParametricBoundary = leftParametricBoundary;
	}


	/**
	 * @return the rightParametricBoundary
	 */
	public double getRightParametricBoundary() {
		return rightParametricBoundary;
	}


	/**
	 * @param rightParametricBoundary the rightParametricBoundary to set
	 */
	public void setRightParametricBoundary(double rightParametricBoundary) {
		this.rightParametricBoundary = rightParametricBoundary;
	}


	/**
	 * @return the isComplexParametric
	 */
	public boolean isComplexParametric() {
		return isComplexParametric;
	}


	/**
	 * @param isComplexParametric the isComplexParametric to set
	 */
	public void setComplexParametric(boolean isComplexParametric) {
		this.isComplexParametric = isComplexParametric;
	}


	public void setUpComponents(){



		JButton back = new JButton("Back");

		back.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getP().newPopupCommands(getWld());
				wld.repaint();
			}
		});

		getCommands().add(back);


		JButton delete = new JButton("Delete");

		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ArrayList<Equation> equations = wld.getEquations();
				equations.remove(wld.findEquation(getName()));
				wld.setEquations(equations);
				getP().newPopupCommands(getWld());
				wld.repaint();
			}
		});

		getCommands().add(delete);



		setTextComponents(new JTextField[8]); 
		String[] initialValues = new String[8]; 
		String[] prompts = new String[8];
		initialValues[1]=getExpression(); 
		prompts[1]="Equation: ";
		initialValues[0]=getName();
		prompts[0]="Name: ";
		initialValues[2]=getxParametric();
		prompts[2]="X Parametric: ";
		initialValues[3]=getyParametric(); 
		prompts[3]="Y Parametric: ";
		initialValues[4]=roundNSigFigsNoScientific(getLeftParametricBoundary())+""; 
		prompts[4]="Left Boundary: ";
		initialValues[5]=roundNSigFigsNoScientific(getRightParametricBoundary())+""; 
		prompts[5]="Right Boundary: ";
		initialValues[6]=roundNSigFigsNoScientific(getTestNumbers())+"";
		prompts[6]="Test Numbers: ";
		initialValues[7]=roundNSigFigsNoScientific(getRoundDigits())+"";
		prompts[7]="Round Digits: ";


		for (int i = 0; i < getTextComponents().length; i++){
			setUpTextField(prompts[i], initialValues[i],  i);
		}




		JButton set = new JButton("Set");

		set.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					getEq().setName(textComponents[0].getText().substring(prompts[0].length()));
					try{
						setExpression(textComponents[1].getText().substring(prompts[1].length()));
					}
					catch (Exception ex){
						setxParametric(textComponents[2].getText().substring(prompts[2].length()));
						setyParametric(textComponents[3].getText().substring(prompts[3].length()));	
					}
					try{
						setxParametric(textComponents[2].getText().substring(prompts[2].length()));
						setyParametric(textComponents[3].getText().substring(prompts[3].length()));
					}
					catch (Exception ex){
						setExpression(textComponents[1].getText().substring(prompts[1].length()));	
					}
					setLeftParametricBoundary(Double.parseDouble(textComponents[4].getText().substring(prompts[4].length())));
					setRightParametricBoundary(Double.parseDouble(textComponents[5].getText().substring(prompts[5].length())));
					setTestNumbers(Integer.parseInt(textComponents[6].getText().substring(prompts[6].length())));
					setRoundDigits(Integer.parseInt(textComponents[7].getText().substring(prompts[7].length())));
					getWld().repaint();
				}
				catch (Exception ex){
				}
				getWld().repaint();
			}
		});
		getCommands().add(set);

	}






	public void setUpTextField(String prompt, String initialValue, int position){

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
		getTextComponents()[position]=j;

	}



	@Override
	public ArrayList<Component> commands() {
		return commands;
	}


	/**
	 * @return the eq
	 */
	public Equation getEq() {
		return eq;
	}


	/**
	 * @param eq the eq to set
	 */
	public void setEq(Equation eq) {
		this.eq = eq;
	}


	/**
	 * @return the wld
	 */
	public EquationWorld getWld() {
		return wld;
	}


	/**
	 * @param wld the wld to set
	 */
	public void setWld(EquationWorld wld) {
		this.wld = wld;
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


	/**
	 * @return the textComponents
	 */
	public JTextField[] getTextComponents() {
		return textComponents;
	}


	/**
	 * @param textComponents the textComponents to set
	 */
	public void setTextComponents(JTextField[] textComponents) {
		this.textComponents = textComponents;
	}

}
