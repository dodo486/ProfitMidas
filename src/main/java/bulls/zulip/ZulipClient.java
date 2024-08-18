package bulls.zulip;

import bulls.log.DefaultLogger;
import bulls.tool.util.MsgUtil;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public enum ZulipClient {
    Instance;

    private final String ZULIP_ADDR = "https://172.28.203.110";
    private final String ZULIP_BOT_API_KEY = "AiDupMHlu9xcoFJuwgwEWgNltBDAICCj";
    private final String ZULIP_BOT_EMAIL_ADDR = "oracleArena-bot@172.28.203.110";
    private HttpClient httpclient;
    private HttpPost httppost;

    ZulipClient() {
        init();
    }

    public void init() {
        try {
            // Self-signed Certificate handling
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (certificate, authType) -> true).build();
            CredentialsProvider provider = new BasicCredentialsProvider();
            provider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(ZULIP_BOT_EMAIL_ADDR, ZULIP_BOT_API_KEY));
            httpclient = HttpClients.custom().setSslcontext(sslContext).setSSLHostnameVerifier(new NoopHostnameVerifier()).setDefaultCredentialsProvider(provider).build();
            httppost = new HttpPost(ZULIP_ADDR + "/api/v1/messages");
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            DefaultLogger.logger.error("ZulipClient 초기화 실패!\n{}", e.toString());
        }
    }

    public boolean postMessage(String stream, String topic, String msg) {
        try {
            httppost.setHeader("Accept", "application/json");
            httppost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

            List<NameValuePair> params = new ArrayList<>(4);
            params.add(new BasicNameValuePair("type", "stream"));
            params.add(new BasicNameValuePair("to", stream));
            params.add(new BasicNameValuePair("subject", topic));
            params.add(new BasicNameValuePair("content", msg));
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

            HttpResponse response = httpclient.execute(httppost);

            if (response.getStatusLine().getStatusCode() == 200) {
                ResponseHandler<String> handler = new BasicResponseHandler();
                String body = handler.handleResponse(response);
                System.out.println(body);
                return true;
            } else {
                System.out.println("response is error : " + response.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean postMessage(String stream, String topic, String msg, Object... arguments) {
        String formattedMsg = MsgUtil.getFormattedString(msg, arguments);
        return postMessage(stream, topic, formattedMsg);
    }
}
