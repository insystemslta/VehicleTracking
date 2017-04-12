
package mz.co.insystems.trackingservice.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import mz.co.insystems.trackingservice.R;
import mz.co.insystems.trackingservice.dao.UserDAO;
import mz.co.insystems.trackingservice.model.User;
import mz.co.insystems.trackingservice.sync.InSystemsSyncService;
import mz.co.insystems.trackingservice.sync.Sync;

public class FirstSyncActivity extends AppCompatActivity {

    private Button syncSubmit;
    private Button syncCancel;
    private EditText userContact;

    private String loginNumber;
    protected ProgressDialog syncProgress;

    private Handler handler = new Handler(){
        public void handleMessage(Message message){
            if (message.arg1 == RESULT_OK) {
                if (syncProgress.isShowing()) syncProgress.dismiss();
                redirectToLogin(loginNumber);
            }else if (message.arg1 == RESULT_CANCELED) {
                if (syncProgress.isShowing()) syncProgress.dismiss();
                Toast.makeText(getApplicationContext(),
                        getString(R.string.invalid_user_contact), Toast.LENGTH_LONG).show();
            } else{
                if (syncProgress.isShowing()) syncProgress.dismiss();
                Toast.makeText(getApplicationContext(),
                        getString(R.string.error_during_sync), Toast.LENGTH_LONG).show();
            }
        };
    };

    UserDAO userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_sync);

        setupToolbar();

        userDao = new UserDAO(this);
        userDao.open();

        if (userDao.getAll(getApplicationContext()) != null) redirectToLogin(null);
        if (userDao.isOpened()) userDao.close();

        syncSubmit = (Button) findViewById(R.id.first_sync_submit);
        syncCancel = (Button) findViewById(R.id.first_sync_cancel);
        userContact = (EditText) findViewById(R.id.first_sync_contact);

        syncCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        syncSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginNumber = userContact.getText().toString().trim();
                if (!loginNumber.isEmpty() || loginNumber != null || !loginNumber.equals(null)) {
                    final User user = new User();
                    user.setLoginNumber(loginNumber);

                    syncProgress = ProgressDialog.show(FirstSyncActivity.this, ""
                            , getString(R.string.first_sync_in_progress));
                    syncProgress.setCancelable(true);
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                Intent userSyncService = new Intent(getApplicationContext(), InSystemsSyncService.class);
                                Messenger messenger = new Messenger(handler);
                                userSyncService.putExtra("messenger", messenger);
                                userSyncService.putExtra("user", user);
                                userSyncService.putExtra("syncType", Sync.FIRST_USER_SYNC);
                                startService(userSyncService);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }else {
                    Toast.makeText(FirstSyncActivity.this,
                            getString(R.string.invalid_user_contact),
                            Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
        toolbar.bringToFront();


        ActionBar actionbar = getSupportActionBar();
        actionbar.setIcon(R.mipmap.ic_launcher);
    }

    protected void redirectToLogin(String loginNumber) {
        Intent loginIntent = new Intent(FirstSyncActivity.this, LoginActivity.class);
        if (loginNumber != null) loginIntent.putExtra("loginNumber", loginNumber);
        if (userDao.isOpened()) userDao.close();
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);
        finish();
    }

    @Override
    protected void onPause() {
        userDao.close();
        super.onPause();
    }

    @Override
    protected void onResume() {
        userDao.open();
        super.onResume();
    }
}
