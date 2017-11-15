package com.example.im.googlesign;

import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * Created by Im on 14-11-2017.
 */

public class dialogFragment extends DialogFragment implements View.OnClickListener ,android.text.TextWatcher {
    Button ok, cancel;
    EditText fName, address;
    private String fnames;
    private String useraddress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialogfragment, container, false);
        ok = (Button) rootView.findViewById(R.id.ok);
        cancel = (Button) rootView.findViewById(R.id.cancel);
        fName = (EditText) rootView.findViewById(R.id.fName);
        address = (EditText) rootView.findViewById(R.id.address);

        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);
        fName.addTextChangedListener(this);
        address.addTextChangedListener(this);
        getDialog().setTitle("Enter Some More Info.");
        getDialog().setCancelable(false);
        return rootView;
    }

    @Override
    public void onClick(View view) {


        int id = view.getId();
        switch (id) {
            case R.id.ok: {

                fnames = fName.getText().toString();

                useraddress = address.getText().toString();
//                validate(fnames);
//                validate(useraddress);

//               MainActivity.showtext(fatherName,userAdress);
                Toast.makeText(getActivity(), "Ok button is Clicked", Toast.LENGTH_SHORT).show();
                MainActivity callingActivity = (MainActivity) getActivity();
                callingActivity.showtext(fName.getText().toString(), address.getText().toString());
//                setfname(fName.getText().toString());
//                setaddress(address.getText().toString());
                ((EditText) fName.findViewById(R.id.fName)).setText("");
                ((EditText) address.findViewById(R.id.address)).setText("");
                dismiss();

                break;
            }
            case R.id.cancel: {
                Toast.makeText(getActivity(), "Cancel button is Pressed", Toast.LENGTH_SHORT).show();
                ((EditText) fName.findViewById(R.id.fName)).setText("");
                ((EditText) address.findViewById(R.id.address)).setText("");
                dismiss();
                break;
            }
        }

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        fnames = fName.getText().toString();
        useraddress = address.getText().toString();
        if (fnames.isEmpty()) {
            fName.setError("Please enter Name");
            ok.setEnabled(false);
        } else if (useraddress.isEmpty()) {
            address.setError("Please enter Address");
            ok.setEnabled(false);
        } else
            ok.setEnabled(true);
// validation code goes here
    }
}