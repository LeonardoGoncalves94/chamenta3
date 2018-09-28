/************************************************************************
 *                                                                       *
 *  Certificate Service -  Car2Car Core                                  *
 *                                                                       *
 *  This software is free software; you can redistribute it and/or       *
 *  modify it under the terms of the GNU Affero General Public License   *
 *  License as published by the Free Software Foundation; either         *
 *  version 3   of the License, or any later version.                    *
 *                                                                       *
 *  See terms of license at gnu.org.                                     *
 *                                                                       *
 *************************************************************************/
package com.multicert.v2x.datastructures.message.secureddata;


import com.multicert.v2x.asn1.coer.COERSequence;
import com.multicert.v2x.datastructures.base.CrlSeries;
import com.multicert.v2x.datastructures.base.HashedId3;

import java.io.IOException;


public class MissingCrlIdentifier extends COERSequence
{
	private static final int SEQUENCE_SIZE = 2;
	
	private static final int CRACAID = 0;
	private static final int CRLSERIES = 1;

	/**
	 * Constructor used when encoding
	 */
	public MissingCrlIdentifier(HashedId3 cracaid, CrlSeries crlSeries) throws IOException
	{
		super(SEQUENCE_SIZE);
		createSequence();
		setComponentValue(CRACAID, cracaid);
		setComponentValue(CRLSERIES, crlSeries);
	}

	/**
	 * Constructor used when decoding
	 */
	public MissingCrlIdentifier(){
		super(SEQUENCE_SIZE);
		createSequence();
	}

	/**
	 * 
	 * @return cracaid
	 */
	public HashedId3 getCracaid()
	{
		return (HashedId3) getComponentValue(CRACAID);
	}
	
	/**
	 * 
	 * @return crlSeries
	 */
	public CrlSeries getCrlSeries()
	{
		return (CrlSeries) getComponentValue(CRLSERIES);
	}
	
	private void createSequence(){
		addComponent(CRACAID, false, new HashedId3(), null);
		addComponent(CRLSERIES, false, new CrlSeries(), null);
	}

	@Override
	public String toString() {
		return "MissingCrlIdentifier [cracaid=" + getCracaid().toString().replace("HashedId3 ", "") + ", crlSeries=" + getCrlSeries().toString().replace("CrlSeries ", "")  + "]";
	}


}
