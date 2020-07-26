package com.mexs.scanner;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.ResultReceiver;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;

public class PrintLabelZebra {

    // Built for Zebra ZQ520 label printer
    // Note: max char printable for name is 30

    private Context mContext;
    private UserSettings mSettings;
    private Record mRecord;

    public PrintLabelZebra(Context mContext, UserSettings mSettings, Record mRecord) {
        this.mContext = mContext;
        this.mSettings = mSettings;
        this.mRecord = mRecord;
    }

    public void print(){
        // Construct print parameters
        if (!mSettings.isPrintLabels())
            return;

        String templateData = "";
        HashMap<String, String> variableData = new HashMap<>();

        switch (mSettings.getPrintStyle()){
            case 1:
                templateData = "^\u0010CT~~CD,~CC^~CT~\n" +
                        "^XA~TA000~JSN^LT0^MNW^MTD^POI^PMN^LH0,0^JMA^PR5,5~SD10^JUS^LRN^CI0^XZ\n" +
                        "^XA\n" +
                        "^MMT\n" +
                        "^PW424\n" +
                        "^LL0296\n" +
                        "^LS0\n" +
                        "^FT65,90^BCN,50,N,N^FD%BARCODE%^FS\n" +
                        "^FT30,120^A0N,21,21^FD%LINE1%^FS\n" +
                        "^FT30,145^A0N,21,21^FD%LINE2%^FS\n" +
                        "^FT30,170^A0N,21,21^FD%LINE3%^FS\n" +
                        "^PQ%QTY%,0,0,Y^XZ\n";

                variableData.put("%BARCODE%", mRecord.getId());
                variableData.put("%LINE1%", String.format("%s     %s     %s", mRecord.getId(), mRecord.getDob(), mRecord.getGender()));
                variableData.put("%LINE2%", mRecord.printName());
                variableData.put("%LINE3%", String.format("%s     %s", mRecord.getSwabDateTime(), mRecord.getNationality()));
                variableData.put("%QTY%", String.format(Locale.US, "%d", mSettings.getPrintQty()));

                break;
            case 2:
                templateData = "^\u0010CT~~CD,~CC^~CT~\n" +
                        "^XA~TA000~JSN^LT0^MNW^MTD^POI^PMN^LH0,0^JMA^PR5,5~SD10^JUS^LRN^CI0^XZ\n" +
                        "^XA\n" +
                        "^MMT\n" +
                        "^PW424\n" +
                        "^LL0296\n" +
                        "^LS0\n" +
                        "^FT150,30^A0N,21,21^FD%SPECIMENID%^FS\n" +
                        "^FT65,90^BCN,50,N,N^FD%BARCODE%^FS\n" +
                        "^FT30,120^A0N,21,21^FD%LINE1%^FS\n" +
                        "^FT30,145^A0N,21,21^FD%LINE2%^FS\n" +
                        "^FT30,170^A0N,21,21^FD%LINE3%^FS\n" +
                        "^PQ%QTY%,0,0,Y^XZ\n";

                variableData.put("%SPECIMENID%", mRecord.getSpecimenId());
                variableData.put("%BARCODE%", mRecord.getId());
                variableData.put("%LINE1%", String.format("%s     %s     %s", mRecord.getId(), mRecord.getDob(), mRecord.getGender()));
                variableData.put("%LINE2%", mRecord.printName());
                variableData.put("%LINE3%", String.format("%s     %s", mRecord.getSwabDateTime(), mRecord.getNationality()));
                variableData.put("%QTY%", String.format("%d", mSettings.getPrintQty()));

                break;
            case 3:
                templateData = "^\u0010CT~~CD,~CC^~CT~\n" +
                        "^XA~TA000~JSN^LT0^MNW^MTD^POI^PMN^LH0,0^JMA^PR5,5~SD10^JUS^LRN^CI0^XZ\n" +
                        "^XA\n" +
                        "^MMT\n" +
                        "^PW424\n" +
                        "^LL0296\n" +
                        "^LS0\n" +
                        "^FT150,30^A0N,21,21^FD%BARCODE%^FS\n" +
                        "^FT65,90^BCN,50,N,N^FD%BARCODE%^FS\n" +
                        "^FT30,120^A0N,21,21^FD%LINE1%^FS\n" +
                        "^FT30,145^A0N,21,21^FD%LINE2%^FS\n" +
                        "^FT30,170^A0N,21,21^FD%LINE3%^FS\n" +
                        "^PQ%QTY%,0,0,Y^XZ\n";

                variableData.put("%BARCODE%", mRecord.getSpecimenId());
                variableData.put("%LINE1%", String.format("%s     %s     %s", mRecord.getId(), mRecord.getDob(), mRecord.getGender()));
                variableData.put("%LINE2%", mRecord.printName());
                variableData.put("%LINE3%", String.format("%s     %s", mRecord.getSwabDateTime(), mRecord.getNationality()));
                variableData.put("%QTY%", String.format(Locale.US, "%d", mSettings.getPrintQty()));

                break;
            case 4:
                templateData = "^\u0010CT~~CD,~CC^~CT~\n" +
                        "^XA~TA000~JSN^LT0^MNW^MTD^POI^PMN^LH0,0^JMA^PR5,5~SD10^JUS^LRN^CI0^XZ\n" +
                        "^XA\n" +
                        "^MMT\n" +
                        "^PW424\n" +
                        "^LL0296\n" +
                        "^LS0\n" +
                        "^FT65,90^BCN,50,N,N^FD%BARCODE%^FS\n" +
                        "^FT150,30^A0N,21,21^FD%BARCODE%^FS\n" +
                        "^FT30,120^A0N,21,21^FD%LINE1%^FS\n" +
                        "^PQ%QTY%,0,0,Y^XZ\n";

                variableData.put("%BARCODE%", mRecord.getSpecimenId());
                variableData.put("%LINE1%", String.format("%s", mRecord.getSwabDateTime()));
                variableData.put("%QTY%", String.format(Locale.US, "%d", mSettings.getPrintQty()));

                break;
        }

        // Send intent to Zebra PrintConnect (install from Google Play Store)
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.zebra.printconnect", "com.zebra.printconnect.print.TemplatePrintWithContentService"));
        intent.putExtra("com.zebra.printconnect.PrintService.TEMPLATE_DATA", templateData.getBytes(StandardCharsets.UTF_8));
        intent.putExtra("com.zebra.printconnect.PrintService.VARIABLE_DATA", variableData);
        intent.putExtra("com.zebra.printconnect.PrintService.RESULT_RECEIVER", buildIPCSafeReceiver(new ResultReceiver(null){
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultCode == 0) {
                    // Successful print
                } else {
                    // Failed to print
                }
            }
        }));

        mContext.startService(intent);
    }

    private ResultReceiver buildIPCSafeReceiver(ResultReceiver resultReceiver){
        Parcel parcel = Parcel.obtain();
        resultReceiver.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        ResultReceiver receiverForSending = ResultReceiver.CREATOR.createFromParcel(parcel);
        parcel.recycle();
        return receiverForSending;
    }

}
