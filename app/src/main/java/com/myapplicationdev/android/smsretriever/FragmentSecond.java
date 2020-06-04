package com.myapplicationdev.android.smsretriever;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentSecond#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSecond extends Fragment {
    Button btnRetrieve2,btnEmail;
    TextView tvSms2;
    EditText etWord;
    String word,body;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentSecond() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentSecond.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentSecond newInstance(String param1, String param2) {
        FragmentSecond fragment = new FragmentSecond();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);

            tvSms2 = view.findViewById(R.id.tvFrag2);
            etWord = view.findViewById(R.id.etWord);
            btnRetrieve2 = view.findViewById(R.id.btnRetrieve2);
            btnEmail = view.findViewById(R.id.btnEmail);
            btnRetrieve2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int permissionCheck = PermissionChecker.checkSelfPermission
                            (getActivity(), Manifest.permission.READ_SMS);

                    if (permissionCheck != PermissionChecker.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.READ_SMS}, 0);
                        // stops the action from proceeding further as permission not
                        //  granted yet
                        return;
                    }
                    // Create all messages URI
                    Uri uri = Uri.parse("content://sms");
                    // The columns we want
                    //  date is when the message took place
                    //  address is the number of the other party
                    //  body is the message content
                    //  type 1 is received, type 2 sent
                    String[] reqCols = new String[]{"date", "address", "body", "type"};

                    // Get Content Resolver object from which to
                    //  query the content provider
                    ContentResolver cr = getActivity().getContentResolver();
                    // The filter String
                    word = etWord.getText().toString();

                   String [] seperated = word.split(" ");
                    String filter= "";
                    // The matches for the ?
                    String[] filterArgs = new String[]{};
                    if(seperated.length == 1){
                        filter ="body LIKE ?";
                        filterArgs = new String[]{"%" + seperated[0] + "%"};
                    }
                    if(seperated.length == 2){
                        filter = "body LIKE ? OR body LIKE?";
                        filterArgs = new String[]{"%" + seperated[0] + "%","%" + seperated[1] + "%"};
                    }

                    // Fetch SMS Message from Built-in Content Provider

                    Cursor cursor = cr.query(uri, reqCols, filter, filterArgs, null);
                    // Fetch SMS Message from Built-in Content Provider
                    String smsBody = "";
                    if (cursor.moveToFirst()) {
                        do {
                            long dateInMillis = cursor.getLong(0);
                            String date = (String) DateFormat
                                    .format("dd MMM yyyy h:mm:ss aa", dateInMillis);
                            String address = cursor.getString(1);
                            body = cursor.getString(2);
                            String type = cursor.getString(3);
                            type = "Sent:";

                            smsBody += type + " " + address + "\n at " + date
                                    + "\n\"" + body + "\"\n\n";
                        } while (cursor.moveToNext());
                    }
                    tvSms2.setText(smsBody);
                }
            });
            btnEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // The action you want this intent to do;
                    // ACTION_SEND is used to indicate sending text
                    Intent email = new Intent(Intent.ACTION_SEND);
                    // Put essentials like email address, subject & body text
                    email.putExtra(Intent.EXTRA_EMAIL,
                            new String[]{"benedictlim@hotmail.sg"});
                    email.putExtra(Intent.EXTRA_TEXT,
                           body);
                    // This MIME type indicates email
                    email.setType("message/rfc822");
                    // createChooser shows user a list of app that can handle
                    // this MIME type, which is, email
                    startActivity(Intent.createChooser(email,
                            "Choose an Email client :"));

                }
            });

            return view;
        }
    }

