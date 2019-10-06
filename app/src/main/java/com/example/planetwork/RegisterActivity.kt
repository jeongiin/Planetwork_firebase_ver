package com.likelion.planetwork

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.planetwork.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.toast


class RegisterActivity : AppCompatActivity() {

    //Google Login Result
    private val RC_SIGN_IN = 9001

    //Firebase Auth
    private var firebaseAuth: FirebaseAuth? = null

    //Google Api 클라이언트
    private var googleSignInClient: GoogleSignInClient? = null

    //구글 로그인 버튼
    private var buttonGoogle : SignInButton? = null

    //firebase 인증 객체 선언
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        buttonGoogle = findViewById(R.id.btn_googleSignIn)

        //구글 로그인을 앱에 통합
        // [START config_signin]
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        // [END config_signin]

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // [START initialize_auth]
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        // [END initialize_auth]


        //버튼을 눌렀을 때 사용자가 구글 사용자인지 물어보는 과정.
        btn_googleSignIn.setOnClickListener {
            val signInIntent = googleSignInClient?.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

    }
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // GoogleSignInApi.getSignInIntent (...)에서 인텐트를 시작한 결과가 반환되었다.
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google 로그인에 성공했습니다. Firebase로 인증하십시오.
                val account = task.getResult(ApiException::class.java)

                firebaseAuthWithGoogle(account!!) //구글 사용자가 맞으면 firebase로 넘긴다.

            } catch (e: ApiException) {
                // Google 로그인 실패, UI 업데이트
                //Log.w(TAG, "Google 로그인 실패", e)
                // ...
            }
        }
    }

    //사용자에 대한 idToken을 받고 Firebase에 넘겨줘서
    // Firebase Console의 Authentication에 사용자가 등록됨.
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->

                //파이어베이스로 값을 정상적으로 넘겼을 때
                if (task.isSuccessful) {
                    toast("Firebase 아이디 등록 완료")

                } else {
                    // 에러 났을 때
                    toast("Firebase 아이디 등록 실패")
                }
            }
    }

}
