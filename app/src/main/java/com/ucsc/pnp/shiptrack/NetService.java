package com.ucsc.pnp.shiptrack;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;


public class NetService extends Service {
Context servicecontext;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        servicecontext=getApplicationContext();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);



        Runnable mainThreadrunnable=new Runnable() {
            @Override
            public void run() {

                Runnable runnable=new Runnable() {
                    @Override
                    public void run() {
                        Socket socket = null;

                        try {
                            socket = new Socket("192.34.63.88", 8072);


                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024 * 1024);
                                byte[] buffer = new byte[1024 * 1024];

                                int bytesRead;
                                InputStream inputStream = socket.getInputStream();

         /*
          * notice: inputStream.read() will block if no data return
          */
                                bytesRead = inputStream.read(buffer);
                                Log.e("Service", "Data In");
                                byteArrayOutputStream.write(buffer, 0, bytesRead);
                                String data = byteArrayOutputStream.toString("UTF-8");

                                Intent intent = new Intent();
                                intent.putExtra("data", data);
                                intent.setAction("com.ucsc.pnp.ais.CUSTOM");
                                sendBroadcast(intent);
                                Log.e("Service", data);


                        } catch (UnknownHostException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();

                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();

                        } finally {
                            if (socket != null) {
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }

                    }
                };


                for (int k=0;true;k++) {

                    try {
                        Thread.sleep(10000,0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Thread thread = new Thread(runnable);
                    thread.start();

                }

            }
        };
        Thread mainthread=new Thread(mainThreadrunnable);
        mainthread.start();

        return START_STICKY;
    }
}
