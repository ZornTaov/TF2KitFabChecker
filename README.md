# TF2KitFabChecker
A Java based Killstreak Kit Fabricator calculator to tell the user which fabricators can be made with the given robot parts in the users TF2 inventory.

# Compiling
create the file src/main/java/org/zornco/tf2kitfabchecker/Steam.java and fill it with your personal Steam API key, then run gradle>application>run to test.
```Java
package org.zornco.tf2kitfabchecker;

public class Steam {
    public static final String API_KEY = "APIKEYHERE";
}
```

# Current Bugs
Currently doesn't download players inventory, even if players inventory is marked public.
Access to Item Schema is gated behind use of a Steam_API key, used to not be needed.
