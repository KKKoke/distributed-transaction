package com.kkkoke.gtransaction.util;

import com.kkkoke.gtransaction.constant.TransactionProperty;
import com.kkkoke.gtransaction.transactional.GlobalTransactionManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * @author KeyCheung
 * @date 2023/10/28
 * @desc
 */
@Slf4j
public class HttpClient {

    public static String get(String url) {
        String result = "";
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();

            HttpGet httpGet = new HttpGet(url);
            httpGet.addHeader("Content-type", "application/json");
            httpGet.addHeader(TransactionProperty.GROUP_ID, GlobalTransactionManager.getCurrentGroupId());
            httpGet.addHeader(TransactionProperty.TRANSACTION_COUNT, String.valueOf(GlobalTransactionManager.getTransactionCount()));
            CloseableHttpResponse response = httpClient.execute(httpGet);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(response.getEntity(), "utf-8");
            }
            response.close();
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
        }

        return result;
    }
}
