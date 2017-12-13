package com.blogspot.anindyabhandari.filelocker;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by Anindya Bhandari on 12/7/2017.
 */

public class JSONHandling {
    final static String debugger = "JSONHandling::";
    public static JSONObject createCredObject(String networkName, String password)
    {
        try {
            JSONObject networkObj = new JSONObject();
            JSONObject content = new JSONObject();
            content.put("Password",password);
            networkObj.put(networkName,content);
            return networkObj;
        }
        catch(Exception e)
        {
            Log.e(debugger,e.getMessage());
            return null;
        }
    }
    public static JSONObject addCredToJSON(JSONObject current, String networkName, String password)
    {
        try {
            JSONObject content = new JSONObject();
            content.put("Password",password);
            current.put(networkName,content);
            return current;
        }
        catch(Exception e)
        {
            Log.e(debugger,e.getMessage());
            return null;
        }
    }
    public static String getPassword(JSONObject credFile, String networkName)
    {
        try
        {
            JSONObject passJSON = credFile.getJSONObject(networkName);
            String password = passJSON.getString("Password");
            return password;
        }
        catch(Exception e)
        {
            Log.e(debugger,e.getMessage());
            return null;
        }
    }
    public static String createStringCredObject(String networkName, String password)
    {
        JSONObject temp = createCredObject(networkName,password);
        String result = temp.toString();
        return result;
    }
    public static String getPasswordFromStringCred(String fileData, String networkName)
    {
        try {
            JSONObject temp = new JSONObject(fileData);
            String password = getPassword(temp, networkName);
            return password;
        }
        catch (Exception e)
        {
            Log.e(debugger,e.getMessage());
            return null;
        }
    }
    public static JSONObject addCredToStringJSON(String fileData, String networkName, String password)
    {
        try {
            JSONObject current = new JSONObject(fileData);
            JSONObject newObj = addCredToJSON(current,networkName,password);
            return newObj;
        }
        catch (Exception e)
        {
            Log.e(debugger,e.getMessage());
            return null;
        }
    }
    public static String addCredGetString(String fileData, String networkName, String password)
    {
        JSONObject newObj = addCredToStringJSON(fileData,networkName,password);
        return newObj.toString();
    }
    public static boolean hasCred(JSONObject current, String networkName)
    {
        return current.has(networkName);
    }
    public static boolean stringHasCred(String fileData, String networkName)
    {
        try {
            JSONObject current = new JSONObject(fileData);
            return hasCred(current, networkName);
        }
        catch (Exception e)
        {
            Log.e(debugger,e.getMessage());
            return false;
        }
    }
}