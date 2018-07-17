package com.nenbeg.smart.tools.tuya.helper

import com.alibaba.fastjson.JSON
import com.google.common.hash.Hashing.md5
import com.nenbeg.smart.tools.tuya.helper.CommonPromiseExample.Companion.handlePromise
import com.tuya.smart.common.i
import com.tuya.smart.common.s
import com.tuya.smart.common.v
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import nl.komponents.kovenant.deferred
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class TuyaRegisterCloud(optionsParam:HashMap<String,String>) {


    lateinit var key:String
    lateinit var secret:String
    lateinit var region:String
    lateinit var endpoint:String
    lateinit var deviceID:String
    lateinit var sid:String
    /*lateinit var key:String
    lateinit var key:String
    lateinit var key:String*/

    /**
     * A TuyaCloud object
     * @class
     * @param {Object} options construction options
     * @param {String} options.key API key
     * @param {String} options.secret API secret
     * @param {String} [options.region='AZ'] region (AZ=Americas, AY=Asia, EU=Europe)
     * @param {String} [options.deviceID] ID of device calling API (defaults to a random value)
     * @example
     * const api = new Cloud({key: 'your-api-key', secret: 'your-api-secret'})
     */
    init {
        // Set to empty object if undefined
        val options = if(optionsParam.isEmpty()) HashMap<String,String>() else optionsParam;

        // Key and secret
        if (options.get("key")!!.isNullOrBlank() || options.get("secret")!!.isNullOrBlank() ||
                options.get("key")!!.length !== 20 || options.get("secret")!!.length !== 32) {

            throw  Exception("Invalid format for key or secret.")
        } else {
            this.key = options.get("key")!!
            this.secret = options.get("secret")!!
        }

        // Device ID
        if (options.get("key")!!.isNullOrBlank()) {
          //TODO : UNCOMMENTE THIS AFTER TEST  this.deviceID = randomize('a0', 44, options);
        } else {
            this.deviceID = options.get("deviceID")!!
        }

        // Region
        if (options.get("region")!!.isNullOrBlank()  || options.get("region")!!.equals("AZ") ) {
            this.region = "AZ"
            this.endpoint ="https://a1.tuyaus.com/api.jso"
        } else if (options.get("region")!!.equals("AY") ) {
            this.region = "Y";
            this.endpoint = "https://a1.tuyacn.com/api.json"
        } else if (options.get("region")!!.equals("EU") ) {
            this.region = "EU"
            this.endpoint = "https://a1.tuyaeu.com/api.json"
        }
    }






    /**
     * Sends an API request
     * @param {Object} options
     * request options
     * @param {String} options.action
     * API action to invoke (for example, 'tuya.cloud.device.token.create')
     * @param {Object} [options.data={}]
     * data to send in the request body
     * @param {Boolean} [options.requiresSID=true]
     * set to false if the request doesn't require a session ID
     * @example
     * // generate a new token
     * api.request({action: 'tuya.m.device.token.create',
     *              data: {'timeZone': '-05:00'}}).then(token => console.log(token))
     * @returns {Promise<Object>} A Promise that contains the response body parsed as JSON
     */
    fun request(optionsParam:HashMap<String,String>,dataParam:HashMap<String,String>) = async   {

        val deferred = deferred<String,Exception>()
        handlePromise(deferred.promise)


        // Set to empty object if undefined
        val options = if(optionsParam.isEmpty()) HashMap<String,String>() else optionsParam;

        // Check arguments
        if (options.get("requiresSID")!!.isNullOrEmpty()) {
            options.put("requiesSID","1")
        }

        if (options.get("action")!!.isNullOrEmpty()) {
            throw  Exception("Must specify an action to call.")
        }

        val data = if(dataParam.isEmpty()) HashMap<String,String>() else dataParam;

        // Must have SID if we need it later
        if (options.get("sid")!!.isNotEmpty() && options.get("requiresSID").equals("1")) {
            throw Exception("Must call login() first.")
        }

        val d =  Date();
        val pairs = HashMap<String,String>()
        pairs.put("a",options.get("action")!!)
        pairs.put("deviceId",options.get("deviceId")!!)
        pairs.put("os",options.get("Linux")!!)
        pairs.put("v",options.get("1.0")!!)
        //TODO : UNCOMMENTE THIS AFTER TESTpairs.put("clientId",this.key) //todo: do not put key in the param but init with the class
        pairs.put("lang",options.get("en")!!)
        pairs.put("time",Math.round(1.00*d.time/1000).toString())
        //TODO : UNCOMMENTE THIS AFTER TESTpairs.put("postData", JSON.stringify(options.data))
        pairs.put("a",options.get("action")!!)


        if (options.get("requiresSID").equals("1")) {
            pairs.put("sid",options.get("sid")!!)
        }


        // Generate signature for request
        val valuesToSign = arrayListOf("a", "v", "lat", "lon", "lang", "deviceId", "imei",
                "imsi", "appVersion", "ttid", "isH5", "h5Token","os",
                "clientId", "postData", "time", "n4h5", "sid", "sp")

        //TODO : UNCOMMENTE THIS AFTER TEST
        /*val out = Array<String>();
        val sortedPairs = pairs.toSortedMap();
        val tail=sortedPairs!!.size

        var ind=0
        for ( key in sortedPairs.keys) {
            if (!valuesToSign.includes(key) || pairs.get(key)!!.isNullOrEmpty()) {
                continue;
            } else {
                out[ind]=(key + '=' + pairs[key]);
                ind++
            }
        }



        val strToSign = this.secret + out.joinToString("|")

        pairs.put("sign",md5(strToSign))


        try {
            //TODO : UNCOMMENTE THIS AFTER TEST val apiResult = await got(this.endpoint, {query: pairs});
            val apiResult=HashMap<String,String>()
            val data = JSON.parse(apiResult.get("body"));

            if (data.success === false) {
                throw Exception(data.errorCode);
            }

            deferred.resolve(data.result)
        } catch (err:Exception) {
            deferred.reject(err)
        }*/
    }

















    /**
     * Helper to register a new user. If user already exists, it instead attempts to log in.
     * @param {Object} options
     * register options
     * @param {String} options.email
     * email to register
     * @param {String} options.password
     * password for new user
     * @example
     * api.register({email: 'example@example.com',
    password: 'example-password'})
    .then(sid => console.log('Session ID: ', sid))
     * @returns {Promise<String>} A Promise that contains the session ID
     */
    fun register(optionsParam: HashMap<String, String>) = async   {
        val deferred = deferred<String,Exception>()
        handlePromise(deferred.promise)
        try {
            /*val apiResult =request({"action" to "tuya.m.user.email.register", data: {countryCode: this.region,
                    email: options.email,
                    passwd: md5(options.password)},
                requiresSID: false});*/




            // Set to empty object if undefined
            val options = if(optionsParam.isEmpty()) HashMap<String,String>() else optionsParam

            val param =  HashMap<String,String>()

            val apiResult =request(options,param).await()


            //TODO : UNCOMMENTE THIS AFTER TEST   sid = apiResult.sid;

            sid
        } catch (err:Exception) {
            if (err.message === "USER_NAME_IS_EXIST") {

                login(optionsParam)
            }
             err;
        }
    }












    /**
     * Helper to log in a user.
     * @param {Object} options
     * register options
     * @param {String} options.email
     * user's email
     * @param {String} options.password
     * user's password
     * @example
     * api.login({email: 'example@example.com',
    password: 'example-password'}).then(sid => console.log('Session ID: ', sid))
     * @returns {Promise<String>} A Promise that contains the session ID
     */
    fun login(options:HashMap<String,String>) = async   {
        try {
            /*val apiResult = await this.request({action: 'tuya.m.user.email.password.login',
                data: {countryCode: this.region,
                    email: options.email,
                    passwd: md5(options.password)},
                requiresSID: false});*/

            val apiResult = request(options,options).await()

            //TODO : UNCOMMENTE THIS AFTER TEST  sid= apiResult.sid;

             sid;
        } catch (err:Exception) {
             err;
        }
    };

    /**
     * Helper to wait for device(s) to be registered.
     * It's possible to register multiple devices at once,
     * so this returns an array.
     * @param {Object} options
     * options
     * @param {String} options.token
     * token being registered
     * @param {Number} [options.devices=1]
     * number of devices to wait for
     * @example
     * api.waitForToken({token: token.token}).then(result => {
     *   let device = result[0];
     *   console.log('Params:');
     *   console.log(JSON.stringify({id: device['id'], localKey: device['localKey']}));
     * });
     * @returns {Promise<Array>} A Promise that contains an array of registered devices
     */
    fun waitForToken(options:HashMap<String,String>): Deferred<Unit> {

        if (options.get("devices")?.isNullOrBlank() as Boolean) {
            options.put("devices","1")
        }

        val deferred = deferred<String,Exception>()
        handlePromise(deferred.promise)

        return async {

            for (i in 0..199) {
            try {
                /* eslint-disable-next-line no-await-in-loop */
                /*val tokenResult = request({action: 'tuya.m.device.list.token',
                    data: {token: options.token}}).await()*/

                val tokenResult = request(options,options).await()

                if (tokenResult.toString().length >= options.get("devices")!!.toInt()) {
                    deferred.resolve(tokenResult.toString());
                }

                // Wait for 200 ms
                /* eslint-disable-next-line no-await-in-loop */
                 delay(200);
            } catch (err:Exception) {
                deferred.reject(err);
            }
        }
            deferred.reject(Exception("Timed out wating for device(s) to connect to cloud"));
        }
    }

















































































    companion object {



    }
}