package com.vishnujoshi.ioteverywhere.compiler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorManager;
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
        symbol_table table = new symbol_table();
        symbol temp_s = new symbol();

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

                    }else if (temp_ast.get_function_name().equals("input")){

                        ArrayList arg  = temp_ast.get_args_list();

                        function_arg arg1 = (function_arg) arg.get(0);

                        if(k.is_keyword(arg1.get_arg_name())){

                            SensorManager sensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);

                            if(arg1.get_arg_name().equals("LIGHT")){

                                Sensor light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
                                //temp_ast.setFunction_return_value(light.);

                            }

                        }else{

                            err_list.add_new_error(new error("sensor " + arg1.get_arg_name() + " not present", temp_ast.get_ast_node_index()));

                        }

                    }

                }//TODO(user defined function)
            }else if(temp_ast.get_type().equals("AST_VAR_DEF")){

                temp_s = table.search_symbol(temp_ast.get_var_def_var_name());

                if(temp_s == null) {

                    table.add_new_symbol(new symbol(temp_ast.get_var_def_var_name(),
                            "NA",
                            "NA"));

                }else{

                    err_list.add_new_error(new error("symbol " + temp_ast.get_var_def_var_name() + " already present", temp_ast.get_ast_node_index()));

                }

            }else if(temp_ast.get_type().equals("AST_VAR_DEF_ASSIGNMENT_CONSTANT")){

                temp_s = table.search_symbol(temp_ast.get_var_def_var_name());

                if(temp_s == null) {

                    table.add_new_symbol(new symbol(temp_ast.get_var_def_var_name(),
                            temp_ast.get_var_def_var_content(),
                            "T_CONSTANT"));

                }else{

                    err_list.add_new_error(new error("symbol " + temp_ast.get_var_def_var_name() + " already present", temp_ast.get_ast_node_index()));

                }

            }else if(temp_ast.get_type().equals("AST_VAR_DEF_ASSIGNMENT_IDENTIFIER")){

                temp_s = table.search_symbol(temp_ast.get_var_def_var_name());

                if(temp_s == null) {

                    temp_s = table.search_symbol(temp_ast.get_var_def_var_content());

                    if(temp_s != null) {

                        table.add_new_symbol(new symbol(temp_ast.get_var_def_var_name(),
                                temp_ast.get_var_def_var_content(),
                                "T_CONSTANT"));

                    }else{
                        err_list.add_new_error(new error("symbol " + temp_ast.get_var_def_var_name() + " not present", temp_ast.get_ast_node_index()));
                    }
                }else{

                    err_list.add_new_error(new error("symbol " + temp_ast.get_var_def_var_name() + " already present", temp_ast.get_ast_node_index()));

                }
            }else if(temp_ast.get_type().equals("AST_VAR_DEF_ASSIGNMENT_FUNCTION")){

                temp_s = table.search_symbol(temp_ast.get_var_def_var_name());

                table.add_new_symbol(new symbol(temp_ast.get_var_def_var_name(),
                        temp_ast.get_var_def_var_content(),
                        "T_CONSTANT"));

            }


        }

    }

}
