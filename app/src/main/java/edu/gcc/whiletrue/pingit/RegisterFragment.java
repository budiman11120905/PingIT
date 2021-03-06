package edu.gcc.whiletrue.pingit;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import java.util.Objects;


public class RegisterFragment extends Fragment implements View.OnClickListener {

    private ViewGroup fragmentContainer;
    private OnHeadlineSelectedListener mCallback;

    private EditText nameTxt;
    private EditText emailTxt;
    private EditText passTxt;
    private EditText passConfirmTxt;
    private SignUpTask signUpTask;
    private AlertDialog signUpDialog;

    // Container Activity must implement this interface
    public interface OnHeadlineSelectedListener {
        public void onSwitchToLogin();
        public boolean checkNetworkStatus();
    }

    public RegisterFragment() {
        // Required empty public constructor
    }

    public static boolean hasDigitsAndLetters(String pass)
    {
        return (hasDigits(pass) && hasLetters(pass));
    }

    public static boolean hasLetters(String pass)
    {
        for(int i=0; i<pass.length(); i++)
        {
            if(Character.isLetter(pass.charAt(i)))
            {
                return true;
            }

        }
        return false;
    }

    public static boolean hasDigits(String pass)
    {
        for(int i=0; i<pass.length(); i++)
        {
            if(Character.isDigit(pass.charAt(i)))
            {
                return true;
            }

        }
        return false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {mCallback = (OnHeadlineSelectedListener) getActivity();
        } catch (ClassCastException e) { throw new ClassCastException(getActivity().toString()
                    + " must implement OnHeadlineSelectedListener");}
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        fragmentContainer = container;

        Button btnTemp = (Button) view.findViewById(R.id.registerBtn);
        btnTemp.setOnClickListener(this);

        Button btnSwitchToLogin = (Button) view.findViewById(R.id.switchToLoginBtn);
        btnSwitchToLogin.setOnClickListener(this);

        nameTxt = (EditText) view.findViewById(R.id.registerNameTxt);
        emailTxt = (EditText) view.findViewById(R.id.registerEmailTxt);
        passTxt = (EditText) view.findViewById(R.id.registerPasswordTxt);
        passConfirmTxt = (EditText) view.findViewById(R.id.registerConfirmPassword);

        //create the signup dialog to display while the signup thread is running later
        AlertDialog.Builder builder = new AlertDialog.Builder(fragmentContainer.getContext());
        builder.setTitle(R.string.app_name);
        builder.setView(inflater.inflate(R.layout.dialog_signin, null));
        builder.setNegativeButton(R.string.dialogCancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                signUpTask.cancel(true);//cancel the signup background thread
            }
        });
        signUpDialog = builder.create();

        return view;
    }

    //handles all onClicke events for this fragment
    @Override
    public void onClick(View v) {

        ParseUser.logOut();

        final View view = v; //creating an Intent or Toast requires a view casted as 'final'

        switch (v.getId()){//identify what item was pressed

            case R.id.switchToLoginBtn:
                mCallback.onSwitchToLogin();
                break;
            case R.id.registerBtn:
                //start building an alert dialog incase there was an issue with registration credentials
                AlertDialog.Builder builder = new AlertDialog.Builder(fragmentContainer.getContext());
                builder.setTitle(R.string.app_name);
                builder.setPositiveButton(R.string.dialogConfirm, null);

                String dialogMsg = "";

                //sanitize and validate registration credentials before sending to parse

                String EmailStr = emailTxt.getText().toString();

                if (Objects.equals(nameTxt.getText().toString(), ""))
                    dialogMsg = getString(R.string.nameNotValid);
                else if (EmailStr.length() <=7  ||
                !EmailStr.substring(EmailStr.length() - 7).contains("gcc.edu"))
                    dialogMsg = getString(R.string.emailNotValid);
                else if (!Objects.equals(passTxt.getText().toString(), passConfirmTxt.getText().toString()))
                    dialogMsg = getString(R.string.passwordsDontMatch);
                else if (passTxt.getText().toString().length()<8)
                    dialogMsg = getString(R.string.passwordTooShort);
                else if (!hasDigitsAndLetters(passTxt.getText().toString()))
                    dialogMsg = getString(R.string.passwordMissingCharacters);


                //display dialog if there were any issues
                if (dialogMsg != ""){
                    builder.setMessage(dialogMsg);
                    AlertDialog errorDialog = builder.create();
                    errorDialog.show();
                    break;
                }

                //create a ParseUser with the provided credentials
                ParseUser user = new ParseUser();
                user.put("friendlyName", nameTxt.getText().toString());
                user.setUsername(emailTxt.getText().toString().toLowerCase());
                user.setEmail(emailTxt.getText().toString());
                user.setPassword(passTxt.getText().toString());

                //dismiss keyboard
                InputMethodManager imm = (InputMethodManager)getActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                //show register dialog with progress spinner while Parse executes in the background
                signUpDialog.show();
                signUpTask = new SignUpTask(user, view,fragmentContainer.getContext());
                signUpTask.execute();//attempt to signup in the background

                break;
            default:
                break;
        }
    }

    private class SignUpTask extends AsyncTask<String, Void, Integer> {
        ParseUser user;
        final View view;
        Context context;

        public SignUpTask(ParseUser user, View view, Context context) {
            this.user = user;
            this.view = view;
            this.context = context;
        }

        //this is the actual background process
        @Override
        protected Integer doInBackground(String... params) {
            if (!mCallback.checkNetworkStatus())
                return -1;//no internet, couldn't even attempt to login

            ParseUser.logOut();//make sure the user is logged out first

            try {
                user.signUp();
                ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                installation.put("user",ParseUser.getCurrentUser());
                installation.saveInBackground();
            } catch (ParseException e) {return e.getCode();}//return exception code
            return 0;//no issues
        }

        //this is run on UI thread after the background thread completes
        @Override
        protected void onPostExecute(Integer errorCode) {
            super.onPostExecute(errorCode);
            signUpDialog.dismiss();

            if (errorCode == 0){//Registration succeeded! Open home activity!
                ((MainApplication)getActivity().getApplication()).currentPage=0;
                //record successful login
                SecurePreferences preferences = new SecurePreferences(getContext(),getString(R.string.pref_login),SecurePreferences.generateDeviceUUID(getContext()),true);
                preferences.put(getString(R.string.pref_login_username),emailTxt.getText().toString());
                preferences.put(getString(R.string.pref_login_password), passTxt.getText().toString());

                Toast.makeText(fragmentContainer.getContext(), R.string.registerSuccessMsg, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(view.getContext(), HomeActivity.class);
                startActivity(intent);
                getActivity().finish();
            }else{
                //Something went wrong, display a new dialog explaining what happened

                AlertDialog.Builder builder = new AlertDialog.Builder(fragmentContainer.getContext());
                builder.setTitle(R.string.app_name);
                builder.setPositiveButton(R.string.dialogConfirm, null);

                switch (errorCode){
                    case -1://no internet connection
                        builder.setMessage(R.string.noNetworkConnectionMsg);
                        break;
                    case ParseException.EMAIL_TAKEN:
                    case ParseException.USERNAME_TAKEN:
                        builder.setMessage(user.getEmail() + context.getString(R.string.emailAlreadyInUse));
                        break;
                    case ParseException.INVALID_EMAIL_ADDRESS:
                        builder.setMessage("\"" + user.getEmail() + "\"" +
                                context.getString(R.string.isNotAValidEmail));
                        break;
                    default://handles all other parse exceptions
                        builder.setMessage("Error (" + errorCode + ") ");
                        break;
                }

                //build and display alert dialog for the user
                AlertDialog errorDialog = builder.create();
                errorDialog.show();
            }
        }
    }



    }
