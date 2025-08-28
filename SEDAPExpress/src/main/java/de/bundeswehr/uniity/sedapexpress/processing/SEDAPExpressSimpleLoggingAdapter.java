/*******************************************************************************
 * Copyright (C)2012-2024, German Federal Armed Forces - All rights reserved.
 *
 * MUKdo II
 * Wibbelhofstraße 3
 * 26384 Wilhelmshaven
 * Germany
 *
 * This source code is part of the MEDAS/SEDAP Project.
 * Person of contact (POC): Volker Voß, MUKdo II A, Wilhelmshaven
 *
 * Unauthorized use, modification, redistributing, copying, selling and
 * printing of this file in source and binary form including accompanying
 * materials is STRICTLY prohibited.
 *
 * This source code and it's parts is classified as OFFEN / NATO UNCLASSIFIED!
 *******************************************************************************/
package de.bundeswehr.uniity.sedapexpress.processing;

/**
 * Very simple logging subscriber, which logs everything to the console
 * 
 * @author Volker Voß
 *
 */
public class SEDAPExpressSimpleLoggingAdapter implements SEDAPExpressInputLoggingSubscriber, SEDAPExpressOutputLoggingSubscriber {

    public SEDAPExpressSimpleLoggingAdapter() {
	// Nothing to do
    }

    @Override
    public void processSEDAPExpressOutputLoggingMessage(String message) {
	System.out.println(message);
    }

    @Override
    public void processSEDAPExpressInputLoggingMessage(String message) {
	System.out.println(message);
    }

}
