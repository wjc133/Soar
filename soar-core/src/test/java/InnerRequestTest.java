
import com.elite.tools.soar.*;
import com.elite.tools.soar.exception.SoarError;
import com.elite.tools.soar.toolbox.GsonRequest;
import com.elite.tools.soar.toolbox.RequestFuture;
import com.elite.tools.soar.toolbox.Soar;
import com.elite.tools.soar.toolbox.StringRequest;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by wjc133
 * DATE: 16/5/20
 * TIME: 下午3:06
 */
public class InnerRequestTest {
    private RequestQueue mQueue = Soar.newRequestQueue();

    @Ignore
    @Test
    public void testRequest() {
        String url = "http://www.baidu.com";
        StringRequest request = new StringRequest(InnerRequest.Method.GET, url, new InnerResponse.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
            }
        }, new InnerResponse.ErrorListener() {
            @Override
            public void onErrorResponse(SoarError error) {
                System.out.println(error.toString());
            }
        });
        mQueue.add(request);
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Ignore
    @Test
    public void testGsonRequest() {
        String url = "http://localhost:8081/soar/news";
        RequestFuture<News> future = RequestFuture.newFuture();
        GsonRequest<News> request = new GsonRequest<News>(InnerRequest.Method.GET, url, News.class, future, future);
        mQueue.add(request);
        try {
            News news = future.get();
            System.out.println(news);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
