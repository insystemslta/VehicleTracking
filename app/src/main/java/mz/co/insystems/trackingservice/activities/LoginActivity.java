package mz.co.insystems.trackingservice.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mz.co.insystems.trackingservice.R;
import mz.co.insystems.trackingservice.dao.UserDAO;
import mz.co.insystems.trackingservice.model.User;

public class LoginActivity extends Activity {

    private UserDAO userDao;
    protected User user;

    private EditText loginNumber;
    private EditText password;

    ProgressDialog loginProgress;

    private List<User> users;
    private User userFromForm;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        userDao = new UserDAO(this);
        userDao.open();
        users = new ArrayList<>();
        users = userDao.getAll(this);
        user = new User();

        loginNumber                 = (EditText) findViewById(R.id.login_contact);
        password 					= (EditText) findViewById(R.id.login_password);
        final Button login 			= (Button) 	 findViewById(R.id.btn_login);
        TextView userRegistation 	= (TextView) findViewById(R.id.user_registation);
        TextView passwordReset 		= (TextView) findViewById(R.id.user_forgot_password);

		/*
		 * Verifica se deve ser realizada a primeira sincronizacao de utilizador
		 * caso sim direcciona o utilizador para o ecra de sincronizacao
		 */
        if (databaseContainsUsers()) {
            redirectToFisrtUserSync();
        }

        Bundle loginNumberFromFirstSync = getIntent().getExtras();
        if (loginNumberFromFirstSync != null) {
            loginNumber.setText(loginNumberFromFirstSync.getString("loginNumber"));
        }

		/*
		 * Inicia a actividade de registo de utilizador
		 */
        userRegistation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
               // Intent i = new Intent(getApplicationContext(), UserCreateActivity.class);
               // startActivity(i);
            }
        });

		/*
		 * Executa o login quando a tecla finish é precionado no teclado
		 */
        password.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    login.performClick();
                    return true;
                }
                return false;
            }
        });

		/*
		 * Inicia a actividade de reposição da palavra-passe
		 */
        passwordReset.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AccountConfirmRequest.class);
                startActivity(i);
            }
        });

		/*
		 * Inicia o login do utilizador quando o botão entrar é precionado
		 */
        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                loginProgress = ProgressDialog.show(LoginActivity.this, "",
                        getString(R.string.user_authentication_in_progress));
                loginProgress.setCancelable(false);
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            //Verifica se os campos foram preenchidos

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                if (loginFieldsComplete()) {
                    userFromForm = new User(loginNumber.getText().toString().trim(),
                            password.getText().toString().trim());

                    if (login(userFromForm)) {
                        loginProgress.dismiss();
                        redirectToVehicleList();
                    }
                }

                if (!loginFieldsComplete()) {
                    loginProgress.dismiss();
                    Toast.makeText(LoginActivity.this,
                            getString(R.string.invalid_user),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * @return
     */
    private boolean loginFieldsComplete() {
        return !loginNumber.getText().toString().trim().isEmpty() && !password.getText().toString().trim().isEmpty();
    }

    private void redirectToFisrtUserSync() {
        Intent it = new Intent(LoginActivity.this, FirstSyncActivity.class);
        startActivity(it);
        finish();
    }

    private boolean databaseContainsUsers(){
        return (users == null)? true : false;
    }

    private boolean isUserValidOnLocalDB(User user){
        return (userDao.validateOnDB(user))? true : false;
    }

    private User loadValidUser(User user){
        return userDao.getFromLogin(user, getApplicationContext());
    }


    private void redirectToVehicleList(){
        if (userDao.isOpened()) userDao.close();
        Intent intent = new Intent(LoginActivity.this, VehicleList.class);
        intent.putExtra("user", user);
        startActivity(intent);
        finish();
    }



    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPause() {
        password.setText(null);
        if (userDao.isOpened()) userDao.close();
        super.onPause();
    }

    @Override
    protected void onResume() {
        userDao.open();
        super.onResume();
    }

    private boolean login(User userFromForm) {
        boolean isLoggedIn = false;
        if (isUserValidOnLocalDB(userFromForm)) {
            user = loadValidUser(userFromForm);
            if (user.isActive()) {
                isLoggedIn = true;
            }else {
                loginProgress.dismiss();
                Toast.makeText(LoginActivity.this,
                        getString(R.string.user_not_authorized),
                        Toast.LENGTH_LONG).show();
            }
        }else {
            loginProgress.dismiss();
            Toast.makeText(LoginActivity.this,
                    getString(R.string.invalid_user),
                    Toast.LENGTH_LONG).show();
        }
        return isLoggedIn;
    }
}