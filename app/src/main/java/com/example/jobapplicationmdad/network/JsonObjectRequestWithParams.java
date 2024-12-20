package com.example.jobapplicationmdad.network;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * A custom implementation of Volley 'Request' to send HTTP requests and receive JSON responses
 *
 * @warning
 */
public class JsonObjectRequestWithParams extends Request<JSONObject> {

    private Response.Listener<JSONObject> listener;
    private Map<String, String> params;
    private Map<String, String> headers;


    /**
     * Request providing ONLY params, no headers
     * @param method           The HTTP method to use for the request
     * @param url              The URL to send the request to
     * @param params           A map of key-value pairs to include as parameters
     * @param responseListener The listener for handling a successful JSON response
     * @param errorListener    The listener for handling errors during the request
     */
    public JsonObjectRequestWithParams(int method, String url, Map<String, String> params, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.listener = responseListener;
        this.params = params;
    }

    /**
     * Request providing both params and headers
     * @param method
     * @param url
     * @param params
     * @param headers
     * @param responseListener
     * @param errorListener
     */
    public JsonObjectRequestWithParams(int method, String url, Map<String, String> params, Map<String, String> headers, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.listener = responseListener;
        this.params = params;
        this.headers = headers;
    }

    /**
     * GET request providing ONLY headers
     * @param url
     * @param headers
     * @param responseListener
     * @param errorListener
     */
    public JsonObjectRequestWithParams(String url, Map<String, String> headers, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        super(Request.Method.GET, url, errorListener);
        this.listener = responseListener;
        this.headers = headers;
    }


    @Override
    protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
        return params;
    }

    /**
     * Overridden method to handle JSONObject response
     *
     * @param response The raw network response received
     * @return JSONObject response
     */
    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        listener.onResponse(response);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }
}
