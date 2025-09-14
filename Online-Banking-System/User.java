package com.bank;

public class User{
    /*These are the properties or "fields" of a User.
      The 'private' keyword is very important. It means these variables can only be
      accessed or modified by methods inside this User class. This is a core concept
      called ENCAPSULATION, which protects the data from accidental changes. 
    */
    private int userId;
    private String fullName;
    private String userName;
    private String email;

    /*
     * This is a "constructor". It's a special method that is called when we create a new User object.
     * Its job is to take the initial values and assign them to the private fields of the object.
     */
    public User(int userId, String fullName, String userName, String email){
        /*
         * 'this.userId' refers to the private field of the object.
           'userId' refers to the value that was passed into the constructor.
            This line says, "Set this object's userId to the userId value we were given."
         */
        this.userId = userId;
        this.fullName = fullName;
        this.userName = userName;
        this.email = email;
    }
    // These are "Getter" methods. Since the fields are private, we need a safe, public
    // way to read their values from outside the User class. Getters provide that read-only access.
    public int getUserId(){
        return userId;
    }
    public String getFullName(){
        return fullName;
    }
    public String getUserName(){
      return userName;
    }
    public String getEmail(){
      return email;
    }
}