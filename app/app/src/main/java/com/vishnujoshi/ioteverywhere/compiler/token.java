class token{
	
	private String type;
	private String content;

	public token(String type, String content){

		this.type = type;
		this.content = content;

	}

	public String get_type(){

		return this.type;

	}

	public String get_content(){

		return this.content;

	}

}
