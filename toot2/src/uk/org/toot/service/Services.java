// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.service;

import java.util.Iterator;
import javax.imageio.spi.IIORegistry; // !!! jse 5/6 compamtible hack

/**
 * Services encapsulates the platform=specific provision of service lookup and
 * may require editing for platforms other than Sun's JSE 5.
 */
public class Services
{
    private static IIORegistry registry = IIORegistry.getDefaultInstance();

	@SuppressWarnings(value={"unchecked"})
    public static <T extends ServiceProvider> Iterator<T> lookup(Class<T> clazz) {
		ClassLoader cl = ClassLoader.getSystemClassLoader();
//      return java.util.ServiceLoader.load(clazz, cl).iterator(); // jse 6
///		return (Iterator<T>)sun.misc.Service.providers(clazz, cl); // jse 5 sun
		return registry.lookupProviders(clazz, cl); // jse version compatibility hack !!!
    }

/*    public static void accept(ServiceVisitor v, Class<?> clazz) {
        Iterator<T> pit = providers();
        while ( pit.hasNext() ) {
            T sp = pit.next();
            sp.accept(v, clazz);
        }
	} */
}
