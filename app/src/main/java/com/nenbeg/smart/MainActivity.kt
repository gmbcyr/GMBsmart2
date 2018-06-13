package com.nenbeg.smart

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.nenbeg.smart.allstatic.generateUserPwd
import com.nenbeg.smart.dummy.DummyContent
import com.tuya.smart.android.user.api.ILoginCallback
import com.tuya.smart.android.user.api.IRegisterCallback
import com.tuya.smart.android.user.api.IResetPasswordCallback
import com.tuya.smart.android.user.bean.User
import com.tuya.smart.sdk.TuyaUser
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*




class MainActivity : AppCompatActivity(),DeviceFragment.OnListFragmentInteractionListener,AddDeviceFragment.OnFragmentInteractionListener,DeviceDetailFragment.OnFragmentInteractionListener {

    lateinit var navControl: NavController
    private val TAG="NBG_ACCUEIL"

    // Firebase instance variables
    private var mFirebaseAuth: FirebaseAuth? = null
    private var mFirebaseUser: FirebaseUser? = null

    val ANONYMO = "anonymous"
    private var mUsername: String? = null
    private var mPhotoUrl: String? = null

    private var generatedPWD="NenBeg(1234)"

    private val GOOGLE_TOS_URL = "https://www.google.com/policies/terms/"
    private val RC_SIGN_IN = 100



    lateinit var tuyaListenerLogin : ILoginCallback
    lateinit var tuyaListnerReg  : IRegisterCallback
    lateinit var tuyaListnerReset  : IResetPasswordCallback




    override fun onListFragmentInteraction(item: DummyContent.DummyItem?) {

        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onFragmentInteraction(uri: Uri) {

        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }



     private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
         when (item.itemId) {
             R.id.navigation_home -> {
                 //message.setText(R.string.title_home)
                 navControl!!.navigate(R.id.deviceFragment)
                 return@OnNavigationItemSelectedListener true
             }
             R.id.navigation_dashboard -> {
                 //message.setText(R.string.title_dashboard)
                 navControl.navigate(R.id.addDeviceFragment)
                 return@OnNavigationItemSelectedListener true
             }
             R.id.navigation_notifications -> {
                 //message.setText(R.string.title_notifications)
                 navControl.navigate(R.id.deviceDetailFragment)
                 return@OnNavigationItemSelectedListener true
             }

             R.id.nav_add_device -> {
                 //message.setText(R.string.title_dashboard)
                 navControl.navigate(R.id.viewModelFragment)
                 return@OnNavigationItemSelectedListener true
             }
             R.id.nav_device_detail -> {
                 //message.setText(R.string.title_notifications)
                 val directions = DeviceFragmentDirections.nav_to_settings()

                 navControl.navigate(directions)

                 /*arguments?.let {
                     val args = DeviceFragmentArgs.fromBundle(it)
                     args.myarg
                 }*/
                 return@OnNavigationItemSelectedListener true
             }
         }
         false
     }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

         tuyaListenerLogin =object : ILoginCallback {
            override fun onSuccess(user: User) {
                Toast.makeText(applicationContext, "Login successful!!", Toast.LENGTH_SHORT).show()
            }

            override fun onError(code: String, error: String) {
                Toast.makeText(applicationContext, "code: " + code + "error:" + error, Toast.LENGTH_SHORT).show()
            }
        }


         tuyaListnerReg = object : IRegisterCallback {

            override fun  onSuccess(user:com.tuya.smart.android.user.bean.User) {
                Toast.makeText(applicationContext, "Registration successful!!", Toast.LENGTH_SHORT).show();

                loginToTuya("1",mFirebaseUser!!.uid,generatedPWD,tuyaListenerLogin);
            }

            override fun onError(code:String, error:String) {
                Toast.makeText(applicationContext, "code: " + code + "error:" + error, Toast.LENGTH_SHORT).show();
            }
        }




        tuyaListnerReset = object : IResetPasswordCallback {

            override fun  onSuccess() {
                Toast.makeText(applicationContext, "Reset successful!!", Toast.LENGTH_SHORT).show();
            }

            override fun onError(code:String, error:String) {
                Toast.makeText(applicationContext, "code: " + code + "error:" + error, Toast.LENGTH_SHORT).show();
            }
        }







        mUsername = ANONYMO

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance()
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser()
        //Log.e("MainTabActivity","this is FirebaseUser ->"+mFirebaseUser)
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            //startActivity(Intent(this, SignInActivity::class.java))
            showSignInScreen()
            finish()
            return
        } else {

            mUsername = mFirebaseUser!!.getDisplayName()
            //Log.e("MainTabActivity","this is FirebaseUser name->"+mUsername)
            if (mFirebaseUser!!.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser!!.getPhotoUrl().toString()
            }
        }

        accueil_bottom_nav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        navControl= findNavController(this,R.id.nav_host_fragment)

        setupActionBarWithNavController(this,navControl)



        /*findViewById<BottomNavigationView>(R.id.accueil_bottom_nav)?.let { bottomNavView ->
            NavigationUI.setupWithNavController(bottomNavView, navControl)
        }*/
    }


    private fun showSignInScreen() {

        var providers = Arrays.asList(
                AuthUI.IdpConfig.EmailBuilder().build(),

                //AuthUI.IdpConfig.PhoneBuilder().build(),
                //AuthUI.IdpConfig.TwitterBuilder().build(),
                //AuthUI.IdpConfig.FacebookBuilder().build(),

                AuthUI.IdpConfig.GoogleBuilder().build())

        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setTheme(R.style.AlertDialog_AppCompat)
                        .setAvailableProviders(providers )
                        .setTosUrl(GOOGLE_TOS_URL)
                        .setIsSmartLockEnabled(false)
                        .setLogo(R.drawable.abc_seekbar_thumb_material)      // Set logo drawable
                        .setTheme(R.style.AlertDialog_AppCompat)
                        .build(),
                RC_SIGN_IN)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                mFirebaseUser = FirebaseAuth.getInstance().currentUser



                Toast.makeText(this,"Authentification success for ->"+mFirebaseUser!!.displayName,Toast.LENGTH_LONG)


                val countryCode="1"
                val uid=mFirebaseUser!!.uid
                val password= generateUserPwd(mFirebaseUser!!)





                TuyaUser.getUserInstance().


                TuyaUser.getUserInstance().registerAccountWithUid(countryCode,  uid,  password, tuyaListnerReg);
                // ...
            } else {

                Toast.makeText(this,"Authentification failed for ->"+response!!.error!!.localizedMessage,Toast.LENGTH_LONG)
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }


    fun tuyaDeviceFunction(){

        TuyaUser.getDeviceInstance().getDevList();
//云端查询单个设备信息
        TuyaUser.getDeviceInstance().queryGw(String gwId);
//销毁监听事件，清理缓存数据，需要在退出时进行调用
        TuyaUser.getDeviceInstance().onDestroy();
//获取设备所有dp数据
        TuyaUser.getDeviceInstance().getDps(String devId);
//获取设备单个dp点数据
        TuyaUser.getDeviceInstance().getDp(String devId, String dpId);
//从本地缓存中获取所有设备列表
        TuyaUser.getDeviceInstance().getDevList();
//获取设备schema信息
        TuyaUser.getDeviceInstance().getSchema(String devId);
    }


    fun loginToTuya( contryCode:String,uid:String,pwd:String,listener: ILoginCallback){

        TuyaUser.getUserInstance().loginWithUid(contryCode,uid,pwd,listener)
    }

    fun RegisterToTuya( contryCode:String,uid:String,pwd:String,listener: IRegisterCallback){

        TuyaUser.getUserInstance().registerAccountWithUid(contryCode,uid,pwd,listener)
    }



    fun signOut(){

        AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener(object : OnCompleteListener<Void> {
                  override  fun onComplete(task: Task<Void>) {
                        // ...
                    }
                })
    }

    override fun onSupportNavigateUp() = findNavController(this,R.id.nav_host_fragment).navigateUp()
}
