package com.nenbeg.smart

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
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
import com.nenbeg.smart.R.id.accueil_bottom_nav
import com.nenbeg.smart.allstatic.generateUserPwd
import com.nenbeg.smart.app.NenbegApp
import com.nenbeg.smart.dummy.DummyContent
import com.tuya.smart.android.base.TuyaSmartSdk.appkey
import com.tuya.smart.android.user.api.ILoginCallback
import com.tuya.smart.android.user.api.IRegisterCallback
import com.tuya.smart.android.user.api.IResetPasswordCallback
import com.tuya.smart.android.user.bean.User
import com.tuya.smart.sdk.*
import com.tuya.smart.sdk.bean.DeviceBean
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*




class MainActivity : AppCompatActivity(),DeviceFragment.OnListFragmentInteractionListener,AddDeviceFragment.OnFragmentInteractionListener,
        DeviceDetailFragment.OnFragmentInteractionListener,
        DeviceEventFragment.OnListFragmentInteractionListener{
    override fun hideNavigationForHistoFragment(data: String) {

        accueil_bottom_nav.visibility= View.GONE
    }

    override fun onBackToAccueilFragment(data: String) {


        accueil_bottom_nav.menu.clear()


        accueil_bottom_nav.inflateMenu(R.menu.nav_accueil)

        accueil_bottom_nav.visibility= View.VISIBLE
    }


    override fun onListFragmentInteraction(item: DummyContent.DummyItem?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

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




    override fun onListFragmentInteraction(item: DeviceBean?) {

        /*val intent = Intent(this, DeviceDetailFragment::class.java)
        // To pass any data to next activity
        intent.putExtra("id", item!!.devId )
        intent.putExtra("nom", item!!.name )
        // start your next activity
        startActivity(intent)*/


        /*val detailsFragment =
    RageComicDetailsFragment.newInstance(comic)
supportFragmentManager.beginTransaction()
    .replace(R.id.root_layout, detailsFragment, "rageComicDetails")
    .addToBackStack(null)
    .commit()*/

        val args=Bundle()
        args.putString("id",item!!.devId)
        args.putString("nom",item!!.name)

        navControl!!.navigate(R.id.deviceEventFragment,args)
        //return@OnNavigationItemSelectedListener true

        /*accueil_bottom_nav.menu.removeItem(R.id.navigation_home)
        accueil_bottom_nav.menu.removeItem(R.id.navigation_dashboard)
        accueil_bottom_nav.menu.removeItem(R.id.navigation_notifications)
        accueil_bottom_nav.menu.removeItem(R.id.nav_add_device)
        accueil_bottom_nav.menu.removeItem(R.id.nav_device_detail)



        val options = NavOptions.Builder()
    .setEnterAnim(R.anim.slide_in_right)
    .setExitAnim(R.anim.slide_out_left)
    .setPopEnterAnim(R.anim.slide_in_left)
    .setPopExitAnim(R.anim.slide_out_right)
    .build()

view.findViewById<Button>(R.id.navigate_dest_bt)?.setOnClickListener {
    findNavController(it).navigate(R.id.flow_step_one, null, options)
}





Bundle args = new Bundle();
args.putString("myarg", "From Widget");
PendingIntent pendingIntent = new NavDeepLinkBuilder(context)
    .setGraph(R.navigation.mobile_navigation)
    .setDestination(R.id.android)
    .setArguments(args)
    .createPendingIntent();

remoteViews.setOnClickPendingIntent(R.id.deep_link, pendingIntent);

        */



        accueil_bottom_nav.menu.clear()


        accueil_bottom_nav.inflateMenu(R.menu.nav_device_histo_bottom)

        accueil_bottom_nav

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
                 navControl.navigate(R.id.viewModelFragment)


                 return@OnNavigationItemSelectedListener true
             }
             R.id.navigation_notifications -> {
                 //message.setText(R.string.title_notifications)
                 navControl.navigate(R.id.deviceDetailFragment)
                 return@OnNavigationItemSelectedListener true
             }

             R.id.nav_add_device -> {
                 //message.setText(R.string.title_dashboard)
                 navControl.navigate(R.id.addDeviceFragment)

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

                val devList=TuyaUser.getDeviceInstance().getDevList();

                Log.e(TAG,"MainActivity onCreate login success with list->"+devList)
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

                Log.e(TAG,"MainActivity onCreate Rrgistration Error with code: " + code + "error:" + error)

                if(code.equals("IS_EXIST")){

                    loginToTuya("1",mFirebaseUser!!.uid,generatedPWD,tuyaListenerLogin);
                }
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


        if(TuyaUser.getUserInstance().user!=null){

            //Toast.makeText(applicationContext, "User Already registter, no need to register", Toast.LENGTH_SHORT).show();

            Log.e(TAG,"User Already registter, no need to register")



            loginToTuya( "1",mFirebaseUser!!.uid,generatedPWD,tuyaListenerLogin)
        }
        else{

            //Toast.makeText(applicationContext, "User needs to be register", Toast.LENGTH_SHORT).show();

            Log.e(TAG,"User needs to be register")
            RegisterToTuya( "1",mFirebaseUser!!.uid,generatedPWD,tuyaListnerReg)
        }




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






                /*TuyaUser.getUserInstance().


                TuyaUser.getUserInstance().registerAccountWithUid(countryCode,  uid,  password, tuyaListnerReg);*/
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

      /*  TuyaUser.getDeviceInstance().getDevList();
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
        TuyaUser.getDeviceInstance().getSchema(String devId);*/
    }


    fun loginToTuya( contryCode:String,uid:String,pwd:String,listener: ILoginCallback){

        if(TuyaUser.getUserInstance().isLogin){

            //Toast.makeText(applicationContext, "User Already login, no need to Login", Toast.LENGTH_SHORT).show();

            Log.e(TAG,"User Already login, no need to Login")

            val devList=TuyaUser.getDeviceInstance().getDevList();

            Log.e(TAG,"MainActivity onCreate login success with list->"+devList)
            return
        }

        TuyaUser.getUserInstance().loginWithUid(contryCode,uid,pwd,listener)
    }

    fun RegisterToTuya( contryCode:String,uid:String,pwd:String,listener: IRegisterCallback){

        if(TuyaUser.getUserInstance().user!=null){

            Log.e(TAG,"User Already register, no need to register")
            return
        }

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


        try {
            TuyaUser.getDeviceInstance().onDestroy()
        }
        catch (err:Exception){

            err.printStackTrace()
        }
    }


    override fun onStop() {
        super.onStop()

        try {
            TuyaUser.getDeviceInstance().onDestroy()
        }
        catch (err:Exception){

            err.printStackTrace()
        }
    }

    override fun onSupportNavigateUp() = findNavController(this,R.id.nav_host_fragment).navigateUp()
}
