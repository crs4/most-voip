.. java:import:: org.apache.http.conn.util InetAddressUtils

.. java:import:: android.content Context

.. java:import:: android.content.res AssetManager

.. java:import:: android.util Log

Utils
=====

.. java:package:: most.voip.api
   :noindex:

.. java:type:: public class Utils

Methods
-------
bytesToHex
^^^^^^^^^^

.. java:method:: public static String bytesToHex(byte[] bytes)
   :outertype: Utils

   Convert byte array to hex string

   :param bytes:

copyAssets
^^^^^^^^^^

.. java:method:: static void copyAssets(Context ctx)
   :outertype: Utils

getIPAddress
^^^^^^^^^^^^

.. java:method:: public static String getIPAddress(boolean useIPv4)
   :outertype: Utils

   Get IP address from first non-localhost interface

   :param ipv4: true=return ipv4, false=return ipv6
   :return: address or empty string

getMACAddress
^^^^^^^^^^^^^

.. java:method:: public static String getMACAddress(String interfaceName)
   :outertype: Utils

   Returns MAC address of the given interface name.

   :param interfaceName: eth0, wlan0 or NULL=use first interface
   :return: mac address or empty string

getResourcePathByAssetCopy
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public static String getResourcePathByAssetCopy(Context ctx, String assetSubFolder, String fileToCopy)
   :outertype: Utils

   Copy the specified resource file from the assets folder into the "files dir" of this application, so that this resource can be opened by the Voip Lib by providing it the absolute path of the copied resource

   :param ctx: The application context
   :param assetPath: The path of the resource (e.g on_hold.wav or sounds/on_hold.wav)
   :return: the absolute path of the copied resource, or null if no file was copied.

getUTF8Bytes
^^^^^^^^^^^^

.. java:method:: public static byte[] getUTF8Bytes(String str)
   :outertype: Utils

   Get utf8 byte array.

   :param str:
   :return: array of NULL if error was found

loadFileAsString
^^^^^^^^^^^^^^^^

.. java:method:: public static String loadFileAsString(String filename) throws java.io.IOException
   :outertype: Utils

   Load UTF8withBOM or any ansi text file.

   :param filename:
   :throws java.io.IOException:

