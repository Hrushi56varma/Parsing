/* *
 * Developed  for the class project in COP5556 Programming Language Principles 
 * at the University of Florida, Fall 2019.
 * 
 * This software is solely for the educational benefit of students 
 * enrolled in the course during the Fall 2019 semester.  
 * 
 * This software, and any software derived from it,  may not be shared with others or posted to public web sites,
 * either during the course or afterwards.
 * 
 *  @Beverly A. Sanders, 2019
 */


package cop5556fa19;

import static cop5556fa19.Token.Kind.*;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

import java.io.Reader;
import java.io.StringReader;

import org.junit.jupiter.api.Test;

import cop5556fa19.AST.Exp;
import cop5556fa19.AST.ExpBinary;
import cop5556fa19.AST.ExpFalse;
import cop5556fa19.AST.ExpFunction;
import cop5556fa19.AST.ExpInt;
import cop5556fa19.AST.ExpName;
import cop5556fa19.AST.ExpNil;
import cop5556fa19.AST.ExpString;
import cop5556fa19.AST.ExpTable;
import cop5556fa19.AST.ExpTrue;
import cop5556fa19.AST.ExpUnary;
import cop5556fa19.AST.ExpVarArgs;
import cop5556fa19.AST.Expressions;
import cop5556fa19.AST.Field;
import cop5556fa19.AST.FieldExpKey;
import cop5556fa19.AST.FieldImplicitKey;
import cop5556fa19.AST.ParList;
import cop5556fa19.ExpressionParser.SyntaxException;

class ExpressionParserTest {

	// To make it easy to print objects and turn this output on and off
	static final boolean doPrint = true;

	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}


	
	// creates a scanner, parser, and parses the input.  
	Exp parseAndShow(String input) throws Exception {
		show("parser input:\n" + input); // Display the input
		Reader r = new StringReader(input);
		Scanner scanner = new Scanner(r); // Create a Scanner and initialize it
		ExpressionParser parser = new ExpressionParser(scanner);  // Create a parser
		Exp e = parser.exp(); // Parse and expression
		show("e=" + e);  //Show the resulting AST
		return e;
	}
	


	@Test
	void testIdent0() throws Exception {
		String input = "x";
		Exp e = parseAndShow(input);
		assertEquals(ExpName.class, e.getClass());
		assertEquals("x", ((ExpName) e).name);
	}

	@Test
	void testIdent1() throws Exception {
		String input = "(x)";
		Exp e = parseAndShow(input);
		assertEquals(ExpName.class, e.getClass());
		assertEquals("x", ((ExpName) e).name);
	}

	@Test
	void testString() throws Exception {
		String input = "\"string\"";
		Exp e = parseAndShow(input);
		assertEquals(ExpString.class, e.getClass());
		assertEquals("\"string\"", ((ExpString) e).v);
	}

	@Test
	void testBoolean0() throws Exception {
		String input = "true";
		Exp e = parseAndShow(input);
		assertEquals(ExpTrue.class, e.getClass());
	}

	@Test
	void testBoolean1() throws Exception {
		String input = "false";
		Exp e = parseAndShow(input);
		assertEquals(ExpFalse.class, e.getClass());
	}


	@Test
	void testBinary0() throws Exception {
		String input = "1 + 2";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(1,OP_PLUS,2);
		show("expected="+expected);
		assertEquals(expected,e);
	}
	
	@Test
	void testUnary0() throws Exception {
		String input = "-2";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeExpUnary(OP_MINUS, 2);
		show("expected="+expected);
		assertEquals(expected,e);
	}
	
	@Test
	void testUnary1() throws Exception {
		String input = "-*2\n";
		assertThrows(SyntaxException.class, () -> {
		Exp e = parseAndShow(input);
		});	
	}
	@Test
	void f0200() throws Exception{
		String input = "f(0,200)";
		Exp e = parseAndShow(input);
	}
	@Test
	void testfunc() throws Exception {
		String input = "function(a, b, c) end";
		Exp e = parseAndShow(input);
	}
	
	@Test
	void testtablecons() throws Exception {
		String input = "{ [a*b] = 5 , a = 6 ;}";
		Exp e = parseAndShow(input);
	}
	
	@Test
	void abc() throws Exception{
		String input = "abc;";
		Exp e = parseAndShow(input);
	}
	
	@Test
	void testfunc1() throws Exception {
		String input = "{3,a}";
		Exp e = parseAndShow(input);
	}
	
	@Test
	void tableconst1() throws Exception {
		String input = "{[x + y] = xx * yy,}";
		Exp e = parseAndShow(input);
	}
	
	@Test
	void tableconstnull() throws Exception {
		String input = "{}";
		Exp e = parseAndShow(input);
	}
	@Test
	void functe1() throws Exception {
		String input = "function (a,b) end";
		Exp e = parseAndShow(input);
	}
	@Test
	void functe2() throws Exception {
		String input = "function (aa, b) end >> function(test, l, ...) end & function(...) end";
		Exp e = parseAndShow(input);
	}
	@Test
	void functe3() throws Exception {
		String input = "function (aa, b) end >>  function(test, l, ...) end";
		Exp e = parseAndShow(input);
	}
	
	
	
	@Test
	void testRightAssoc() throws Exception {
		String input = "\"concat\" .. \"is\"..\"right associative\"";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(
				Expressions.makeExpString("concat")
				, DOTDOT
				, Expressions.makeBinary("is",DOTDOT,"right associative"));
		show("expected=" + expected);
		assertEquals(expected,e);
	}
	@Test
	void testParenthesisPrecedence() throws Exception {
		String input = "(1+2*9)+(3-4)";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary( Expressions.makeBinary(Expressions.makeInt(1),OP_PLUS,Expressions.makeBinary(Expressions.makeInt(2), OP_TIMES, Expressions.makeInt(9)))
						, OP_PLUS
				, Expressions.makeBinary(Expressions.makeInt(3),OP_MINUS,Expressions.makeInt(4)));
		show("expected=" + expected);
		assertEquals(expected,e);
		
	}
	@Test
	void testOpPrecedence() throws Exception {
		String input = "\"a\"|\"b\"";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(
						Expressions.makeExpString("a")
				, BIT_OR
				, Expressions.makeExpString("b"));
		show("expected=" + expected);
		assertEquals(expected,e);
		
	}
	
	@Test
	void testPowerPrecedence1() throws Exception {
		String input = "1^2^3+1";
		Exp e = parseAndShow(input);
		Exp expected =Expressions.makeBinary(Expressions.makeBinary( Expressions.makeInt(1)
				, OP_POW
		, Expressions.makeBinary(Expressions.makeInt(2),OP_POW,Expressions.makeInt(3))),OP_PLUS,Expressions.makeInt(1));
		show("expected=" + expected);
		assertEquals(expected,e);
		
	}
	
	@Test
	void testPowerPrecedence2() throws Exception {
		String input = "1^2^3^4+5";
		Exp e = parseAndShow(input);
		Exp expected =Expressions.makeBinary(Expressions.makeBinary( Expressions.makeInt(1)
				, OP_POW
		, Expressions.makeBinary(Expressions.makeInt(2),OP_POW,Expressions.makeBinary(Expressions.makeInt(3), OP_POW,Expressions.makeInt(4)))),OP_PLUS,Expressions.makeInt(5));
		show("expected=" + expected);
		assertEquals(expected,e);
		
	}
	
	@Test
	void testPowerPrecedence3() throws Exception {
		String input = "1^2+1";
		Exp e = parseAndShow(input);
		Exp expected =Expressions.makeBinary(Expressions.makeBinary( Expressions.makeInt(1)
				, OP_POW
		, Expressions.makeInt(2)),OP_PLUS,Expressions.makeInt(1));
		show("expected=" + expected);
		assertEquals(expected,e);
		
	}
	
	//void testleftass() throws Exception{
		//String input = "1 - 2 - 3";
		//Exp e = parseAndShow(input);
		//Exp expected = Expressions.makeBinary(Expressions.makeBinary(Expressions.makeInt(Expressions.makeExpString("1")), OP_MINUS, Expression.makeEx))
	//}
	
	@Test
	void testLeftAssoc() throws Exception {
		String input = "\"minus\" - \"is\" - \"left associative\"";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(
				Expressions.makeBinary(
						Expressions.makeExpString("minus")
				, OP_MINUS
				, Expressions.makeExpString("is")), OP_MINUS, 
				Expressions.makeExpString("left associative"));
		show("expected=" + expected);
		assertEquals(expected,e);
		
	}

}
