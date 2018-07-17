package com.nenbeg.smart.tools.tuya.helper

import android.util.Log
import com.alibaba.wireless.security.open.nocaptcha.INoCaptchaComponent.token
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import nl.komponents.kovenant.deferred

class TuyaRegisterSimpleWizard {


    lateinit var key:String
    lateinit var secret:String
    lateinit var region:String
    lateinit var endpoint:String
    lateinit var deviceID:String
    lateinit var sid:String
    lateinit var email:String
    lateinit var password:String
    lateinit var timezone:String




    /**
     * A wrapper that combines `@tuyapi/cloud` and
     * `(@tuyapi/link).manual` (included in this package)
     * to make registration Just Work™️. Exported as
     * `(@tuyapi/link).wizard`.
     * @class
     * @param {Object} options construction options
     * @param {String} options.apiKey API key
     * @param {String} options.apiSecret API secret
     * @param {String} options.email user email
     * @param {String} options.password user password
     * @param {String} [options.region='AZ'] region (AZ=Americas, AY=Asia, EU=Europe)
     * @param {String} [options.timezone='-05:00'] timezone of device
     * @example
     * // Note: user account does not need to already exist
     * const register = new TuyaLink.wizard({key: 'your-api-key',
     *                                           secret: 'your-api-secret',
     *                                           email: 'example@example.com',
     *                                           password: 'example-password'});
     */
    fun TuyaLinkWizard(optionsParam:HashMap<String,String>) {

        // Set to empty object if undefined
        val options = if(optionsParam.isEmpty()) HashMap<String,String>() else optionsParam

        // Key and secret
        if (options.get("email")!!.isNullOrBlank() || options.get("password")!!.isNullOrBlank() ) {

            throw  Exception("Both email and password must be provided'")
        }

        email = options.get("email")!!
        password = options.get("password")!!

        // Set defaults
        region=if(options.get("region")!!.isNotBlank()) options.get("region")!! else "AZ"
        timezone=if(options.get("timezone")!!.isNotBlank()) options.get("timezone")!! else "-05:00"


        //TODO : UNCOMMENTE THIS AFTER TEST

        /*
        // Don't need to check key and secret for correct format as
        // tuyapi/cloud already does
        this.api = new Cloud({key: options.apiKey,
            secret: options.apiSecret,
            region: this.region});

        // Construct instance of TuyaLink
        this.device =  TuyaLink();*/
    }

    /**
     * Logins to Tuya cloud using credentials provided to constructor
     * @example
     * register.init()
     * @returns {Promise<String>} A Promise that contains the session ID
     */
     init {
        // Register/login user
        //TODO : UNCOMMENTE THIS AFTER TEST return this.api.register({email: this.email, password: this.password});
    }












    /**
     * Links device to WiFi and cloud
     * @param {Object} options
     * options
     * @param {String} options.ssid
     * the SSID to send to the device
     * @param {String} options.wifiPassword
     * password for the SSID
     * @param {Number} [options.devices=1]
     * if linking more than 1 device at a time,
     * set to number of devices being linked
     * @example
     * register.linkDevice({ssid: 'HOME-C168',
    wifiPassword: '795F48E494285B6A'}).then(device => {
     *   console.log(device);
     * });
     * @returns {Promise<Object>} A Promise that contains data on device(s)
     */
    fun linkDevice(options:HashMap<String,String>) = async{

        // Key and secret
        if (options.get("ssid")!!.isNullOrBlank() || options.get("wifiPassword")!!.isNullOrBlank() ) {

            throw  Exception("Both SSID and WiFI password must be provided'")
        }



        // Default for options.devices
        if (options.get("devices")?.isNullOrBlank() as Boolean) {
            options.put("devices","1")
        }

        val deferred = deferred<String,Exception>()
        CommonPromiseExample.handlePromise(deferred.promise)

        //TODO : UNCOMMENTE THIS AFTER TEST
        /*

        try {
            /*val token = await this.api.request({action: 'tuya.m.device.token.create',
                data: {timeZone: this.timezone}});*/

            val token = TuyaRegisterCloud(options).request(options,options).await()

            Log.e("TAG", token.toString());

            /*await this.device.registerSmartLink({region: this.region,
                token: token.token,
                secret: token.secret,
                ssid: options.ssid,
                wifiPassword: options.wifiPassword});*/

            TuyaRegisterDevice(context = context).registerSmartLink(context,options.get("region")!!,options.get("token")!!,
                    options.get("secret")!!,options.get("ssid")!!,options.get("password")!!).await()

            // While UDP packets are being sent, start polling for device
            Log.e("LinkDevice","Polling cloud for details on token...");

            /*const devices = await this.api.waitForToken({token: token.token,
                                                         devices: options.devices});*/


            val devices = TuyaRegisterCloud(options).waitForToken(options).await()



            Log.e("LinkDevice","Found device(s)!"+devices);

            // Remove binding on socket
            this.device.cleanup();

             devices;
        } catch (err:Exception) {
            this.device.cleanup();
             err;
        }*/
    };
}