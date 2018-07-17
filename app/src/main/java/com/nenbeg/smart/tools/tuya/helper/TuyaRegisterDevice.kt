package com.nenbeg.smart.tools.tuya.helper

import android.content.Context
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pManager
import android.os.StrictMode
import android.util.Log
import com.alibaba.wireless.security.open.nocaptcha.INoCaptchaComponent.token
import com.nenbeg.smart.tools.tuya.helper.TuyaRegisterDevice

import com.squareup.okhttp.Response
import com.tuya.smart.android.user.bean.User
import com.tuya.smart.common.e
import com.tuya.smart.common.x
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import nl.komponents.kovenant.Promise
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.*
import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.EmptyCoroutineContext
import kotlin.coroutines.experimental.startCoroutine
import kotlin.experimental.xor

import nl.komponents.kovenant.*
import nl.komponents.kovenant.Kovenant.context
import nl.komponents.kovenant.jvm.asDispatcher
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.atomic.AtomicReference


class TuyaRegisterDevice(var context: Context)  {




        val SOCKET_PORT=63145
        val TAG="TuyaRegDev"
        var socketUDP: DatagramSocket?=null



    /**
     * Thin wrapper for this.sendSmartLinkStart()
     * and this.sendSmartLinkData(). Unless you
     * have a special use case, prefer this method
     * over calling this.sendSmartLinkStart() and
     * this.sendSmartLinkData() directly.
     * @param {Object} options
     * options
     * @param {String} options.region
     * region (see smartLinkEncode() for options)
     * @param {String} options.token
     * generated token to send
     * @param {String} options.secret
     * generated secret to send
     * @param {String} options.ssid
     * SSID to connect to
     * @param {String} options.wifiPassword
     * password of WiFi
     * @example
     * device.registerSmartLink({region: 'AZ',
     *                           token: '00000000',
     *                           secret: '0101',
     *                           ssid: 'Example SSID',
     *                           wifiPassword: 'example-password'}).then(() => {
     *  console.log('Done!');
     * });
     * @returns {Promise<Undefined>} A Promise that resolves when all data has been transmitted
     */
    fun registerSmartLink(context: Context,region:String, token:String, secret:String, ssid:String, wifiPassword:String)= async {
        // Check arguments

        if (!region?.isNullOrBlank() || region!!.length !== 2) throw ExceptionInInitializerError("Invalid region")
        if (!token?.isNullOrBlank()|| token!!.length !== 8) throw ExceptionInInitializerError("Invalid token")
        if (!secret?.isNullOrBlank() || secret!!.length !== 4) throw ExceptionInInitializerError("Invalid secret")
        if (!ssid?.isNullOrBlank() || ssid!!.length > 32) throw ExceptionInInitializerError("Invalid wifiName")
        if (!wifiPassword?.isNullOrBlank()|| wifiPassword!!.length > 64) throw ExceptionInInitializerError("Invalid wifiPassword")

        Log.e(TAG,"Sending SmartLink initialization packets");

        val deferred = deferred<String,Exception>()
        handlePromise(deferred.promise)


        try {
            sendSmartLinkStart(context).await()

            Log.e(TAG,"Sending SmartLink data packets")

            sendSmartLinkData(context,smartLinkEncode(region, token, secret, ssid, wifiPassword)!!).await()
            Log.e(TAG,"Finished sending packets.");


            deferred.resolve("all start data sent")
        } catch (err:Exception) {
            deferred.reject(err)
        }


        /*return  Promise(async (resolve, reject) => {
            try {
                await this.sendSmartLinkStart();
                debug('Sending SmartLink data packets');
                await this.sendSmartLinkData(that.smartLinkEncode(options));
                debug('Finished sending packets.');
                resolve();
            } catch (err) {
                reject(err);
            }
        });*/
    }







    /**
     * Transmits start pattern of packets
     * (1, 3, 6, 10) 144 times with
     * a delay between transmits.
     * @returns {Promise<Undefined>} A Promise that resolves when data has been transmitted
     */
    fun sendSmartLinkStart (context: Context) = async {

        val deferred = deferred<String,Exception>()
        handlePromise(deferred.promise)

        try {
            /* eslint-disable no-await-in-loop */
            for (x in 0..143) {
                _broadcastUDP(context,1).await();
                _broadcastUDP(context,3).await();
                _broadcastUDP(context,6).await();
                _broadcastUDP(context,10).await();
                delay((x % 8) + 33);
            }
            /* eslint-enable no-await-in-loop */

            deferred.resolve("all start data sent")
        } catch (err:Exception) {
            deferred.reject(err)
        }



        /*return  Promise((async (resolve, reject) => {
            try {
                /* eslint-disable no-await-in-loop */
                for (let x = 0; x < 144; x++) {
                    await that._broadcastUDP(1);
                    await that._broadcastUDP(3);
                    await that._broadcastUDP(6);
                    await that._broadcastUDP(10);
                    await delay((x % 8) + 33);
                }
                /* eslint-enable no-await-in-loop */

                deferred.resolve("all data sent")
            } catch (err:Exception) {
                deferred.reject(err)
            }
        }));*/
    }













        /**
         * Transmits provided data
         * as UDP packet lengths 30
         * times with a delay between
         * transmits.
         * @param {Array} data of packet lengths to send
         * @returns {Promise<Undefined>} A Promise that resolves when data has been transmitted
         */

        fun sendSmartLinkData(context: Context,data:IntArray)= async{

            val deferred = deferred<String,Exception>()
            handlePromise(deferred.promise)

            try {
                var delayMs = 0

                /* eslint-disable no-await-in-loop */
                for (x in 0..29) {
                    if (delayMs > 26) {
                        delayMs = 6;
                    }

                    _asyncForEach (data,  { a, b,c -> async {
                        _broadcastUDP (context ,x);
                        delay (delayMs) }
                    } ).await()
                    // 17, 40, 53, 79

                    delay (200);
                    delayMs += 3;
                }
                /* eslint-enable no-await-in-loop */

                deferred.resolve("all data sent")
            } catch (err:Exception) {
                deferred.reject(err)
            }
        }



        /*fun sendSmartLinkDataOld (data:Objects) {ß

            return Promise {
                async(resolve, reject) => {
                try {
                    let delayMs = 0;

                    /* eslint-disable no-await-in-loop */
                    for (let x = 0; x < 30; x++) {
                        if (delayMs > 26) {
                            delayMs = 6;
                        }

                        await that . _asyncForEach (data, async b => {
                        await that . _broadcastUDP (b);
                        await delay (delayMs);
                    }); // 17, 40, 53, 79

                        await delay (200);
                        delayMs += 3;
                    }
                    /* eslint-enable no-await-in-loop */

                    resolve();
                } catch (err) {
                    reject(err);
                }
            }
            }
        }*/





        fun handlePromise(promise: Promise<String, Exception>) {
            promise success {
                msg -> println(msg)
            }
            promise fail {
                e -> println(e)
            }
        }



        //Function as parameter with direct implementation

        private fun sum2(a: Int, b: Int, c: (Int, Int) -> Int): Int {
            return a + b + c(a, b)}

        val sum2 = sum2(6, 3, { a, b -> a / b }) // returns 11


        //callback:(obj: Objects, ind:Int, arr: Array<Objects>) -> Unit


        /**
         * Encodes data as UDP packet
         * lengths.
         * @param {Object} options options
         * @param {String} options.region
         * two-letter region (AZ=Americas, AY=Asia, EU=Europe)
         * @param {String} options.token token
         * @param {String} options.secret secret
         * @param {String} options.ssid SSID
         * @param {String} options.wifiPassword
         * password of WiFi
         * @returns {Array} array of packet lengths
         */
        fun smartLinkEncode(region:String, token:String, secret:String, wifiName:String, wifiPassword:String):IntArray?{


            val charset = Charsets.UTF_8

            val wifiPasswordBytes = wifiPassword.toByteArray(charset)
            val regionToken=region+token+secret
            val regionTokenSecretBytes = regionToken.toByteArray(charset)
            val wifiNameBytes =wifiName.toByteArray(charset)


            var rawByteArray = ByteArray(1 + wifiPasswordBytes.size + 1 + regionTokenSecretBytes.size + wifiNameBytes.size)
            var rawByteArrayIndex = 0

            rawByteArray[rawByteArrayIndex++] = wifiPassword.length as Byte
            //Buffer.BlockCopy(wifiPasswordBytes, 0, rawByteArray, rawByteArrayIndex, wifiPasswordBytes.size)

            rawByteArray= rawByteArray.plus(wifiPasswordBytes)

            rawByteArrayIndex += wifiPasswordBytes.size

            rawByteArray[rawByteArrayIndex++] = regionTokenSecretBytes.size as Byte

            //Buffer.BlockCopy(regionTokenSecretBytes, 0, rawByteArray, rawByteArrayIndex, regionTokenSecretBytes.Length);

            rawByteArray=rawByteArray.plus(regionTokenSecretBytes)

            rawByteArrayIndex += regionTokenSecretBytes.size;

            //Buffer.BlockCopy(wifiNameBytes, 0, rawByteArray, rawByteArrayIndex, wifiNameBytes.Length);

            rawByteArray=rawByteArray.plus(wifiNameBytes)
            rawByteArrayIndex += wifiNameBytes.size;


            if (rawByteArrayIndex != rawByteArray.size) return null


            // Packet broadcast (using length for data)
            //int rawDataLengthRoundedUp = (int)Rounder(rawByteArray.Length, 4);
            val rawDataLengthRoundedUp = _rounder(rawByteArray.size as Double, 4).toInt()

            val finalDataArrayLength = 4 + rawDataLengthRoundedUp + rawDataLengthRoundedUp / 4 * 2

            val encodedData = IntArray(finalDataArrayLength)

            //
            // Fill in first 4 bytes
            // of header here
            //
            val stringLength = (wifiPasswordBytes.size + regionTokenSecretBytes.size + wifiNameBytes.size + 2) % 256
            val stringLengthBuffer = ByteArray(1)
            stringLengthBuffer[0] = stringLength as Byte
            val stringLengthCrc = _tuyaCRC8(stringLengthBuffer)


            // Length encoded into the first two bytes based at 16 and then 32
            encodedData[0] = stringLength / 16 or 16
            encodedData[1] = stringLength % 16 or 32
            // Length CRC encoded into the next two bytes based at 46 and 64
            encodedData[2] = stringLengthCrc / 16 or 48
            encodedData[3] = stringLengthCrc % 16 or 64

            var encodedDataIndex = 4
            var sequenceCounter = 0
            for ( x   in 4..rawDataLengthRoundedUp  step 4)
            {

                val tail=rawByteArray.size
                // Build CRC buffer, pulling in data or 0 values if ran past the end.
                val crcData = ByteArray(5)
                crcData[0] = sequenceCounter++.toByte()
                crcData[1] = if ((x + 0)< tail) rawByteArray[x + 0] else 0.toByte()
                crcData[2] = if ((x + 1) < tail) rawByteArray[x + 1] else 0.toByte()
                crcData[3] = if ((x + 2) < tail) rawByteArray[x + 2] else 0.toByte()
                crcData[4] = if ((x + 3) < tail) rawByteArray[x + 3] else 0.toByte()

                // Calculate the CRC
                val crc = _tuyaCRC8(crcData)

                // Move data into encodedData array...

                // CRC8
                encodedData[encodedDataIndex++] = crc % 128 or 128
                // Sequence number
                encodedData[encodedDataIndex++] = crcData[0] % 128 or 128
                // Data
                encodedData[encodedDataIndex++] = crcData[1] % 256 or 256
                encodedData[encodedDataIndex++] = crcData[2] % 256 or 256
                encodedData[encodedDataIndex++] = crcData[3] % 256 or 256
                encodedData[encodedDataIndex++] = crcData[4] % 256 or 256



            }
            return encodedData
        }




        /**
         * Un-references UDP instance
         * so that a script can cleanly
         * exit.
         */
        fun cleanup () {
            if (socketUDP!=null) {
                socketUDP!!.disconnect()
            }
        };






        /**
         * Returns the length in bytes
         * of a string.
         * @param {String} str input string
         * @returns {Number} length in bytes
         * @private
         */
        fun _getLength (str:String):Int {
            return str.length
            //return Buffer.byteLength(str, 'utf8');
        };




        /**
         * Rounds input `x` to the next
         * highest multiple of `g`.
         * @param {Number} x input number
         * @param {Number} g rounding factor
         * @returns {Number} rounded result
         * @private
         */
        fun _rounder(x:Double, g:Int) : Double {
            return Math.ceil(x / g) * g;
        };


        /**
         * Calculates a modified CRC8
         * of a given arary of data.
         * @param {Array} p input data
         * @returns {Number} CRC result
         * @private
         */
        fun _tuyaCRC8(p:ByteArray) :Byte {
            var crc = 0.toByte()
            var i = 0

            val len = p.size


            while (i < len) {
                crc = calcrc_1byte(crc.xor(p[i]).toInt());
                i++;
            }

            return crc;
        };


        /**
         * Calculates a modified
         * CRC8 of one byte.
         * @param {Number} abyte one byte as an integer
         * @returns {Number} resulting CRC8 byte
         * @private
         */

        private fun calcrc_1byte(abyte: Int): Byte {
            var crc1Byte = 0
            var abyte=abyte
            //crc1Byte[0] = 0;

            for (i in 0..7) {
                if (((crc1Byte.xor(abyte)).and(0x01)) > 0) {

                    crc1Byte = crc1Byte xor 0x18
                    crc1Byte = crc1Byte shr 1
                    crc1Byte = crc1Byte or 0x80


            } else {
                    crc1Byte = crc1Byte shr 1
            }

                abyte = abyte shr 1
            }

            return crc1Byte.toByte()
        };



        /**
         * Broadcasts input number as the
         * length of a UDP packet.
         * @param {Number} len length of packet to broadcast
         * @returns {Promise<Undefined>}
         * A Promise that resolves when input has been broadcasted
         * @private
         */
        fun _broadcastUDP(context: Context,len:Int) = async {
            // Hack Prevent crash (sending should be done using an async task)
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            // Create and bind UDP socket


            try {
            if(socketUDP==null) {


                socketUDP = DatagramSocket()
                socketUDP!!.setBroadcast(true)

            }




                // 0-filled buffer
                val sendData = ByteArray(1,{ len -> len.toByte()})
                val sendPacket = DatagramPacket(sendData, sendData.size, getBroadcastAddress(context), SOCKET_PORT)

                sendPacketAsync(socketUDP!!,sendPacket).await()

                //socket.send(sendPacket)
                println(javaClass.name + "Broadcast packet sent to: " + getBroadcastAddress(context).getHostAddress())
            } catch (e: IOException) {
                Log.e(TAG, "IOException: " + e.message)
            }


        }








        /**
         * Broadcasts input number as the
         * length of a UDP packet.
         * @param {Number} len length of packet to broadcast
         * @returns {Promise<Undefined>}
         * A Promise that resolves when input has been broadcasted
         * @private
         */
        fun sendBroadcast(context: Context,messageStr: String)= async {
            // Hack Prevent crash (sending should be done using an async task)
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)

            try {
                //Open a random port to send the package
                val socket = DatagramSocket()
                socket.setBroadcast(true)


                val sendData = messageStr.toByteArray()
                val sendPacket = DatagramPacket(sendData, sendData.size, getBroadcastAddress(context), SOCKET_PORT)

                sendPacketAsync(socket,sendPacket).await()

                //socket.send(sendPacket)
                println(javaClass.name + "Broadcast packet sent to: " + getBroadcastAddress(context).getHostAddress())
            } catch (e: IOException) {
                Log.e(TAG, "IOException: " + e.message)
            }

        }

        @Throws(IOException::class)
        fun getBroadcastAddress(context: Context): InetAddress {
            val wifi = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val dhcp = wifi.dhcpInfo
            // handle null somehow

            val broadcast = dhcp.ipAddress and dhcp.netmask or dhcp.netmask.inv()
            val quads = ByteArray(4)
            for (k in 0..3)
                quads[k] = (broadcast shr k * 8 and 0xFF).toByte()
            return InetAddress.getByAddress(quads)
        }










        /**
         * A helper that provides an easy
         * way to iterate over an array with
         * an asynchronous function.
         * @param {Array} array input array to iterate over
         * @param {function(item, index, array)} callback
         * function to call for iterations
         * @private
         */

        fun _asyncForEach(array: IntArray, callback: (Int, Int, IntArray)  -> Unit) = async   {

            val tail=array.size
            for ( index in 0..tail-1) {
                // eslint-disable-next-line no-await-in-loop
                callback(array[index], index, array)
            }
        }










        fun sendPacketAsync(socket: DatagramSocket, packet: DatagramPacket) = async {

            socket.send(packet)
        }
















}