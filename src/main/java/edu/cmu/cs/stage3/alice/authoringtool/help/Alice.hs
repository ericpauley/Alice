<?xml version='1.0' encoding='ISO-8859-1' ?>
<!DOCTYPE helpset
  PUBLIC "-//Sun Microsystems Inc.//DTD JavaHelp HelpSet Version 1.0//EN"
         "http://java.sun.com/products/javahelp/helpset_1_0.dtd">

<?MyFavoriteApplication this is data for my favorite application ?>

<helpset version="1.0">

  <!-- title -->
  <title>Alice</title>

  <!-- maps -->
  <maps>
     <homeID>alice_home</homeID>
     <mapref location="Map.jhm"/>
  </maps>

  <!-- views -->
  <view>
    <name>TOC</name>
    <label>Alice</label>
    <type>javax.help.TOCView</type>
    <data>AliceTOC.xml</data>
  </view>

<!--
  <view>
    <name>Index</name>
    <label>Index</label>
    <type>javax.help.IndexView</type>
    <data>HolidayIndex.xml</data>
  </view>
-->

  <view>
    <name>Search</name>
    <label>Search</label>
    <type>javax.help.SearchView</type>
    <data engine="com.sun.java.help.search.DefaultSearchEngine">
      JavaHelpSearch
    </data>
  </view>

</helpset>

