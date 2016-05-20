
import com.elite.tools.soar.Request;
import com.elite.tools.soar.RequestQueue;
import com.elite.tools.soar.Response;
import com.elite.tools.soar.SoarError;
import com.elite.tools.soar.toolbox.Soar;
import com.elite.tools.soar.toolbox.StringRequest;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by wjc133
 * DATE: 16/5/20
 * TIME: 下午3:06
 */
public class RequestTest {
    private RequestQueue mQueue = Soar.newRequestQueue();

    @Test
    public void testRequest() {
        String url = "http://www.baidu.com";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
            }
        }, new Response.ErrorListener() {
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
}
