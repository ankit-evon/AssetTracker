package com.asset.tracker.utils;

import com.firebase.client.Firebase;

public class Constant {
	
	public static String baseUrl = "https://assettrackerevontech.firebaseio.com/";
	public static Firebase loginRef = new Firebase(baseUrl + "users/login/login");
	
	public static Firebase baseNewUrl = new Firebase("https://popping-fire-4741.firebaseio.com/users");
	
}
