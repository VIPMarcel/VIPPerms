package vip.marcel.vipperms.proxy.vipperms.utils.helper;

import java.util.concurrent.TimeUnit;

public class GroupExpiresTimeHelper {

    // z.B. 30d
    public long getExpiresTimeMillis(String dateString) {

        if(dateString.endsWith("s")) {
            int time = Integer.parseInt(dateString.replaceAll("s", ""));
            return (System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(time));
        } else if(dateString.endsWith("m")) {
            int time = Integer.parseInt(dateString.replaceAll("m", ""));
            return (System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(time));
        } else if(dateString.endsWith("h")) {
            int time = Integer.parseInt(dateString.replaceAll("h", ""));
            return (System.currentTimeMillis() + TimeUnit.HOURS.toMillis(time));
        } else if(dateString.endsWith("d")) {
            int time = Integer.parseInt(dateString.replaceAll("d", ""));
            return (System.currentTimeMillis() + TimeUnit.DAYS.toMillis(time));
        } else {
            return System.currentTimeMillis();
        }

    }

}
