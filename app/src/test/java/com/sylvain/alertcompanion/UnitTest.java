package com.sylvain.alertcompanion;

import com.sylvain.alertcompanion.utils.Converter;

import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;


public class UnitTest {

    /*CONVERTER CLASS*/
    @Test
    public void convertTimeIntToStringTest(){
        String hour;
        hour  = Converter.convertTimeIntToString(9, 45);
        assertEquals("09:45", hour);
    }

    @Test
    public void convertTimeStringToDateTest() throws ParseException {
        Date dateActual = Converter.convertTimeStringToDate("09:45");
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date dateExpected = dateFormat.parse("09:45");
        assertEquals(dateExpected, dateActual);
    }

    @Test
    public void convertListContactToStringTest(){
        List<String> lst = new ArrayList<>();
        lst.add("robert/0601020304");
        lst.add("francois/0658412056");
        lst.add("sylvain/0785749623");
        String actual = Converter.convertListContactToString(lst);
        assertEquals("robert/0601020304,francois/0658412056,sylvain/0785749623,", actual);
    }

    @Test
    public void convertStringContactToListTest(){
        String contact = "robert/0601020304,francois/0658412056,sylvain/0785749623";
        List<String> lstACtual = Converter.convertStringContactToList(contact);
        List<String> lstExpected = new ArrayList<>();
        lstExpected.add("robert/0601020304");
        lstExpected.add("francois/0658412056");
        lstExpected.add("sylvain/0785749623");
        assertEquals(lstExpected, lstACtual);
    }
}