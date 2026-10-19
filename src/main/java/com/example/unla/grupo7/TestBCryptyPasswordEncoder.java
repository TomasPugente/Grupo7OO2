package com.example.unla.grupo7;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestBCryptyPasswordEncoder {

	public static void main(String[] args) {
		BCryptPasswordEncoder pe = new BCryptPasswordEncoder();
		System.out.println(pe.encode("user"));
	}
}