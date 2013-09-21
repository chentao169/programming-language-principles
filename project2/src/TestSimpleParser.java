package cop5555fa13;

import static cop5555fa13.TokenStream.Kind.*;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import cop5555fa13.SimpleParser.SyntaxException;
import cop5555fa13.TokenStream;
import cop5555fa13.TokenStream.Kind;
import cop5555fa13.TokenStream.LexicalException;

public class TestSimpleParser {
	/* Scans and parses the given program.  Use this method for tests that expect to discover an error during parsing.
	 * The second parameter is the expected kind of the erroneous token.  The test case itself should "expect SyntaxException.class"
	 */
	private void parseErrorInput (String program, Kind expectedErrorKind) throws LexicalException, SyntaxException {
		TokenStream stream = new TokenStream(program);
		Scanner s = new Scanner(stream);
		try{
			s.scan();
		}
		catch(LexicalException e){
			System.out.println(e.toString());
			throw e;
		}
		Kind errorKind;
		try{
			System.out.println("*********************  parse   *************************");
			System.out.println(program);
			SimpleParser p = new SimpleParser(stream);
			p.parse();
		}
		catch(SyntaxException e){
			System.out.println("Parsed with error: ");
			//System.out.println(program);
			System.out.println(e.toString());
			errorKind = e.getKind();
			assertEquals(expectedErrorKind, errorKind);						
			System.out.println("---------------------   end    --------------------------");
			throw e;
		}
	}
	
	
	/* Scans and parses the given program.  Use this method for tests that expect to discover an error during scanning or 
	 * to successfully parse the input.  If an error during scanning is expected, the test case should "expect LexicalException.class".
	 */
	private void parseInput (String program) throws LexicalException, SyntaxException {
		TokenStream stream = new TokenStream(program);
		Scanner s = new Scanner(stream);
		try{
		s.scan();
		}
		catch(LexicalException e){
			System.out.println("Lexical error parsing program: ");
			System.out.println(program);
			System.out.println(e.toString());
			System.out.println("---------");
			throw e;
		}
		try{
		System.out.println("*********************  parse   *************************");
		System.out.println(program);
		SimpleParser p = new SimpleParser(stream);
		p.parse();
		}
		catch(SyntaxException e){
			System.out.println(e.toString());
			throw e;			
		}
		System.out.println("Parsed without error: ");
		//System.out.println(program);
		System.out.println("--------------------  end  ------------------------------");
	}
	



/* Example testing an erroneous program. */
	@Test(expected=SyntaxException.class)
	public void emptyProg() throws LexicalException, SyntaxException  {
		String input = "";
		parseErrorInput(input,EOF);
	}
	
/* Here is an example testing a correct program. */
	@Test
	public void minimalProg() throws LexicalException, SyntaxException  {
		String input = "smallestProg{}";
		parseInput(input);
	}
	
/* Another correct program */
	@Test
	public void decs() throws LexicalException, SyntaxException  {
		String input = "decTest {\n  int a;\n  image b; boolean c; pixel p; \n}";
		parseInput(input);
	}
	
/* A program missing a ; after "int a".  The token where the error will be detected is "image"*/
	@Test(expected=SyntaxException.class)
	public void missingSemi() throws LexicalException, SyntaxException  {
		String input = "decTest {\n  int a\n  image b; boolean c; pixel p; \n}";
		parseErrorInput(input, image);		
	}
	
/* A program with a lexical error */
	@Test(expected=LexicalException.class)
	public void lexError() throws LexicalException, SyntaxException  {
		String input = "decTest {\n  int a@;\n  image b; boolean c; pixel p; \n}";
		parseInput(input);
	}
	
	// missing ;
	@Test(expected=SyntaxException.class)	
	public void assignstmt1() throws LexicalException, SyntaxException  {
		String input = "decTest {\n  int a;\n  image b; boolean c; pixel p; a = 10\n}";
		parseErrorInput(input, RBRACE);		
	}
	
	//missing expr
	@Test(expected=SyntaxException.class)
	public void assignstmt2() throws LexicalException, SyntaxException  {
		String input = "decTest {\n  int a;\n  image b; boolean c; pixel p; a = {{ 1,2,}}\n}";
		parseErrorInput(input, RBRACE);		
	}
	
	//missing string
	@Test(expected=SyntaxException.class)
	public void assignstmt3() throws LexicalException, SyntaxException  {
		String input = "decTest {\n  int a;\n  image b; boolean c; pixel p; a = \"str\" \n}";
		parseErrorInput(input, RBRACE);		
	}
	
	//missing ;
		@Test(expected=SyntaxException.class)
		public void assignstmt4() throws LexicalException, SyntaxException  {
			String input = "decTest {\n  int a;\n  image b; boolean c; pixel p; a.pixels[1,2]= {{1,2,3}} \n}";
			parseErrorInput(input, RBRACE);		
		}
	
		@Test(expected=SyntaxException.class)
		public void assignstmt5() throws LexicalException, SyntaxException  {
			String input = "decTest {\n  int a;\n  image b; boolean c; pixel p; a.pixels[1,2]red = {{1,2,3}} \n}";
			parseErrorInput(input, LBRACE);		
		}
		
		@Test(expected=SyntaxException.class)
		public void assignstmt6() throws LexicalException, SyntaxException  {
			String input = "decTest {\n  int a;\n  image b; boolean c; pixel p; a.pixels[1,2]blue = b[3,4]green \n}";
			parseErrorInput(input, RBRACE);		
		}
		
		@Test(expected=SyntaxException.class)
		public void assignstmt7() throws LexicalException, SyntaxException  {
			String input = "decTest {\n  int a;\n  image b; boolean c; pixel p; a.shape =[x,y] \n}";
			parseErrorInput(input, RBRACE);		
		}
		
		@Test(expected=SyntaxException.class)
		public void assignstmt8() throws LexicalException, SyntaxException  {
			String input = "decTest {\n  int a;\n  image b; boolean c; pixel p; a.location =[x,y] \n}";
			parseErrorInput(input, RBRACE);		
		}
		
		@Test(expected=SyntaxException.class)
		public void assignstmt9() throws LexicalException, SyntaxException  {
			String input = "decTest {\n  int a;\n  image b; boolean c; pixel p; a.visible = (Z + x | y * 12 / 3 % 4) \n}";
			parseErrorInput(input, RBRACE);		
		}
		
		@Test(expected=SyntaxException.class)
		public void assignstmt10() throws LexicalException, SyntaxException  {
			String input = "decTest {\n  int a;\n  image b; boolean c; pixel p; a.visible = (Z + x | y * 12 / 3 % 4 << 2 == 3 ) \n}";
			parseErrorInput(input, RBRACE);		
		}
		
		@Test(expected=SyntaxException.class)
		public void assignstmt11() throws LexicalException, SyntaxException  {
			String input = "decTest {\n pause ttrue;\n a.visible = (Z + x | y * 12 / 3 % 4 << 2 == 3) \n}";
			parseErrorInput(input, RBRACE);		
		}
		
		@Test(expected=SyntaxException.class)
		public void assignstmt12() throws LexicalException, SyntaxException  {
			String input = "decTest {\n pause ttrue;\n a.visible = (Z & x & y <= 12 / 3 % 4 << 2 == 3 ) \n}";
			parseErrorInput(input, RBRACE);		
		}
		
		@Test(expected=SyntaxException.class)
		public void assignstmt13() throws LexicalException, SyntaxException  {
			String input = "decTest {\n pause true;\n while (false){ pause t; t= x!=y|x&Z+2-1*y/x%t<<a.y_loc>>t.x_loc} \n}";
			parseErrorInput(input, RBRACE);		
		}
		
		@Test(expected=SyntaxException.class)
		public void assignstmt14() throws LexicalException, SyntaxException  {
			String input = "decTest {\n pause true;\n while (false){ pause t; if(t[x,y]green){} else { } \n}";
			parseErrorInput(input, EOF);		
		}
}
