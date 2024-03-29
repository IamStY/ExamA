package testing.steven.myapplication.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import testing.steven.myapplication.datamodels.OpenDataModel;

public class ApiRequestManager {
    private Gson gson = new Gson();
    private RequestQueue requestQueue;
    private static ApiRequestManager instance;
    public static synchronized ApiRequestManager getInstance() {
        if (instance != null) {
            return instance;
        } else {
            instance = new ApiRequestManager();
            return instance;
        }
    }
    // function - get data from api
    public void getData(final Context context,int all , int skip,ICallback_Notify iCallback_notify) {
        String functionURL = "http://data.coa.gov.tw/Service/OpenData/TransService.aspx?UnitId=QcbUEzN6E6DL&$top="+all+"&$skip="+skip;
        sendGetRequest(new TypeToken<ArrayList<OpenDataModel>>() {
        }.getType(), context, functionURL,  iCallback_notify);
    }

    // all get request entrance -
    private void sendGetRequest(final Type type, Context context, String functionURL, final ICallback_Notify iCallback_notify) {
        if(requestQueue==null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET, functionURL, response -> {

        }, error -> {
            Log.e("error",error.getMessage());
            if(iCallback_notify!=null)
                iCallback_notify.failure();
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");

                return headers;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                  if (response != null) {
                    responseString = String.valueOf(response.statusCode);
                }
                Map<String, String> responseHeaders = response.headers;
                if (response.statusCode == 200||response.statusCode==304) {
                    String responseRoot = new String(response.data);
                    Object object = gson.fromJson(responseRoot, type);
                    if (iCallback_notify != null)
                        iCallback_notify.dataFetched(object);


                } else {

                    // Here we are, we got a 401 response and we want to do something with some header field; in this example we return the "Content-Length" field of the header as a succesfully response to the Response.Listener<String>
                    Response<String> result = Response.success(responseHeaders.get("Content-Length"), HttpHeaderParser.parseCacheHeaders(response));


                    return result;
                }

                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);


    }
}
