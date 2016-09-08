/**
 * @name             Logger.java
 * @partof           zucred AG
 * @description
 * @author	 		Vasco Mouta
 * @created			08/09/16
 *
 * Copyright (c) 2015 zucred AG All rights reserved.
 * This material, including documentation and any related
 * computer programs, is protected by copyright controlled by
 * zucred AG. All rights are reserved. Copying,
 * including reproducing, storing, adapting or translating, any
 * or all of this material requires the prior written consent of
 * zucred AG. This material also contains confidential
 * information which may not be disclosed to others without the
 * prior written consent of zucred AG.
 */

package com.vascomouta.vmlogger;

import android.util.Log;

/**
 * Created by vmouta on 08/09/16.
 */
public class Logger {

    public static void d(String message) {
        Log.d("Logger", message);
    }
}
