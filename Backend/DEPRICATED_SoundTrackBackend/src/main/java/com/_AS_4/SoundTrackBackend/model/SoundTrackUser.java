package com._AS_4.SoundTrackBackend.model;


/**
 * Will turn into Admin account
 */
public class SoundTrackUser {
    public String name;

    public String userEmail;

    public String userPassword;

    //followers  list
    // public List<followers>

    /**
     * no longer needed here. Will change when we update this class to be the admin user class.
     */
    public int userType; // when user is turned into a curator, they are given separate account...?
    // 1 for admin, 2 for curator, 3 for basic

    public SoundTrackUser(String name, String userEmail, String userPassword, int userType){
        this.name = name;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.userType = userType;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserEmail() {
        return this.userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPassword() {
        return this.userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    //public int getUserType() {
    //     return this.userType;
    //}

    //public void setUserType(int userType) {
    //    this.userType = userType;
    //}


}
