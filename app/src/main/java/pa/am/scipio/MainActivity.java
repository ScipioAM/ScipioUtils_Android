package pa.am.scipio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sdsmdg.tastytoast.TastyToast;

import pa.am.scipioutils_android.android.SweetDialogUtil;
import pa.am.scipioutils_android.android.SysApplication;
import pa.am.scipioutils_android.android.permission.PermissionHelper;
import pa.am.scipioutils_android.android.permission.PermissionInterface;

public class MainActivity extends AppCompatActivity implements PermissionInterface {

    private SysApplication application;
    private PermissionHelper permissionHelper;

    private Button btn_request;
    private Button btn_sweetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        setOnClick();
    }

    //==================================================================

    private void init()
    {
        application=SysApplication.getInstance();
        application.addActivity(this);
        permissionHelper=new PermissionHelper(this,this);
        btn_request=findViewById(R.id.btn_request);
        btn_sweetDialog=findViewById(R.id.btn_sweetDialog);
    }

    private void setOnClick()
    {
        btn_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permissionHelper.requestPermissions();
            }
        });
        btn_sweetDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SweetDialogUtil.showWarningDialog(true,MainActivity.this,"测试",
                        "这是测试内容","确定",null,null,null);
            }
        });
    }

    //==================================================================
    //权限请求相关
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if( permissionHelper.requestPermissionsResult(getPermissionsRequestCode(), getPermissions(), grantResults) )
            return;
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public int getPermissionsRequestCode() {
        return 1001;
    }

    @Override
    public String[] getPermissions() {
        return new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE //外部存储的读写权限
        };
    }

    @Override
    public void requestPermissionsSuccess() {
        TastyToast.makeText(this,"权限请求成功",TastyToast.LENGTH_SHORT,TastyToast.SUCCESS);
    }

    @Override
    public void requestPermissionsFail() {
        TastyToast.makeText(this,"权限请求失败",TastyToast.LENGTH_SHORT,TastyToast.ERROR);
    }
}
