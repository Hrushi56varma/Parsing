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

package cop5556fa19.AST;

import cop5556fa19.Token;

public class ExpTrue extends Exp {

	
	public static final ExpTrue expTrueConst = new ExpTrue(null);
	
	public ExpTrue(Token firstToken) {
		super(firstToken);
	}
	
	

	@Override
	public String toString() {
		return "ExpTrue [firstToken=" + firstToken + "]";
	}



	@Override
	public int hashCode() {
		return 0;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		return true;
	}



	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitExpTrue(this,arg);
	}



}
