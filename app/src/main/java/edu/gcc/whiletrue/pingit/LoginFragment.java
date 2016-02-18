package edu.gcc.whiletrue.pingit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * A placeholder fragment containing a simple view.
 */
public class LoginFragment extends Fragment implements View.OnClickListener{

    private ViewGroup fragmentContainer;
    private OnHeadlineSelectedListener mCallback;

    private EditText emailTxt;
    private EditText passTxt;

    // Container Activity must implement this interface
    public interface OnHeadlineSelectedListener {
        public void onSwitchToRegister();
        public boolean checkNetworkStatus();

    }

    public LoginFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnHeadlineSelectedListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        fragmentContainer = container;

        Button btnTemp = (Button) view.findViewById(R.id.loginBtn);
        btnTemp.setOnClickListener(this);

        Button btnSwitchToRegister = (Button) view.findViewById(R.id.switchToRegisterBtn);
        btnSwitchToRegister.setOnClickListener(this);

        emailTxt = (EditText) view.findViewById(R.id.loginEmailTxt);
        passTxt = (EditText) view.findViewById(R.id.loginPasswordTxt);

        return view;
    }

    @Override
    public void onClick(View v) {
        final View view = v;//final reference to the view that called onClick

        switch (v.getId()){
            case R.id.switchToRegisterBtn:
                mCallback.onSwitchToRegister();
                break;
            case R.id.loginBtn:
                ParseUser.logOut();//make sure the user is logged out first

                if (!mCallback.checkNetworkStatus()){
                    Toast.makeText(fragmentContainer.getContext(),
                            getString(R.string.noNetworkConnectionMsg), Toast.LENGTH_SHORT).show();
                    break;
                }

                loginUser(emailTxt.getText().toString().toLowerCase(), passTxt.getText().toString(),view);

                break;
            default:
                break;
        }
    }

    private void loginUser(String email, String pass, final View view){

        ParseUser.logInInBackground(emailTxt.getText().toString().toLowerCase(), passTxt.getText().toString(), new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    Toast.makeText(fragmentContainer.getContext(), "Login Successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(view.getContext(), HomeActivity.class);
                    startActivity(intent);
                } else{

                    AlertDialog.Builder builder = new AlertDialog.Builder(fragmentContainer.getContext());
                    builder.setTitle(R.string.app_name);
                    builder.setPositiveButton("Okay", null);

                    switch(e.getCode()){
                        case 101://Invalid login credentials
                            builder.setMessage("Username or password are incorrect. Please try again.");
                            break;
                        default://handles all other parse exceptions
                            builder.setMessage("Error (" + e.getCode() + ") " + e.getMessage());
                            break;
                    }

                    //build and display alert dialog for the user
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            }
        });
    }
}
