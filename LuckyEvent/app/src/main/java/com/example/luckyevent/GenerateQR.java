package com.example.luckyevent;

import android.graphics.Bitmap;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

public class GenerateQR {

    public static Bitmap generateQRCode(String eventDetails) throws WriterException {
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        return barcodeEncoder.encodeBitmap(eventDetails, BarcodeFormat.QR_CODE, 400, 400);
    }
}
