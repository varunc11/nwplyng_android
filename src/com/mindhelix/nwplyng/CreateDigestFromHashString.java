package com.mindhelix.nwplyng;

import java.math.BigInteger;
import java.security.MessageDigest;

public class CreateDigestFromHashString {
	
	public static String sha1Hash( String toHash )
	{
	    String hash = null;
	    try
	    {
	        MessageDigest digest = MessageDigest.getInstance( "SHA-1" );
	        digest.update( toHash.getBytes(), 0, toHash.length() );
	        hash = new BigInteger( 1, digest.digest() ).toString( 16 );
	    }
	    catch(Exception e )
	    {
	        e.printStackTrace();
	    }
	    return hash;
	}

}
