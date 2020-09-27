package com.vishnujoshi.ioteverywhere.compiler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.util.TimeUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;

public class evaluator {

    private ast_l ast_list;
    private static String TAG = "evaluator";
    private Context context;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public evaluator(){
    }

    public evaluator(ast_l ast_list){
        this.ast_list = ast_list;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void run(){

        keywords k = new keywords();
        errors err_list = new errors();

        for(ast temp_ast : ast_list.getAst_list()){


            if(temp_ast.get_type().equals("AST_BUILTIN_FUNCTION_CALL")){

                if(k.is_builtin_function(temp_ast.get_function_name())) {

                    if (temp_ast.get_function_name().equals("output")) {

                        ArrayList args = temp_ast.get_args_list();

                        function_arg arg1 = (function_arg) args.get(0);
                        function_arg arg2 = (function_arg) args.get(1);

                        if (arg1.get_arg_name().equals("LED")) {

                            Log.e(TAG, "here");

                            CameraManager cm = (CameraManager) context.getSystemService(context.CAMERA_SERVICE);

                            if (arg2.get_arg_name().equals("HIGH")) {

                                try {
                                    cm.setTorchMode("0", true);
                                } catch (CameraAccessException e) {
                                    e.printStackTrace();
                                }

                            } else if (arg2.get_arg_name().equals("LOW")) {

                                try {
                                    cm.setTorchMode("0", false);
                                } catch (CameraAccessException e) {
                                    e.printStackTrace();
                                }
                            }

                        }

                    } else if (temp_ast.get_function_name().equals("wait")) {

                        ArrayList args = temp_ast.get_args_list();

                        function_arg arg1 = (function_arg) args.get(0);

                        if(arg1.get_arg_type().equals("T_IDENTIFIER")){

                            //TODO(add wait for identifier)

                        }else if(arg1.get_arg_type().equals("T_CONSTANT")){

                            if(k.is_num(arg1.get_arg_name())){

                                try {
                                    TimeUnit.SECONDS.sleep(Long.parseLong(arg1.get_arg_name()));
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                            }

                        }

                    }

                }//TODO(user defined function)
            } else if(temp_ast.get_type().equals("AST_VAR_DEF")){

            }


        }

    }

}
