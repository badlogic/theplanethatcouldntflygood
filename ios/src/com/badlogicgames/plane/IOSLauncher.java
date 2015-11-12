/*
KTHXBYE License Version 2.0!

 * Do NOT slap your corporation's license header anywhere, it's the law :D
 * You can totally do this:
   - fork it, spoon it, knife it
   - redistribute it, in source, binary, pizza or lasagne form
   - modify it, convert it, remix it, blend it
 * ALWAYS retain the CREDITS file, it credits the sources for all art, namely
   - The AWESOME Kenney aka Asset Jesus (http://www.kenney.nl)
   - "Bacterial Love" by RoleMusic (http://freemusicarchive.org/music/Rolemusic/Pop_Singles_Compilation_2014/01_rolemusic_-_bacterial_love)
 * ALWAYS retain this LICENSE file and any related material such as license headers.
 * IF USED FOR COMMERCIAL PURPOSES (including training material, talks, etc.) YOU MUST:
   - Take a photo of you wearing a pink hat, standing on one leg, holding a turtle
     - Turtle may be substituted by chicken, polar bear, or great white shark
   - Post the photo to Twitter along with the message "Am I pretty @badlogicgames?"

 If you violate this license, Karma will be a bitch (and i'll be petty on Twitter)!

Kthxbye, <3 Mario (Zechner, Copyright 2014-2234)
*/
package com.badlogicgames.plane;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
import com.badlogicgames.plane.PlaneGame;

public class IOSLauncher extends IOSApplication.Delegate {
    @Override
    protected IOSApplication createApplication() {
        IOSApplicationConfiguration config = new IOSApplicationConfiguration();
        return new IOSApplication(new PlaneGame(), config);
    }

    public static void main(String[] argv) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(argv, null, IOSLauncher.class);
        pool.close();
    }
}