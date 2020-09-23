public class Main{

	public static void main( String args[] ){

		lexer l = new lexer(
			"var led = 12\n" +  
			"var l = led\n" +
			"var a\n" +
			"a = l\n" +
			"output(LED, HIGH)\n" +
			"wait(1)\n" +
			"output(LED, HIGH)\n" +
			"wait(1)\n" +
			'\0');		
		
		parser p = new parser(l);

		ast_l ast_list = new ast_l();

		ast_list = p.parse_statements();

		if(ast_list != null){

			ast_list.print_ast();

		}
	}
}

