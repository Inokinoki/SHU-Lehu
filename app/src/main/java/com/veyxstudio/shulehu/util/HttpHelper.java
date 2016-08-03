package com.veyxstudio.shulehu.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Veyx Shaw on 16-1-10.
 * A universal http request sender.
 */
public class HttpHelper {

    // Constructors.
    public HttpHelper(){
        // Set default encode to utf-8.
        this.encode = "utf-8";
        this.method = METHOD_GET;
        setUrl("http://1.veyxstudio.sinaapp.com/apps/lehu/response.php");
        this.waitTime = 10;
        getMethodParam = new HashMap<>();
        postMethodParam = new HashMap<>();
        cookies = new HashMap<>();
    }
    public HttpHelper(String url){
        this();
        setUrl(url);
    }
    public HttpHelper(int method){
        this();
        this.method = method;
    }
    public HttpHelper(int method,String url,String encode){
        this(url);
        this.method = method;
        this.encode = encode;
    }

    // Constants.
    public static int METHOD_POST  = 1;
    public static int METHOD_GET   = 2;

    public void setNeedEncode(boolean needEncode) {
        this.needEncode = needEncode;
    }

    private boolean needEncode = true;
    private String url;
    private int method;
    private String encode;

    private int waitTime;

    private String result = "";

    private Map<String,String> postMethodParam;
    private Map<String,String> getMethodParam;
    private Map<String,String> cookies;

    public void setTimeOut(int second){
        this.waitTime = second;
    }
    public String getResult(){return this.result;}
    public String getUrl() {return this.url;}
    public void setUrl(String url){this.url = url;}

    public int addParam(String key, String value, boolean query){
        if (method == METHOD_GET || query){
            if (needEncode) {
                try {
                    if (!getMethodParam.containsKey(URLEncoder.encode(key, encode))) {
                        getMethodParam.put(URLEncoder.encode(key, encode)
                                , URLEncoder.encode(value, encode));
                    } else {
                        changeParam(key, value, query);
                    }
                    }catch(UnsupportedEncodingException e){
                        e.printStackTrace();
                        System.out.println("[ERROR] Param add failed.");
                        return -1;
                    }
            }else{
                if (!getMethodParam.containsKey(key)) {
                    getMethodParam.put(key, value);
                } else {
                    changeParam(key, value, query);
                }
            }
            return getMethodParam.size();
        }else if (method == METHOD_POST){
            try {
                if (!postMethodParam.containsKey(URLEncoder.encode(key,encode))) {
                    postMethodParam.put(URLEncoder.encode(key, encode)
                            , URLEncoder.encode(value, encode));
                }else{
                    changeParam(key, value, query);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                System.out.println("[ERROR] Param add failed.");
                return -1;
            }
            return postMethodParam.size();
        }
        return -1;
    }
    public int removeParam(String key,boolean query){
        if (method == METHOD_GET || query){
            try {
                if(getMethodParam.containsKey(URLEncoder.encode(key,encode))){
                    getMethodParam.remove(URLEncoder.encode(key, encode));
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                System.out.println("[ERROR] Param remove failed.");
                return -1;
            }
            return getMethodParam.size();
        }else if (method == METHOD_POST){
            try {
                if(postMethodParam.containsKey(URLEncoder.encode(key,encode))) {
                    postMethodParam.remove(URLEncoder.encode(key, encode));
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                System.out.println("[ERROR] Param remove failed.");
                return -1;
            }
            return postMethodParam.size();
        }
        return -1;
    }
    public int changeParam(String key, String value,boolean query){
        if (method == METHOD_GET || query){
            removeParam(key, query);
            addParam(key, value, query);
            return getMethodParam.size();
        }else if (method == METHOD_POST){
            removeParam(key, query);
            addParam(key, value, query);
            return postMethodParam.size();
        }
        return -1;
    }
    public void clearParam(){
        if (!getMethodParam.isEmpty()){
            getMethodParam.clear();
        }
        if (!postMethodParam.isEmpty()){
            postMethodParam.clear();
        }
    }

    public int addCookie(String key, String value){
        if (cookies.containsKey(key)){
            changeCookie(key, value);
        }else{
            cookies.put(key, value);
        }
        return cookies.size();
    }
    public int removeCookie(String key){
        if (cookies.containsKey(key)){
            cookies.remove(key);
            return cookies.size();
        }
        return -1;
    }
    public int changeCookie(String key, String value){
        if (cookies.containsKey(key)){
            removeCookie(key);
            addCookie(key, value);
        }else{
            addCookie(key, value);
        }
        return cookies.size();
    }
    public void clearCookie(){
        if (!cookies.isEmpty()){
            cookies.clear();
        }
    }

    public void start() throws HttpHelperException{
        HttpURLConnection connection = null;
        // Generate url with query params.
        String urlWithParam = url + "?";
        Set<String> getParamKeys = getMethodParam.keySet();
        for (String key : getParamKeys) {
            try {
                urlWithParam += URLEncoder.encode(key, encode) + "="
                        + URLEncoder.encode(getMethodParam.get(key)
                        , encode)
                        + "&";
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        try {
            // Open link.
            connection = (HttpURLConnection)(new URL(urlWithParam).openConnection());
            // Set request method.
            if (this.method == METHOD_POST) {
                connection.setRequestMethod("POST");
            } else {
                connection.setRequestMethod("GET");
            }
            // Set read timeout.
            connection.setReadTimeout(this.waitTime*1000);
            // Set io.
            connection.setDoInput(true);
            // Set what post method's needs.
            if (this.method == METHOD_POST){
                // Allow to write.
                connection.setDoOutput(true);
                // Param type.
                connection.setRequestProperty("Content-Type"
                        , "application/x-www-form-urlencoded");
            }
            // Set cookies.
            String cookie = "";
            Set<String> keys = cookies.keySet();
            for (String key : keys) {
                cookie += key + "=" +cookies.get(key) + ";";
            }
            connection.setRequestProperty("Cookie", cookie);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("[ERROR] Failed to open connection to "
                    +this.url);
        }
        // All is ready
        if(connection!=null) {
            BufferedReader reader = null;
            if (method==METHOD_POST) {
                PrintWriter printWriter = null;
                try {
                    String param = "";
                    Set<String> keys = postMethodParam.keySet();
                    if (needEncode) {
                        for (String key : keys) {
                            param += URLEncoder.encode(key, encode) + "="
                                    + URLEncoder.encode(postMethodParam.get(key)
                                    , encode)
                                    + "&";
                        }
                    } else {
                        for (String key : keys) {
                            param += key + "=" + postMethodParam.get(key) + "&";
                        }
                    }
                    // Special one. Not common in HttpHelper.
//                    if (url.equals(URLHelper.cardTradeDetail)){
//                        param = param.replace("%252B","+");
//                        param = param.replace("%253A","%3A");
//                    }
                    if (param.endsWith("&")){
                        param = param.substring(0, param.length()-1);
                    }
                    Log.i("HttpHelper", param);
                    // Get OutputStream.
                    printWriter = new PrintWriter(
                            connection.getOutputStream());
                    printWriter.print(param);
                    printWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("[ERROR] Failed to write to "
                            + this.url);
                    throw new HttpHelperException(e.getMessage() + "\n" +
                            "[ERROR] Failed to write to " + this.url);
                } finally {
                    if (printWriter != null) {
                        printWriter.close();
                    }
                }
            }

            try{
                String line;
                StringBuilder stringBuilder =new StringBuilder() ;
                reader = new BufferedReader(
                                new InputStreamReader(
                                        connection.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                this.result = stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("[ERROR] Failed to read from "
                        + this.url);
                throw new HttpHelperException(e.getMessage() + "\n" +
                        "[ERROR] Failed to write to " + this.url);
            }finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

}
