package mediator;

import java.util.Date;

class ChatRoom {
  public static void showMessage(User user, String message){
    System.out.println(new Date() + " [" + user.getName() + "] : " + message);
  }
}

class User {
  private final String name;

  public String getName() {
    return name;
  }

  public User(String name){
    this.name  = name;
  }

  public void sendMessage(String message){
    ChatRoom.showMessage(this,message);
  }
}

public class BasicClientDemo {
  public static void main(String[] args) {
    User robert = new User("Robert");
    User john = new User("John");

    robert.sendMessage("Hi! John!");
    john.sendMessage("Hello! Robert!");
  }
}
