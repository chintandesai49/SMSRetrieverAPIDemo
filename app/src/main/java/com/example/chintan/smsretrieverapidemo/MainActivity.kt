package com.example.chintan.smsretrieverapidemo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient

class MainActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, MySMSBroadcastReceiver.OTPReceiveListener {


    var mCredentialsApiClient: GoogleApiClient? = null
    private val KEY_IS_RESOLVING = "is_resolving"
    private val RC_HINT = 2
    private var otpReceiver: MySMSBroadcastReceiver.OTPReceiveListener = this

    val smsBroadcast = MySMSBroadcastReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mCredentialsApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .addApi(Auth.CREDENTIALS_API)
                .build()

        startSMSListener()

        smsBroadcast.initOTPListener(this)
        val intentFilter = IntentFilter()
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)


        applicationContext.registerReceiver(smsBroadcast, intentFilter)
//        requestHint()


    }

    private fun startSMSListener() {

        val client = SmsRetriever.getClient(this /* context */)
        val task = client.startSmsRetriever()
        task.addOnSuccessListener {
            // Successfully started retriever, expect broadcast intent
            // ...
            Toast.makeText(this, "SMS Retriever starts", Toast.LENGTH_LONG).show()
        }

        task.addOnFailureListener {
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("LongLogTag")
    private fun requestHint() {
        val hintRequest = HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build()

        val intent = Auth.CredentialsApi.getHintPickerIntent(
                mCredentialsApiClient, hintRequest)

        try {
            startIntentSenderForResult(intent.intentSender,
                    RC_HINT, null, 0, 0, 0)
        } catch (e: Exception) {
            Log.e("Error In getting Message", e.message)
        }

    }


    override fun onConnected(p0: Bundle?) {

    }

    override fun onConnectionSuspended(p0: Int) {
    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    override fun onOTPReceived(otp: String?) {
        if (smsBroadcast != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(smsBroadcast)

        }
        Toast.makeText(this, otp, Toast.LENGTH_SHORT).show()
        Log.e("OTP Received", otp)
    }

    override fun onOTPTimeOut() {
        Toast.makeText(this, " SMS retriever API Timeout", Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_HINT && resultCode == Activity.RESULT_OK) {

            /*You will receive user selected phone number here if selected and send it to the server for request the otp*/
            var credential: Credential = data!!.getParcelableExtra(Credential.EXTRA_KEY)

        }
    }
}