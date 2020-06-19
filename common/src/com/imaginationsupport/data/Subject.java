package com.imaginationsupport.data;

import java.util.ArrayList;
import java.util.List;

public class Subject {
	
   private List<Observer> observers = new ArrayList<>();
 
   public void observe(Observer observer){
      observers.add(observer);		
   }

   public void notifyObservers(){
      for (Observer observer : observers) {
         observer.update(this);
      }
   } 	
}
