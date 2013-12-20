package ru.mobstudio.voicechanger.httpCore;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.LayeredSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.Socket;

import ru.mobstudio.voicechanger.R;
import ru.mobstudio.voicechanger.Settings;
import ru.mobstudio.voicechanger.Utils.Debug;

/**
 * Created by Evgenij on 11.11.13.
 *
 */
public class HttpSocket extends AsyncTask<Integer, Integer, Responce>
{
    //-----------------------------
    //Constants
    //-----------------------------

    private static final int HTTP_OK = 200;
    private static final String TEST_ADDRESS = "https://voicechanger.mobstudio.ru/api/app/v1/";
    private static final String ADDRESS = "https://voicechanger.mobstudio.ru/api/app/v1/";
    private static final String ADDRESS_API = "https://voicechanger.mobstudio.ru/api/app/";

    //-----------------------------
    //Variables
    //-----------------------------

    private JSONRequest mRequest;
    private OnFinishTask mOnFinish;
    private Context mContext;

    //-----------------------------
    //Ctors
    //-----------------------------

    public HttpSocket(final JSONRequest r, final OnFinishTask i, final Context c)
    {
        this.mRequest = r;
        this.mOnFinish = i;
        this.mContext = c;
    }

    //-----------------------------
    //Methods
    //-----------------------------

    private void workAroundReverseDnsBugInHoneycombAndEarlier(HttpClient client)
    {
        // Android had a bug where HTTPS made reverse DNS lookups (fixed in Ice Cream Sandwich)
        // http://code.google.com/p/android/issues/detail?id=13117
        SocketFactory socketFactory = new LayeredSocketFactory()
        {
            SSLSocketFactory delegate = SSLSocketFactory.getSocketFactory();
            @Override
            public Socket createSocket() throws IOException
            {
                return delegate.createSocket();
            }

            @Override
            public Socket connectSocket(Socket sock, String host, int port, InetAddress localAddress, int localPort, HttpParams params) throws IOException
            {
                return delegate.connectSocket(sock, host, port, localAddress, localPort, params);
            }

            @Override
            public boolean isSecure(Socket sock) throws IllegalArgumentException
            {
                return delegate.isSecure(sock);
            }

            @Override
            public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException
            {
                injectHostname(socket, host);
                return delegate.createSocket(socket, host, port, autoClose);
            }

            private void injectHostname(Socket socket, String host)
            {
                try
                {
                    Field field = InetAddress.class.getDeclaredField("hostName");
                    field.setAccessible(true);
                    field.set(socket.getInetAddress(), host);
                }
                catch (Exception ignored) {   }
            }
        };
        client.getConnectionManager().getSchemeRegistry().register(new Scheme("https", socketFactory, 443));
    }

    @Override
    protected Responce doInBackground(Integer... params)
    {
        Debug.i("Начнем запрос на сервак");

        DefaultHttpClient client = new DefaultHttpClient();
        workAroundReverseDnsBugInHoneycombAndEarlier(client);

        String addr;
        if(!mRequest.getKey().equals(Settings.CHECK_API))
            if(Settings.IS_DEBUG)
                addr = TEST_ADDRESS + mRequest.getKey();
            else
                addr = ADDRESS + mRequest.getKey();
        else
            addr = ADDRESS_API + mRequest.getKey();

        Debug.i("Адрес до сервака: " + addr);

        HttpPost httppost = new HttpPost(addr);
        StringEntity se;
        try
        {
            se = new StringEntity("");
            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httppost.setEntity(se);
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        String data;
        if(Settings.mSecret.length() > 0)
            data = Settings.mUserID + ":" + Settings.mSecret;
        else
            data = Settings.mUserID + ":";

        Debug.i("В шапку кидаем: " + data);

        //Стандартный заголовок
        httppost.addHeader("Authorization", "Basic " + Base64.encodeToString(data.getBytes(), Base64.NO_WRAP));

        for(Header h :httppost.getAllHeaders())
        {
            Debug.i("Шапка: " + h.toString());
        }

        try
        {
            Debug.i("Заполняем данными POST");
            if(mRequest.getData() != null)
            {
                httppost.setEntity(new StringEntity(mRequest.getData().toString()));
                Debug.i("Data = " + EntityUtils.toString(httppost.getEntity()));
            }
            Debug.i("Отправляем  на сервак POST");

            HttpResponse response = client.execute(httppost);
            if(response.getStatusLine().getStatusCode() != HTTP_OK)
            {
                Debug.i("Ответ не с 200!! Код ответа = " + response.getStatusLine().getStatusCode());
                String mess = "Ошибка в протоколе";
                if(response.getStatusLine().getStatusCode() == 500)
                    mess = mContext.getString(R.string.Internal_Server_Error);
                return new Responce(mRequest.getKey(), true, response.getStatusLine().getStatusCode(), mess, null);
            }

            Debug.i("Ответ пришел, обработаем его.");
            HttpEntity responseEntity = response.getEntity();
            long length = responseEntity.getContentLength();
            InputStream is = responseEntity.getContent();

            Debug.i("Перегоним данные в строку");
            String re = new String(readBytesFromStream(is, length));
            JSONObject json = new JSONObject(re);
            Debug.i("Нам пришло: " + json.toString());
            return new Responce(mRequest.getKey(), false, -1, "", json);
        }
        catch (Exception e)
        {
            Debug.e(e);
            return new Responce(mRequest.getKey(), true, -1, "Ошибка соединения с сервером", null);
        }
    }

    @Override
    protected void onPostExecute(Responce resp)
    {
        if(mOnFinish != null)
            mOnFinish.finishTask(resp);
    }

    private byte[] readBytesFromStream(InputStream is, long length) throws IOException
    {
        int bytesRead, offset = 0;
        if (length > Integer.MAX_VALUE)
        {
            Debug.e("Слишком большой объем данных!!!!!!");
            return null;
        }
        int BLOCK_SIZE = 78*10240;
        byte[] data = new byte[20480];

        do
        {
            bytesRead = is.read(data, offset, data.length - offset);
            offset+=bytesRead;
            if(offset == data.length)
            {
                //ext array
                byte[] mas = new byte[data.length + BLOCK_SIZE];
                for(int i =0 ;i< data.length; i++)
                {
                    mas[i] = data[i];
                }
                data = mas;
            }
        }
        while(bytesRead > 0);
        return data;
    }

    //-----------------------------
    //Getters/Setters
    //-----------------------------

    //-----------------------------
    //Inner Classes
    //-----------------------------
}
