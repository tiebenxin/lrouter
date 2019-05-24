package xzzb.com.runningannotation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import xzzb.com.processor_lib.LRoute;


@LRoute(path = "/main/main1")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //按钮跳转
        findViewById(R.id.btn_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转Main2页面
                LRouter.getInstance().build("Main2").navigation();
            }
        });

        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转Main2页面
                LRouter.getInstance().build("Login").navigation();
            }
        });


    }
}
