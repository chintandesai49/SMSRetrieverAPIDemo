package com.example.chintan.smsretrieverapidemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

/**
 * Created by Chintan on 08-11-2017.
 */

public class MySMSBroadcastReceiver extends BroadcastReceiver {

  private OTPReceiveListener otpReceiver;

  public void initOTPListener(OTPReceiveListener receiver) {
    this.otpReceiver = receiver;
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
      Bundle extras = intent.getExtras();
      Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

      switch (status.getStatusCode()) {
        case CommonStatusCodes.SUCCESS:
          // Get SMS message contents
          String otp = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);

          // Extract one-time code from the message and complete verification
          // by sending the code back to your server for SMS authenticity.
          // But here we are just passing it to MainActivity
          if (otpReceiver != null) {
            otp = otp.replace("<#> Your ExampleApp code is: ", "").split("\n")[0];
            otpReceiver.onOTPReceived(otp);
          }
          break;

        case CommonStatusCodes.TIMEOUT:
          // Waiting for SMS timed out (5 minutes)
          // Handle the error ...
          otpReceiver.onOTPTimeOut();

          break;
      }
    }
  }

  public interface OTPReceiveListener {

    void onOTPReceived(String otp);

    void onOTPTimeOut();
  }
}
