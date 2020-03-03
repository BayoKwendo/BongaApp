package com.navigatpeer.models;

import android.view.View;

import com.navigatpeer.R;

import java.util.ArrayList;

/**
 * Simple POJO model for example
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class Item {

    private String price;
    private String fromAddress;
    private String toAddress;
    private String time;
    private String content;
    private String footer;
    private int image;
    private String head;
//    private int image;

    private View.OnClickListener requestBtnClickListener;




    public Item(String price, String toAddress, String fromAddress, String head, String time, int image, String content, String footer) {
        this.price = price;
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.time = time;
        this.head = head;
        this.image = image;
        this.content = content;
        this.footer = footer;

    }


//    public Item(int image){
//        this.image=image;
//    }
    public String getPrice() {
        return price;
    }

//    public static int getImage(int image) {
//      image;
//    }

    public void setPrice(String price) {
        this.price = price;
    }



    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public String getFooter() {
        return footer;
    }
    public void setFooter(String footer) {
        this.footer = footer;
    }
    public String getHead(){
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }
    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public View.OnClickListener getRequestBtnClickListener() {
        return requestBtnClickListener;
    }

    public void setRequestBtnClickListener(View.OnClickListener requestBtnClickListener) {
        this.requestBtnClickListener = requestBtnClickListener;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        if (fromAddress != null ? !fromAddress.equals(item.fromAddress) : item.fromAddress != null)
            return false;
        if (toAddress != null ? !toAddress.equals(item.toAddress) : item.toAddress != null)
            return false;
         return !(time != null ? !time.equals(item.time) : item.time != null);

    }

    @Override
    public int hashCode() {
        int result = price != null ?   price.hashCode() : 0;
        result = 31 * result + (toAddress != null ? toAddress.hashCode() : 0);
        result = 31 * result + (fromAddress != null ? fromAddress.hashCode() : 0);
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + (head != null ? head.hashCode() : 0);
        return result;
    }

    /**
     * @return List of elements prepared for tests
     */
    public static ArrayList<Item> getTestingList() {
        ArrayList<Item> items = new ArrayList<>();


        items.add(new Item("NO",  "Brief explanation ",
                "What is Cervical Cancer?","What is Cervical Cancer",
                "CANCER", R
                .drawable.intr,"Cervical cancer " +
                "is cancer that begins in the uterine cervix, the lower end of the uterus that contacts the upper vagina.","Cervical cancer remains a common cause of cancer and cancer death" +
                " in women in developing countries without access to SCREENING (Pap testing)" +
                " for cervical cancer or vaccines against human papillomaviruses (HPVs)."));
        items.add(new Item("ZERO",  "Preventions ", "Risk Factors",
                "Preventions","DEATHS", R.drawable.prev,"Cervical screening","Human papillomavirus (HPV) vaccine"));
        items.add(new Item("STOP",  "Symptoms",
                "Signs","Signs and Symptoms","CERVICAL",R.drawable.signs,"Abnormal vaginal bleeding","pelvic pain"));
        items.add(new Item("HEALTHY",  "How to detect cervical",
                "Diagnosis", "Diagnosis and Tests","WOMEN",R.drawable.dia,"HPV DNA testing","Biopsy"));
        items.add(new Item("NO",  "Cervical treatment procedures",
                "Treatment", "Treatment","MOURNING",R.drawable.tret,"Radiotherapy","Chemotherapy"));

        return items;

    }

//    public static ArrayList<Item> getList() {
//        ArrayList<Item> items = new ArrayList<>();
//        items.add(new Item(R.drawable.head_image));
//
//        return items;
//
//    }
//public static ArrayList<Item> getList() {
//    ArrayList<Item> items = new ArrayList<>();
//    items.add(new Item(R.drawable.head_image));
//    items.add(new Item(R.drawable.head_image));
//    items.add(new Item(R.drawable.head_image));
//    items.add(new Item(R.drawable.head_image));
//    items.add(new Item(R.drawable.head_image));
//    return items;
//
//}


}
