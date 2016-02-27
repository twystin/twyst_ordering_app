package com.twyst.app.android.activities;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.twyst.app.android.R;

/**
 * Created by Vipul Sharma on 2/25/2016.
 */
public class RechargeActivity extends BaseActionActivity {
    private static final int PICK_CONTACT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);

        setupToolBar();

        findViewById(R.id.ibContact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openContacts();
            }
        });
    }

    private void openContacts() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        switch (reqCode) {
            case (PICK_CONTACT):

                try {
                    if (resultCode == Activity.RESULT_OK) {
                        Uri contactData = data.getData();
                        Cursor cur = managedQuery(contactData, null, null, null, null);
                        ContentResolver contect_resolver = getContentResolver();

                        if (cur.moveToFirst()) {
                            String id = cur.getString(cur.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                            String name = "";
                            String no = "";

                            Cursor phoneCur = contect_resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);

                            if (phoneCur.moveToFirst()) {
                                name = phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                                no = phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            }

                            updateContact(no);

                            id = null;
                            name = null;
                            no = null;
                            phoneCur = null;
                        }
                        contect_resolver = null;
                        cur = null;
                        //                      populateContacts();
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    Log.e("IllegalArgException", e.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Error :: ", e.toString());
                }


                break;
        }
    }

    private void updateContact(String number) {
        if (!TextUtils.isEmpty(number)) {
            EditText etNumber = (EditText) findViewById(R.id.et_number);
            etNumber.setText(number);
        } else {
            Toast.makeText(RechargeActivity.this, "Could not read phone number!", Toast.LENGTH_SHORT).show();
        }
    }

//    private String getFormattedNumber(String number) {
//        if (number.length() > 10) {
//
//        } else if () {
//
//        }
//        return "";
//    }

}
