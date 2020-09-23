package com.vishnujoshi.ioteverywhere.compiler;

public class compiler {

	private lexer l;
	private parser p;
	private ast_l ast_list;

	public compiler(String code) {

		System.out.println(code);

		this.l = new lexer(code);

		this.p = new parser(this.l);

	}

	public void compile(){

		this.ast_list = new ast_l();

		this.ast_list = this.p.parse_statements();

		if(this.ast_list != null){

			this.run(this.ast_list);

		}

	}

	public void run(ast_l ast_list){

	}

}

