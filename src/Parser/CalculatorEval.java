package Parser;

import java.io.InputStream;
import java.io.IOException;

public class CalculatorEval {
	private int lookaheadToken;
	private InputStream in;

	public CalculatorEval(InputStream in) throws IOException {
		this.in = in;
		lookaheadToken = in.read();
	}

	private void consume(int symbol) throws IOException, ParseError {
		if (lookaheadToken != symbol)
			throw new ParseError();
		lookaheadToken = in.read();
	}

	private int evalDigit(int digit){
		return digit - '0';
	}

	private boolean isDigit(int symbol) {
		return lookaheadToken >= '0' && lookaheadToken <= '9';
	}

	private boolean isEOF(int symbol) {
		return symbol == -1 || symbol == '\n' || symbol == '\r';
	}

	private int Tern() throws IOException, ParseError {
		if(lookaheadToken < '0' || lookaheadToken > '9')
			throw new ParseError();

		int cond = evalDigit(lookaheadToken);
		consume(lookaheadToken);

		return TernTail(cond);
	}

	private int TernTail(int cond) throws IOException, ParseError {
		if(lookaheadToken == ':' || lookaheadToken == '\n' || lookaheadToken == -1)
			return cond;
		if(lookaheadToken != '?')
			throw new ParseError();

		consume('?');
		int thenPart = Tern();
		consume(':');
		int elsePart = Tern();

		return cond != 0 ? thenPart : elsePart;
	}

	private int Expr() throws IOException, ParseError {
		int rv = Term();
		return Expr2(rv);
	}

	private int Expr2(int value) throws IOException, ParseError {
		if (isDigit(lookaheadToken) || lookaheadToken == '*'|| lookaheadToken == '/'
				|| lookaheadToken == '(')
			throw new ParseError();
		if (lookaheadToken == ')' || isEOF(lookaheadToken))
			return value;

		int new_value;
		if (lookaheadToken == '+') {
			consume('+');
			new_value = value + Term();
		}
		else { // lookaheadToken == '-'
			consume('-');
			new_value = value - Term();
		}
		return Expr2(new_value);
	}

	private int Term() throws IOException, ParseError {
		int rv = Factor();
		return Term2(rv);
	}

	private int Term2(int value) throws IOException, ParseError {
		if (isDigit(lookaheadToken) || lookaheadToken == '(')
			throw new ParseError();
		if (lookaheadToken == '+' || lookaheadToken == '-' || lookaheadToken == ')'
				|| isEOF(lookaheadToken))
			return value;

		int new_value;
		if (lookaheadToken == '*') {
			consume('*');
			new_value = value*Factor();
		}
		else { // lookaheadToken == '/'
			consume('/');
			new_value = value/Factor();
		}
		return Term2(new_value);
	}

	private int Factor() throws IOException, ParseError {
		if (lookaheadToken == '(') {
			consume('(');
			int rv  = Expr();
			consume(')');
			return rv;
		}
		return Integer.parseInt(Number());
	}

	private String Number() throws IOException, ParseError {
		if (isDigit(lookaheadToken)) {
			String firstDigit = Character.toString(Digit());
			consume(lookaheadToken);
			String restDigits = Number();
			return firstDigit + restDigits;
		}
		else if (lookaheadToken != '(') { // lookaheadToken in [+,-,*,/,),$]
			return "";
		}
		else {
			throw new ParseError();
		}
	}

	private int Digit() {
		return lookaheadToken;
	}

	public int eval() throws IOException, ParseError {
		int rv = Expr();
		if (lookaheadToken != '\n' && lookaheadToken != -1)
			throw new ParseError();
		return rv;
	}
}
