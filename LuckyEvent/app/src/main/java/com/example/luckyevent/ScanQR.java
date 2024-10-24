package com.example.luckyevent;

import android.app.Activity;
import android.widget.Toast;

import com.google.android.gms.common.moduleinstall.ModuleInstall;
import com.google.android.gms.common.moduleinstall.ModuleInstallClient;
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

public class ScanQR {
    private static GmsBarcodeScanner scanner;
    private static boolean isScannerInstalled = false;

    public ScanQR(Activity activity) {
        GmsBarcodeScannerOptions options = new GmsBarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .enableAutoZoom()
                .build();
        scanner = GmsBarcodeScanning.getClient(activity, options);
    }

    public static void installScanner(Activity activity, InstallScannerCallback callback) {
        ModuleInstallClient moduleInstall = ModuleInstall.getClient(activity);
        ModuleInstallRequest moduleInstallRequest = new ModuleInstallRequest.Builder()
                .addApi(GmsBarcodeScanning.getClient(activity))
                .build();

        moduleInstall.installModules(moduleInstallRequest)
                .addOnSuccessListener(unused -> {
                    isScannerInstalled = true;
                    callback.onInstallSuccess();
                })
                .addOnFailureListener(e -> {
                    isScannerInstalled = false;
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    callback.onInstallFailure();
                });
    }

    public static void startScan(Activity activity, ScanCallback callback) {
        if (!isScannerInstalled) {
            Toast.makeText(activity, "Scanner not installed", Toast.LENGTH_SHORT).show();
            return;
        }

        scanner.startScan()
                .addOnSuccessListener(barcode -> {
                    String result = barcode.getRawValue();
                    if (result != null) {
                        callback.onScanSuccess(result);
                    }
                })
                .addOnCanceledListener(() -> {
                    Toast.makeText(activity, "Cancelled", Toast.LENGTH_SHORT).show();
                    callback.onScanCancelled();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    callback.onScanFailure(e);
                });
    }

    public interface InstallScannerCallback {
        void onInstallSuccess();

        void onInstallFailure();
    }

    public interface ScanCallback {
        void onScanSuccess(String result);

        void onScanCancelled();

        void onScanFailure(Exception e);
    }
}