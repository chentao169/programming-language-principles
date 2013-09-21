package cop5555fa13;

import static cop5555fa13.TokenStream.Kind.*;

import java.util.Hashtable;

import cop5555fa13.TokenStream.Kind;
import cop5555fa13.TokenStream.LexicalException;
import cop5555fa13.TokenStream.Token;

public class Scanner {

   //ADD METHODS AND FIELDS
    private final TokenStream stream;
    private int index;
    private int inputsize;
    private Hashtable<String, Kind> reserve;
    
	public Scanner(TokenStream stream) {
		//IMPLEMENT THE CONSTRUCTOR
		this.stream = stream;
		this.index = 0;
		this.inputsize = stream.inputChars.length;
		this.reserve = new Hashtable<String, Kind>();
		this.reserve.put("image", Kind.image);
		this.reserve.put("int", Kind._int);
		this.reserve.put("boolean", Kind._boolean);
		this.reserve.put("pixel", Kind.pixel);
		this.reserve.put("pixels", Kind.pixels);
		this.reserve.put("red", Kind.red);
		this.reserve.put("green", Kind.green);
		this.reserve.put("blue", Kind.blue);
		this.reserve.put("Z", Kind.Z);
		this.reserve.put("shape", Kind.shape);
		this.reserve.put("width", Kind.width);
		this.reserve.put("height", Kind.height);
		this.reserve.put("location", Kind.location);
		this.reserve.put("x_loc", Kind.x_loc);
		this.reserve.put("y_loc", Kind.y_loc);
		this.reserve.put("SCREEN_SIZE", Kind.SCREEN_SIZE);
		this.reserve.put("visible", Kind.visible);
		this.reserve.put("x", Kind.x);
		this.reserve.put("y", Kind.y);
		this.reserve.put("pause", Kind.pause);
		this.reserve.put("while", Kind._while);
		this.reserve.put("if", Kind._if);
		this.reserve.put("else", Kind._else);	
				
	}

	public void scan() throws LexicalException {
		//THIS IS PROBABLY COMPLETE
		if(inputsize < 1) return;
		Token t;
		do {
			t = next();
			if (t.kind.equals(COMMENT)) {
				stream.comments.add(t);
			} else
				stream.tokens.add(t);
		} while (!t.kind.equals(EOF));
	}

	private static enum State{
		START, DIGITS, IDENT_PART, STRING, COMMENT, EOF
	}
	
	private Token next() throws LexicalException{
        //COMPLETE THIS METHOD.  THIS IS THE FUN PART!
		int beg = index;
		State state;
		if(index < inputsize)
			state = State.START;
		else state = State.EOF;
		Token t = null;
		int c = 0;
		do{
			if(index < inputsize)
				c = stream.inputChars[index];
			else c = -1;			
			
			switch(state){
				case START: 					
					switch(c){
						case -1: state = State.EOF; 								break;
						case ' ': case '\t': case '\n': case '\f': case '\r': 	
							++index; beg = index;									break;
						/* . | ; | , | ( | ) | [ | ] | { | } | : | ? 
						DOT, SEMI, COMMA, LPAREN, RPAREN, LSQUARE, RSQUARE, LBRACE, RBRACE, COLON, QUESTION*/
						case '.': t = stream.new Token(Kind.DOT, beg, ++index);		break;
						case ';': t = stream.new Token(Kind.SEMI, beg, ++index); 	break;
						case ',': t = stream.new Token(Kind.COMMA, beg, ++index); 	break;
						case '(': t = stream.new Token(Kind.LPAREN, beg,++index); 	break;
						case ')': t = stream.new Token(Kind.RPAREN, beg, ++index); 	break;
						case '[': t = stream.new Token(Kind.LSQUARE, beg, ++index); break;
						case ']': t = stream.new Token(Kind.RSQUARE, beg, ++index); break;
						case '{': t = stream.new Token(Kind.LBRACE, beg, ++index); 	break;
						case '}': t = stream.new Token(Kind.RBRACE, beg, ++index); 	break;
						case ':': t = stream.new Token(Kind.COLON, beg, ++index);	break;
						case '?': t = stream.new Token(Kind.QUESTION, beg, ++index);break;
						/* = | | | & | == | != | < | > | <= | >= | + | - | * | / | % | ! | << | >> 
						ASSIGN, OR, AND, EQ, NEQ, LT, GT, LEQ, GEQ, PLUS, MINUS, TIMES, DIV, MOD, NOT, LSHIFT, RSHIFT*/
						case '=': 
							if(++index< inputsize && stream.inputChars[index] == '=')
								t = stream.new Token(Kind.EQ, beg,++index);
							else t = stream.new Token(Kind.ASSIGN, beg, index);		break;
						case '|': t = stream.new Token(Kind.OR, beg, ++index);		break;
						case '&': t = stream.new Token(Kind.AND, beg, ++index); 	break;					
						case '!':
							if(++index< inputsize && stream.inputChars[index] == '=')
								t = stream.new Token(Kind.NEQ, beg, ++index);
							else t = stream.new Token(Kind.NOT, beg, index); 		break;	
						case '<':
							if(++index < inputsize){
								if(stream.inputChars[index] == '='){
									t = stream.new Token(Kind.LEQ, beg, ++index);	break;
								}else if(stream.inputChars[index] == '<'){
									t = stream.new Token(Kind.LSHIFT,beg, ++index);	break;
								}
							}							
							t = stream.new Token(Kind.LT, beg, index); 				break;	
						case '>':
							if(++index < inputsize){
								if(stream.inputChars[index] == '='){
									t = stream.new Token(Kind.GEQ, beg, ++index);	break;
								}else if(stream.inputChars[index] == '>'){
									t = stream.new Token(Kind.RSHIFT, beg, ++index);break;
								}
							}
							t = stream.new Token(Kind.GT, beg, index); 				break;
						case '+': t = stream.new Token(Kind.PLUS, beg, ++index);	break;
						case '-': t = stream.new Token(Kind.MINUS, beg, ++index);	break;
						case '*': t = stream.new Token(Kind.TIMES, beg, ++index);	break;
						case '/': 
							if(++index < inputsize && stream.inputChars[index] == '/'){
								state = State.COMMENT;
								++index;
							}else t = stream.new Token(Kind.DIV, beg, index);	 	break;
						case '%': t = stream.new Token(Kind.MOD, beg, ++index);	 	break;	
						case '0': t = stream.new Token(Kind.INT_LIT, beg, ++index);	break;
						case '"': state = State.STRING; ++index;					break;
						default:
							if(Character.isDigit(c))
								state = State.DIGITS;
							else if(Character.isJavaIdentifierStart(c))
								state = State.IDENT_PART;
							else throw stream.new LexicalException(index, "should not reach here");	
							++index;
						}
					break;
				 
				case DIGITS:
					if(!Character.isDigit(c))
						t = stream.new Token(Kind.INT_LIT, beg, index);
					else ++index;
					break;
					
				case IDENT_PART:
					if(!Character.isJavaIdentifierPart(c)){
						String s = new String(stream.inputChars, beg, index-beg);
						if(reserve.containsKey(s))
							t = stream.new Token(reserve.get(s), beg, index);
						else if(s.equals("true") || s.equals("false"))
							t = stream.new Token(Kind.BOOLEAN_LIT, beg, index);
						else t = stream.new Token(Kind.IDENT, beg, index);
					}else ++index;
					break;
					
				case STRING:
					if(c == -1) throw stream.new LexicalException(index, "incomplete string literal");
					if(c == '"')
						t = stream.new Token(Kind.STRING_LIT, beg, ++index);
					else ++index;					
					break;
					
				case EOF:
					t = stream.new Token(Kind.EOF, beg, index);
					break;
					
				case COMMENT:
					if (c == '\n'||c == '\r'||c == -1||c == '\u0085'||c == '\u2028'||c == '\u2029'){
						if(c == '\r' && index+1 < inputsize && stream.inputChars[index+1] == '\n') ++index;
						if(c == -1) --index;
						t = stream.new Token(Kind.COMMENT, beg, ++index);						
					}else ++index;
					break;
						
				default:
					assert false: "should not reach here";				
			}			
		}while(t==null);
		return t;
	}
}
