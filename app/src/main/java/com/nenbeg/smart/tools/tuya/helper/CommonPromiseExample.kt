package com.nenbeg.smart.tools.tuya.helper

import android.provider.Contacts
import android.provider.Contacts.Intents.UI
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import nl.komponents.kovenant.Promise
import nl.komponents.kovenant.all
import nl.komponents.kovenant.deferred
import nl.komponents.kovenant.task
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.EmptyCoroutineContext
import kotlin.coroutines.experimental.startCoroutine

class CommonPromiseExample {

    companion object {




        /**************************/


        fun foo() {
            val deferred = deferred<String,Exception>()
            handlePromise(deferred.promise)

            deferred.resolve("Hello World")

            deferred.reject(Exception("Hello exceptional World"))
        }


        fun handlePromise(promise: Promise<String, Exception>) {
            promise success {
                msg -> println(msg)
            }
            promise fail {
                e -> println(e)
            }
        }



        /*******************************/

        fun doIt(co:Int)= task {
            1 + 1
            val co2=45*co
        } success {
            println("1")
        } success {
            println("2")
        } success {
            println("3")
        }



        /***************************/
        val promisez = task {
            //mimicking those long running operations with:
            1 + 1
        }

        val succes1= promisez success {

            val res=4*5

        }

        /*****************************/



        val firstRef = AtomicReference<String>()
        val secondRef = AtomicReference<String>()

        val first = task { "hello" } success {
            firstRef.set(it)
        }
        val second = task { "world" } success {
            secondRef.set(it)
        }

        fun getThemAll()= all (first, second) success {
            println("${firstRef.get()} ${secondRef.get()}")
        }



        /**************TASK WITH THEN****************/


        /*fun getWithThen()=task {
            fib(20)
        } then {
            "fib(20) = $it, and fib(21) = (${fib(21)})"
        } success {
            println(it)
        }*/



        /**********TASK WITH THEN APPLY*************/




        /*fun thenApply()=task {
            fib(20)
        } thenApply {
            "fib(20) = $this, and fib(21) = (${fib(21)})"
        } success {
            println(it)
        }*/




        /**************************/



        /*fun doSearch(): Promise<TuyaRegisterDevice, Throwable> {
            val deferred = deferred<Response, Throwable>()
            esClient.prepareSearch("index")
                    .setQuery(QueryBuilders.matchAllQuery())
                    .execute(object: WifiP2pManager.ActionListener<T> {
                        override fun onResponse(response: T) {
                            deferred.resolve(response)
                        }

                        override fun onFailure(e: Throwable) {
                            deferred.reject(e)
                        })
                        return deferred.promise
                    }


        }




        promise success {
            //called on succesfull completion of the promise
            println("result: $it")
        }

        promise fail {
            //called when an exceptions has occurred
            println("that's weird ${it.message}")
        }

        promise always {
            //no matter what result we get, this is always called once.
        }*/


        fun foo2() = async {
            //val text = await(bar())
            val text2=bar().await()
            println(text2)
        }


        fun bar() = async<String> {

            "Hello world!"
        }


        fun loadDataAsync() = async {
            try {
                //Turn on busy indicator.
                val job = async(CommonPool) {
                    //We're on a background thread here.
                    //Execute blocking calls, such as retrofit call.execute().body() + caching.
                }
                job.await();
                //We're back on the main thread here.
                //Update UI controls such as RecyclerView adapter data.
            }
            catch (e: Exception) {
            }
            finally {
                //Turn off busy indicator.
            }
        }






        val sum: (Int, Int) -> Int = { x, y -> x + y }


        var funOrNull: ((Int, Int) -> Int)? = null




        /*Promise(
    executor: (resolve: (T) -> Unit, reject: (Throwable) -> Unit) -> Unit)*/

        fun launch(block: suspend () -> Unit) {
            block.startCoroutine(object : Continuation<Unit> {
                override val context: CoroutineContext get() = EmptyCoroutineContext
                override fun resume(value: Unit) {}
                override fun resumeWithException(e: Throwable) { println("Coroutine failed: $e") }
            })
        }




        /*fun c(): Promise<Int> {

            val fetchUserRequest = {85*53};
            onCancel {
                // will be called if promise has been canceled
                // it is task responsibility to terminate any internal long running jobs
                // and clean up resources here
                //fetchUserRequest.cancel()
            }


            // do some real job
            //val user = fetchUserRequest.execute();

            // notify about success
            resolve("test")

            //or report error
            //reject(RuntimeException("Failed"))
        }*/






        fun fetchUserString(userId: String) = async {
            // request user from network
            // return user String
            "val retour"
        }

        fun deserializeUser(userString: String) = async {
            // deserialize
            // return User
            "User "
        }

        val starting= kotlinx.coroutines.experimental.launch(UI) {

            //progressBar.visibility = View.VISIBLE
            try {
                val userString = fetchUserString("1").await()
                val user = deserializeUser(userString).await()
                //showUserData(user)
            } catch (ex: Exception) {
                //log(ex)
            } finally {
                // progressBar.visibility = View.GONE
            }
        }

    }
}