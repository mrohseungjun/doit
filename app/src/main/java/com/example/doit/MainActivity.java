package com.example.doit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doit.databinding.ActivityNavigationBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInAccount gsa;


    // ?????????????????? ?????????????????? ??????
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    //DatabaseReference??? ????????????????????? ?????? ????????? ???????????? ????????? ???????????? ??????.
    //?????? ????????? ???????????????????????? ??? ???????????????
    //??????(????????? ?????? ??????)??? ?????? ????????? ??????????????? ?????? ????????????.
    private DatabaseReference databaseReference = database.getReference();
    private FirebaseUser user;

    //??????????????? ?????? ????????????
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityNavigationBinding binding;

    //????????? ????????? ????????? ????????? ??? ?????????
    CircleImageView circleView;
    TextView textView1, textView2;
    private final int gallayImage = 200;

    String date;
    private String uid;
    private List<todoList> listDto = new ArrayList<>();
    private List<String> uidList = new ArrayList<>(); //????????? key
    private String updateKey;

    HashMap<String, Object> listUpdate = new HashMap<String, Object>();

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //=======================??????????????? ?????? ????????????==================
        binding = ActivityNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarNavigation.toolbar);
        //toolbar title ??????
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                //????????? ??????
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_navigation);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        //==========================???????????????===========================

        //============================?????????=============================
        View header = navigationView.getHeaderView(0);
        circleView = (CircleImageView) header.findViewById(R.id.CircleView);
        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);

        //??????????????? ????????? ??????????????? ?????? ??????
        circleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, gallayImage);


            }
        });
//===============================================?????????==========================

        //---------------Google ?????????------------------------
        firebaseAuth = FirebaseAuth.getInstance();
        // Google ???????????? ?????? ??????
        // GoogleSignInOptions ????????? ????????? ??? requestIdToken??? ??????
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        //---------------Google ?????????------------------------//

        //---------------?????? ?????????---------------------------//
        CalendarView calendarView = findViewById(R.id.calendarView);
        Button btn_save = findViewById(R.id.save_Btn);
        Button btn_updte = findViewById(R.id.update_Btn);
        EditText editText = findViewById(R.id.contextEditText);

        // todoList??? ?????? RecyclerView
        RecyclerView recyclerView1 = findViewById(R.id.recyclerView1);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView1.setLayoutManager(linearLayoutManager);
        todoListAdapter adapter = new todoListAdapter(listDto, uidList);

        // friends??? ?????? RecyclerView
        RecyclerView recyclerFriends = findViewById(R.id.recyclerFriends);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerFriends.setLayoutManager(linearLayoutManager1);
        FriendsAdapter fadapter = new FriendsAdapter();
        recyclerFriends.setAdapter(fadapter);

        editText.setVisibility(View.VISIBLE);
        btn_save.setVisibility(View.VISIBLE);

        // ?????? ?????????(????????? ?????? ????????? ??????????????? ?????????)
        Calendar cal = Calendar.getInstance();
        Date nowDate = cal.getTime();
        SimpleDateFormat dataformat = new SimpleDateFormat("yyyy-M-d");
        date = dataformat.format(nowDate);

        //?????? ????????? ????????????
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                date = year + "-" + (month + 1) + "-" + dayOfMonth;

                dateList(recyclerView1, adapter);
            }
        });
        // ?????? ????????? ????????????
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    uid = user.getUid();
                }
                addList(editText.getText().toString(), date, 0, uid);
                editText.setText("");
            }
        });
        adapter.setListener(new todoListListener() {
            @Override
            public void onItemClick(int position, String key) {
                updateKey = key;
                todoList todolist = adapter.getItem(position);
                editText.setText(todolist.getContent());
                btn_save.setVisibility(View.GONE);
                btn_updte.setVisibility(View.VISIBLE);
            }

        });
        btn_updte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listUpdate.put("content", editText.getText().toString());
                listUpdate.put("date", date);

                //HashMap??? ????????? ????????? ?????????.(?????? ????????? DB??? ?????? ?????????)
                database.getReference().child("todoList").child(updateKey).updateChildren(listUpdate);

                editText.setText("");
                btn_save.setVisibility(View.VISIBLE);
                btn_updte.setVisibility(View.GONE);
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.nav_Logout:

                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        signOut(); //????????????
                        break;
                }

                return false;
            }
        });

    }//~~oncreate

    //EditText??? ?????? ?????? ?????????????????? Realtime database??? ????????? ??????
    public void addList(String content, String date, int chkId, String uid) {
        todoList todoList = new todoList(content, date, chkId, uid);
        databaseReference.child("todoList").push().setValue(todoList);
    }

    // ???????????????????????? ????????? ?????????
    //????????? ?????? --> ????????? ????????? ?????????????????? ????????????.
    // ???????????? ?????? ?????? ??????????????? ??????(????????????)
    public void dateList(RecyclerView recyclerView1, todoListAdapter adapter) {
        database.getReference().child("todoList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //????????? ?????? DataSnapshot ?????? ????????????.
                //???????????? ????????? ?????????  clear()
                listDto.clear();
                uidList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    todoList todolistDto = ds.getValue(todoList.class);
                    String uidKey = ds.getKey();

                    if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(todolistDto.getUid()) && date.equals(todolistDto.getDate())) {
                        listDto.add(todolistDto);
                        uidList.add(uidKey);
                    }
                }
                recyclerView1.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //??????(??????,??????) ??????
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId()) {
            case R.id.action_Like:
                Intent likeIntent = new Intent(this, LikeActivity.class);
                startActivity(likeIntent);
                break;

            case R.id.action_Search:
                Intent searchIntent = new Intent(this, FindFriendActivity.class);
                startActivity(searchIntent);
                break;


        }

        return super.onOptionsItemSelected(item);

    }


    //???????????????  ????????????
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    //???????????????  ????????????
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_navigation);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();

    }

    //?????????
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == gallayImage && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            circleView.setImageURI(selectedImageUri);
        }

    }

    /* ???????????? */
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, task -> {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    Log.d("usertest :", user + "");
                    updateUI(user);

                    firebaseAuth.signOut();
                    Toast.makeText(MainActivity.this, R.string.success_logout, Toast.LENGTH_SHORT).show();
                    // ...
                });
        gsa = null;
    }

    //    ???????????? ?????? ??????
    private void updateUI(FirebaseUser user) { //update ui code here
        if (user != null) {
            Intent intent = new Intent(this, LogInActivity.class);
            startActivity(intent);
            finish();
        }
    }
}