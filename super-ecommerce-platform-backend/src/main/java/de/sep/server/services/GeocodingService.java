package de.sep.server.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.*;
import de.sep.server.util.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class GeocodingService {

   Logger logger = new Logger(getClass().getCanonicalName());
   GeoApiContext context;
   Gson gson;

   public GeocodingService(){
      context = new GeoApiContext.Builder()
              .apiKey("AIzaSyA77VHN-8ZsuzeDHv4nXflWepqFtxRgUTE")
              .build();
      gson = new GsonBuilder().setPrettyPrinting().create();
   }

   public String [] getAddress(String address){
     // GeocodingResult[] response = new GeocodingResult[0];
      String [] fulladdress = new String [8];
      try {
       GeocodingResult[] response = GeocodingApi.geocode(context, address).await();
       if (response == null) return null;
       fulladdress [0] = response [0].formattedAddress;
         Arrays.stream(response[0].addressComponents).forEach(component -> {
            AddressComponentType [] type = component.types;
            if (type[0].toString().contains("street_number")) fulladdress [2] = component.longName;
            if (type[0].toString().contains("route")) fulladdress [1] = component.longName;
            if (type[0].toString().contains("locality")) fulladdress [4] = component.longName;
            if (type[0].toString().contains("postal_code")) fulladdress [3] = component.longName;
         });
         fulladdress [5] = String.valueOf(response[0].geometry.location.lat);
         fulladdress [6] = String.valueOf(response[0].geometry.location.lng);
         fulladdress [7] = "true";
         Arrays.stream(fulladdress).forEach( node -> {
            if (node == null) fulladdress [7] = "false";
         });
         return fulladdress;

      } catch (ApiException e) {
         e.printStackTrace();
      } catch (InterruptedException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
      return null;
   }



   public GeocodingResult[] getCoordinates (String address){
      GeocodingResult[] response = new GeocodingResult[0];
      try {
         response = GeocodingApi.geocode(context, address).await();
      } catch (ApiException e) {
         e.printStackTrace();
      } catch (InterruptedException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
      LatLng coordinates = new LatLng(response[0].geometry.location.lat,response[0].geometry.location.lng);
      return response;
   }

   public double getDistance (LatLng origin, LatLng destination){
      String [] origins = {(origin.toString())};
      String [] destinations = {(destination.toString())};
      DistanceMatrix response = null;
      if (origin == destination) return 0;
      try {
         response = DistanceMatrixApi.getDistanceMatrix(context,origins,destinations).await();
      } catch (ApiException e) {
         e.printStackTrace();
      } catch (InterruptedException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
      return ((double)response.rows[0].elements[0].distance.inMeters)/1000;
   }
   public ArrayList<Double> getDistances (String [] origins, String [] destinations){
      DistanceMatrix response = null;
      try {
         response = DistanceMatrixApi.getDistanceMatrix(context,origins,destinations).await();
      } catch (ApiException e) {
         e.printStackTrace();
      } catch (InterruptedException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
      ArrayList<Double> distances = new ArrayList<>();
      for (int i= 0; i<destinations.length;i++){
         distances.add(((double)response.rows[0].elements[i].distance.inMeters)/1000);
      }
      return distances;
   }

}
