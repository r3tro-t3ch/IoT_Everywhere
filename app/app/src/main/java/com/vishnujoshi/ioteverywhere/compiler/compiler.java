package com.vishnujoshi.ioteverywhere.compiler;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

public class compiler {

	private lexer l;
	private parser p;
	private ast_l ast_list;
	private evaluator e;
	private static String TAG = "compiler";

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	private Context context;


	public compiler(String code) {

		System.out.println(code);

		this.l = new lexer(code);

		this.p = new parser(this.l);


	}

	public void compile(){

		this.ast_list = new ast_l();

		this.ast_list = this.p.parse_statements();

		if(this.ast_list != null){

			ast_list.print_ast();
			this.run(this.ast_list);

		}

	}

	public void run(ast_l ast_list){

		Log.e(TAG, "here");

		e = new evaluator(ast_list);
		e.setContext(context);
		e.run();

	}

}

