
package application;

import java.text.NumberFormat;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;


public class SampleController {
	
	//The type of format in the Model are currency
	//$100.10
	private static final NumberFormat CURRENCY = NumberFormat.getCurrencyInstance();
	
	//The type of format in the Model are percentage 
	//0.06 -> 6%	
	private static final NumberFormat PERCENTAGE = NumberFormat.getPercentInstance();
	
	
	//Loan Period
	private Integer loanPeriod = 12;


    @FXML
    private Slider periodSlider;

    @FXML
    private TextField ageTF;

    @FXML
    private TextField downpayTF;

    @FXML
    private TextField interestTF;

    @FXML
    private TextField loanFreTF;

    @FXML
    private TextField monthlyPaymentTF;

    @FXML
    private TextField priceTF;

    @FXML
    private TextField typeTF;


    //This function is to clear up all the text field to their default
    @FXML
    void clearBtn(ActionEvent event) {
    	
    	//Type of Vehicle
    	typeTF.setText(null);
    	
    	//Type of age of vehicle
    	ageTF.setText(null);
    	
    	//Price of Vehicle
    	priceTF.setText(null);
    	
    	//Down Payment
    	downpayTF.setText(null);
    	
    	//Interest Rate
    	interestTF.setText(null);
    	
    	//Period Slider
    	periodSlider.setValue(loanPeriod);
    	
    	//Loan Payment Frequency
    	loanFreTF.setText(null);
    	
    	//per time's loan payment
    	monthlyPaymentTF.setText(null);

    }
    
    //Calculation Function
    // P is the principle, n is the number payments per year, t is the total year of loan
    // r is the interest rate per year
    public static BigDecimal calculationFunction(BigDecimal p, Integer n, BigDecimal t, BigDecimal r) {
    	
//    	System.out.println("====================Jumping in Calaculation Function====================" );
//    	System.out.println(
//    			"Principle: " + p + 
//    			", Number of Payment Per year: " + n + 
//    			", Years: " + t + ", "
//    			+ "Interest Rate: " + r 
//    	);
    	
        BigDecimal monthlyInterestRate = r.divide(BigDecimal.valueOf(n), 20, RoundingMode.DOWN);
//        System.out.println("Monthly Interest Rate: " + monthlyInterestRate);
        
        BigDecimal numberOfPayments = t.multiply(BigDecimal.valueOf(n));
//        System.out.println("Number Of Payments: " + numberOfPayments);
        
        BigDecimal m = BigDecimal.ONE.add(monthlyInterestRate).pow(numberOfPayments.intValue());
        
        BigDecimal numerator = monthlyInterestRate.multiply(m);
//        System.out.println("Nnumerator: " + numerator);
        
        BigDecimal denominator = m.subtract(BigDecimal.ONE);
//        System.out.println("Denominator: " + denominator);
        
        BigDecimal ratio = numerator.divide(denominator, 20, RoundingMode.DOWN);
        
        BigDecimal monthlyPayment = p.multiply(ratio);
        monthlyPayment = monthlyPayment.setScale(2, RoundingMode.HALF_UP);
        
//        System.out.println(" ====================Jump Out Calculation Function==================== \n\n" );
        
        return monthlyPayment;
    }

    @FXML
    void getResultBtn(ActionEvent event) {
    	try {
	    	// Handling the text field of price, down payment and interest has been entered
	    	if(priceTF.getText().isEmpty() || downpayTF.getText().isEmpty() || interestTF.getText().isEmpty()) {
	    		throw new Exception("Please Enter Proper Number");
	    	}
	    	
	    	// Handling they are all Numbers
	    	// If totalAmount and downPayment fail to read BigDecimal, throw new Exception
		    BigDecimal totalAmount, downPayment;
		    try {
		        totalAmount = new BigDecimal(priceTF.getText().replaceAll("[^0-9.]", ""));
		        downPayment = new BigDecimal(downpayTF.getText().replaceAll("[^0-9.]", ""));
		    } catch (NumberFormatException e) {
		        throw new Exception("Please enter valid numbers for price and down payment");
		    }
		    
		    // Get the principle = totalAmount - downPayment
		    BigDecimal principle = totalAmount.subtract(downPayment);
		    if(principle.compareTo(BigDecimal.ZERO) == -1) {
		    	throw new Exception("Your Downpay is more than the car price");
		    }
//		    System.out.println("Principle: " + principle);
	    	
	    	// Handling the type of car is either Car or Truck
	    	if(typeTF.getText().isEmpty()) {
	    		throw new Exception("Please Enter Your Car Type");
	    	}
	    	String vehicleType = typeTF.getText().toLowerCase();
	    	if(!vehicleType.equals("car") && !vehicleType.equals("truck")) {
	    		throw new Exception("Please Enter Proper Type of Car");
	    	}
	    	

	    	// Handling the age of the Vehicle is either New or Used
	    	if(ageTF.getText().isEmpty()) {
	    		throw new Exception("Please Enter You Car Age");
	    	}
	    	String vehicleAge = ageTF.getText().toLowerCase();
	    	if(!vehicleAge.equals("new") && !vehicleAge.equals("used")) {
	    		throw new Exception("Please Enter Proper Age of Car");
	    	}
	    	
	
	    	//Handling the interest: BigDecimal(0.06) => 6% expressed as a decimal
	    	String interestRateStr = interestTF.getText().replaceAll("[^\\d.]", "");
	    	if (interestRateStr.isEmpty()) {
	    	    throw new Exception("Please enter a valid interest rate");
	    	}
	    	BigDecimal interestRate= new BigDecimal(interestRateStr).divide(BigDecimal.valueOf(100));
	    	if(interestRate.compareTo(BigDecimal.ZERO) == -1 || interestRate.equals(BigDecimal.ZERO)) {
	    		throw new Exception("Please Enter Proper Interest Rate");
	    	}
//	    	System.out.println("Interest Rate: " + interestRate);
	    	
	    	// Handling the loan Period from Period Slider
	    	BigDecimal totalLoanYears = new BigDecimal(loanPeriod/12);
//	    	System.out.println("Total Loan Years: " + totalLoanYears);
	    	
	    	// Handling the payment frequency per year
	    	Integer numberOfPaymentsPerYear;
	    	if(loanFreTF.getText().isEmpty()) {
	    		throw new Exception("Please Enter Frequency of Loan Payment");
	    	}
	    	String frequency = loanFreTF.getText().toLowerCase();
	    	if(frequency.equals("weekly")) {
	    		numberOfPaymentsPerYear = 52;
	    	}else if(frequency.equals("bi-weekly")) {
	    		numberOfPaymentsPerYear = 26;
	    	}else if(frequency.equals("monthly")) {
	    		numberOfPaymentsPerYear = 12;
	    	}else {
	    		throw new Exception("Please Enter Proper Frequency of Loan Payment");
	    	}
//	    	System.out.println("Number Of Payments Per Year: " + numberOfPaymentsPerYear);
	    	
	    	// Calling Calculation Function
	    	BigDecimal monthlyPayment = calculationFunction(principle, numberOfPaymentsPerYear, totalLoanYears, interestRate);
//	        System.out.println("Monthly Payment: " + monthlyPayment);
		        
	    	monthlyPaymentTF.setText(CURRENCY.format(monthlyPayment));
	    	PERCENTAGE.setMinimumFractionDigits(2);
	    	PERCENTAGE.setMaximumFractionDigits(2);
	    	interestTF.setText(PERCENTAGE.format(interestRate));
	    	priceTF.setText(CURRENCY.format(totalAmount));
	    	downpayTF.setText(CURRENCY.format(downPayment));
	    	

	    	
    	}catch(Exception ex) {
    		monthlyPaymentTF.setText(ex.getMessage());
    	}
    }

	public void initialize() {
		CURRENCY.setRoundingMode(RoundingMode.HALF_UP);
		
		periodSlider.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> objectValue, Number oldValue, Number newValue) {
				loanPeriod = newValue.intValue();
//				System.out.println("Read loan Period is:" + loanPeriod);
			}
		});
	}
}
