package com.slickdevs.apps.shopex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by HP on 17-10-2015.
 */
public class InventoryList {
    public static Map<String,String> barList;
    public static List<String> voiceList;
    public static List<String> imageList;

    static
    {
        barList = new HashMap<String, String>();
        voiceList = new ArrayList<String>();
        imageList = new ArrayList<String>();
        barList.put("123456789012","Cheese");
        barList.put("milk", "Milk");
        barList.put("biscuits", "Biscuits");
        voiceList.add("Tomatoes");
        voiceList.add("Bananas");
        voiceList.add("Mangoes");
        imageList.add("Handbag");
        imageList.add("Watch");
        imageList.add("Speakers");
    }

    public static Map<String, String> getBarList() {
        return barList;
    }

    public static List<String> getVoiceList() {
        return voiceList;
    }

    public static List<String> getImageList() {
        return imageList;
    }
}
