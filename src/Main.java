import Parser.ParseError;
import Parser.CalculatorEval;

import java.io.InputStream;
import java.io.IOException;

public class Main {
	public static void main(String[] args) {
		try {
			CalculatorEval evaluate = new CalculatorEval(System.in);
			System.out.println(evaluate.eval());
		}
		catch (IOException e) {
			System.err.println(e.getMessage());
		}
		catch(ParseError err){
			System.err.println(err.getMessage());
		}
	}
}
