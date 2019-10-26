/**
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cop5556fa19.AST.Block;
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
import cop5556fa19.AST.FieldNameKey;
import cop5556fa19.AST.FuncBody;
import cop5556fa19.AST.Name;
import cop5556fa19.AST.ParList;
import cop5556fa19.Token.Kind;
import static cop5556fa19.Token.Kind.*;

public class ExpressionParser {

	@SuppressWarnings("serial")
	class SyntaxException extends Exception {
		Token t;

		public SyntaxException(Token t, String message) {
			super(t.line + ":" + t.pos + " " + message);
		}
	}

	final Scanner scanner;
	Token t; // invariant: this is the next token

	ExpressionParser(Scanner s) throws Exception {
		this.scanner = s;
		t = scanner.getNext(); // establish invariant
	}

	Exp exp() throws Exception {
		Token first = t;
			/*
			 * if(isKind(STRINGLIT)) { e0 = new ExpString(t); consume(); } else
			 * if(isKind(INTLIT)) { e0 = new ExpInt(t); consume(); } else if(isKind(KW_nil))
			 * { e0 = new ExpNil(t); consume(); } else if(isKind(KW_false)) { e0 = new
			 * ExpFalse(t); consume(); } else if(isKind(KW_true)) { e0 = new ExpTrue(t);
			 * consume(); }
			 * 
			 * if(isKind(OP_PLUS)) { consume(); Exp e1 = exp(); e0 = new ExpBinary(first,
			 * e0, t, e1); } else if(isKind(OP_TIMES)) { consume(); Exp e1 = exp(); e0 = new
			 * ExpBinary(first, e0, t, e1); } else if(isKind(OP_MINUS)) { consume(); Exp e1
			 * = exp(); e0 = new ExpBinary(first, e0, t, e1);
			 */
		/*
		 * else if(isKind(OP_DIV)) { consume(); Exp e1 = exp(); e0 = new
		 * ExpBinary(first, e0, t, e1); }
		 */
		// }
		Exp e0 = andExp();
		while (isKind(KW_or)) {
			Token op = consume();
			Exp e1 = andExp();
			e0 = new ExpBinary(first, e0, op, e1);
		}

		return e0;
	}

	private Exp andExp() throws Exception {
		Token first = t;
		// TODO Auto-generated method stub
		// throw new UnsupportedOperationException("andExp"); //I find this is a more
		// useful placeholder than returning null.
		Exp e0 = CompareExp();
		while (isKind(KW_and)) {
			Token op = consume();
			Exp e1 = CompareExp();
			e0 = new ExpBinary(first, e0, op, e1);
		}
		return e0;
	}

	private Exp CompareExp() throws Exception {
		Token first = t;
		Exp e0 = BitorExp();
		while (isKind(REL_GT) || isKind(REL_LT) || isKind(REL_GE) || isKind(REL_LE) || isKind(REL_EQEQ)
				|| isKind(REL_NOTEQ)) {
			Token op = consume();
			Exp e1 = BitorExp();
			e0 = new ExpBinary(first, e0, op, e1);
		}
		return e0;
	}

	private Exp BitorExp() throws Exception {
		Token first = t;
		Exp e0 = BitXorExp();
		while (isKind(BIT_OR)) {
			Token op = consume();
			Exp e1 = BitXorExp();
			e0 = new ExpBinary(first, e0, op, e1);
		}
		return e0;
	}

	private Exp BitXorExp() throws Exception {
		Token first = t;
		Exp e0 = BitAmpExp();
		while (isKind(BIT_XOR)) {
			Token op = consume();
			Exp e1 = BitAmpExp();
			e0 = new ExpBinary(first, e0, op, e1);
		}
		return e0;
	}

	private Exp BitAmpExp() throws Exception {
		Token first = t;
		Exp e0 = BitShiftExp();
		while (isKind(BIT_AMP)) {
			Token op = consume();
			Exp e1 = BitShiftExp();
			e0 = new ExpBinary(first, e0, op, e1);
		}
		return e0;
	}

	private Exp BitShiftExp() throws Exception {
		Token first = t;
		Exp e0 = DotDotExp();
		while (isKind(BIT_SHIFTL) || isKind(BIT_SHIFTR)) {
			Token op = consume();
			Exp e1 = DotDotExp();
			e0 = new ExpBinary(first, e0, op, e1);
		}
		return e0;
	}

	private Exp DotDotExp() throws Exception {
		Token first = t;
		Exp e0 = SumExp();
		while (isKind(DOTDOT)) {
			Token op = consume();
			Exp e1 = SumExp();
			if(isKind(DOTDOT)) {
				match(DOTDOT);
				e0 = new ExpBinary(first,e0,op,Expressions.makeBinary(e1, DOTDOT, DotDotExp()));
			}
			else {
			e0 = new ExpBinary(first, e0, op, e1);
			}
		}
		return e0;
	}

	private Exp SumExp() throws Exception {
		Token first = t;
		Exp e0 = FactorExp();
		while (isKind(OP_PLUS) || isKind(OP_MINUS)) {
			Token op = consume();
			Exp e1 = FactorExp();
			e0 = new ExpBinary(first, e0, op, e1);
		}
		return e0;
	}

	private Exp FactorExp() throws Exception {
		Token first = t;
		Exp e0 = UnaryExp();
		while (isKind(OP_TIMES) || isKind(OP_DIV) || isKind(OP_DIVDIV) || isKind(OP_MOD)) {
			Token op = consume();
			Exp e1 = UnaryExp();
			e0 = new ExpBinary(first, e0, op, e1);
		}
		return e0;
	}

	private Exp UnaryExp() throws Exception {
		Token first = t;
		Exp e0 = PowerExp();
		while (isKind(OP_HASH) || isKind(KW_not) || isKind(OP_MINUS) || isKind(BIT_XOR)) {
			Token op = consume();
			Exp e1 = PowerExp();
			e0 = new ExpBinary(first, e0, op, e1);

		}
		return e0;
	}

	private Exp PowerExp() throws Exception {
		Token first = t;
		Exp e0 = term();
		while (isKind(OP_POW)) {
			Token op = consume();
			Exp e1 = term();
			if(isKind(OP_POW)) {
				match(OP_POW);
				e0 = new ExpBinary(first,e0,op,Expressions.makeBinary(e1, OP_POW, PowerExp()));
			}
			else {
			e0 = new ExpBinary(first, e0, op, e1);
			}
		}
		return e0;
	}

	private Exp term() throws Exception {
		Token first = t;
		Exp e0 = null;
		if (isKind(STRINGLIT)) {
			e0 = new ExpString(t);
			consume();
		} else if (isKind(INTLIT)) {
			e0 = new ExpInt(t);
			consume();
		} else if (isKind(KW_nil)) {
			e0 = new ExpNil(t);
			consume();
		} else if (isKind(KW_false)) {
			e0 = new ExpFalse(t);
			consume();
		} else if (isKind(KW_true)) {
			e0 = new ExpTrue(t);
			consume();
		} else if (isKind(OP_MINUS)) {
			Token op = consume();
			Exp e1 = exp();
			e0 = new ExpUnary(first, OP_MINUS, e1);
		} else if (isKind(OP_PLUS)) {
			Token op = consume();
			Exp e1 = exp();
			e0 = new ExpUnary(first, OP_PLUS, e1);
		} 
		else if (isKind(DOTDOTDOT)) {
			e0 = new ExpVarArgs(t);
			consume();
		} else if (isKind(KW_function)) {
			match(KW_function);
			FuncBody e1 = funcbody();
			e0 = new ExpFunction(first, e1);
			
		} else if(isKind(LCURLY)) {
			match(LCURLY);
			List<Field> e1 = fieldlist();
			e0 = new ExpTable(first, e1);
		} else if(isKind(NAME)) {
			
			e0 = new ExpName(first);
			match(NAME);
			
		} else if(isKind(LPAREN)) {
			match(LPAREN);
		    e0 = exp();
		    if(isKind(RPAREN)) {
		    	match(RPAREN);
		    }
		    
		} else {
			throw new SyntaxException(t,"Invalid Syntax");
		}
		return e0;
		// else if ()
	}

	private FuncBody funcbody() throws Exception {
		Token first = t;
		FuncBody fbody = null;
		if (isKind(LPAREN)) {
			match(LPAREN);
			if (isKind(NAME)) {
				parlist();
			}
			if (isKind(RPAREN)) {
				match(RPAREN);
				block();
				if (isKind(KW_end)) {
					match(KW_end);
				} else {
					Token t = consume();
					error(t.kind);
				}
			} 
			
		} else {
			Token t = consume();
			error(t.kind);
		}
		return fbody;
	}

	private ParList parlist() throws Exception {
		Token first = t;
		ParList pl = null;
		boolean hasVarArgs = false;
		if (isKind(DOTDOTDOT)) {
			List<Name> nl = new ArrayList<>();
			pl = new ParList(first, nl, true);
			consume();
		} else if (isKind(NAME)) {
			List<Name> nl = namelist();
			while (isKind(COMMA)) {
				consume();
				if (isKind(DOTDOTDOT)) {
					hasVarArgs = true;
					consume();
				} else {
					Token t = consume();
					error(t.kind);
				}
				pl = new ParList(first, nl, hasVarArgs);
			}
		}
		return pl;
	}

	private List<Name> namelist() throws Exception {
		Token first = t;
		boolean hasVarArgs = false;
		List<Name> nl = new ArrayList<>();
		if (isKind(NAME)) {
			nl.add(new Name(t, t.text));
			match(NAME);
			while (isKind(COMMA)) {
				consume();
				if (isKind(NAME)) {
					nl.add(new Name(first, t.text));
					consume();
				} else if (isKind(DOTDOTDOT)) {
					hasVarArgs = true;
					consume();
					
				}
				else {
					Token t = consume();
					error(t.kind);
				}

			}
		}
		return nl;

	}

	private List<Field> fieldlist() throws Exception {
		Token first = t;
		List<Field> fl = new ArrayList<>();
		while(!isKind(RCURLY)) {
		if (isKind(LSQUARE)) {
			match(LSQUARE);
			Exp e0 = exp();
			match(RSQUARE);
			match(ASSIGN);
			Exp e1 = exp();
			Field f0 = new FieldExpKey(first, e0, e1);
			fl.add(f0);
			if (isKind(COMMA)) {
				match(COMMA);
			} else if (isKind(SEMI)) {
				match(SEMI);
			} else {
				Token t = consume();
				error(t.kind);
			}
		} else if (isKind(NAME)) {
			Exp e0 = exp();
			//Field f0 = new FieldImplicitKey(first,e0);
			//fl.add(f0); //name is matched
			if(isKind(ASSIGN)) {
				match(ASSIGN);
				Exp e1 = exp();
				Field f0 = new FieldNameKey(first, new Name(t, t.text), e1);
				fl.add(f0);
				if (isKind(COMMA)) {
					match(COMMA);
				} else if (isKind(SEMI)) {
					match(SEMI);
				} else {
					Token t = consume();
					error(t.kind);
			}
			}else {
				Field f0 = new FieldImplicitKey(first, e0);
				fl.add(f0);
			}
			//else {
				//Exp e1 = exp();
				//Field f0 = new FieldImplicitKey(first, e1);
				//fl.add(f0);
				//if (isKind(COMMA)) {
					//match(COMMA);
				//} else if (isKind(SEMI)) {
					//match(SEMI);
				//}
		   //}
		} else{
			Exp e0 = exp();
			Field f0 = new FieldImplicitKey(first, e0);
			fl.add(f0);
			if (isKind(COMMA)) {
				match(COMMA);
			} else if (isKind(SEMI)) {
				match(SEMI);
			}
		}//if(isKind(RCURLY)) {match(RCURLY);}
		}
		
		match(RCURLY);
	return fl;
	}

	private Block block() {
		return new Block(null); // this is OK for Assignment 2
	}

	protected boolean isKind(Kind kind) {
		return t.kind == kind;
	}

	protected boolean isKind(Kind... kinds) {
		for (Kind k : kinds) {
			if (k == t.kind)
				return true;
		}
		return false;
	}

	/**
	 * @param kind
	 * @return
	 * @throws Exception
	 */
	Token match(Kind kind) throws Exception {
		Token tmp = t;
		if (isKind(kind)) {
			consume();
			return tmp;
		}
		error(kind);
		return null; // unreachable
	}

	/**
	 * @param kind
	 * @return
	 * @throws Exception
	 */
	Token match(Kind... kinds) throws Exception {
		Token tmp = t;
		if (isKind(kinds)) {
			consume();
			return tmp;
		}
		StringBuilder sb = new StringBuilder();
		for (Kind kind1 : kinds) {
			sb.append(kind1).append(kind1).append(" ");
		}
		error(kinds);
		return null; // unreachable
	}

	Token consume() throws Exception {
		Token tmp = t;
		t = scanner.getNext();
		return tmp;
	}

	void error(Kind... expectedKinds) throws SyntaxException {
		String kinds = Arrays.toString(expectedKinds);
		String message;
		if (expectedKinds.length == 1) {
			message = "Expected " + kinds + " at " + t.line + ":" + t.pos;
		} else {
			message = "Expected one of" + kinds + " at " + t.line + ":" + t.pos;
		}
		throw new SyntaxException(t, message);
	}

	void error(Token t, String m) throws SyntaxException {
		String message = m + " at " + t.line + ":" + t.pos;
		throw new SyntaxException(t, message);
	}

}
