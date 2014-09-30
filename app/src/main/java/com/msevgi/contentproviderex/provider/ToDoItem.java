
package com.msevgi.contentproviderex.provider;

public class ToDoItem {
   private final String mTask;

   public String getTask() {
      return mTask;
   }

   public ToDoItem(String task) {
      mTask = task;
   }

   @Override
   public String toString() {
      return mTask;
   }
}
