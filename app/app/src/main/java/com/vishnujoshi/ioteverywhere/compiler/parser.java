package com.vishnujoshi.ioteverywhere.compiler;

import android.content.Context;
import android.media.session.MediaSession;
import android.util.Log;

import java.util.ArrayList;
import java.util.Stack;

class parser{

	private token current_token;
	private lexer l;
	private String TAG = this.getClass().getSimpleName();

	//constructor
	parser(lexer l){
		this.l = l;
	}

	//parser methods
	public token get_next_token(){

		keywords k = new keywords();

		if( this.l.get_current_char() == '\0' ){

			this.set_current_token(new token("T_NULL", "NULL"));
			return get_current_token();

		}

		if( this.l.get_current_char() == ' ' ){

			while(this.l.get_current_char() == ' '){
				
				this.l.next_char();
				
			}
		}

		if( this.l.get_current_char() == '\t'){

			while (this.l.get_current_char() == '\t'){

				this.l.next_char();

			}

		}

		if( this.l.get_current_char() == '\n' ){

			this.l.next_char();
			this.set_current_token(new token("T_NEWLINE", "NEWLINE"));
			return get_current_token();

		}
			
		if( this.l.is_alpha_numeric(this.l.get_current_char()) ){

			String identifier = this.l.get_identifier();

			if( k.is_num(identifier) ){
					
				this.set_current_token( new token("T_CONSTANT", identifier));
				return get_current_token();

			}else if( k.is_keyword(identifier) ){

				this.set_current_token( new token("T_KEYWORD", identifier));
				return this.get_current_token();

			}else{
					
				this.set_current_token( new token("T_IDENTIFIER", identifier));
				return get_current_token();

			}
		}

		switch(this.l.get_current_char()){
			case '(' : {

							this.l.next_char();
							this.set_current_token( new token("T_LPAREN", String.valueOf(l.get_current_char())));
							return this.get_current_token();

					   }
			case ')' : {

							this.l.next_char();
							this.set_current_token(new token("T_RPAREN", String.valueOf(l.get_current_char())));
							return this.get_current_token();

					   }
			case '{' : {
							this.l.next_char();
							this.set_current_token(new token("T_LBRACE", String.valueOf(l.get_current_char())));
							return this.get_current_token();

					   }

			
			case '}' : {
	
						    this.l.next_char();
							this.set_current_token( new token("T_RBRACE", String.valueOf(l.get_current_char())));
							return this.get_current_token();

					   }
			case '+' : {

						    this.l.next_char();
							this.set_current_token( new token("T_PLUS", String.valueOf(l.get_current_char())));
							return get_current_token();

					   }
			case '-' : {

						  	this.l.next_char();
							this.set_current_token(new token("T_MINUS", String.valueOf(l.get_current_char())));
							return get_current_token();

					   }
			case '*' : {

						    this.l.next_char();
							this.set_current_token(new token("T_ASTERIX", String.valueOf(l.get_current_char())));
							return get_current_token();

					   }
			case '/' : {

						    this.l.next_char();
							this.set_current_token(new token("T_FSLASH", String.valueOf(l.get_current_char())));
							return get_current_token();

					   }
			case '%' : {

						    this.l.next_char();
							this.set_current_token( new token("T_MOD", String.valueOf(l.get_current_char())));
							return get_current_token();

					   }
			case '=' : {

							char next = this.l.peek_next_char();

							if( next == '='){

								token t = new token("T_EE", "==");
								this.current_token = t;
								this.l.next_char();
								this.l.next_char();
								return t;

							}

						    this.l.next_char();
							this.set_current_token(new token("T_EQUAL", String.valueOf(l.get_current_char())));
							return get_current_token();

					   }
			case '>' : {

							char next = this.l.peek_next_char();

							if(next == '='){

								token t = new token("T_GE", ">=");
								this.current_token = t;
								this.l.next_char();
								this.l.next_char();
								return	t;
							}

						    this.l.next_char();
							this.set_current_token( new token("T_GREATER", String.valueOf(l.get_current_char())));
							return get_current_token();
								
						}
			case '<' : {

							char next = this.l.peek_next_char();

							if(next == '='){

								token t = new token("T_LE", "<=");
								this.current_token = t;
								this.l.next_char();
								this.l.next_char();
								return	t;

							}

							this.l.next_char();
							this.set_current_token( new token("T_LESSER", String.valueOf(l.get_current_char())));
							return get_current_token();

					   }
			case '|' : {

						char next = this.l.peek_next_char();

						if( next == '|'){

							token t = new token("T_LOR", "||");
							this.current_token = t;
							this.l.next_char();
							this.l.next_char();
							return t;

						}

						this.l.next_char();
						this.set_current_token( new token("T_BOR", String.valueOf(l.get_current_char())));
						return get_current_token();
			}
			case '&' :{

						char next = this.l.peek_next_char();

						if( next == '&'){

							this.set_current_token( new token("T_LAND", "&&"));
							this.l.next_char();
							this.l.next_char();
							return get_current_token();

						}

						this.l.next_char();
						this.set_current_token(new token("T_BAND", String.valueOf(l.get_current_char())));
						return get_current_token();
			}
			case '!' :{

						char next = this.l.get_current_char();

						if ( next == '!'){

							this.set_current_token( new token("T_NE", String.valueOf(l.get_current_char())));
							this.l.next_char();
							this.l.next_char();
							return get_current_token();

						}

						this.l.next_char();
						this.set_current_token(new token("T_LNOT", String.valueOf(this.l.get_current_char())));
						return this.get_current_token();

			}
			case ',' : {

							this.l.next_char();
							this.set_current_token(new token("T_COMMA", String.valueOf(l.get_current_char())));
							return get_current_token();

					   }
			case '"' : {
							String string_identifier = this.l.get_string();

							this.l.next_char();
							this.set_current_token(new token("T_STRING", string_identifier));
							return get_current_token();

					   }
				
		}
			
		return null;

	}
	
	public token peek_next_token(){

		keywords k = new keywords();

		lexer lex = new lexer();

		lex.set_index(this.l.get_index());
		lex.set_code(this.l.get_code());
		lex.set_current_char(this.l.get_current_char());
		
		if( lex.get_current_char() == '\0' ){

			return (new token("T_NULL", "NULL"));

		}
		if( lex.get_current_char() == ' ' ){

			while(lex.get_current_char() == ' '){
				
				lex.next_char();
				
			}
		}

		if( lex.get_current_char() == '\t'){

			while (lex.get_current_char() == '\t'){

				lex.next_char();

			}

		}

		if( lex.get_current_char() == '\n' ){

			lex.next_char();
			return new token("T_NEWLINE", "NEWLINE");

		}
			
		if( lex.is_alpha_numeric(lex.get_current_char()) ){

			String identifier = lex.get_identifier();

			if( k.is_num(identifier) ){
					
				return new token("T_CONSTANT", identifier);

			}else if( k.is_keyword(identifier) ){

				return new token("T_KEYWORD", identifier);

			}else{
					
				return new token("T_IDENTIFIER", identifier);

			}
		}

		switch(lex.get_current_char()){
			case '(' : {

							lex.next_char();
							return ( new token("T_LPAREN", String.valueOf(l.get_current_char())));

					   }
			case ')' : {

							lex.next_char();
							return (new token("T_RPAREN", String.valueOf(l.get_current_char())));

					   }
			case '{' : {
							lex.next_char();
							return (new token("T_LBRACE", String.valueOf(l.get_current_char())));

					   }

			
			case '}' : {

						    lex.next_char();
							return ( new token("T_RBRACE", String.valueOf(l.get_current_char())));

					   }
			case '+' : {

						    lex.next_char();
							return ( new token("T_PLUS", String.valueOf(l.get_current_char())));

					   }
			case '-' : {

						  	lex.next_char();
							return (new token("T_MINUS", String.valueOf(l.get_current_char())));

					   }
			case '*' : {

						    lex.next_char();
							return (new token("T_ASTERIX", String.valueOf(l.get_current_char())));

					   }
			case '/' : {

						    lex.next_char();
							return (new token("T_FSLASH", String.valueOf(l.get_current_char())));

					   }
			case '%' : {

						    lex.next_char();
							return ( new token("T_MOD", String.valueOf(l.get_current_char())));

					   }
			case '=' : {

							char next = lex.peek_next_char();

							if( next == '='){

								lex.next_char();
								return (new token("T_EE", "=="));

							}

						    lex.next_char();
							return(new token("T_EQUAL", /*String.valueOf(l.get_current_char())*/  l.get_current_char() + "\0"));

					   }
			case '>' : {
							char next = lex.peek_next_char();

							if( next == '='){

								lex.next_char();
								return (new token("T_GE", ">="));

							}

						    lex.next_char();
							return ( new token("T_GREATER", String.valueOf(l.get_current_char())));
								
						}
			case '<' : {
							char next = lex.peek_next_char();

							if( next == '='){

								lex.next_char();
								return (new token("T_LE", "<="));

							}

						    lex.next_char();
							return ( new token("T_LESSER", String.valueOf(l.get_current_char())));

					   }
			case '|' :{
							char next = lex.peek_next_char();

							if( next == '|'){

								lex.next_char();
								return (new token("T_LOR", "||"));

							}

							lex.next_char();
							return ( new token("T_BOR", String.valueOf(l.get_current_char())));

			}
			case '&' :{

						char next = lex.peek_next_char();

						if( next == '&'){

							lex.next_char();
							return (new token("T_LAND", "&&"));

						}

						lex.next_char();
						return ( new token("T_BAND", String.valueOf(l.get_current_char())));

			}
			case '!' :{

						char next = lex.peek_next_char();

						if( next == '='){

							lex.next_char();
							return (new token("T_NE", "!="));
						}

						lex.next_char();
						return ( new token("T_LNOT", String.valueOf(l.get_current_char())));

			}
			case ',' : {

							lex.next_char();
							return(new token("T_COMMA", String.valueOf(l.get_current_char())));

					   }
			case '"' : {
							String string_identifier = lex.get_string();

							lex.next_char();
							return (new token("T_STRING", string_identifier));

					   }
				
		}
			
		return null;

	}

	//getters and setter
	public token get_current_token(){
		return this.current_token;
	}

	public void set_current_token(token t){
		this.current_token = t;
	}

	//parser eat function
	public boolean eat(token t, String type, errors err_list, int code_size){

		if(!t.get_type().equals(type)){

			String err_msg = "unexpected token " + t.get_content() + " of type " + t.get_type() + " expected " + type;

			error e = new error(err_msg, code_size);

			err_list.add_new_error(e);

			return false;

		}else{

			return true;

		}

	}

	public void parse_newline(errors err_list, ast_l list){

		this.eat(this.get_current_token(), "T_NEWLINE",err_list, list.get_ast_list_count());
			
	}

	public ArrayList<token> parse_expression(){

		this.get_next_token();

		expression e = new expression();

		ArrayList<token> list = new ArrayList<>();

		while ( e.is_expression_token(this.get_current_token())){

			list.add(this.get_current_token());
			this.get_next_token();

		}

		if( list.size() == 1){
			return list;
		}

		return e.infix_to_postfix(list);

	}

	public ast parse_var_def(errors err_list, ast_l list){

		ast node = new ast("AST_VAR_DEF");

		keywords k = new keywords();

		expression e = new expression();

		boolean error_flag = false;

		if( !this.eat(this.get_current_token(), "T_KEYWORD", err_list, list.get_ast_list_count()) ){
			error_flag = true;
			return null;
		}

		this.get_next_token();

		if( !this.eat(this.get_current_token(), "T_IDENTIFIER", err_list, list.get_ast_list_count()) ){
			System.out.println("here");
			error_flag = true;
			return null;
		}

		node.set_var_def_var_name(this.get_current_token().get_content());

		token t = this.peek_next_token();

		if(t.get_type().equals("T_EQUAL")){

			this.get_next_token();

			if( !this.eat(this.get_current_token(), "T_EQUAL", err_list, list.get_ast_list_count()) ){
				error_flag = true;
				return null;
			}

			t = this.peek_next_token();

			if( t.get_type().equals("T_CONSTANT") ||
				t.get_type().equals("T_IDENTIFIER") ||
				t.get_type().equals("T_STRING")){

				if( t.get_type().equals("T_IDENTIFIER")){

					if( k.is_builtin_function(t.get_content())){

						this.get_next_token();

						ast temp = this.parse_function_call(err_list, list);

						if(temp != null) {

							node.set_type("AST_VAR_DEF_ASSIGNMENT_FUNCTION");
							node.set_function_name(temp.get_function_name());
							node.set_args_list(temp.get_args_list());

							node.set_ast_node_index(list.get_ast_list_count());

							return node;
						}else{

							return null;

						}

					}

				}

				node.setVar_def_var_expr(this.parse_expression());

				if( !e.is_postfix_valid(node.getVar_def_var_expr()) ){

					err_list.add_new_error(new error("Invalid expression", list.get_ast_list_count()));
					return null;

				}

				node.set_type("AST_VAR_DEF_ASSIGNMENT");

				node.set_ast_node_index(list.get_ast_list_count());

				return node;

			}

		}else{

			t = this.peek_next_token();

			if( !this.eat(t, "T_NEWLINE", err_list, list.get_ast_list_count()) ){
				error_flag = true;
				return null;
			}

			node.set_ast_node_index(list.get_ast_list_count());

			if(!error_flag){

				return node;

			}

		}	

		return null;

	}

	public ast parse_function_call(errors err_list, ast_l list){

		keywords k = new keywords();

		boolean error_flag = false;

		ast node = new ast("");

		if( !this.eat(get_current_token(), "T_IDENTIFIER", err_list, list.get_ast_list_count()) ){

			error_flag = true;
			return null;

		}

		if( k.is_builtin_function(this.get_current_token().get_content()) ){

			node = new ast("AST_BUILTIN_FUNCTION_CALL");

			if( get_current_token().get_content().equals("output") ){

				node.set_function_name(get_current_token().get_content());

				this.get_next_token(); //(

				if( !this.eat(get_current_token(), "T_LPAREN", err_list, list.get_ast_list_count()) ){

					error_flag = true;
					return null;

				}

				//first arguement

				token t = peek_next_token();

				this.get_next_token();
	
				if( !this.eat(this.get_current_token(), "T_KEYWORD", err_list, list.get_ast_list_count() )){
	
					error_flag = true;
					return null;

				}

				function_arg arg = new function_arg(this.get_current_token().get_content(), this.get_current_token().get_type());

				node.add_function_arg(arg);

				//comma
				
				this.get_next_token();

				if( !this.eat(get_current_token(), "T_COMMA", err_list, list.get_ast_list_count()) ){

					error_flag = false;
					return null;

				}

				//second arguement
				t = peek_next_token();

				if( t.get_type().equals("T_CONSTANT") ){

					this.get_next_token();
	
					if( !this.eat(this.get_current_token(), "T_CONSTANT", err_list, list.get_ast_list_count() )){
	
						error_flag = true;
						return null;

					}

					arg = new function_arg(this.get_current_token().get_content(), this.get_current_token().get_type());

					node.add_function_arg(arg);

				}else if( t.get_type().equals("T_IDENTIFIER") ){

					this.get_next_token();
	
					if( !this.eat(this.get_current_token(), "T_IDENTIFIER", err_list, list.get_ast_list_count() )){
	
						error_flag = true;
						return null;

					}

					arg = new function_arg(this.get_current_token().get_content(), this.get_current_token().get_type());

					node.add_function_arg(arg);

				}else if( t.get_type().equals("T_KEYWORD") ){

					this.get_next_token();
	
					if( !this.eat(this.get_current_token(), "T_KEYWORD", err_list, list.get_ast_list_count() )){
	
						error_flag = true;
						return null;

					}

					arg = new function_arg(this.get_current_token().get_content(), this.get_current_token().get_type());

					node.add_function_arg(arg);

				}

			}else if( get_current_token().get_content().equals("input") ){

				node.set_function_name(get_current_token().get_content());

				this.get_next_token(); //(

				if( !this.eat(get_current_token(), "T_LPAREN", err_list, list.get_ast_list_count()) ){

					error_flag = true;
					return null;

				}

				//first arguement

				this.get_next_token();
	
				if( !this.eat(this.get_current_token(), "T_KEYWORD", err_list, list.get_ast_list_count() )){
	
					error_flag = true;
					return null;

				}

				function_arg arg = new function_arg(this.get_current_token().get_content(), this.get_current_token().get_type());

				node.add_function_arg(arg);

			}else if( get_current_token().get_content().equals("wait")){

				node.set_function_name(get_current_token().get_content());

				this.get_next_token(); //(

				if( !this.eat(get_current_token(), "T_LPAREN", err_list, list.get_ast_list_count()) ){

					error_flag = true;
					return null;

				}

				//first arguement

				token t = peek_next_token();

				if( t.get_type().equals("T_CONSTANT") ){

					this.get_next_token();
	
					if( !this.eat(this.get_current_token(), "T_CONSTANT", err_list, list.get_ast_list_count() )){
	
						error_flag = true;
						return null;

					}

					function_arg arg = new function_arg(this.get_current_token().get_content(), this.get_current_token().get_type());

					node.add_function_arg(arg);

				}else if( t.get_type().equals("T_IDENTIFIER") ){

					this.get_next_token();
	
					if( !this.eat(this.get_current_token(), "T_IDENTIFIER", err_list, list.get_ast_list_count() )){
	
						error_flag = true;
						return null;

					}

					function_arg arg = new function_arg(this.get_current_token().get_content(), this.get_current_token().get_type());

					node.add_function_arg(arg);

				}
			}
			
			get_next_token();

			if( !this.eat(get_current_token(), "T_RPAREN", err_list, list.get_ast_list_count()) ){

				error_flag = true;
				return null;

			}

		}// TODO(implement user defined function call)

		node.set_ast_node_index(list.get_ast_list_count());

		if( !error_flag ){

			return node;

		}

		return null;

	}
	
	public ast parse_var_assignment( errors err_list, ast_l list){

		ast node = new ast("AST_VAR_ASSIGNMENT");

		boolean error_flag = false;

		keywords k = new keywords();

		expression e = new expression();

		if( !this.eat(get_current_token(), "T_IDENTIFIER", err_list, list.get_ast_list_count()) ){

			error_flag = true;
			return null;

		}

		node.set_var_name(get_current_token().get_content());

		this.get_next_token(); // =

		if( !this.eat(get_current_token(), "T_EQUAL", err_list, list.get_ast_list_count()) ){

			error_flag = true;
			return null;

		}

		token t = peek_next_token();

		if( t.get_type().equals("T_CONSTANT") ||
			t.get_type().equals("T_STRING") ||
			t.get_type().equals("T_IDENTIFIER") ){


			if( t.get_type().equals("T_IDENTIFIER")){

				if( k.is_builtin_function(t.get_content())){

					this.get_next_token();

					ast temp = parse_function_call(err_list, list);

					if (temp != null) {
						node.set_type("AST_VAR_ASSIGNMENT_FUNCTION");

						node.set_function_name(temp.get_function_name());
						node.set_args_list(temp.get_args_list());

						node.set_ast_node_index(list.get_ast_list_count());

						return node;
					}else{
						return null;
					}

				}

			}


			node.set_type("AST_VAR_ASSIGNMENT");

			node.setVar_expr( this.parse_expression() );

			if( !e.is_postfix_valid(node.getVar_expr()) ){

				err_list.add_new_error(new error("Invalid expression", list.get_ast_list_count()));

				error_flag = true;

				return null;

			}

			node.set_ast_node_index( list.get_ast_list_count() );

		}

		if( !error_flag ) {
			return node;
		}

		return null;
	}

	public ArrayList<token> parse_condtional_expression(){

		ArrayList<token> list = new ArrayList<>();

		this.get_next_token();

		expression e = new expression();

		while ( e.is_logical_expression_token(this.get_current_token())){

			list.add(this.get_current_token());

			this.get_next_token();

		}

		if( list.size() == 1){

			return list;

		}

		ArrayList<token> postfix = e.infix_to_postfix(list);

		return postfix;

	}

	public ast parse_conditional_statements( errors err_list, ast_l list){

		ast node = new ast("AST_CONDITIONAL_IF");

		expression e = new expression();

		if( !this.eat(this.get_current_token(), "T_KEYWORD", err_list, list.get_ast_list_count())){

			return null;

		}

		this.get_next_token(); //(

		if( !this.eat(this.get_current_token(), "T_LPAREN", err_list, list.get_ast_list_count())){

			return null;

		}

		ArrayList<token> expr = this.parse_condtional_expression();

		if( !e.is_postfix_valid(expr) || expr.size() == 1){

			err_list.add_new_error( new error( "Invalid conditional statement",  list.get_ast_list_count()));

			return null;

		}

		node.setConditional_statement_expr(expr);

		if( !this.eat(this.get_current_token(), "T_RPAREN", err_list, list.get_ast_list_count())){

			return null;

		}

		Stack<token> s = new Stack<>();

		ast_l true_block = this.parse_statement_block(err_list, s, list);

		if( true_block != null ) {

			if( true_block.get_ast_list_count() == 0){

				err_list.add_new_error( new error("Empty if block", list.get_ast_list_count()));
				return null;

			}

			node.setTrue_block(true_block);

		}else{

			return	null;

		}

		token t = this.peek_next_token();

		while ( t.get_type().equals("T_NEWLINE")){

			this.get_next_token();
			this.parse_newline(err_list, list);
			t = peek_next_token();

		}

		switch (get_current_token().get_type()){


			case "T_NULL" :{

				node.set_ast_node_index(list.get_ast_list_count());
				return node;

			}
			case "T_KEYWORD" :{

				if( get_current_token().get_content().equals("else") ){

					if( !this.eat(this.get_current_token(), "T_KEYWORD", err_list, list.get_ast_list_count())){

						return null;

					}

					ast_l false_block = this.parse_statement_block(err_list, s, list);

					node.set_type("AST_CONDITIONAL_IF_ELSE");

					if( false_block != null){

						if( false_block.get_ast_list_count() == 0){

							err_list.add_new_error(new error("Empty else block", list.get_ast_list_count()));
							return null;

						}

						node.setFalse_block(false_block);

					}else {

						return null;

					}

				}

				break;

			}

			case "T_NEWLINE" : {

				this.parse_newline(err_list, list);

				break;

			}


		}

		node.set_ast_node_index(list.get_ast_list_count());

		return node;

	}

	public ast_l parse_statements(){
	
		errors err_list = new errors();

		ast_l ast_list = new ast_l();

		this.get_next_token();

		while( !this.get_current_token().get_type().equals("T_NULL") ){

			if( this.get_current_token().get_type().equals("T_KEYWORD") ){

				if( this.get_current_token().get_content().equals("var")){

					ast node = this.parse_var_def(err_list, ast_list);

					if(node != null){

						ast_list.add_new_ast(node);

					}

				}else if ( this.get_current_token().get_content().equals("if") ){

					ast node = this.parse_conditional_statements(err_list, ast_list);

					if(node != null){

						ast_list.add_new_ast(node);

					}

				}

			}else if(this.get_current_token().get_type().equals("T_IDENTIFIER") ){

				token t = this.peek_next_token();

				if( t.get_type().equals("T_EQUAL") ){

					ast node = this.parse_var_assignment(err_list, ast_list);

					if(node != null){

						ast_list.add_new_ast(node);

					}

				}else if( t.get_type().equals("T_LPAREN") ){

					ast node = this.parse_function_call(err_list, ast_list);

					if(node != null){

						ast_list.add_new_ast(node);

					}

				}else{
					this.parse_newline(err_list, ast_list);
				}

			}



			if( this.get_current_token().get_type().equals("T_NULL") ){
				break;
			}

			if( this.get_current_token().get_type().equals("T_NEWLINE" )){

				this.parse_newline(err_list, ast_list);

			}
			
			this.get_next_token();
		
		}

		if(err_list.get_errors_count() > 0){

			err_list.print_errors();
			return null;

		}else{

			return ast_list;

		}


	}

	public ast_l parse_statement_block(errors err_list, Stack<token> s, ast_l parent_ast_list){

		ast_l ast_list = new ast_l();

		this.get_next_token(); // {

		if( !this.eat(this.get_current_token(), "T_LBRACE", err_list, parent_ast_list.get_ast_list_count()) ){

			return null;

		}

		s.push(this.get_current_token());

		this.get_next_token();

		if( this.get_current_token().get_type().equals("T_NEWLINE") ){

			while( this.get_current_token().get_type().equals("T_NEWLINE") ){

				this.parse_newline(err_list, ast_list);
				this.get_next_token();

			}

		}

		while( s.size() > 0 && !this.get_current_token().get_type().equals("T_RBRACE") ){

			if( this.get_current_token().get_type().equals("T_KEYWORD") ){

				if( this.get_current_token().get_content().equals("var")){

					ast node = this.parse_var_def(err_list, ast_list);

					if(node != null){

						ast_list.add_new_ast(node);

					}

				}else if( this.get_current_token().get_content().equals("if")){

					ast node = this.parse_conditional_statements(err_list, ast_list);

					if( node != null ){

						ast_list.add_new_ast(node);

					}

				}

			}else if(this.get_current_token().get_type().equals("T_IDENTIFIER") ){

				token t = this.peek_next_token();

				if( t.get_type().equals("T_EQUAL") ){

					ast node = this.parse_var_assignment(err_list, ast_list);

					if(node != null){

						ast_list.add_new_ast(node);

					}

				}else if( t.get_type().equals("T_LPAREN") ){

					ast node = this.parse_function_call(err_list, ast_list);

					if(node != null){

						ast_list.add_new_ast(node);

					}

				}else{
					this.parse_newline(err_list, ast_list);
				}

			}
			if( this.get_current_token().get_type().equals("T_LBRACE")){

				s.push(this.get_current_token());

			}

			if( this.get_current_token().get_type().equals("T_RBRACE")){

				s.pop();

			}

			if( this.get_current_token().get_type().equals("T_NULL") ){
				break;
			}

			if( this.get_current_token().get_type().equals("T_NEWLINE" )){

				this.parse_newline(err_list, ast_list);

			}

			this.get_next_token();

		}

		this.get_next_token();

		return ast_list;

	}
}
