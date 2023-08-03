package com._AS_4.SoundTrackBackend.POJOs;

import org.mindrot.jbcrypt.BCrypt;

public class Password {
    private static final int STRENGTH_FACTOR = 4;
    public static String hashPassword(String unhashedPassword){
        String salt = BCrypt.gensalt(STRENGTH_FACTOR); //Salt with strength factor of 4
        return BCrypt.hashpw(unhashedPassword, salt);
    }
    //yes, this looks dumb but I wanted to keep all jbcrypt methods contained here.
    public static boolean verifyPassword(String providedPassword, String hashedPassword){
        return BCrypt.checkpw(providedPassword, hashedPassword);
    }
}
