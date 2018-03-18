package io.zipcoder;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;

import java.lang.reflect.Array;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemParser {
    ArrayList<String> rawItems;
    private ArrayList<ValueList> theObjects;
    private Map<String, ArrayList<ValueList>> theMap;
    private Integer itemCount = 0;
    private Integer exceptionsCount = 0;

    ItemParser(String raw) {
        rawItems = parseRawDataIntoStringArray(raw);
        try {
            createMap();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    ItemParser() {

    }

    private void expectionHandled() throws ItemParseException {
        exceptionsCount++;
        throw new ItemParseException();
    }


    //Method is taking a String stringPattern and spliting stringPattern based on the inputSt
    private ArrayList<String> splitStringWithRegexPattern(String stringPattern, String inputString) {
        return new ArrayList<String>(Arrays.asList(inputString.split(stringPattern)));
    }

    //splits stirng based on ##
    public ArrayList<String> parseRawDataIntoStringArray(String rawData) {
        String stringPattern = "##";
        ArrayList<String> response = splitStringWithRegexPattern(stringPattern, rawData);
        return response;
    }

    //splits string based on symbols to get key-values
    //not accessing in ourparse method
    public ArrayList<String> findKeyValuePairsInRawItemData(String rawItem) {
        String stringPattern = "[@|^|*|%|!|;]";
        ArrayList<String> response = splitStringWithRegexPattern(stringPattern, rawItem);
        return response;
    }


    public Item parseStringIntoItem(String rawItem) throws ItemParseException {

        String name = checkName(rawItem);
        Double price = Double.valueOf(checkPrice(rawItem));
        String type = checkType(rawItem);
        String expiration = checkExpiration(rawItem);
        if (checkName(rawItem) == null | checkPrice(rawItem) == null) expectionHandled();

        return new Item(name, price, type, expiration);

    }

    public ArrayList<Item> rawItemToCleanItem() throws ItemParseException {
        ArrayList<Item> itemArrayList = new ArrayList<Item>();
        for (int i = 0; i < rawItems.size(); i++) {
            String s = rawItems.get(i);

            Item item = parseStringIntoItem(rawItems.get(i));
            System.out.println(item.toString());
            itemArrayList.add(item);
        }
        return itemArrayList;
    }

    class ValueList {
        private Double price;
        private Integer count;

        ValueList(Double thePrice, Integer theCount) {
            this.price = thePrice;
            this.count = theCount;
        }

        public Double getPrice() {
            return price;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        @Override
        public String toString() {
            return
                    "Price=" + price +
                            ", seen=" + count;
        }
    }

    public Map<String, ArrayList<ValueList>> createMap() throws Exception {
        theMap = new HashMap<String, ArrayList<ValueList>>();
        ArrayList<Item> temp = rawItemToCleanItem();
        ValueList theValueAndCount;
        Double price;
        Integer count = 1;

        for (int i = 0; i < temp.size(); i++) {

            String name = temp.get(i).getName();
            price = temp.get(i).getPrice();
            if (!theMap.containsKey(name)) {
                add(name, price, count);
            } else {

                updateTheMapByIncreasingCount(name, price);

            }
        }
        return theMap;
    }

    public void add(String theName, Double thePrice, Integer theCount) {
        ValueList theValueAndCount = new ValueList(thePrice, theCount);
        theObjects = new ArrayList<ValueList>();
        theObjects.add(theValueAndCount);
        theMap.put(theName, theObjects);
    }

    public void updateTheMapByIncreasingCount(String theName, Double price) {
        ArrayList<ValueList> theList = theMap.get(theName);
        ValueList theValueAndCount;
        Integer theCount = 1;
        for (int j = 0; j < theList.size(); j++) {
            ValueList values = theList.get(j);
            Double thePrice = values.getPrice();
            if (thePrice.equals(price)) {
                theCount = values.getCount();
                values.setCount(theCount + 1);
                theMap.put(theName, theList);
                break;
            } else {
                theValueAndCount = new ValueList(price, theCount);
                theMap.get(theName).add(theValueAndCount);
            }
        }
    }

    public Integer getItemCount(Integer item) {
        itemCount += item;
        return itemCount;
    }

    public String getPrice(ArrayList<ValueList> values) {
        StringBuilder theName = new StringBuilder();

        for (int i = 0; i < values.size(); i++) {
            ValueList value = values.get(i);
            Integer itemCount = getItemCount(value.getCount());
            theName.append("Price:    " + value + "         " + "Seen " + itemCount + "\n");
        }
        return theName.toString();
    }

    public void print() {
        for (Map.Entry<String, ArrayList<ValueList>> entry : theMap.entrySet()) {
            //name of the Product;
            String name = entry.getKey();
            String price = getPrice(entry.getValue());
            System.out.println("Name" + "    " + name + "\n" + price);
        }
    }


    public String fixCookie(String input) {
        Pattern patternCookies = Pattern.compile("[Cc][Oo0][Oo0][Kk][Ii][Ee][Ss]");
        Matcher matcherCookies = patternCookies.matcher(input);
        return matcherCookies.replaceAll("cookies");
    }

    public String checkName(String input) {
        String newInput = fixCookie(input);
        Pattern patternName = Pattern.compile("([Nn]..[Ee]:)(\\w+)");
        Matcher matcherName = patternName.matcher(newInput);

        if (matcherName.find()) {
            return matcherName.group(2).toLowerCase();
        }
        return null;
    }

    public String checkPrice(String input) {
        Pattern patternPrice = Pattern.compile("([Pp]...[Ee]:)(\\d\\.\\d{2})");
        Matcher matcherPrice = patternPrice.matcher(input);

        if (matcherPrice.find()) {
            return matcherPrice.group(2).toLowerCase();
        }

        return null;
    }

    public String checkType(String input) {
        Pattern patternType = Pattern.compile("([Tt]..[Ee]:)(\\w+)");
        Matcher matcherType = patternType.matcher(input);

        if (matcherType.find()) {
            return matcherType.group(2).toLowerCase();
        }
        return null;
    }

    public String checkExpiration(String input) {
        Pattern patternExpiration = Pattern.compile("([Ee]........[Nn]:)(\\d\\/\\d{2}\\/\\d{4})");
        Matcher matcherExpiration = patternExpiration.matcher(input);

        if (matcherExpiration.find()) {
            return matcherExpiration.group(2).toLowerCase();
        }

        return null;
    }
}
