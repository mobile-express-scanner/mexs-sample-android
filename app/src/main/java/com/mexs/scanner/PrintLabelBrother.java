package com.mexs.scanner;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.Socket;

public class PrintLabelBrother extends Thread {

    // Built for Brother QL-820NWB label printer
    private String mHost;
    private int mPort;
    private UserSettings mSettings;
    private Record mRecord;

    public PrintLabelBrother(String host, int port, UserSettings settings, Record record){
        this.mHost = host;
        this.mPort = port;
        this.mSettings = settings;
        this.mRecord = record;
    }

    public void run(){
        try {
            if (!mSettings.isPrintLabels())
                return;

            PrinterTcpIp printerControl = new PrinterTcpIp(mHost, mPort);

            byte[] commandMode = new byte[] {0x1B, 0x69, 0x61, 0x00};
            byte[] initialize = new byte[] {0x1B, 0x40};
            byte[] setPortraitMode = new byte[] {0x1B, 0x69, 0x4C, 0x00};
            byte[] setLineFeedHeight = new byte[] {0x1B, 0x41, 0x01};
            byte[] selectFont = new byte[] {0x1B, 0x6B, 0x0B};
            byte[] selectSize = new byte[] {0x1B, 0x58, 0x00, 0x21, 0x00};
            byte[] name = mRecord.getNameBytes();
            byte[] id = mRecord.getIdBytes();
            byte[] dob = mRecord.getDobBytes();
            byte[] gender = mRecord.getGenderBytes();
            byte[] nationality = mRecord.getNationalityBytes();
            byte[] date = mRecord.getSwabDateTimeBytes();
            byte[] specimenId = mRecord.getSpecimenIdBytes();
            byte[] spaceS = new byte[] {0x20, 0x20};
            byte[] spaceL = new byte[] {0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20};

            byte[] lineFeed = new byte[] {0x0A};
            byte[] leftAlign = new byte[] {0x1B, 0x61, 0x00};
            byte[] centerAlign = new byte[] {0x1B, 0x61, 0x01};
            byte[] startPrint = new byte[] {0x0C};

            // Works
            byte[] startBarcode = new byte[] {0x1B, 0x69, 0x74, 0x30, 0x72, 0x30, 0x68, 0x50, 0x00, 0x42};
            byte[] endBarcode = new byte[] {0x5C};

            printerControl.write(commandMode);
            printerControl.write(initialize);
            printerControl.write(setPortraitMode);
            printerControl.write(setLineFeedHeight);
            printerControl.write(selectFont);
            printerControl.write(selectSize);

            for (int i=0; i<mSettings.getPrintQty(); i++){
                switch (mSettings.getPrintStyle()){
                    case 1:
                        printerControl.write(centerAlign);
                        printerControl.write(startBarcode);
                        printerControl.write(id);
                        printerControl.write(endBarcode);
                        printerControl.write(leftAlign);
                        printerControl.write(lineFeed);
                        printerControl.write(spaceS);
                        printerControl.write(id);
                        printerControl.write(spaceL);
                        printerControl.write(dob);
                        printerControl.write(spaceL);
                        printerControl.write(gender);
                        printerControl.write(lineFeed);
                        printerControl.write(spaceS);
                        printerControl.write(name);
                        printerControl.write(lineFeed);
                        printerControl.write(spaceS);
                        printerControl.write(date);
                        printerControl.write(spaceL);
                        printerControl.write(nationality);
                        printerControl.write(startPrint);
                        break;
                    case 2:
                        printerControl.write(centerAlign);
                        printerControl.write(specimenId);
                        printerControl.write(startBarcode);
                        printerControl.write(id);
                        printerControl.write(endBarcode);
                        printerControl.write(leftAlign);
                        printerControl.write(lineFeed);
                        printerControl.write(spaceS);
                        printerControl.write(id);
                        printerControl.write(spaceL);
                        printerControl.write(dob);
                        printerControl.write(spaceL);
                        printerControl.write(gender);
                        printerControl.write(lineFeed);
                        printerControl.write(spaceS);
                        printerControl.write(name);
                        printerControl.write(lineFeed);
                        printerControl.write(spaceS);
                        printerControl.write(date);
                        printerControl.write(spaceL);
                        printerControl.write(nationality);
                        printerControl.write(startPrint);
                        break;
                    case 3:
                        printerControl.write(centerAlign);
                        printerControl.write(specimenId);
                        printerControl.write(startBarcode);
                        printerControl.write(specimenId);
                        printerControl.write(endBarcode);
                        printerControl.write(leftAlign);
                        printerControl.write(lineFeed);
                        printerControl.write(spaceS);
                        printerControl.write(id);
                        printerControl.write(spaceL);
                        printerControl.write(dob);
                        printerControl.write(spaceL);
                        printerControl.write(gender);
                        printerControl.write(lineFeed);
                        printerControl.write(spaceS);
                        printerControl.write(name);
                        printerControl.write(lineFeed);
                        printerControl.write(spaceS);
                        printerControl.write(date);
                        printerControl.write(spaceL);
                        printerControl.write(nationality);
                        printerControl.write(startPrint);
                        break;
                    case 4:
                        printerControl.write(centerAlign);
                        printerControl.write(specimenId);
                        printerControl.write(startBarcode);
                        printerControl.write(specimenId);
                        printerControl.write(endBarcode);
                        printerControl.write(centerAlign);
                        printerControl.write(lineFeed);
                        printerControl.write(date);
                        printerControl.write(startPrint);
                        break;
                }
            }

            printerControl.close();
        } catch (IOException ignored) {
        }
    }

    private static class PrinterTcpIp {

        private OutputStream mOutputStream;

        PrinterTcpIp(String host, int port) throws IOException {
            this.mOutputStream = new TcpIpOutputStream(host, port);
        }

        void write(byte[] command) throws IOException {
            mOutputStream.write(command);
        }

        void close() throws IOException {
            mOutputStream.close();
        }

        private static class TcpIpOutputStream extends PipedOutputStream {
            private final PipedInputStream mPipedInputStream;

            TcpIpOutputStream(String host, int port) throws IOException {
                mPipedInputStream = new PipedInputStream();
                super.connect(mPipedInputStream);

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        try{
                            Socket socket = new Socket(host, port);
                            OutputStream outputStream = socket.getOutputStream();

                            byte[] buf = new byte[1024];
                            while(true) {
                                int n = mPipedInputStream.read(buf);
                                if( n < 0 ) break;
                                outputStream.write(buf,0,n);
                            }
                        } catch (Exception ignored){

                        }
                    }
                };

                Thread mThread = new Thread(runnable);
                mThread.start();
            }
        }

    }

}
