package ranjit.com.chartapplication;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import tech.gusavila92.websocketclient.WebSocketClient;

//import org.java_websocket.client.WebSocketClient;

public class WebSocketActivity extends AppCompatActivity {

    org.java_websocket.client.WebSocketClient mWebSocketClient;
    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_socket);
        // connectWebSocket();
        /// createWebSocketClient();
        //socketIO();
        client = new OkHttpClient();
        OkHttpClie();

    }

    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI("wss://streamer.cryptocompare.com");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new org.java_websocket.client.WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
                mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("Websocket", "message " + message);
                        /*TextView textView = (TextView)findViewById(R.id.messages);
                        textView.setText(textView.getText() + "\n" + message);*/
                    }
                });
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }


    public void sendMessage(View view) {
        EditText editText = (EditText) findViewById(R.id.message);
        mWebSocketClient.send(editText.getText().toString());
        editText.setText("");
    }


    private WebSocketClient webClient;

    private void createWebSocketClient() {
        URI uri;
        try {
            uri = new URI("wss://streamer.cryptocompare.com'");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        webClient = new WebSocketClient(uri) {
            @Override
            public void onOpen() {
                System.out.println("onOpen");
                webClient.send("Hello, World!");
            }

            @Override
            public void onTextReceived(String message) {
                System.out.println("onTextReceived");
            }

            @Override
            public void onBinaryReceived(byte[] data) {
                System.out.println("onBinaryReceived");
            }

            @Override
            public void onPingReceived(byte[] data) {
                System.out.println("onPingReceived");
            }

            @Override
            public void onPongReceived(byte[] data) {
                System.out.println("onPongReceived");
            }

            @Override
            public void onException(Exception e) {
                System.out.println(e.getMessage());
            }

            @Override
            public void onCloseReceived() {
                System.out.println("onCloseReceived");
            }
        };

        webClient.setConnectTimeout(10000);
        webClient.setReadTimeout(60000);
        webClient.addHeader("Origin", "http://developer.example.com");
        webClient.enableAutomaticReconnection(5000);
        webClient.connect();
    }

    private void socketIO() {

        try {
            mSocket = IO.socket("http://chat.socket.io");
        } catch (URISyntaxException e) {}

        mSocket.connect();
        mSocket.on(Socket.EVENT_CONNECT,onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT,onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("new message", onNewMessage);
        //mSocket.on("new message", onNewMessage);
       // mSocket.emit("new message", "msgggsgs");

    }
/*
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];
            String username;
            String message;
            try {
                username = data.getString("username");
                message = data.getString("message");
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }

        }
     *//*   @Override
        public void call(final Object.. args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String message;
                    try {
                        username = data.getString("username");
                        message = data.getString("message");
                    } catch (JSONException e) {
                        return;
                    }

                    // add the message to view
                    addMessage(username, message);
                }
            });
        }*//*
    };*/

    /*@Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
        mSocket.off("new message", onNewMessage);
    }*/

    public void createEvent(View view) {
        mSocket.emit("new message", "random");
    }


    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Log.i(TAG, "diconnected");
                    ///isConnected = false;
                    Toast.makeText(WebSocketActivity.this,
                            "connect", Toast.LENGTH_LONG).show();
                }
            });
        /*}
           /* getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!isConnected) {
                        if(null!=mUsername)
                            mSocket.emit("add user", mUsername);
                        Toast.makeText(getActivity().getApplicationContext(),
                                R.string.connect, Toast.LENGTH_LONG).show();
                        isConnected = true;
                    }
                }
            });*/
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
         runOnUiThread(new Runnable() {
                @Override
                public void run() {
                   // Log.i(TAG, "diconnected");
                    ///isConnected = false;
                    Toast.makeText(WebSocketActivity.this,
                           "disconnect", Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Log.i(TAG, "diconnected");
                    ///isConnected = false;
                    Toast.makeText(WebSocketActivity.this,
                            "onConnectError", Toast.LENGTH_LONG).show();
                }
            });


            /*getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "Error connecting");
                    Toast.makeText(getActivity().getApplicationContext(),
                            R.string.error_connect, Toast.LENGTH_LONG).show();
                }
            });*/
        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
           runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                   /* String username;
                    String message;
                    try {
                        username = data.getString("username");
                        message = data.getString("message");
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        return;
                    }

                    removeTyping(username);
                    addMessage(username, message);*/
                }
            });
        }
    };

    private final class EchoWebSocketListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            webSocket.send("Hello, it's SSaurel !");
            webSocket.send("What's up ?");
            webSocket.send(ByteString.decodeHex("deadbeef"));
            webSocket.close(NORMAL_CLOSURE_STATUS, "Goodbye !");
        }
        @Override
        public void onMessage(WebSocket webSocket, String text) {
            output("Receiving : " + text);
            Log.d("text", ""+ text);
        }
        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
           output("Receiving bytes : " + bytes.hex());
            Log.d("bytes.hex()", ""+ bytes.hex());
        }
        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null);
            output("Closing : " + code + " / " + reason);
            Log.d("reason", ""+ reason);
        }
        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
           output("Error : " + t.getMessage());
            Log.d("Throwable", ""+  t.getMessage());
        }
    }

    private OkHttpClient client;
    private void OkHttpClie(){
      // Request request = new Request.Builder().url("ws://echo.websocket.org").build();
        Request request = new Request.Builder().url("wss://streamer.cryptocompare.com").build();

        EchoWebSocketListener listener = new EchoWebSocketListener();
        WebSocket ws = client.newWebSocket(request, listener);

        client.dispatcher().executorService().shutdown();
    }
    private void output(final String txt) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Log.d("output: "," "+ "" + "\n\n" + txt);
                //output.setText(output.getText().toString() + "\n\n" + txt);
            }
        });
    }


}