package mz.co.insystems.trackingservice.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import mz.co.insystems.trackingservice.R;
import mz.co.insystems.trackingservice.dao.UserDAO;
import mz.co.insystems.trackingservice.sync.InSystemsSyncService;
import mz.co.insystems.trackingservice.sync.Sync;

public class AccountConfirmRequest extends AppCompatActivity {

    protected ProgressDialog syncProgress;
    private String contact;
    UserDAO userDao = new UserDAO(AccountConfirmRequest.this);

    private Handler handler = new Handler(){
        public void handleMessage(Message message){
            if (message.arg1 == RESULT_OK) {
                if (syncProgress.isShowing()) syncProgress.dismiss();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_confirm_request);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        final EditText account_contact = (EditText) findViewById(R.id.account_contact);
        final Button requestCode = (Button) findViewById(R.id.account_confirm_submit);
        final Button cancelRequestCode = (Button) findViewById(R.id.account_confirm_cancel);


        cancelRequestCode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
            }
        });

        requestCode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                contact = account_contact.getText().toString().trim();

                if ((!contact.isEmpty() || contact != null || !contact.equals(null)) && isValidContact(contact)) {
                    syncProgress = ProgressDialog.show(AccountConfirmRequest.this, ""
                            , getString(R.string.first_sync_in_progress));
                    syncProgress.setCancelable(false);
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                Intent userSyncService = new Intent(getApplicationContext(),
                                        InSystemsSyncService.class);
                                Messenger messenger = new Messenger(handler);
                                userSyncService.putExtra("contact", contact);
                                userSyncService.putExtra("syncType", Sync.PASSWORD_RESET);
                                startService(userSyncService);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }else {
                    Toast.makeText(AccountConfirmRequest.this,
                            getString(R.string.invalid_user_contact),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    protected boolean isValidContact(String contact) {

        userDao.open();
        return userDao.validateContact(contact);

    }

    protected void generatePasswordResetCode(String account_contact) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);


    }




    @Override
    protected void onPause() {
        userDao.close();
        super.onPause();
    }
}
