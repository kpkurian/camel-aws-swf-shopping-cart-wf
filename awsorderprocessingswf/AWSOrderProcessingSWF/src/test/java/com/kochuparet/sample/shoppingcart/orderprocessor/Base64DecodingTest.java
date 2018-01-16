package com.kochuparet.sample.shoppingcart.orderprocessor;

import static org.junit.Assert.*;

import java.util.Base64;

import org.junit.Test;

public class Base64DecodingTest {

	@Test
	public void test() {
		String input = "eyJvcmRlck51bWJlciI6ImMzZGQ2MmU2LTE5Y2YtNDVhNC1hNDMxLTUyYWNiYzQ1NDNmZSIsInN0YXRlIjoiVmVyaWZ5T3JkZXIiLCJzdGF0dXMiOiJJbnByb2dyZXNzIiwiZXJyb3JDb2RlIjpudWxsLCJlcnJvck1lc3NhZ2UiOm51bGwsIm9yZGVyRGF0ZSI6bnVsbCwiY3VzdElkIjpudWxsLCJlbWFpbElkIjpudWxsLCJvcmRlclZlcmlmaWVkIjp0cnVlLCJvcmRlckl0ZW1zIjpudWxsLCJvcmRlclRvdGFsIjpudWxsfQ==";
		String jsonBody =new String(Base64.getDecoder().decode(input));
		System.out.println(jsonBody);
	}

}
