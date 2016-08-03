package com.veyxstudio.shulehu.util;

/**
 * Created by Veyx Shaw on 16-1-11.
 * Specified HttpHelper to login.
 */
public class LoginHelper extends HttpHelper{
    public LoginHelper(){
        super(HttpHelper.METHOD_POST, URLHelper.login, "utf-8");
        super.addParam("action", "1", true);
    }
    public LoginHelper(String username, String password){
        this();
        super.addParam("username", username, false);
        super.addParam("password", password, false);
    }

    public int setUsername(String username){
        return super.addParam("username", username, false);
    }
    public int setPassword(String password){
        return super.addParam("password", password, false);
    }
    public int setUser(String username, String password){
        int result1 = setUsername(username);
        int result2 = setPassword(password);
        return result1<0 ? result1: result2;
    }

    /*
     * @return <code>"00000000-0000-0000-0000-000000000000"</code> Passport
     *          <code>"1"</code> Wrong password.
     *          <code>"2"</code> Network issue.
     */
    public String getPassport() throws HttpHelperException{
        super.start();
        String passport;
        passport = super.getResult();
        // Success on login.
        if (passport.startsWith("0")) {
            passport = passport.substring(
                    passport.indexOf("g=") +
                            String.valueOf("g=").length(),
                    passport.indexOf("g=") +
                            String.valueOf("g=").length()
                            + "00000000-0000-0000-0000-000000000000"
                            .length());
            return passport;
        } else {
            return "1";
        }
    }
}
