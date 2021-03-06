package edu.gatech.bobsbuilders.socialsaver;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.util.Locale;


public class SignUpUser extends Activity {
    private EditText name, email, password, passwordverify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_user);
        name = (EditText)findViewById(R.id.namefield);
        email = (EditText)findViewById(R.id.emailfield);
        password = (EditText)findViewById(R.id.password);
        passwordverify = (EditText)findViewById(R.id.confirmpassword);


        findViewById(R.id.canceluserbutton).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent goBack = new Intent(SignUpUser.this,LoginPage.class);
                finish();
                startActivity(goBack);
            }
        });

        findViewById(R.id.submituserbutton).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(isEmpty(name) || isEmpty(email) || isEmpty(password) || isEmpty(passwordverify)) {
                    Toast.makeText(SignUpUser.this,
                            "Some fields have not been filled in yet!",
                            Toast.LENGTH_LONG).show();
                } else {
                    ParseUser user = new ParseUser();
                    String pass = password.getText().toString();
                    String passverify = passwordverify.getText().toString();
                    if (pass.equals(passverify)) {
                        //continue sign up process
                        String emailclearwhite = email.getText().toString().toLowerCase(Locale.getDefault());
                        String newuserinfo = emailclearwhite.replaceAll("\\s+", "");
                        user.setEmail(newuserinfo);
                        user.setUsername(newuserinfo);

                        user.setPassword(password.getText().toString());
                        String nameNoWhite = name.getText().toString().toLowerCase();
                        user.put("name", nameNoWhite.replaceAll("\\s+$", ""));
                        user.put("isOn","true");
                        user.put("reportCount","0");
                        user.put("Rating","0.0");

                        /*START Add generic image of user*/

                        Drawable drawable = getResources().getDrawable(R.drawable.black200);
                        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byte[] data = stream.toByteArray();
                        ParseFile imagefile = new ParseFile("image.jpg", data);
                        imagefile.saveInBackground();

                        /*END Add generic image of user*/
                        user.put("userImage",imagefile);
                        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                        installation.put("isOn","true");
                        installation.put("useremail", emailclearwhite);

                        try {
                            installation.save();
                        } catch(Exception e) {
                            Toast.makeText(SignUpUser.this,"Something went wrong. Please try again or check your internet connection.", Toast.LENGTH_LONG).show();
                        }

                        user.signUpInBackground(new SignUpCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
                                    // Show the error message
                                    e.getMessage();
                                    Toast.makeText(SignUpUser.this,"Something went wrong. Please try again or check your internet connection.", Toast.LENGTH_LONG).show();

                                } else {
                                    // Start an intent for the dispatch activity
                                    Intent intent = new Intent(SignUpUser.this,MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            }
                        });

                    } else {
                        Toast.makeText(SignUpUser.this,
                                "Sorry, your passwords dont match up.",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


    }

    /**
     * Checks field inputted to see if field is empty
     *
     * @param  etText field to check
     */
    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() <= 0;
    }
}
