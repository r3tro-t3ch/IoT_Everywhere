import java.util.*;

class ast{


	private String type;
	private int ast_node_index;

	//funciton call
	private String function_name;
	private ArrayList<function_arg> args_list;


	//variable definition and assignment
	private String var_def_var_name;
	private String var_def_var_content;

	//variable
	private String var_name;
	private String var_content;

	//constructor
	public ast(String type){

		this.type = type;
		args_list = new ArrayList<function_arg>();

	}

	
	//getters and setters
	//type
	public String get_type(){
		return this.type;
	}

	public void set_type(String type){
		this.type = type;
	}
	//ast_node_index
	public int get_ast_node_index(){
		return this.ast_node_index;
	}

	public void set_ast_node_index(int index){
		this.ast_node_index = index;
	}

	//function call funciton name
	public String get_function_name(){
		return this.function_name;
	} 

	public void set_function_name(String name){
		this.function_name = name;
	}

	//function call function argument list
	public ArrayList<function_arg> get_args_list(){
		return this.args_list;
	}

	public void add_function_arg(function_arg arg){
		this.args_list.add(arg);
	}

	//function call args count
	public int get_args_count(){
		return this.args_list.size();
	}

	//var def variable name
	public String get_var_def_var_name(){
		return this.var_def_var_name;
	}

	public void set_var_def_var_name(String name){
		this.var_def_var_name = name;
	}

	//var def variable content
	public String get_var_def_var_content(){
		return this.var_def_var_content;
	}

	public void set_var_def_var_content(String content){
		this.var_def_var_content = content;
	}

	//variable name
	public String get_var_name(){
		return this.var_name;
	}

	public void set_var_name(String name){
		this.var_name = name;
	}

	//variable content
	public String get_var_content(){
		return this.var_content;
	}

	public void set_var_content(String content){
		this.var_content = content;
	}
	
}

class ast_l{

	private ArrayList<ast> ast_list;

	//constructor
	public ast_l(){
		ast_list = new ArrayList<ast>();
	}

	//add new ast
	public void add_new_ast(ast a){
		this.ast_list.add(a);
	}

	public int get_ast_list_count(){
		return this.ast_list.size();
	}

	public void print_ast(){

		for(ast i : this.ast_list){

			System.out.println(String.valueOf(i.get_ast_node_index()) + ", " + i.get_type() );

		}

	}

}

class function_arg{

	private String arg_name;
	private String arg_type;

	public function_arg(String name, String type){
		this.arg_name = name;
		this.arg_type = type;
	}

	public String get_arg_name(){
		return this.arg_name;
	}

	public String get_arg_type(){
		return this.arg_type;
	}
}
