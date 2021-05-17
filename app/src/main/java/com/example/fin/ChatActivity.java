//package com.example.fin;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.annotation.RequiresApi;
//import androidx.appcompat.app.ActionBar;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
//
//import android.app.ProgressDialog;
//import android.content.ActivityNotFoundException;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.icu.text.SimpleDateFormat;
//import android.icu.util.Calendar;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.speech.RecognizerIntent;
//import android.text.Editable;
//import android.text.TextUtils;
//import android.text.TextWatcher;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.android.gms.tasks.Continuation;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.ChildEventListener;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.OnProgressListener;
//import com.google.firebase.storage.StorageReference;
//import com.google.firebase.storage.StorageTask;
//import com.google.firebase.storage.UploadTask;
//import com.squareup.picasso.Picasso;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//
//import de.hdodenhof.circleimageview.CircleImageView;
//
//public class ChatActivity extends AppCompatActivity
//{
//    private String messageReceiverID, messageReceiverName, messageReceiverImage, messageSenderID;
//
//    private TextView userName, userLastSeen;
//    private CircleImageView userImage;
//
//    private Toolbar ChatToolBar;
//    private FirebaseAuth mAuth;
//    private DatabaseReference RootRef;
//
//    private ImageButton SendMessageButton, SendFilesButton;
//    private EditText MessageInputText;
//
//    private final List<Messages> messagesList = new ArrayList<>();
//    private LinearLayoutManager linearLayoutManager;
//    private MessageAdapter messageAdapter;
//    private RecyclerView userMessagesList;
//
//
//    private String saveCurrentTime, saveCurrentDate;
//    private String checker = "", myUrl ="";
//    private StorageTask uploadTask;
//    private Uri fileUri;
//    private ProgressDialog loadingBar;
//
//
//
//    @RequiresApi(api = Build.VERSION_CODES.N)
//    @Override
//    protected void onCreate(Bundle savedInstanceState)
//    {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_chat);
//
//
//        mAuth = FirebaseAuth.getInstance();
//        messageSenderID = mAuth.getCurrentUser().getUid();
//        RootRef = FirebaseDatabase.getInstance().getReference();
//
//
//        messageReceiverID = getIntent().getExtras().get("visit_user_id").toString();
//        messageReceiverName = getIntent().getExtras().get("visit_user_name").toString();
//        messageReceiverImage = getIntent().getExtras().get("visit_image").toString();
//
//
//        IntializeControllers();
//
//
//        userName.setText(messageReceiverName);
//        Picasso.get().load(messageReceiverImage).placeholder(R.drawable.profile_image).into(userImage);
//
//
//        SendMessageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view)
//            {
//                SendMessage();
//            }
//        });
//
//
//        DisplayLastSeen();
//
//
//        SendFilesButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                CharSequence options[] = new CharSequence[]
//                        {
//                             "Images",
//                             "PDF Fichiers",
//                              "MS Word Fichiers"
//                        };
//                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
//                builder.setTitle("Sélectionner le fichier");
//                builder.setItems(options, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which)
//                    {
//                        if(which==0)
//                        {
//                            checker = "image";
//
//                            Intent intent = new Intent();
//                            intent.setAction(Intent.ACTION_GET_CONTENT);
//                            intent.setType("image/*");
//                            startActivityForResult(intent.createChooser(intent,"Sélectionner une image"), 438);
//                        }
//                        if(which==1)
//                        {
//                            checker = "pdf";
//
//                            Intent intent = new Intent();
//                            intent.setAction(Intent.ACTION_GET_CONTENT);
//                            intent.setType("application/pdf");
//                            startActivityForResult(intent.createChooser(intent,"Sélectionner PDF fichier"), 438);
//                        }
//                        if(which==2)
//                        {
//                            checker = "docx";
//
//                            Intent intent = new Intent();
//                            intent.setAction(Intent.ACTION_GET_CONTENT);
//                            intent.setType("application/msword");
//                            startActivityForResult(intent.createChooser(intent,"Sélectionner MS Word fichier"), 438);
//                        }
//                    }
//                });
//                builder.show();
//            }
//        });
//    }
//
//
//
//
//    @RequiresApi(api = Build.VERSION_CODES.N)
//    private void IntializeControllers()
//    {
//        ChatToolBar = (Toolbar) findViewById(R.id.chat_toolbar);
//        setSupportActionBar(ChatToolBar);
//
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setDisplayShowCustomEnabled(true);
//
//        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View actionBarView = layoutInflater.inflate(R.layout.custom_chat_bar, null);
//        actionBar.setCustomView(actionBarView);
//
//        userName = (TextView) findViewById(R.id.custom_profile_name);
//        userLastSeen = (TextView) findViewById(R.id.custom_user_last_seen);
//        userImage = (CircleImageView) findViewById(R.id.custom_profile_image);
//
//        SendMessageButton = (ImageButton) findViewById(R.id.send_message_btn);
//        SendFilesButton = (ImageButton) findViewById(R.id.send_files_btn);
//        MessageInputText = (EditText) findViewById(R.id.input_message);
//
//        messageAdapter = new MessageAdapter(messagesList);
//        userMessagesList = (RecyclerView) findViewById(R.id.private_messages_list_of_users);
//        linearLayoutManager = new LinearLayoutManager(this);
//        userMessagesList.setLayoutManager(linearLayoutManager);
//        userMessagesList.setAdapter(messageAdapter);
//
//
//        loadingBar = new ProgressDialog(this);
//
//        Calendar calendar = Calendar.getInstance();
//
//        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
//        saveCurrentDate = currentDate.format(calendar.getTime());
//
//        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
//        saveCurrentTime = currentTime.format(calendar.getTime());
//    }
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(requestCode == 438 && resultCode==RESULT_OK && data!=null && data.getData()!=null)
//        {
//            loadingBar.setTitle("Envoi du fichier");
//            loadingBar.setMessage("Veuillez patienter, nous envoyons ce fichier...");
//            loadingBar.setCanceledOnTouchOutside(false);
//            loadingBar.show();
//
//            fileUri = data.getData();
//            if(!checker.equals("image"))
//            {
//                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Document Files");
//
//                final String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
//                final String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID;
//
//                DatabaseReference userMessageKeyRef = RootRef.child("Messages")
//                        .child(messageSenderID).child(messageReceiverID).push();
//
//                final String messagePushID = userMessageKeyRef.getKey();
//
//                final StorageReference filePath = storageReference.child(messagePushID + "." + checker);
//
//                filePath.putFile(fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
//                    {
//                        if (task.isSuccessful())
//                        {
//                            Map messageTextBody = new HashMap();
//                            messageTextBody.put("message", task.getResult().getStorage().getDownloadUrl().toString());   ///////////////////hta hada nl9a m3ah mochkil
//                            messageTextBody.put("name", fileUri.getLastPathSegment());
//                            messageTextBody.put("type", checker);
//                            messageTextBody.put("from", messageSenderID);
//                            messageTextBody.put("to", messageReceiverID);
//                            messageTextBody.put("messageID", messagePushID);
//                            messageTextBody.put("time", saveCurrentTime);
//                            messageTextBody.put("date", saveCurrentDate);
//
//                            Map messageBodyDetails = new HashMap();
//                            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
//                            messageBodyDetails.put( messageReceiverRef + "/" + messagePushID, messageTextBody);
//
//                            RootRef.updateChildren(messageBodyDetails);
//                            loadingBar.dismiss();
//                        }
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e)
//                    {
//                        loadingBar.dismiss();
//                        Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot)
//                    {
//                        double p = (100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
//                        loadingBar.setMessage((int) p + "%Uploading....");
//                    }
//                });
//            }
//            else if(checker.equals("image"))
//            {
//                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");
//
//                final String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
//                final String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID;
//
//                DatabaseReference userMessageKeyRef = RootRef.child("Messages")
//                        .child(messageSenderID).child(messageReceiverID).push();
//
//               final String messagePushID = userMessageKeyRef.getKey();
//
//                final StorageReference filePath = storageReference.child(messagePushID + "." + "jpg");
//
//                uploadTask = filePath.putFile(fileUri);
//
//                uploadTask.continueWithTask(new Continuation() {
//                    @Override
//                    public Object then(@NonNull Task task) throws Exception {
//                        if(!task.isSuccessful())
//                        {
//                            throw task.getException();
//                        }
//
//                        return filePath.getDownloadUrl();
//                    }
//                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Uri> task)
//                    {
//                        if(task.isSuccessful())
//                        {
//                            Uri downloadUrl = task.getResult();
//                            myUrl = downloadUrl.toString();
//
//                            Map messageTextBody = new HashMap();
//                            messageTextBody.put("message", myUrl);
//                            messageTextBody.put("name", fileUri.getLastPathSegment());
//                            messageTextBody.put("type", checker);
//                            messageTextBody.put("from", messageSenderID);
//                            messageTextBody.put("to", messageReceiverID);
//                            messageTextBody.put("messageID", messagePushID);
//                            messageTextBody.put("time", saveCurrentTime);
//                            messageTextBody.put("date", saveCurrentDate);
//
//                            Map messageBodyDetails = new HashMap();
//                            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
//                            messageBodyDetails.put( messageReceiverRef + "/" + messagePushID, messageTextBody);
//
//                            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
//                                @Override
//                                public void onComplete(@NonNull Task task)
//                                {
//                                    if (task.isSuccessful())
//                                    {
//                                        loadingBar.dismiss();
//                                        Toast.makeText(ChatActivity.this, "Message envoyé avec succès...", Toast.LENGTH_SHORT).show();
//                                    }
//                                    else
//                                    {
//                                        loadingBar.dismiss();
//                                        Toast.makeText(ChatActivity.this, "Erreur", Toast.LENGTH_SHORT).show();
//                                    }
//                                    MessageInputText.setText("");
//                                }
//                            });
//                        }
//                    }
//                });
//            }
//            else
//            {
//                loadingBar.dismiss();
//                Toast.makeText(this, "Rien de sélectionné, erreur", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    private void DisplayLastSeen()
//    {
//        RootRef.child("Users").child(messageReceiverID)
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot)
//                    {
//                        if (dataSnapshot.child("userState").hasChild("state"))
//                        {
//                            String state = dataSnapshot.child("userState").child("state").getValue().toString();
//                            String date = dataSnapshot.child("userState").child("date").getValue().toString();
//                            String time = dataSnapshot.child("userState").child("time").getValue().toString();
//
//                            if (state.equals("online"))
//                            {
//                                userLastSeen.setText("en ligne");
//                            }
//                            else if (state.equals("offline"))
//                            {
//                                userLastSeen.setText("Dernière vue : " + date + " " + time);
//                            }
//                        }
//                        else
//                        {
//                            userLastSeen.setText("hors ligne");
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//    }
//
//
//    @Override
//    protected void onStart()
//    {
//        super.onStart();
//
//        RootRef.child("Messages").child(messageSenderID).child(messageReceiverID)
//                .addChildEventListener(new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(DataSnapshot dataSnapshot, String s)
//                    {
//                        Messages messages = dataSnapshot.getValue(Messages.class);
//
//                        messagesList.add(messages);
//
//                        messageAdapter.notifyDataSetChanged();
//
//                        userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());
//                    }
//
//                    @Override
//                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//                    }
//
//                    @Override
//                    public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//                    }
//
//                    @Override
//                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//    }
//
//
//
//    private void SendMessage()
//    {
//        String messageText = MessageInputText.getText().toString();
//
//        if (TextUtils.isEmpty(messageText))
//        {
//            Toast.makeText(this, "écrivez d'abord votre message...", Toast.LENGTH_SHORT).show();
//        }
//        else
//        {
//            String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
//            String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID;
//
//            DatabaseReference userMessageKeyRef = RootRef.child("Messages")
//                    .child(messageSenderID).child(messageReceiverID).push();
//
//            String messagePushID = userMessageKeyRef.getKey();
//
//            Map messageTextBody = new HashMap();
//            messageTextBody.put("message", messageText);
//            messageTextBody.put("type", "text");
//            messageTextBody.put("from", messageSenderID);
//            messageTextBody.put("to", messageReceiverID);
//            messageTextBody.put("messageID", messagePushID);
//            messageTextBody.put("time", saveCurrentTime);
//            messageTextBody.put("date", saveCurrentDate);
//
//            Map messageBodyDetails = new HashMap();
//            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
//            messageBodyDetails.put( messageReceiverRef + "/" + messagePushID, messageTextBody);
//
//            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
//                @Override
//                public void onComplete(@NonNull Task task)
//                {
//                    if (task.isSuccessful())
//                    {
//                        Toast.makeText(ChatActivity.this, "Message envoyé avec succès...", Toast.LENGTH_SHORT).show();
//                    }
//                    else
//                    {
//                        Toast.makeText(ChatActivity.this, "Erreur", Toast.LENGTH_SHORT).show();
//                    }
//                    MessageInputText.setText("");
//                }
//            });
//        }
//    }
//}


package com.example.fin;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity
{
    private final int REQ_CODE = 100;
    TextView cherch;

    private EditText ed;

    private final List<Messages> arr_sort = new ArrayList<>();
    int textlength=0;

    private Boolean textchanged=false ;

    private String messageReceiverID, messageReceiverName, messageReceiverImage, messageSenderID;

    private TextView userName, userLastSeen;
    private CircleImageView userImage;

    private Toolbar ChatToolBar;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;

    private ImageButton SendMessageButton, SendFilesButton;
    private EditText MessageInputText;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView userMessagesList;

    private String saveCurrentTime, saveCurrentDate;
    private String checker = "", myUrl ="";
    private StorageTask uploadTask;
    private Uri fileUri;
    private ProgressDialog loadingBar;



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        cherch = findViewById(R.id.text);
        ImageView speak = findViewById(R.id.speak);

        ed=(EditText)findViewById(R.id.text);

        ed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int i2) {

                arr_sort.clear();

                textchanged=true ;

                int textlength=ed.getText().length();
                for(int i=0;i<messagesList.size();i++)
                {
                    if(textlength<=messagesList.get(i).toString().length())
                    {
                        if(ed.getText().toString().equalsIgnoreCase((String) messagesList.get(i).getMessage().subSequence(0, textlength)))
                        {
                            arr_sort.add(messagesList.get(i));
                        }
                    }
                }
                userMessagesList.setAdapter(new MessageAdapter(arr_sort));
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        mAuth = FirebaseAuth.getInstance();
        messageSenderID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();


        messageReceiverID = getIntent().getExtras().get("visit_user_id").toString();
        messageReceiverName = getIntent().getExtras().get("visit_user_name").toString();
        messageReceiverImage = getIntent().getExtras().get("visit_image").toString();


        IntializeControllers();

        speak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Besoin de parler");
                try {
                    startActivityForResult(intent, REQ_CODE);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(),
                            "Désolé que votre appareil ne soit pas pris en charge",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        userName.setText(messageReceiverName);
        Picasso.get().load(messageReceiverImage).placeholder(R.drawable.profile_image).into(userImage);


        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SendMessage();
            }
        });

        DisplayLastSeen();

        SendFilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[] = new CharSequence[]
                        {
                                "Images",
                                "PDF Fichiers",
                                "MS Word Fichiers"
                        };
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                builder.setTitle("Sélectionner le fichier");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if(which==0)
                        {
                            checker = "image";

                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent.createChooser(intent,"Sélectionner une image"), 438);
                        }
                        if(which==1)
                        {
                            checker = "pdf";

                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/pdf");
                            startActivityForResult(intent.createChooser(intent,"Sélectionner PDF fichier"), 438);
                        }
                        if(which==2)
                        {
                            checker = "docx";

                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/msword");
                            startActivityForResult(intent.createChooser(intent,"Sélectionner MS Word fichier"), 438);
                        }
                    }
                });
                builder.show();
            }
        });
    }




    @RequiresApi(api = Build.VERSION_CODES.N)
    private void IntializeControllers()
    {
        ChatToolBar = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(ChatToolBar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.custom_chat_bar, null);
        actionBar.setCustomView(actionBarView);

        userName = (TextView) findViewById(R.id.custom_profile_name);
        userLastSeen = (TextView) findViewById(R.id.custom_user_last_seen);
        userImage = (CircleImageView) findViewById(R.id.custom_profile_image);

        SendMessageButton = (ImageButton) findViewById(R.id.send_message_btn);
        SendFilesButton = (ImageButton) findViewById(R.id.send_files_btn);
        MessageInputText = (EditText) findViewById(R.id.input_message);

        messageAdapter = new MessageAdapter(messagesList);
        userMessagesList = (RecyclerView) findViewById(R.id.private_messages_list_of_users);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);


        loadingBar = new ProgressDialog(this);

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    cherch.setText((CharSequence) result.get(0));
                }
                break;
            }
            case 438: {
                if (requestCode == 438 && resultCode == RESULT_OK && data != null && data.getData() != null) {
                    loadingBar.setTitle("Envoi du fichier");
                    loadingBar.setMessage("Veuillez patienter, nous envoyons ce fichier...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    fileUri = data.getData();
                    if (!checker.equals("image")) {
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Document Files");

                        final String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
                        final String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID;

                        DatabaseReference userMessageKeyRef = RootRef.child("Messages")
                                .child(messageSenderID).child(messageReceiverID).push();

                        final String messagePushID = userMessageKeyRef.getKey();

                        final StorageReference filePath = storageReference.child(messagePushID + "." + checker);

                        filePath.putFile(fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    Map messageTextBody = new HashMap();
                                    messageTextBody.put("message", task.getResult().getStorage().getDownloadUrl().toString());   ///////////////////hta hada nl9a m3ah mochkil
                                    messageTextBody.put("name", fileUri.getLastPathSegment());
                                    messageTextBody.put("type", checker);
                                    messageTextBody.put("from", messageSenderID);
                                    messageTextBody.put("to", messageReceiverID);
                                    messageTextBody.put("messageID", messagePushID);
                                    messageTextBody.put("time", saveCurrentTime);
                                    messageTextBody.put("date", saveCurrentDate);

                                    Map messageBodyDetails = new HashMap();
                                    messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
                                    messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageTextBody);

                                    RootRef.updateChildren(messageBodyDetails);
                                    loadingBar.dismiss();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loadingBar.dismiss();
                                Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                                double p = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                loadingBar.setMessage((int) p + "%Uploading....");
                            }
                        });
                    } else if (checker.equals("image")) {
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");

                        final String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
                        final String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID;

                        DatabaseReference userMessageKeyRef = RootRef.child("Messages")
                                .child(messageSenderID).child(messageReceiverID).push();

                        final String messagePushID = userMessageKeyRef.getKey();

                        final StorageReference filePath = storageReference.child(messagePushID + "." + "jpg");

                        uploadTask = filePath.putFile(fileUri);

                        uploadTask.continueWithTask(new Continuation() {
                            @Override
                            public Object then(@NonNull Task task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }

                                return filePath.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUrl = task.getResult();
                                    myUrl = downloadUrl.toString();

                                    Map messageTextBody = new HashMap();
                                    messageTextBody.put("message", myUrl);
                                    messageTextBody.put("name", fileUri.getLastPathSegment());
                                    messageTextBody.put("type", checker);
                                    messageTextBody.put("from", messageSenderID);
                                    messageTextBody.put("to", messageReceiverID);
                                    messageTextBody.put("messageID", messagePushID);
                                    messageTextBody.put("time", saveCurrentTime);
                                    messageTextBody.put("date", saveCurrentDate);

                                    Map messageBodyDetails = new HashMap();
                                    messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
                                    messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageTextBody);

                                    RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            if (task.isSuccessful()) {
                                                loadingBar.dismiss();
                                                Toast.makeText(ChatActivity.this, "Message envoyé avec succès...", Toast.LENGTH_SHORT).show();
                                            } else {
                                                loadingBar.dismiss();
                                                Toast.makeText(ChatActivity.this, "Erreur", Toast.LENGTH_SHORT).show();
                                            }
                                            MessageInputText.setText("");
                                        }
                                    });
                                }
                            }
                        });
                    } else {
                        loadingBar.dismiss();
                        Toast.makeText(this, "Rien de sélectionné, erreur", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private void DisplayLastSeen()
    {
        RootRef.child("Users").child(messageReceiverID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.child("userState").hasChild("state"))
                        {
                            String state = dataSnapshot.child("userState").child("state").getValue().toString();
                            String date = dataSnapshot.child("userState").child("date").getValue().toString();
                            String time = dataSnapshot.child("userState").child("time").getValue().toString();

                            if (state.equals("online"))
                            {
                                userLastSeen.setText("en ligne");
                            }
                            else if (state.equals("offline"))
                            {
                                userLastSeen.setText("Dernière vue : " + date + " " + time);
                            }
                        }
                        else
                        {
                            userLastSeen.setText("hors ligne");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    @Override
    protected void onStart()
    {
        super.onStart();

        RootRef.child("Messages").child(messageSenderID).child(messageReceiverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s)
                    {
                        Messages messages = dataSnapshot.getValue(Messages.class);

                        messagesList.add(messages);

                        messageAdapter.notifyDataSetChanged();

                        userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }



    private void SendMessage()
    {
        String messageText = MessageInputText.getText().toString();

        if (TextUtils.isEmpty(messageText))
        {
            Toast.makeText(this, "écrivez d'abord votre message...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
            String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID;

            DatabaseReference userMessageKeyRef = RootRef.child("Messages")
                    .child(messageSenderID).child(messageReceiverID).push();

            String messagePushID = userMessageKeyRef.getKey();

            Map messageTextBody = new HashMap();
            messageTextBody.put("message", messageText);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", messageSenderID);
            messageTextBody.put("to", messageReceiverID);
            messageTextBody.put("messageID", messagePushID);
            messageTextBody.put("time", saveCurrentTime);
            messageTextBody.put("date", saveCurrentDate);

            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
            messageBodyDetails.put( messageReceiverRef + "/" + messagePushID, messageTextBody);

            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task)
                {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(ChatActivity.this, "Message envoyé avec succès...", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(ChatActivity.this, "Erreur", Toast.LENGTH_SHORT).show();
                    }
                    MessageInputText.setText("");
                }
            });
        }
    }
}
