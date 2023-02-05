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
    private Button btnLogoutGoogle;


    // 파이어베이스 데이터베이스 연동
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    //DatabaseReference는 데이터베이스의 특정 위치로 연결하는 거라고 생각하면 된다.
    //현재 연결은 데이터베이스에만 딱 연결해놓고
    //키값(테이블 또는 속성)의 위치 까지는 들어가지는 않은 모습이다.
    private DatabaseReference databaseReference = database.getReference();
    private FirebaseUser user;

    //네비게이션 메뉴 추가부분
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityNavigationBinding binding;

    //프로필 이미지 텍스트 변경에 쓸 친구들
    CircleImageView circleView;
    TextView textView1,textView2;
    private final int gallayImage =200;

    String date;
    private String uid;
    private List<todoList> listDto = new ArrayList<>();
    private List<String> uidList = new ArrayList<>(); //게시물 key
    private String updateKey;

    HashMap<String, Object> listUpdate = new HashMap<String, Object>();

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //=======================네비게이션 메뉴 추가부분==================
        binding = ActivityNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarNavigation.toolbar);
        //toolbar title 제거
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                //페이지 구성
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_navigation);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        //==========================네비게이션===========================

        //============================프로필=============================
        View header = navigationView.getHeaderView(0);
        circleView = (CircleImageView)header.findViewById(R.id.CircleView);
        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);

        //프로필사진 클릭시 갤러리에서 사진 변경
        circleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                startActivityForResult(intent,gallayImage);


            }
        });
//===============================================프로필==========================

        //---------------Google 로그인------------------------
        firebaseAuth = FirebaseAuth.getInstance();
        // Google 로그인을 앱에 통합
        // GoogleSignInOptions 개체를 구성할 때 requestIdToken을 호출
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        btnLogoutGoogle = findViewById(R.id.btn_logout_google);

        btnLogoutGoogle.setOnClickListener(view -> {
            signOut(); //로그아웃


        });
        //---------------Google 로그인------------------------//

        //---------------메인 캘린더---------------------------//
        CalendarView calendarView = findViewById(R.id.calendarView);
        Button btn_save = findViewById(R.id.save_Btn);
        Button btn_updte = findViewById(R.id.update_Btn);
        EditText editText = findViewById(R.id.contextEditText);


        // todoList에 대한 RecyclerView
        RecyclerView recyclerView1 = findViewById(R.id.recyclerView1);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView1.setLayoutManager(linearLayoutManager);
        todoListAdapter adapter = new todoListAdapter(listDto, uidList);

        // friends에 대한 RecyclerView
        RecyclerView recyclerFriends = findViewById(R.id.recyclerFriends);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerFriends.setLayoutManager(linearLayoutManager1);
        FriendsAdapter fadapter = new FriendsAdapter();
        recyclerFriends.setAdapter(fadapter);

        editText.setVisibility(View.VISIBLE);
        btn_save.setVisibility(View.VISIBLE);

        // 날짜 기본값(클릭을 하지 않으면 오늘날짜가 들어감)
        Calendar cal = Calendar.getInstance();
        Date nowDate = cal.getTime();
        SimpleDateFormat dataformat = new SimpleDateFormat("yyyy-M-d");
        date = dataformat.format(nowDate);

        //다른 날짜를 클릭하면
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                date = year + "-" + (month + 1) + "-" + dayOfMonth;

                dateList(recyclerView1, adapter);
            }
        });
        // 저장 버튼을 클릭하면
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null){
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

                //HashMap에 저장된 값으로 변경됨.(값이 없으면 DB에 값이 추가됨)
                database.getReference().child("todoList").child(updateKey).updateChildren(listUpdate);

                editText.setText("");
                btn_save.setVisibility(View.VISIBLE);
                btn_updte.setVisibility(View.GONE);
            }
        });
    }//~~oncreate

    //EditText에 있는 값을 파이어베이스 Realtime database로 넘기는 함수
    public void addList(String content, String date, int chkId, String uid){
        todoList todoList = new todoList(content, date, chkId, uid);
        databaseReference.child("todoList").push().setValue(todoList);
    }
    // 파이어베이스에서 데이터 가져옴
    //옵저버 패턴 --> 변화가 있으면 클라이언트에 알려준다.
    // 데이터를 달력 밑에 출력해주는 함수(날짜별로)
    public void dateList(RecyclerView recyclerView1, todoListAdapter adapter){
        database.getReference().child("todoList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //변화된 값이 DataSnapshot 으로 넘어온다.
                //데이터가 쌓이기 때문에  clear()
                listDto.clear();
                uidList.clear();

                for(DataSnapshot ds : snapshot.getChildren()) {
                    todoList  todolistDto = ds.getValue(todoList.class);
                    String uidKey = ds.getKey();

                    if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(todolistDto.getUid())&&date.equals(todolistDto.getDate())) {
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
    //메뉴(하트,서치) 추가
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId()) {
            case R.id.action_Like:
                Intent likeIntent = new Intent(this, LikeActivity.class);
                startActivity(likeIntent);
                break;

            case R.id.action_Search:
                Intent searchIntent = new Intent(this, LikeActivity.class);
                startActivity(searchIntent);
                break;

            case R.id.nav_Logout:

                FirebaseUser  user = firebaseAuth.getCurrentUser();
                updateUI(user);
                Log.d("usertest :",user+"usertest");
                signOut(); //로그아웃
                break;
        }

        return super.onOptionsItemSelected(item);

    }


    //네비게이션  추가부분
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }
    //네비게이션  추가부분
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_navigation);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();

    }
    //프로필
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == gallayImage && resultCode == RESULT_OK && data!= null&& data.getData()!=null){
            Uri selectedImageUri = data.getData();
            circleView.setImageURI(selectedImageUri);
        }

    }
    
    /* 로그아웃 */
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, task -> {
                    FirebaseUser  user = firebaseAuth.getCurrentUser();
                    Log.d("usertest :",user+"");
                    updateUI(user);

                    firebaseAuth.signOut();
                    Toast.makeText(MainActivity.this, R.string.success_logout, Toast.LENGTH_SHORT).show();
                    // ...
                });
        gsa = null;
    }
//    로그아웃 화면 전환
    private void updateUI(FirebaseUser user) { //update ui code here
        if (user != null) {
            Intent intent = new Intent(this, LogInActivity.class);
            startActivity(intent);
            finish();
        }
    }
}