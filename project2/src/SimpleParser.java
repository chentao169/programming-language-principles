package cop5555fa13;


import java.util.List;

import cop5555fa13.TokenStream;
import cop5555fa13.TokenStream.Token;
import cop5555fa13.TokenStream.Kind;

public class SimpleParser {

	@SuppressWarnings("serial")
	public class SyntaxException extends Exception {
		Token t;

		public SyntaxException(Token t, String msg) {
			super(msg);
			this.t = t;
		}

		public String toString() {
			return super.toString() + "\n" + t.toString();
		}
		
		public Kind getKind(){
			return t.kind;
		}
	}

	TokenStream stream;
    int size = 0;
    int currPos = 0;
    Token t;
    
    /** creates a simple parser.  
     * 
     * @param initialized_stream  a TokenStream that has already been initialized by the Scanner 
     */
	public SimpleParser(TokenStream initialized_stream) {
		this.stream = initialized_stream;
		/* You probably want to do more here */
		this.size = stream.tokens.size();		
	}
	
	private boolean consume(){
		if(currPos < size){
			t = stream.getToken(currPos++);
			return true;
		}else {
			t = null;
			return false; 
		}
	}
	
	private void testToken(){
		System.out.println("token kind = "+t.kind);
	}
	/* This method parses the input from the given token stream.  If the input is correct according to the phrase
	 * structure of the language, it returns normally.  Otherwise it throws a SyntaxException containing
	 * the Token where the error was detected and an appropriate error message.  The contents of your
	 * error message will not be graded, but the "kind" of the token will be.
	 */
	public void parse() throws SyntaxException {
	    /* You definitely need to do more here */	
		if(size == 0) throw new SyntaxException(stream.new Token(Kind.EOF,0,0) , "parse: EOF is expected");;
		
		while(consume() && ! isKind(Kind.EOF)){
			if(! isKind(Kind.IDENT)) throw new SyntaxException(t, "parse: program nanme is expected");
			consume();
			if(! isKind(Kind.LBRACE)) throw new SyntaxException(t, "parse: { is expected");
			consume();
			if(! isKind(Kind.RBRACE)) parseBody(); 
		}		
	}
	
	private void parseBody() throws SyntaxException{	
		while(! isKind(Kind.RBRACE)){
			if(isKind(Kind.image, Kind.pixel, Kind._int, Kind._boolean)){
				consume();
				if(! isKind(Kind.IDENT)) throw new SyntaxException(t, "parseBody: IDENT is expected");
				consume();
				if(! isKind(Kind.SEMI)) throw new SyntaxException(t, "parseBody: IDENT is expected");
				else{
					consume();
				}
			}else if(!isStmt())	
				throw new SyntaxException(t, "parseBody : } is expected");	
		}
	}
		
	private boolean isStmt() throws SyntaxException{
		if(isKind(Kind.SEMI) || isAssignStmt()||isPauseStmt()||isIterationStmt()||isAlternativeStmt()){
			if(isKind(Kind.SEMI)) consume();
			return true;
		}else return false;
	}
	
	private boolean isAssignStmt() throws SyntaxException{
		if(! isKind(Kind.IDENT)) return false;
		
		consume();
		if(isKind(Kind.ASSIGN)){
			consume();
			if(isKind(Kind.STRING_LIT)){
				consume();
			}else if(! isExpr() && ! isPixel())
				throw new SyntaxException(t, "isAssignStmt: expr, pixel, string_lit is expected");
			if(! isKind(Kind.SEMI))throw new SyntaxException(t, "isAssignStmt: ; is expected");
			else{
				consume();
				return true;
			}
			
		}else if(isKind(Kind.DOT)){
			consume();
			switch(t.kind){
			case pixels:
				consume();
				if(! isKind(Kind.LSQUARE)) throw new SyntaxException(t, "isAssignStmt: [ is expected");
				consume();
				if(! isExpr()) throw new SyntaxException(t, "isAssignStmt: expr is expected");
				if(! isKind(Kind.COMMA)) throw new SyntaxException(t, "isAssignStmt: , is expected");
				consume();
				if(! isExpr()) throw new SyntaxException(t, "isAssignStmt: expr is expected");
				if(! isKind(Kind.RSQUARE)) throw new SyntaxException(t, "isAssignStmt: ] is expected");
				consume();
				if(isKind(Kind.ASSIGN)){
					consume();
					if(! isPixel()) throw new SyntaxException(t, "isAssignStmt: pixel is expected");
					if(! isKind(Kind.SEMI)) throw new SyntaxException(t, "isAssignStmt: ; is expected");
					else{
						consume();
						return true;
					}
				}else if(isKind(Kind.red, Kind.green, Kind.blue)){
					consume();
					if(! isKind(Kind.ASSIGN)) throw new SyntaxException(t, "isAssignStmt: = is expected");
					consume();
					if(! isExpr()) throw new SyntaxException(t, "isAssignStmt: expr is expected");
					if(! isKind(Kind.SEMI)) throw new SyntaxException(t, "isAssignStmt: ; is expected");
					else {
						consume();
						return true;
					}
				}else throw new SyntaxException(t, "isAssignStmt: =,r,g,b is expected");
			
			case shape: case location:
				consume();
				if(! isKind(Kind.ASSIGN)) throw new SyntaxException(t, "isAssignStmt: = is expected");
				consume();
				if(! isKind(Kind.LSQUARE)) throw new SyntaxException(t, "isAssignStmt: [ is expected");
				consume();
				if(! isExpr()) throw new SyntaxException(t, "isAssignStmt: expr is expected");
				if(! isKind(Kind.COMMA)) throw new SyntaxException(t, "isAssignStmt: , is expected");
				consume();
				if(! isExpr()) throw new SyntaxException(t, "isAssignStmt: expr is expected");
				if(! isKind(Kind.RSQUARE)) throw new SyntaxException(t, "isAssignStmt: ] is expected");
				consume();
				if(! isKind(Kind.SEMI)) throw new SyntaxException(t, "isAssignStmt: ; is expected");
				else{
					consume();
					return true;
				}			
				
			case visible:
				consume();
				if(! isKind(Kind.ASSIGN)) throw new SyntaxException(t, "isAssignStmt: = is expected");
				consume();
				if(! isExpr())  throw new SyntaxException(t, "isAssignStmt: expr is expected");
				if(! isKind(Kind.SEMI)) throw new SyntaxException(t, "isAssignStmt: ; is expected");
				else{
					consume();
					return true;
				}			
				
			default: throw new SyntaxException(t, "isAssignStmt: pixels,shape,location,visible is expected");
			}
			
		}else throw new SyntaxException(t, "isAssignStmt: =, . is expected");	
	}
	
	private boolean isPauseStmt() throws SyntaxException{
		if(! isKind(Kind.pause)) return false;
		
		consume();
		if(! isExpr()) throw new SyntaxException(t, "isPauseStmt: expr is expected"); 
		if(! isKind(Kind.SEMI)) throw new SyntaxException(t, "isPauseStmt: ; is expected");
		else{
			consume();
			return true;
		}		
	}
	
	private boolean isIterationStmt() throws SyntaxException{
		if(! isKind(Kind._while)) return false;
		
		consume();
		if(! isKind(Kind.LPAREN)) throw new SyntaxException(t, "isIterationStmt: ( is expected");
		consume();
		if(! isExpr()) throw new SyntaxException(t, "isIterationStmt: expr is expected");
		if(! isKind(Kind.RPAREN)) throw new SyntaxException(t, "isIterationStmt: ) is expected");
		consume();
		if(! isKind(Kind.LBRACE)) throw new SyntaxException(t, "isIterationStmt: { is expected");
		consume();
		while(! isKind(Kind.RBRACE)){
			if(! isStmt()) throw new SyntaxException(t, "isIterationStmt: stmt is expected");
		}
		consume();
		return true;		
	}
	
	private boolean isAlternativeStmt() throws SyntaxException{
		if(! isKind(Kind._if)) return false;
		
		consume();
		if(! isKind(Kind.LPAREN)) throw new SyntaxException(t, "isAlternativeStmt: ( is expected");
		consume();
		if(! isExpr()) throw new SyntaxException(t, "isAlternativeStmt: expr is expected");
		if(! isKind(Kind.RPAREN)) throw new SyntaxException(t, "isAlternativeStmt: ) is expected");
		consume();
		if(! isKind(Kind.LBRACE)) throw new SyntaxException(t, "isAlternativeStmt: { is expected");
		consume();
		while(! isKind(Kind.RBRACE)){
			if(! isStmt()) throw new SyntaxException(t, "isAlternativeStmt: stmt is expected");
		}
		consume();
		if(isKind(Kind._else)){
			consume();
			if(! isKind(Kind.LBRACE)) throw new SyntaxException(t, "isAlternativeStmt: { is expected");
			consume();
			while(! isKind(Kind.RBRACE)){
				if(! isStmt()) throw new SyntaxException(t, "isAlternativeStmt: stmt is expected");
			}
			consume();
		}
		return true;		
	}
	
	private boolean isPixel() throws SyntaxException{
		if(! isKind(Kind.LBRACE)) return false;
		
		consume();
		if(! isKind(Kind.LBRACE))throw new SyntaxException(t, "isPixel: { is expected");
		consume();
		if(! isExpr()) throw new SyntaxException(t, "isPixel: expr is expected");
		if(! isKind(Kind.COMMA)) throw new SyntaxException(t, "isPixel: , is expected");
		consume();
		if(! isExpr()) throw new SyntaxException(t, "isPixel: expr is expected");
		if(! isKind(Kind.COMMA)) throw new SyntaxException(t, "isPixel: , is expected");
		consume();
		if(! isExpr()) throw new SyntaxException(t, "isPixel: expr is expected");
		if(! isKind(Kind.RBRACE))throw new SyntaxException(t, "isPixel: } is expected");
		consume();
		if(! isKind(Kind.RBRACE))throw new SyntaxException(t, "isPixel: } is expected");	
		
		consume();
		return true;
	}
		
	private boolean isExpr() throws SyntaxException{
		if(! isOrExpr()) return false;
		
		if(isKind(Kind.QUESTION)){
			consume();
			if(! isExpr()) throw new SyntaxException(t, " isExpr: isExpr is expected");	
			if(! isKind(Kind.COLON)) throw new SyntaxException(t, " isExpr: : is expected");
			consume();			
			if(! isExpr()) throw new SyntaxException(t, " isExpr: isExpr is expected");	
		}		
		return true;			
	}
	
	private boolean isOrExpr() throws SyntaxException{
		if(! isAndExpr()) return false;
		
		while(isKind(Kind.OR)){
			consume();
			if(!isAndExpr()) throw new SyntaxException(t, " isOrExpr: AndExpr is expected");	
		}
		return true;			
	}
	
	private boolean isAndExpr() throws SyntaxException{
		if(! isEqualityExpr()) return false;
		
		while(isKind(Kind.AND)){
			consume();
			if(! isEqualityExpr()) throw new SyntaxException(t, " isAndExpr: EqualityExpr is expected");	
		}
		return true;		
	}
	
	private boolean isEqualityExpr() throws SyntaxException{
		if(!isRelExpr()) return false;
		
		while(isKind(Kind.EQ, Kind.NEQ)){
			consume();
			if(! isRelExpr()) throw new SyntaxException(t, " isEqualityExpr: RelExpr is expected");	
		}
		return true;		
	}
	
	private boolean isRelExpr() throws SyntaxException{
		if(!isShiftExpr()) return false;
		
		while(isKind(Kind.LT, Kind.GT, Kind.GEQ, Kind.LEQ)){
			consume();
			if(! isShiftExpr()) throw new SyntaxException(t, " isRelExpr: ShiftExpr is expected");	
		}
		return true;	
	}
	
	private boolean isShiftExpr() throws SyntaxException{
		if(!isAddExpr()) return false;
		
		while(isKind(Kind.LSHIFT, Kind.RSHIFT)){
			consume();
			if(! isAddExpr()) throw new SyntaxException(t, " isShiftExpr: AddExpr is expected");	
		}
		return true;		
	}
	
	private boolean isAddExpr() throws SyntaxException{
		if(!isMultExpr()) return false;
		
		while(isKind(Kind.PLUS, Kind.MINUS)){
			consume();
			if(! isMultExpr()) throw new SyntaxException(t, " isAddExpr: multExpr is expected");	
		}
		return true;
	}
	
	private boolean isMultExpr() throws SyntaxException{
		if(!isPrimaryExpr()) return false;
		
		while(isKind(Kind.TIMES, Kind.DIV,Kind.MOD)){
			consume();
			if(! isPrimaryExpr()) throw new SyntaxException(t, " isMultExpr: primaryExpr is expected");	
		}
		return true;
	}
	
	private boolean isPrimaryExpr() throws SyntaxException{
		if(currPos >= size) return false;
		
		switch(t.kind){
			case INT_LIT: case BOOLEAN_LIT: case x: case y: case Z: case SCREEN_SIZE:
				consume();
				return true;
				
			case LPAREN:
				consume();
				if(! isExpr())throw new SyntaxException(t, " PrimaryExpr: expr is expected");
				if(! isKind(Kind.RPAREN)) throw new SyntaxException(t, " PrimaryExpr: ( is expected");
				consume(); 
				return true;					
				
			case IDENT: 
				consume();
				switch(t.kind){
					case DOT: 
						consume();
						if(! isKind(Kind.height, Kind.width, Kind.x_loc, Kind.y_loc)) 
							throw new SyntaxException(t, " isPrimaryExpr: height,width,x_loc,y_loc is expected");
						consume();
						return true;													
						
					case LSQUARE: 
						consume();
						if(! isExpr())throw new SyntaxException(t, " isPrimaryExpr: expr is expected");
						if(! isKind(Kind.COMMA))throw new SyntaxException(t, " isPrimaryExpr:, is expected");
						consume();
						if(! isExpr())throw new SyntaxException(t, " isPrimaryExpr: expr is expected");
						if(! isKind(Kind.RSQUARE))throw new SyntaxException(t, " isPrimaryExpr: ] is expected");
						consume();
						if(! isKind(Kind.red, Kind.green, Kind.blue)) 
							throw new SyntaxException(t, " isPrimaryExpr: r,g,b is expected");
						consume();
						return true;							
																
					default: return true;										
				}	
			
			default: return false;
		}		
	}
		
	/* Java hint -- Methods with a variable number of parameters may be useful.  
	 * For example, this method takes a token and variable number of "kinds", and indicates whether the
	 * kind of the given token is among them.  The Java compiler creates an array holding the given parameters.
	 */
	   private boolean isKind(Kind... kinds) {
		Kind k = t.kind;
		for (int i = 0; i != kinds.length; ++i) {
			if (k==kinds[i]) return true;
		}
		return false;
	  }

}
