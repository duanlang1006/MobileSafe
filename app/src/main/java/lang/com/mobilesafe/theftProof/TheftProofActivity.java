package lang.com.mobilesafe.theftProof;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import lang.com.mobilesafe.R;

import static android.view.Gravity.CENTER;

/**
 * Created by android on 12/7/15.
 */
public class TheftProofActivity extends Activity {
    private final String LOG_TAG = "mobilesafe";

    private SharedPreferences sharedPreferences;

    private EditText edit_password;
    private EditText edit_password_confirm;
    private Button btn_ok;
    private Button btn_cancel;

    private AlertDialog alertDialog;

    private Toast toast;

    private EditText edit_normal_pwd;
    private Button btn_normal_ok;
    private Button btn_normal_cancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.theftproof_layout);

        sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);

        if (ifFirstEntry()) {
            showFirstScreen();
        } else {
            showNormalScreen();
        }
    }

    private void showNormalScreen() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.normal_entry, null);

        edit_normal_pwd = (EditText) view.findViewById(R.id.edit_normal_pwd);
        btn_normal_ok = (Button) view.findViewById(R.id.btn_normal_ok);
        btn_normal_cancel = (Button) view.findViewById(R.id.btn_normal_cancel);

        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void showFirstScreen() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.first_entry, null);

        edit_password = (EditText) view.findViewById(R.id.edit_password);
        edit_password_confirm = (EditText) view.findViewById(R.id.edit_password_confirm);
        btn_ok = (Button) view.findViewById(R.id.btn_ok);
        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);

        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();

    }

    private boolean ifFirstEntry() {
        String password = sharedPreferences.getString("password", "");
        Log.i(LOG_TAG, "TheftProof password : " + password);
        return password.isEmpty();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_ok:
                String input = edit_password.getText().toString().trim();
                String inputconfirm = edit_password_confirm.getText().toString().trim();

                if (input.isEmpty() || inputconfirm.isEmpty()) {
                    if (toast == null) {
                        toast = Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT);
                        toast.setGravity(CENTER, 0, 0);
                        toast.show();
                    } else {
                        toast.setText("密码不能为空");
                        toast.setGravity(CENTER, 0, 0);
                        toast.show();
                    }
                    return;
                }

                if (!input.equals(inputconfirm)) {
                    if (toast == null) {
                        toast = Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT);
                        toast.setGravity(CENTER, 0, 0);
                        toast.show();
                    } else {
                        toast.setText("两次密码不一致");
                        toast.setGravity(CENTER, 0, 0);
                        toast.show();
                    }
                } else {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("password", input);
                    editor.commit();
                    alertDialog.dismiss();
                    finish();
                }

                break;
            case R.id.btn_cancel:
                alertDialog.cancel();
                finish();
                break;
            case R.id.btn_normal_ok:
                String str = edit_normal_pwd.getText().toString().trim();
                String pwd = sharedPreferences.getString("password", "");

                if (str.isEmpty()) {
                    if (toast == null) {
                        toast = Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT);
                        toast.setGravity(CENTER, 0, 0);
                        toast.show();
                    } else {
                        toast.setText("密码不能为空");
                        toast.setGravity(CENTER, 0, 0);
                        toast.show();
                    }
                }

                if (pwd.equals(str)) {
                    alertDialog.cancel();
                    return;
                } else {
                    if (toast == null) {
                        toast = Toast.makeText(this, "密码不正确", Toast.LENGTH_SHORT);
                        toast.setGravity(CENTER, 0, 0);
                        toast.show();
                    } else {
                        toast.setText("密码不正确");
                        toast.setGravity(CENTER, 0, 0);
                        toast.show();
                    }
                }

                break;
            case R.id.btn_normal_cancel:
                alertDialog.cancel();
                finish();
                break;


            default:
                break;
        }
    }
}
