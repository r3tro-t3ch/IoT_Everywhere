package com.vishnujoshi.ioteverywhere.compiler;

import android.content.SyncAdapterType;
import android.os.CpuUsageInfo;

import java.util.ArrayList;
import java.util.Stack;

public class expression {

    private ArrayList<token> token_list;
    private Stack<token> stack;
    private boolean STRING_FLAG;

    public expression(ArrayList<token> token_list){

        this.token_list = token_list;
        stack = new Stack<token>();

    }

    public expression(){
        stack = new Stack<token>();
    }

    public boolean is_expression_token(token t){

        if(t.get_type().equals("T_IDENTIFIER") ||
           t.get_type().equals("T_CONSTANT") ||
           t.get_type().equals("T_PLUS") ||
           t.get_type().equals("T_MINUS") ||
           t.get_type().equals("T_ASTERIX") ||
           t.get_type().equals("T_FSLASH") ||
           t.get_type().equals("T_MOD") ||
           t.get_type().equals("T_STRING") ){

            return true;

        }else{
            return false;
        }

    }

    public boolean is_operator(token t){

        if(
            t.get_type().equals("T_PLUS") ||
            t.get_type().equals("T_MINUS") ||
            t.get_type().equals("T_ASTERIX") ||
            t.get_type().equals("T_FSLASH") ||
            t.get_type().equals("T_MOD")){

            return true;

        }else{
            return false;
        }
    }

    public boolean string_present(ArrayList<token> token_list, symbol_table table){

        boolean flag = false;

        for(token t: token_list){

            if( t.get_type().equals("T_STRING") ){
                flag = true;
                break;
            }else if( t.get_type().equals("T_IDENTIFIER")){

                symbol s = table.search_symbol(t.get_content());

                if(s.getData_type().equals("STRING")){
                    flag = true;
                    break;
                }

            }

        }

        return flag;

    }

    public boolean is_valid_string_expr(ArrayList<token> token_list){

        boolean flag = true;

        for(token t : token_list){

            if( t.get_type().equals("T_MINUS") ||
                t.get_type().equals("T_ASTERIX") ||
                t.get_type().equals("T_FSLASH")){

                flag = false;
                break;

            }

        }

        return flag;

    }

    public String evaluate_expression(ArrayList<token> token_list,errors err_list, symbol_table table, int line){

        if(token_list.size() == 1){

            if(this.string_present(token_list ,table)){
                if(this.is_valid_string_expr(token_list )){
                    STRING_FLAG = true;
                }
            }

            return token_list.get(0).get_content();

        }

        String answer = new String();

        if(string_present(token_list , table)){

            if(this.is_valid_string_expr(token_list)){

                STRING_FLAG = true;

                for(token t : token_list){

                    if( t.get_type().equals("T_CONSTANT") ||
                        t.get_type().equals("T_STRING")){

                        stack.push(t);

                    }else if( t.get_type().equals("T_IDENTIFIER")){

                        symbol s = table.search_symbol(t.get_content());

                        if( s != null){

                            stack.push(t);

                        }else{

                            err_list.add_new_error(new error("variable " + t.get_content() + " is not declared", line));

                        }

                    }else{

                        String left_operand, right_operand;

                        token left = stack.pop();
                        token right = stack.pop();

                        if( left.get_type().equals("T_IDENTIFIER") ||
                            right.get_type().equals("T_IDENTIFIER")){

                            if( left.get_type().equals("T_IDENTIFIER") &&
                                !right.get_type().equals("T_IDENTIFIER")){

                                symbol s = table.search_symbol(left.get_content());
                                left_operand = s.getValue();
                                right_operand = right.get_content();

                            }else if(!left.get_type().equals("T_IDENTIFIER") &&
                                    right.get_type().equals("T_IDENTIFIER")){

                                symbol s = table.search_symbol(right.get_content());

                                right_operand = s.getValue();
                                left_operand = left.get_content();

                            }else{

                                symbol l, r;

                                l = table.search_symbol(left.get_content());
                                r = table.search_symbol(right.get_content());

                                left_operand = l.getValue();
                                right_operand = r.getValue();

                            }

                        }else{

                            left_operand = left.get_content();
                            right_operand = right.get_content();
                        }

                        if( t.get_type().equals("T_PLUS")){

                            answer = left_operand + right_operand;

                            stack.push(new token("T_STRING", answer));
                        }

                    }

                }

                return stack.peek().get_content();

            }else{

                err_list.add_new_error(new error("Invalid expression", line));

            }

        }else{

            for( token t: token_list){

                if( t.get_type().equals("T_CONSTANT")){

                    stack.push(t);

                }else if( t.get_type().equals("T_IDENTIFIER")){

                    symbol s = table.search_symbol(t.get_content());

                    if( s != null ){

                        stack.push(t);

                    }else{

                        err_list.add_new_error(new error("Variable " + t.get_content() + " is not declared", line));

                    }

                }else {

                    int left_operand, right_operand;

                    token left = stack.pop();
                    token right = stack.pop();

                    if( left.get_type().equals("T_IDENTIFIER") ||
                            right.get_type().equals("T_IDENTIFIER")){

                        if( left.get_type().equals("T_IDENTIFIER") &&
                                !right.get_type().equals("T_IDENTIFIER")){

                            symbol s = table.search_symbol(left.get_content());
                            left_operand = Integer.parseInt(s.getValue());
                            right_operand = Integer.parseInt(right.get_content());

                        }else if(!left.get_type().equals("T_IDENTIFIER") &&
                                right.get_type().equals("T_IDENTIFIER")){

                            symbol s = table.search_symbol(right.get_content());

                            right_operand = Integer.parseInt(s.getValue());
                            left_operand = Integer.parseInt(left.get_content());

                        }else{

                            symbol l, r;

                            l = table.search_symbol(left.get_content());
                            r = table.search_symbol(right.get_content());

                            left_operand = Integer.parseInt(l.getValue());
                            right_operand = Integer.parseInt(r.getValue());

                        }

                    }else{

                        left_operand = Integer.parseInt(left.get_content());
                        right_operand = Integer.parseInt(right.get_content());

                    }

                    if( t.get_type().equals("T_PLUS") ){

                        answer = String.valueOf(left_operand + right_operand);
                        stack.push(new token("T_CONSTANT", answer));

                    }else if( t.get_type().equals("T_MINUS") ){

                        answer = String.valueOf(left_operand - right_operand);
                        stack.push(new token("T_CONSTANT", answer));

                    }else if( t.get_type().equals("T_ASTERIX") ){

                        answer = String.valueOf(left_operand * right_operand);
                        stack.push(new token("T_CONSTANT", answer));

                    }else if( t.get_type().equals("T_FSLASH") ){

                        answer = String.valueOf(left_operand / right_operand);
                        stack.push(new token("T_CONSTANT", answer));

                    }

                }

            }

        }

        return stack.peek().get_content();

    }

    private int check_precedence(token t){

        if( t.get_type().equals("T_ASTERIX") ||
            t.get_type().equals("T_FSLASH") ||
            t.get_type().equals("T_MOD") ){

            return 3;

        }else if( t.get_type().equals("T_PLUS") ||
                  t.get_type().equals("T_MINUS") ){

            return 2;

        }else{
            return 1;
        }

    }

    public ArrayList<token> infix_to_postfix(ArrayList<token> list){

        ArrayList<token> rev = this.reverse_token_list(list);

        rev.add(0, new token("T_LPAREN", "("));

        rev.add(new token("T_RPAREN", ")"));

        ArrayList<token>postfix = new ArrayList<token>();

        Stack<token> stack = new Stack<token>();

        for(token t : rev){

            if( t.get_type().equals("T_CONSTANT") ||
                t.get_type().equals("T_IDENTIFIER") ||
                t.get_type().equals("T_STRING")){

                postfix.add(t);

            }else if( t.get_type().equals("T_LPAREN")){

                stack.push(t);

            }else if( t.get_type().equals("T_RPAREN")){

                while ( !stack.peek().get_type().equals("T_LPAREN") ){

                    postfix.add(stack.peek());
                    stack.pop();

                }

                stack.pop();

            }else{

                if( this.is_operator(t)){

                    while (this.check_precedence(t) <= this.check_precedence(stack.peek())){

                        postfix.add(stack.peek());
                        stack.pop();

                    }

                    stack.push(t);

                }

            }

        }

        return postfix;

    }

    boolean is_postfix_valid(ArrayList<token> list){

        if(list.size() == 1){
            return true;
        }

        int operand_count = 0, operator_count = 0;

        for(token t : list){

            if( this.is_operator(t)){

                operator_count++;

            }else{

                operand_count++;

            }

        }

        //postfix expression validation
        //1 -> first two elements are operand
        //2 -> last element is always an operator
        //3 -> for every n operands there are n-1 operators

        if( is_operator(list.get(0)) &&
            is_operator(list.get(1)) &&
            operand_count == operator_count+1 &&
            !is_operator(list.get(list.size() - 1 ))){

            return true;

        }

        return false;

    }

    public ArrayList<token> reverse_token_list(ArrayList<token> list){

        ArrayList<token> reverse = new ArrayList<token>();

        for(int i = list.size() - 1; i >= 0; i--){

            reverse.add(list.get(i));

        }

        return reverse;

    }


    public ArrayList<token> getToken_list() {
        return token_list;
    }

    public void setToken_list(ArrayList<token> token_list) {
        this.token_list = token_list;
    }

    public Stack<token> getStack() {
        return stack;
    }

    public void setStack(Stack<token> stack) {
        this.stack = stack;
    }

    public boolean getSTRING_FLAG() {
        return STRING_FLAG;
    }

    public void setSTRING_FLAG(boolean STRING_FLAG) {
        this.STRING_FLAG = STRING_FLAG;
    }
}
